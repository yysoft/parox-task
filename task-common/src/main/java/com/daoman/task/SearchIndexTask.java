/**
 * 
 */
package com.daoman.task;

/**
 * solr索引制作任务
 * 
 * <br />
 * <br />idxReq：检测数据库中指定时间段内是否有数据更新
 * <br />
 * <br />idxPost：查找出指定时间范围内的索引数据，并将该数据提交给solr，用于创建索引
 * <br />
 * 
 * @author mays
 *
 */
public abstract class  SearchIndexTask {
	
	private Long nextFireTime;
	private String cron;
	
	public final static String FORMATE="yyyy-MM-dd HH:mm:ss";

	/**
	 * 检测start与end之间是否有数据需要更新索引
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract Boolean idxReq(Long start, Long end) throws Exception;
	
	/**
	 * 提交start与end之间的数据给solr做索引
	 * @param start
	 * @param end
	 */
	public abstract void idxPost(Long start, Long end) throws Exception;
	
	public abstract void optimize() throws Exception;

	public Long getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Long nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
	
	
	
}
