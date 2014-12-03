/**
 * 
 */
package com.daoman.task.service.job.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

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
		// TODO Auto-generated method stub
		//需要初始化一些必要的参数信息
		return null;
	}

	@Override
	public JobDefinition queryOne(Integer id) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		//is_in_use 不在更新范围内
		return null;
	}

	@Override
	public Integer remove(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Pager<JobDefinition> pageDefault(JobDefinitionCond cond,
			Pager<JobDefinition> page) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
