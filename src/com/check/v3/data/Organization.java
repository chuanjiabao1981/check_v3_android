package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Organization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7853573653896095025L;

	private String name;
	private int id;
	private ArrayList<User> users;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<User> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}	
}
