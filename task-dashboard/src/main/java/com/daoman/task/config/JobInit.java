/**
 * 
 */
package com.daoman.task.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import net.caiban.db.YYConnPool;
import net.caiban.utils.file.PropertiesUtil;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
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
		Map<String, String> configProp=null;
		try {
			configProp = PropertiesUtil.classpathRead("config.properties");
			YYConnPool.getInstance().initConnPools(configProp.get("yyconn.db"));
		} catch (IOException e) {
			LOG.error("Error occurred when read properties file. The file is config.properties", e);
		}

		TaskControlThread taskThread = new TaskControlThread();
		taskThread.setName("TaskControlThread");
		taskThread.setJobDefinitionService(jobDefinitionService);
		taskThread.setJobStatusService(jobStatusService);
		taskThread.start();
		
		//设置监听
		List<String> initJobList = watchChild(true);
		
		//获取 job definition
		initFromDB(initJobList);
		
	}
	
	private void initFromDB(List<String> nodeList){
		
		List<JobDefinition> jobList = jobDefinitionService.queryAll(true);
		
		List<String> initedList = Lists.newArrayList();
		
		ZookeeperUtil zu = ZookeeperUtil.getInstance();
		for(JobDefinition definition: jobList){
			
			if(JobDefinition.RUNNING_MULITY.equalsIgnoreCase(definition.getSingleRunning())){
				TaskControlThread.addRunTask(definition);
				continue;
			}
			
			//通过 getData 判断是否存在
			String path= AppConst.getJobListPath(definition.getJobName());
			if(zu.exist(path, false)==null){
				zu.create(path, "1", CreateMode.PERSISTENT);
			}
			
			if(zu.exist(AppConst.getJobPath(definition.getJobName()), false)==null){
				zu.create(AppConst.getJobPath(definition.getJobName()), "1",
						CreateMode.PERSISTENT);
			}
			
			TaskControlThread.addRunTask(definition);
			initedList.add(definition.getJobName());
		}
		
		for(String jobname: initedList){
			nodeList.remove(jobname);
		}
		
		for(String jobname: nodeList){
			zu.delete(AppConst.getJobListPath(jobname), -1);
		}
		
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
	
	private List<String> watchChild(boolean watchOnly){
		
		try {
			
			ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
			List<String> childList = zk.getChildren(AppConst.JOB_LIST_ROOT, getWatcher());
			
			if(!watchOnly){
				stopTask();
				startTask(childList);
			}
			
			return childList;
		} catch (KeeperException e) {
			LOG.error("Can not get JOB LIST ROOT.", e);
		} catch (InterruptedException e) {
			LOG.error("Failure get and watch JOB LIST ROOT", e);
		}
		return null;
	}
	
	private void stopTask(){
		List<String> stopedJob = Lists.newArrayList();
		
		ZookeeperUtil zu = ZookeeperUtil.getInstance();
		for (String jobname : TaskControlThread.runningTasks.keySet()) {
			if(zu.exist(AppConst.getJobListPath(jobname), false)==null){
				stopedJob.add(jobname);
			}
		}
		
		for(String jobname: stopedJob){
			TaskControlThread.removeRunningTask(jobname);
			
			//XXX 最好删除 /parox/task/job/taskname 节点，
			//通过watch机制检测所有lock和next_fire_time均已经删除的情况下
		}
	}
	
	private void startTask(List<String> nodeList){
		for(String jobname: nodeList){
			if(TaskControlThread.isRunning(jobname)){
				continue ;
			}
			JobDefinition definition = jobDefinitionService.queryOne(jobname);
			if(definition!=null){
				//TODO 检查并同步JAR包(一台机器跑无所谓)
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
