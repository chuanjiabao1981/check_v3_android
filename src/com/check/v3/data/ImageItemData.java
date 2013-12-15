package com.check.v3.data;

import java.io.Serializable;

public class ImageItemData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 902677781031785449L;
	
	private int id;
	private String path;
	
	
	
	public ImageItemData(int id, String path) {
		super();
		this.id = id;
		this.path = path;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	
}
