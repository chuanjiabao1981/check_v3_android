package com.check.v3.mainui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.check.client.R;
import com.check.v3.ApplicationController.OrgMngr;
import com.check.v3.util.FileHelper;
import com.check.v3.util.UriUtils;
import com.check.v3.widget.CustomGridView;

public class QuickCheckComposeActivity extends SherlockFragmentActivity implements CustomGridView.OnItemClickListener, OnClickListener{
	private static String TAG = "RandomCheckComposeFragment";
    public static final String ARG_SELECTED_NUMBER = "menu_selected_position";
    private CustomGridView mOrgListView;
    AttachmentSimpleAdapter mAttachmentsSimpleAdapter;
    
	private static final int REQUEST_IMAGE_CAPTURE = 2;
	private static final int REQUEST_PHOTO_LIBRARY = 3;
	
	private static final int DATE_PICKER_ID = 1;
	private static final int ATTACH_PICKER_ID = 2;
	
	private EditText deadLinedatePicker;
	
	private File mFile;
	private ImageView mPreview;
	
	private Spinner mComposeServSpinn, mComposeOrgSpinn, mComposeRspPersonSpinn;
	ArrayAdapter<String> mOrgAdapter, mPersonAdapter;
	ArrayList<String> mOrgList;
	
	private static final int MAX_BITMAP_SIZE = 400;

	private File mImageFile;
	private Uri mImageUri;

    String[] random_check_compose_prio_arry = {  
            "高",  
            "中",  
            "低"  
       };
    
    ArrayList<HashMap<String, Object>> imageItemsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(true);

		setContentView(R.layout.quick_check_compose_fragment);

		mComposeServSpinn = (Spinner) findViewById(R.id.issue_prio_spinner);
		mComposeOrgSpinn = (Spinner) findViewById(R.id.issue_rsp_org_spinner);
		mComposeRspPersonSpinn = (Spinner) findViewById(R.id.issue_rsp_person_spinner);

		mOrgList = OrgMngr.getOrgList();
		mOrgAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mOrgList);

		mOrgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mComposeOrgSpinn.setAdapter(mOrgAdapter);
		mComposeOrgSpinn.setOnItemSelectedListener(mOrgSpinnSelectedListener);
		
		mComposeRspPersonSpinn.setAdapter(mPersonAdapter);

		Button btn = (Button) findViewById(R.id.btn);

		deadLinedatePicker = (EditText) findViewById(R.id.issue_dealine_picker);

		final Calendar cd = Calendar.getInstance();
		Date date = new Date();
		cd.setTime(date);

		deadLinedatePicker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_PICKER_ID);
			}
		});

		btn.setOnClickListener(this);

		mOrgListView = (CustomGridView) findViewById(R.id.org_list);

		imageItemsList = new ArrayList<HashMap<String, Object>>();

		mAttachmentsSimpleAdapter = new AttachmentSimpleAdapter(this,
				imageItemsList);
		mAttachmentsSimpleAdapter.setDeleteHandler(mAttachmentDeleteHandler);

		mOrgListView.setAdapter(mAttachmentsSimpleAdapter);
		mOrgListView.setOnItemClickListener(this);

		this.setTitle("新建走动巡查");

	}
	
	private OnItemSelectedListener mOrgSpinnSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			 
			 String selectedOrgName = mOrgList.get(pos);
			 
			 ArrayList<String> mPersonList = OrgMngr.getPersonListByOrgName(selectedOrgName);
			 mPersonAdapter = new ArrayAdapter<String>(QuickCheckComposeActivity.this,
			   android.R.layout.simple_spinner_item, mPersonList);
			 mPersonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 mComposeRspPersonSpinn.setAdapter(mPersonAdapter);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
        switch(id){
        case DATE_PICKER_ID:  
        	Calendar cd = Calendar.getInstance();
            Date date = new Date();
            cd.setTime(date);
            dialog = new DatePickerDialog(this, onDateSetListener,cd.get(Calendar.YEAR),cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH));
            break;
        case ATTACH_PICKER_ID:
        	final CharSequence[] items = {"选取图片", "现场拍照"}; 
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("选择图片");
        	builder.setItems(items, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
        	        if(item == 0){
        	    		onOpenPhotoLibrary();
        	        } else if(item == 1){
        	        	onOpenImageCapture();
        	        }
        	    }
        	});
        	dialog = builder.create();
        	break;
        }  
          
        return dialog;  
    }
    
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {  
        public void onDateSet(DatePicker view, int year, int monthOfYear,  
                int dayOfMonth) {
        	deadLinedatePicker.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
            Log.d("test", ""+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");  
        }  
    };    
    
	private AttachmentSimpleAdapter.AttachmentDeleteHandler mAttachmentDeleteHandler = new AttachmentSimpleAdapter.AttachmentDeleteHandler() {
		@Override
		public void doDeleteAttachment(int position) {
			imageItemsList.remove(position);
	        mAttachmentsSimpleAdapter.notifyDataSetChanged();
		}
	};    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = ((SherlockFragmentActivity)this).getSupportMenuInflater();
		inflater.inflate(R.menu.random_check_compose_fragment_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch(item.getItemId()) {
		default:
			Toast.makeText(this, "Menu item " + item.getTitle() + " is selected.", Toast.LENGTH_LONG).show();
		return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null) {
	        return;
	    }
	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0) {
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
	        }
	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d("LL", "item is clicked " + arg2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("image/*");
//		startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
		
		showDialog(ATTACH_PICKER_ID);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_PHOTO_LIBRARY
				&& resultCode == RESULT_OK) {
			mImageUri = data.getData();

			Bitmap bitMap = getPic(mImageUri);
			
			HashMap<String, Object> map = new HashMap<String, Object>();  
	        map.put("ItemImage", bitMap); 
	        imageItemsList.add(map);
	        mAttachmentsSimpleAdapter.notifyDataSetChanged();
		}else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bitmap bitMap = getPic(mImageUri);
			
			HashMap<String, Object> map = new HashMap<String, Object>();  
	        map.put("ItemImage", bitMap); 
	        imageItemsList.add(map);
	        mAttachmentsSimpleAdapter.notifyDataSetChanged();
		}
		
	}
	
	private Bitmap getPic(Uri attachmentUri) {
		mFile = null;
		Bitmap bitMap = null;
		
		Log.d("", "insertLocalAttachment: " + attachmentUri);
		Uri encodedUri = UriUtils.getEncodedUri(attachmentUri);
		Uri decodedUri = UriUtils.getDecodedUri(attachmentUri);

		mImageUri = attachmentUri;
		if (UriUtils.isContentUri(encodedUri)) {
//			mFile = new File(getRealPathFromURI(mImageUri));
			mFile = new File(_getRealPathFromURI(decodedUri));
		} else if(UriUtils.isFileUri(encodedUri)){
//			mFile = new File(mImageUri.getPath());
			mFile = UriUtils.fileFromUri(encodedUri);
		}

		bitMap = createThumbnailBitmap(mImageUri, MAX_BITMAP_SIZE);

		return bitMap;
	}
	
	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	private String _getRealPathFromURI(Uri decodedUri) {
		Cursor c = null;
		long size = -1;
		String nameFromDB = null;
		
		try {
			c = this.getContentResolver().query(
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
			input = this.getContentResolver().openInputStream(uri);
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

			input = this.getContentResolver().openInputStream(uri);

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

}
