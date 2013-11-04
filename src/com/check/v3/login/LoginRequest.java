package com.check.v3.login;

import android.util.Log;
import com.check.v3.api.FormRequest;
import com.check.v3.api.ViewForm;

import org.json.JSONObject;

public class LoginRequest extends FormRequest{
    private final String TAG = LoginRequest.class.getName();

    public LoginRequest(ViewForm viewForm) {
            super(viewForm);
    }

    @Override
    public JSONObject toJson() {
		JSONObject _json_data = new JSONObject();
		try {	        
	        setJsonKVFromControl(_json_data, LoginApiConstant.USER_NAME);
	        setJsonKVFromControl(_json_data, LoginApiConstant.PASSWORD);
		} catch (Exception e) {
		    Log.e(TAG, "获取(设置)数据出错");
		}
		return _json_data;
    }

}