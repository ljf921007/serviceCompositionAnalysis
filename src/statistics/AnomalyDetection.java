package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Params;

public class AnomalyDetection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	//�ⲿ��list�Ƿ���ڵ���б��ڲ���list��ÿ������metric���б�
	public List<ArrayList<String>> detectAnomaly(List<ArrayList<String>> metrics) {
		//�ⲿ��list�Ƿ���ڵ���б��ڲ���list��ÿ�������ĳ��metric���ݳ����쳣��ʱ�����б�
		List<ArrayList<String>> serviceAnomaly = new ArrayList<ArrayList<String>>();
		
		for (int i = 0;i < metrics.size();i++) {
			ArrayList<String> anomalyMemonts = new ArrayList<String>();
			for (int j = 0;j < metrics.get(i).size();j++) {
				if (Integer.parseInt(metrics.get(i).get(j)) > (Params.commonMetric + Params.upperBound)) {
					anomalyMemonts.add(String.valueOf(j));
				}
			}
			serviceAnomaly.add(anomalyMemonts);
		}
		
		return serviceAnomaly;
	}
	
	public Map<String,String> detectSingleServiceAnomaly(List<String> metrics) {
		if(metrics.isEmpty() || metrics == null) 
			return null;
		int upperLimit = Params.commonMetric + Params.upperBound;
		int lowerLimit = Params.commonMetric - Params.lowerBound;
		Map<String,String> anomalies = new HashMap<String,String>();
		int count = 0;
		int timeRange = 0;
		for (String metric : metrics) {
			timeRange++;
			int value = Integer.parseInt(metric);
			if (value > upperLimit || value < lowerLimit) {
				count++;
			} else {
				count = 0;
			}
			if (count >= 10) {
				anomalies.put(String.valueOf(timeRange - count), String.valueOf(timeRange));
			}
		}
		return anomalies;
	}

}
