/**
 * 
 */
package com.daoman.task.service.job.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.daoman.task.config.AppConst;
import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.domain.job.JobStatusCond;
import com.daoman.task.persist.JobStatusMapper;
import com.daoman.task.service.job.JobStatusService;
import com.google.common.base.Strings;

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

	@Override
	public Pager<JobStatus> pageDefault(JobStatusCond cond,
			Pager<JobStatus> page) {
		
		if(page.getLimit()==0){
			page.setLimit(AppConst.DEFAULT_PAGE_SIZE_100);
		}
		
		page.setSortColumn(cond.getSort(page.getSort()));
		
		page.setRecords(jobStatusMapper.pageDefault(cond, page));
		page.setTotals(jobStatusMapper.pageDefaultCount(cond));
		
		return page;
	}

	@Override
	public Integer delete(JobStatusCond cond) {
		if(cond.getId()==null && Strings.isNullOrEmpty(cond.getJobName())){
			
			return null;
		}
		return jobStatusMapper.delete(cond);
	}

}
