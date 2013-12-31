package com.check.v3.ui.main;

import java.lang.reflect.Field;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewConfiguration;

public class SubActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		
		// enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.sub_fragment_menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item)
	    {
	        // TODO Auto-generated method stub
	        if(item.getItemId() == android.R.id.home)
	        {
	            finish();
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }

}
