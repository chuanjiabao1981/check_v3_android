package com.check.v3.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.check.client.R;
import com.check.v3.data.SimpleOrganization;

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
import android.widget.TextView;

public class OrganizationAdapter extends BaseAdapter {
	private Context context;

	private LayoutInflater layoutInflater;

	private ArrayList<SimpleOrganization> list;

	public OrganizationAdapter(Context context,
			ArrayList<SimpleOrganization> orgList) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.list = orgList;
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
					R.layout.org_list_item, null);
		}

		TextView orgNameTextView = (TextView) convertView.findViewById(R.id.orgName);
		orgNameTextView.setText((CharSequence) list.get(position).getOrgName());

		return convertView;
	}
}