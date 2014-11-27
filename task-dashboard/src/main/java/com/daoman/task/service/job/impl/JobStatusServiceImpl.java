/**
 * 
 */
package com.daoman.task.service.job.impl;

import org.springframework.stereotype.Component;

import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.service.job.JobStatusService;

/**
 * @author parox
 *
 */
@Component("jobStatusService")
public class JobStatusServiceImpl implements JobStatusService {

	@Override
	public Integer save(JobStatus status) {
		return null;
	}

	@Override
	public Integer updateById(JobStatus status) {
		return null;
	}

}
