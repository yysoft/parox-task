/**
 * 
 */
package com.daoman.task.service.job;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;
import com.daoman.task.exception.ServiceException;


/**
 * @author mays
 *
 */
public interface JobDefinitionService {
	
	public List<JobDefinition> queryAll(Boolean isInUse);
	
	public Pager<JobDefinition> pageDefault(JobDefinitionCond cond, Pager<JobDefinition> page);
	
	public JobDefinition save(HttpServletRequest request, JobDefinition definition) throws ServiceException;
	
	public JobDefinition queryOne(Integer id);
	
	public JobDefinition queryOne(String jobname);
	
	public Integer update(HttpServletRequest request, JobDefinition definition);
	
	public Integer remove(Integer id)  throws ServiceException;
	
	public String uploadJar(HttpServletRequest request);
	
	public void start(Integer id) throws ServiceException;
	
	public void stop(Integer id) throws ServiceException;
	
	public void run(Integer id, String jobName, Date targetDate) throws ServiceException;
	
}
