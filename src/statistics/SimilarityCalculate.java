package statistics;

import java.util.ArrayList;
import java.util.List;

public class SimilarityCalculate {
	//����ÿ������ڵ�������ڵ��ģʽ���ƶ�
	//ע��metric���ݵ�����
	//�ⲿ��list�����з�����б��ڲ���list��ÿ�������Ӧ��ʱ�䷶Χ�ڵ�metric���ݵ��б�
	public List<Double> getcosineSim(List<ArrayList<String>> metrics) {
		List<Double> result = new ArrayList<Double>();
		for(int i = 1;i < metrics.size();i++) {
			double cosine = 0;
			double vector1 = 0;
			double vector2 = 0;
			double vectorPro = 0;
			int length = metrics.get(0).size();
			for(int j = 0;j < length;j++) {
				double firstNodeMetric = Double.parseDouble(metrics.get(0).get(j));
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
}
