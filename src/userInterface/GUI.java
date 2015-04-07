package userInterface;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Params;
import statistics.CalculateVector;
import statistics.CallGraphGenerator;
import statistics.DisturbFactor;
import statistics.PerPageRank;
import statistics.SimilarityCalculate;
import util.ServiceHandler;
import dao.BaseDao;

public class GUI extends JFrame implements ActionListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GUI("交互分析模块");
	}
	
	JLabel noteInformation,anomalyService,anomalyMetric,anomalyTime,outputNumber;
	JTextField textAnomalyService,textAnomalyMetric,textAnomalyTime,textoutputNum;
	JButton submit;
	JTextArea result;
	
	
	GridBagLayout g=new GridBagLayout();
	GridBagConstraints c=new GridBagConstraints();
	
	GUI(String str) {
		super(str);
		setSize(300,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(g);
		//调用方法
		addComponent();
		submit.addActionListener(this);
		setVisible(true);
		setLocationRelativeTo(null);//设居中显示;
	}
	
	//在这个方法中将会添加所有的组件;
	//使用的网格包布局;希望楼主能看懂;
	public void addComponent()
	{
	//交互分析模块
	noteInformation=new JLabel("交互分析：");
	add(g,c,noteInformation,0,0,1,1);
	/*Information=new JTextField(10);
	add(g,c,Information,1,0,2,1);*/
	//异常被发现的服务节点
	anomalyService=new JLabel("异常服务节点：");
	add(g,c,anomalyService,0,1,1,1);
	//异常服务节点输入框
	textAnomalyService=new JTextField(12);
	add(g,c,textAnomalyService,1,1,2,1);
	//出现异常的服metric数据
	anomalyMetric=new JLabel("Metric数据：");
	add(g,c,anomalyMetric,0,2,1,1);
	//异常metric输入框
	textAnomalyMetric=new JTextField(12);
	add(g,c,textAnomalyMetric,1,2,2,1);
	//异常被发现的时间
	anomalyTime=new JLabel("异常发现时间：");
	add(g,c,anomalyTime,0,3,1,1);
	textAnomalyTime=new JTextField(12);
	add(g,c,textAnomalyTime,1,3,2,1);
	//
	outputNumber=new JLabel("输出服务数量：");
	add(g,c,outputNumber,0,4,1,1);
	textoutputNum=new JTextField(12);
	add(g,c,textoutputNum,1,4,2,1);
	
	//输入数据说明
	/*Information=new JLabel("为方便测试，服务节点名称从0到99,metric数据使用metric1,异常时间以整型代替");
	add(g,c,textAnomalyTime,0,4,1,1);*/
	//异常时间输入框
	/*textAnomalyTime=new JTextField(10);
	System.out.println("**********************************************");
	add(g,c,anomalyTime,1,3,2,1);
	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");*/
	
	//submit按钮
	submit=new JButton("提交");
	c.insets=new Insets(7,0,4,0);
	add(g,c,submit,1,5,1,1);

	result=new JTextArea(15,20);
	add(g,c,result,0,6,3,4);

	}
	
	public void add(GridBagLayout g,GridBagConstraints c,JComponent jc,int x ,int y,int gw,int gh)
	{
	c.gridx=x;
	c.gridy=y;
	c.anchor=GridBagConstraints.WEST;
	c.gridwidth=gw;
	c.gridheight=gh;
	g.setConstraints(jc,c);
	add(jc);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{

	String serviceName=textAnomalyService.getText();
	String metricName=textAnomalyMetric.getText();
	String timeRange = textAnomalyTime.getText();
	int serviceNumber = Integer.parseInt(textoutputNum.getText());
	
	
	BaseDao bd = new BaseDao();
	String nowTime = bd.getLastTime();
	int beginTime=0;
	int endTime=0;
	
	//如果发现异常的时间到现在数据库中存储的最近数据时间超过60，则beginTime=timeRange
	if ((Integer.parseInt(nowTime)-Integer.parseInt(timeRange) >= Params.timeWindowSize)) {
		beginTime = Integer.parseInt(timeRange);
		endTime = Integer.parseInt(timeRange) + Params.timeWindowSize;
	} else {
		beginTime = Integer.parseInt(nowTime) - Params.timeWindowSize;
		endTime = Integer.parseInt(nowTime);
	}
	List<ArrayList<String>> metrics = bd.getMetrics(metricName, String.valueOf(beginTime), String.valueOf(endTime));
//	result.setText(beginTime+"" + metrics.get(0).get(50));
	SimilarityCalculate sc = new SimilarityCalculate();
//	result.setText(beginTime+"" + metrics.get(0).get(50));
	List<Double> similarity = sc.getcosineSim(metrics, serviceName);
//	System.out.println(similarity.toString());
//	result.setText(similarity.toString());
	DisturbFactor df = new DisturbFactor();
	List<HashSet<String>> disFactors = df.getFactorsFromStore(Params.fileForFactors);
	Set<String> factor = df.matchDisturbFactor(disFactors, similarity);
	List<Double> finalSimilarity = sc.getSimilarity(similarity, factor);
	CalculateVector cv = new CalculateVector();
	CallGraphGenerator cg = new CallGraphGenerator();
	int[][] graph = cg.getCallGraph();
	double[][] transMatrix = cv.getPMatrix(finalSimilarity, graph);
	double[] preference = cv.getPV(finalSimilarity, serviceName);
	PerPageRank ppr = new PerPageRank();
	double[] pageRank = ppr.runPageRank(transMatrix, preference);
	ServiceHandler sh = new ServiceHandler();
	String[] resultService = sh.outputService(pageRank, serviceNumber);
	String results = "异常根源服务节点排序：" + "\n" + resultService.toString();
	result.setText(results);
	}

}
