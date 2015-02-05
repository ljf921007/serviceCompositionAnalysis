package util;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class GraphUtil {

	public static void main(String[] args) {
		OrientGraphFactory factory = new OrientGraphFactory("plocal:C:/temp/graph/db").setupPool(1,10);
		OrientGraph graph = factory.getTx();
		try{
			  Vertex luca = graph.addVertex(null); // 1st OPERATION: IMPLICITLY BEGIN A TRANSACTION
			  luca.setProperty( "name", "Luca" );
			  Vertex marko = graph.addVertex(null);
			  marko.setProperty( "name", "Marko" );
			  Edge lucaKnowsMarko = graph.addEdge(null, luca, marko, "knows");
			  graph.commit();
			} catch( Exception e ) {
			  graph.rollback();
			}
	}
}
