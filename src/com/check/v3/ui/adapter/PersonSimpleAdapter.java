package com.check.v3.ui.adapter;

import java.util.ArrayList;
import com.check.client.R;
import com.check.v3.data.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonSimpleAdapter extends BaseAdapter {
	private Context context;

	private LayoutInflater layoutInflater;

	private ArrayList<User> list;

	public PersonSimpleAdapter(Context context,
			ArrayList<User> personList) {
		this.context = context;

		layoutInflater = LayoutInflater.from(context);

		this.list = personList;
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
					R.layout.person_list_item, null);
		}

		TextView personNameTextView = (TextView) convertView.findViewById(R.id.personName);
		personNameTextView.setText((CharSequence) list.get(position).getName());

		return convertView;
	}
}