package com.check.v3.api;

import android.os.AsyncTask;
import android.util.Log;

public abstract class PostTask extends AsyncTask<String, Integer, Boolean> {
	public static final String TAG = PostTask.class.getCanonicalName();
	private JsonHttpRequest mJsonHttpRequest = null;

	public PostTask() {
		mJsonHttpRequest = new JsonHttpRequest();
	}

	protected abstract String getPostParams();

	protected abstract String getApiUrl();

	public abstract void go();

	@Override
	protected Boolean doInBackground(String... params) {
		return mJsonHttpRequest.post(getApiUrl(), getPostParams());
	}

	@Override
	protected void onCancelled() {
		// showProgress(false);
	}

	public String getResult() {
		return mJsonHttpRequest.getResult();
	}

	public String getErrorMsg() {
		return mJsonHttpRequest.getErrorMsg();
	}

	public void setCookie(String key, String val, String domain) {
		mJsonHttpRequest.setCookie(key, val, domain);
	}
}
