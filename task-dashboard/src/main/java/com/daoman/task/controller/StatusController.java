/**
 * 
 */
package com.daoman.task.controller;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobStatus;
import com.daoman.task.domain.job.JobStatusCond;
import com.daoman.task.service.job.JobStatusService;

/**
 * @author parox
 *
 */
@Controller
public class StatusController extends BaseController {
	
	@Resource
	private JobStatusService jobStatusService;
	@Resource
	private MessageSource messageSource;
	
	@RequestMapping
	public ModelAndView index(HttpServletRequest request, ModelMap model,
			String jobName){
		
		model.put("jobName", jobName);
		
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Pager<JobStatus> page(HttpServletRequest request, 
			JobStatusCond cond, Pager<JobStatus> page){
		
		Pager<JobStatus> resultPage = jobStatusService.pageDefault(cond, page);
		
		return resultPage;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request,
			JobStatusCond cond, Locale locale) {
		
		Integer impact = jobStatusService.delete(cond);
		if(impact!=null && impact.intValue()>0){
			return ajaxResult(true, messageSource.getMessage("e.status.remove.success", null, locale));
		}
		return ajaxResult(false, messageSource.getMessage("e.status.remove.failure", null, locale));
	}
	
}
