package com.check.v3.api;

import java.util.Iterator;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class Response {
	private final String TAG = FormResponse.class.getName();

	enum RESULT_JSON_TYPE {
		JSONObject, JSONArray
	};

	private String mRStr;
	private Object mRjson = null;
	private RESULT_JSON_TYPE mRJsonType = null;
	private String mSysErrorMessage = null;

	public abstract void parser();

	protected abstract void dealError(String key, String val);

	public Response(String r) {
		this.mRStr = r;
		init();
	}

	public boolean hasError() {
		if (hasSysError()) {
			dealAllError();
			return true;
		}
		if (hasAppError()) {
			dealAllError();
			return true;
		}
		parser();
		return false;
	}

	private void dealAllError() {
		if (hasSysError()) {
			dealError(ApiConstant.BASE, getSysErrorMesssage());
			return;
		}
		if (hasAppError()) {
			try {
				JSONArray errJsonArray = ((JSONObject)getJsonResult()).getJSONArray(ApiConstant.ERROR);
				int errArrayLen = errJsonArray.length();
				
				for(int i = 0; i < errArrayLen; i++){
			        JSONObject errItem = errJsonArray.getJSONObject(i);
			        String fieldStr = errItem.getString(ApiConstant.FIELD);
			        String messageStr = errItem.getString(ApiConstant.MESSAGE);
			        dealError(fieldStr, messageStr);
			        Log.d(TAG, "error info:" + fieldStr + ":" + messageStr);
				}
			} catch (JSONException e) {
				Log.e(TAG, "解析应用层数据出错(JSON)!");
			} catch (Exception e) {
				Log.e(TAG, "解析应用层数据出错!");
			}
		}
	}

	private void init() {
		if (mRStr == null)
			return;
		JSONTokener jsParser = new JSONTokener(mRStr);
		try {
			if (mRStr.startsWith("[")) {
				mRJsonType = RESULT_JSON_TYPE.JSONArray;
			} else if (mRStr.startsWith("{")) {
				mRJsonType = RESULT_JSON_TYPE.JSONObject;
			}
			mRjson = jsParser.nextValue();

		} catch (JSONException e) {
			setSysErrorMessage(SysErrorMessage.ERROR_API_DATA_ERROR);
		} catch (Exception e) {
			setSysErrorMessage(SysErrorMessage.ERROR_NET_WORK);
		}
	}

	private RESULT_JSON_TYPE getResponseJsonType() {
		return mRJsonType;
	}

	public Object getJsonResult() {
		return mRjson;
	}

	public void setSysErrorMessage(String m) {
		this.mSysErrorMessage = m;
	}

	public String getSysErrorMesssage() {
		return this.mSysErrorMessage;
	}

	protected boolean hasSysError() {
		if (mSysErrorMessage != null) {
			return true;
		}
		return false;
	}

	protected boolean hasAppError() {
		if (getResponseJsonType() == RESULT_JSON_TYPE.JSONObject
				&& ((JSONObject) getJsonResult()).has(ApiConstant.ERROR)) {
			Log.d(TAG, "has app error!");
			return true;
		}
		return false;
	}
}