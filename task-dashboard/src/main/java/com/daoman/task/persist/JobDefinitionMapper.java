/**
 * 
 */
package com.daoman.task.persist;

import java.util.List;

import com.daoman.task.domain.job.JobDefinition;

/**
 * @author parox
 *
 */
public interface JobDefinitionMapper {

	public JobDefinition insert(JobDefinition definition);
	
	public Integer delete(Integer id);
	
	public List<JobDefinition>queryAll();
}
