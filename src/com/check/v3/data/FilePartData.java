package com.check.v3.data;

import java.io.File;
import java.io.Serializable;

public class FilePartData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5185577560573211154L;


	private File file;
	private String contentType;
    private String contentKey;

	public FilePartData(String contentKey, String contentType, File file) {
		super();
		this.file = file;
		this.contentType = contentType;
		this.contentKey = contentKey;
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

	public String getContentKey() {
		return contentKey;
	}

	public void setContentKey(String contentKey) {
		this.contentKey = contentKey;
	}
    
    
}
