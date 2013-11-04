package com.check.v3.api;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class FormRequest {
	private final String TAG = FormRequest.class.getName();
	private ViewForm mViewForm;

	public FormRequest(ViewForm viewForm) {
		this.mViewForm = viewForm;
	}

	protected void setJsonKVFromControl(JSONObject obj, String controlName) {
		if (obj != null && getViewForm() != null) {
			try {
				obj.put(controlName, getViewForm().getControlVal(controlName));
			} catch (JSONException e) {
				Log.e(TAG, "获取(设置)数据出错[" + controlName + "]");
			}
		}
	}

	protected ViewForm getViewForm() {
		return mViewForm;
	}

	public abstract JSONObject toJson();
}
