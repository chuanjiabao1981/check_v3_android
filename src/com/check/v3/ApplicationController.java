package com.check.v3;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.check.v3.data.Organization;
import com.check.v3.data.User;
import com.check.v3.preferences.DataPreference;
import com.check.v3.preferences.PrefConstant;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

public class ApplicationController extends Application {

	/**
	 * Log or request TAG
	 */
	public static final String TAG = "ApplicationController";


	// http client instance  
	private DefaultHttpClient mHttpClient;
	
	private DataPreference mDataPreferences;
	
	private static ArrayList<Organization> mOrganizationList;
	private static HashMap<Integer, String> mId2NameOrgHashMap;
	private static HashMap<String, Integer> mName2IdOrgHashMap;
	private static HashMap<Integer, ArrayList<String>> mOrgId2UsersHashMap;

	
	public static class OrgMngr{
		public static void initOrgList(ArrayList<Organization> orgList){
			mOrganizationList = orgList;
			mId2NameOrgHashMap = new HashMap<Integer, String>();
			mName2IdOrgHashMap = new HashMap<String, Integer>();
			mOrgId2UsersHashMap = new HashMap<Integer, ArrayList<String>>();
			
        	for(int i = 0; i < mOrganizationList.size(); i++){
        		Log.d("Test", "dep id = " + mOrganizationList.get(i).getId() + " , dep name = " + mOrganizationList.get(i).getName());
        		mId2NameOrgHashMap.put(mOrganizationList.get(i).getId(), mOrganizationList.get(i).getName());
        		mName2IdOrgHashMap.put(mOrganizationList.get(i).getName(), mOrganizationList.get(i).getId());
        		
        		ArrayList<String> userList = new ArrayList<String>();
        		ArrayList<User> orgUserList = mOrganizationList.get(i).getUsers();
        		
        		for(int j = 0; j < orgUserList.size(); j++){
        			userList.add(orgUserList.get(j).getName());
        		}
        		
        		mOrgId2UsersHashMap.put(mOrganizationList.get(i).getId(), userList);
        		
        	}
		}
		
		public static ArrayList<String> getOrgList(){
			ArrayList<String> orgList = new ArrayList<String>();
			
			for(int i = 0; i < mOrganizationList.size(); i++){
				orgList.add(mOrganizationList.get(i).getName());
			}
			return orgList;
		}
		
		public static int getOrgIdByName(String orgName){
			return mName2IdOrgHashMap.get(orgName);
		}
		
		public static String getOrgNameById(int orgId){
			return mId2NameOrgHashMap.get(orgId);
		}
		
		public static ArrayList<String> getPersonListByOrgId(int orgId){
			return mOrgId2UsersHashMap.get(orgId);
		}
		
		public static ArrayList<String> getPersonListByOrgName(String orgName){			
			return mOrgId2UsersHashMap.get(getOrgIdByName(orgName));
		}
		
	}

	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 */
	private static ApplicationController sInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		sInstance = this;
		
		mDataPreferences = new DataPreference(this.getApplicationContext());
	}

	/**
	 * @return ApplicationController singleton instance
	 */
	public static synchronized ApplicationController getInstance() {
		return sInstance;
	}

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mHttpClient = new DefaultHttpClient();
			mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HttpClientStack(mHttpClient));
		}

		return mRequestQueue;
	}

	/** 
	 * Method to set a cookie 
	*/  
	public void setCookie() {
		CookieStore cs = mHttpClient.getCookieStore();
		
		// create a cookie
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", mDataPreferences.getString((PrefConstant.SESSION_ID)));
		cookie.setDomain("365check.net");
		cookie.setPath("/");
		
		cs.addCookie(cookie);
	}
	
	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

		VolleyLog.d("Adding request to queue: %s", req.getUrl());


		// add the cookie before adding the request to the queue  
		setCookie();  

		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);

		// add the cookie before adding the request to the queue  
		setCookie(); 
		
		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
