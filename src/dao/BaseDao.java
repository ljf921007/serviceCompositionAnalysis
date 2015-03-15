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
		//ͼ�нڵ�ĺ�����ַ�����ʽ����:�ָ�
		int result = stat.executeUpdate("create table service_data "
				+ "(serviceId int auto_increment primary key, "
				+ "service_name varchar(255), "
				+ "identifier varchar(255), "
				+ "downstream varchar(255), "
				+ "metric1 varchar(255), "
				+ "metric2 varchar(255), "
				+ "metric3 varchar(255), "
				+ "time_range varchar(255));");  //һ��ע����������Ҫ��ϸ
		System.out.println(result);
		stat.close();
		conn.close();
	}
	
	public void addItem(String serviceName, String identifier, String metrics, String timeRange) throws Exception {
		/*String s = "insert into service_data (service_name,identifier,metrics,time_range)"
		+ " values ("+serviceName+", "+identifier+", "+metrics+", "+timeRange+");";
		System.out.println(s);*/
		try {
			Connection conn = MysqlUtil.getConnection();
			Statement stat = conn.createStatement();
			//ע�����ݿ��е��ַ���Ҫ��������������sql����ϸ�����׳��ִ���
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
	
	//��ȡָ��metric��ָ��ʱ�䷶Χ��ֵ
	public List<ArrayList<String>> getMetrics(String metric, int beginTime, int endTime) {
		//ע�ⷺ��֮���з��͵�����
		//�ⲿ��list��ÿ�������list���ڲ���list�к���ָ��ʱ�䷶Χ��metricֵ
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
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metrics;
	}
	
	//������ݿ������з���ڵ�����η���ڵ����ݣ��Թ���һ���������ɵ���ͼ
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//������µ�ʱ�䷶Χ���ݣ��Խ�һ��ȷ����Ҫ��ȡ�ĸ���Χ������
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
}
