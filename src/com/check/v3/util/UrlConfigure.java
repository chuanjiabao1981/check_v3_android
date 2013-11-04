package com.check.v3.util;

import android.app.Activity;
import android.content.Context;
import com.check.v3.preferences.DataPreference;

public class UrlConfigure
{
  private final String TAG = UrlConfigure.class.getName();
  private final String URL_PREFER_ITEM_URL_DEFAULT = "42.121.55.211";
  private Activity mActivity;
  private DataPreference mDataPreference;

  public UrlConfigure(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    Context localContext = this.mActivity.getApplicationContext();
    DataPreference localDataPreference = new DataPreference(localContext);
    this.mDataPreference = localDataPreference;
  }

  public String getServerHost()
  {
    String str = String.valueOf(this.mDataPreference.getString("SERVER_HOST", URL_PREFER_ITEM_URL_DEFAULT));
    return str + ":8088";
  }
}