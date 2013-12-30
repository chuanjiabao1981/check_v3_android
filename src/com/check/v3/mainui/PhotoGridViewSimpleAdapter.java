package com.check.v3.mainui;

import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.check.client.R;
import com.check.v3.CloudCheckApplication;
import com.check.v3.data.ImageItemData;
import com.check.v3.util.LruBitmapCache;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoGridViewSimpleAdapter extends BaseAdapter {
	private Context context;
	
	private AttachmentDeleteHandler mDeleteHandler;

	private LayoutInflater layoutInflater;
	private String baseUrl = "http://www.365check.net:8088/check-service/check-data/";
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private boolean removeBtnVisible = false;
    
	public ArrayList<ImageItemData> mImageDataList;
	
	private int maxSize = 5 * 1024 * 1024;

	public PhotoGridViewSimpleAdapter(Context context,
			ArrayList<ImageItemData> imageDataList, boolean removeBtnVisible) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.mImageDataList = imageDataList;
		this.removeBtnVisible = removeBtnVisible;
		
		mQueue = CloudCheckApplication.getInstance().getRequestQueue();
		CloudCheckApplication.getInstance().setCookie();
		mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(maxSize));
	}

	public int getCount() {
		return this.mImageDataList != null ? this.mImageDataList.size() : 0;
	}

	public Object getItem(int position) {
		return this.mImageDataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = layoutInflater.inflate(
					R.layout.photo_grid_view_list_item, null);
			
			holder.issue_photo_image = (ImageView) convertView.findViewById(R.id.preview);
			holder.delete_preview_btn = (Button) convertView.findViewById(R.id.delete_preview_btn);
			
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ImageItemData imageItemData = mImageDataList.get(position);
		String imagePath = imageItemData.getPath();
		
		ImageListener listener = ImageLoader.getImageListener(holder.issue_photo_image, R.drawable.ic_photo_preview_default_image, R.drawable.ic_photo_preview_default_image);
		String imageFullUrlStr = baseUrl + imagePath + "-normal" + ".jpg";
		mImageLoader.get(imageFullUrlStr, listener);
		
		if(removeBtnVisible == true){
			holder.delete_preview_btn.setVisibility(View.VISIBLE);
			holder.delete_preview_btn.setOnClickListener(new AttachmentRemoveOnClickListener(position));
		}
		else{
			holder.delete_preview_btn.setVisibility(View.GONE);
		}
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
	
    public final class ViewHolder {
        public ImageView issue_photo_image;
        public Button delete_preview_btn;
    }
}