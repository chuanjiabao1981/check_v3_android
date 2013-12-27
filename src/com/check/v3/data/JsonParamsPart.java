package com.check.v3.data;

import java.io.Serializable;

public class JsonParamsPart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 511615293720453613L;

	private String contentType;
	private String contentBody;
	private String contentKey;
	
	public JsonParamsPart(String contentKey, String contentType,
			String contentBody) {
		super();
		this.contentType = contentType;
		this.contentBody = contentBody;
		this.contentKey = contentKey;
	}

	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getContentBody() {
		return contentBody;
	}
	
	public void setContentBody(String contentBody) {
		this.contentBody = contentBody;
	}
	
	public String getContentKey() {
		return contentKey;
	}
	
	public void setContentKey(String contentKey) {
		this.contentKey = contentKey;
	}
}
