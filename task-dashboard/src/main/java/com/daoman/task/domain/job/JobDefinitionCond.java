package com.daoman.task.domain.job;

import java.io.Serializable;

public class JobDefinitionCond implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	

	private Integer id;
	private String jobName;
	private String jobGroup;
	private String jobClassName;
	private Integer isInUse; //停用0启用1
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	public Integer getIsInUse() {
		return isInUse;
	}
	public void setIsInUse(Integer isInUse) {
		this.isInUse = isInUse;
	}
	
	
}

