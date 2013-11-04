package com.check.v3.session;

import android.util.Log;
import com.check.v3.Session;
import com.check.v3.api.FormResponse;
import com.check.v3.api.ViewForm;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionResponse extends FormResponse
  implements Session
{
  private final String TAG;
  private String mSessionId;

  public SessionResponse(ViewForm paramViewForm, String paramString)
  {
    super(paramViewForm, paramString);
    String str = SessionResponse.class.getName();
    this.TAG = str;
  }

  public String getSessionId()
  {
    return this.mSessionId;
  }

  public void parser()
  {
    try
    {
      if (((JSONObject)getJsonResult()).has("sessionId"))
      {
        String str = ((JSONObject)getJsonResult()).getString("sessionId");
        this.mSessionId = str;
      }
      return;
    }
    catch (JSONException localJSONException)
    {
      int i = Log.d(this.TAG, "结果出错!");
    }
  }
}