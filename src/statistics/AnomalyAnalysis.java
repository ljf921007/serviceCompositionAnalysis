package statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import model.Params;

//获取matlab的处理结果并整理输出
public class AnomalyAnalysis {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CallGraphGenerator cgg = new CallGraphGenerator();
		int[][] matrix = cgg.getSample(Params.serviceNumber);
		/*for(int i = 0;i < 20;i++) {
			for(int j = 0;j < 20;j++) {
				System.out.println(matrix[i][j]);
			}
		}*/
	//	writeGraph(matrix, Params.fileForGraph);
		int[][] graph = readGraph(Params.fileForGraph);
		/*for(int i = 0;i < 20;i++) {
			for(int j = 0;j < 20;j++) {
				System.out.println(graph[i][j]);
			}
		}*/
	}
	
	//将服务调用图写入文件中
	private static void writeGraph(int[][] graph, String filename) throws IOException {
		File file = new File(filename);
		FileWriter out = new FileWriter(file);
		for (int i = 0; i < graph.length;i++) {
			for (int j = 0;j < graph.length;j++) {
				out.write(graph[i][j] + "\t");
			}
			out.write("\r\n");
		}
		out.close();
	}

	//从文件中读取服务调用图
	private static int[][] readGraph(String filename) throws IOException {
		int[][] graph = new int[Params.serviceNumber][Params.serviceNumber];
		File file = new File(filename);
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		int row = 0;
		while ((line = in.readLine())!= null) {
			String[] temp = line.split("\t");
			for (int j = 0;j < temp.length;j++) {
				graph[row][j] = Integer.parseInt(temp[j]);
			}
			row++;		
		}
		in.close();
		return graph;
	}
}
