package com.check.v3;

import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.check.v3.data.IssueLevel;
import com.check.v3.data.Organization;
import com.check.v3.data.SimpleOrganization;
import com.check.v3.data.User;
import com.check.v3.preferences.DataPreference;
import com.check.v3.preferences.PrefConstant;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class CloudCheckApplication extends Application {

	/**
	 * Log or request TAG
	 */
	public static final String TAG = "CloudCheckApplication";

	public final static boolean DEBUG = Configuration.getDebug();

	public static Context mContext;
	// http client instance  
	private DefaultHttpClient mHttpClient;
	
	public static CloudCheckAsyncClient mAsyncHttpClientApi;
	
	private DataPreference mDataPreferences;
	
	private static String[] mIssueLevelIdList = { "", "HIGH", "MID", "LOW" };
	private static String[] mIssueLevelDisplayNameList = { "", "高", "中", "低" };
	private static ArrayList<IssueLevel> mIssueLevelList;
	private static ArrayList<Organization> mOrganizationList;
	private static HashMap<Integer, String> mId2NameOrgHashMap;
	private static HashMap<String, Integer> mName2IdOrgHashMap;
	private static HashMap<Integer, ArrayList<String>> mOrgId2UsersNameHashMap;
	private static HashMap<Integer, ArrayList<User>> mOrgId2UsersHashMap;
	
	private static int mSelectedOrgId = -1;

	private static void initIssueLevelList(){
		mIssueLevelList = new ArrayList<IssueLevel>();
		for(int i = 0; i < mIssueLevelDisplayNameList.length; i++){
			IssueLevel issueLevelItem = new IssueLevel(mIssueLevelIdList[i], mIssueLevelDisplayNameList[i]);
			mIssueLevelList.add(issueLevelItem);
		}
	}
	
	public static class AccountMngr{
		public static void initOrgList(ArrayList<Organization> orgList){
			mOrganizationList = orgList;
			mId2NameOrgHashMap = new HashMap<Integer, String>();
			mName2IdOrgHashMap = new HashMap<String, Integer>();
			mOrgId2UsersNameHashMap = new HashMap<Integer, ArrayList<String>>();
			mOrgId2UsersHashMap = new HashMap<Integer, ArrayList<User>>();
			
        	for(int i = 0; i < mOrganizationList.size(); i++){
        		Log.d("Test", "dep id = " + mOrganizationList.get(i).getId() + " , dep name = " + mOrganizationList.get(i).getName());
        		mId2NameOrgHashMap.put(mOrganizationList.get(i).getId(), mOrganizationList.get(i).getName());
        		mName2IdOrgHashMap.put(mOrganizationList.get(i).getName(), mOrganizationList.get(i).getId());
        		
        		ArrayList<String> userNameList = new ArrayList<String>();
        		ArrayList<User> orgUserList = mOrganizationList.get(i).getUsers();
        		
        		for(int j = 0; j < orgUserList.size(); j++){
        			userNameList.add(orgUserList.get(j).getName());
        		}
        		
        		mOrgId2UsersNameHashMap.put(mOrganizationList.get(i).getId(), userNameList);
        		mOrgId2UsersHashMap.put(mOrganizationList.get(i).getId(), orgUserList);
        	}
        	
        	initIssueLevelList();
		}
		
		public static ArrayList<Organization> getOrgList(){
			return mOrganizationList;
		}		
		
		public static ArrayList<String> getOrgNameList(){
			ArrayList<String> orgNameList = new ArrayList<String>();
			
			for(int i = 0; i < mOrganizationList.size(); i++){
				orgNameList.add(mOrganizationList.get(i).getName());
			}
			return orgNameList;
		}
		
		public static ArrayList<SimpleOrganization> getSimpleOrgList(){
			ArrayList<SimpleOrganization> orgIdNameList = new ArrayList<SimpleOrganization>();
			
			for(int i = 0; i < mOrganizationList.size(); i++){
				SimpleOrganization simpleOrg = new SimpleOrganization();
				simpleOrg.setOrgId(mOrganizationList.get(i).getId());
				simpleOrg.setOrgName(mOrganizationList.get(i).getName());
				orgIdNameList.add(simpleOrg);
			}
			return orgIdNameList;
		}
		
		public static int getOrgIdByName(String orgName){
			return mName2IdOrgHashMap.get(orgName);
		}
		
		public static String getOrgNameById(int orgId){
			return mId2NameOrgHashMap.get(orgId);
		}
		
		public static ArrayList<String> getPersonNameListByOrgId(int orgId){
			return mOrgId2UsersNameHashMap.get(orgId);
		}
		
		public static ArrayList<User> getPersonListByOrgId(int orgId){
			
			return mOrgId2UsersHashMap.get(orgId);
		}
		
		public static ArrayList<String> getPersonListByOrgName(String orgName){			
			return mOrgId2UsersNameHashMap.get(getOrgIdByName(orgName));
		}
		
		public static ArrayList<IssueLevel> getIssueLevelList(){
			return mIssueLevelList;
		}
		
		public static void setGlobalOrgId(int orgId){
			mSelectedOrgId = orgId;
		}
		
		public static int getGlobalOrgId(){
			return mSelectedOrgId;
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
	private static CloudCheckApplication sInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		sInstance = this;
		
		mContext = this.getApplicationContext();
		
		mDataPreferences = new DataPreference(this.getApplicationContext());
		
		mAsyncHttpClientApi = new CloudCheckAsyncClient(this.getApplicationContext());
	}

	/**
	 * @return ApplicationController singleton instance
	 */
	public static synchronized CloudCheckApplication getInstance() {
		return sInstance;
	}

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			
			
			BasicHttpParams httpParameters = new BasicHttpParams(); 

            HttpConnectionParams.setConnectionTimeout(httpParameters,  
                    60000); 
            HttpConnectionParams.setSoTimeout(httpParameters, 60000);  
            SchemeRegistry schreg = new SchemeRegistry();  
            schreg.register(new Scheme("http", PlainSocketFactory  
                    .getSocketFactory(), 80));  
            schreg.register(new Scheme("https", PlainSocketFactory  
                    .getSocketFactory(), 443));  
            ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(  
                    httpParameters, schreg);
		      		
			
			
			mHttpClient = new DefaultHttpClient();
			mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HttpClientStack(mHttpClient));
		}

		return mRequestQueue;
	}
	
	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue(boolean defaultReqQueue) {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
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
