/**
 * 
 */
package com.daoman.task;

import java.util.Date;

/**
 * @author parox
 *
 */
public class DocumentClearTask implements CronTask {

	@Override
	public boolean exec(Date baseDate) throws Exception {
		
		return false;
	}

	@Override
	public boolean clear(Date baseDate) throws Exception {
		
		return false;
	}

}
