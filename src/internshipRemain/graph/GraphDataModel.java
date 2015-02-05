package internshipRemain.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import internshipRemain.graph.LifeCycleCallback;
import internshipRemain.graph.ConfigureManager;

public class GraphDataModel {

	private Map header_vertex;
	private Map header_edge;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String type;
	
	private boolean usable;
	
	private static String GDM_STORAGE;
	private static final String suffix = ".gdm";

	public GraphDataModel(String type) {
		this.type = type;
		GDM_STORAGE = ConfigureManager.getInstance().getValue("graph.service.data.home") + "/datamodel/";
	}
	
	
	private final static String DEFAULT_LABEL = "key";
	
	public void delete() {
		logger.info("delete graph data model of type: " + type);
		
	}
	
	public void create(String jsonContent) {
		
		parseGraphDataModel(jsonContent);
	}
	

	public void load() throws GraphDataModelNotFoundException {
		parseGraphDataModel(readDataModelFile(GDM_STORAGE + type + suffix));
	}

	// start from position of "0"
	public String getVertexLabelByPosition(int position) {
		
		if(!usable)
			return DEFAULT_LABEL;

		String p = String.valueOf(position);

		Object v = this.header_vertex.get(p);

		if (v == null) {
			return null;
		}

		return v.toString();
	}
	
	public boolean usable() {
		return usable;
	}
	
	public String getEdgeLabel() {
		return header_edge.get("label").toString();
	}
	
	private boolean isPersistent = true;
	
	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}
	
	public boolean isPersistent() {
		return isPersistent;
	}
	
	private String readDataModelFile(String filename) throws GraphDataModelNotFoundException {
		
		File f = new File(filename);
		if(!f.exists()) {
			throw new GraphDataModelNotFoundException();
		}
		
		StringBuilder buffer = new StringBuilder();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = null;

			while ((line = reader.readLine()) != null) {
				buffer.append(line).append("\n");
			}

		} catch (Exception e) {
			throw new GraphDataModelNotFoundException();
			
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					logger.error("close file " + filename + " error", e1);
				}
			}
		}
		
		return buffer.toString();
	}
	
	public void parseGraphDataModel(String jsonString) {

		JSONParser jsonParser = new JSONParser();

		JSONObject rootObject;
		try {
			rootObject = (JSONObject) jsonParser.parse(jsonString);

			JSONObject header = (JSONObject) rootObject.get("header");

			// parse vertex data
			header_vertex = (HashMap) header.get("vertex");

			// parse edge data
			header_edge = (HashMap) header.get("edge");

		} catch (ParseException e) {
			usable = false;
			//logger.error("parse content: " + jsonString + ".", e);
		}
	}

	public static void main(String[] args) {
		JSONParser jsonParser = new JSONParser();
		try {
			jsonParser.parse("");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getHeaderVertices() {
		List<String> result = new ArrayList<String>();
		if(header_vertex != null)
			for(Object s : header_vertex.values()) {
				result.add(s.toString());
			}
		return result;
	}

	public String getType() {
		return type;
	}
	
	
}
