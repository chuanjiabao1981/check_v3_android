package com.check.v3.mainui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.check.client.R;
import com.check.v3.data.QuickCheckRspData;

public class QuickCheckViewerFragment extends SherlockFragment {
	private static final String TAG = "QuickCheckViewerFragment";

	private static final String ARG_REFERENCE = "reference";
	
	TextView mIssueDscpTextView;
	TextView mIssueStateTextView;
	TextView mIssueLevelTextView;
	TextView mIssueSubmitterNameTextView;
	TextView mIssueDeadlineTextView;
	TextView mIssueRspOrgTextView;
	TextView mIssueRspPersonTextView;

	public static QuickCheckViewerFragment newInstance(
			QuickCheckRspData reference) {
		QuickCheckViewerFragment fragment = new QuickCheckViewerFragment();

		Bundle args = new Bundle();
		args.putSerializable(ARG_REFERENCE, reference);
		fragment.setArguments(args);

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
		View rootView = inflater.inflate(R.layout.quick_check_viewer_fragment,
				container, false);
		
		mIssueDscpTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_dscp_text);
		mIssueStateTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_state_text);
		mIssueLevelTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_level_text);
		mIssueSubmitterNameTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_submitter_text);
		mIssueDeadlineTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_deadline_text);
		mIssueRspOrgTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_rsp_org_text);
		mIssueRspPersonTextView = (TextView) rootView.findViewById(R.id.qc_v_issue_rsp_person_text);

		return rootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        QuickCheckRspData qcData;
        if (savedInstanceState != null) {
        	qcData = null;
        } else {
            Bundle args = getArguments();
            qcData = (QuickCheckRspData) args.getSerializable(ARG_REFERENCE);
        }

        displayQuickCheck(qcData);
    }
    
    private void displayQuickCheck(QuickCheckRspData data){
    	mIssueDscpTextView.setText(data.getDescription());
    	mIssueStateTextView.setText(data.getState());
    	mIssueLevelTextView.setText(data.getLevel());
    	mIssueSubmitterNameTextView.setText(data.getSubmitterName());
    	mIssueDeadlineTextView.setText(data.getDeadline());
    	mIssueRspOrgTextView.setText(data.getOrganizationName());
    	mIssueRspPersonTextView.setText(data.getResponsiblePersonName());
    }
	
}
