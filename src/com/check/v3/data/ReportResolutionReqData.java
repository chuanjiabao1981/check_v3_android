package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportResolutionReqData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1316541297500511851L;

	private String description;
	private ArrayList<Integer> neededdeleteImagesId;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ArrayList<Integer> getNeededdeleteImagesId() {
		return neededdeleteImagesId;
	}

	public void setNeededdeleteImagesId(ArrayList<Integer> neededdeleteImagesId) {
		this.neededdeleteImagesId = neededdeleteImagesId;
	}
	
}
