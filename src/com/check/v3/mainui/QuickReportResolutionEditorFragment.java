package com.check.v3.mainui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.AsyncHttpExeptionHelper;
import com.check.v3.CloudCheckApplication;
import com.check.v3.api.ApiConstant;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.FilePartData;
import com.check.v3.data.ImageItemData;
import com.check.v3.data.JsonParamsPart;
import com.check.v3.data.PhotoInfoData;
import com.check.v3.data.ReportResolutionReqData;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.mainui.ListItemDialogFragment.ListItemDialogFragmentListener;
import com.check.v3.util.CommonHelper;
import com.check.v3.util.FileHelper;
import com.check.v3.util.FragmentDialogUtil;
import com.check.v3.widget.CustomGridView;
import com.google.gson.Gson;

public class QuickReportResolutionEditorFragment extends SherlockFragment implements 
CustomGridView.OnItemClickListener, OnClickListener,ListItemDialogFragmentListener{
	private static final String TAG = "QuickReportResolutionEditorFragment";
	
	private static final int NORMAL_BITMAP_SIZE = 512;
	
	private Context mContext;
	QuickReportResolutionEditorFragmentListener mQuickReportResolutionEditorFragmentListener;

	private int mActionMode = ApiConstant.ACTION_MODE_RSLV_NEW_ADDED;
	
	private EditText mIssueRslvDscptEditView;
	private String mIssueRslvDescriptionStr;
	
	private int mOrigReportId = 0;
	private int mOrigRslvId = 0;
	private ReportResolutionReqData mQuickReportResolutionReqData;
	private ReportResolutionRspData mQuickReportResolutionRsqData;
	
	Gson gson;
	
	private CustomGridView mInsertPhotoGridView;
	private InsertPhotoGridViewSimpleAdapter mInsertPhotoGridViewSimpleAdapter;

	private LinearLayout mDeletePhotoViewContainer;
	private CustomGridView mDeletePhotoGridView;
	private PhotoGridViewSimpleAdapter mPhotoGridViewSimpleAdapter;
	
	private File mImageFile;
	private Uri mImageUri;
	
	private Button mInsertPhotBbtn;
	ArrayList<Integer> mImageNeedToDeletedList;
	ArrayList<ImageItemData> mOrigImageList;
	ArrayList<PhotoInfoData> mPhotoListToAdd;
	ArrayList<Integer> mFileListToDelete;

	private static final int RSLV_REQUEST_IMAGE_CAPTURE = 5;
	private static final int RSLV_REQUEST_PHOTO_LIBRARY = 6;
	
	private Fragment mFragCtx;

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
		mFragCtx = this;
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
	  public void onAttach(Activity activity) {
	      super.onAttach(activity);
	      
	      mContext = activity.getApplicationContext();
	      
	      try {
	    	  mQuickReportResolutionEditorFragmentListener = (QuickReportResolutionEditorFragmentListener) activity;
	      } catch (ClassCastException e) {
	          throw new ClassCastException(activity.toString() + " must implement QuickReportResolutionEditorFragmentListener");
	      }
	  }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// find view and set adapter
		super.onActivityCreated(savedInstanceState);

		initView();
		
		initViewData();
	}
	
	private void initView(){
		mIssueRslvDscptEditView = (EditText) getView().findViewById(R.id.qr_issue_resolution_dscp_edit);
		
		mInsertPhotBbtn = (Button) getView().findViewById(R.id.qr_issue_resolution_add_pic_btn);
		
		mPhotoListToAdd = new ArrayList<PhotoInfoData>();
		mFileListToDelete = new ArrayList<Integer>();
		
		mInsertPhotoGridView = (CustomGridView) getView().findViewById(
				R.id.qr_issue_resolution_editor_photo_list);
		
		mDeletePhotoViewContainer = (LinearLayout) getView().findViewById(
				R.id.qr_issue_resolution_container);
		
		mDeletePhotoGridView = (CustomGridView) getView().findViewById(
				R.id.qr_issue_resolution_editor_delete_photo_list);
		
		mInsertPhotBbtn.setOnClickListener(this);
		
		mInsertPhotoGridViewSimpleAdapter = new InsertPhotoGridViewSimpleAdapter(getActivity(),
				mPhotoListToAdd);
		mInsertPhotoGridViewSimpleAdapter.setDeleteHandler(mInsertPhotoDeleteHandler);
		mInsertPhotoGridView.setAdapter(mInsertPhotoGridViewSimpleAdapter);
		mInsertPhotoGridView.setOnItemClickListener(this);
		
		
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
        		
    			if (mQuickReportResolutionRsqData.getImages() != null && 
    					mQuickReportResolutionRsqData.getImages().size() > 0) {
    				mDeletePhotoViewContainer.setVisibility(View.VISIBLE);
    				mImageNeedToDeletedList = new ArrayList<Integer>();
    				mOrigImageList = mQuickReportResolutionRsqData.getImages();
    				mPhotoGridViewSimpleAdapter = new PhotoGridViewSimpleAdapter(
    						getActivity(), mOrigImageList, true);
    				mPhotoGridViewSimpleAdapter
    						.setDeleteHandler(mDeleteAttachmentDeleteHandler);

    				mDeletePhotoGridView.setAdapter(mPhotoGridViewSimpleAdapter);
    				mDeletePhotoGridView.setOnItemClickListener(this);
    			} else {
    				mDeletePhotoViewContainer.setVisibility(View.GONE);
    			}
        	}
        }
	}
	
	private InsertPhotoGridViewSimpleAdapter.AttachmentDeleteHandler mInsertPhotoDeleteHandler = new InsertPhotoGridViewSimpleAdapter.AttachmentDeleteHandler() {
		@Override
		public void doDeleteAttachment(int position) {
			mPhotoListToAdd.remove(position);
			mInsertPhotoGridViewSimpleAdapter.notifyDataSetChanged();
		}
	};

	private PhotoGridViewSimpleAdapter.AttachmentDeleteHandler mDeleteAttachmentDeleteHandler = new PhotoGridViewSimpleAdapter.AttachmentDeleteHandler() {
		@Override
		public void doDeleteAttachment(int position) {
			Log.d(TAG, "delete pos = " + position + "id = "
					+ mOrigImageList.get(position).getId());
			mImageNeedToDeletedList.add((Integer) mOrigImageList.get(position)
					.getId());
			mOrigImageList.remove(position);
			mPhotoGridViewSimpleAdapter.notifyDataSetChanged();
		}
	};

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
    	
    	if (mImageNeedToDeletedList != null
				&& mImageNeedToDeletedList.size() > 0) {
    		mQuickReportResolutionReqData.setNeededdeleteImagesId(mImageNeedToDeletedList);
		}
    }
    
	private void doCommitQuickReportResolution() {
		prepareQuickReportResolutionData();		
		
		ArrayList<FilePartData> fileList = new ArrayList<FilePartData>();
		for (int i = 0; i < mPhotoListToAdd.size(); i++) {
			FilePartData fileDataItem = new FilePartData(
					ApiConstant.QUICK_REPORT_RSLV_FILE_PART_KEY,
					ApiConstant.IMAGE_JPEG_CONTENT_TYPE, mPhotoListToAdd.get(i).getPhotoFile());
			fileList.add(fileDataItem);
			Log.i(TAG, "file item: " + mPhotoListToAdd.get(i).getPhotoFile().getName()
					+ ", size = " + mPhotoListToAdd.get(i).getPhotoFile().length());
		}
		
		String relativeUrl = "";
		if(mActionMode == ApiConstant.ACTION_MODE_RSLV_NEW_ADDED  && mOrigReportId > 0){
			relativeUrl = "quick_reports/" + mOrigReportId + "/quick_report_resolves";
		} else if(mActionMode == ApiConstant.ACTION_MODE_RSLV_EDIT_EXIST && mOrigRslvId > 0){
			relativeUrl = "quick_report_resolves/" + mOrigRslvId;
		}
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            	FragmentDialogUtil.showDialog(mFragCtx, R.id.dialog_show_progress);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	FragmentDialogUtil.removeDialog(mFragCtx, R.id.dialog_show_progress);
            	
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
            	FragmentDialogUtil.removeDialog(mFragCtx, R.id.dialog_show_progress);
            	
            	String errorStr = AsyncHttpExeptionHelper.getMessage(getActivity(), e, errorResponse, statusCode);
            	CommonHelper.notify(getActivity(), errorStr);
            }
        };
        String jsonReqStr = gson.toJson(mQuickReportResolutionReqData);
        
        JsonParamsPart jsonPart = new JsonParamsPart(ApiConstant.QUICK_REPORT_RSLV_JSON_PART_KEY, 
        		ApiConstant.JSON_CONTENT_TYPE, jsonReqStr);
        
        CloudCheckApplication.mAsyncHttpClientApi.post(relativeUrl, jsonPart, fileList, responseHandler);
	}
	
	protected void onOpenPhotoLibrary() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, RSLV_REQUEST_PHOTO_LIBRARY);
	}

	protected void onOpenImageCapture() {
		try {
			String filename = CommonHelper.getPhotoFilename(new Date());
			Log.d(TAG, "Photo filename=" + filename);
			mImageFile = new File(FileHelper.getBasePath(), filename);
			mImageUri = Uri.fromFile(mImageFile);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			startActivityForResult(intent, RSLV_REQUEST_IMAGE_CAPTURE);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RSLV_REQUEST_PHOTO_LIBRARY
				&& resultCode == Activity.RESULT_OK) {
			mImageUri = data.getData();

			PhotoInfoData photoInfoData = CommonHelper.getPhotoFromUri(mContext, mImageUri, NORMAL_BITMAP_SIZE);

			mPhotoListToAdd.add(photoInfoData);
			mInsertPhotoGridViewSimpleAdapter.notifyDataSetChanged();
		} else {
			if (requestCode == RSLV_REQUEST_IMAGE_CAPTURE
					&& resultCode == Activity.RESULT_OK) {
				PhotoInfoData photoInfoData = CommonHelper.getPhotoFromUri(mContext, mImageUri, NORMAL_BITMAP_SIZE);
				mPhotoListToAdd.add(photoInfoData);
				mInsertPhotoGridViewSimpleAdapter.notifyDataSetChanged();
			}
		}

	}

	private void doDiscardQuickReportResolution() {

	}

	@Override
	public void doNegativeClick(int dialogId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dialogCancelled(int dialogId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doListItemClick(int dialogId, int item) {
		if (dialogId == R.id.dialog_choose_photo_list_item) {
			if (item == 0) {
				onOpenPhotoLibrary();
			} else if (item == 1) {
				onOpenImageCapture();
			}
		}
		
	}

	@Override
	public void onClick(View arg0) {
		FragmentDialogUtil.showDialog(this, R.id.dialog_choose_photo_list_item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d("LL", "item is clicked " + arg2);
	}

}
