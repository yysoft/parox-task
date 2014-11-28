/**
 * 
 */
package com.daoman.task;

import java.util.Date;

/**
 * @author parox
 *
 */
public class DemoCronTask implements CronTask {

//	@Override
//	public boolean init() throws Exception {
//		
//		return false;
//	}

	@Override
	public boolean exec(Date baseDate) throws Exception {
		System.out.println(">>>>>>>>任务成功执行，时间：" + baseDate.getTime());
		return true;
	}

	@Override
	public boolean clear(Date baseDate) throws Exception {
		System.out.println(">>>>>>>>CLEAR 任务成功执行，时间：" + baseDate.getTime());
		return false;
	}

}
