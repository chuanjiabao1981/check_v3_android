package com.check.v3.api;

import android.app.Activity;
import android.text.Editable;
import android.widget.EditText;

public class TextInputControl extends ViewControl {
	public EditText mEditText;

	public TextInputControl(Activity activity, Integer resourceId) {
		super(activity, resourceId);
		mEditText = (EditText) this.mActivity
				.findViewById(this.getResourceId());
	}

	public String getValue() {
		return mEditText.getText().toString();
	}

	@Override
	public void setError(String erromsg) {
		mEditText.setError(erromsg);
		mEditText.requestFocus();
	}

	@Override
	public void clearError() {
		mEditText.setError(null);
	}

}