package com.check.v3.mainui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockFragment;
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
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.IssueLevel;
import com.check.v3.data.QuickCheckGetListRspData;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.data.QuickCheckReqFilePartData;
import com.check.v3.data.QuickCheckRspData;
import com.check.v3.data.Session;
import com.check.v3.data.SimpleOrganization;
import com.check.v3.data.User;
import com.check.v3.login.LoginApiConstant;
import com.check.v3.mainui.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
import com.check.v3.mainui.DatePickerDialogFragment.DatePickerDialogFragmentListener;
import com.check.v3.mainui.ListItemDialogFragment.ListItemDialogFragmentListener;
import com.check.v3.preferences.PrefConstant;
import com.check.v3.util.FileHelper;
import com.check.v3.util.UriUtils;
import com.check.v3.widget.CustomGridView;
import com.google.gson.Gson;

public class QuickCheckEditorFragment extends SherlockFragment implements CustomGridView.OnItemClickListener, OnClickListener,
			ListItemDialogFragmentListener, ConfirmationDialogFragmentListener, DatePickerDialogFragmentListener {
	private static String TAG = "QuickCheckEditorFragment";
    public static final String ARG_SELECTED_NUMBER = "menu_selected_position";
    private static final String ARG_REFERENCE = "reference";
    
    QuickCheckEditorFragmentListener mQuickCheckEditorFragmentListener;
    
    private CustomGridView mPhotoGridView;
    AttachmentSimpleAdapter mAttachmentsSimpleAdapter;
    
	private static final int REQUEST_IMAGE_CAPTURE = 2;
	private static final int REQUEST_PHOTO_LIBRARY = 3;
	
	private Context mContext;
	
	private EditText deadLinedatePicker, mIssueDescriptionEditText;
	
	private File mFile;
	private ImageView mPreview;
	
	private Spinner mComposeIssueLevelSpinn, mComposeOrgSpinn, mComposeRspPersonSpinn;
	ArrayAdapter<String> mIssueServSpinnAdapter, mOrgAdapter, mPersonAdapter;
	OrganizationAdapter mOrganizationAdapter;
	PersonSimpleAdapter mPersonSimpleAdapter;
	IssueLevelSimpleAdapter mIssueLevelSimpleAdapter;
	ArrayList<String> mOrgList, mPersonNameList;
	ArrayList<SimpleOrganization> mSimpleOrgList;
	ArrayList<User> mPersonList;
	
	private String mIssueLevelId;
	private int mIssueRspOrgIndex;
	private int mIssueRspPersonIndex;
	private String mIssueDeadLineStr = "";
	private String mIssueDescriptionStr = "";
	
	JSONObject mQuickCheckReqJsonData;
	private JsonObjectRequest mQuickCheckSubmitJasonObjReq;
	private RequestQueue mQueue;
	Gson gson;
	
	private static final int MAX_BITMAP_SIZE = 400;

	private File mImageFile;
	private Uri mImageUri;
    
    ArrayList<HashMap<String, Object>> imageItemsList;
    ArrayList<File> fileListToAdd;
    ArrayList<Integer> fileListToDelete;
    
    QuickCheckRspData qcData;
	
    public static QuickCheckEditorFragment newInstance(QuickCheckRspData reference) {
    	QuickCheckEditorFragment fragment = new QuickCheckEditorFragment();

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
		View rootView = inflater.inflate(R.layout.quick_check_editor_fragment,
				container, false);

		mComposeIssueLevelSpinn = (Spinner) rootView.findViewById(R.id.issue_prio_spinner);
		mComposeOrgSpinn = (Spinner) rootView.findViewById(R.id.issue_rsp_org_spinner);
		mComposeRspPersonSpinn = (Spinner) rootView.findViewById(R.id.issue_rsp_person_spinner);
		Button btn = (Button) rootView.findViewById(R.id.btn);

		deadLinedatePicker = (EditText) rootView.findViewById(R.id.issue_dealine_picker);
		mIssueDescriptionEditText = (EditText) rootView.findViewById(R.id.issue_dscp_edit);
		mPhotoGridView = (CustomGridView) rootView.findViewById(R.id.issue_editor_photo_list);
	
		mIssueLevelSimpleAdapter = new IssueLevelSimpleAdapter(getActivity(), AccountMngr.getIssueLevelList());
		mComposeIssueLevelSpinn.setAdapter(mIssueLevelSimpleAdapter);
		mComposeIssueLevelSpinn.setOnItemSelectedListener(mIssueLevelSpinnSelectedListener);
		mComposeIssueLevelSpinn.setSelection(0);
		
		mSimpleOrgList = AccountMngr.getSimpleOrgList();
		SimpleOrganization emptySimpleOrgItem = new SimpleOrganization();
		emptySimpleOrgItem.setOrgId(-1);
		emptySimpleOrgItem.setOrgName("");
		mSimpleOrgList.add(0, emptySimpleOrgItem);
		mOrganizationAdapter = new OrganizationAdapter(getActivity(), mSimpleOrgList);
		mComposeOrgSpinn.setAdapter(mOrganizationAdapter);
		mComposeOrgSpinn.setSelection(0);
		mComposeOrgSpinn.setOnItemSelectedListener(mOrgSpinnSelectedListener);		
				
		mPersonList = new ArrayList<User>();
		User emptyUserItem = new User();
		emptyUserItem.setId(-1);
		emptyUserItem.setName("");
		mPersonList.add(0, emptyUserItem);
		mPersonSimpleAdapter = new PersonSimpleAdapter(getActivity(), mPersonList);
		mComposeRspPersonSpinn.setAdapter(mPersonSimpleAdapter);
		mComposeRspPersonSpinn.setOnItemSelectedListener(mIssueRspPersonSpinnSelectedListener);
		

		final Calendar cd = Calendar.getInstance();
		Date date = new Date();
		cd.setTime(date);

		deadLinedatePicker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(R.id.dialog_date_picker);
			}
		});

		btn.setOnClickListener(this);

		imageItemsList = new ArrayList<HashMap<String, Object>>();
		fileListToAdd = new ArrayList<File>();
		fileListToDelete = new ArrayList<Integer>();

		mAttachmentsSimpleAdapter = new AttachmentSimpleAdapter(getActivity(),
				imageItemsList);
		mAttachmentsSimpleAdapter.setDeleteHandler(mAttachmentDeleteHandler);

		mPhotoGridView.setAdapter(mAttachmentsSimpleAdapter);
		mPhotoGridView.setOnItemClickListener(this);
		
		gson = new Gson();	    
	    mQueue = CloudCheckApplication.getInstance().getRequestQueue();
	    
		return rootView;
	}
	
	private void initViewData(){
        Bundle args = getArguments();
        if(args != null){
        	qcData = (QuickCheckRspData) args.getSerializable(ARG_REFERENCE);
        }
        
        if(qcData != null){
        	mIssueDescriptionEditText.setText(qcData.getDescription());
        	deadLinedatePicker.setText(qcData.getDeadline());
        	
        	String issueLevel = qcData.getLevel();
        	ArrayList<IssueLevel> issueLevelList = AccountMngr.getIssueLevelList();
        	for(int i = 0; i < issueLevelList.size(); i++){
        		if(issueLevelList.get(i).getIssueLevelId().equals(issueLevel)){
        			mComposeIssueLevelSpinn.setSelection(i);
        			break;
        		}
        	}
        	
//        	mSimpleOrgList = AccountMngr.getSimpleOrgList();
//    		SimpleOrganization emptySimpleOrgItem = new SimpleOrganization();
//    		emptySimpleOrgItem.setOrgId(-1);
//    		emptySimpleOrgItem.setOrgName("");
//    		mSimpleOrgList.add(0, emptySimpleOrgItem);
        	
        	int rspOrgId = qcData.getOrganizationId();
        	for(int j = 0; j < mSimpleOrgList.size(); j++){
        		if(mSimpleOrgList.get(j).getOrgId() == rspOrgId){
        			mComposeOrgSpinn.setSelection(j);
        			break;
        		}
        	}
        	
        	int rspPersonId = qcData.getResponsiblePeronId();
        	mPersonList.clear();
			User emptyUserItem = new User();
			emptyUserItem.setId(-1);
			emptyUserItem.setName("");
			mPersonList.add(0, emptyUserItem);
			if (rspOrgId != -1) {
				mPersonList.addAll(AccountMngr
						.getPersonListByOrgId(rspOrgId));
			}

			mPersonSimpleAdapter = new PersonSimpleAdapter(getActivity(),
					mPersonList);
			mComposeRspPersonSpinn.setAdapter(mPersonSimpleAdapter);
			mComposeRspPersonSpinn.setSelection(0);
			
			for(int k = 0; k < mPersonList.size(); k++){
        		if(mPersonList.get(k).getId() == rspPersonId){
        			mComposeRspPersonSpinn.setSelection(k);
        			Log.d("Test", "andy debug, k = " + k);
        			break;
        		}
        	}
        }

	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        Log.d(TAG, "onActivityCreated");
        
        if (savedInstanceState != null) {
        	qcData = null;
        } else {
            Bundle args = getArguments();
            if(args != null){
            	qcData = (QuickCheckRspData) args.getSerializable(ARG_REFERENCE);
            }
        }

        initViewData();
    }
  
  @Override
  public void onAttach(Activity activity) {
      super.onAttach(activity);
      
      mContext = activity.getApplicationContext();
      
      try {
    	  mQuickCheckEditorFragmentListener = (QuickCheckEditorFragmentListener) activity;
      } catch (ClassCastException e) {
          throw new ClassCastException(activity.toString() + " must implement OnProcessItemSelectedListener");
      }
  }
  
  
  @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((QuickCheckComposeActivity)getActivity()).getSupportMenuInflater();
		inflater.inflate(R.menu.quick_check_editor_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.qc_edit_action_submit:
//			doCommitQuickCheck();
			__doCommitQuickCheck();
			break;
		case R.id.qc_edit_action_discard:
			onProcessItemOptionSelected();
			break;
		case R.id.qc_edit_action_process:
			onProcessItemOptionSelected();
			testMultiPartJsonAndFile();
			break;			
		default:
			Toast.makeText(this.getActivity(),
					"Menu item " + item.getTitle() + " is selected.",
					Toast.LENGTH_LONG).show();

		}
		return super.onOptionsItemSelected(item);
	}
	
	public void testMultiPartJsonAndFile(){
		
		JSONObject jreq = new JSONObject();
		try {
			jreq.put("name", "ceshi_qiao");
			jreq.put("password", "123456");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File file1 = new File("/mnt/sdcard/checkFileTest/ic_save.jpg");
		File file2 = new File("/mnt/sdcard/checkFileTest/ic_refresh.jpg");
		
		QuickCheckReqFilePartData fileDataItem1 = new QuickCheckReqFilePartData(file1, "image/jpeg");
		QuickCheckReqFilePartData fileDataItem2 = new QuickCheckReqFilePartData(file2, "image/jpeg");
		
		ArrayList<QuickCheckReqFilePartData> fileList = new ArrayList<QuickCheckReqFilePartData>();
		
		fileList.add(fileDataItem1);
		fileList.add(fileDataItem2);
		
		String baseUrl = "http://www.365check.net:8088/check-service/api/v1/";
		String relativeUrl = "organizations/" + AccountMngr.getGlobalOrgId() + "/quick_reports";
		Log.i(TAG, "server request url : " + baseUrl + relativeUrl);
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String rspStr = new String(response);
				Log.d(TAG, "quick report submit response : " + rspStr.toString());			
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {
            	String rspStr = new String(errorResponse);
				Log.d(TAG, "quick report submit error response : " + rspStr.toString());
            }
        };
        CloudCheckApplication.mAsyncHttpClientApi.post(relativeUrl, jreq.toString(), fileList, responseHandler);
	}
	
	private OnItemSelectedListener mIssueLevelSpinnSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {			 
			IssueLevel issueLevel = (IssueLevel)parent.getItemAtPosition(pos);
			mIssueLevelId = issueLevel.getIssueLevelId();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};

	private OnItemSelectedListener mOrgSpinnSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			SimpleOrganization selectedSimpleOrg = (SimpleOrganization) parent
					.getItemAtPosition(pos);
			mIssueRspOrgIndex = selectedSimpleOrg.getOrgId();

			// Set the first item as empty user
			mPersonList.clear();
			User emptyUserItem = new User();
			emptyUserItem.setId(-1);
			emptyUserItem.setName("");
			mPersonList.add(0, emptyUserItem);
			if (mIssueRspOrgIndex != -1) {
				mPersonList.addAll(AccountMngr
						.getPersonListByOrgId(mIssueRspOrgIndex));
			}
			
			mPersonSimpleAdapter.notifyDataSetChanged();
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};
	
	private OnItemSelectedListener mIssueRspPersonSpinnSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			User selectedPerson = (User) parent.getItemAtPosition(pos);
			mIssueRspPersonIndex = selectedPerson.getId();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	};
	
	private AttachmentSimpleAdapter.AttachmentDeleteHandler mAttachmentDeleteHandler = new AttachmentSimpleAdapter.AttachmentDeleteHandler() {
		@Override
		public void doDeleteAttachment(int position) {
			imageItemsList.remove(position);
	        mAttachmentsSimpleAdapter.notifyDataSetChanged();
		}
	};
	
	private void onProcessItemOptionSelected(){
		mQuickCheckEditorFragmentListener.OnProcessItemSelected();
	}
	
	// Container Activity must implement this interface
    public interface QuickCheckEditorFragmentListener {
    	public void OnQuickCheckSubmitSuccess(QuickCheckRspData qcRspData);
        public void OnProcessItemSelected();
    }
    
	public void prepareQuickCheckData(){
		mIssueDescriptionStr = mIssueDescriptionEditText.getText().toString().trim();
		mIssueDeadLineStr = deadLinedatePicker.getText().toString().trim();
		mQuickCheckReqJsonData = new JSONObject();
	    try {
	    	mQuickCheckReqJsonData.put(LoginApiConstant.ISSUE_LEVEL, mIssueLevelId);
	    	mQuickCheckReqJsonData.put(LoginApiConstant.ISSUE_RSP_ORG_ID, mIssueRspOrgIndex);
	    	mQuickCheckReqJsonData.put(LoginApiConstant.ISSUE_RSP_PERSON_ID, mIssueRspPersonIndex);
	    	mQuickCheckReqJsonData.put(LoginApiConstant.ISSUE_DEAD_LINE, mIssueDeadLineStr);
	    	mQuickCheckReqJsonData.put(LoginApiConstant.ISSUE_DESCRIPTION, mIssueDescriptionStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    Log.i(TAG, "server request for compose quick check : " + mQuickCheckReqJsonData.toString());
	}
	
	public void doCommitQuickCheck(){
		prepareQuickCheckData();
		
		String baseUrl = "http://www.365check.net:8088/check-service/api/v1/";
		String API = "organizations/" + AccountMngr.getGlobalOrgId() + "/quick_reports";
		Log.i(TAG, "server request url : " + baseUrl + API);
		
		mQuickCheckSubmitJasonObjReq = new JsonObjectRequest(
				Method.POST,
				baseUrl + API,
				mQuickCheckReqJsonData, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "response : " + response.toString());

						QuickCheckRspData qcRspData = gson.fromJson(response.toString(),
								QuickCheckRspData.class);
						Log.i(TAG, "server response for compose quick check : " + response.toString());
						mQuickCheckEditorFragmentListener.OnQuickCheckSubmitSuccess(qcRspData);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError errInfo) {
						String errorStr = VolleyErrorHelper.getMessage(
								getActivity(), errInfo);
						Log.d(TAG, "error response : " + errorStr);
						
//						mProgressStatusView.dismiss();
					}
				});

		//CloudCheckApplication.getInstance().setCookie();
		mQueue.add(mQuickCheckSubmitJasonObjReq);		
	}
	
	public void _doCommitQuickCheck(){
		prepareQuickCheckData();
		
		String baseUrl = "http://www.365check.net:8088/check-service/api/v1/";
		String relativeUrl = "organizations/" + AccountMngr.getGlobalOrgId() + "/quick_reports";
		Log.i(TAG, "server request url : " + baseUrl + relativeUrl);
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String rspStr = new String(response);
				Log.d(TAG, "quick report submit response : " + rspStr.toString());
				
				QuickCheckRspData qcRspData = gson.fromJson(rspStr.toString(),
						QuickCheckRspData.class);
				Log.i(TAG, "server response for compose quick check : " + rspStr.toString());
				mQuickCheckEditorFragmentListener.OnQuickCheckSubmitSuccess(qcRspData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {

            }
        };
        CloudCheckApplication.mAsyncHttpClientApi.post(relativeUrl, mQuickCheckReqJsonData.toString(), responseHandler);
	}
	
	public void __doCommitQuickCheck(){
		prepareQuickCheckData();
		
//		File file1 = new File("/mnt/sdcard/checkFileTest/ic_save.jpg");
//		File file2 = new File("/mnt/sdcard/checkFileTest/ic_refresh.jpg");
//		
//		QuickCheckReqFilePartData fileDataItem1 = new QuickCheckReqFilePartData(file1, "image/jpeg");
//		QuickCheckReqFilePartData fileDataItem2 = new QuickCheckReqFilePartData(file2, "image/jpeg");
//		
//		ArrayList<QuickCheckReqFilePartData> fileList = new ArrayList<QuickCheckReqFilePartData>();
//		
//		fileList.add(fileDataItem1);
//		fileList.add(fileDataItem2);
		
		ArrayList<QuickCheckReqFilePartData> fileList = new ArrayList<QuickCheckReqFilePartData>();
		for(int i = 0; i < fileListToAdd.size(); i++){
			QuickCheckReqFilePartData fileDataItem = new QuickCheckReqFilePartData(fileListToAdd.get(i), "image/jpeg");
			fileList.add(fileDataItem);
			Log.i(TAG, "file item: " + fileListToAdd.get(i).getName() + ", size = " + fileListToAdd.get(i).getTotalSpace());
			Log.i(TAG, "file item: " + fileListToAdd.get(i).getAbsolutePath());
		}
		
		
		String baseUrl = "http://www.365check.net:8088/check-service/api/v1/";
		String relativeUrl = "organizations/" + AccountMngr.getGlobalOrgId() + "/quick_reports";
		Log.i(TAG, "server request url : " + baseUrl + relativeUrl);
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String rspStr = new String(response);
				Log.d(TAG, "quick report submit response : " + rspStr.toString());
				
				QuickCheckRspData qcRspData = gson.fromJson(rspStr.toString(),
						QuickCheckRspData.class);
				Log.i(TAG, "server response for compose quick check : " + rspStr.toString());
				mQuickCheckEditorFragmentListener.OnQuickCheckSubmitSuccess(qcRspData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {

            }
        };
        CloudCheckApplication.mAsyncHttpClientApi.post(relativeUrl, mQuickCheckReqJsonData.toString(), fileList, responseHandler);
	}	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d("LL", "item is clicked " + arg2);
	}

	@Override
	public void onClick(View v) {		
		showDialog(R.id.dialog_choose_photo_list_item);
	}
	
    private void showDialog(int dialogId) {
        DialogFragment fragment;
        switch (dialogId) {
            case R.id.dialog_confirm_delete: {
                String title = "delete?";
                String message = "Are you sure to delete?";
                String confirmText = "Ok";
                String cancelText = "Cancel";

                fragment = ConfirmationDialogFragment.newInstance(dialogId, title, message,
                        confirmText, cancelText);
                break;
            }
            case R.id.dialog_choose_photo_list_item: {
                String title = "添加图片";
                String cancelText = "取消";
                String[] items = {"选取图片", "现场拍照"};

                fragment = ListItemDialogFragment.newInstance(dialogId, title, items,
                         cancelText);
                break;
            }
			case R.id.dialog_date_picker: {
				fragment = DatePickerDialogFragment.newInstance(dialogId, "选择日期", "");
				break;
			}
//            case R.id.dialog_attachment_progress: {
//                String message = getString(R.string.dialog_attachment_progress_title);
//                fragment = ProgressDialogFragment.newInstance(null, message);
//                break;
//            }
            default: {
                throw new RuntimeException("Called showDialog(int) with unknown dialog id.");
            }
        }

        fragment.setTargetFragment(this, dialogId);
        fragment.show(getFragmentManager(), getDialogTag(dialogId));
    }
	
    private String getDialogTag(int dialogId) {
        return String.format("dialog-%d", dialogId);
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_PHOTO_LIBRARY
				&& resultCode == Activity.RESULT_OK) {
			mImageUri = data.getData();

			Bitmap bitMap = getPic(mImageUri);
			
			HashMap<String, Object> map = new HashMap<String, Object>();  
	        map.put("ItemImage", bitMap); 
	        imageItemsList.add(map);
	        mAttachmentsSimpleAdapter.notifyDataSetChanged();
		} else {
			if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
				Bitmap bitMap = getPic(mImageUri);
				
				HashMap<String, Object> map = new HashMap<String, Object>();  
			    map.put("ItemImage", bitMap); 
			    imageItemsList.add(map);
			    mAttachmentsSimpleAdapter.notifyDataSetChanged();
			}
		}
		
	}
	
	private Bitmap getPic(Uri attachmentUri) {
		mFile = null;
		Bitmap bitMap = null;
		
		Log.d("", "insertLocalAttachment: " + attachmentUri);
		Uri encodedUri = UriUtils.getEncodedUri(attachmentUri);
		Uri decodedUri = UriUtils.getDecodedUri(attachmentUri);

		mImageUri = attachmentUri;
		
//		if(DocumentsContract.isDocumentUri(context, contentUri)){
//		    String wholeID = DocumentsContract.getDocumentId(attachmentUri);
//		    String id = wholeID.split(:)[1];
//		    String[] column = { MediaStore.Images.Media.DATA };
//		    String sel = MediaStore.Images.Media._ID + =?;
//		    Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
//		            sel, new String[] { id }, null);
//		    int columnIndex = cursor.getColumnIndex(column[0]);
//		    if (cursor.moveToFirst()) {
//		        filePath = cursor.getString(columnIndex);
//		    }
//		    cursor.close();
//		}
		if (UriUtils.isContentUri(encodedUri)) {
//			mFile = new File(getRealPathFromURI(mImageUri));
			mFile = new File(getRealPathFromURI(decodedUri));
		} else if(UriUtils.isFileUri(encodedUri)){
//			mFile = new File(mImageUri.getPath());
			mFile = UriUtils.fileFromUri(encodedUri);
		}
		if(mFile != null){
			Log.i(TAG, "add file item: " + mFile.getName() + ", size = " + mFile.getTotalSpace());
			Log.i(TAG, "add file item: " + mFile.getAbsolutePath());
			fileListToAdd.add(mFile);
		}

		bitMap = createThumbnailBitmap(mImageUri, MAX_BITMAP_SIZE);

		return bitMap;
	}
	
	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(proj[0]);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	private String _getRealPathFromURI(Uri decodedUri) {
		Cursor c = null;
		long size = -1;
		String nameFromDB = null;
		
		try {
			c = getActivity().getContentResolver().query(
					decodedUri,
					new String[] { OpenableColumns.DISPLAY_NAME,
							OpenableColumns.SIZE }, null, null, null);
			if (c != null && c.moveToFirst()) {
				nameFromDB = c.getString(0);
				Log.v(TAG, "name from DB: " + nameFromDB);

				try {
					size = c.getLong(1);
					Log.v(TAG, "Size from DB: " + size);
				} catch (NumberFormatException e) {
					
				}
			}
		} catch (Exception e) {
			Log.d(TAG,
					"Failed retrieving filename from _data field for '"
							+ decodedUri + "'", e);
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		
		return nameFromDB;
	}
	
	private String _getPhotoFilename(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddKms");
		return dateFormat.format(date) + ".jpg";
	}
	
	protected void onOpenPhotoLibrary(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
	}
	
	protected void onOpenImageCapture() {
		try {
			// TODO: API < 1.6, images size too small
			String filename = _getPhotoFilename(new Date());
			Log.d(TAG, "Photo filename=" + filename);
			mImageFile = new File(FileHelper.getBasePath(), filename);
			mImageUri = Uri.fromFile(mImageFile);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}	
	
	
	/**
	 * 制作微缩图
	 * 
	 * @param uri
	 * @param size
	 * @return
	 */
	private Bitmap createThumbnailBitmap(Uri uri, int size) {
		InputStream input = null;

		try {
			input = mContext.getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();

			// Compute the scale.
			int scale = 1;
			while ((options.outWidth / scale > size)
					|| (options.outHeight / scale > size)) {
				scale *= 2;
			}

			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;

			input = mContext.getContentResolver().openInputStream(uri);

			return BitmapFactory.decodeStream(input, null, options);
		} catch (IOException e) {
			Log.w("", e);

			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Log.w("", e);
				}
			}
		}
	}

	@Override
	public void datePickerDialogOnDateSet(int dialogId, String dateStr) {
		// TODO Auto-generated method stub
		deadLinedatePicker.setText(dateStr);
        Log.d(TAG, "Selected deadline: " + dateStr); 
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

	@Override
	public void doListItemClick(int dialogId, int item) {
		if(dialogId == R.id.dialog_choose_photo_list_item){
			if(item == 0){
	    		onOpenPhotoLibrary();
	        } else if(item == 1){
	        	onOpenImageCapture();
	        }
		}
	}
    
}
