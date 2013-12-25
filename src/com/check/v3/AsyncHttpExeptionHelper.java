package com.check.v3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;

import com.check.client.R;
import com.check.v3.data.ResponseData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;

public class AsyncHttpExeptionHelper {

	private static String TAG = "AsyncHttpExeptionHelper";

	/** OK: Success! */
	public static final int OK = 200;
	/** Not Modified: There was no new data to return. */
	public static final int NOT_MODIFIED = 304;
	/**
	 * Bad Request: The request was invalid. An accompanying error message will
	 * explain why. This is the status code will be returned during rate
	 * limiting.
	 */
	public static final int BAD_REQUEST = 400;
	/** Not Authorized: Authentication incorrect. */
	public static final int NOT_AUTHORIZED = 401;
	/**
	 * Forbidden: The request is understood, but it has been refused. An
	 * accompanying error message will explain why.
	 */
	public static final int FORBIDDEN = 403;
	/**
	 * Not Found: The URI requested is invalid or the resource requested, such
	 * as a user, does not exists.
	 */
	public static final int NOT_FOUND = 404;
	/**
	 * Not Acceptable
	 */
	public static final int NOT_ACCEPTABLE = 406;
	/**
	 * Internal Server Error: Something is broken.
	 */
	public static final int INTERNAL_SERVER_ERROR = 500;
	/** Bad Gateway: Server is down or being upgraded. */
	public static final int BAD_GATEWAY = 502;
	/**
	 * Service Unavailable: The servers are up, but overloaded with
	 * requests. Try again later.
	 */
	public static final int SERVICE_UNAVAILABLE = 503;
	
	
	/**
	 * parse HTTP status code
	 * 
	 * @param statusCode
	 * @return
	 */
	private static String getCause(int statusCode) {
		String cause = null;
		switch (statusCode) {
		case NOT_MODIFIED:
			break;
		case BAD_REQUEST:
			cause = "The request was invalid.  An accompanying error message will explain why.";
			break;
		case NOT_AUTHORIZED:
			cause = "Authentication incorrect, the session is expired.";
			break;
		case FORBIDDEN:
			cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
			break;
		case NOT_FOUND:
			cause = "The URI requested is invalid or the resource requested does not exists.";
			break;
		case NOT_ACCEPTABLE:
			cause = "Returned an invalid format is specified in the request.";
			break;
		case INTERNAL_SERVER_ERROR:
			cause = "Something is broken.  Please contact the service ISP.";
			break;
		case BAD_GATEWAY:
			cause = "The server is down or being upgraded.";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable: The servers are up, but overloaded with requests. Try again later.";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}
	
	private static String handleResponseStatusCode(int statusCode, byte[] resBody){
		String msg = getCause(statusCode) + "\n";
		String resBodyStr = "";
		if(resBody != null){
			resBodyStr = new String(resBody);
		}
		
		switch (statusCode) {
		// It's OK, do nothing
		case OK:
			break;
		// The errors info from server
		case BAD_REQUEST:
			msg = resBodyStr;
			break;
		// Mine mistake, Check the Log
		case NOT_MODIFIED:
		case NOT_FOUND:
		case NOT_ACCEPTABLE:
			msg = msg + resBodyStr;
			break;
			// UserName/Password incorrect
		case NOT_AUTHORIZED:
//
			msg = "认证失败, 会话超时, 请重新登录.";
			break;
			// Server will return a error message
		case FORBIDDEN:
			break;
			// Something wrong with server
		case INTERNAL_SERVER_ERROR:
		case BAD_GATEWAY:
		case SERVICE_UNAVAILABLE:
			msg = msg + resBodyStr;
			break;
			// Others
		default:
			msg = msg + resBodyStr;
			break;
		}
		return msg;
	}
	
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
			return handleResponseStatusCode(statusCode, responseBody);
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
