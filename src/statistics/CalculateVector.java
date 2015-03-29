package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//计算PageRank所需要的向量
public class CalculateVector {
	//计算偏好向量，firstAnomalyService是发现异常的的服务节点，从0开始计算
	public double[] getPV(List<Double> similarity, int firstAnomalyService) {
		double[] PV = new double[similarity.size()];
		double sum = 0;
		for (int i = 0;i < similarity.size();i++) {
			sum += similarity.get(i);
		}
		
		//将发现异常的节点向量值设为0
		PV[firstAnomalyService] = 0;
		
		return PV;
		
	}
	
	//概率转移矩阵
	public double[][] getPMatrix(List<Double> similarity, int[][] graph) {
		int n = similarity.size();
		double[][] MP = new double[n][n];
		
		/*for (int i = 0;i < n;i++) {
			graph[i][i] = 1;
		}*/
		//每个服务节点的下游节点的列表
		Map<String,ArrayList<String>> s = new HashMap<String,ArrayList<String>>();
		for (int i = 0;i < n;i++) {
			ArrayList<String> downs = new ArrayList<String>();
			for (int j = 0;j < n;j++) {
				if (graph[i][j] == 1) {
					downs.add(String.valueOf(j));
				}
			}
			if (downs.size() > 0) {
				s.put(String.valueOf(i), downs);
			}
		}
		
		return MP;
	}
}
