package com.daoman.task.web;


import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.daoman.task.utils.HttpSessionUtil;
import com.daoman.task.utils.StrUtils;


public class MemberContextInterceptor extends HandlerInterceptorAdapter{

	private Logger log = Logger.getLogger(MemberContextInterceptor.class);
	
	
	public static final String USER_SESSION = "user";
	
	/**登录页面相关的常量*/
	public static final String PROCESS_URL = "processUrl";
	public static final String RETURN_URL = "returnUrl";
			
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		//在用户超时退出的时候记录日志，以备跟踪和分析问题
//		SessionTimeOutDto logInfo = new SessionTimeOutDto(cs==null?null:cs.getUserId(), sessionId, RequestUtils.getLocation(request));
		String uri = getURI(request);
		// 不在验证的范围内
		if (exclude(uri)) {
			return true;
		}
		//cookie为空就从session读(搜索服务器等其他应用可根据具体需要是否舍弃以下代码)
		if(HttpSessionUtil.getAttribute(request, USER_SESSION) != null){
			return true;
		}

		//保存异常跳出的日志信息
//			logInfo.setDetail(JSON.toJSONString(request.getCookies()));
//			userLogService.add(null, null, 999, "09", request, logInfo);

		//ajax请求登录专门处理
		if(isAjaxRequest(request)){
			response.addHeader("sessionstate", "timeout");
			if(isHtml(request)){
				//结果为html格式ajax请求
				response.getWriter().write("");
			}else{
				//结果为json格式ajax请求
				//response.getWriter().write("{\"success\":false, \"message\":\""+FrontUtils.getMessage(request, "error.common.session.timeout")+"\"}");
				/*sendError(request, response, "error.common.session.timeout");*/
			
			}
			return false;
		}
		response.sendRedirect(getLoginUrl(request));
		return false;
	}
	
	
	private String getLoginUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		StringBuilder buff = new StringBuilder();
		if (loginUrl.startsWith("/")) {
			String ctx = request.getContextPath();
			if (!StringUtils.isBlank(ctx)) {
				buff.append(ctx);
			}
		}
		buff.append(loginUrl).append("?");
		//buff.append(RETURN_URL).append("=").append(returnUrl);
		buff.append(RETURN_URL).append("=").append(StrUtils.encode(getLocation(request)));
		if (!StringUtils.isBlank(processUrl)) {
			buff.append("&").append(PROCESS_URL).append("=").append(
					getProcessUrl(request));
		}
		return buff.toString();
	}
	/**
	 * 获得当的访问路径
	 * 
	 * HttpServletRequest.getRequestURL+"?"+HttpServletRequest.getQueryString
	 * 
	 * @param request
	 * @return
	 */
	public static String getLocation(HttpServletRequest request) {
		UrlPathHelper helper = new UrlPathHelper();
		StringBuffer buff = request.getRequestURL();
		String uri = request.getRequestURI();
		String origUri = helper.getOriginatingRequestUri(request);
		buff.replace(buff.length() - uri.length(), buff.length(), origUri);
		String queryString = helper.getOriginatingQueryString(request);
		if (queryString != null) {
			buff.append("?").append(queryString);
		}
		return buff.toString();
	}
	
	private boolean isAjaxRequest(HttpServletRequest request) {
		String requestType = request.getHeader("X-Requested-With");
		//System.out.println("X-Requested-With:"+requestType);
		//System.out.println("Accept:"+request.getHeader("Accept"));
		if("XMLHttpRequest".equalsIgnoreCase(requestType)){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isHtml(HttpServletRequest request) {
		String acceptType = request.getHeader("Accept");
		if(acceptType.indexOf("text/html")!=-1){
			return true;
		}else{
			return false;
		}
	}

	private String getProcessUrl(HttpServletRequest request) {
		StringBuilder buff = new StringBuilder();
		if (loginUrl.startsWith("/")) {
			String ctx = request.getContextPath();
			if (!StringUtils.isBlank(ctx)) {
				buff.append(ctx);
			}
		}
		buff.append(processUrl);
		return buff.toString();
	}
	
	private boolean exclude(String uri) {
		//System.out.println(JSON.toJSONString(excludeUrls));
		if (excludeUrls != null) {
			for (String exc : excludeUrls) {
				if (exc.equals(uri)) {
					return true;
				}else if(exc.endsWith("*") && uri.startsWith(exc.substring(0,exc.length()-1))){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获得第三个路径分隔符的位置
	 * 
	 * @param request
	 * @throws IllegalStateException
	 *             访问路径错误，没有三(四)个'/'
	 */
	private static String getURI(HttpServletRequest request)
			throws IllegalStateException {
		UrlPathHelper helper = new UrlPathHelper();
		String uri = helper.getOriginatingRequestUri(request);
		String ctxPath = helper.getOriginatingContextPath(request);
		if (!StringUtils.isBlank(ctxPath)) {
			int start = uri.indexOf('/', 1);
			uri=uri.substring(start);
		}
		//System.out.println("uri:"+uri);
		return uri;
	}
	
/*	protected void sendError(HttpServletRequest request, HttpServletResponse response, String errorCode) throws IOException{
		String msg = FrontUtils.getMessage(request, errorCode);
		response.addHeader("parox_error", URLEncoder.encode(msg, "utf-8"));
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorCode);
	}*/
	
	private String loginUrl;
	private String processUrl;
	private String returnUrl;
	
	private boolean auth = true;
	private String[] excludeUrls;
	
	
	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getProcessUrl() {
		return processUrl;
	}

	public void setProcessUrl(String processUrl) {
		this.processUrl = processUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String[] getExcludeUrls() {
		return excludeUrls;
	}

	public void setExcludeUrls(String[] excludeUrls) {
		this.excludeUrls = excludeUrls;
	}
	}