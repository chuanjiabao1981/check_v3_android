package com.check.v3.login;

import com.check.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class WelcomeActivity extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.splash_screen);
    
    new Handler().postDelayed(new Runnable(){
    	 
        @Override
        public void run() {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            WelcomeActivity.this.startActivity(intent);
            WelcomeActivity.this.finish();
        }
          
       }, 2000);
  }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}