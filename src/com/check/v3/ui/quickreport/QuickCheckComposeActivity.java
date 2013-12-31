package com.check.v3.ui.quickreport;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.check.client.R;
import com.check.v3.Constants;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.data.QuickCheckRspData;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.ui.quickreport.QuickCheckEditorFragment.QuickCheckEditorFragmentListener;
import com.check.v3.ui.quickreport.QuickCheckViewerFragment.QuickCheckViewerFragmentListener;
import com.check.v3.ui.quickreport.QuickReportResolutionEditorFragment.QuickReportResolutionEditorFragmentListener;
import com.check.v3.ui.quickreport.QuickReportResolutionViewerFragment.QuickReportResolutionViewerFragmentListener;
import com.check.v3.utils.CommonHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


public class QuickCheckComposeActivity extends SherlockFragmentActivity implements QuickCheckEditorFragmentListener,
				QuickCheckViewerFragmentListener, QuickReportResolutionViewerFragmentListener,
				QuickReportResolutionEditorFragmentListener {
	private static final String TAG = "QuickCheckComposeActivity";
	private int mQuickCheckAction = Constants.QUICK_CHECK_EDIT_NEW_ACTION;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_check_compose_activity_layout);
	    
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.WHAT_ACTION)) {
        	mQuickCheckAction = intent.getIntExtra(Constants.WHAT_ACTION, Constants.QUICK_CHECK_EDIT_NEW_ACTION);
        }
        
        SherlockFragment firstFragment;
        Bundle origData = new Bundle();
        if(mQuickCheckAction == Constants.QUICK_CHECK_EDIT_NEW_ACTION){
        	origData.putInt(Constants.WHAT_ACTION, Constants.QUICK_CHECK_EDIT_NEW_ACTION);
        	firstFragment = QuickCheckEditorFragment.newInstance(origData);
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, firstFragment).commit();
        } else if(mQuickCheckAction == Constants.QUICK_CHECK_VIEW_ACTION){
        	QuickCheckListItemData qcListItemData = (QuickCheckListItemData) intent.getSerializableExtra(Constants.KEY_QUICK_CHECK_DATA);
        	
        	QuickCheckRspData qcReportData = CommonHelper.convertQuickCheckListItemData2RspData(qcListItemData);
        	origData.putInt(Constants.WHAT_ACTION, Constants.QUICK_CHECK_VIEW_ACTION);
        	origData.putInt(Constants.ORIG_REPORT_ID, qcReportData.getId());
        	origData.putSerializable(Constants.KEY_QUICK_CHECK_DATA, qcReportData);
        	SherlockFragment targetFragment = QuickCheckViewerFragment.newInstance(origData);
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, targetFragment).commit();
        }else if(mQuickCheckAction == Constants.QUICK_CHECK_VIEW_RSP_ACTION){
        	QuickCheckRspData qrRspData = (QuickCheckRspData) intent.getSerializableExtra(Constants.KEY_QUICK_CHECK_DATA);

        	origData.putInt(Constants.WHAT_ACTION, Constants.QUICK_CHECK_VIEW_RSP_ACTION);
        	origData.putSerializable(Constants.KEY_QUICK_CHECK_DATA, qrRspData);
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
