package com.check.v3.mainui;

import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.check.client.R;
import com.check.v3.CloudCheckApplication;
import com.check.v3.data.ImageItemData;
import com.check.v3.data.ReportResolutionRspData;
import com.check.v3.util.LruBitmapCache;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IssueResolutionListAdapter extends BaseAdapter {
	private Context context;

	private LayoutInflater layoutInflater;
	private String baseUrl = "http://www.365check.net:8088/check-service/check-data/";
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    
	public ArrayList<ReportResolutionRspData> mRslvDataList;
	
	private int maxSize = 5 * 1024 * 1024;

	public IssueResolutionListAdapter(Context context,
			ArrayList<ReportResolutionRspData> reportResolutionRspData) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.mRslvDataList = reportResolutionRspData;
		mQueue = CloudCheckApplication.getInstance().getRequestQueue();
		CloudCheckApplication.getInstance().setCookie();
		mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(maxSize));
	}

	public int getCount() {
		return this.mRslvDataList != null ? this.mRslvDataList.size() : 0;
	}

	public Object getItem(int position) {
		return this.mRslvDataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = layoutInflater.inflate(
					R.layout.issue_resolution_list_item_view, null);
			
			holder.issue_rslv_photo_preview_image = (ImageView) convertView.findViewById(R.id.issue_rslv_photo_preview_image);
			holder.issue_rslv_person_text = (TextView) convertView.findViewById(R.id.issue_rslv_person_name_text);
			holder.issue_rslv_date_text = (TextView) convertView.findViewById(R.id.issue_rslv_date_text);
			holder.issue_rslv_dscp_text = (TextView) convertView.findViewById(R.id.issue_rslv_dscp_text);
			
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ReportResolutionRspData rslvItemData = mRslvDataList.get(position);
		ArrayList<ImageItemData> imageList = rslvItemData.getImages();
		
		ImageListener listener = ImageLoader.getImageListener(holder.issue_rslv_photo_preview_image, R.drawable.ic_photo_preview_default_image, R.drawable.ic_photo_preview_default_image);
		
		if(imageList != null && imageList.size() > 0){
			int imageIdPart = imageList.get(0).getId();
			String imagePathPart = imageList.get(0).getPath();
			String imageFullUrlStr = baseUrl +imagePathPart + "-small-thumbnail" + ".jpg";
			mImageLoader.get(imageFullUrlStr, listener);
		}else{
			holder.issue_rslv_photo_preview_image.setImageResource(R.drawable.ic_photo_preview_default_image);
		}
		
		holder.issue_rslv_person_text.setText(rslvItemData.getSubmitterName());
		
		
		holder.issue_rslv_dscp_text.setText(rslvItemData.getDescription());		

		return convertView;
	}
	
    public final class ViewHolder {
        public ImageView issue_rslv_photo_preview_image;
        public TextView issue_rslv_person_text;
        public TextView issue_rslv_date_text;
        public TextView issue_rslv_dscp_text;
    }
}