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

import org.quartz.CronExpression;

import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.service.job.JobStatusService;

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
				
				//如果nexttime为null，表示已经执行过了，此时重新计算nexttime并写入nexttime
				//否则判断当前时间是否大于nexttime，如果是则执行
				
				if(task.getNextFireTime()==null){
					
					CronExpression cron;
					try {
						cron = new CronExpression(task.getCron());
						if(CronExpression.isValidExpression(task.getCron())){
							Date nextFireTime=cron.getNextValidTimeAfter(now);
							task.setNextFireTime(nextFireTime.getTime());
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					if(now.getTime()>=task.getNextFireTime() && getTaskSize()<=workQueueSize){
						TaskRunThread runThread = new TaskRunThread(task,jobDefinitionService, jobStatusService);
						runThread.setName("TaskThread_"+task.getJobName());
						mainPool.execute(runThread);
						task.setNextFireTime(null);
					}
				}
			}
			
//			//索引优化任务
//			for(String jobname:BUILD_TASK_MAP.keySet()){
//				AbstractIdxTask task=BUILD_TASK_MAP.get(jobname);
//				if(task.getNextFireTime() == null){
//					CronExpression cron;
//					try {
//						cron = new CronExpression(task.getCron());
//						if(CronExpression.isValidExpression(task.getCron())){
//							Date nextFireTime=cron.getNextValidTimeAfter(now);
//							task.setNextFireTime(nextFireTime.getTime());
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}else{
//					if(now.getTime()>=task.getNextFireTime() && getTaskSize()<=workQueueSize){
//						mainPool.execute(new OptimizeThread(jobname, jobStatusService));
//						task.setNextFireTime(null);
//					}
//				}
//			}
			
			TaskControlThread.numTask = mainPool.getNumTask();
			TaskControlThread.totalTime = mainPool.getTotalTime();
			TaskControlThread.numQueue = mainPool.getQueue().size();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		if(runningTasks.get(taskName)!=null){
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
	
}
