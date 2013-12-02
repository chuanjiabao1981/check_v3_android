package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53254991316708634L;

	private ArrayList<ErrorItem> errors;

	public ArrayList<ErrorItem> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<ErrorItem> errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "ResponseData [errors=" + errors + "]";
	}	
	
	
}
