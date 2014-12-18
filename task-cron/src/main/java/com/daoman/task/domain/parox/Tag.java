package com.daoman.task.domain.parox;

public class Tag {
	
	public static final int CAT_DOC = 1;
	public static final int CAT_TASK = 2;
	
    private Long tagId;

    private String tagName;
    
    private Integer cat;

    private Long orgId;

    private Long infoCount;

    private Long searchCount;

    private Long clickCount;
    
    public Tag(){
    	super();
    }
    
    public Tag(String tagName, Integer cat, Long orgId){
    	super();
    	this.tagName = tagName;
    	this.cat = cat;
    	this.orgId = orgId;
    	this.infoCount = 0L;
    	this.searchCount = 0L;
    	this.clickCount = 0L;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }

    public Integer getCat() {
		return cat;
	}

	public void setCat(Integer cat) {
		this.cat = cat;
	}

	public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getInfoCount() {
        return infoCount;
    }

    public void setInfoCount(Long infoCount) {
        this.infoCount = infoCount;
    }

    public Long getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Long searchCount) {
        this.searchCount = searchCount;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}