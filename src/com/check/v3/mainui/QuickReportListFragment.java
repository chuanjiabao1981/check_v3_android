package com.check.v3.mainui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.check.client.R;
import com.check.v3.CloudCheckApplication;
import com.check.v3.VolleyErrorHelper;
import com.check.v3.CloudCheckApplication.AccountMngr;
import com.check.v3.api.ApiConstant;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.QuickCheckGetListRspData;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.mainui.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
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

	private RequestQueue mQueue;
	Gson gson;
	private JsonObjectRequest mGetQuickCheckListJasonObjReq;
	private QuickCheckGetListRspData mQuickCheckGetListRspData;
	
	private QuickReportListSimpleAdapter mQuickReportListSimpleAdapter;
	private CustomPushToRefreshListView mListView;
	private int mTotalPages = 1;
	private int mCurrentPage = 0;
	private int mTotalRecords;
	private int mQuickReportItemCount = 0;

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
		mQueue = CloudCheckApplication.getInstance().getRequestQueue();
		mQuickCheckDscpList = new ArrayList<String>();
		mQuickCheckDscpList.add("");
		
		mQuickCheckDataList = new ArrayList<QuickCheckListItemData>();
//		QuickCheckListItemData qcItemData = new QuickCheckListItemData();
//		qcItemData.setId(10);
//		qcItemData.setSubmitterId(15);
//		qcItemData.setSubmitterName("Andy Qiao");
//		qcItemData.setResponsiblePeronId(20);
//		qcItemData.setResponsiblePersonName("Andy Qiao");
//		qcItemData.setOrganizationId(100);
//		qcItemData.setOrganizationName("总务处");
//		qcItemData.setDeadline("2013-12-15");
//		qcItemData.setState("OPENED");
//		qcItemData.setLevel("HIGH");
//		qcItemData.setDescription("这个消防问题非常严重，请各部门协调及时予以解决");
//		mQuickCheckDataList.add(qcItemData);
		
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
				Log.e(TAG, "onRefresh");
				loadData(0);
			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO 加载更多
				Log.e(TAG, "onLoad");
				loadData(1);
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				Log.e(TAG, "click position:" + position);
				Toast.makeText(
						getActivity(),
						"You have selected " + position
								+ " in list fragment.", Toast.LENGTH_SHORT).show();
				QuickCheckListItemData selectedListItemData = (QuickCheckListItemData) parent.getItemAtPosition(position);
				
				Intent intent = new Intent(getActivity(), QuickCheckComposeActivity.class);
				intent.putExtra(ApiConstant.KEY_QUICK_CHECK_ACTION, ApiConstant.QUICK_CHECK_VIEW_ACTION);
				intent.putExtra(ApiConstant.KEY_QUICK_CHECK_DATA, selectedListItemData);
				startActivity(intent);

			}
		});
	}
	
	public void loadData(final int type) {
				final List<QuickCheckListItemData> _List = new ArrayList<QuickCheckListItemData>();
				int orgId = AccountMngr.getGlobalOrgId();
				AsyncHttpResponseHandler responseHandler;
				
				switch (type) {
				case 0:
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
			            }
			        };
			        CloudCheckApplication.mAsyncHttpClientApi.get("organizations/" + orgId + "/quick_reports?page=" + mCurrentPage, null, responseHandler);

					break;

				case 1:					
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
	
//	public void doGetQuickCheckListInfo() {
//		int page = 1;
//		int orgId = AccountMngr.getGlobalOrgId();
//		Log.d(TAG, "req orgId = " + orgId);
//		mGetQuickCheckListJasonObjReq = builderGetListReq(orgId, page);
//		mQueue.add(mGetQuickCheckListJasonObjReq);
//	}
	
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
	
	
	private JsonObjectRequest builderGetListReq(int orgId, int page){
		JsonObjectRequest getReq = new JsonObjectRequest(Method.GET, 
				"http://www.365check.net:8088/check-service/api/v1/organizations/" + orgId + "/quick_reports?page=" + page, null,
	            new Response.Listener<JSONObject>() {
	        @Override
	        public void onResponse(JSONObject response) {
	            Log.d(TAG, "get report response : " + response.toString());
	            
	            
	            mQuickCheckGetListRspData = gson.fromJson(response.toString(), QuickCheckGetListRspData.class);
	            
	            ArrayList<QuickCheckListItemData> quickCheckList = mQuickCheckGetListRspData.getQuickReports();
	            for(int i = 0; i < quickCheckList.size(); i++){
	            	mQuickCheckDscpList.add(i, quickCheckList.get(i).getDescription());
	            	mQuickCheckDataList.add(i, quickCheckList.get(i));
	            }
	            mHandler.obtainMessage(MSG_REMOVE_PROGRESS_DIALOG).sendToTarget();        
	    }
	    }, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError errInfo) {
				String errorStr = VolleyErrorHelper.getMessage(getActivity().getApplicationContext(), errInfo);
				Log.d(TAG, "error response : " + errorStr);
			}
		});
		
		return getReq;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((SherlockFragmentActivity) getActivity())
				.getSupportMenuInflater();
		inflater.inflate(R.menu.random_check_list_fragment_menu, menu);
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