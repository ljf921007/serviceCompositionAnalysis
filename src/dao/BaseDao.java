package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import util.MysqlUtil;

public class BaseDao {
	
	public void createTable() throws Exception {
		Connection conn = MysqlUtil.getConnection();
		Statement stat = conn.createStatement();
		int result = stat.executeUpdate("create table service_data "
				+ "(serviceId int auto_increment primary key, "
				+ "service_name varchar(255), "
				+ "identifier varchar(255), "
				+ "metrics varchar(255), "
				+ "time_range varchar(255));");
		System.out.println(result);
	}
	
	public void addItem(String serviceName, String identifier, String metrics, String timeRange) throws Exception {
		/*String s = "insert into service_data (service_name,identifier,metrics,time_range)"
		+ " values ("+serviceName+", "+identifier+", "+metrics+", "+timeRange+");";
		System.out.println(s);*/
		Connection conn = MysqlUtil.getConnection();
		Statement stat = conn.createStatement();
		//注意数据库中的字符串要用引号括起来，sql语句的细节容易出现错误
		stat.executeUpdate("insert into service_data (service_name,identifier,metrics,time_range)"
				+ " values ('"+serviceName+"', '"+identifier+"', '"+metrics+"', '"+timeRange+"');");
		
	}
}
