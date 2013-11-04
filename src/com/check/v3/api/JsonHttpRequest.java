package com.check.v3.api;

import android.util.Log;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class JsonHttpRequest {
	private final static String TAG = JsonHttpRequest.class.getName();
	private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	private List<Header> mHeaders = null;
	private CookieStore mCookieStore = null;
	private HttpResponse mHttpResponse = null;
	private byte[] mResult = null;
	private String mError = null;
	private String mResultStr = null;

	public JsonHttpRequest() {
		mHeaders = new ArrayList<Header>();
		mResult = null;
		addHeader("Content-Type", "application/json");

	}

	public void addHeader(String key, String val) {
		mHeaders.add(new BasicHeader(key, val));
	}

	public void setCookie(String key, String val, String domain) {
		if (key == null || val == null || domain == null) {
			Log.e(TAG, "初始化Cookie出错");
			return;
		}
		mCookieStore = new BasicCookieStore();
		BasicClientCookie bc1 = new BasicClientCookie(key, val);
		bc1.setVersion(0);
		bc1.setDomain(domain);
		bc1.setPath("/");

		try {
			final Date ed = mSimpleDateFormat.parse("2050-04-23");
			bc1.setExpiryDate(ed);
			mCookieStore.addCookie(bc1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean get(String url) {
		HttpContext httpContext = new BasicHttpContext();
		HttpParams httpparam = new BasicHttpParams();
		HttpGet httpRequest = new HttpGet(url);

		this.initRequest(httpRequest, httpContext, httpparam);
		try {
			mHttpResponse = new DefaultHttpClient(httpparam).execute(
					httpRequest, httpContext);
			mResult = EntityUtils.toByteArray(mHttpResponse.getEntity());
			if (mResult != null)
				mResultStr = new String(mResult);
		} catch (ClientProtocolException e) {
			mError = "网络错误，请检查网络是否正常";
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			mError = "系统错误";
			e.printStackTrace();
			return false;
		}
		if (mHttpResponse.getStatusLine().getStatusCode() != 200) {
			mError = "服务器出现异常";
			Log.d(TAG, "服务器的返回码:"
					+ mHttpResponse.getStatusLine().getStatusCode());
			return false;
		}
		return true;
	}

	public boolean post(String url, String postBody) {
		HttpContext httpContext = new BasicHttpContext();
		HttpParams httpparam = new BasicHttpParams();
		HttpPost httpRequest = new HttpPost(url);
		boolean result = false;

		initRequest(httpRequest, httpContext, httpparam);
		try {

			if (postBody != null && postBody.length() > 0) {
				httpRequest.setEntity(new StringEntity(postBody, "UTF-8"));
				Log.d(TAG, "post_param length:" + postBody.length());
			}
			mHttpResponse = new DefaultHttpClient(httpparam).execute(
					httpRequest, httpContext);
			mResult = EntityUtils.toByteArray(mHttpResponse.getEntity());
			if (mResult != null)
				mResultStr = new String(mResult);
		} catch (Exception e) {
			e.printStackTrace();
			mError = "网络错误，请检查网络是否正常";
			return false;
		}
		
		int respStatusCode = mHttpResponse.getStatusLine().getStatusCode();
		if(respStatusCode == 200 || respStatusCode == 400){
			result = true;
		}else{
			mError = "服务器出现异常";
			Log.d(TAG, "服务器的返回码:"
					+ mHttpResponse.getStatusLine().getStatusCode());
			result = false;
		}
		return result;
	}

	public String getErrorMsg() {
		return mError;
	}

	public String getResult() {
		return mResultStr;
	}

	private void initRequest(HttpRequestBase httpRequest,
			HttpContext httpContext, HttpParams httpparam) {
		String useragent = "cloud check api v3";
		if (mCookieStore != null) {
			httpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
		}
		HttpProtocolParams.setUserAgent(httpparam, useragent);
		for (Header hh : mHeaders) {
			httpRequest.setHeader(hh);
		}
	}
}