/**
 * 
 */
package com.daoman.task.service.job;

import java.util.List;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;


/**
 * @author mays
 *
 */
public interface JobDefinitionService {
	
	public List<JobDefinition> queryAll(Boolean isInUse);
	
	public Pager<JobDefinition> pageDefault(JobDefinitionCond cond, Pager<JobDefinition> page);
	
	public JobDefinition save(JobDefinition definition);
	
	public JobDefinition queryOne(Integer id);
	
	public JobDefinition queryOne(String jobname);
	
	public Integer update(JobDefinition definition);
	
	public Integer remove(Integer id);
	
}
