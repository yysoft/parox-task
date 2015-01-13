/**
 * 
 */
package com.daoman.task.controller;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.caiban.utils.upload.MvcUpload;
import net.caiban.utils.upload.UploadResult;
import net.sf.json.JSONObject;

import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.config.AppConst;
import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
import com.daoman.task.domain.job.JobDefinitionCond;
import com.daoman.task.exception.ServiceException;
import com.daoman.task.service.job.JobDefinitionService;

/**
 * @author parox
 *
 */
@Controller
public class DefinitionController extends BaseController {
	
	@Resource
	private JobDefinitionService jobDefinitionService;
	@Resource
	private MessageSource messageSource;
	
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
		
		Pager<JobDefinition> resultPage = jobDefinitionService.pageDefault(cond, page);
		
		return resultPage;
	}
	
	@RequestMapping
	public ModelAndView create(HttpServletRequest request, JobDefinition definition, ModelMap model,
			Locale locale){
		
		try {
			definition = jobDefinitionService.save(request, definition);
			if(definition.getId()!=null && definition.getId().intValue()>0){
				return new ModelAndView("redirect:index.do");
			}
		} catch (ServiceException e) {
			model.put("error", messageSource.getMessage(e.getMessage(), null, locale));
		}
		
		model.put("definition", JSONObject.fromObject(definition).toString());
		return new ModelAndView("/definition/edit");
	}
	
	@RequestMapping
	@ResponseBody
	public JobDefinition queryOne(HttpServletRequest request, Integer id){
		
		JobDefinition definition = jobDefinitionService.queryOne(id);
		return definition;
	}
	
	@RequestMapping
	public ModelAndView update(HttpServletRequest request, JobDefinition definition, ModelMap model){
		
		Integer impact = jobDefinitionService.update(request, definition);
		if(impact!=null && impact.intValue()>0){
			return new ModelAndView("redirect:index.do");
		}
		model.put("error", "failure update");
		return new ModelAndView("edit");
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request, Integer id,
			Locale locale){
		//确认任务已从各个节点全部移除（结合停止功能实现）
		//执行删除
		
		try {
			Integer impact = jobDefinitionService.remove(id);
			boolean result = (impact!=null && impact.intValue()>0)?true:false;
			String code = result?"e.definition.remove.success":"e.definition.remove.failure";
			return ajaxResult(result, messageSource.getMessage(code, null, locale));
		} catch (ServiceException e) {
			return ajaxResult(false, messageSource.getMessage(e.getMessage(), null, locale));
		}
		
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> startTask(HttpServletRequest request, Integer id,
			@DateTimeFormat(pattern=AppConst.DATE_FORMAT_DEFAULT) Date start, 
			Locale locale) {
		String error = null;
		try {
			jobDefinitionService.start(id);
			return ajaxResult(true, messageSource.getMessage("e.definition.start.success", null, locale));
		} catch (ServiceException e) {
			error = e.getMessage();
		}
		return ajaxResult(false, messageSource.getMessage(error, null, locale));
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> stopTask(HttpServletRequest request, Integer id, Locale locale){
		String error = null;
		try {
			jobDefinitionService.stop(id);
			return ajaxResult(true, messageSource.getMessage("e.definition.stop.success", null, locale));
		} catch (ServiceException e) {
			error = e.getMessage();
		}
		return ajaxResult(false, messageSource.getMessage(error, null, locale));
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> run(HttpServletRequest request, Integer id, String jobName,
			@DateTimeFormat(pattern=AppConst.DATE_FORMAT_DEFAULT) Date gmtBasetime, 
			Locale locale){
		//TODO 手动执行任务
		String error = null;
		try {
			jobDefinitionService.run(id, jobName, gmtBasetime);
			return ajaxResult(true, messageSource.getMessage("e.definition.run.success", null, locale));
		} catch (ServiceException e) {
			error = e.getMessage();
		}
		
		return ajaxResult(false, messageSource.getMessage(error, null, locale));
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> runSync(HttpServletRequest request, Integer id, Date start){
		//TODO 手动执行任务（同步）
		
		return null;
	}
	
	@Deprecated
	@RequestMapping
	@ResponseBody
	public Map<String, UploadResult> uploadjar(HttpServletRequest request) {
		
//		Map<String, Object> result = Maps.newHashMap();
//		String uploadedFile = MvcUpload.localUpload(request, UPLOAD_ROOT, null);
//		if (!Strings.isNullOrEmpty(uploadedFile)) {
//			result.put("success", true);
//			result.put("message", uploadedFile);
//		}
		
//		return result;
		Map<String, UploadResult> resultMap = MvcUpload.batchLocalUpload(request, "/home/parox/static/task/jar/", ""+new Date().getTime());
		
		return resultMap;
	}
}
