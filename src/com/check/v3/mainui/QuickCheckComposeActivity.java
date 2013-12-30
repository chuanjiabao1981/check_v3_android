package com.check.v3.mainui;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.check.client.R;
import com.check.v3.api.ApiConstant;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.data.QuickCheckRspData;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.mainui.QuickCheckEditorFragment.QuickCheckEditorFragmentListener;
import com.check.v3.mainui.QuickCheckViewerFragment.QuickCheckViewerFragmentListener;
import com.check.v3.mainui.QuickReportResolutionEditorFragment.QuickReportResolutionEditorFragmentListener;
import com.check.v3.mainui.QuickReportResolutionViewerFragment.QuickReportResolutionViewerFragmentListener;
import com.check.v3.util.CommonHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


public class QuickCheckComposeActivity extends SherlockFragmentActivity implements QuickCheckEditorFragmentListener,
				QuickCheckViewerFragmentListener, QuickReportResolutionViewerFragmentListener,
				QuickReportResolutionEditorFragmentListener {
	private static final String TAG = "QuickCheckComposeActivity";
	private int mQuickCheckAction = ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_check_compose_activity_layout);
	    
        Intent intent = getIntent();
        if (intent.hasExtra(ApiConstant.WHAT_ACTION)) {
        	mQuickCheckAction = intent.getIntExtra(ApiConstant.WHAT_ACTION, ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION);
        }
        
        SherlockFragment firstFragment;
        Bundle origData = new Bundle();
        if(mQuickCheckAction == ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION){
        	origData.putInt(ApiConstant.WHAT_ACTION, ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION);
        	firstFragment = QuickCheckEditorFragment.newInstance(origData);
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, firstFragment).commit();
        } else if(mQuickCheckAction == ApiConstant.QUICK_CHECK_VIEW_ACTION){
        	QuickCheckListItemData qcListItemData = (QuickCheckListItemData) intent.getSerializableExtra(ApiConstant.KEY_QUICK_CHECK_DATA);
        	
        	QuickCheckRspData qcReportData = CommonHelper.convertQuickCheckListItemData2RspData(qcListItemData);
        	origData.putInt(ApiConstant.WHAT_ACTION, ApiConstant.QUICK_CHECK_VIEW_ACTION);
        	origData.putInt(ApiConstant.ORIG_REPORT_ID, qcReportData.getId());
        	origData.putSerializable(ApiConstant.KEY_QUICK_CHECK_DATA, qcReportData);
        	SherlockFragment targetFragment = QuickCheckViewerFragment.newInstance(origData);
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, targetFragment).commit();
        }else if(mQuickCheckAction == ApiConstant.QUICK_CHECK_VIEW_RSP_ACTION){
        	QuickCheckRspData qrRspData = (QuickCheckRspData) intent.getSerializableExtra(ApiConstant.KEY_QUICK_CHECK_DATA);

        	origData.putInt(ApiConstant.WHAT_ACTION, ApiConstant.QUICK_CHECK_VIEW_RSP_ACTION);
        	origData.putSerializable(ApiConstant.KEY_QUICK_CHECK_DATA, qrRspData);
        	SherlockFragment targetFragment = QuickCheckViewerFragment.newInstance(origData);
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, targetFragment).commit();
        }
        
	  }


	@Override
	public void onProcessItemSelected() {
//		QuickCheckViewerFragment newFragment = new QuickCheckViewerFragment();
//		Bundle args = new Bundle();
//
//		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
//		transaction.addToBackStack(null);
//
//		transaction.commit();
		
	}


	@Override
	public void onQuickCheckSubmitSuccess(Bundle qcRspData) {
		QuickCheckViewerFragment newFragment = QuickCheckViewerFragment.newInstance(qcRspData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
//		transaction.addToBackStack(null);

		transaction.commit();		
	}

	@Override
	public void onQuickCheckEditMenuItemClicked(Bundle qrOrigData) {
		QuickCheckEditorFragment newFragment = QuickCheckEditorFragment.newInstance(qrOrigData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
		transaction.addToBackStack(null);

		transaction.commit();	
	}

	@Override
	public void onQuickCheckResolveMenuItemClicked(Bundle qrRslvData) {
		QuickReportResolutionEditorFragment newFragment = QuickReportResolutionEditorFragment.newInstance(qrRslvData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
		transaction.addToBackStack(null);

		transaction.commit();
		
	}

	@Override
	public void onQuickReportResolutionEditMenuItemClicked(
			Bundle qrRslvRspData) {
		QuickReportResolutionEditorFragment newFragment = QuickReportResolutionEditorFragment.newInstance(qrRslvRspData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
		transaction.addToBackStack(null);

		transaction.commit();
		
	}


	@Override
	public void onDiscardQuickReportResolution() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubmitQuickReportResolutionSuccess(
			Bundle qrRslvRspData) {
		QuickReportResolutionViewerFragment newFragment = QuickReportResolutionViewerFragment.newInstance(qrRslvRspData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
		transaction.addToBackStack(null);

		transaction.commit();
	}


	@Override
	public void onQuickReportResolveListItemClicked(Bundle qrRslvDataBundle) {
		QuickReportResolutionViewerFragment newFragment = QuickReportResolutionViewerFragment.newInstance(qrRslvDataBundle);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
		transaction.addToBackStack(null);

		transaction.commit();
	}
	
}
