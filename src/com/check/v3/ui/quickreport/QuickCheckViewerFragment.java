package com.check.v3.ui.quickreport;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.Constants;
import com.check.v3.AsyncHttpExeptionHelper;
import com.check.v3.CloudCheckApplication;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.QuickCheckDetailedRspData;
import com.check.v3.data.QuickCheckRspData;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.ui.adapter.IssueResolutionListAdapter;
import com.check.v3.ui.adapter.PhotoGridViewSimpleAdapter;
import com.check.v3.utils.CommonHelper;
import com.check.v3.widget.CustomGridView;
import com.check.v3.widget.CustomIssueItemView;
import com.check.v3.widget.CustomListView;
import com.google.gson.Gson;

public class QuickCheckViewerFragment extends SherlockFragment {
	private static final String TAG = "QuickCheckViewerFragment";

	private CustomIssueItemView mCustomIssueDscpView;
	private TextView mIssueDscpTextView;
	private CustomIssueItemView mCustomIssueStateView;
	private TextView mIssueStateTextView;
	private CustomIssueItemView mCustomIssueLevelView;
	private TextView mIssueLevelTextView;
	private CustomIssueItemView mCustomIssueSubmitterNameView;
	private TextView mIssueSubmitterNameTextView;
	private CustomIssueItemView mCustomIssueDeadlineView;
	private TextView mIssueDeadlineTextView;
	private CustomIssueItemView mCustomIssueRspOrgView;
	private TextView mIssueRspOrgTextView;
	private CustomIssueItemView mCustomIssueRspPersonView;
	private TextView mIssueRspPersonTextView;
	
	private LinearLayout mIssuePhotoViewerContainer;
	private CustomGridView mIssuePhotoViewerGridView;
	private PhotoGridViewSimpleAdapter mPhotoGridViewSimpleAdapter;

	private LinearLayout mIssueResolutionListContainer;
	private CustomListView mIssueResolutionListView;
	private IssueResolutionListAdapter mIssueResolutionListAdapter;

	private int mActionMode = Constants.QUICK_CHECK_VIEW_ACTION;
	private int mOrigReportId = 0;
	private QuickCheckRspData qcData = null;
	private QuickCheckDetailedRspData detailedData = null;
	private Gson gson;
	private QuickCheckViewerFragmentListener mQuickCheckViewerFragmentListener;
	
	private static final int CTX_MENU_EDIT_ITEM = Menu.FIRST;
	private static final int CTX_MENU_DELETE_ITEM = Menu.FIRST + 1;

	public static QuickCheckViewerFragment newInstance(Bundle reference) {
		QuickCheckViewerFragment fragment = new QuickCheckViewerFragment();

		fragment.setArguments(reference);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		gson = new Gson();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.quick_check_viewer_fragment,
				container, false);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mQuickCheckViewerFragmentListener = (QuickCheckViewerFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement QuickCheckViewerFragmentListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mCustomIssueDscpView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_dscp_container);
		mIssueDscpTextView = (TextView) mCustomIssueDscpView.findViewById(
				R.id.issue_item_value_text);
				
