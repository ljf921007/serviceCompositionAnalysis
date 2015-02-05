package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Interaction {
	//用户与系统交互的界面
	JFrame jf = new JFrame("服务组合中异常定位");
	JButton begin = new JButton("开始分析");
	JTextField serviceName = new JTextField(30);
	JTextField metricName = new JTextField(30);
	JTextField timeRange = new JTextField(30);
	JLabel label1 = new JLabel("服务名称");
	JLabel label2 = new JLabel("度量时间");
	JLabel label3 = new JLabel("时间范围");
	JTextArea result = new JTextArea();
	
	public void init() {
		JPanel jp = new JPanel();
		jp.add(label1);
		jp.add(serviceName);
		jp.add(label2);
		jp.add(metricName);
		jp.add(label3);
		jp.add(timeRange);
		jp.add(begin);
		jp.add(result);
		jf.add(jp);
		jf.setVisible(true);
		
		begin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//result.setText("hahahahhaha");
			}
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Interaction().init();
	}

}
