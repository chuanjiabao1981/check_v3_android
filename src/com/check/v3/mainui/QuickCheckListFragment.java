package com.check.v3.mainui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment; 
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;

import android.os.Bundle;  
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.ArrayAdapter;  
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;  
import android.widget.Toast;  
 
public class QuickCheckListFragment extends SherlockListFragment {
	private static String TAG = "RandomCheckListFragment";
     String[] random_check_list_arry = {  
          "走动巡查1",  
          "走动巡查2",  
          "走动巡查3",  
          "走动巡查4",  
          "走动巡查5",  
          "走动巡查6",
          "走动巡查7",  
          "走动巡查8",  
          "走动巡查9",  
          "走动巡查10",
          "走动巡查11",  
          "走动巡查12",  
          "走动巡查13",  
          "走动巡查14",
          "走动巡查15"  
     };  
     
     public QuickCheckListFragment() {
         // Empty constructor required for fragment subclasses
     }
 
     @Override  
     public View onCreateView(LayoutInflater inflater,   
     ViewGroup container, Bundle savedInstanceState) {
          return inflater.inflate(R.layout.quick_check_list_fragment, container, false);  
     }  
 
     @Override  
     public void onCreate(Bundle savedInstanceState) {  
          super.onCreate(savedInstanceState);  
          setHasOptionsMenu(true);
          
          setListAdapter(new ArrayAdapter<String>(getActivity(),  
               android.R.layout.simple_list_item_1, random_check_list_arry)); 
     }  
  
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "on pause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "on stop");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "on destroy");
	}
     
    @Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "on resume");
		final LinearLayout mLoadingLayout = (LinearLayout)this.getActivity().findViewById(R.id.progress_container_id);
		final FrameLayout mListLayout = (FrameLayout)this.getActivity().findViewById(R.id.list_container_id);

        new Handler().postDelayed(new Runnable(){
   	   	 
            @Override
            public void run() {
            	mLoadingLayout.setVisibility(View.GONE);
                mListLayout.setVisibility(View.VISIBLE);
            }              
           }, 2000);
	}
    
    @Override
	public void onListItemClick(ListView parent, View v,   
     int position, long id)   
     {            
          Toast.makeText(getActivity(),   
               "You have selected " + random_check_list_arry[position] + "in list fragment.",   
               Toast.LENGTH_SHORT).show();  
     }
     
     @Override
 	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
 		inflater = ((SherlockFragmentActivity)getActivity()).getSupportMenuInflater();
 		inflater.inflate(R.menu.random_check_list_fragment_menu, menu);
 		super.onCreateOptionsMenu(menu, inflater);
 	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch(item.getItemId()) {
		default:
			Toast.makeText(this.getActivity(), "Menu item " + item.getTitle() + " is selected.", Toast.LENGTH_LONG).show();
		return super.onOptionsItemSelected(item);
		}
	}	
 
}  