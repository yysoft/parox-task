/**
 * 
 */
package com.daoman.task.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;
import com.daoman.task.service.job.JobDefinitionService;
import com.daoman.task.utils.MvcUpload;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author parox
 *
 */
@Controller
public class DefinitionController extends BaseController {
	
	@Resource
	private JobDefinitionService jobDefinitionService;
	
	/**
	 * TODO 优先从 zookeeper 配置中心获取，否则本地获取（需要获取配置的工具类）
	 */
	final static String UPLOAD_ROOT = "/usr/data/task";

	@RequestMapping
	public ModelAndView index(HttpServletRequest request, ModelMap model){
		return null;
	}
	
	@RequestMapping
	public ModelAndView edit(HttpServletRequest request, ModelMap model,
			Integer id){
		
		model.put("id", id);
		
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Pager<JobDefinition> page(HttpServletRequest request,
			JobDefinitionCond cond, Pager<JobDefinition> page){
		
		//TODO 分页获取 job definition
		page = jobDefinitionService.pageDefault(cond, page);
		
		return page;
	}
	
	@RequestMapping
	@ResponseBody
	public JobDefinition create(HttpServletRequest request, JobDefinition definition){
		//TODO 创建任务
		
		//以下为上传JAR包的示例代码（file field name:uploadfile）
//		String uploadedFile = MvcUpload.localUpload(request, UPLOAD_ROOT, null);
		//如果上传成功，uploadedFile 为上传成功后的文件路径
		
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public JobDefinition queryOne(HttpServletRequest request, Integer id){
		//TODO 获取单个任务信息
		
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public JobDefinition update(HttpServletRequest request, JobDefinition definition){
		//TODO 更新任务信息（不包含JAR包上传更新，不包含启动任务）
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request, Integer id){
		//TODO 删除任务
		//确认任务已从各个节点全部移除（结合停止功能实现）
		//执行删除
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> runTask(HttpServletRequest request, Integer id, Date start){
		//TODO 手动执行任务（异步）
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> runTaskSync(HttpServletRequest request, Integer id, Date start){
		//TODO 手动执行任务（同步）
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> stopTask(HttpServletRequest request, Integer id){
		//TODO 停止运行任务（需要停止所有节点运行的任务）
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> resumeTask(HttpServletRequest request, Integer id){
		//TODO 恢复运行任务（需要恢复所有节点运行的任务）
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> uploadjar(HttpServletRequest request) {
		
		//TODO 单独上传一个JAR包
		
		Map<String, Object> result = Maps.newHashMap();
		String uploadedFile = MvcUpload.localUpload(request, UPLOAD_ROOT, null);
		if (!Strings.isNullOrEmpty(uploadedFile)) {
			result.put("success", true);
			result.put("message", uploadedFile);
		}
		
		return result;
	}
}
