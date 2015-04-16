/**
 * Copyright 2011 ASTO.
 * All right reserved.
 * Created on 2011-3-18
 */
package com.daoman.task.thread;

import java.net.MalformedURLException;
import java.util.Date;

import net.caiban.utils.ClassHelper;
import net.caiban.utils.exception.StacktraceUtil;
import net.caiban.utils.http.IpUtil;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import com.daoman.task.CronTask;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.service.job.JobStatusService;
import com.daoman.task.utils.ZookeeperUtil;

/**
 * @author mays (mays@zz91.com)
 * 
 *         created on 2011-3-18
 */
public class TaskRunThread extends Thread {

	JobDefinition definition;

	JobDefinitionService jobDefinitionService;
	JobStatusService jobStatusService;
	
	Date targetDate;
	
	private final static Logger LOG = Logger.getLogger(TaskRunThread.class);
	
	private String lockPath;
	

	public TaskRunThread() {

	}

	public TaskRunThread(JobDefinition job,
			JobDefinitionService jobDefinitionService,
			JobStatusService jobStatusService) {
		this.definition = job;
		this.jobDefinitionService = jobDefinitionService;
		this.jobStatusService = jobStatusService;
	}

	@Override
	public void run() {
//		LOG.debug("taskbasetime:"+targetDate.getTime()+" 任务开始。。。。");
		JobStatus status = new JobStatus();
		Date start = new Date();
		if(targetDate==null){
			targetDate = start;
		}
		try {
			
//			jobDefinitionService.updateStartDateById(start, definition.getId());
			
			status.setJobName(definition.getJobName());
			status.setGmtBasetime(targetDate);
			status.setGmtTrigger(start);
			status.setResult("运行中...");
			status.setCategory(JobStatus.CATEGORY_SCHEDULER);
			status.setNodeKey(IpUtil.getRealIp());
			jobStatusService.save(status);
			LOG.debug("taskbasetime:"+targetDate.getTime()+" 准备。。。。");
			
			CronTask jobInstance = (CronTask) ClassHelper.load(
					definition.getJobClasspath(), definition.getJobClassName())
					.newInstance();
			LOG.debug("taskbasetime:"+targetDate.getTime()+" 实例化任务体。。。。");
			LOG.debug(jobInstance);
			jobInstance.clear(targetDate); //清理任务执行前的数据
			if (jobInstance.exec(targetDate)) {
				status.setResult("执行成功");
				LOG.debug("taskbasetime:"+targetDate.getTime()+" 任务执行成功。。。。");
			} else {
				status.setResult("执行失败");
				LOG.debug("taskbasetime:"+targetDate.getTime()+" 任务执行失败。。。。");
			}
			
		} catch (MalformedURLException e) {
			status.setResult(e.getMessage());
			status.setErrorMsg(StacktraceUtil.getStackTrace(e));
		} catch (ClassNotFoundException e) {
			status.setResult(e.getMessage());
			status.setErrorMsg(StacktraceUtil.getStackTrace(e));
		} catch (InstantiationException e) {
			status.setResult(e.getMessage());
			status.setErrorMsg(StacktraceUtil.getStackTrace(e));
		} catch (IllegalAccessException e) {
			status.setResult(e.getMessage());
			status.setErrorMsg(StacktraceUtil.getStackTrace(e));
		} catch (Exception e) {
			status.setResult(e.getMessage());
			status.setErrorMsg(StacktraceUtil.getStackTrace(e));
		}
		
		releaseLock();
		
		Date end = new Date();
		status.setRuntime(end.getTime() - start.getTime());
		LOG.debug("taskbasetime:"+targetDate.getTime()+" 任务执行结束。。。。");
		jobStatusService.updateById(status);
	}
	
	private void releaseLock(){
		
		if(lockPath==null){
			LOG.debug("There is no lock of job. job name is "+definition.getJobName());
			return ;
		}
		
		LOG.debug("Releasing lock. job name is "+definition.getJobName());
		ZooKeeper zk =ZookeeperUtil.getInstance().getZKClient();
		try {
			zk.delete(lockPath, -1);
		} catch (InterruptedException e) {
			LOG.error("Failure unlock task lock. job name is "+definition.getJobName(), e);
		} catch (KeeperException e) {
			LOG.error("Failure unlock task lock. job name is "+definition.getJobName(), e);
		}
	}

	public void setJobDefinitionService(JobDefinitionService service) {
		this.jobDefinitionService = service;
	}

	public void setJobStatusService(JobStatusService service) {
		this.jobStatusService = service;
	}
	
	public void setTargetDate(Date targetDate){
		this.targetDate = targetDate;
	}
	
	public void setReleaseLock(String path){
		this.lockPath=path;
	}
	
}
