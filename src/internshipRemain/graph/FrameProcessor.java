package internshipRemain.graph;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.wink.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class FrameProcessor implements Runnable {

	private static final String DEFAULT_TYPE = "topology";
	
	private String type = DEFAULT_TYPE;

	protected RestClient restClient;

	private AtomicLong numIndicesPolled;
	private AtomicLong numIndicesCompleted;
	private AtomicLong lastMetadataIndexPolled;
	
	private AtomicBoolean running = new AtomicBoolean(false);

	private ConcurrentLinkedDeque<IndexMetadata> indexMetadataWaitingQueue;
	private IndexMetadata lastIndexMetadata;

	public String getType() {
		return type;
	}
	
	private String tenant;
	
	public String getTenant() {
		return tenant;
	}

	private static Logger logger = LoggerFactory.getLogger(FrameProcessor.class
			.getName());

	public FrameProcessor(String name) {
		if(name == null)
			throw new IllegalArgumentException("frame processor name is null.");
		
		this.tenant = name;
		
		this.restClient = new RestClient();
		this.numIndicesCompleted = new AtomicLong(0);
		this.numIndicesPolled = new AtomicLong(0);

		// starts as '-2' which implies oldest data in the broker
		this.lastMetadataIndexPolled = new AtomicLong(-2);

		this.indexMetadataWaitingQueue = new ConcurrentLinkedDeque<IndexMetadata>();
	}

	public boolean isRunning() {
		return running.get();
	}
	
	@Override
	public void run() {		
		
		while (!indexMetadataWaitingQueue.isEmpty()) {
			
			running.set(true);
			
			lastIndexMetadata = indexMetadataWaitingQueue.poll();
			
			if (lastIndexMetadata != null) {
				
				try {
					execute(lastIndexMetadata);

				} catch (Exception e) {
					logger.error("Error in processing Frame BodyResponse.", e);
					e.printStackTrace();
				} finally {
					invokeFrameProcessingCompletionCallback(lastIndexMetadata);
				}
			}
		}
		running.set(false);
	}

	public abstract void execute(IndexMetadata indexMetadata);

	private final void invokeFrameProcessingCompletionCallback(
			IndexMetadata indexMetadata) {

		numIndicesCompleted.incrementAndGet();

		// iPoller.callbackForCompletionOfFrameIngestion(indexMetadata);
	}

	public long getLastMetadataIndexPolled() {
		return lastMetadataIndexPolled.get();
	}

	public void setLastMetadataIndexPolled(long lastMetadataIndexPolled) {

		this.lastMetadataIndexPolled.set(lastMetadataIndexPolled);
	}

	protected final IndexMetadata getCurrentCompletedFrames() {
		return lastIndexMetadata;
	}

	public void setIndexMetadata(IndexMetadata indexMetadata) {
		this.indexMetadataWaitingQueue.offer(indexMetadata);
	}
	
	public String toString() {
		return "Frame Processor [" + tenant + "]";
	}

	public long getNumOfIndicesCompleted() {
		return numIndicesCompleted.get();
	}

	public long increaseNumOfIndicesCompleted() {
		return numIndicesCompleted.incrementAndGet();
	}

	public long getNumOfIndicesPolled() {

		return numIndicesPolled.get();
	}

	public long increaseNumOfIndicesPolled() {
		return numIndicesPolled.incrementAndGet();
	}
	
}

