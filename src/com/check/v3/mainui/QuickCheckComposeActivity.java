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
        if (intent.hasExtra(ApiConstant.KEY_QUICK_CHECK_ACTION)) {
        	mQuickCheckAction = intent.getIntExtra(ApiConstant.KEY_QUICK_CHECK_ACTION, ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION);
        }
        
        SherlockFragment firstFragment;
        if(mQuickCheckAction == ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION){
        	firstFragment = new QuickCheckEditorFragment();
        	firstFragment.setArguments(getIntent().getExtras());
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, firstFragment).commit();
        } else if(mQuickCheckAction == ApiConstant.QUICK_CHECK_VIEW_ACTION){
        	QuickCheckListItemData qcListItemData = (QuickCheckListItemData) intent.getSerializableExtra(ApiConstant.KEY_QUICK_CHECK_DATA);
        	
        	QuickCheckRspData qcReportData = convertQuickCheckListItemData2RspData(qcListItemData);
        	
        	SherlockFragment targetFragment = QuickCheckViewerFragment.newInstance(qcReportData);
        	
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.quick_check_compose_activity_container, targetFragment).commit();
        }
        
	  }
    
    private QuickCheckRspData convertQuickCheckListItemData2RspData(QuickCheckListItemData itemData){
    	QuickCheckRspData data = new QuickCheckRspData();
		data.setId(itemData.getId());
		data.setSubmitterId(itemData.getSubmitterId());
		data.setSubmitterName(itemData.getSubmitterName());
		data.setResponsiblePeronId(itemData.getResponsiblePeronId());
		data.setResponsiblePersonName(itemData.getResponsiblePersonName());
		data.setOrganizationId(itemData.getOrganizationId());
		data.setOrganizationName(itemData.getOrganizationName());
		data.setDeadline(itemData.getDeadline());
		data.setState(itemData.getState());
		data.setLevel(itemData.getLevel());
		data.setDescription(itemData.getDescription());
		data.setImages(itemData.getImages());
    	return data;
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
	public void onQuickCheckSubmitSuccess(QuickCheckRspData qcRspData) {
		QuickCheckViewerFragment newFragment = QuickCheckViewerFragment.newInstance(qcRspData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
//		transaction.addToBackStack(null);

		transaction.commit();		
	}

	@Override
	public void onQuickCheckEditMenuItemClicked(QuickCheckRspData qcRspData) {
		QuickCheckEditorFragment newFragment = QuickCheckEditorFragment.newInstance(qcRspData);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.quick_check_compose_activity_container, newFragment);
//		transaction.addToBackStack(null);

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
	}
