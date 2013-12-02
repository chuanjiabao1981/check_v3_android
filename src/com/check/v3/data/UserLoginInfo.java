package com.check.v3.data;

import java.io.Serializable;

public class UserLoginInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3744307099294300132L;
	
	private String userName;
	private String userPassword;
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserPassword() {
		return userPassword;
	}
	
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	@Override
	public String toString() {
		return "UserLoginInfo [userName=" + userName + ", userPassword="
				+ userPassword + "]";
	}
}
