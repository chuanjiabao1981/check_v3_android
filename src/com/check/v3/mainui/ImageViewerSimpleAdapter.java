package com.check.v3.mainui;

import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.check.client.R;
import com.check.v3.CloudCheckApplication;
import com.check.v3.data.ImageItemData;
import com.check.v3.util.LruBitmapCache;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageViewerSimpleAdapter extends BaseAdapter {
	private Context context;

	private LayoutInflater layoutInflater;
	private String baseUrl = "http://www.365check.net:8088/check-service/check-data/";
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    
	public ArrayList<ImageItemData> mImageDataList;
	
	private int maxSize = 5 * 1024 * 1024;

	public ImageViewerSimpleAdapter(Context context,
			ArrayList<ImageItemData> imageDataList) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.mImageDataList = imageDataList;
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
					R.layout.quick_check_image_viewer_item, null);
			
			holder.issue_photo_image = (ImageView) convertView.findViewById(R.id.qc_v_image_viewer_item);
			
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ImageItemData imageItemData = mImageDataList.get(position);
		String imagePath = imageItemData.getPath();
		
		ImageListener listener = ImageLoader.getImageListener(holder.issue_photo_image, R.drawable.ic_photo_preview_default_image, R.drawable.ic_photo_preview_default_image);
		String imageFullUrlStr = baseUrl + imagePath + "-normal" + ".jpg";
		mImageLoader.get(imageFullUrlStr, listener);
		
		
		return convertView;
	}
	
    public final class ViewHolder {
        public ImageView issue_photo_image;
    }
}