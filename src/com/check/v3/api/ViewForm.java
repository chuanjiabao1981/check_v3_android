package com.check.v3.api;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.check.v3.util.DialogUtil;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewForm {
	private Map<String, ViewControl> mControls = null; // 在html规范中中每个input都叫做control，沿用此名称
	private Activity mActivity = null;
	private final String TAG = ViewForm.class.getName();

	private Dialog mProgressStatusView = null;
	private View mFormView = null;

	public ViewForm(Activity activity) {
		mActivity = activity;
		mControls = new HashMap<String, ViewControl>();
		init();
	    this.mProgressStatusView = DialogUtil.createLoadingDialog(this.mActivity, "正在登录...", android.R.style.Theme_NoTitleBar);
		mFormView = this.mActivity.findViewById(getFormView());
	}
	
	public Activity getActivity(){
		return mActivity;
	}

	public String getControlVal(String name) {
		if (mControls.containsKey(name)) {
			return mControls.get(name).getValue();
		} else {
			Log.e(TAG, "不存在此control " + name);
			return "";
		}
	}

	public void setControlFieldError(String name, String errmsg) {
		if (mControls.containsKey(name)) {
			mControls.get(name).setError(errmsg);
		} else {
			Toast.makeText(this.mActivity, errmsg, Toast.LENGTH_SHORT).show();
			// Log.e(TAG, "不存在此control " + name);
			return;
		}
	}

	  public void showProgress(boolean show)
	  {
	    if (show)
	      this.mProgressStatusView.show();
	    else
	      this.mProgressStatusView.dismiss();
	  }

	protected abstract void init();

	protected abstract int getProgressStatusView();

	protected abstract int getFormView();

	protected int getControlViewId(String name) {
		return mControls.get(name).getResourceId();
	}

	protected View findViewById(int id) {
		return this.mActivity.findViewById(id);
	}

	protected void addControl(String name, int id) {
		mControls.put(name, new TextInputControl(this.mActivity, id));
		mControls.get(name).clearError();
	}

	protected void addSpinnerControl(String name, int id) {
		mControls.put(name, new SpinnerInputControl(this.mActivity, id));
		mControls.get(name).clearError();
	}
}