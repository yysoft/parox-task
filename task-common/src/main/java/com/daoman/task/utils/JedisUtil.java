package com.daoman.task.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisUtil {
	
	private static JedisPool pool;
	
	private static Lock lock = new ReentrantLock();
	
	private static Logger LOG = Logger.getLogger(ZookeeperUtil.class);
	
	public static JedisPool getPool(){
		if(pool == null){
			LOG.debug("Jedis pool is not exist, lock and create pool.");
			
			lock.lock();
			
			if(pool!=null){
				lock.unlock();
				return pool;
			}
			
			try {
				Map<String, String> conf = FileUtil.readPropertyFile("config.properties", "utf-8");
				String host = conf.get("redis.server");
				String confPort = conf.get("redis.server.port");
				int port = (confPort==null || "".equals(confPort))?Protocol.DEFAULT_PORT:Integer.valueOf(confPort);
						
				pool = new JedisPool(new JedisPoolConfig(), host, port);
//				pool = new JedisPool("127.0.0.1", 6379);
				
			} catch (IOException e) {
				LOG.error("Error occurred when create jedis pool.", e);
			}finally{
				lock.unlock();
			}
		}
		
		return pool;
	}
	
	public static Jedis getJedis(){
		try {
			Jedis jedis = getPool().getResource();
			return jedis;
		} catch (Exception e) {
			LOG.error("Error occurred when get jedis client from pool.", e);
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		for(int i =0; i<20; i++){
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					Jedis jedis = null;
					try {
						 jedis = JedisUtil.getJedis();
						
						Set<String> tags = jedis.keys("tag_*_*_*");
						
						System.out.println(">>>>"+tags.toString());
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						JedisUtil.getPool().returnResource(jedis);
					}
				}
			});
			thread.start();
		}
		
	}
}
