package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.MysqlUtil;


public class BaseDao {
	
	public void createTable() throws Exception {
		Connection conn = MysqlUtil.getConnection();
		Statement stat = conn.createStatement();
		//图中节点的后继是字符串形式，以:分割
	//	int result = stat.executeUpdate("create table service_data "
		int result = stat.executeUpdate("create table service_data_final "
				+ "(serviceId int auto_increment primary key, "
				+ "service_name varchar(255), "
				+ "identifier varchar(255), "
				+ "downstream varchar(255), "
				+ "metric1 varchar(255), "
				+ "metric2 varchar(255), "
				+ "metric3 varchar(255), "
				+ "time_range varchar(255));");  //一定注意最后的括号要仔细
		System.out.println(result);
		stat.close();
		conn.close();
	}
	
	public void deleteTable() {
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			String sql = "drop table service_data";
			stat.executeUpdate(sql);
			stat.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addItem(String serviceName, String identifier, String metrics, String timeRange) throws Exception {
		/*String s = "insert into service_data (service_name,identifier,metrics,time_range)"
		+ " values ("+serviceName+", "+identifier+", "+metrics+", "+timeRange+");";
		System.out.println(s);*/
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			//注意数据库中的字符串要用引号括起来，sql语句的细节容易出现错误
			stat.executeUpdate("insert into service_data (service_name,identifier,metric1,time_range)"
					+ " values ('"+serviceName+"', '"+identifier+"', '"+metrics+"', '"+timeRange+"');");
			stat.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*finally {
			stat.
		}*/
		
	}
	
	public void addItems() {
		
		try {
			Connection conn = MysqlUtil.getConnection();
			PreparedStatement pstm = conn.prepareStatement(
				"insert into service_data (service_name,metric1,metric2,metric3,time_range) values(?,?,?,?,?)");
			pstm.setString(1, "1");
			pstm.setString(2, "25");
			pstm.setString(3, "55");
			pstm.setString(4, "60");
			pstm.setString(5, "1");
			pstm.executeUpdate();
			pstm.close();
			conn.close();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
						
	}
	
	public void addfinalItems(int serviceNum, int timeNum, List<String> downstream, List<ArrayList<String>> metrics) {
		//	int batchSize = 100;
		//	int commitSize = serviceNum*timeNum;
			try {
				Connection conn = MysqlUtil.getConnection();
				conn.setAutoCommit(false);
				PreparedStatement pstm = conn.prepareStatement(
				//	"insert into service_data_final (service_name,downstream,metric1,time_range) values(?,?,?,?)");
					"insert into service_data (service_name,downstream,metric1,time_range) values(?,?,?,?)");
				pstm.clearParameters();
				for (int i = 0;i < timeNum;i++) {
					System.out.println(i);
					pstm.clearBatch();
					for (int j = 0;j < serviceNum;j++) {
						pstm.setString(1, String.valueOf(j));
						pstm.setString(2, downstream.get(j));
						pstm.setString(3, metrics.get(j).get(i));
						pstm.setString(4, String.valueOf(i));
						pstm.addBatch();
					}
					pstm.executeBatch();
				}
				conn.commit();
				
				pstm.close();
				conn.close();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
							
		}
	
	//向数据库中添加数据，数据中不含metric数据
	public void addItemsWithoutMetric(int serviceNum, int timeNum, List<String> downstream) {
	//	int batchSize = 100;
	//	int commitSize = serviceNum*timeNum;
		try {
			Connection conn = MysqlUtil.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstm = conn.prepareStatement(
				"insert into service_data (service_name,downstream,time_range) values(?,?,?)");
			pstm.clearParameters();
			for (int i = 0;i < timeNum;i++) {
				pstm.clearBatch();
				for (int j = 0;j < serviceNum;j++) {
					pstm.setString(1, String.valueOf(j));
					pstm.setString(2, downstream.get(j));
					pstm.setString(3, String.valueOf(i));
					pstm.addBatch();
				}
				pstm.executeBatch();
			}
			conn.commit();
			
			pstm.close();
			conn.close();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		"insert into service_data_final (service_name,downstream,time_range) values(?,?,?)");						
	}
	
	public void addMetrics(List<ArrayList<String>> metrics, String metricName, String beginTime) {
//	public void addMetrics(String metricName) {	
		int metricLength = metrics.get(0).size();
		int serviceNumber = metrics.size();
		try {
			Connection conn = MysqlUtil.getConnection();
		//特别重要，当不使用批处理时，一点要不要set成false
			conn.setAutoCommit(false);
			PreparedStatement pstm = conn.prepareStatement(
		//		"insert into service_data (" + metricName + ") values(?) where serviceId=?");
				"update service_data set " + metricName + "=? where service_name=? and time_range=?");
		//	pstm.clearParameters();
			/*for (int i = 0;i < timeNum;i++) {
				pstm.clearBatch();
				for (int j = 0;j < serviceNum;j++) {
					pstm.setString(1, String.valueOf(j));
					pstm.setString(2, downstream.get(j));
					pstm.setString(3, String.valueOf(i));
					pstm.addBatch();
				}
				pstm.executeBatch();
			}
			conn.commit();*/
			
			/*pstm.setString(1, "60");
			pstm.setString(2, "6");
			pstm.setString(3, "1");
			pstm.executeUpdate();*/
			
			for (int i = 0;i < metricLength;i++) {
				pstm.clearBatch();
				for (int j = 0;j < serviceNumber;j++) {
					pstm.setString(1, metrics.get(j).get(i));
					pstm.setString(2, String.valueOf(j));
					//beginTime从下一个空的时间范围开始
					pstm.setString(3, String.valueOf(i + Integer.parseInt(beginTime)));
					pstm.addBatch();
				}
				pstm.executeBatch();
			}
			conn.commit();
			
			pstm.close();
			conn.close();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	//获取指定metric的指定时间范围的值
	public List<ArrayList<String>> getMetrics(String metric, String beginTime, String endTime) {
		//注意泛型之中有泛型的类型
		//外部的list是每个服务的list，内部的list中含有指定时间范围的metric值
	//	Map<String,List<Integer>> m = new HashMap<String,ArrayList<Integer>>();
		List<ArrayList<String>> metrics = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < 100; i++) {
			ArrayList<String> list = new ArrayList<String>();
			metrics.add(list);
		}
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			String sql = "select " + metric + " from service_data where time_range between "
					+ beginTime + " and "+ endTime;
			ResultSet rs = stat.executeQuery(sql);
			int i = 0;
			while(rs.next()) {
				metrics.get(i).add(rs.getString(1));
				i++;
				if (i >= 100) {
					i = 0;
				}
			}
			stat.close();
			conn.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metrics;
	}
	
	//获得每个服务的某个metric数据列表
	public List<String> getSpecServMetrics(String serviceName, String metric, int beginTime, int endTime) {
		List<String> specServMetrics = new ArrayList<String>();
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			String sql = "select " + metric + " from service_data where service_name=" + serviceName + 
					" and time_range between "
					+ beginTime + " and "+ endTime;
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
				specServMetrics.add(rs.getString(1));
			}
			stat.close();
			conn.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return specServMetrics;
	}
	
	//获得数据库中所有服务节点的下游服务节点数据，以供进一步处理生成调用图
	public List<String> getCallGraph(int timeRange) {
		List<String> result = new ArrayList<String>();
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			String sql = "select downstream from service_data where time_range=" + timeRange;
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
				result.add(rs.getString(1));
			}
			stat.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//获得最新的时间范围数据，以进一步确定需要获取哪个范围的数据
	public String getLastID() {
		String id = "";
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			String sql = "select time_range from service_data where id=(select max(id) from service_data)";
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
				id = rs.getString(1);
			}
			stat.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	public String getLastTime() {
		String lastTime = "";
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			String sql = "select time_range from service_data where serviceId=(select max(serviceId) from service_data)";
			ResultSet rs = stat.executeQuery(sql);
			while(rs.next()) {
				lastTime = rs.getString(1);
			}
			stat.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastTime;
	}
}
