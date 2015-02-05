package internshipRemain.graph;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.wink.client.RestClient;

import internshipRemain.graph.LifeCycleCallback;
import internshipRemain.graph.ConfigureManager;
import util.ExecutorUtil;
import util.StringHelper;

/**
 * 
 */
public class BrokerPoller extends Thread implements LifeCycleCallback {
	
	private static class SingletonHolder {
		public final static BrokerPoller instance = new BrokerPoller();
	}

	public static BrokerPoller getInstance() {
		return SingletonHolder.instance;
	}
	
	private String TRANSACTIONLOG; // This file is used to kep track of
									// indices/offsets ingested, so that the
									// next run does not ingest same
									// index/offset agin

	private static long STARTINDEX = 1;
	private static long STOPINDEX = -1;
	private static long POLLPERIODINMILLIS = 1000; // 50 ms
	private static int MAXINGESTTHREADS = 1;
	private static int PREFETCHCOUNT = 1000;
	private static int MAXINDICESPOLLEDTOGETHER = 100; // Even if ListIndices
														// returns more results,
														// we fetch metadata of
														// atmost this size to
														// prevent unnecessary
														// memory usage in
														// pending list in
														// ingestionState

	private static String INGESTORADDRESS = "";

	private RestClient rClient;

	private JSONParser jsonParser;
	private ExecutorService threadPoolExecutor;

	// private static Object waitObj = new Object();

	// The ingestor polling is now done via the callbackForFrameCompletion in
	// addition to the main ingestor thread
	// This was because the main ingestor thread was observed to starve when
	// numthreads is high
	// Using a simple monitor based lock is not enough since we want the
	// tryLock() like behaviour where the callback
	// can return quickly if some other thread is ingesting currently
	private final ReentrantLock pollIndicesToIngestLock = new ReentrantLock();

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, Long> maxIngestTimeOfChannelInPrevRun = new HashMap<String, Long>();
	
	public static void main(String[] args) {
		BrokerPoller poller = new BrokerPoller();
		String tenant = "31990112F011E1058A3057030915C0660E052AE";
		poller.addFrameProcessor(tenant, new GraphFrameProcessor(tenant));
		
		poller.start();
	}

	private BrokerPoller() {
		this.rClient = new RestClient();
		this.jsonParser = new JSONParser();
		int maxPoolSize = MAXINGESTTHREADS;
		int corePoolSize = MAXINGESTTHREADS;
		long keepAliveTime = 5000;
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		String dataHome = ConfigureManager.getInstance().getValue(
				"graph.service.data.home");
		TRANSACTIONLOG = dataHome + "/TRANSACTIONLOG.log";

		File f = new File(dataHome);
		if (!f.exists())
			f.mkdirs();

		readPreviousTransactionLog();
	}

	public static void setINGESTORADDRESS(String val) {
		INGESTORADDRESS = val;
	}

	public static String getINGESTORADDRESS() {
		return INGESTORADDRESS;
	}

	public static void setNumIngestThreads(int val) {
		MAXINGESTTHREADS = val;
	}

	public static int getNumIngestThreads() {
		return MAXINGESTTHREADS;
	}

	public static void setStartIndex(long val) {
		STARTINDEX = val;
	}

	public static long getStartIndex() {
		return STARTINDEX;
	}

	public static void setStopIndex(long val) {
		STOPINDEX = val;
	}

	public static long getStopIndex() {
		return STOPINDEX;
	}

	public RestClient getRestClient() {
		return this.rClient;
	}

	@Override
	public void run() {
		while (true) {
			// printStatus("FromMethod=run()");
			pollIndicesToIngest();
			periodicMaintenance();
			try {
				// synchronized(waitObj) {
				// waitObj.wait(POLLPERIODINMILLIS);
				// }
				Thread.sleep(POLLPERIODINMILLIS);
			} catch (InterruptedException ex) {
				// This will be interrupted by
				// callbackForCompletionOfFrameIngestion()
			}
		}

	}

	private void readPreviousTransactionLog() {
		File tlog = new File(TRANSACTIONLOG);
		try {
			if (tlog.exists()) {
				logger.info("Previous transaction log:" + TRANSACTIONLOG
						+ " found, will read it to infer last frame ingested");
				BufferedReader tlogreader = new BufferedReader(new FileReader(
						TRANSACTIONLOG));
				String tlogline;
				while ((tlogline = tlogreader.readLine()) != null) {
					if (tlogline.trim().isEmpty())
						continue;
					// Format is channelName(String) \t ingestTime(long)
					String[] parts = tlogline.split("\t");
					String channelName = parts[0];
					long ingestTime = Long.parseLong(parts[1]);

					maxIngestTimeOfChannelInPrevRun
							.put(channelName, ingestTime);
				}
				tlogreader.close();

			} else {
				logger.info("Did not find any previous transaction log:"
						+ TRANSACTIONLOG);
			}

		} catch (Exception e) {
			logger.error("Exception while reading previous transactionlog", e);
		}
	}

