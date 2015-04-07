package model;

public class Params {
	//集中整理各常量数据
	public static final String service_table_name = "service_data" ;
	//pagerank中需要的系数
	public static final double alpha = 0.2;
	//pagerank中需要的系数
	public static final double rho = 0.5;
	//pagerank算法中的收敛阈值
	public static final double threshold = 0.00000000001;
	//存放服务调用图的文件
	public static final String fileForGraph = "f:\\graph.txt";
	//干扰因素的存储文件
	public static final String fileForFactors = "f:\\factors.txt";
	//系统中服务的数量
	public static final int serviceNumber = 100;
	//计算metric异常相似度的时间窗口大小
	public static final int timeWindowSize = 60;
	//为测试使用，指定一个metric数据正常状态的中间值
	public static final int commonMetric = 50;
	//为测试使用，数据的上限超出中间值时视为异常
	public static final int upperBound = 15;
	//为测试使用，数据的下限低于中间值时视为异常
	public static final int lowerBound = 15;
	//正常状态下在中间值附近的浮动范围
	public static final int limit = 15;
	
}
