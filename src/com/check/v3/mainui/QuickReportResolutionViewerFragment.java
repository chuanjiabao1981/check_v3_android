package com.check.v3.mainui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.api.ApiConstant;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.widget.CustomGridView;

public class QuickReportResolutionViewerFragment extends SherlockFragment {
	private static final String TAG = "QuickReportResolutionViewerFragment";

	private int mViewMode = ApiConstant.ACTION_MODE_VIEW_RSLV_FROM_LIST;
	
	private TextView mIssueRlsvDscpTextView;
	private LinearLayout mIssueRslvPhotoViewerContainer;
	private CustomGridView mIssueRslvPhotoViewerGridView;
	private PhotoGridViewSimpleAdapter mIssueRslvPhotoGridViewSimpleAdapter;
	
	ReportResolutionRspData qrResolutionRspData;
	QuickReportResolutionViewerFragmentListener mQuickReportResolutionViewerFragmentListener;

	public static QuickReportResolutionViewerFragment newInstance(
			Bundle reference) {
		QuickReportResolutionViewerFragment fragment = new QuickReportResolutionViewerFragment();

		fragment.setArguments(reference);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.quick_report_resolution_viewer_fragment,
				container, false);

		return rootView;
	}

	  @Override
	  public void onAttach(Activity activity) {
	      super.onAttach(activity);
	      
	      try {
	    	  mQuickReportResolutionViewerFragmentListener = (QuickReportResolutionViewerFragmentListener) activity;
	      } catch (ClassCastException e) {
	          throw new ClassCastException(activity.toString() + " must implement QuickReportResolutionViewerFragmentListener");
	      }
	  }
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
               
		mIssueRlsvDscpTextView = (TextView) getView().findViewById(R.id.qr_v_issue_resolution_dscp_text);
		mIssueRslvPhotoViewerContainer = (LinearLayout) getView().findViewById(R.id.qr_v_issue_resolution_photo_container);
		mIssueRslvPhotoViewerGridView = (CustomGridView) getView().findViewById(R.id.qr_v_issue_resolution_photo_grid_view);
		
		initViewData();
		
        displayQuickReportResolution(qrResolutionRspData);
    }
    
    private void initViewData(){
        Bundle args = getArguments();
        if(args != null){                	
        	if(args.containsKey(ApiConstant.WHAT_ACTION)){
        		mViewMode = args.getInt(ApiConstant.WHAT_ACTION);
        	}
        	
    		if(args.containsKey(ApiConstant.ORIG_RESOLUTION_DATA)){  		
    			qrResolutionRspData = (ReportResolutionRspData) args.getSerializable(ApiConstant.ORIG_RESOLUTION_DATA);
        	}        		
        }
    }
    
    @Override
  	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
  		inflater = ((QuickCheckComposeActivity)getActivity()).getSupportMenuInflater();
  		inflater.inflate(R.menu.quick_report_resolution_viewer_menu, menu);
  		super.onCreateOptionsMenu(menu, inflater);
  	}

  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  		// Handle action buttons
  		switch (item.getItemId()) {
  		case R.id.rlsv_viewer_action_edit:
  			doEditQuickReportResolution();
  			break;
  		default:
  			break;
  		}
  		return super.onOptionsItemSelected(item);
  	}
  	
	// Container Activity must implement this interface
    public interface QuickReportResolutionViewerFragmentListener {
    	public void onQuickReportResolutionEditMenuItemClicked(Bundle qrRslvRspData);
    }
    
    private void displayQuickReportResolution(final ReportResolutionRspData data){
    	mIssueRlsvDscpTextView.setText(data.getDescription());
    	    	
    	if(data.getImages() != null && data.getImages().size() > 0){
    		mIssueRslvPhotoViewerContainer.setVisibility(View.VISIBLE);
    		mIssueRslvPhotoGridViewSimpleAdapter = new PhotoGridViewSimpleAdapter(getActivity(), data.getImages(), false);
    		mIssueRslvPhotoViewerGridView.setAdapter(mIssueRslvPhotoGridViewSimpleAdapter);
    	} else {
    		mIssueRslvPhotoViewerContainer.setVisibility(View.GONE);
    	}
    }
    
    private void doEditQuickReportResolution(){
    	if(qrResolutionRspData != null){
    		Bundle qrRslvData = new Bundle();
    		qrRslvData.putSerializable(ApiConstant.ORIG_RESOLUTION_DATA, qrResolutionRspData);
    		qrRslvData.putInt(ApiConstant.WHAT_ACTION, ApiConstant.ACTION_MODE_RSLV_EDIT_EXIST);
    		mQuickReportResolutionViewerFragmentListener.onQuickReportResolutionEditMenuItemClicked(qrRslvData);
    	}
    }
	
}
