package com.daoman.task.domain.job;

import java.util.Date;

import com.daoman.task.domain.BaseDomain;

public class JobStatus extends BaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2324915422637310967L;
	
	public static final String SUCCESS="success";
	public static final String FAILED="failed";
	
	private String jobName;
	private Date gmtBasetime;
	private String result;
	private Long runtime;
	private Date gmtTrigger;
	private String errorMsg;
	private String category;
	private Integer numRetry;
	private String nodeKey;
	
	/**
	 * 
	 */
	public JobStatus() {
	}
	
	/**
	 * @return the gmtBasetime
	 */
	public Date getGmtBasetime() {
		return gmtBasetime;
	}
	/**
	 * @param gmtBasetime the gmtBasetime to set
	 */
	public void setGmtBasetime(Date gmtBasetime) {
		this.gmtBasetime = gmtBasetime;
	}
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return the runtime
	 */
	public Long getRuntime() {
		return runtime;
	}
	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(Long runtime) {
		this.runtime = runtime;
	}
	/**
	 * @return the gmtTrigger
	 */
	public Date getGmtTrigger() {
		return gmtTrigger;
	}
	/**
	 * @param gmtTrigger the gmtTrigger to set
	 */
	public void setGmtTrigger(Date gmtTrigger) {
		this.gmtTrigger = gmtTrigger;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the numRetry
	 */
	public Integer getNumRetry() {
		return numRetry;
	}
	/**
	 * @param numRetry the numRetry to set
	 */
	public void setNumRetry(Integer numRetry) {
		this.numRetry = numRetry;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}
	
}
