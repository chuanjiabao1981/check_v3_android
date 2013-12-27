package com.check.v3.mainui;

import org.apache.http.Header;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.AsyncHttpExeptionHelper;
import com.check.v3.CloudCheckApplication;
import com.check.v3.CloudCheckApplication.AccountMngr;
import com.check.v3.api.ApiConstant;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.ReportResolutionReqData;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.util.CommonHelper;
import com.google.gson.Gson;

public class QuickReportResolutionEditorFragment extends SherlockFragment {
	private static final String TAG = "QuickReportResolutionEditorFragment";
	
	QuickReportResolutionEditorFragmentListener mQuickReportResolutionEditorFragmentListener;

	private int mActionMode = ApiConstant.ACTION_MODE_RSLV_NEW_ADDED;
	
	private EditText mIssueRslvDscptEditView;
	private String mIssueRslvDescriptionStr;
	
	private int mOrigReportId = 0;
	private int mOrigRslvId = 0;
	private ReportResolutionReqData mQuickReportResolutionReqData;
	private ReportResolutionRspData mQuickReportResolutionRsqData;
	
	Gson gson;

	public static QuickReportResolutionEditorFragment newInstance(
			Bundle reference) {
		QuickReportResolutionEditorFragment fragment = new QuickReportResolutionEditorFragment();

		fragment.setArguments(reference);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// init variable and adapter
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		gson = new Gson();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflater the view
		View rootView = inflater.inflate(
				R.layout.quick_report_resolution_editor_fragment, container,
				false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// find view and set adapter
		super.onActivityCreated(savedInstanceState);

		mIssueRslvDscptEditView = (EditText) getView().findViewById(R.id.qr_issue_resolution_dscp_edit);
		
		initViewData();
	}
	
	private void initViewData(){
        Bundle args = getArguments();
        if(args != null){                	
        	if(args.containsKey(ApiConstant.WHAT_ACTION)){
        		mActionMode = args.getInt(ApiConstant.WHAT_ACTION);
        	}
        	
        	if(mActionMode == ApiConstant.ACTION_MODE_RSLV_NEW_ADDED){
        		if(args.containsKey(ApiConstant.ORIG_REPORT_ID)){
            		mOrigReportId = args.getInt(ApiConstant.ORIG_REPORT_ID);
            	}
        	}else if(mActionMode == ApiConstant.ACTION_MODE_RSLV_EDIT_EXIST){
        		if(args.containsKey(ApiConstant.ORIG_RESOLUTION_DATA)){  		
            		mQuickReportResolutionRsqData = (ReportResolutionRspData) args.getSerializable(ApiConstant.ORIG_RESOLUTION_DATA);
            		mOrigRslvId = mQuickReportResolutionRsqData.getId();
            		mIssueRslvDscptEditView.setText(mQuickReportResolutionRsqData.getDescription());
            	}        		
        	}
        }
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((QuickCheckComposeActivity) getActivity())
				.getSupportMenuInflater();
		inflater.inflate(R.menu.quick_report_resolution_compose_fragment_menu,
				menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.rslv_action_submit:
			doCommitQuickReportResolution();
			break;
		case R.id.rslv_action_discard:
			doDiscardQuickReportResolution();
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	// Container Activity must implement this interface
    public interface QuickReportResolutionEditorFragmentListener {
    	public void onSubmitQuickReportResolutionSuccess(Bundle qrRslvRspData);
        public void onDiscardQuickReportResolution();
    }
	
    public void prepareQuickReportResolutionData(){
    	mIssueRslvDescriptionStr = mIssueRslvDscptEditView.getText().toString().trim();
    	mQuickReportResolutionReqData = new ReportResolutionReqData();
    	mQuickReportResolutionReqData.setDescription(mIssueRslvDescriptionStr);
    }
    
	private void doCommitQuickReportResolution() {
		prepareQuickReportResolutionData();		
		
		
		String relativeUrl = "";
		if(mActionMode == ApiConstant.ACTION_MODE_RSLV_NEW_ADDED  && mOrigReportId > 0){
			relativeUrl = "quick_reports/" + mOrigReportId + "/quick_report_resolves";
		} else if(mActionMode == ApiConstant.ACTION_MODE_RSLV_EDIT_EXIST && mOrigRslvId > 0){
			relativeUrl = "quick_report_resolves/" + mOrigRslvId;
		}
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	
            	String rspStr = new String(response);
				Log.d(TAG, "quick report submit response : " + rspStr.toString());
				
				ReportResolutionRspData qrRslvRspData = gson.fromJson(rspStr.toString(),
						ReportResolutionRspData.class);
				Log.i(TAG, "server response for compose quick check : " + rspStr.toString());
				
				Bundle qrRslvData = new Bundle();
				qrRslvData.putSerializable(ApiConstant.ORIG_RESOLUTION_DATA, qrRslvRspData);
	    		qrRslvData.putInt(ApiConstant.WHAT_ACTION, ApiConstant.ACTION_MODE_VIEW_RSLV_FROM_SUBMIT_RSP);
				mQuickReportResolutionEditorFragmentListener.onSubmitQuickReportResolutionSuccess(qrRslvData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {            	
            	String errorStr = AsyncHttpExeptionHelper.getMessage(getActivity(), e, errorResponse, statusCode);
            	CommonHelper.notify(getActivity(), errorStr);
            }
        };
        String jsonReqStr = gson.toJson(mQuickReportResolutionReqData);
        CloudCheckApplication.mAsyncHttpClientApi.post(relativeUrl, jsonReqStr, null, responseHandler);
	}

	private void doDiscardQuickReportResolution() {

	}
}
