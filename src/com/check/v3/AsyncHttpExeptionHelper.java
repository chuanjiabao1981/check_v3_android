package com.check.v3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.check.client.R;
import com.check.v3.data.ResponseData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;

public class AsyncHttpExeptionHelper {

	private static String TAG = "AsyncHttpExeptionHelper";

	/**
	 * Returns appropriate message which is to be displayed to the user against
	 * the specified error object.
	 * 
	 * @param error
	 * @param context
	 * @return
	 */
	public static String getMessage(Context context, Object error,
			byte[] responseBody, int statusCode) {
		if (error instanceof ConnectTimeoutException) {
			return context.getResources().getString(
					R.string.connection_timeout_error);
		} else if (isHttpResponseProblem(error)) {
			return handleServerError(statusCode, responseBody, error, context);
		} else if (isNetworkProblem(error)) {
			return context.getResources().getString(R.string.no_internet);
		}

		return context.getResources().getString(R.string.generic_error);
	}

	/**
	 * Determines whether the error is related to network
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isNetworkProblem(Object error) {
		return (error instanceof IOException);
	}

	/**
	 * Determines whether the error is related to server
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isHttpResponseProblem(Object error) {
		return (error instanceof HttpResponseException);
	}

	/**
	 * Handles the server error, tries to determine whether to show a stock
	 * message or to show a message retrieved from the server.
	 * 
	 * @param err
	 * @param context
	 * @return
	 */
	private static String handleServerError(int statusCode, byte[] body,
			Object err, Context context) {
		IOException error = (IOException) err;

		switch (statusCode) {
		case 400:
			try {
				String rspStr = new String(body);
				Log.i(TAG, "error response : " + rspStr.toString());

				ResponseData rspData = new Gson().fromJson(rspStr,
						ResponseData.class);

				return rspStr;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 401:
			Log.i(TAG, "error response, statusCode = " + statusCode);
		case 404:
		case 422:
			try {
				// server might return error like this { "error":
				// "Some error occured" }
				// Use "Gson" to parse the result
				String rspStr = new String(body);
				HashMap<String, String> result = new Gson().fromJson(
						new String(body), new TypeToken<Map<String, String>>() {
						}.getType());

				if (result != null && result.containsKey("error")) {
					return result.get("error");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// invalid request
			return error.getMessage();
		default:
			break;
		}
		
		return context.getResources().getString(R.string.generic_server_down);
	}
}
