package model;

public class Params {
	//�����������������
	public static final String service_table_name = "service_data" ;
	//pagerank����Ҫ��ϵ��
	public static final double alpha = 0.2;
	//pagerank����Ҫ��ϵ��
	public static final double rho = 0.5;
	//pagerank�㷨�е�������ֵ
	public static final double threshold = 0.00000000001;
	//��ŷ������ͼ���ļ�
	public static final String fileForGraph = "f:\\graph.txt";
	//�������صĴ洢�ļ�
	public static final String fileForFactors = "f:\\factors.txt";
	//ϵͳ�з��������
	public static final int serviceNumber = 100;
	//����metric�쳣���ƶȵ�ʱ�䴰�ڴ�С
	public static final int timeWindowSize = 60;
	//Ϊ����ʹ�ã�ָ��һ��metric��������״̬���м�ֵ
	public static final int commonMetric = 50;
	//Ϊ����ʹ�ã����ݵ����޳����м�ֵʱ��Ϊ�쳣
	public static final int upperBound = 15;
	//Ϊ����ʹ�ã����ݵ����޵����м�ֵʱ��Ϊ�쳣
	public static final int lowerBound = 15;
	//����״̬�����м�ֵ�����ĸ�����Χ
	public static final int limit = 15;
	
}
