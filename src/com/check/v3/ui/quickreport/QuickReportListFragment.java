package com.check.v3.ui.quickreport;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.Constants;
import com.check.v3.AsyncHttpExeptionHelper;
import com.check.v3.CloudCheckApplication;
import com.check.v3.CloudCheckApplication.AccountMngr;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.QuickCheckGetListRspData;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.ui.adapter.QuickReportListSimpleAdapter;
import com.check.v3.ui.dialog.ConfirmationDialogFragment;
import com.check.v3.ui.dialog.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
import com.check.v3.utils.CommonHelper;
import com.check.v3.widget.CustomPushToRefreshListView;
import com.check.v3.widget.CustomPushToRefreshListView.OnLoadMoreListener;
import com.check.v3.widget.CustomPushToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class QuickReportListFragment extends SherlockFragment implements
		ConfirmationDialogFragmentListener {
	private static String TAG = "QuickReportListFragment";
	
	LinearLayout mLoadingLayout;
	FrameLayout mListLayout;
	private static final int MSG_SHOW_PROGRESS_DIALOG = 1;
	private static final int MSG_REMOVE_PROGRESS_DIALOG = 2;
	
	private static final int LOAD_DATA_FINISH = 10;
	private static final int REFRESH_DATA_FINISH = 11;
	private static final int NO_MORE_DATA = 12;
	
	private static final int LOAD_MODE_PULL_REFRESH = 1;
	private static final int LOAD_MODE_LOAD_MORE = 2;

	private Gson gson;
	private QuickCheckGetListRspData mQuickCheckGetListRspData;
	
	private QuickReportListSimpleAdapter mQuickReportListSimpleAdapter;
	private CustomPushToRefreshListView mListView;
	private int mTotalPages = 1;
	private int mCurrentPage = 0;
	private int mTotalRecords;
	private int mQuickReportItemCount = 0;
	private String mRelativeUrl;

	String[] random_check_list_arry = {""};
	private ArrayList<String> mQuickCheckDscpList;
	private ArrayList<QuickCheckListItemData> mQuickCheckDataList;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_PROGRESS_DIALOG:
				
				break;
			case MSG_REMOVE_PROGRESS_DIALOG:
				mQuickReportListSimpleAdapter.notifyDataSetChanged();
				mLoadingLayout.setVisibility(View.GONE);
				mListLayout.setVisibility(View.VISIBLE);
				break;
			case REFRESH_DATA_FINISH:
				if (mQuickReportListSimpleAdapter != null) {
					mQuickReportListSimpleAdapter.mList = (ArrayList<QuickCheckListItemData>) msg.obj;
					mQuickReportListSimpleAdapter.notifyDataSetChanged();
				}
				mListView.onRefreshComplete(); // 下拉刷新完成
				break;
			case LOAD_DATA_FINISH:
				if (mQuickReportListSimpleAdapter != null) {
					mQuickReportListSimpleAdapter.mList.addAll((ArrayList<QuickCheckListItemData>) msg.obj);
					mQuickReportListSimpleAdapter.notifyDataSetChanged();
				}
				mListView.onLoadMoreComplete(); // 加载更多完成
				break;	
			case NO_MORE_DATA:
				mListView.onLoadMoreComplete(); // 加载更多完成
			default:
				break;
			}
		}
	};
	
	public QuickReportListFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.quick_report_list_fragment, container, false);
		
		mListView = (CustomPushToRefreshListView) rootView.findViewById(R.id.quick_report_list_view);
		initView();
		
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		gson = new Gson();
		mQuickCheckDscpList = new ArrayList<String>();
		mQuickCheckDscpList.add("");
		
		mQuickCheckDataList = new ArrayList<QuickCheckListItemData>();
	}
	
	private void initView() {
		mQuickReportListSimpleAdapter = new QuickReportListSimpleAdapter(getActivity(), mQuickCheckDataList);
		
		mListView.setAdapter(mQuickReportListSimpleAdapter);
		
		mListView.setCanLoadMore(true);
		mListView.setCanRefresh(true);
		mListView.setAutoLoadMore(false);
		mListView.setMoveToFirstItemAfterRefresh(true);
		mListView.setDoRefreshOnUIChanged(true);

		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO 下拉刷新
				Log.d(TAG, "onRefresh");
				loadData(LOAD_MODE_PULL_REFRESH);
			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO 加载更多
				Log.d(TAG, "onLoad");
				loadData(LOAD_MODE_LOAD_MORE);
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				Log.d(TAG, "click position:" + position);

				QuickCheckListItemData selectedListItemData = (QuickCheckListItemData) parent.getItemAtPosition(position);
				
				Intent intent = new Intent(getActivity(), QuickCheckComposeActivity.class);
				intent.putExtra(Constants.WHAT_ACTION, Constants.QUICK_CHECK_VIEW_ACTION);
				intent.putExtra(Constants.KEY_QUICK_CHECK_DATA, selectedListItemData);
				startActivity(intent);

			}
		});
	}
	
	public void loadData(final int type) {
				final List<QuickCheckListItemData> _List = new ArrayList<QuickCheckListItemData>();
				int orgId = AccountMngr.getGlobalOrgId();
				AsyncHttpResponseHandler responseHandler;
				
				switch (type) {
				case LOAD_MODE_PULL_REFRESH:
					mQuickReportItemCount = 0;
					
					mCurrentPage = 1;
					Log.d(TAG, "req orgId = " + orgId);
					responseHandler = new AsyncHttpResponseHandler() {

			            @Override
			            public void onStart() {

			            }

			            @Override
			            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
			            	String rspStr = new String(response);                
				            Log.d(TAG, "get report response : " + rspStr.toString());
				            	            
				            mQuickCheckGetListRspData = gson.fromJson(rspStr.toString(), QuickCheckGetListRspData.class);
				            
				            ArrayList<QuickCheckListItemData> quickCheckList = mQuickCheckGetListRspData.getQuickReports();
				            mTotalPages = mQuickCheckGetListRspData.getTotalPages();
				            for(int i = 0; i < quickCheckList.size(); i++){
				            	_List.add(i, quickCheckList.get(i));
				            	mQuickReportItemCount++;
				            }
				            Message _Msg = mHandler.obtainMessage(REFRESH_DATA_FINISH,
									_List);
							mHandler.sendMessage(_Msg);
			            }

			            @Override
			            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {
			            	mTotalPages = 1;
			            	mCurrentPage = 0;
			            	Log.d(TAG, "pull to refresh failed, error rsp = " + e.toString());
				            Message _Msg = mHandler.obtainMessage(REFRESH_DATA_FINISH,
												_List);
							mHandler.sendMessage(_Msg);
							
							String errorStr = AsyncHttpExeptionHelper.getMessage(getActivity(), e, errorResponse, statusCode);
			            	CommonHelper.notify(getActivity(), errorStr);
			            }
			        };
			        CloudCheckApplication.mAsyncHttpClientApi.get("organizations/" + orgId + "/quick_reports?page=" + mCurrentPage, null, responseHandler);

					break;

				case LOAD_MODE_LOAD_MORE:					
					if(mCurrentPage < mTotalPages){
						mCurrentPage++;
					}else{
						Message _Msg = mHandler.obtainMessage(NO_MORE_DATA,
								null);
						mHandler.sendMessage(_Msg);
						return;
					}
					
					Log.d(TAG, "req orgId = " + orgId);
					responseHandler = new AsyncHttpResponseHandler() {

			            @Override
			            public void onStart() {

			            }

			            @Override
			            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
			            	String rspStr = new String(response);                
				            Log.d(TAG, "get report response : " + rspStr.toString());
				            	            
				            mQuickCheckGetListRspData = gson.fromJson(rspStr.toString(), QuickCheckGetListRspData.class);
				            
				            ArrayList<QuickCheckListItemData> quickCheckList = mQuickCheckGetListRspData.getQuickReports();
				            mTotalPages = mQuickCheckGetListRspData.getTotalPages();
				            for(int i = 0; i < quickCheckList.size(); i++){
				            	_List.add(i, quickCheckList.get(i));
				            	mQuickReportItemCount++;
				            }
				            Message _Msg = mHandler.obtainMessage(LOAD_DATA_FINISH,
									_List);
							mHandler.sendMessage(_Msg);
			            }

			            @Override
			            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {
			            	if(mCurrentPage > 0){
			            		mCurrentPage--;
			            	}
			            	
			            	Log.d(TAG, "pull to refresh failed, error rsp = " + e.toString());
			            	Message _Msg = mHandler.obtainMessage(LOAD_DATA_FINISH,
									_List);
							mHandler.sendMessage(_Msg);
							
							String errorStr = AsyncHttpExeptionHelper.getMessage(getActivity(), e, errorResponse, statusCode);
			            	CommonHelper.notify(getActivity(), errorStr);
			            }
			        };
			        CloudCheckApplication.mAsyncHttpClientApi.get("organizations/" + orgId + "/quick_reports?page=" + mCurrentPage, null, responseHandler);
			        
					break;
				}
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
//		mLoadingLayout = (LinearLayout) this.getActivity()
//				.findViewById(R.id.progress_container_id);
//		mListLayout = (FrameLayout) this.getActivity()
//				.findViewById(R.id.list_container_id);
//
//		doGetQuickCheckListInfo();
		
	}
	
	public void doGetQuickCheckListInfo() {
		int page = 1;
		int orgId = AccountMngr.getGlobalOrgId();
		Log.d(TAG, "req orgId = " + orgId);

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String rspStr = new String(response);                
	            Log.d(TAG, "get report response : " + rspStr.toString());
	            	            
	            mQuickCheckGetListRspData = gson.fromJson(rspStr.toString(), QuickCheckGetListRspData.class);
	            
	            ArrayList<QuickCheckListItemData> quickCheckList = mQuickCheckGetListRspData.getQuickReports();
	            for(int i = 0; i < quickCheckList.size(); i++){
	            	mQuickCheckDscpList.add(i, quickCheckList.get(i).getDescription());
	            	mQuickCheckDataList.add(i, quickCheckList.get(i));
	            }
	            mHandler.obtainMessage(MSG_REMOVE_PROGRESS_DIALOG).sendToTarget();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {

            }
        };
        CloudCheckApplication.mAsyncHttpClientApi.get("organizations/" + orgId + "/quick_reports?page=" + page, null, responseHandler);
	}	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((SherlockFragmentActivity) getActivity())
				.getSupportMenuInflater();
		inflater.inflate(R.menu.quick_report_list_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			Toast.makeText(this.getActivity(),
					"Menu item " + item.getTitle() + " is selected.",
					Toast.LENGTH_LONG).show();
			return super.onOptionsItemSelected(item);
		}
	}

	private void showDialog(int dialogId) {
		DialogFragment fragment;
		switch (dialogId) {// dialog_date_picker
		case R.id.dialog_confirm_delete: {
			String title = "delete?";
			String message = "Are you sure to delete?";
			String confirmText = "Ok";
			String cancelText = "Cancel";

			fragment = ConfirmationDialogFragment.newInstance(dialogId, title,
					message, confirmText, cancelText);
			break;
		}
		case R.id.dialog_confirm_spam: {
			String title = "Spam?";
			String message = "Spam?";
			String confirmText = "Ok";
			String cancelText = "Cancel";

			fragment = ConfirmationDialogFragment.newInstance(dialogId, title,
					message, confirmText, cancelText);
			break;
		}
		default: {
			throw new RuntimeException(
					"Called showDialog(int) with unknown dialog id.");
		}
		}

		fragment.setTargetFragment(this, dialogId);
		fragment.show(getFragmentManager(), getDialogTag(dialogId));
	}

	private void removeDialog(int dialogId) {
		FragmentManager fm = getFragmentManager();

		if (fm == null || isRemoving() || isDetached()) {
			return;
		}

		// Make sure the "show dialog" transaction has been processed when we
		// call
		// findFragmentByTag() below. Otherwise the fragment won't be found and
		// the dialog will
		// never be dismissed.
		fm.executePendingTransactions();

		DialogFragment fragment = (DialogFragment) fm
				.findFragmentByTag(getDialogTag(dialogId));

		if (fragment != null) {
			fragment.dismiss();
		}
	}

	private String getDialogTag(int dialogId) {
		return String.format("dialog-%d", dialogId);
	}

	@Override
	public void doPositiveClick(int dialogId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doNegativeClick(int dialogId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dialogCancelled(int dialogId) {
		// TODO Auto-generated method stub

	}

}