package com.daoman.task.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.utils.FileUtil;

@Controller
public class LoginController extends BaseController{

	/**
	 * TODO 优先从 zookeeper 配置中心获取，否则本地获取（需要获取配置的工具类）
	 */
	final static String UPLOAD_ROOT = "/usr/data/task";
	
	@RequestMapping
	public ModelAndView login(HttpServletRequest request, ModelMap model){
		return null;
	}
	
	@RequestMapping
	public ModelAndView doLogin(HttpServletRequest request, String name, String password, ModelMap model) throws IOException {
		//帐号和密码写死在配置文件中
		Map<String, String> conf = FileUtil.readPropertyFile("user.account.properties", "utf-8");
		if(conf.get(name) != null){
			if(conf.get(name).equals(password)){
				HttpSession session =request.getSession();
				session.setAttribute("user", name);
				return new ModelAndView("definition/index");
			}
		}
		return new ModelAndView("/login/login");
	}
}
