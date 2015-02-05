package util;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import test.Producer;


public class ThreadManage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public  void   startThreadPool(Queue<Producer> que){
//		 ExecutorService pool = Executors.newFixedThreadPool(3);
		 int k=0;
		 while(que.size()>0){
			 
			 CountDownLatch latch = new CountDownLatch(3);
			 int i=0 ;
			 //每次开启3个线程
			 int pre = 3;;
			 for(;i<pre;i++){
				 
			 }
			 k++;
			 try {
				latch.await();
				System.out.println((k*pre+i)+" thread  finished");
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		 }
		 
//		 CountDownLatch cd;
	}
	
	public  void   startProducerThreadPool(Queue<Producer> que){
//		 ExecutorService pool = Executors.newFixedThreadPool(3);
		 int k=0;
		 int cnt_size = que.size();
		 while(que!=null&&que.size()>0){
			 System.out.println("-------latch-----执行第----------"+(k+1)+"------------轮");
			 int pre = 3 ;
			 CountDownLatch latch = new CountDownLatch(pre);
			 int i=0 ;
			
			 for(;i<pre;i++){
			 }
			 
			 try {
				latch.await();
				System.out.println((k*pre+i)+" thread  finished ,  休息 10 秒钟");
				
				Thread.sleep(10*1000);
				k++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 System.out.println("--------------返回主线程------------------");
//		 CountDownLatch cd;
	}
	

}

