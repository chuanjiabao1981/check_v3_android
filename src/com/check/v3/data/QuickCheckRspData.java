package com.check.v3.data;

import java.io.Serializable;

public class QuickCheckRspData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 273265606371042886L;

	// {
	// "id": 175,
	// "submitterId": 858,
	// "submitterName": "ceshi_002",
	// "responsiblePeronId": 868,
	// "responsiblePersonName": "ceshi_003",
	// "organizationId": 579,
	// "organizationName": "tt2",
	// "deadline": "2012-03-12",
	// "level": "HIGH",
	// "state": "OPENED",
	// "description": "1111111111"
	// }

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

	@Override
	public String toString() {
		return "QuickCheckRspData [id=" + id + ", submitterId=" + submitterId
				+ ", submitterName=" + submitterName + ", responsiblePeronId="
				+ responsiblePeronId + ", responsiblePersonName="
				+ responsiblePersonName + ", organizationId=" + organizationId
				+ ", organizationName=" + organizationName + ", deadline="
				+ deadline + ", level=" + level + ", state=" + state
				+ ", description=" + description + "]";
	}

}
