/**
 * 
 */
package com.daoman.task.service.job.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.daoman.task.config.AppConst;
import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;
import com.daoman.task.persist.JobDefinitionMapper;
import com.daoman.task.service.job.JobDefinitionService;
import com.google.common.base.Strings;

/**
 * @author mays
 *
 */
@Component("jobDefinitionService")
public class JobDefinitionServiceImpl implements JobDefinitionService {
	
	@Resource
	private JobDefinitionMapper jobDefinitionMapper;
	
	@Override
	public List<JobDefinition> queryAll(Boolean isInUse) {
		
		Integer inuse = null;
		if(isInUse!=null){
			inuse = isInUse?JobDefinition.INUSE_TRUE:JobDefinition.INUSE_FALSE;
		}
		
		return jobDefinitionMapper.queryAll(inuse);
	}

	@Override
	public JobDefinition save(JobDefinition definition) {
		definition = defaultDefinition(definition);
		return jobDefinitionMapper.insert(definition);
	}
	
	private JobDefinition defaultDefinition(JobDefinition definition){
		
		definition.setIsInUse(JobDefinition.INUSE_FALSE);
		definition.setJobGroup(JobDefinition.GROUP_TASK);
		
		return definition;
	}

	@Override
	public JobDefinition queryOne(Integer id) {
		if(id == null || id.intValue() == 0){
			return null;
		}
		JobDefinitionCond cond = new JobDefinitionCond();
		cond.setId(id);
		return jobDefinitionMapper.queryOne(cond);
	}

	@Override
	public JobDefinition queryOne(String jobname) {
		if(Strings.isNullOrEmpty(jobname)){
			return null;
		}
		
		JobDefinitionCond cond = new JobDefinitionCond();
		cond.setJobName(jobname);
		return jobDefinitionMapper.queryOne(cond);
	}

	@Override
	public Integer update(JobDefinition definition) {
		
		return jobDefinitionMapper.update(definition);
	}

	@Override
	public Integer remove(Integer id) {
		return jobDefinitionMapper.delete(id);
	}

	@Override
	public Pager<JobDefinition> pageDefault(JobDefinitionCond cond,
			Pager<JobDefinition> page) {
		
		if(page.getLimit()==0){
			page.setLimit(AppConst.DEFAULT_PAGE_SIZE_50);
		}
		
		page.setRecords(jobDefinitionMapper.pageDefault(cond, page));
		page.setTotals(jobDefinitionMapper.pageDefaultCount(cond));
		
		return page;
	}
	
}
