/**
 * 
 */
package com.daoman.task.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.domain.Pager;
import com.daoman.task.domain.job.JobDefinition;

/**
 * @author parox
 *
 */
@Controller
public class DefinitionController extends BaseController {

	@RequestMapping
	public ModelAndView index(HttpServletRequest request, ModelMap model){
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Pager<JobDefinition> page(HttpServletRequest request, ModelMap model, Pager<JobDefinition> page){
		
		page.setRecords(new ArrayList<JobDefinition>());
		
		return page;
	}
	
	
}
