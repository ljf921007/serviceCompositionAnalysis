package util;


import java.util.HashMap;
import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//import BaseConfig;
//import RedisConfig;

/**
 * Redis������,���ڻ�ȡRedisPool.
 * �ο�����˵�����£�
 * You shouldn't use the same instance from different threads because you'll have strange errors.
 * And sometimes creating lots of Jedis instances is not good enough because it means lots of sockets and connections,
 * which leads to strange errors as well. A single Jedis instance is not threadsafe!
 * To avoid these problems, you should use JedisPool, which is a threadsafe pool of network connections.
 * This way you can overcome those strange errors and achieve great performance.
 * To use it, init a pool:
 *	JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
 *	You can store the pool somewhere statically, it is thread-safe.
 *	JedisPoolConfig includes a number of helpful Redis-specific connection pooling defaults.
 *	For example, Jedis with JedisPoolConfig will close a connection after 300 seconds if it has not been returned.
 * @author wujintao
 */
public class JedisUtil  {
 //   protected Logger log = LoggerFactory.getLogger(getClass());
    
	/**
	 * ˽�й�����.
	 */
	private JedisUtil() {
	    
	}
	private static Map<String,JedisPool> maps  = new HashMap<String,JedisPool>();
	
    
    /**
     * ��ȡ���ӳ�.
     * @return ���ӳ�ʵ��
     */
	private static JedisPool getPool(String ip, int port) {
		String key = ip + ":" + port;
		JedisPool pool = null;
		//����Ϊ���ṩ�����������̳߳�Map�����Ѿ��ж�Ӧip���̳߳�ֱ�ӷ��أ����Ч��
		if(maps.containsKey(key)){
			pool = maps.get(key);
			return pool;
		}
		//�����ͬ��������ֹ����߳�ͬʱ���������ͬ��ip�̳߳�
		synchronized (JedisUtil.class) {
			if (!maps.containsKey(key)) {
				JedisPoolConfig config = new JedisPoolConfig();
	//			config.setMaxTotal(RedisConfig.getMaxTotal());
	//			config.setMaxIdle(RedisConfig.getMaxIdle());
	//			config.setMaxWaitMillis(RedisConfig.getMaxWaitMillis());
	//			config.setTestOnBorrow(true);
	//			config.setTestOnReturn(true);
				
				
				try {
					/**
					 * ��������� java.net.SocketTimeoutException: Read timed out
					 * exception���쳣��Ϣ �볢���ڹ���JedisPool��ʱ�������Լ��ĳ�ʱֵ.
					 * JedisPoolĬ�ϵĳ�ʱʱ����2��(��λ����)
					 */
	//				pool = new JedisPool(config, ip, port, RedisConfig.getTimeout());
					maps.put(key, pool);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				pool = maps.get(key);
			}
		}
		return pool;
	}



    /**
     *�༶���ڲ��࣬Ҳ���Ǿ�̬�ĳ�Աʽ�ڲ��࣬���ڲ����ʵ�����ⲿ���ʵ��
     *û�а󶨹�ϵ������ֻ�б����õ�ʱ�Ż�װ�أ��Ӷ�ʵ�����ӳټ��ء�
     */
    private static class RedisUtilHolder{
        /**
         * ��̬��ʼ��������JVM����֤�̰߳�ȫ
         */
        private static JedisUtil instance = new JedisUtil();
    }

    /**
     *��getInstance������һ�α����õ�ʱ������һ�ζ�ȡ
     *RedisUtilHolder.instance������RedisUtilHolder��õ���ʼ�������������װ�ز�����ʼ����ʱ�򣬻��ʼ�����ľ�
     *̬�򣬴Ӷ�����RedisUtil��ʵ���������Ǿ�̬�������ֻ���������װ�����ʱ���ʼ��һ�Σ��������������֤�����̰߳�ȫ�ԡ�
     *���ģʽ���������ڣ�getInstance������û�б�ͬ��������ֻ��ִ��һ����ķ��ʣ�����ӳٳ�ʼ����û�������κη��ʳɱ���
     */
	public static JedisUtil getInstance() {
		return RedisUtilHolder.instance;
	}
	
	/**
	 * ��ȡRedisʵ��.
	 * @return Redis������ʵ��
	 */
	public Jedis getJedis(String ip,int port) {
		Jedis jedis  = null;
		int count =0;
		do{
    		try{ 
    			jedis = getPool(ip,port).getResource();
    			//log.info("get redis master1!");
    		} catch (Exception e) {
 //   			log.error("get redis master1 failed!", e);
    			 // ���ٶ���  
    			getPool(ip,port).returnBrokenResource(jedis);  
    		}
    		count++;
//		}while(jedis==null&&count<BaseConfig.getRetryNum());
	}while(jedis==null);
		return jedis;
	}

	/**
	 * �ͷ�redisʵ�������ӳ�.
     * @param jedis redisʵ��
     */
	
	public static Jedis getJedis() {
		return new Jedis("127.0.0.1",6379);
	}
	
	public void closeJedis(Jedis jedis,String ip,int port) {
		if(jedis != null) {
		    getPool(ip,port).returnResource(jedis);
		}
	}
}

