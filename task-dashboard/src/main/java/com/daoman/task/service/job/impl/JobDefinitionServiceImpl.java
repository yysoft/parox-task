/**
 * 
 */
package com.daoman.task.service.job.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import com.daoman.task.config.AppConst;
import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;
import com.daoman.task.exception.ServiceException;
import com.daoman.task.persist.JobDefinitionMapper;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.utils.MvcUpload;
import com.daoman.task.utils.ZookeeperUtil;
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
	public JobDefinition save(HttpServletRequest request, JobDefinition definition) {
		
		//XXX job name, job classname 不允许重名未做判定
		String jarpath = uploadJar(request);
		
		if(Strings.isNullOrEmpty(jarpath)){
			return null;
		}
		
		definition.setJobClasspath(jarpath);
		definition = defaultDefinition(definition);
		jobDefinitionMapper.insert(definition);
		
		return definition;
	}
	
	public String uploadJar(HttpServletRequest request) {
		ZooKeeper zk = ZookeeperUtil.getInstance().getZKClient();

		byte[] path = null;
		try {
			path = zk.getData(AppConst.ZKCONFIG_JAR_UPLOAD_ROOT, false, null);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return MvcUpload.localUpload(request, new String(path), null);
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
	public Integer update(HttpServletRequest request, JobDefinition definition) {
		
		String jarpath = uploadJar(request);
		
		if(!Strings.isNullOrEmpty(jarpath)){
			definition.setJobClasspath(jarpath);
		}
		//XXX 需要处理变更 single_running 时任务重新启动的问题
		return jobDefinitionMapper.update(definition);
	}

	@Override
	public Integer remove(Integer id) throws ServiceException {
		
		stop(id);
		
		Integer impact = jobDefinitionMapper.delete(id);
		return impact;
	}

	@Override
	public Pager<JobDefinition> pageDefault(JobDefinitionCond cond,
			Pager<JobDefinition> page) {
		
		if(page.getLimit()==0){
			page.setLimit(AppConst.DEFAULT_PAGE_SIZE_50);
		}
		
		page.setSortColumn(cond.getSort(page.getSort()));
		
		page.setRecords(jobDefinitionMapper.pageDefault(cond, page));
		page.setTotals(jobDefinitionMapper.pageDefaultCount(cond));
		
		return page;
	}

	@Override
	public void start(Integer id) throws ServiceException {
		
		JobDefinition definition = queryOne(id);
		
		if(definition==null){
			throw new ServiceException("e.definition.not.exist");
		}
		
		if(JobDefinition.INUSE_TRUE == definition.getIsInUse()){
			throw new ServiceException("e.definition.start.repeat");
		}
		
		Integer i = jobDefinitionMapper.updateIsInUse(id, JobDefinition.INUSE_TRUE);
		if(i==null ||i.intValue()==0){
			throw new ServiceException("e.definition.start.failure");
		}
		
		ZookeeperUtil zu = ZookeeperUtil.getInstance();
		
		//通过 getData 判断是否存在
		String path= AppConst.getJobListPath(definition.getJobName());
		if(zu.exist(path, false)==null){
			zu.create(path, "1", CreateMode.PERSISTENT);
		}
		
		if(zu.exist(AppConst.getJobPath(definition.getJobName()), false)==null){
			zu.create(AppConst.getJobPath(definition.getJobName()), "1",
					CreateMode.PERSISTENT);
		}
		
	}

	@Override
	public void stop(Integer id) throws ServiceException {
		
		JobDefinition definition = queryOne(id);

		if (definition == null) {
			throw new ServiceException("e.definition.not.exist");
		}
		
		Integer i = jobDefinitionMapper.updateIsInUse(id, JobDefinition.INUSE_FALSE);
		if(i==null ||i.intValue()==0){
			throw new ServiceException("e.definition.stop.failure");
		}

		ZookeeperUtil zu = ZookeeperUtil.getInstance();
		
		String path = AppConst.getJobListPath(definition.getJobName());
		zu.delete(path, -1);
		
	}
	
}
