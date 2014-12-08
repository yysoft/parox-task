/**
 * 
 */
package com.daoman.task.config;

/**
 * @author mays
 * 
 */
public class AppConst {

	public final static String SESSION_KEY = "sessionuserkey";
	
	/**
	 * 默认日期格式：yyyy-MM-dd HH:mm:ss
	 */
	public final static String DATE_FORMAT_DEFAULT="yyyy-MM-dd HH:mm:ss";
	/**
	 * 仅日期格式：yyyy-MM-dd
	 */
	public final static String DATE_FORMAT_DATE="yyyy-MM-dd";
	
	public final static int DEFAULT_PAGE_SIZE_100 = 100;
	
	public final static int DEFAULT_PAGE_SIZE_50 = 50;
	
	/**
	 * 仅时间格式：HH:mm:ss
	 */
	public final static String DATE_FORMAT_TIME="HH:mm:ss";
	
	public final static String JOB_LIST_ROOT="/parox/task/job_list";
	public static String getJobListPath(String jobname){
		return JOB_LIST_ROOT+"/"+jobname;
	}
	
	public final static String JOB_ROOT="/parox/task/job";
	public static String getJobPath(String jobname){
		return JOB_ROOT+"/"+jobname;
	}
	
}
