package com.check.v3.login;

import android.content.Intent;
import android.util.Log;

import com.check.v3.api.PostTask;
import com.check.v3.business.MainActivity;
import com.check.v3.preferences.DataPreference;
import com.check.v3.preferences.PrefConstant;
import com.check.v3.session.SessionResponse;
import com.check.v3.util.UrlConfigure;

public class LoginTask extends PostTask{

	private final String API3 = "/v3/api/v1/sessions/create";
	private final String TAG = LoginTask.class.getName();
	private DataPreference mDataPreference;
	private LoginForm mLoginForm;
	private LoginRequest mLoginRequest;
	private UrlConfigure mUrlConfigure;

	public LoginTask(LoginForm loginForm)
	{
		this.mLoginForm 			= loginForm;
		this.mLoginRequest			= new LoginRequest(mLoginForm);
		this.mUrlConfigure 			= new UrlConfigure(this.mLoginForm.getActivity());
	    this.mDataPreference = new DataPreference(this.mLoginForm.getActivity().getApplicationContext());
	}
	
	public void go()
	{
		this.mLoginForm.showProgress(true);
		execute();
	}
	
	protected void onPostExecute(final Boolean succ) 
	{
		SessionResponse sessionResponse = new SessionResponse(mLoginForm,this.getResult());
		if (!succ){
			sessionResponse.setSysErrorMessage(this.getErrorMsg());
		}
		if (!sessionResponse.hasError()){
		      Intent intent = new Intent(this.mLoginForm.getActivity(), MainActivity.class);
		      this.mLoginForm.getActivity().startActivity(intent);
		      String sessionIdStr = sessionResponse.getSessionId();
		      mDataPreference.saveData(PrefConstant.SESSION_ID, sessionIdStr);
		      this.mLoginForm.showProgress(false);
		      this.mLoginForm.getActivity().finish();
		}else{
			this.mLoginForm.showProgress(false);
			return;
		}
	}
	
	protected String getApiUrl()
	{
		return "http://" + this.mUrlConfigure.getServerHost() + API3;
	}
	
	@Override
	protected String getPostParams() {
		if ( mLoginRequest != null ) {
			String postBodyStr = mLoginRequest.toJson().toString();
			Log.d(TAG, postBodyStr);
			return postBodyStr;
		}		
		return "";
	}



}