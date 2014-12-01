/**
 * 
 */
package com.daoman.task.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.google.common.collect.Lists;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

/**
 * @author parox
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, KeeperException, InterruptedException {
		List<ACL> acls = Lists.newArrayList();
		Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest("parox:parox606"));
		
		ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
		acls.add(acl1);
		
		Id id2 = new Id("world", "anyone");
		ACL acl2 = new ACL(ZooDefs.Perms.READ, id2);
		acls.add(acl2);
		
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 2000, null);
		zk.addAuthInfo("digest", "parox:parox606".getBytes());
		zk.create("/test", "data".getBytes(), acls, CreateMode.EPHEMERAL);
		
		String result=null;
		
		result = zk.create("/parox/task/test", "".getBytes(), acls, CreateMode.PERSISTENT);
//		zk.delete("/parox/task/test", -1);
		
//		result = zk.create("/parox", "".getBytes(), acls, CreateMode.PERSISTENT);
//		System.out.println(result);
//		result = zk.create("/parox/task", "".getBytes(), acls, CreateMode.PERSISTENT);
//		System.out.println(result);
		result = zk.create("/parox/task/task1_", "".getBytes(), acls, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(result);
		result = zk.create("/parox/task/task2_", "".getBytes(), acls, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(result);
		result = zk.create("/parox/task/task3_", "".getBytes(), acls, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(result);
		result = zk.create("/parox/task/task4_", "".getBytes(), acls, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(result);
		
		List<String> child = zk.getChildren("/parox/task", new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getPath()+"   "+event.getType());
			}
		});
		
		String preNode = null;
		for(String c:child){
			if(c.equals(result.substring("/parox/task/".length()))){
				break;
			}
			preNode = c;
		}
		
		zk.getData("/parox/task/"+preNode, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getPath()+"   "+event.getType());
				//拿到锁
			}
			
		}, null);
		
		zk.delete("/parox/task/"+preNode, -1);
		
//		zk.getData(result, new Watcher() {
//			
//			@Override
//			public void process(WatchedEvent event) {
//				System.out.println(event.getPath()+"   "+event.getType());
//			}
//			
//		}, null);
//		
//		zk.setData(result, "update3".getBytes(), -1);
//		
//		zk.delete(result, -1);
		
		
		
//		zk.delete("/parox", -1);
		
//		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {
//			
//			@Override
//			public void process(WatchedEvent event) {
////				System.out.println("EVENT PATH>>>>>>>"+event.getPath());
//				System.out.println("EVENT PATH>>>>>>>"+JSONObject.fromObject(event).toString());
//			}
//			
//		});
//		
//		zk.addAuthInfo("digest", "parox:parox606".getBytes());
////		zk.create("/test", "data".getBytes(), acls, CreateMode.PERSISTENT);
//		byte[] result = zk.getData("/test", false, null);
//		
//		System.out.println(new String(result));
//		
//		zk.setData("/test", "update data1".getBytes(), 1);
//		
//		result = zk.getData("/test", false, null);
//		
//		System.out.println(new String(result));
//		
//		zk.getData("/test", new Watcher() {
//			
//			@Override
//			public void process(WatchedEvent event) {
//				System.out.println("EVENT TYPE"+event.getType().name());
//			}
//			
//		}, null);
//		
//		zk.create("/test", "update".getBytes(), null, createMode)
//		zk.delete("/test", 0);
	}

}