		mCustomIssueStateView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_state_container);
		mIssueStateTextView = (TextView) mCustomIssueStateView.findViewById(
				R.id.issue_item_value_text);
		
		mCustomIssueLevelView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_level_container);
		mIssueLevelTextView = (TextView) mCustomIssueLevelView.findViewById(
				R.id.issue_item_value_text);
		
		mCustomIssueSubmitterNameView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_submitter_container);
		mIssueSubmitterNameTextView = (TextView) getView().findViewById(
				R.id.issue_item_value_text);
		
		mCustomIssueDeadlineView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_deadline_container);
		mIssueDeadlineTextView = (TextView) mCustomIssueDeadlineView.findViewById(
				R.id.issue_item_value_text);
		
		mCustomIssueRspOrgView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_rsp_org_container);
		mIssueRspOrgTextView = (TextView) mCustomIssueRspOrgView.findViewById(
				R.id.issue_item_value_text);
		
		mCustomIssueRspPersonView = (CustomIssueItemView) getView().findViewById(
				R.id.qr_v_issue_rsp_person_container);
		mIssueRspPersonTextView = (TextView) mCustomIssueRspPersonView.findViewById(
				R.id.issue_item_value_text);
		
		mIssuePhotoViewerContainer = (LinearLayout) getView().findViewById(
				R.id.qr_v_issue_photo_list_container);
		mIssuePhotoViewerGridView = (CustomGridView) getView().findViewById(
				R.id.qr_v_issue_photo_grid_view);

		mIssueResolutionListContainer = (LinearLayout) getView().findViewById(
				R.id.qr_v_issue_resolve_container);
		mIssueResolutionListView = (CustomListView) getView().findViewById(
				R.id.quick_report_resolve_list_view);

		initData();
	}

	private void initData() {
		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey(Constants.WHAT_ACTION)) {
				mActionMode = args.getInt(Constants.WHAT_ACTION);
			}

			if (mActionMode == Constants.QUICK_CHECK_VIEW_ACTION) {
				if (args.containsKey(Constants.ORIG_REPORT_ID)) {
					mOrigReportId = args.getInt(Constants.ORIG_REPORT_ID);
					doRetrieveDetailedQuickReportInfo(mOrigReportId);
				}
			} else if (mActionMode == Constants.QUICK_CHECK_VIEW_RSP_ACTION) {
				if (args.containsKey(Constants.KEY_QUICK_CHECK_DATA)) {
					qcData = (QuickCheckRspData) args
							.getSerializable(Constants.KEY_QUICK_CHECK_DATA);
					initView(qcData);
				}
			}
		}
	}

	private void initView(QuickCheckRspData data) {

		mIssueDscpTextView.setText(data.getDescription());
		mIssueStateTextView.setText(data.getState());
		mIssueLevelTextView.setText(data.getLevel());
		mIssueSubmitterNameTextView.setText(data.getSubmitterName());
		mIssueDeadlineTextView.setText(data.getDeadline());
		mIssueRspOrgTextView.setText(data.getOrganizationName());
		mIssueRspPersonTextView.setText(data.getResponsiblePersonName());

		if (data.getImages() != null && data.getImages().size() > 0) {
			mIssuePhotoViewerContainer.setVisibility(View.VISIBLE);
			mPhotoGridViewSimpleAdapter = new PhotoGridViewSimpleAdapter(
					getActivity(), data.getImages(), false);
			mIssuePhotoViewerGridView.setAdapter(mPhotoGridViewSimpleAdapter);
		}else{
			mIssuePhotoViewerContainer.setVisibility(View.GONE);
		}

	}

	private void initViewExt(QuickCheckDetailedRspData detailedData) {
		if (detailedData != null
				&& detailedData.getQuickReport().getResolveNum() > 0
				&& detailedData.getQuickReportResolves().size() > 0) {
			mIssueResolutionListContainer.setVisibility(View.VISIBLE);
			mIssueResolutionListAdapter = new IssueResolutionListAdapter(
					getActivity(), detailedData.getQuickReportResolves());
			mIssueResolutionListView.setAdapter(mIssueResolutionListAdapter);

			mIssueResolutionListView.setOnCreateContextMenuListener(this);

			mIssueResolutionListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Log.d(TAG, "click position:" + position);
							Toast.makeText(
									getActivity(),
									"You have selected " + position
											+ " in list fragment.",
									Toast.LENGTH_SHORT).show();
							ReportResolutionRspData selectedRslvListItemData = (ReportResolutionRspData) parent
									.getItemAtPosition(position);
							doQuickReportRslvListItemClicked(selectedRslvListItemData);
						}
					});
		}
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo mi) {
		menu.setHeaderTitle("操作");
		menu.add(0, CTX_MENU_EDIT_ITEM, 0, "编辑");
		menu.add(0, CTX_MENU_DELETE_ITEM, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case CTX_MENU_EDIT_ITEM:
			break;
		case CTX_MENU_DELETE_ITEM:
			break;
		default:
			break;

		}
		Log.d(TAG, "clicked context menu item " + item.getItemId());
		return super.onContextItemSelected(item);
	}

	public void doRetrieveDetailedQuickReportInfo(int reportId) {
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				String rspStr = new String(response);
				Log.d(TAG, "response : " + rspStr.toString());

				detailedData = gson.fromJson(rspStr,
						QuickCheckDetailedRspData.class);

				qcData = CommonHelper
						.convertQuickCheckListItemData2RspData(detailedData
								.getQuickReport());
				initView(qcData);
				initViewExt(detailedData);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {

				String errStr = new String(errorResponse);

				String errorStr = AsyncHttpExeptionHelper.getMessage(
						getActivity(), e, errorResponse, statusCode);
				CommonHelper.notify(getActivity(), errorStr);
				Log.d(TAG, "error response of get detailed quick report item: "
						+ e.toString() + ", statuscode = " + statusCode + ", "
						+ errStr);
			}
		};
		CloudCheckApplication.mAsyncHttpClientApi.get("quick_reports/"
				+ reportId, null, responseHandler);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((QuickCheckComposeActivity) getActivity())
				.getSupportMenuInflater();
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
		case R.id.qc_view_action_process:
			doResolveQuickReportIssue();
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
		public void onQuickCheckEditMenuItemClicked(Bundle qrOrigData);

		public void onQuickCheckResolveMenuItemClicked(Bundle qrRslvDataBundle);

		public void onQuickReportResolveListItemClicked(Bundle qrRslvDataBundle);
	}

	private void doEditQuickCheckItem() {
		if (qcData != null) {
			Bundle qrOrigData = new Bundle();
			qrOrigData.putInt(Constants.WHAT_ACTION,
					Constants.QUICK_CHECK_EDIT_EXIST_ACTION);
			qrOrigData
					.putSerializable(Constants.KEY_QUICK_CHECK_DATA, qcData);
			mQuickCheckViewerFragmentListener
					.onQuickCheckEditMenuItemClicked(qrOrigData);
		}
	}

	private void doResolveQuickReportIssue() {
		if (qcData != null) {
			Bundle qrRslvData = new Bundle();
			qrRslvData.putInt(Constants.ORIG_REPORT_ID, qcData.getId());
			qrRslvData.putInt(Constants.WHAT_ACTION,
					Constants.ACTION_MODE_RSLV_NEW_ADDED);
			mQuickCheckViewerFragmentListener
					.onQuickCheckResolveMenuItemClicked(qrRslvData);
		}
	}

	private void doQuickReportRslvListItemClicked(ReportResolutionRspData data) {
		if (data != null) {
			Bundle origData = new Bundle();
			origData.putInt(Constants.WHAT_ACTION,
					Constants.ACTION_MODE_VIEW_RSLV_FROM_LIST);
			origData.putSerializable(Constants.ORIG_RESOLUTION_DATA, data);
			mQuickCheckViewerFragmentListener
					.onQuickReportResolveListItemClicked(origData);
		}
	}

}
