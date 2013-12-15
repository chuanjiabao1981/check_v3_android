package com.check.v3.data;

import java.io.Serializable;

public class IssueLevel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7289960367020665316L;

	private String issueLevelId;
	private String issueLevelDisplayName;
	
	public IssueLevel(String issueLevelId, String issueLevelDisplayName) {
		super();
		this.issueLevelId = issueLevelId;
		this.issueLevelDisplayName = issueLevelDisplayName;
	}

	public String getIssueLevelId() {
		return issueLevelId;
	}
	
	public void setIssueLevelId(String issueLevelId) {
		this.issueLevelId = issueLevelId;
	}
	
	public String getIssueLevelDisplayName() {
		return issueLevelDisplayName;
	}
	
	public void setIssueLevelDisplayName(String issueLevelDisplayName) {
		this.issueLevelDisplayName = issueLevelDisplayName;
	}
	
}
