package com.check.v3.mainui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.Header;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.AsyncHttpExeptionHelper;
import com.check.v3.CloudCheckApplication;
import com.check.v3.CloudCheckApplication.AccountMngr;
import com.check.v3.api.ApiConstant;
import com.check.v3.asynchttp.AsyncHttpResponseHandler;
import com.check.v3.data.ImageItemData;
import com.check.v3.data.IssueLevel;
import com.check.v3.data.JsonParamsPart;
import com.check.v3.data.PhotoInfoData;
import com.check.v3.data.QuickCheckReqData;
import com.check.v3.data.FilePartData;
import com.check.v3.data.QuickCheckRspData;
import com.check.v3.data.SimpleOrganization;
import com.check.v3.data.User;
import com.check.v3.mainui.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
import com.check.v3.mainui.DatePickerDialogFragment.DatePickerDialogFragmentListener;
import com.check.v3.mainui.ListItemDialogFragment.ListItemDialogFragmentListener;
import com.check.v3.mainui.ProgressDialogFragment.ProgressDialogFragmentListener;
import com.check.v3.util.CommonHelper;
import com.check.v3.util.FileHelper;
import com.check.v3.util.FragmentDialogUtil;
import com.check.v3.widget.CustomGridView;
import com.google.gson.Gson;

