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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

import com.daoman.task.thread.TaskControlThread;
import com.google.common.collect.Maps;

/**
 * @author mays
 *
 */
@Controller
public class RootController extends BaseController{

	
	@Resource
	MessageSource messageSource;
	
	@RequestMapping
	public ModelAndView index(HttpServletRequest request, Map<String, Object> out
			){
		
		RequestContext requestContext = new RequestContext(request);
		Locale locale = requestContext.getLocale();
		request.setAttribute("locale", locale);
		
		return null;
	}
	
	@RequestMapping
	public ModelAndView indexMobel(HttpServletRequest request, Map<String, Object> out){
	
		return null;
	}
	
	@RequestMapping
	public ModelAndView test(HttpServletRequest request, Map<String, Object> out){
	
		return null;
	}
	
	@RequestMapping
	@ResponseBody
	public Map<String, Object> monitor(HttpServletRequest request){
		Map<String, Object> map = Maps.newHashMap();
		
		map.put("numQueue", TaskControlThread.getNumQueue());
		map.put("numTask", TaskControlThread.getNumTask());
		map.put("poolSize", TaskControlThread.getPoolSize());
		map.put("activityCount", TaskControlThread.getActivityCount());
		
		return map;
	}
}
