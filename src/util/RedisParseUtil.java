package util;

import redis.clients.jedis.Jedis;

public class RedisParseUtil {
	
	public static void main(String[] args) {
		Jedis jedis = JedisUtil.getJedis();
//		jedis.set("name", "jordon");
		System.out.println(jedis.get("name"));
	}
	
	public static void parseRedis(String redisStr) {
		//redis�е�value�ַ�������ʽΪs1;s2;s3;s4;����ʹ��;�ָ�
		String[] column = redisStr.split(";");
		String serviceName = column[0];
		String identifier = column[1];
		String metrics = column[2];
		String timeRange = column[3];
	}
}
