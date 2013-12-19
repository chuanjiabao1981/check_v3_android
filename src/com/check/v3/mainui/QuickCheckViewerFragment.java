package com.check.v3.mainui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.data.QuickCheckRspData;
import com.check.v3.mainui.QuickCheckEditorFragment.QuickCheckEditorFragmentListener;
import com.check.v3.widget.CustomGridView;

public class QuickCheckViewerFragment extends SherlockFragment {
	private static final String TAG = "QuickCheckViewerFragment";

	private static final String ARG_REFERENCE = "reference";
	
	private TextView mIssueDscpTextView;
	private TextView mIssueStateTextView;
	private TextView mIssueLevelTextView;
	private TextView mIssueSubmitterNameTextView;
	private TextView mIssueDeadlineTextView;
	private TextView mIssueRspOrgTextView;
	private TextView mIssueRspPersonTextView;
	private CustomGridView mIssuePhotoViewerGridView;
	private ImageViewerSimpleAdapter mImageViewerSimpleAdapter;
	QuickCheckRspData qcData;
	QuickCheckViewerFragmentListener mQuickCheckViewerFragmentListener;

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
		mIssuePhotoViewerGridView = (CustomGridView) rootView.findViewById(R.id.qc_v_issue_photo_grid_view);

		return rootView;
	}

	  @Override
	  public void onAttach(Activity activity) {
	      super.onAttach(activity);
	      
	      try {
	    	  mQuickCheckViewerFragmentListener = (QuickCheckViewerFragmentListener) activity;
	      } catch (ClassCastException e) {
	          throw new ClassCastException(activity.toString() + " must implement mQuickCheckViewerFragmentListener");
	      }
	  }
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        	qcData = null;
        } else {
            Bundle args = getArguments();
            qcData = (QuickCheckRspData) args.getSerializable(ARG_REFERENCE);
        }

        displayQuickCheck(qcData);
    }
    
    @Override
  	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
  		inflater = ((QuickCheckComposeActivity)getActivity()).getSupportMenuInflater();
  		inflater.inflate(R.menu.quick_check_viewer_menu, menu);
  		super.onCreateOptionsMenu(menu, inflater);
  	}

  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  		// Handle action buttons
  		switch (item.getItemId()) {
  		case R.id.qc_view_action_edit:
  			doEditQuickCheckItem();
  			break;		
  		default:
  			Toast.makeText(this.getActivity(),
  					"Menu item " + item.getTitle() + " is selected.",
  					Toast.LENGTH_LONG).show();

  		}
  		return super.onOptionsItemSelected(item);
  	}
  	
	// Container Activity must implement this interface
    public interface QuickCheckViewerFragmentListener {
    	public void OnQuickCheckEditMenuItemClicked(QuickCheckRspData qcRspData);
        public void OnQuickCheckProcessMenuItemClicked();
    }
    
    private void displayQuickCheck(final QuickCheckRspData data){
    	mIssueDscpTextView.setText(data.getDescription());
    	mIssueStateTextView.setText(data.getState());
    	mIssueLevelTextView.setText(data.getLevel());
    	mIssueSubmitterNameTextView.setText(data.getSubmitterName());
    	mIssueDeadlineTextView.setText(data.getDeadline());
    	mIssueRspOrgTextView.setText(data.getOrganizationName());
    	mIssueRspPersonTextView.setText(data.getResponsiblePersonName());
    	    	
    	if(data.getImages() != null && data.getImages().size() > 0){
    		mImageViewerSimpleAdapter = new ImageViewerSimpleAdapter(getActivity(), data.getImages());
    		mIssuePhotoViewerGridView.setAdapter(mImageViewerSimpleAdapter);
    	}
    }
    
    private void doEditQuickCheckItem(){
    	if(qcData != null){
    		mQuickCheckViewerFragmentListener.OnQuickCheckEditMenuItemClicked(qcData);
    	}
    }
	
}
