package com.daoman.task.service.job;

import com.daoman.task.domain.job.JobStatus;

public interface JobStatusService {

	public Integer save(JobStatus status);
	
	public Integer updateById(JobStatus status);
}
