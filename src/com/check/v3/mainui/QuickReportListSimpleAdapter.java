package com.check.v3.mainui;

import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.check.client.R;
import com.check.v3.CloudCheckApplication;
import com.check.v3.data.ImageItemData;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.util.LruBitmapCache;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuickReportListSimpleAdapter extends BaseAdapter {
	private Context context;

	private LayoutInflater layoutInflater;
	private String baseUrl = "http://www.365check.net:8088/check-service/check-data/";
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    
	public ArrayList<QuickCheckListItemData> mList;
	
	private int maxSize = 5 * 1024 * 1024;

	public QuickReportListSimpleAdapter(Context context,
			ArrayList<QuickCheckListItemData> quickCheckListItemData) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.mList = quickCheckListItemData;
		mQueue = CloudCheckApplication.getInstance().getRequestQueue();
		CloudCheckApplication.getInstance().setCookie();
		mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(maxSize));
	}

	public int getCount() {
		return this.mList != null ? this.mList.size() : 0;
	}

	public Object getItem(int position) {
		return this.mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = layoutInflater.inflate(
					R.layout.quick_check_list_item_view, null);
			
			holder.issue_photo_preview_image = (ImageView) convertView.findViewById(R.id.issue_photo_preview_image);
			holder.issue_rsp_person_text = (TextView) convertView.findViewById(R.id.issue_rsp_person_name_text);
			holder.issue_deadlie_text = (TextView) convertView.findViewById(R.id.issue_deadline_date_text);
			holder.issue_dscp_text = (TextView) convertView.findViewById(R.id.issue_dscp_text);
			holder.issue_level_image = (ImageView) convertView.findViewById(R.id.issue_level_image);
			
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		QuickCheckListItemData qcListItemData = mList.get(position);
		ArrayList<ImageItemData> imageList = qcListItemData.getImages();
		
//		holder.issue_photo_preview_image.setImageResource(R.drawable.ic_photo_preview_default_image);
		
		ImageListener listener = ImageLoader.getImageListener(holder.issue_photo_preview_image, R.drawable.ic_photo_preview_default_image, R.drawable.ic_photo_preview_default_image);
		
		if(imageList != null && imageList.size() > 0){
			int imageIdPart = imageList.get(0).getId();
			String imagePathPart = imageList.get(0).getPath();
			String imageFullUrlStr = baseUrl +imagePathPart + "-small-thumbnail" + ".jpg";
			mImageLoader.get(imageFullUrlStr, listener);
//			mImageLoader.get("http://imgstatic.baidu.com/img/image/shouye/fanbingbing.jpg", listener);
		}else{
//			mImageLoader.get("http://imgstatic.baidu.com/img/image/shouye/fanbingbing.jpg", listener);
			holder.issue_photo_preview_image.setImageResource(R.drawable.ic_photo_preview_default_image);
		}
		
		holder.issue_rsp_person_text.setText(qcListItemData.getResponsiblePersonName());
		
		if(qcListItemData.getDeadline() != null){
			holder.issue_deadlie_text.setText(qcListItemData.getDeadline());
		}
		
		holder.issue_dscp_text.setText(qcListItemData.getDescription());		
		
		String issueLevelStr = qcListItemData.getLevel();
		Log.d("Debug", "issue level in adapter: " + issueLevelStr);
		int issueLevelImageResId = R.drawable.ic_issue_level_low;
		if(issueLevelStr != null){
			if(issueLevelStr.equals("HIGH")){
				issueLevelImageResId = R.drawable.ic_issue_level_high;
			}else if(issueLevelStr.equals("MID")){
				issueLevelImageResId = R.drawable.ic_issue_level_middle;
			}else if(issueLevelStr.equals("LOW")){
				issueLevelImageResId = R.drawable.ic_issue_level_low;
			}
		}
		holder.issue_level_image.setImageResource(issueLevelImageResId);
		
		return convertView;
	}
	
    public final class ViewHolder {
        public ImageView issue_photo_preview_image;
        public TextView issue_rsp_person_text;
        public TextView issue_deadlie_text;
        public TextView issue_dscp_text;
        public ImageView issue_level_image;
    }
}