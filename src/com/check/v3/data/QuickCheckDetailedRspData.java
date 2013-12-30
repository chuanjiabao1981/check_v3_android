package com.check.v3.data;

import java.io.Serializable;
import java.util.ArrayList;

public class QuickCheckDetailedRspData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -309180936548984287L;

	private QuickCheckListItemData quickReport;
	private ArrayList<ReportResolutionRspData> quickReportResolves;

	public QuickCheckListItemData getQuickReport() {
		return quickReport;
	}

	public void setQuickReport(QuickCheckListItemData quickReport) {
		this.quickReport = quickReport;
	}
	
	public ArrayList<ReportResolutionRspData> getQuickReportResolves() {
		return quickReportResolves;
	}

	public void setQuickReportResolves(ArrayList<ReportResolutionRspData> quickReportResolves) {
		this.quickReportResolves = quickReportResolves;
	}

}
