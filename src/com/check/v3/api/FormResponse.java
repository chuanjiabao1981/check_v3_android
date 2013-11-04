package com.check.v3.api;

import android.util.Log;

public abstract class FormResponse extends Response {
	private final String TAG = FormResponse.class.getName();
	private ViewForm mViewForm = null;

	public FormResponse(ViewForm viewForm, String rStr) {
		super(rStr);
		this.mViewForm = viewForm;
	}

	private ViewForm getViewForm() {
		return this.mViewForm;
	}

	protected void dealError(String key, String val) {
		if (getViewForm() != null) {
			getViewForm().setControlFieldError(key, val);
		} else {
			Log.e(this.TAG, "not view form is given");
		}
	}
}
