package com.daoman.task.domain.job;

import java.io.Serializable;

import com.google.common.base.Strings;

public class JobDefinitionCond implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	

	private Integer id;
	private String jobName;
	private String jobGroup;
	private String jobClassName;
	private Integer isInUse; //停用0启用1
	private String jobNameMatchBefore;
	
	private enum Sort{
		
		ID("1", "jd.id");
		
		private String code;
		private String column;
		
		private Sort(String code, String column){
			this.code = code;
			this.column = column;
		}
		
		@Override
		public String toString(){
			return this.column;
		}
	}
	
	public String getSort(String code){
		if(Strings.isNullOrEmpty(code)){
			return Sort.ID.toString();
		}
		
		for(Sort sort: Sort.values()){
			if(code.equalsIgnoreCase(sort.code)){
				return sort.toString();
			}
		}
		
		return Sort.ID.toString();
	}
	
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
	public String getJobNameMatchBefore() {
		return jobNameMatchBefore;
	}
	public void setJobNameMatchBefore(String jobNameMatchBefore) {
		this.jobNameMatchBefore = jobNameMatchBefore;
	}
	
}

