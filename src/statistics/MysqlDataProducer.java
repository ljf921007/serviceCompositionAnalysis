package statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Params;

import dao.BaseDao;

public class MysqlDataProducer {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		List<ArrayList<String>> metrics = generateMetrics(14*24*60);
		List<String> downstream = downStreamServices();
	//	System.out.println(metrics.size());
	//	System.out.println(metrics.get(0).size());
		BaseDao bd = new BaseDao();
	//	bd.addItems();
	//	bd.createTable();
	//	bd.addMetrics(metrics, "metric1", "0");
		//��û����ɲ���ʱ�����ݱ�����ɲ���ʧ��
		//���һ�γɹ�����ֻ����15���ӣ������Ե������й�����������ݿ�������ʱ�����˿���̨�����һֱ��������������֮��ĵ����һ�κ�˳��
		bd.addfinalItems(Params.serviceNumber, Params.timeWindowSize*24*14, downstream, metrics);
	//	bd.addItemsWithoutMetric(Params.serviceNumber, Params.timeWindowSize, downStreamServices());
	
	//	bd.addMetrics(generateMetrics(Params.timeWindowSize*24*14), "metric1", "2");
	//	bd.addfinalItems(Params.serviceNumber, Params.timeWindowSize*24*14, downStreamServices(), generateMetrics(Params.timeWindowSize*24*14));
	//	System.out.println("update service_data set " + "metric1" + "=? where serviceId=?");
		/*try {
			bd.createTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*try {
			bd.addItem("first", "thinking", "many values", "under considering");
			bd.addItems();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	//	List<String> downs = downStreamServices();
	//	bd.addItemsWithoutMetric(Params.serviceNumber, 10, downs);
		
		
		//System.out.println(downs);
		/*List<String> samples = new ArrayList<String>();
		//��list����Ԫ�ص�λ�ü���add����Ԫ�أ�֮��Ԫ�ص�λ�û�˳�κ���
		samples.add(0,null);
		samples.add(1, null);
		samples.add(0,"1222");
		samples.set(0,"1222");
		System.out.println(samples);*/
	}
	
	public static List<String> downStreamServices() {
		List<String> downStreams = new ArrayList<String>();
		for (int i = 0;i < Params.serviceNumber;i++) {
			downStreams.add(null);
		}
		downStreams.set(1, "11:12:13");
		downStreams.set(3,"15:16");
		downStreams.set(11,"31:32");
		downStreams.set(12,"32");
		downStreams.set(13,"33:35");
		downStreams.set(15,"34:35");
		downStreams.set(16,"36:37");
		downStreams.set(31,"51");
		downStreams.set(32,"52:53");
		downStreams.set(34,"53");
		downStreams.set(35,"55");
		downStreams.set(36,"56");
		downStreams.set(37,"57");
		downStreams.set(51,"71:72");
		downStreams.set(52,"73");
		downStreams.set(53,"74");
		downStreams.set(55,"77");
		downStreams.set(56,"76");
		downStreams.set(57,"78");
		downStreams.set(72,"91");
		downStreams.set(73,"91");
		downStreams.set(78,"99");
		return downStreams;
		
	}
	
	//ÿ������һ��ʱ�䴰��T��metric����
	private static List<ArrayList<String>> generateMetrics(int timeLength) {
		//ÿ������ڵ���ʱ��T�ڵ�metric����
		
		List<ArrayList<String>> metrics = new ArrayList<ArrayList<String>>();
		Random random = new Random();
		for(int i = 0;i < Params.serviceNumber;i++) {
			ArrayList<String> innerMetric = new ArrayList<String>();
			for(int j = 0;j < timeLength;j++) {
				int value = Params.commonMetric + coefficient()*random.nextInt(Params.limit);
				innerMetric.add(String.valueOf(value));
			}
			metrics.add(innerMetric);
		}
		
		return metrics;
	}
	
	private static int coefficient() {
		int a=(int)(Math.random()*2+1);
	//	System.out.println(a);
		int aa=(int)(Math.pow(-1, a));
	//	System.out.println(aa);
		return aa;
		/*int aaa=(int)(Math.random()*100+1);
		System.out.println(aaa);
		int num=aa*aaa;
		System.out.println(num);
		if(num>0){
			System.out.println("����");
		}else if(num<0){
			System.out.println("����");
		}else{System.out.println("0");}*/
	}
	
	
}
