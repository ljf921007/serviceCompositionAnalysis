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
		new GUI("��������ģ��");
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
		//���÷���
		addComponent();
		submit.addActionListener(this);
		setVisible(true);
		setLocationRelativeTo(null);//�������ʾ;
	}
	
	//����������н���������е����;
	//ʹ�õ����������;ϣ��¥���ܿ���;
	public void addComponent()
	{
	//��������ģ��
	noteInformation=new JLabel("����������");
	add(g,c,noteInformation,0,0,1,1);
	/*Information=new JTextField(10);
	add(g,c,Information,1,0,2,1);*/
	//�쳣�����ֵķ���ڵ�
	anomalyService=new JLabel("�쳣����ڵ㣺");
	add(g,c,anomalyService,0,1,1,1);
	//�쳣����ڵ������
	textAnomalyService=new JTextField(12);
	add(g,c,textAnomalyService,1,1,2,1);
	//�����쳣�ķ�metric����
	anomalyMetric=new JLabel("Metric���ݣ�");
	add(g,c,anomalyMetric,0,2,1,1);
	//�쳣metric�����
	textAnomalyMetric=new JTextField(12);
	add(g,c,textAnomalyMetric,1,2,2,1);
	//�쳣�����ֵ�ʱ��
	anomalyTime=new JLabel("�쳣����ʱ�䣺");
	add(g,c,anomalyTime,0,3,1,1);
	textAnomalyTime=new JTextField(12);
	add(g,c,textAnomalyTime,1,3,2,1);
	//
	outputNumber=new JLabel("�������������");
	add(g,c,outputNumber,0,4,1,1);
	textoutputNum=new JTextField(12);
	add(g,c,textoutputNum,1,4,2,1);
	
	//��������˵��
	/*Information=new JLabel("Ϊ������ԣ�����ڵ����ƴ�0��99,metric����ʹ��metric1,�쳣ʱ�������ʹ���");
	add(g,c,textAnomalyTime,0,4,1,1);*/
	//�쳣ʱ�������
	/*textAnomalyTime=new JTextField(10);
	System.out.println("**********************************************");
	add(g,c,anomalyTime,1,3,2,1);
	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");*/
	
	//submit��ť
	submit=new JButton("�ύ");
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
	
	//��������쳣��ʱ�䵽�������ݿ��д洢���������ʱ�䳬��60����beginTime=timeRange
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
	String results = "�쳣��Դ����ڵ�����" + "\n" + resultService.toString();
	result.setText(results);
	}

}
