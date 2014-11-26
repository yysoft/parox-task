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

	@Override
	public boolean init() throws Exception {
		return false;
	}

	@Override
	public boolean exec(Date baseDate) throws Exception {
		return false;
	}

	@Override
	public boolean clear(Date baseDate) throws Exception {
		return false;
	}

}
