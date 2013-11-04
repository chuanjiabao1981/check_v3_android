package com.check.v3.api;

import android.app.Activity;
import android.widget.Spinner;
import android.widget.Toast;

public class SpinnerInputControl extends ViewControl {

	public SpinnerInputControl(Activity activity, Integer resourceId) {
		super(activity, resourceId);
	}

	@Override
	public String getValue() {
		return ((Spinner) mActivity.findViewById(this.getResourceId()))
				.getSelectedItem().toString();
	}

	@Override
	public void setError(String errMsg) {
		Toast.makeText(this.mActivity, errMsg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void clearError() {
	}

}