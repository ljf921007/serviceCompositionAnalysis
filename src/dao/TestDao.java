package dao;

import java.util.ArrayList;
import java.util.List;

import statistics.MysqlDataProducer;

public class TestDao {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BaseDao bd = new BaseDao();
		bd.createTable();
	//	List<String> downs = MysqlDataProducer.downStreamServices();
	//	bd.addItemsWithoutMetric(100, 5, downs);
	//	List<ArrayList<String>> metrics = bd.getMetrics("metric1", "0", "2");
	//	System.out.println(metrics);
	//	bd.deleteTable();
	//	System.out.println(bd.getLastTime());
	}

}
