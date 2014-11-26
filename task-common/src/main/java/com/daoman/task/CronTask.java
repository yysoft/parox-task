/**
 * Copyright 2010 ASTO.
 * All right reserved.
 * Created on Oct 20, 2010
 */
package com.daoman.task;

import java.util.Date;

/**
 * @author mays(mays@zz91.net)
 * 
 * 任务抽象类，所有mission都必需实现ZZTask接口<br />
 * 
 * init():<br/>
 * 在新增任务，或者编辑任务且需要重新上传jar包时调用，一般init用来生成数据表<br/><br/>
 * 
 * exec(Date baseDate):<br/>
 * 任务的具体执行方法，其中baseDate是个时间基准<br/>
 * 当任务需要以某个时间为基准执行时，就可以以该时间作为基准<br/>
 * <br/>
 * clear(Date baseDate):<br/>
 * 用来清理任务执行数据<br/>
 * 当某个任务执行失败，需要恢复重新执行时，会先执行clear()清理执行一半的数据
 */
public interface CronTask {
	
	public boolean init() throws Exception;
	
	public boolean exec(Date baseDate) throws Exception;
	
	public boolean clear(Date baseDate) throws Exception;
}