	private void periodicMaintenance() {
		File f = new File(TRANSACTIONLOG);
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				logger.error("", e);
			}
		}

		StringBuilder buffer = new StringBuilder();
		for (FrameProcessor fp : frameProcessors.values()) {
			IndexMetadata indexMetadata = fp.getCurrentCompletedFrames();
			if (indexMetadata != null) {
				buffer.append(fp.getTenant()).append("\t");
				buffer.append(indexMetadata.getIngestTime()).append("\n");
			}
		}

		BufferedWriter tlogWriter = null;
		try {
			tlogWriter = new BufferedWriter(new FileWriter(TRANSACTIONLOG));
			String content = buffer.toString().trim();
			if (!"".equals(content)) {
				tlogWriter.write(content);
			}
		} catch (Exception e) {
			logger.error("ERROR in opening transactionlog in write mode", e);
		} finally {
			if (tlogWriter != null)
				try {
					tlogWriter.close();
				} catch (IOException e) {
					logger.error("ERROR in close transactionlog writer", e);
				}
		}

	}


	private void pollIndicesToIngest() {
		for (FrameProcessor fp : frameProcessors.values()) {
			pollIndicesToIngest0(fp);
		}
	}

	private void pollIndicesToIngest0(FrameProcessor frameProcessor) {
		pollIndicesToIngestFromNewDatabroker(frameProcessor);
	}

	// This pulls data using the DataConsumerAPI of the new Kafka like
	// DataBroker
	private void pollIndicesToIngestFromNewDatabroker(FrameProcessor frameProcessor) {

		long lastIndexPolled = frameProcessor.getLastMetadataIndexPolled();

		if ((getStopIndex() != -1) && (lastIndexPolled >= getStopIndex())) {
			logger.info("Stopped polling since lastIndexPolled="
					+ lastIndexPolled + " has reached desired stopIndex="
					+ getStopIndex());
			return;
		}

		long numIndicesPolledButNotCompleted = frameProcessor
				.getNumOfIndicesPolled()
				- frameProcessor.getNumOfIndicesCompleted();
		if (numIndicesPolledButNotCompleted > PREFETCHCOUNT) {
			logger.debug("Dampening polling since numIndicesPolledButNotCompleted="
					+ numIndicesPolledButNotCompleted
					+ " is greater than prefetchcount=" + PREFETCHCOUNT);
			return;
		}
		// We need a tryLock behaviour where the callback thread invoking
		// pollIndicesToIngest() can return without waiting
		// on the lock if some other thread is currently possessing the lock and
		// polling index metadata
		if (pollIndicesToIngestLock.tryLock()) {
			try {

				String url = ConfigureManager.getInstance().getValue(
						"broker.metadata.api");

				String getmetadataTarget = StringHelper.completeStr(url,
						INGESTORADDRESS, frameProcessor.getType(),
						lastIndexPolled, MAXINDICESPOLLEDTOGETHER, frameProcessor.getTenant());

				logger.debug("Invoking REST API for GET " + getmetadataTarget);

				String getmetadataResponse;
				
				// getmetadataResponse = "{\"metadata\": [{\"envelope\": {\"origin\": \"\", \"namespace\": \"192.168.50.1\", \"type\": \"test\", \"timestamp\": \"2014-07-10T10:00:40\"}, \"next_offset\": 9, \"offset\": 8}]}";
				try {
					getmetadataResponse = rClient.resource(getmetadataTarget)
							.accept("*/*").get(String.class);
				} catch (Exception e) {
					logger.error(
							"ERROR: Getting response via RestClient for target="
									+ getmetadataTarget, e);
					return;
				}

				try {
					JSONObject rootObject = (JSONObject) jsonParser
							.parse(getmetadataResponse);
					JSONArray indicesArray = (JSONArray) rootObject
							.get("metadata");
					Iterator<JSONObject> iterator = indicesArray.iterator();
					int numIndicesToPoll = 0;
					while (iterator.hasNext()) {
						if (numIndicesToPoll >= MAXINDICESPOLLEDTOGETHER) {
							break;
						}
						JSONObject indexMetadataObj = iterator.next();
						long index = Long.parseLong(indexMetadataObj.get(
								"offset").toString());
						long nextIndex = Long.parseLong(indexMetadataObj.get(
								"next_offset").toString());

						JSONObject envelopeObj = (JSONObject) indexMetadataObj
								.get("envelope");
						String timestampStr = (String) envelopeObj
								.get("timestamp");

						SimpleDateFormat timestampDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss");
						Date timestampDateObj = timestampDateFormat
								.parse(timestampStr);
						long timestamp = timestampDateObj.getTime();

						if (index < lastIndexPolled) {
							// Note: we poll from the oldest index in the
							// databroker giving it offset=-2,
							// We do this instead of setting
							// offset=(this.lastIndexPolled) since dataitems can
							// expire from broker and unless we have support
							// for a special tag in count which specifies
							// everything till the latest, we will call it with
							// offset=-2
							logger.debug("Skipping frame index/offset=" + index
									+ " since it is <= lastIndexPolled="
									+ lastIndexPolled);
							continue;
						}
						// Note: We use timestamps and not index/offset since
						// the broker on restart generates offsets starting from
						// zero again

						Long maxIngestTimeInPrevRun = this.maxIngestTimeOfChannelInPrevRun
								.get(frameProcessor.getTenant());
						if (maxIngestTimeInPrevRun != null
								&& timestamp <= maxIngestTimeInPrevRun) {
							logger.debug("Skipping frame with <timestampStr="
									+ timestampStr
									+ ",timestamp="
									+ timestamp
									+ ",index="
									+ index
									+ "> since it is <= maxIngestTimeInPrevRun="
									+ maxIngestTimeInPrevRun);
							continue;
						}

						// Scenario: Non-emulation mode
						numIndicesToPoll++;
						pollIndexFromNewDatabroker(frameProcessor, index, indexMetadataObj);
						if (nextIndex > lastIndexPolled) {
							frameProcessor
									.setLastMetadataIndexPolled(nextIndex);
						}
					}
				} catch (Exception e) {
					logger.error(
							"Exception in parsing GET broker/metadata.JSONStructure",
							e);
				}

			} catch (Exception e) {
				logger.error("Exception in trylock() code snippet, e:", e);
			} finally {
				pollIndicesToIngestLock.unlock();
			}
		} else {
			logger.debug("pollIndicesToIngest() returning prematurely since tryLock returned false since some other thread is currently executing this logic");
		}

	}

	
	public void pollIndexFromNewDatabroker(FrameProcessor frameProcessor, long index, JSONObject indexMetadataObj) {
		long starttime = System.currentTimeMillis();

		String namespace = "None";
		String origin = "";

		long ingestTime; // in seconds
		String ingestTimeStr; // in human-readable Date string format
		
		try {
			JSONObject rootObject = indexMetadataObj;
			JSONObject argsMap = (JSONObject) rootObject.get("envelope");
			origin = (String) argsMap.get("origin");
			
			frameProcessor.increaseNumOfIndicesPolled();

			namespace = (String) argsMap.get("namespace");

			ingestTimeStr = (String) argsMap.get("timestamp");

			SimpleDateFormat ingesttimedateformat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss");
			Date ingestTimeDateObj = ingesttimedateformat.parse(ingestTimeStr);
			ingestTime = ingestTimeDateObj.getTime();
		
			logger.info("Retrieved FrameMetadata<" + "index=" + index
					+ ", type=" + frameProcessor.getType() 
					+ ", tenant=" + frameProcessor.getTenant() 
					+ ", namespace=" + namespace + ", timestamp=" + ingestTimeStr + ">");

		} catch (Exception e) {
			logger.error("Error in parsing Frame MetadataResponse, e:" + e);
			// We will return prematurely since this frame cannot be processed
			return;
		}

		IndexMetadata indexMetadata = new IndexMetadata(index, origin,
				namespace, ingestTime, ingestTimeStr, frameProcessor.getType());

		frameProcessor.setIndexMetadata(indexMetadata);
		
		if(!frameProcessor.isRunning())
			ExecutorUtil.runWithoutDeadLock(threadPoolExecutor, frameProcessor);
	
		long endtime = System.currentTimeMillis();
		logger.debug("pollindextimeinmillis= " + (endtime - starttime));

	}

	private Map<String, FrameProcessor> frameProcessors = new HashMap<String, FrameProcessor>();

	public void addFrameProcessor(String name, FrameProcessor frameProcessor) {
		
		if (frameProcessor == null)
			return;
		if (frameProcessors.get(name) != null)
			return;
		
		frameProcessors.put(name, frameProcessor);
	}

	public void removeFrameProcessor(String name) {
		FrameProcessor fp = frameProcessors.remove(name);
		
		logger.info("stop frame processor[" + name + "]");
		
	}
	
	@Override
	public void beforeCreate(String graph, String dbName) {

	}

	@Override
	public void beforeDrop(String graph, String dbName) {

	}

	@Override
	public void afterCreate(String name, String dbName) {
		addFrameProcessor(name, new GraphFrameProcessor(name));
	}

	@Override
	public void afterDrop(String name, String database) {
		removeFrameProcessor(name);
	}

	@Override
	public void afterInit(Collection<String> names) {
		for (String name : names) {
			addFrameProcessor(name, new GraphFrameProcessor(name));
		}
	}
}

