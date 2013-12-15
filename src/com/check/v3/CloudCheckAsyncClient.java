package com.check.v3;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;

import android.content.Context;
import android.util.Log;

import com.check.v3.asynchttp.AsyncHttpClient;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.asynchttp.RequestParams;
import com.check.v3.asynchttp.PersistentCookieStore;
import com.check.v3.data.QuickCheckReqFilePartData;
import com.check.v3.data.Session;
import com.check.v3.preferences.PrefConstant;

public class CloudCheckAsyncClient {
	private static final String BASE_URL = "http://www.365check.net:8088/check-service/api/v1/";

	private AsyncHttpClient client;
	private Context mContext;
	
	public CloudCheckAsyncClient(Context context) {
		super();
		mContext = context;
		client = new AsyncHttpClient();
		CookieStore pCookieStore = new PersistentCookieStore(mContext);
        client.setCookieStore(pCookieStore);
	}

	public void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void post(String url, String params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), null, params, null, responseHandler);
	}
	
	public void post(String url, String jsonStrBody, ArrayList<QuickCheckReqFilePartData> fileList,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), null, jsonStrBody, null, fileList, responseHandler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
