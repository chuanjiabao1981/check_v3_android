package com.check.v3.api;

import android.app.Activity;

public abstract class ViewControl {
	protected Activity mActivity;
	private int mResourceId;

	public ViewControl(Activity activity, Integer resourceId) {
		this.mActivity = activity;
		this.mResourceId = resourceId;
	}

	public int getResourceId() {
		return mResourceId;
	}

	public abstract String getValue();

	public abstract void setError(String errMsg);

	public abstract void clearError();

}