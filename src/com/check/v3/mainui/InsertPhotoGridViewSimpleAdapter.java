package com.check.v3.mainui;

import java.util.ArrayList;
import java.util.HashMap;

import com.check.client.R;
import com.check.v3.data.PhotoInfoData;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class InsertPhotoGridViewSimpleAdapter extends BaseAdapter {
	private Context context;
	
	private AttachmentDeleteHandler mDeleteHandler;

	private LayoutInflater layoutInflater;

	private ArrayList<PhotoInfoData> mPhotoList;

	public InsertPhotoGridViewSimpleAdapter(Context context,
			ArrayList<PhotoInfoData> photoList) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.mPhotoList = photoList;
	}

	public int getCount() {
		return this.mPhotoList != null ? this.mPhotoList.size() : 0;
	}

	public Object getItem(int position) {
		return this.mPhotoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.compose_attachment_list_item, null);
		}

		ImageView preview = (ImageView) convertView.findViewById(R.id.preview);
		Bitmap thumbNail = (Bitmap) mPhotoList.get(position).getPhotoBitmap();
		preview.setImageBitmap(thumbNail);
		
		Button removeBtn = (Button) convertView.findViewById(R.id.delete_preview_btn);
		removeBtn.setOnClickListener(new AttachmentRemoveOnClickListener(position));

		return convertView;
	}
	
	public void setDeleteHandler(AttachmentDeleteHandler deleteHandler) {
		mDeleteHandler = deleteHandler;
	}
	
	class AttachmentRemoveOnClickListener implements View.OnClickListener {
		int id;

		public AttachmentRemoveOnClickListener(int id) {
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			final AttachmentDeleteHandler deletor = mDeleteHandler;
			if (mDeleteHandler == null)
				return;
			new AlertDialog.Builder(context)
					.setTitle("Remove confirmation")
					.setMessage("Confirm to remove the photo?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									deletor.doDeleteAttachment(id);
								}
							}).setNegativeButton("No", null)
					.show();
		}
	}	
	
	public interface AttachmentDeleteHandler {
		public void doDeleteAttachment(int position);
	}
}