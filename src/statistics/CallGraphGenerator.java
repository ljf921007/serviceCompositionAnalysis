package statistics;

import java.util.List;
import java.util.Random;

import model.Params;

public class CallGraphGenerator {
	//Graph g = TinkerGraph.open();
	public int[][] generateGlobalGraph(List<String> services) {
		//ͼ�нڵ�ĺ�����ַ�����ʽ����:�ָ�
		//Ϊ�˼򻯣�����ڵ������������1,2��������
		int n = services.size();
		int[][] graph = new int[n][n];
		for(int i = 0;i < n;i++) {
			//��֤split�Ķ���Ϊnull
			if(services.get(i) == null)
				continue;
			String[] downstream = services.get(i).split(":");
			for(int j = 0;j < downstream.length;j++) {
				int x = Integer.parseInt(downstream[j]) - 1;
				graph[i][x] = 1;
			}
		}
		
		return graph;
	}
	
	public int[][] getCallGraph() {
		int[][] graph = new int[Params.serviceNumber][Params.serviceNumber];
		
		return graph;
	}
	
	//���쳣��ɢ�������෴����ʱ����ת����ͼ�еıߵķ���
	public int[][] reverseGraph(int[][] graph) {
		int length = graph.length;
		int[][] regraph = new int[length][length];
		for (int i = 0;i < length;i++) {
			for (int j = 0;j < length;j++) {
				
			}
		}
		return regraph;
	}
	
	public int[][] getSample(int size) {
		int[][] matrix = new int[size][size];
		Random random = new Random();
		for (int i = 0;i < size;i++) {
			for (int j = 0;j < size;j++) {
				int value = random.nextInt(10) >= 5 ? 1 : 0;
				matrix[i][j] = value;
			}
		}
		return matrix;
	}
	
	
}
