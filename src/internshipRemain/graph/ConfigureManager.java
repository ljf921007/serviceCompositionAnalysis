package internshipRemain.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigureManager {
	private String path = "/graphdata.properties";
	private Properties prop;

	private static class SingletonHolder {
		public final static ConfigureManager instance = new ConfigureManager();
	}

	public static ConfigureManager getInstance() {

		return SingletonHolder.instance;
	}

	private ConfigureManager() {
		prop = new Properties();
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream(path);
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getValue(String key) {
		return prop.getProperty(key);
	}

	public int getValueAsInt(String key) {
		String strValue = getValue(key);
		int v = 0;
		try {
			v = Integer.parseInt(strValue);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return v;
	}

	public static void main(String[] args) throws Exception {
		ConfigureManager p = new ConfigureManager();
		System.out.println(p.getValue("server"));
		System.out.println(p.getValue("message.queue.name"));
	}
}

