/**
 * 
 */
package com.daoman.task.persist;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.domain.job.JobStatusCond;

/**
 * @author parox
 *
 */
public interface JobStatusMapper {

	public Integer insert(JobStatus status);
	
	public Integer update(JobStatus status);
	
	public List<JobStatus> pageDefault(@Param("cond") JobStatusCond cond,
			@Param("page") Pager<JobStatus> page);
	
	public Integer pageDefaultCount(@Param("cond") JobStatusCond cond);
	
	public Integer delete(@Param("cond") JobStatusCond cond);
	
}
