package com.check.v3.login;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import com.check.v3.AsyncHttpExeptionHelper;
import com.check.v3.CloudCheckApplication;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.Organization;
import com.check.v3.data.OrganizationsByAccount;
import com.check.v3.CloudCheckApplication.AccountMngr;
import com.check.v3.data.Session;
import com.check.v3.mainui.MainActivity;
import com.check.v3.preferences.DataPreference;
import com.check.v3.preferences.PrefConstant;
import com.check.v3.util.CommonHelper;
import com.check.v3.util.DialogUtil;
import com.google.gson.Gson;

public class LoginTask{
	private final String TAG = LoginTask.class.getName();
	JSONObject mLoginJsonData;

	Gson gson;
	private DataPreference mDataPreferences;
	private Activity mActivity;
	
	private Dialog mProgressStatusView = null;

	public LoginTask(final Activity context, String name, String password)
	{
		this.mActivity = context;
	    
	    gson = new Gson();    
	    
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
	}

	public void doLogin() {
		mProgressStatusView.show();

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String rspStr = new String(response);
				Log.d(TAG, "response : " + rspStr.toString());
				
				Session userInfo = gson.fromJson(rspStr,
						Session.class);
				Log.i(TAG, "server response : " + userInfo.toString());

				mDataPreferences.saveData(PrefConstant.USER_NAME, userInfo.getAccount());
				mDataPreferences.saveData(PrefConstant.USER_ROLE, userInfo.getRole());
				mDataPreferences.saveData(PrefConstant.USER_ID, userInfo.getId());
				mDataPreferences.saveData(PrefConstant.USER_ACCOUNT, userInfo.getName());
				mDataPreferences.saveData(PrefConstant.SESSION_ID,
						userInfo.getJsession_id());
				
				AccountMngr.saveUserInfo(userInfo);
				
				doGetOrgInfo();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {
            	mProgressStatusView.dismiss();           
            	String errStr = new String(errorResponse);
            	
            	String errorStr = AsyncHttpExeptionHelper.getMessage(mActivity.getApplicationContext(), e, errorResponse, statusCode);
            	CommonHelper.notify(mActivity, errorStr);
            	
				Log.d(TAG, "error response of login : " + e.toString() + ", statuscode = " + statusCode + ", " + errStr);
            }
        };
        Log.d(TAG, mLoginJsonData.toString());
        CloudCheckApplication.mAsyncHttpClientApi.post("sessions/create", mLoginJsonData.toString(), responseHandler);
	}
	
	public void doGetOrgInfo() {
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String rspStr = new String(response);
                Log.d(TAG, "response : " + rspStr.toString());
                
                OrganizationsByAccount deptArrayList1 = gson.fromJson(rspStr, OrganizationsByAccount.class);                
                	ArrayList<Organization> deptArrayList = deptArrayList1.getOrganizations();
                	for(int i = 0; i < deptArrayList.size(); i++){
                		Log.d("Test", "dep id = " + deptArrayList.get(i).getId() + " , dep name = " + deptArrayList.get(i).getName());
                		for(int j = 0; j < deptArrayList.get(i).getUsers().size(); j++){
                			Log.d("Test", "user id = " + deptArrayList.get(i).getUsers().get(j).getId() + " , user name = " + deptArrayList.get(i).getUsers().get(j).getName());
                		}
                	}
                
                	AccountMngr.initOrgList(deptArrayList);
              	
                	Intent intent = new Intent(mActivity, MainActivity.class);
					mActivity.startActivity(intent);					      
					
                	mProgressStatusView.dismiss();
                	mActivity.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {
            	mProgressStatusView.dismiss();
            	String errStr = new String(errorResponse);
            	
            	String errorStr = AsyncHttpExeptionHelper.getMessage(mActivity, e, errorResponse, statusCode);
            	CommonHelper.notify(mActivity, errorStr);
				Log.d(TAG, "error response of get org info: " + e.toString() + ", statuscode = " + statusCode + ", " + errStr);				
            }
        };
        CloudCheckApplication.mAsyncHttpClientApi.get("organizations", null, responseHandler);		
	}	

}