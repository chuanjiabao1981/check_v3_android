package com.check.v3.data;

import java.io.Serializable;

public class ErrorItem implements Serializable {

	private static final long serialVersionUID = -1468782221463086053L;

	private String field;
	private String message;
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "ErrorItem [field=" + field + ", message=" + message + "]";
	}
	
}
