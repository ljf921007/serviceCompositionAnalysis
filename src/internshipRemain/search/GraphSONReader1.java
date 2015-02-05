package internshipRemain.search;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.util.io.graphson.GraphElementFactory;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONMode;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReader;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONTokens;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility;

public class GraphSONReader1 extends GraphSONReader {

	public GraphSONReader1(Graph graph) {
		super(graph);
	}

	public static void inputGraph(final Graph graph,
			final InputStream jsonInputStream) throws IOException {
		inputGraph(graph, jsonInputStream, 1000);
	}

	public static void inputGraph(final Graph inputGraph,
			final InputStream jsonInputStream, int bufferSize)
			throws IOException {
		inputGraph(inputGraph, jsonInputStream, bufferSize, null, null);
	}

	public static void inputGraph(final Graph inputGraph,
			final InputStream jsonInputStream, int bufferSize,
			final Set<String> edgePropertyKeys,
			final Set<String> vertexPropertyKeys) throws IOException {

		//final JsonParser jp = jsonFactory.createParser(jsonInputStream);

		// if this is a transactional graph then we're buffering
		// final BatchGraph graph = BatchGraph.wrap(inputGraph, bufferSize);
		TransactionalGraph graph = (TransactionalGraph) inputGraph;

		final GraphElementFactory elementFactory = new GraphElementFactory(graph);
		GraphSONUtility graphson = new GraphSONUtility(GraphSONMode.NORMAL,
				elementFactory, vertexPropertyKeys, edgePropertyKeys);

		GraphSONMode mode = GraphSONMode.NORMAL;
		Map<Object, Vertex> externalIdAndOldVertexMapping = new HashMap<Object, Vertex>();
		Map<Object, Vertex> externalIdAndNewVertexMapping = new HashMap<Object, Vertex>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode gsNode = mapper.readTree(jsonInputStream);
		
		if(gsNode.get("mode") != null) {
			mode = GraphSONMode.valueOf(gsNode.get("mode").asText());
			graphson = new GraphSONUtility(mode, elementFactory,
					vertexPropertyKeys, edgePropertyKeys);
		}
		
		boolean hasEmbeddedTypes = mode == GraphSONMode.EXTENDED;
		
		JsonNode vertexNodes = gsNode.get("vertices");
		if(vertexNodes != null && vertexNodes.isArray()) {
			Iterator<JsonNode> it = vertexNodes.elements();
			while(it.hasNext()) {
				JsonNode node = it.next();
				final Map<String, Object> props = GraphSONUtility.readProperties(node, true, hasEmbeddedTypes);

				String indexKey = "name";
				Object indexValue = props.get(indexKey);
				if (indexValue != null) {
					
					IndexableGraph indexableGraph = (IndexableGraph) inputGraph;
					String indexName = "name_idx";
					Index<Vertex> index;
					if (indexableGraph.getIndex(indexName, Vertex.class) == null) {
						index = indexableGraph.createIndex(indexName,
								Vertex.class);
					} else {
						index = indexableGraph.getIndex(indexName,
								Vertex.class);
					}

					Iterator<Vertex> indexIt = index.get(indexKey, indexValue)
							.iterator();

					final Object vertexIdFromJSON = GraphSONUtility
							.getTypedValueFromJsonNode(node
									.get(GraphSONTokens._ID));

					if (!indexIt.hasNext()) {

						Vertex v = graphson.vertexFromJson(node);
						index.put(indexKey, indexValue, v);
						externalIdAndNewVertexMapping.put(vertexIdFromJSON,
								v);
					} else {
						Vertex v = indexIt.next();
						graphson.updateVertexFromJson(v, node);
						externalIdAndOldVertexMapping.put(vertexIdFromJSON, v);
					}
				} else {
					
					final Object vertexIdFromJSON = GraphSONUtility
							.getTypedValueFromJsonNode(node.get(GraphSONTokens._ID));
					Vertex v = graphson.vertexFromJson(node);
					externalIdAndNewVertexMapping.put(vertexIdFromJSON, v);
				}
			}
		}
		
		JsonNode edgeNodes = gsNode.get("edges");
		if(edgeNodes != null && edgeNodes.isArray()) {
			
			Iterator<JsonNode> it = edgeNodes.elements();
			while(it.hasNext()) {
				JsonNode node = it.next();
				
				Object inVID = GraphSONUtility
						.getTypedValueFromJsonNode(node
								.get(GraphSONTokens._IN_V));
				
				boolean newInV = true;
				Vertex inV = externalIdAndNewVertexMapping.get(inVID);
				if (inV == null) {
					
					inV = externalIdAndOldVertexMapping.get(inVID);
					
					newInV = false;
				}

				Object outVID = GraphSONUtility
						.getTypedValueFromJsonNode(node
								.get(GraphSONTokens._OUT_V));
				
				Object label = GraphSONUtility
						.getTypedValueFromJsonNode(node
								.get(GraphSONTokens._LABEL));
				
				boolean newOutV = true;
				Vertex outV = externalIdAndNewVertexMapping.get(outVID);
				if (outV == null) {
					
					outV = externalIdAndOldVertexMapping.get(outVID);
					
					newOutV = false;
				}

				if(inV != null && outV != null) {
					if (!newInV && !newOutV) {
						Iterator<Edge> edges = inV.getEdges(Direction.IN).iterator();
						
						Edge sameEdge = null;
						
						while(edges.hasNext()) {
							Edge edge = edges.next();
							
							if(outV.equals(edge.getVertex(Direction.OUT))) {
								// 
								if(label.equals(edge.getLabel())) {
									sameEdge = edge;
									break;
								}
							}
						}
						
						if(sameEdge != null) {
							graphson.updateEdgeFromJson(sameEdge, node);
						} else {
							graphson.edgeFromJson(node, outV, inV);
						}
					} else {
						graphson.edgeFromJson(node, outV, inV);
					}
				}
			}
		}
		
		//jp.close();

		graph.commit();
	}

	public static void main(String[] args) throws IOException {
		String orientDBAddr = "remote:localhost/networkgraph";

		final OrientGraphFactory factory = new OrientGraphFactory(orientDBAddr)
				.setupPool(1, 10);

		OrientGraph graph = factory.getTx();
		graph.setUseLightweightEdges(false);
		try {
			GraphSONReader1.inputGraph(graph, GraphSONReader1.class.getResourceAsStream("/graphData.json"));
			
			final Iterable<Edge> edges = graph.getEdges();

			for (Edge e : edges) {
				System.out.println(e);
			}
			
			
		} finally {
			graph.shutdown();
		}
		
		


		/*ObjectMapper mapper = new ObjectMapper();
		
		JsonNode node = mapper.readTree(GraphSONReader1.class.getResourceAsStream("/graphData.json"));
		
		System.out.println(node.get("mode"));
		System.out.println();
		if(node.get("vertices").isArray()) {
			Iterator<JsonNode> it = node.get("vertices").elements();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}
		System.out.println(node.get("edges"));*/
	}
}