public class QuickCheckEditorFragment extends SherlockFragment implements
		CustomGridView.OnItemClickListener, OnClickListener,
		ListItemDialogFragmentListener, ConfirmationDialogFragmentListener,
		DatePickerDialogFragmentListener, ProgressDialogFragmentListener {
	private static String TAG = "QuickCheckEditorFragment";
	public static final String ARG_SELECTED_NUMBER = "menu_selected_position";

	private QuickCheckEditorFragmentListener mQuickCheckEditorFragmentListener;

	private CustomGridView mInsertPhotoGridView;
	private InsertPhotoGridViewSimpleAdapter mInsertPhotoGridViewSimpleAdapter;

	private CustomGridView mDeletePhotoGridView;
	private PhotoGridViewSimpleAdapter mPhotoGridViewSimpleAdapter;

	private static final int REQUEST_IMAGE_CAPTURE = 2;
	private static final int REQUEST_PHOTO_LIBRARY = 3;

	private Context mContext;

	private EditText mDeadlineEditText, mIssueDescriptionEditText;

	private Button mInsertPhotBbtn;
	private Spinner mComposeIssueLevelSpinn, mComposeOrgSpinn,
			mComposeRspPersonSpinn;
	ArrayAdapter<String> mIssueServSpinnAdapter, mOrgAdapter, mPersonAdapter;
	OrganizationAdapter mOrganizationAdapter;
	PersonSimpleAdapter mPersonSimpleAdapter;
	IssueLevelSimpleAdapter mIssueLevelSimpleAdapter;
	ArrayList<String> mOrgList, mPersonNameList;
	ArrayList<SimpleOrganization> mSimpleOrgList;
	ArrayList<User> mPersonList;

	private int mOrigReportId = 0;
	private int mActionMode = ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION;
	private String mIssueLevelId;
	private int mIssueRspOrgIndex;
	private int mIssueRspPersonIndex;
	private String mIssueDeadLineStr = "";
	private String mIssueDescriptionStr = "";

	private QuickCheckReqData mQuickCheckReqData;
	private Gson gson;

	private static final int NORMAL_BITMAP_SIZE = 512;

	private File mImageFile;
	private Uri mImageUri;

	ArrayList<Integer> mImageNeedToDeletedList;
	ArrayList<ImageItemData> mOrigImageList;
	ArrayList<PhotoInfoData> mPhotoListToAdd;
	ArrayList<Integer> mFileListToDelete;

	QuickCheckRspData qcData = null;
	
	private Fragment mFragCtx;

	public static QuickCheckEditorFragment newInstance(Bundle reference) {
		QuickCheckEditorFragment fragment = new QuickCheckEditorFragment();

		fragment.setArguments(reference);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		gson = new Gson();
		
		mFragCtx = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.quick_check_editor_fragment,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.d(TAG, "onActivityCreated");

		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey(ApiConstant.WHAT_ACTION)) {
				mActionMode = args.getInt(ApiConstant.WHAT_ACTION);
			}

			if (mActionMode == ApiConstant.QUICK_CHECK_EDIT_EXIST_ACTION) {
				if (args.containsKey(ApiConstant.KEY_QUICK_CHECK_DATA)) {
					qcData = (QuickCheckRspData) args
							.getSerializable(ApiConstant.KEY_QUICK_CHECK_DATA);
				}
			}
		}

		initView();

		initViewData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mContext = activity.getApplicationContext();

		try {
			mQuickCheckEditorFragmentListener = (QuickCheckEditorFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnProcessItemSelectedListener");
		}
	}

	private void initView() {
		mComposeIssueLevelSpinn = (Spinner) getView().findViewById(
				R.id.issue_prio_spinner);
		mComposeOrgSpinn = (Spinner) getView().findViewById(
				R.id.issue_rsp_org_spinner);
		mComposeRspPersonSpinn = (Spinner) getView().findViewById(
				R.id.issue_rsp_person_spinner);
		mInsertPhotBbtn = (Button) getView().findViewById(R.id.btn);

		mDeadlineEditText = (EditText) getView().findViewById(
				R.id.issue_dealine_picker);
		mIssueDescriptionEditText = (EditText) getView().findViewById(
				R.id.issue_dscp_edit);
		mInsertPhotoGridView = (CustomGridView) getView().findViewById(
				R.id.issue_editor_photo_list);
		mDeletePhotoGridView = (CustomGridView) getView().findViewById(
				R.id.issue_editor_delete_photo_list);

		mIssueLevelSimpleAdapter = new IssueLevelSimpleAdapter(getActivity(),
				AccountMngr.getIssueLevelList());
		mComposeIssueLevelSpinn.setAdapter(mIssueLevelSimpleAdapter);
		mComposeIssueLevelSpinn
				.setOnItemSelectedListener(mIssueLevelSpinnSelectedListener);
		mComposeIssueLevelSpinn.setSelection(0);

		mSimpleOrgList = AccountMngr.getSimpleOrgList();
		SimpleOrganization emptySimpleOrgItem = new SimpleOrganization();
		emptySimpleOrgItem.setOrgId(-1);
		emptySimpleOrgItem.setOrgName("");
		mSimpleOrgList.add(0, emptySimpleOrgItem);
		mOrganizationAdapter = new OrganizationAdapter(getActivity(),
				mSimpleOrgList);
		mComposeOrgSpinn.setAdapter(mOrganizationAdapter);
		mComposeOrgSpinn.setSelection(0);
		mComposeOrgSpinn.setOnItemSelectedListener(mOrgSpinnSelectedListener);

		mPersonList = new ArrayList<User>();
		User emptyUserItem = new User();
		emptyUserItem.setId(-1);
		emptyUserItem.setName("");
		mPersonList.add(0, emptyUserItem);
		mPersonSimpleAdapter = new PersonSimpleAdapter(getActivity(),
				mPersonList);
		mComposeRspPersonSpinn.setAdapter(mPersonSimpleAdapter);
		mComposeRspPersonSpinn
				.setOnItemSelectedListener(mIssueRspPersonSpinnSelectedListener);

		final Calendar cd = Calendar.getInstance();
		Date date = new Date();
		cd.setTime(date);

		mDeadlineEditText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentDialogUtil.showDialog(mFragCtx, R.id.dialog_date_picker);
			}
		});
		mDeadlineEditText.setInputType(InputType.TYPE_NULL);

		mInsertPhotBbtn.setOnClickListener(this);

		mPhotoListToAdd = new ArrayList<PhotoInfoData>();
		mFileListToDelete = new ArrayList<Integer>();
		
		mInsertPhotoGridViewSimpleAdapter = new InsertPhotoGridViewSimpleAdapter(getActivity(),
				mPhotoListToAdd);
		mInsertPhotoGridViewSimpleAdapter.setDeleteHandler(mInsertPhotoDeleteHandler);

		mInsertPhotoGridView.setAdapter(mInsertPhotoGridViewSimpleAdapter);
		mInsertPhotoGridView.setOnItemClickListener(this);
	}

	private void initViewData() {

		if (qcData != null) {
			mOrigReportId = qcData.getId();
			mIssueDescriptionEditText.setText(qcData.getDescription());
			mDeadlineEditText.setText(qcData.getDeadline());

			String issueLevel = qcData.getLevel();
			ArrayList<IssueLevel> issueLevelList = AccountMngr
					.getIssueLevelList();
			for (int i = 0; i < issueLevelList.size(); i++) {
				if (issueLevelList.get(i).getIssueLevelId().equals(issueLevel)) {
					mComposeIssueLevelSpinn.setSelection(i);
					break;
				}
			}

			int rspOrgId = qcData.getOrganizationId();
			for (int j = 0; j < mSimpleOrgList.size(); j++) {
				if (mSimpleOrgList.get(j).getOrgId() == rspOrgId) {
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
				mPersonList.addAll(AccountMngr.getPersonListByOrgId(rspOrgId));
			}

			mPersonSimpleAdapter = new PersonSimpleAdapter(getActivity(),
					mPersonList);
			mComposeRspPersonSpinn.setAdapter(mPersonSimpleAdapter);
			mComposeRspPersonSpinn.setSelection(0);

			for (int k = 0; k < mPersonList.size(); k++) {
				if (mPersonList.get(k).getId() == rspPersonId) {
					mComposeRspPersonSpinn.setSelection(k);
					Log.d("Test", "andy debug, k = " + k);
					break;
				}
			}

			if (qcData.getImages() != null && qcData.getImages().size() > 0) {
				mImageNeedToDeletedList = new ArrayList<Integer>();
				mOrigImageList = qcData.getImages();
				mPhotoGridViewSimpleAdapter = new PhotoGridViewSimpleAdapter(
						getActivity(), mOrigImageList, true);
				mPhotoGridViewSimpleAdapter
						.setDeleteHandler(mDeleteAttachmentDeleteHandler);

				mDeletePhotoGridView.setAdapter(mPhotoGridViewSimpleAdapter);
				mDeletePhotoGridView.setOnItemClickListener(this);
			} else {
				mDeletePhotoGridView.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((QuickCheckComposeActivity) getActivity())
				.getSupportMenuInflater();
		inflater.inflate(R.menu.quick_check_editor_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.qc_edit_action_submit:
			doCommitQuickCheck();
			break;
		case R.id.qc_edit_action_discard:
			//
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemSelectedListener mIssueLevelSpinnSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			IssueLevel issueLevel = (IssueLevel) parent.getItemAtPosition(pos);
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

	// Container Activity must implement this interface
	public interface QuickCheckEditorFragmentListener {
		public void onQuickCheckSubmitSuccess(Bundle qcRspData);

		public void onProcessItemSelected();
	}

	public void prepareQuickCheckData() {
		mIssueDescriptionStr = mIssueDescriptionEditText.getText().toString()
				.trim();
		mIssueDeadLineStr = mDeadlineEditText.getText().toString().trim();

		mQuickCheckReqData = new QuickCheckReqData();
		mQuickCheckReqData.setLevel(mIssueLevelId);
		mQuickCheckReqData.setOrganizationId(mIssueRspOrgIndex);
		mQuickCheckReqData.setResponsiblePersonId(mIssueRspPersonIndex);
		mQuickCheckReqData.setDeadline(mIssueDeadLineStr);
		mQuickCheckReqData.setDescription(mIssueDescriptionStr);

		if (mImageNeedToDeletedList != null
				&& mImageNeedToDeletedList.size() > 0) {
			mQuickCheckReqData.setNeededdeleteImagesId(mImageNeedToDeletedList);
		}

		Log.i(TAG, "server request for compose quick check : "
				+ mQuickCheckReqData.toString());
	}

	public void doCommitQuickCheck() {
		prepareQuickCheckData();

		ArrayList<FilePartData> fileList = new ArrayList<FilePartData>();
		for (int i = 0; i < mPhotoListToAdd.size(); i++) {
			FilePartData fileDataItem = new FilePartData(
					ApiConstant.QUICK_REPORT_FILE_PART_KEY,
					ApiConstant.IMAGE_JPEG_CONTENT_TYPE, mPhotoListToAdd.get(i).getPhotoFile());
			fileList.add(fileDataItem);
			Log.i(TAG, "file item: " + mPhotoListToAdd.get(i).getPhotoFile().getName()
					+ ", size = " + mPhotoListToAdd.get(i).getPhotoFile().length());
		}

		String relativeUrl = "";
		if (mActionMode == ApiConstant.QUICK_CHECK_EDIT_NEW_ACTION) {
			relativeUrl = "organizations/" + AccountMngr.getGlobalOrgId()
					+ "/quick_reports";
		} else if (mActionMode == ApiConstant.QUICK_CHECK_EDIT_EXIST_ACTION && mOrigReportId > 0) {
			relativeUrl = "quick_reports/" + mOrigReportId;
		}

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				FragmentDialogUtil.showDialog(mFragCtx, R.id.dialog_show_progress);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				FragmentDialogUtil.removeDialog(mFragCtx, R.id.dialog_show_progress);

				String rspStr = new String(response);
				Log.d(TAG,
						"quick report submit response : " + rspStr.toString());

				QuickCheckRspData qcRspData = gson.fromJson(rspStr.toString(),
						QuickCheckRspData.class);
				Log.i(TAG, "server response for compose quick check : "
						+ rspStr.toString());
				Bundle origData = new Bundle();
				origData.putInt(ApiConstant.WHAT_ACTION, ApiConstant.QUICK_CHECK_VIEW_RSP_ACTION);
				origData.putSerializable(ApiConstant.KEY_QUICK_CHECK_DATA, qcRspData);
				mQuickCheckEditorFragmentListener
						.onQuickCheckSubmitSuccess(origData);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {
				FragmentDialogUtil.removeDialog(mFragCtx, R.id.dialog_show_progress);

				String errorStr = AsyncHttpExeptionHelper.getMessage(
						getActivity(), e, errorResponse, statusCode);
				CommonHelper.notify(getActivity(), errorStr);
			}
		};

		String jsonReqStr = gson.toJson(mQuickCheckReqData);

		JsonParamsPart jsonPart = new JsonParamsPart(
				ApiConstant.QUICK_REPORT_JSON_PART_KEY,
				ApiConstant.JSON_CONTENT_TYPE, jsonReqStr);

		CloudCheckApplication.mAsyncHttpClientApi.post(relativeUrl, jsonPart,
				fileList, responseHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d("LL", "item is clicked " + arg2);
	}

	@Override
	public void onClick(View v) {
		FragmentDialogUtil.showDialog(this, R.id.dialog_choose_photo_list_item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_PHOTO_LIBRARY
				&& resultCode == Activity.RESULT_OK) {
			mImageUri = data.getData();

			PhotoInfoData photoInfoData = CommonHelper.getPhotoFromUri(mContext, mImageUri, NORMAL_BITMAP_SIZE);

			mPhotoListToAdd.add(photoInfoData);
			mInsertPhotoGridViewSimpleAdapter.notifyDataSetChanged();
		} else {
			if (requestCode == REQUEST_IMAGE_CAPTURE
					&& resultCode == Activity.RESULT_OK) {
				PhotoInfoData photoInfoData = CommonHelper.getPhotoFromUri(mContext, mImageUri, NORMAL_BITMAP_SIZE);
				mPhotoListToAdd.add(photoInfoData);
				mInsertPhotoGridViewSimpleAdapter.notifyDataSetChanged();
			}
		}

	}

	protected void onOpenPhotoLibrary() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
	}

	protected void onOpenImageCapture() {
		try {
			// TODO: API < 1.6, images size too small
			String filename = CommonHelper.getPhotoFilename(new Date());
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

	@Override
	public void datePickerDialogOnDateSet(int dialogId, String dateStr) {
		// TODO Auto-generated method stub
		mDeadlineEditText.setText(dateStr);
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
		if (dialogId == R.id.dialog_choose_photo_list_item) {
			if (item == 0) {
				onOpenPhotoLibrary();
			} else if (item == 1) {
				onOpenImageCapture();
			}
		}
	}

}
