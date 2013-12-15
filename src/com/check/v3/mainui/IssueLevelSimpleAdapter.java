package com.check.v3.mainui;

import java.util.ArrayList;
import com.check.client.R;
import com.check.v3.data.IssueLevel;
import com.check.v3.data.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IssueLevelSimpleAdapter extends BaseAdapter {
	private Context context;

	private LayoutInflater layoutInflater;

	private ArrayList<IssueLevel> list;

	public IssueLevelSimpleAdapter(Context context,
			ArrayList<IssueLevel> issueLevelList) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.list = issueLevelList;
	}

	public int getCount() {
		return this.list != null ? this.list.size() : 0;
	}

	public Object getItem(int position) {
		return this.list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.issue_level_list_item, null);
		}

		TextView levelNameTextView = (TextView) convertView.findViewById(R.id.issueLevelDisplayName);
		levelNameTextView.setText((CharSequence) list.get(position).getIssueLevelDisplayName());

		return convertView;
	}
}