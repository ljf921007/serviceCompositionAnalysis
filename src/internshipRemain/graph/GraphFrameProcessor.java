package internshipRemain.graph;

import internshipRemain.search.GraphSONReader1;
import internshipRemain.search.NamespaceParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.wink.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.StringHelper;

import com.ibm.json.java.JSONObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
//import com.ibm.json.java.JSONObject;

public class GraphFrameProcessor extends FrameProcessor {

	public GraphFrameProcessor(String name) {
		super(name);
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(IndexMetadata indexMetadata) {
		long starttimeForFrameProcessing = System.currentTimeMillis();

		String namespace = indexMetadata.getNamespace();
		String type = indexMetadata.getType();

		logger.info("GraphFrameProcessor<namespace=" + namespace + ",type="
				+ type + ",starttime=" + starttimeForFrameProcessing
				+ "> called");

		String getbodyTarget;

		String url = ConfigureManager.getInstance().getValue("broker.data.api");
		
		getbodyTarget = StringHelper.completeStr(url,
				BrokerPoller.getINGESTORADDRESS(), type,
				indexMetadata.getIndex(), getTenant());
		
		String bodyResponse = restClient.resource(getbodyTarget).accept("*/*")
				.get(String.class);

		logger.info(bodyResponse);

		String orientDBHost = ConfigureManager.getInstance().getValue(
				"orientdb.host");
		String orientDBAddr = "remote:" + orientDBHost + "/" + getTenant();

		logger.info(orientDBAddr);
		
		OrientGraphFactory factory = new OrientGraphFactory(orientDBAddr).setupPool(1, 10);

		OrientGraph graph = factory.getTx();
		try {
			//namespaceParse(type, namespace, graph);
			
			savePayload(graph, bodyResponse);
		} finally {
			graph.shutdown();
		}

		long endtime = System.currentTimeMillis();

		logger.info("FPTime:<" + "type=" + type + ", namespace=" + namespace
				+ ",index=" + indexMetadata.getIndex() + ",timeinms="
				+ (endtime - starttimeForFrameProcessing) + ">");

		activateRexster();
	}
	
	private boolean isActivateRexster = false;
	
	private void activateRexster() {
		if (isActivateRexster)
			return;
		logger.info("activate rexster for query edges.");
		RestClient rc = new RestClient();
		String url = ConfigureManager.getInstance().getValue("rexster.address");
		try {
			url = "http://" + url + "/graphs/" + getTenant();
			String resp = rc
					.resource(url + "/vertices/A?name=A")
					.post(null).getEntity(String.class);
			JSONObject v1 = JSONObject.parse(resp);
			String v1Id = ((JSONObject) v1.get("results")).get("_id")
					.toString().substring(1);
			resp = rc
					.resource(url + "/vertices/B?name=B")
					.post(null).getEntity(String.class);
			JSONObject v2 = JSONObject.parse(resp);
			String v2Id = ((JSONObject) v2.get("results")).get("_id")
					.toString().substring(1);

			resp = rc
					.resource(url + "/edges?_outV=" + v1Id + "&_label=friend&_inV="
								+ v2Id + "&name=c").post(null)
					.getEntity(String.class);
			JSONObject e = JSONObject.parse(resp);
			String eId = ((JSONObject) e.get("results")).get("_id")
					.toString().substring(1);
			
			rc.resource(url + "/edges/" + eId).delete();
			rc.resource(url + "/vertices/" + v1Id).delete();
			rc.resource(url + "/vertices/" + v2Id).delete();
			
		} catch (IOException e) {
			logger.error("", e);
		}
		
		isActivateRexster = true;
	}
	
	private void savePayload(Graph graph, String payload) {
		if(payload == null || "".equals(payload)) {
			logger.error("payload is empty.");
			return;
		}
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(payload.getBytes());
			GraphSONReader1.inputGraph(graph, stream);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	protected void namespaceParse(String type, String namespace, Graph o) {
		GraphDataModel gdm = GraphDataModelManager.getInstance().get(type);

		NamespaceParser parser = new NamespaceParser();
		parser.setGraphDataModel(gdm);

		List<Entry> nodes = parser.parse(namespace);

		// String edgeLabel = gdm.getEdgeLabel();
		String edgeLabel = "topology_connection";
		String positionKey = "position";

		Vertex current_vertex = null;
		Vertex parent_vertex = null;

		for (int i = 0; i < nodes.size(); i++) {
			Entry e = nodes.get(i);
			boolean vertexExist = false;

			String propertyName = e.getKey();
			String propertyValue = e.getValue();

			Iterator<Vertex> vertices = o.getVertices().iterator();

			while (vertices.hasNext()) {
				Vertex v = vertices.next();
				String pValue = v.getProperty(propertyName);
				Integer position = v.getProperty(positionKey);
				if (pValue != null && position != null) {
					if (pValue.equals(propertyValue)
							&& position.intValue() == i) {
						vertexExist = true;
						current_vertex = v;
						break;
					}
				}
			}

			if (!vertexExist) {
				Vertex v = o.addVertex(null);
				v.setProperty(e.getKey(), e.getValue());
				v.setProperty(positionKey, i);
				current_vertex = v;

				if (parent_vertex != null) {
					Edge newEdge = o.addEdge("class:E", parent_vertex,
							current_vertex, null);
					newEdge.setProperty("name", edgeLabel);
				}
			}

			parent_vertex = current_vertex;

		}

	}

}
