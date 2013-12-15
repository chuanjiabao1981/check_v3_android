package com.check.v3.data;

import java.io.Serializable;

public class SimpleOrganization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7175634492313953870L;
	
	private int orgId;
	private String orgName;
	
	public int getOrgId() {
		return orgId;
	}
	
	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	

}
