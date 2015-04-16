package com.daoman.task.controller.p;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.caiban.utils.file.PropertiesUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.daoman.task.config.AppConst;
import com.daoman.task.controller.BaseController;
import com.daoman.task.domain.SessionUser;
import com.google.common.base.Strings;

@Controller
public class SysController extends BaseController{

	/**
	 * TODO 优先从 zookeeper 配置中心获取，否则本地获取（需要获取配置的工具类）
	 */
	final static String UPLOAD_ROOT = "/usr/data/task";
	
	@RequestMapping
	public ModelAndView login(HttpServletRequest request, ModelMap model, 
			String returnUrl){
		model.put("returnUrl", returnUrl);
		return null;
	}
	
	@RequestMapping
	public ModelAndView doLogin(HttpServletRequest request, String name,
			String password, ModelMap model, String returnUrl)
			throws IOException {
		
		Map<String, String> configProp = PropertiesUtil.classpathRead("config.properties");
		Map<String, String> conf = PropertiesUtil.read(configProp.get("auth.db"),PropertiesUtil.CHARSET_UTF8);
		
		if(conf.get(name) != null){
			if(conf.get(name).equals(password)){
				
				SessionUser user = new SessionUser();
				user.setAccount(name);
				setSessionUser(request, user);
				
				if(Strings.isNullOrEmpty(returnUrl)){
					return new ModelAndView("redirect:/index.do");
				}else{
					return new ModelAndView("redirect:"+returnUrl); 
				}
			}
		}
		
		model.put("returnUrl", returnUrl);
		return new ModelAndView("/p/sys/login");
	}
	
	@RequestMapping
	public ModelAndView logout(HttpServletRequest request, ModelMap model){
		removeSession(request, AppConst.SESSION_KEY);
		return new ModelAndView("redirect:/p/sys/login.do");
	}
}
