package com.check.v3.mainui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
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
import com.check.v3.data.Organization;
import com.check.v3.data.OrganizationsByAccount;
import com.check.v3.data.QuickCheckGetListRspData;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.mainui.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class QuickCheckListFragment extends SherlockListFragment implements
		ConfirmationDialogFragmentListener {
	private static String TAG = "QuickCheckListFragment";
	
	LinearLayout mLoadingLayout;
	FrameLayout mListLayout;
	private static final int MSG_SHOW_PROGRESS_DIALOG = 1;
	private static final int MSG_REMOVE_PROGRESS_DIALOG = 2;

	private RequestQueue mQueue;
	Gson gson;
	private JsonObjectRequest mGetQuickCheckListJasonObjReq;
	private QuickCheckGetListRspData mQuickCheckGetListRspData;
	
	private QuickCheckListSimpleAdapter mQuickCheckListSimpleAdapter;

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
				mQuickCheckListSimpleAdapter.notifyDataSetChanged();
				mLoadingLayout.setVisibility(View.GONE);
				mListLayout.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};
	
	public QuickCheckListFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.quick_check_list_fragment, container,
				false);
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
		QuickCheckListItemData qcItemData = new QuickCheckListItemData();
		qcItemData.setId(10);
		qcItemData.setSubmitterId(15);
		qcItemData.setSubmitterName("Andy Qiao");
		qcItemData.setResponsiblePeronId(20);
		qcItemData.setResponsiblePersonName("Andy Qiao");
		qcItemData.setOrganizationId(100);
		qcItemData.setOrganizationName("总务处");
		qcItemData.setDeadline("2013-12-15");
		qcItemData.setState("OPENED");
		qcItemData.setLevel("HIGH");
		qcItemData.setDescription("这个消防问题非常严重，请各部门协调及时予以解决");
		mQuickCheckDataList.add(qcItemData);

		mQuickCheckListSimpleAdapter = new QuickCheckListSimpleAdapter(getActivity(), mQuickCheckDataList);
		setListAdapter(mQuickCheckListSimpleAdapter);
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
		mLoadingLayout = (LinearLayout) this.getActivity()
				.findViewById(R.id.progress_container_id);
		mListLayout = (FrameLayout) this.getActivity()
				.findViewById(R.id.list_container_id);

		doGetQuickCheckListInfo();
		
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
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Toast.makeText(
				getActivity(),
				"You have selected " + position
						+ " in list fragment.", Toast.LENGTH_SHORT).show();
		// showDialog(R.id.dialog_confirm_delete);
		
		QuickCheckListItemData selectedListItemData = (QuickCheckListItemData) parent.getItemAtPosition(position);
		
		Intent intent = new Intent(getActivity(), QuickCheckComposeActivity.class);
		intent.putExtra(ApiConstant.KEY_QUICK_CHECK_ACTION, ApiConstant.QUICK_CHECK_VIEW_ACTION);
		intent.putExtra(ApiConstant.KEY_QUICK_CHECK_DATA, selectedListItemData);
		startActivity(intent);
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