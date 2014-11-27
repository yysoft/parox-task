/**
 * Copyright 2011 ASTO.
 * All right reserved.
 * Created on 2011-3-31
 */
package com.daoman.task.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.daoman.task.CronTask;

/**
 * @author mays (mays@zz91.com)
 *
 * created on 2011-3-31
 */
@Deprecated
public class RunningSimpleTask {

	static Map<String, CronTask> RUNNING_TASK = new ConcurrentHashMap<String, CronTask>();
	
	public static void putTask(String key, CronTask value){
		RUNNING_TASK.put(key, value);
	}
	
	public static CronTask holderTask(String key){
		return RUNNING_TASK.get(key);
	}
}
