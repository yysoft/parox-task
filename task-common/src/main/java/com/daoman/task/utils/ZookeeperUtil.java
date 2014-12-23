/**
 * 
 */
package com.daoman.task.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.caiban.utils.file.FileUtil;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

/**
 * @author parox
 * 
 */
public class ZookeeperUtil {

	private volatile static ZookeeperUtil _instance;
	private Lock lock;
	private ZooKeeper zkClient;
	
	private List<ACL> acls;
	
	private Logger LOG = Logger.getLogger(ZookeeperUtil.class);

	private ZookeeperUtil() {
		lock = new ReentrantLock(false);
	}

	public static ZookeeperUtil getInstance() {

		if (_instance == null) {
			synchronized (ZooKeeper.class) {
				if (_instance == null) {
					_instance = new ZookeeperUtil();
				}
			}
		}

		return _instance;
	}
	
	public ZooKeeper getZKClient(){
		
		if(zkClient==null){
			LOG.debug("Zookeeper client is not exist, lock and create zkClient.");
			lock.lock();
			
			if(zkClient!=null){
				lock.unlock();
				return zkClient;
			}
			
			try {
				Map<String, String> conf = FileUtil.readPropertyFile("config.properties", "utf-8");
				String zkhost = conf.get("zk.server");
				zkClient = new ZooKeeper(zkhost, 2000, new Watcher() {
					@Override
					public void process(WatchedEvent event) {
						
					}
				});
				
				zkClient.addAuthInfo("digest", conf.get("zk.digest.project").getBytes());
				zkClient.addAuthInfo("digest", conf.get("zk.digest.app").getBytes());
				
			} catch (IOException e) {
				LOG.error("Error occurred when create zookeeper client.", e);
			}finally{
				lock.unlock();
			}
			
		}
		
		return zkClient;
	}
	
	public List<ACL> getAcl(){
		
		if(acls!=null && acls.size()>0){
			return acls;
		}
		
		acls=new ArrayList<ACL>();
		
		
		try {
			Map<String, String> conf = FileUtil.readPropertyFile("config.properties", "utf-8");
			
			Id projectId = new Id("digest", DigestAuthenticationProvider.generateDigest(conf.get("zk.digest.project")));
			
			ACL  projectAcl= new ACL(ZooDefs.Perms.ALL, projectId);
			acls.add(projectAcl);
			
			Id appId = new Id("digest", DigestAuthenticationProvider.generateDigest(conf.get("zk.digest.app")));
			
			ACL  appAcl= new ACL(ZooDefs.Perms.ALL, appId);
			acls.add(appAcl);
			
			Id worldId= new Id("world", "anyone");
			ACL worldAcl = new ACL(ZooDefs.Perms.READ, worldId);
			acls.add(worldAcl);
			
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Can not read properties of zookeeper ACL.", e);
		} catch (IOException e) {
			LOG.error("Failure generate Digest.", e);
		}
		
		return acls;
	}
	
	public Stat exist(String path, boolean watch){
		ZooKeeper zk = getZKClient();
		try {
			Stat stat = zk.exists(path, watch);
			return stat;
		} catch (KeeperException e) {
			LOG.info("Node is not exist. node is "+path, e);
		} catch (InterruptedException e) {
			LOG.error("Failure check exist.",e);
		}
		return null;
	}
	
	public Stat exist(String path, Watcher watcher){
		ZooKeeper zk = getZKClient();
		try {
			Stat stat = zk.exists(path, watcher);
			return stat;
		} catch (KeeperException e) {
			LOG.info("Node is not exist. node is "+path, e);
		} catch (InterruptedException e) {
			LOG.error("Failure check exist.",e);
		}
		return null;
	}
	
	public String create(String path, String data, CreateMode createMode){
		
		ZooKeeper zk = getZKClient();
		try {
			String createdPath = zk.create(path, data.getBytes(), getAcl(),
					createMode);
			return createdPath;
		} catch (KeeperException e) {
			LOG.info("Can not create Node: "+path, e);
		} catch (InterruptedException e) {
			LOG.error("Failure create Node:"+path, e);
		}
		
		return null;
	}
	
	public boolean delete(String path, int version){
		ZooKeeper zk = getZKClient();
		try {
			zk.delete(path, -1);
			return true;
		} catch (InterruptedException e) {
			LOG.error("Failure delete node", e);
		} catch (KeeperException e) {
			LOG.info("The node is not deleted. node is "+path, e);
		}
		
		return false;
	}
	
}
