package com.check.v3.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataPreference
{
  private static final String PREFS_NAME = "check_v3_login_info_pref";
  private Context mCon;

  public DataPreference(Context context)
  {
    this.mCon = context;
  }

  public boolean getBoolean(String key)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(key, true);
  }

  public boolean getBoolean(String key, boolean defVal)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(key, defVal);
  }

  public double getDouble(String key)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getFloat(key, 0.0F);
  }

  public double getDouble(String key, double defVal)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getFloat(key, (float)defVal);
  }

  public int getInt(String key)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(key, 0);
  }

  public int getInt(String key, int defVal)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(key, defVal);
  }

  public String getString(String key)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(key, "");
  }

  public String getString(String key, String defVal)
  {
    return this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(key, defVal);
  }

  public boolean saveData(String key, float val)
  {
    try
    {
      SharedPreferences.Editor editor = this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
      editor.putFloat(key, val);
      editor.commit();
      return true;
    }
    catch (Exception localException)
    {
    	return false;
    }
  }

  public boolean saveData(String key, int val)
  {
    try
    {
      SharedPreferences.Editor editor = this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
      editor.putInt(key, val);
      editor.commit();
      return true;
    }
    catch (Exception localException)
    {
    	return false;
    }
  }
  
  public boolean saveData(String key, String val)
  {
    try
    {
      SharedPreferences.Editor editor = this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
      editor.putString(key, val);
      editor.commit();
      return true;
    }
    catch (Exception localException)
    {
    	return false;
    }
  }

  public boolean saveData(String key, boolean val)
  {
    try
    {
      SharedPreferences.Editor editor = this.mCon.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
      editor.putBoolean(key, val);
      editor.commit();
      return true;
    }
    catch (Exception localException)
    {
    	return false;
    }
  }
}