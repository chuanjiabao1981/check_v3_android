package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class QuickCheckListItemData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5551393286319379485L;
	private int id;
	private int submitterId;
	private String submitterName;
	private int responsiblePeronId;
	private String responsiblePersonName;
	private int organizationId;
	private String organizationName;
	private String deadline;
	private String level;
	private String state;
	private String description;
	private ArrayList<ImageItemData> images;
	
	public QuickCheckListItemData(int id, int submitterId,
			String submitterName, int responsiblePeronId,
			String responsiblePersonName, int organizationId,
			String organizationName, String deadline, String level,
			String state, String description, ArrayList<ImageItemData> images) {
		super();
		this.id = id;
		this.submitterId = submitterId;
		this.submitterName = submitterName;
		this.responsiblePeronId = responsiblePeronId;
		this.responsiblePersonName = responsiblePersonName;
		this.organizationId = organizationId;
		this.organizationName = organizationName;
		this.deadline = deadline;
		this.level = level;
		this.state = state;
		this.description = description;
		this.images = images;
	}

	public QuickCheckListItemData() {

	}

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
	
	public int getResponsiblePeronId() {
		return responsiblePeronId;
	}
	
	public void setResponsiblePeronId(int responsiblePeronId) {
		this.responsiblePeronId = responsiblePeronId;
	}
	public String getResponsiblePersonName() {
		return responsiblePersonName;
	}
	
	public void setResponsiblePersonName(String responsiblePersonName) {
		this.responsiblePersonName = responsiblePersonName;
	}
	public int getOrganizationId() {
		return organizationId;
	}
	
	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
	public String getDeadline() {
		return deadline;
	}
	
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
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
