package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//����PageRank����Ҫ������
public class CalculateVector {
	//����ƫ��������firstAnomalyService�Ƿ����쳣�ĵķ���ڵ㣬��0��ʼ����
	public double[] getPV(List<Double> similarity, int firstAnomalyService) {
		double[] PV = new double[similarity.size()];
		double sum = 0;
		for (int i = 0;i < similarity.size();i++) {
			sum += similarity.get(i);
		}
		
		//�������쳣�Ľڵ�����ֵ��Ϊ0
		PV[firstAnomalyService] = 0;
		
		return PV;
		
	}
	
	//����ת�ƾ���
	public double[][] getPMatrix(List<Double> similarity, int[][] graph) {
		int n = similarity.size();
		double[][] MP = new double[n][n];
		
		/*for (int i = 0;i < n;i++) {
			graph[i][i] = 1;
		}*/
		//ÿ������ڵ�����νڵ���б�
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
