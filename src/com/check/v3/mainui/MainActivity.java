/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.check.v3.mainui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;
import com.check.client.R;
import com.check.v3.CloudCheckApplication.AccountMngr;
import com.check.v3.data.Session;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainActivity extends SherlockFragmentActivity {
	
	private static String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mDrawerHeaderView;
    //private LinearLayout mDrawerView;
    private SherlockActionBarDrawerToggle mDrawerToggle;
    
    private Spinner mDrawerOrgSpinner;
    private ArrayList<String> mOrgList;
    private ArrayAdapter<String> orgAdapter;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private TextView mDrawerDisplayNameTextView;
    
    private static String DRAWER_MENU_SELECTED_POSITION = "menu_selected_position"; 
    
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";
	
	private static String DRAWER_HEADER_VIEW = "drawer_header_item";
	private static String QUICK_REPORT_TITLE = "quickReportGroupTitle";
	private static String LIST_QUICK_REPORT_ITEM = "listQuickreport";
	private static String COMPOSE_QUICK_REPORT_ITEM = "composeQuickReport";
	private static String TEMPLATE_REPORT_TITLE = "templateReportGroupTitle";
	private static String LIST_TEMPLATE_REPORT_ITEM = "listTemplatereport";
	private static String COMPOSE_TEMPLATE_REPORT_ITEM = "composeTemplateReport";
	private static String MISC_TITLE = "miscTitle";
	private static String SETTINGS_ITEM = "settings";
	private static String LOGOUT_ITEM = "logoutAccount";
	
	
    private String[] drawerListItemNames = new String[] { DRAWER_HEADER_VIEW, QUICK_REPORT_TITLE, LIST_QUICK_REPORT_ITEM, COMPOSE_QUICK_REPORT_ITEM, 
    		TEMPLATE_REPORT_TITLE, LIST_TEMPLATE_REPORT_ITEM, COMPOSE_TEMPLATE_REPORT_ITEM,
    		MISC_TITLE, SETTINGS_ITEM, LOGOUT_ITEM};
    
    private String[] drawerListItemStrNames = new String[] { "header_view", 
    		"走动巡查", "走动巡查列表", "新建走动巡查", 
    		"例行巡查", "例行巡查列表", "新建例行巡查",
    		"其他", "设置", "退出"};
    
    private HashMap<String, String> mDrawerItemsHashMap;
    
    public void initDrawerItemsHashMap(){
    	mDrawerItemsHashMap = new HashMap<String, String>();
    	for(int i = 0; i < drawerListItemNames.length; i++){
    		mDrawerItemsHashMap.put(drawerListItemNames[i], drawerListItemStrNames[i]);
    	}
    }

	public Map<String, ?> createItem(String title, String caption) {
		Map<String, String> item = new HashMap<String, String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener //R.layout.drawer_list_item
        // android.R.layout.simple_list_item_1
        

        initDrawerItemsHashMap();
		SeparatedListAdapter separatedAdapter = new SeparatedListAdapter(this);
		separatedAdapter.addSection(mDrawerItemsHashMap.get(QUICK_REPORT_TITLE), new ArrayAdapter<String>(this,
				R.layout.list_item, new String[] { mDrawerItemsHashMap.get(LIST_QUICK_REPORT_ITEM), mDrawerItemsHashMap.get(COMPOSE_QUICK_REPORT_ITEM) }));
		
		separatedAdapter.addSection(mDrawerItemsHashMap.get(TEMPLATE_REPORT_TITLE), new ArrayAdapter<String>(this,
				R.layout.list_item, new String[] { mDrawerItemsHashMap.get(LIST_TEMPLATE_REPORT_ITEM), mDrawerItemsHashMap.get(COMPOSE_TEMPLATE_REPORT_ITEM) }));
		
		separatedAdapter.addSection(mDrawerItemsHashMap.get(MISC_TITLE), new ArrayAdapter<String>(this,
				R.layout.list_item, new String[] { mDrawerItemsHashMap.get(SETTINGS_ITEM), mDrawerItemsHashMap.get(LOGOUT_ITEM) }));
		
		mDrawerHeaderView = (LinearLayout) LayoutInflater.from(this).inflate( 
		        R.layout.drawer_list_view_header, null);
		
		mDrawerDisplayNameTextView = (TextView) mDrawerHeaderView.findViewById(R.id.user_name);
		Session userInfo = AccountMngr.getUserInfo();
		if(userInfo != null){
			mDrawerDisplayNameTextView.setText(userInfo.getName());
		}
		
		mDrawerList.addHeaderView(mDrawerHeaderView, null, false);

		mDrawerOrgSpinner = (Spinner) mDrawerHeaderView
				.findViewById(R.id.orig_spinner);
		
		mOrgList = AccountMngr.getOrgNameList();
    	orgAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mOrgList);
    	
		
		orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDrawerOrgSpinner.setAdapter(orgAdapter);
		mDrawerOrgSpinner.setOnItemSelectedListener(mDrawerOrgSpinnSelectedListener);
		mDrawerOrgSpinner.setSelection(0);
		
		int selectedOrgId = AccountMngr.getOrgIdByName(mOrgList.get(0));
		 AccountMngr.setGlobalOrgId(selectedOrgId);
		
		mDrawerList.setAdapter(separatedAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new SherlockActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(2);
        }
    }
    
	private OnItemSelectedListener mDrawerOrgSpinnSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			 
			 String selectedOrgName = mOrgList.get(pos);
			 int selectedOrgId = AccountMngr.getOrgIdByName(selectedOrgName);
			 AccountMngr.setGlobalOrgId(selectedOrgId);
			 
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_setting:
                Toast.makeText(this, "Menu item " + item.getTitle() + " is selected.", Toast.LENGTH_LONG).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
    	SherlockListFragment listFragment;
    	SherlockFragment fragment;
    	
    	Log.d(TAG, "drawer item is clicked " + position);
    	
        Bundle args = new Bundle();
        args.putInt(DRAWER_MENU_SELECTED_POSITION, position);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
    	
    	if(drawerListItemNames[position].equals(LIST_QUICK_REPORT_ITEM)){
    		fragment = new QuickReportListFragment();
    		transaction.replace(R.id.content_frame, fragment);
//    		fragment = new RandomCheckComposeFragment();
//    		fragment.setArguments(args);
//    		transaction.replace(R.id.content_frame, fragment);
    		
    	}else if(drawerListItemNames[position].equals(COMPOSE_QUICK_REPORT_ITEM)){
//    		fragment = new RandomCheckComposeFragment();
//    		fragment.setArguments(args);
//    		transaction.replace(R.id.content_frame, fragment);
    		Intent localIntent = new Intent(this, QuickCheckComposeActivity.class);
			startActivity(localIntent);
			
			mDrawerList.setItemChecked(position, true);
	        setTitle(mDrawerItemsHashMap.get(drawerListItemNames[position]));
	        mDrawerLayout.closeDrawer(mDrawerList);
	        
	        return;
    	}else {
    		fragment = new CustomFragment();
    		fragment.setArguments(args);
    		transaction.replace(R.id.content_frame, fragment);
    	}
    
        transaction.commit();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerItemsHashMap.get(drawerListItemNames[position]));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public class CustomFragment extends SherlockFragment implements View.OnClickListener{
        public static final String MENU_SELECTED_POS = "menu_selected_position";

        public CustomFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override  
        public void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState);  
         setHasOptionsMenu(true);  
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.sub_fragment_planet2, container, false);
            
            Button startBtn = (Button)rootView.findViewById(R.id.start_btn);
            startBtn.setOnClickListener(this);
            
            int i = getArguments().getInt(MENU_SELECTED_POS);

            getActivity().setTitle(mDrawerItemsHashMap.get(drawerListItemNames[i]));
            return rootView;
        }        
        
        @Override
    	public void onClick(View view) {
    		switch (view.getId()) {
    		case R.id.start_btn:
    			Intent localIntent = new Intent(this.getActivity(), SubActivity.class);
    			startActivity(localIntent);
    			break;
    		}
        }
        
        @Override
    	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    		inflater = ((SherlockFragmentActivity)getActivity()).getSupportMenuInflater();
    		inflater.inflate(R.menu.sub_fragment_menu2, menu);
    		super.onCreateOptionsMenu(menu, inflater);
    	}
    }
}