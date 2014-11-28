/**
 * 
 */
package com.daoman.task.persist;

import com.daoman.task.domain.job.JobStatus;

/**
 * @author parox
 *
 */
public interface JobStatusMapper {

	public Integer insert(JobStatus status);
	
	public Integer update(JobStatus status);
	
}
