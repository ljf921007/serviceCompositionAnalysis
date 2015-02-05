package internshipRemain.graph;

import java.util.Collection;

public interface LifeCycleCallback {
	
	String getName();
	
	void afterInit(Collection<String> graphs);

	void beforeCreate(String graph, String dbName);
	
	void beforeDrop(String graph, String dbName);
	
	void afterCreate(String graph, String dbName);
	
	void afterDrop(String graph, String dbName);
}
