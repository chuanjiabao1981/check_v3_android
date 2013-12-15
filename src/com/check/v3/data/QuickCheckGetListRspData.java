package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class QuickCheckGetListRspData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6904311371073562055L;
	private int totalPages;
	private int currentPage;
	private int totalRecords;
	private ArrayList<QuickCheckListItemData> quickReports;
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getTotalRecords() {
		return totalRecords;
	}
	
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public ArrayList<QuickCheckListItemData> getQuickReports() {
		return quickReports;
	}
	
	public void setQuickReports(ArrayList<QuickCheckListItemData> quickReports) {
		this.quickReports = quickReports;
	}
	
	
}
