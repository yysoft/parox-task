/**
 * Copyright 2011 ASTO.
 * All right reserved.
 * Created on 2011-3-18
 */
package com.daoman.task.thread;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.quartz.CronExpression;

import com.daoman.task.config.AppConst;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.service.job.JobStatusService;
import com.daoman.task.utils.ZookeeperUtil;

/**
 * @author mays (mays@zz91.com)
 * 
 *         created on 2011-3-18
 */
public class TaskControlThread extends Thread {
	
	private static TaskRunThreadPool mainPool; // 任务执行线程池

	private int corePoolSize = 1; // 池中最小线程数量：2
	private int maximumPoolSize = 5; // 同时存在的最大线程数量：10
	private long keepAliveTime = 5; // 线程空闲保持时间：5秒
	private int workQueueSize = 10; // 工作队列最大值:100

	private static long numTask = 0; // 已处理数量
	private static long totalTime = 0; // 总处理时间
	private static int numQueue = 0; // 队列线程数量

	//private long waringValue = 10; // 警戒值,当超过警戒值,可以发出警告
	
	JobDefinitionService jobDefinitionService;
	JobStatusService jobStatusService;
	public static Map<String, JobDefinition> runningTasks = new ConcurrentHashMap<String, JobDefinition>();
//	public final static Map<String, AbstractIdxTask> BUILD_TASK_MAP = new ConcurrentHashMap<String, AbstractIdxTask>();
	public final static Map<String, Long> LAST_BUILD_TIME_MAP = new ConcurrentHashMap<String, Long>();
	
//	public static Map<String, TaskRunThread> runningThread = new ConcurrentHashMap<String, TaskRunThread>();
	public static boolean runSwitch = true;
	
	private final static Logger LOG = Logger.getLogger(TaskControlThread.class);

	public TaskControlThread(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, int workQueueSize) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveTime = keepAliveTime;
		this.workQueueSize = workQueueSize;

