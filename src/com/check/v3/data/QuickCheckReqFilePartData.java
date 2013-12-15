package com.check.v3.data;

import java.io.File;
import java.io.Serializable;

public class QuickCheckReqFilePartData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5185577560573211154L;


	public File file;
    public String contentType;

	public QuickCheckReqFilePartData(File file, String contentType) {
		super();
		this.file = file;
		this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
    
    
}
