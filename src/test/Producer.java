package test;

import redis.clients.jedis.Jedis;
import util.JedisUtil;

public class Producer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private void storeDataToRedis() {
		Jedis jedis = JedisUtil.getJedis();
		String serviceName = "service";
		String identifier = "service";
		String metric1 = "throughput";
		String metric2 = "errorcount";
		String metric3 = "delay";
		String timeRange = "time";
		
	}

}
