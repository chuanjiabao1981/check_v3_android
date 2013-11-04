package com.check.v3.login;

import android.app.Activity;

import com.check.client.R;
import com.check.v3.api.ViewForm;

public class LoginForm extends ViewForm
{
  public LoginForm(Activity activity)
  {
    super(activity);
  }

  protected int getFormView()
  {
    return R.id.check_login_container;
  }

  public String getUserName()
  {
    return getControlVal(LoginApiConstant.USER_NAME);
  }

  public String getPassword()
  {
    return getControlVal(LoginApiConstant.PASSWORD);
  }

  protected int getProgressStatusView()
  {
    return 0;
  }

  protected void init()
  {
    addControl(LoginApiConstant.USER_NAME, R.id.login_user_name);
    addControl(LoginApiConstant.PASSWORD, R.id.login_passwd);
  }
}