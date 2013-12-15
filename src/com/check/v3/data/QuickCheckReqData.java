package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class QuickCheckReqData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2245568132472563910L;

	
	private int organizationId;
	private int responsiblePersonId;
	private String level;
	private String deadline;
	private String description;
	private ArrayList<Integer> neededdeleteImagesId;

	public int getOrganizationId() {
		return organizationId;
	}
	
	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	
	public int getResponsiblePersonId() {
		return responsiblePersonId;
	}
	
	public void setResponsiblePersonId(int responsiblePersonId) {
		this.responsiblePersonId = responsiblePersonId;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getDeadline() {
		return deadline;
	}
	
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	
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
	
	@Override
	public String toString() {
		return "QuickCheckReqData [organizationId=" + organizationId
				+ ", responsiblePersonId=" + responsiblePersonId + ", level="
				+ level + ", deadline=" + deadline + ", description="
				+ description + "]";
	}

}
