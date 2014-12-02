package com.daoman.task.domain.job;

import java.io.Serializable;

public class JobStatusCond implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String jobName;
	private String result;
	private String category;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
