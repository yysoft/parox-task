package com.daoman.task.domain.job;

import java.util.Date;

import com.daoman.task.domain.BaseDomain;

public class JobDefinition extends BaseDomain{
	
	
	private static final long serialVersionUID = 1643190952324185481L;

	private String jobName;
	private String jobGroup;
	private String jobClasspath;
	private String jobClassName;
	private String description;
	private String cron;
	private String isInUse; //停用0启用1
	private Date startTime;
	private Date endTime;
	private Long nextFireTime;
	private String singleRunning;
	
	public JobDefinition() {
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the jobGroup
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * @param jobGroup the jobGroup to set
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * @return the jobClasspath
	 */
	public String getJobClasspath() {
		return jobClasspath;
	}

	/**
	 * @param jobClasspath the jobClasspath to set
	 */
	public void setJobClasspath(String jobClasspath) {
		this.jobClasspath = jobClasspath;
	}

	/**
	 * @return the jobClassName
	 */
	public String getJobClassName() {
		return jobClassName;
	}

	/**
	 * @param jobClassName the jobClassName to set
	 */
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the cron
	 */
	public String getCron() {
		return cron;
	}

	/**
	 * @param cron the cron to set
	 */
	public void setCron(String cron) {
		this.cron = cron;
	}


	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the isInUse
	 */
	public String getIsInUse() {
		return isInUse;
	}

	/**
	 * @param isInUse the isInUse to set
	 */
	public void setIsInUse(String isInUse) {
		this.isInUse = isInUse;
	}

	/**
	 * @return the nextFireTime
	 */
	public Long getNextFireTime() {
		return nextFireTime;
	}

	/**
	 * @param nextFireTime the nextFireTime to set
	 */
	public void setNextFireTime(Long nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public String getSingleRunning() {
		return singleRunning;
	}

	public void setSingleRunning(String singleRunning) {
		this.singleRunning = singleRunning;
	}
	
	
}

