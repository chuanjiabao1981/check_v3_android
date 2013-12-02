package com.check.v3.login;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.check.v3.ApplicationController;
import com.check.v3.VolleyErrorHelper;
import com.check.v3.data.Organization;
import com.check.v3.data.OrganizationsByAccount;
import com.check.v3.ApplicationController.OrgMngr;
import com.check.v3.data.ResponseData;
import com.check.v3.data.Session;
import com.check.v3.mainui.MainActivity;
import com.check.v3.preferences.DataPreference;
import com.check.v3.preferences.PrefConstant;
import com.check.v3.util.DialogUtil;
import com.google.gson.Gson;

public class LoginTask{
	private final String TAG = LoginTask.class.getName();
	JSONObject mLoginJsonData;
	private RequestQueue mQueue;

	Gson gson;
	private DataPreference mDataPreferences;
	private Activity mActivity;
	private JsonObjectRequest mLoginJasonObjReq, mGetOrgInfoJasonObjReq;
	
	private Dialog mProgressStatusView = null;

	private final String API3 = "/v3/api/v1/sessions/create";
	

	public LoginTask(final Activity context, String name, String password)
	{
		this.mActivity = context;
	    
	    gson = new Gson();
	    
	    mQueue = ApplicationController.getInstance().getRequestQueue();
	    mDataPreferences = new DataPreference(context.getApplicationContext());
	    
	    mProgressStatusView = DialogUtil.createLoadingDialog(mActivity, "正在登录...", android.R.style.Theme_NoTitleBar);
	    
	    mLoginJsonData = new JSONObject();
	    try {
	    	mLoginJsonData.put(LoginApiConstant.USER_NAME, name);
	    	mLoginJsonData.put(LoginApiConstant.PASSWORD, password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    mLoginJasonObjReq = new JsonObjectRequest(
				Method.POST,
				"http://www.365check.net:8088/check-service/api/v1/sessions/create",
				mLoginJsonData, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "response : " + response.toString());
						Session sessionId = gson.fromJson(response.toString(),
								Session.class);
						Log.i(TAG, "server response : " + sessionId.toString());

						mDataPreferences.saveData(PrefConstant.SESSION_ID,
								sessionId.getJsession_id());
						
						doGetOrgInfo();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError errInfo) {
						String errorStr = VolleyErrorHelper.getMessage(
								mActivity.getApplicationContext(), errInfo);
						Log.d(TAG, "error response : " + errorStr);
						ResponseData rspData = gson.fromJson(errorStr,
								ResponseData.class);
						Log.i(TAG, "error response : " + rspData.toString());
						
						mProgressStatusView.dismiss();
					}
				});
	    
		mGetOrgInfoJasonObjReq = new JsonObjectRequest(Method.GET, "http://www.365check.net:8088/check-service/api/v1/organizations",null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response : " + response.toString());
                
                OrganizationsByAccount deptArrayList1 = gson.fromJson(response.toString(), OrganizationsByAccount.class);                
                	ArrayList<Organization> deptArrayList = deptArrayList1.getOrganizations();
                	for(int i = 0; i < deptArrayList.size(); i++){
                		Log.d("Test", "dep id = " + deptArrayList.get(i).getId() + " , dep name = " + deptArrayList.get(i).getName());
                		for(int j = 0; j < deptArrayList.get(i).getUsers().size(); j++){
                			Log.d("Test", "user id = " + deptArrayList.get(i).getUsers().get(j).getId() + " , user name = " + deptArrayList.get(i).getUsers().get(j).getName());
                		}
                	}
                
                	OrgMngr.initOrgList(deptArrayList);
                	
//                	ArrayList<String> mOrgList = OrgMngr.getOrgList();
//                	ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mOrgList);
//                	
//                	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);                	
//                	
//                	ArrayList<String> mPersonList = OrgMngr.getPersonByOrgList(373);
//                	ArrayAdapter<String> adapterPerson = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mPersonList);
//                	
//                	adapterPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                	
                	Intent intent = new Intent(mActivity, MainActivity.class);
					mActivity.startActivity(intent);					      
					
                	mProgressStatusView.dismiss();
                	mActivity.finish();
        }
        }, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError errInfo) {
				String errorStr = VolleyErrorHelper.getMessage(mActivity.getApplicationContext(), errInfo);
				Log.d(TAG, "error response : " + errorStr);
////				ResponseData rspData = gson.fromJson(errorStr, ResponseData.class);
////				Log.i(TAG, "error response : " + rspData.toString());
				mProgressStatusView.dismiss();
			}
		});
	}
	

	
	public void doLogin() {
		mProgressStatusView.show();
		ApplicationController.getInstance().addToRequestQueue(mLoginJasonObjReq);
	}
	
	public void doGetOrgInfo() {
		ApplicationController.getInstance().addToRequestQueue(mGetOrgInfoJasonObjReq);
	}

}