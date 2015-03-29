package statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dao.BaseDao;

import model.Params;

public class TestStatistics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*List<ArrayList<String>> metrics = generateMetrics();
	//	System.out.print(metrics.get(5));
		for (int i = 0;i < 60;i++) {
			metrics.get(5).set(i, String.valueOf(i));
		}
		System.out.println(metrics.get(5));
		System.out.println(metrics.get(6));
		SimilarityCalculate sc = new SimilarityCalculate();
		System.out.print(sc.getcosineSim(metrics, 6));*/
		BaseDao bd = new BaseDao();
		String nowTime = bd.getLastTime();
		String timeRange = "1000";
		int beginTime=0;
		int endTime=0;
		String metricName = "metric1";
		String serviceName = "6";
		
		//如果发现异常的时间到现在数据库中存储的最近数据时间超过60，则beginTime=timeRange
		if ((Integer.parseInt(nowTime)-Integer.parseInt(timeRange) >= Params.timeWindowSize)) {
			beginTime = Integer.parseInt(timeRange);
			endTime = Integer.parseInt(timeRange) + Params.timeWindowSize;
		} else {
			beginTime = Integer.parseInt(nowTime) - Params.timeWindowSize;
			endTime = Integer.parseInt(nowTime);
		}
		List<ArrayList<String>> metrics = bd.getMetrics(metricName, String.valueOf(beginTime), String.valueOf(endTime));
//		result.setText(beginTime+"" + metrics.get(0).get(50));
		SimilarityCalculate sc = new SimilarityCalculate();
		System.out.println("nidayede");
//		result.setText(beginTime+"" + metrics.get(0).get(50));
//		List<Double> similarity = sc.getcosineSim(metrics, Integer.parseInt(serviceName));
		System.out.println(metrics.toString());
	}
	
	private static List<ArrayList<String>> generateMetrics() {
		//每个服务节点在时间T内的metric数据
		
		List<ArrayList<String>> metrics = new ArrayList<ArrayList<String>>();
		Random random = new Random();
		for(int i = 0;i < Params.serviceNumber;i++) {
			ArrayList<String> innerMetric = new ArrayList<String>();
			for(int j = 0;j < Params.timeWindowSize;j++) {
				int value = Params.commonMetric + coefficient()*random.nextInt(20);
				innerMetric.add(String.valueOf(value));
			}
			metrics.add(innerMetric);
		}
		
		return metrics;
	}
	
	private static int coefficient() {
		int a=(int)(Math.random()*2+1);
	//	System.out.println(a);
		int aa=(int)(Math.pow(-1, a));
	//	System.out.println(aa);
		return aa;
		/*int aaa=(int)(Math.random()*100+1);
		System.out.println(aaa);
		int num=aa*aaa;
		System.out.println(num);
		if(num>0){
			System.out.println("正数");
		}else if(num<0){
			System.out.println("负数");
		}else{System.out.println("0");}*/
	}

}
