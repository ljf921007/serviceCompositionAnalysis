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
	//�û���ϵͳ�����Ľ���
	JFrame jf = new JFrame("����������쳣��λ");
	JButton begin = new JButton("��ʼ����");
	JTextField serviceName = new JTextField(30);
	JTextField metricName = new JTextField(30);
	JTextField timeRange = new JTextField(30);
	JLabel label1 = new JLabel("��������");
	JLabel label2 = new JLabel("����ʱ��");
	JLabel label3 = new JLabel("ʱ�䷶Χ");
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
