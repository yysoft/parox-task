/**
 * 
 */
package com.daoman.task.config;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import net.caiban.db.YYConnPool;

import org.quartz.CronExpression;
import org.springframework.stereotype.Component;

import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.service.job.JobStatusService;
import com.daoman.task.thread.TaskControlThread;

/**
 * @author parox
 *
 */
@Component("jobInit")
public class JobInit {
	
	@Resource
	private JobDefinitionService jobDefinitionService;
	@Resource
	private JobStatusService jobStatusService;
	
	@PostConstruct
	public void init(){
		YYConnPool.getInstance().initConnPools(null);
		

		TaskControlThread taskThread = new TaskControlThread();
		taskThread.setName("TaskControlThread");
		taskThread.setJobDefinitionService(jobDefinitionService);
		taskThread.setJobStatusService(jobStatusService);
		taskThread.start();

		List<JobDefinition> jobList = jobDefinitionService.queryAll(true);

		for (JobDefinition def : jobList) {
			
			//普通任务
			if(JobDefinition.GROUP_TASK.equals(def.getJobGroup())){
				if (def.getCron() != null
						&& CronExpression.isValidExpression(def.getCron())) {
					TaskControlThread.addRunTask(def);
					continue;
				}
			}
			
			//搜索引擎索引任务
			if(JobDefinition.GROUP_IDX.equals(def.getJobGroup())){
				//XXX 暂不实现索引任务
			}
		}
	}
	
	@PreDestroy
	public void destroy(){
		YYConnPool.getInstance().destoryConnectionPools();
	}
	
}
