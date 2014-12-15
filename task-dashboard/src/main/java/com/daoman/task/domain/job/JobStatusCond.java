package com.daoman.task.domain.job;

import java.io.Serializable;

import com.google.common.base.Strings;

public class JobStatusCond implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String jobName;
	private String result;
	private String category;
	
	private enum Sort{
		
		ID("1", "js.id"), GMT_BASETIME("2", "js.gmt_basetime"), GMT_TRIGGER("3", "js.gmt_trigger") ;
		
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
