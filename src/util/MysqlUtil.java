package util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlUtil {
	public static Connection conn = null;
	//public Statement stm = null;
	//public ResultSet rs = null;
	public static Properties props = null;
	
	public static Connection getConnection() throws Exception {
		/*initParam("mysql.ini");
		String driver = props.getProperty("driver");
		String url = props.getProperty("url");
		String user = props.getProperty("user");
		String password = props.getProperty("password");*/
		
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/service_analysis?useUnicode=true&characterEncoding=UTF-8";
		String user = "root";
		String password = "123456";
		
		Class.forName(driver);
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn;
		
	}
	
	public static void initParam(String paramFile) throws Exception {
		//使用Properties类来加载属性文件
		//Properties props = new Properties();
		props.load(new FileInputStream(paramFile));	
	}
}
