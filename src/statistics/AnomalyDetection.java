package statistics;

import java.util.ArrayList;
import java.util.List;

import model.Params;

public class AnomalyDetection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	//外部的list是服务节点的列表，内部的list是每个服务metric的列表
	public List<ArrayList<String>> detectAnomaly(List<ArrayList<String>> metrics) {
		//外部的list是服务节点的列表，内部的list是每个服务的某个metric数据出现异常的时间点的列表
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

}