		TaskControlThread.mainPool = new TaskRunThreadPool(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(workQueueSize),
				new ThreadPoolExecutor.AbortPolicy());
	}
	
	public TaskControlThread(){
		TaskControlThread.mainPool = new TaskRunThreadPool(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(workQueueSize),
				new ThreadPoolExecutor.AbortPolicy());
	}
	
	public static void excute(Runnable command){
		mainPool.execute(command);
	}

	@Override
	public void run() {
		while (runSwitch) {

			Date now=new Date();

			//普通任务
			for (String jobname : runningTasks.keySet()) {
				
				//如果是single run 
				//nextfiretime 可放到zookeeper内，作为公共配置
				
				JobDefinition task = runningTasks.get(jobname);
				
				//XXX 需要处理RUNNING_MULITY的情况
//				if(JobDefinition.RUNNING_MULITY.equalsIgnoreCase(task.getSingleRunning())){
//					runTask(task, null);
//					continue ;
//				}
				
				Long nextFireTime = getNextFireTime(jobname);
				
				if(nextFireTime==null){
					resetNextFireTime(task, now);
					continue ;
				}
				
				if (now.getTime() >= nextFireTime.longValue()
						&& (getTaskSize() < workQueueSize
						|| getActivityCount() < maximumPoolSize)) {
					//获取锁
					if(!holdLock(jobname, nextFireTime)){
						continue ;
					}
					
//					task.setNextFireTime(nextFireTime);
					//FIXME Caller 获取 thread 执行结果，成功后释放锁（现在释放锁有问题）
					runTask(task, nextFireTime);
					
					deleteNextFireTime(jobname, nextFireTime);
					
				}
				
//				//如果nexttime为null，表示已经执行过了，此时重新计算nexttime并写入nexttime
//				//否则判断当前时间是否大于nexttime，如果是则执行
//				
//				if(task.getNextFireTime()==null){
//					
//					CronExpression cron;
//					try {
//						cron = new CronExpression(task.getCron());
//						if(CronExpression.isValidExpression(task.getCron())){
//							Date nextFireTimeDate=cron.getNextValidTimeAfter(now);
//							task.setNextFireTime(nextFireTimeDate.getTime());
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}else{
//					if(now.getTime()>=task.getNextFireTime() && getTaskSize()<=workQueueSize){
//						
//						runTask(task);
//						
//						task.setNextFireTime(null);
//					}
//				}
				
			}
			
			statisticPool();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void deleteNextFireTime(String jobname, Long nextfiretime){
		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
		try {
			zk.delete(getNextFireTimePath(jobname), -1);
		} catch (InterruptedException e) {
			LOG.debug("Failure delete next fire time. time is "+nextfiretime, e);
		} catch (KeeperException e) {
			LOG.debug("Failure delete next fire time. time is "+nextfiretime, e);
		}
	}
	
	public static String getLockPath(String jobname, Long nextfiretime){
		return AppConst.JOB_ROOT+"/"+jobname+"/lock_"+nextfiretime;
	}
	
	private boolean holdLock(String jobname, Long nextfiretime){
		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
		
		try {
			zk.create(getLockPath(jobname, nextfiretime), jobname.getBytes(), ZookeeperUtil.getInstance().getAcl(), CreateMode.EPHEMERAL);
			LOG.debug("Successfully get lock. job name is "+jobname);
			return true;
		} catch (KeeperException e) {
			LOG.error("Failure get lock. job name is "+jobname, e);
		} catch (InterruptedException e) {
			LOG.error("Failure get lock. job name is "+jobname, e);
		}
		return false;
	}
	
	private void resetNextFireTime(JobDefinition task, Date now){
		CronExpression cron;
		try {
			cron = new CronExpression(task.getCron());
			if(CronExpression.isValidExpression(task.getCron())){
				Date nextFireTimeDate=cron.getNextValidTimeAfter(now);
				
				ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
				
				String nft = String.valueOf(nextFireTimeDate.getTime());
				
				try {
					zk.create(getNextFireTimePath(task.getJobName()), nft.getBytes(), ZookeeperUtil.getInstance().getAcl(), CreateMode.EPHEMERAL);
					LOG.debug("Successfully reset next fire time. job name is "+task.getJobName());
				} catch (KeeperException e) {
					LOG.info("Next fire time not reset. job name is "+task.getJobName(), e);
				} catch (InterruptedException e) {
					LOG.info("Next fire time not reset. job name is "+task.getJobName(), e);
				}
				
			}
			
		} catch (ParseException e) {
			LOG.error("Can not parse cron exception. job name is "+task.getJobName(), e);
		}
	}
	
	public static String getNextFireTimePath(String jobname){
		return AppConst.JOB_ROOT+"/"+jobname+"/next_fire_time";
	}
	
//	static final String PATH_ROOT="/parox/task/job";
	
	private Long getNextFireTime(String jobname){

		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();
		try {
			
			byte[] nft=zk.getData(getNextFireTimePath(jobname), null, null);
			Long nextFireTime= Long.valueOf(new String(nft));
			
			return nextFireTime;
		} catch (KeeperException e1) {
			LOG.info("Can not get next_fire_time from zookeeper, job name is "+jobname, e1);
		} catch (InterruptedException e1) {
			LOG.error("Can not get next_fire_time from zookeeper, job name is "+jobname, e1);
		}
		return null;
	}
	
	private void runTask(JobDefinition task, Long nextFireTime){
		//XXX 可在此处设置服务器运行数（在任务运行结束后从zookeeper删除），以便做不同服务器之间的负载
		TaskRunThread runThread = new TaskRunThread(task,jobDefinitionService, jobStatusService);
		if(nextFireTime!=null){
			runThread.setReleaseLock(getLockPath(task.getJobName(), nextFireTime));
		}
		mainPool.execute(runThread);
	}
	
	private void statisticPool(){	
		TaskControlThread.numTask = mainPool.getNumTask();
		TaskControlThread.totalTime = mainPool.getTotalTime();
		TaskControlThread.numQueue = mainPool.getQueue().size();
	}
	
	
	/**
	 * 添加任务到runningTask
	 */
	synchronized public static boolean addRunTask(JobDefinition task) {
		// 检测是否已经在运行中
		// 添加到runningTask队列
		runningTasks.put(task.getJobName(), task);
		return false;
	}

	/**
	 * 将任务从runningTask移除
	 */
	synchronized public static boolean removeRunningTask(String taskName) {
		// 从runningTask中移除任务
		JobDefinition definition = runningTasks.get(taskName);
		if(definition!=null){
			if(JobDefinition.RUNNING_SINGLE.equalsIgnoreCase(definition.getSingleRunning())){
				//TODO 移除next_fire_time
				ZookeeperUtil.getInstance().delete(getNextFireTimePath(taskName), -1);
			}
			runningTasks.remove(taskName);
		}
		return false;
	}

	public void setJobDefinitionService(JobDefinitionService service) {
		this.jobDefinitionService = service;
	}

	public void setJobStatusService(JobStatusService service) {
		this.jobStatusService = service;
	}

	/**
	 * @return the workQueueSize
	 */
	public int getWorkQueueSize() {
		return workQueueSize;
	}

	/**
	 * @param workQueueSize the workQueueSize to set
	 */
	public void setWorkQueueSize(int workQueueSize) {
		this.workQueueSize = workQueueSize;
	}

	/**
	 * @return the numTask 总处理量
	 */
	public static long getNumTask() {
		return numTask;
	}

	/**
	 * @return the totalTime 总处理时间
	 */
	public static long getTotalTime() {
		return totalTime;
	}

	/**
	 * @return the numQueue 队列长度
	 */
	public static int getNumQueue() {
		return numQueue;
	}

	public static int getTaskSize(){
		return mainPool.getQueue().size();
	}
	
	public static int getActivityCount(){
		return mainPool.getActiveCount();
	}
	
	public static int getPoolSize(){
		return mainPool.getPoolSize();
	}
	
	public static boolean isRunning(String jobname){
		return runningTasks.get(jobname)!=null;
	}
	
}
