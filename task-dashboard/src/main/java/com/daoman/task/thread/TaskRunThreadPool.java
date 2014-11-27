/**
 * Copyright 2011 ASTO.
 * All right reserved.
 * Created on 2011-3-23
 */
package com.daoman.task.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author mays (mays@zz91.com)
 * 
 *         created on 2011-3-23
 */
public class TaskRunThreadPool extends ThreadPoolExecutor {

	/**
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param unit
	 * @param workQueue
	 */
	public TaskRunThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {

		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();  
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    protected void beforeExecute(Thread t, Runnable r){
    	super.beforeExecute(t, r);
    	startTime.set(System.nanoTime());
    }
    
    protected void afterExecute(Runnable r, Throwable t) {
    	try {
    		numTasks.incrementAndGet();  
    		long endTime = System.nanoTime();  
    		long taskTime = endTime - startTime.get();  
    		totalTime.addAndGet(taskTime);
		} finally{
			super.afterExecute(r, t);
		}
	}
    
    protected void terminated() {
		try {
		} finally{
			super.terminated();
		}
	}
    
    /**
     * 获取总处理时间,单位纳秒
     */
    public long getTotalTime(){
    	return totalTime.get();
    }
    
    /**
     * 获取总处理量(处理成功的数量)
     */
    public long getNumTask(){
    	return numTasks.get();
    }
}
