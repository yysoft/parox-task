/**
 * 
 */
package com.daoman.task.controller;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;
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
	
	@RequestMapping
	public ModelAndView index(HttpServletRequest request, ModelMap model){
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Pager<JobDefinition> page(HttpServletRequest request, 
			JobStatusCond cond, Pager<JobDefinition> page){
		
		//TODO 分页获取 job status
		page.setRecords(new ArrayList<JobDefinition>());
		
		return page;
	}
	
}
