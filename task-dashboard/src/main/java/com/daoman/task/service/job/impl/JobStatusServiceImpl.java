/**
 * 
 */
package com.daoman.task.service.job.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.persist.JobStatusMapper;
import com.daoman.task.service.job.JobStatusService;

/**
 * @author parox
 *
 */
@Component("jobStatusService")
public class JobStatusServiceImpl implements JobStatusService {
	
	@Resource
	private JobStatusMapper jobStatusMapper;

	@Override
	public Integer save(JobStatus status) {
		return jobStatusMapper.insert(status);
	}

	@Override
	public Integer updateById(JobStatus status) {
		
		return jobStatusMapper.update(status);
	}

}
