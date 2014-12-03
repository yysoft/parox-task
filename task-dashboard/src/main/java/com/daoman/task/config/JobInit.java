/**
 * 
 */
package com.daoman.task.config;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import net.caiban.db.YYConnPool;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.service.job.JobStatusService;
import com.daoman.task.thread.TaskControlThread;
import com.daoman.task.utils.ZookeeperUtil;
import com.google.common.collect.Lists;

/**
 * @author parox
 *
 */
@Component("jobInit")
public class JobInit {
	
	Logger LOG = Logger.getLogger(JobInit.class);
	
	@Resource
	private JobDefinitionService jobDefinitionService;
	@Resource
	private JobStatusService jobStatusService;
	
	private Watcher jobListWatch;
	
	@PostConstruct
	public void init(){
		YYConnPool.getInstance().initConnPools(null);

		TaskControlThread taskThread = new TaskControlThread();
		taskThread.setName("TaskControlThread");
		taskThread.setJobDefinitionService(jobDefinitionService);
		taskThread.setJobStatusService(jobStatusService);
		taskThread.start();
		
		//初始化任务
		
		
		//设置监听
		List<String> initJobList = watchChild(true);
		
		//获取 job definition
		initFromDB(initJobList);
		
	}
	
	private Watcher getWatcher(){
		if(jobListWatch != null){
			return jobListWatch;
		}
		
		jobListWatch = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				watchChild(false);
			}
		};
		
		return jobListWatch;
	}
	
	final static String JOB_ROOT="/parox/task/job_list";
	
	private void initFromDB(List<String> nodeList){
		
		List<JobDefinition> jobList = jobDefinitionService.queryAll(true);
		
		List<String> initedList = Lists.newArrayList();
		
		for(JobDefinition definition: jobList){
			//通过 getData 判断是否存在
			
			if(jobExist(definition.getJobName())==null){
				createNode(definition.getJobName());
			}
			//TODO 判断/parox/task/job/jobname 是否存在，不存在的情况下要创建
			
			TaskControlThread.addRunTask(definition);
			initedList.add(definition.getJobName());
		}
		
		for(String jobname: initedList){
			nodeList.remove(jobname);
		}
		
		for(String jobname: nodeList){
			deleteNode(jobname);
		}
		
	}
	
	private Stat jobExist(String jobname){
		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
		try {
			Stat stat = zk.exists(JOB_ROOT+"/"+jobname, false);
			return stat;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String createNode(String jobname){
		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
		try {
			String path = zk.create(JOB_ROOT+"/"+jobname, "1".getBytes(), ZookeeperUtil.getInstance().getAcl(), CreateMode.PERSISTENT);
			return path;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void deleteNode(String jobname){
		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
		try {
			zk.delete(JOB_ROOT+"/"+jobname, -1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> watchChild(boolean watchOnly){
		
		try {
			
			ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
			List<String> childList = zk.getChildren("/parox/task/job_list", getWatcher());
			
			if(!watchOnly){
				stopTask();
				startTask(childList);
			}
			
			return childList;
		} catch (KeeperException e) {
			
		} catch (InterruptedException e) {
			
		}
		return null;
	}
	
	private void stopTask(){
		List<String> stopedJob = Lists.newArrayList();
		for (String jobname : TaskControlThread.runningTasks.keySet()) {
			if(jobExist(jobname)==null){
				stopedJob.add(jobname);
			}
		}
		
		for(String jobname: stopedJob){
			TaskControlThread.removeRunningTask(jobname);
		}
	}
	
	private void startTask(List<String> nodeList){
		for(String jobname: nodeList){
			if(TaskControlThread.isRunning(jobname)){
				continue ;
			}
			JobDefinition definition = jobDefinitionService.queryOne(jobname);
			if(definition!=null){
				//TODO 同步JAR包
				TaskControlThread.addRunTask(definition);
			}
		}
	}
	
	@PreDestroy
	public void destroy(){
		
		TaskControlThread.runSwitch = false;
		
		YYConnPool.getInstance().destoryConnectionPools();
		
//		try {
//			ZookeeperUtil.getInstance().getZKClient().close();
//		} catch (InterruptedException e) {
//			LOG.error("Error occurred while close zookeeper client.", e);
//		}
	}
	
}
