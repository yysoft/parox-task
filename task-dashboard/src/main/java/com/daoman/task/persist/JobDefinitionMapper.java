/**
 * 
 */
package com.daoman.task.persist;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;

/**
 * @author parox
 *
 */
public interface JobDefinitionMapper {

	public Integer insert(JobDefinition definition);
	
	public Integer delete(Integer id);
	
	public List<JobDefinition>queryAll( @Param("inUse") Integer inUse);
	
	public List<JobDefinition> pageDefault(@Param("cond") JobDefinitionCond cond, @Param("page") Pager<JobDefinition> page);
	
	public Integer pageDefaultCount(@Param("cond") JobDefinitionCond cond);
	
	/**
	 * 按照id或job_name查找
	 * @param cond
	 * @return
	 */
	public JobDefinition queryOne(JobDefinitionCond cond);
	
	public Integer update(JobDefinition definition);
	
	public Integer updateIsInUse(@Param("id")Integer id, @Param("isInUse")Integer isInUse);
	
}
