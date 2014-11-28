/**
 * 
 */
package com.daoman.task.persist;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.daoman.task.domain.job.JobDefinition;

/**
 * @author parox
 *
 */
public interface JobDefinitionMapper {

	public JobDefinition insert(JobDefinition definition);
	
	public Integer delete(Integer id);
	
	public List<JobDefinition>queryAll( @Param("inUse") Integer inUse);
}
