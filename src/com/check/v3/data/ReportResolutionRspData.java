package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportResolutionRspData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6748562545815804392L;
	
	private int id;
	private int submitterId;
	private String submitterName;
	private String description;
	private ArrayList<ImageItemData> images;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubmitterId() {
		return submitterId;
	}

	public void setSubmitterId(int submitterId) {
		this.submitterId = submitterId;
	}

	public String getSubmitterName() {
		return submitterName;
	}

	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ArrayList<ImageItemData> getImages() {
		return images;
	}

	public void setImages(ArrayList<ImageItemData> images) {
		this.images = images;
	}

}
