package com.check.v3.data;

import java.io.Serializable;

public class Session implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2651288787449747186L;

	private String jsession_id;
	
	private String id;
	private String role;
	private String account;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJsession_id() {
		return jsession_id;
	}

	public void setJsession_id(String jsession_id) {
		this.jsession_id = jsession_id;
	}

	@Override
	public String toString() {
		return "Session [jsession_id=" + jsession_id + ", id=" + id + ", role="
				+ role + ", account=" + account + ", name=" + name + "]";
	}



}
