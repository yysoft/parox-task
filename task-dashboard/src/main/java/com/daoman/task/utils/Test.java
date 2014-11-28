/**
 * 
 */
package com.daoman.task.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.google.common.collect.Lists;

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
		
		String result = zk.create("/test", "data2".getBytes(), acls, CreateMode.EPHEMERAL);
		System.out.println(result);
		
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
