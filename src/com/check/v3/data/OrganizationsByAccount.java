package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class OrganizationsByAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7407088273039738781L;
	
	private ArrayList<Organization> organizations;

	public ArrayList<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(ArrayList<Organization> organizations) {
		this.organizations = organizations;
	}

}
