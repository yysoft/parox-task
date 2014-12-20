package com.daoman.task.service.job;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.domain.job.JobStatusCond;

public interface JobStatusService {

	public Integer save(JobStatus status);
	
	public Integer updateById(JobStatus status);
	
	public Pager<JobStatus> pageDefault(JobStatusCond cond, Pager<JobStatus> page);
	
	public Integer delete(JobStatusCond cond);
}
