package internshipRemain.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import internshipRemain.graph.LifeCycleCallback;
import internshipRemain.graph.GraphDataModel;
import internshipRemain.graph.GraphDataModelNotFoundException;

public class GraphDataModelManager {

	private static String GDM_STORAGE;
	private static final String suffix = ".gdm";

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static Map<String, GraphDataModel> cache = new HashMap<String, GraphDataModel>();

	private List<LifeCycleCallback> cbs = new ArrayList<LifeCycleCallback>();

	private static class SingletonHolder {
		public final static GraphDataModelManager instance = new GraphDataModelManager();
	}

	public static GraphDataModelManager getInstance() {
		return SingletonHolder.instance;
	}

	private GraphDataModelManager() {
		GDM_STORAGE = ConfigureManager.getInstance().getValue("graph.service.data.home") + "/datamodel/";
	}
	
	public Collection<GraphDataModel> getAll() {
		File f = new File(GDM_STORAGE);
		
		if(!f.exists())
			return cache.values();
		
		String[] gdms = f.list();
		if(gdms != null) {
			for(String o : f.list()) {
				int index = o.indexOf(suffix);
				if(index > 0) {
					get(o.substring(0, index));
				}
			}
		}
		
		return cache.values();
	}
	

	public GraphDataModel create(String name, String jsonContent) {
		GraphDataModel gdm = new GraphDataModel(name);
		
		gdm.create(jsonContent);
		
		persistent(name, jsonContent);
		
		cache.put(name, gdm);
		
		for(LifeCycleCallback cb : cbs)
			if(cb != null)
				cb.afterCreate(name, null);
		
		return gdm;
	}
	
	public void delete(String name) {
		
		GraphDataModel gdm = get(name);
		
		if(gdm == null)
			return;
		
		gdm.delete();
		
		String gdmFiles = GDM_STORAGE + name + suffix;
		File f = new File(gdmFiles);
		f.delete();
		
		for(LifeCycleCallback cb : cbs)
			if(cb != null)
				cb.afterDrop(name, null);
	}
	
	public GraphDataModel get(String name) {
		GraphDataModel gdm = cache.get(name);
		
		if(gdm == null) {
			gdm = new GraphDataModel(name);
			try {
				gdm.load();
			} catch (GraphDataModelNotFoundException e) {
				return null;
			}
			cache.put(name, gdm);
		}
		return gdm;
	}
	
	private void persistent(String name, String content) {
		File f = new File(GDM_STORAGE);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		File gdmFiles = new File(GDM_STORAGE + name + suffix);
		FileWriter fw = null;
		try {
			fw = new FileWriter(gdmFiles);
			fw.write(content);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if(fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					logger.error("", e);
				}
		}
	}

	public void setLifeCycleHook(LifeCycleCallback cb) {
		cbs.add(cb);
	}
}
