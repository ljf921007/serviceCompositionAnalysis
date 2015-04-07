package statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimilarityCalculate {
	//计算每个服务节点与给定节点的模式相似度
	//注意metric数据的类型
	//外部的list是所有服务的列表，内部的list是每个服务对应的时间范围内的metric数据的列表
	//firstAnomalyService代表异常被发现的第一个节点
	
	//没有对metric数据为空的记录进行处理，当出现metric数据为空的情况时，可能会导致运行时出现错误
	public List<Double> getcosineSim(List<ArrayList<String>> metrics, String firstAnomalyService) {
		List<Double> result = new ArrayList<Double>();
		for(int i = 1;i < metrics.size();i++) {
			double cosine = 0;
			double vector1 = 0;
			double vector2 = 0;
			double vectorPro = 0;
			int length = metrics.get(0).size();
			for(int j = 0;j < length;j++) {
				double firstNodeMetric = Double.parseDouble(metrics.get(Integer.parseInt(firstAnomalyService)).get(j));
				double otherNodeMetric = Double.parseDouble(metrics.get(i).get(j));
			//	vector1 += metrics.get(0).get(j)*metrics.get(0).get(j);
			//	vector2 += metrics.get(i).get(j)*metrics.get(i).get(j);
			//	vectorPro += metrics.get(0).get(j)*metrics.get(i).get(j);
				vector1 += Math.pow(firstNodeMetric, 2);
				vector2 += Math.pow(otherNodeMetric, 2);
				vectorPro += firstNodeMetric * otherNodeMetric;
			}
			cosine = vectorPro/(Math.sqrt(vector1)*Math.sqrt(vector2));
			result.add(cosine);
		}
		return result;
	}
	
	//
	public List<Double> getSimilarity (List<Double> simpleSim, Set<String> factor) {
		if (factor == null || factor.isEmpty())
			return simpleSim;
		List<Double> similarity = new ArrayList<Double>();
		
		return similarity;
	}
}