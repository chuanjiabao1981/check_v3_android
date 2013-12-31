package com.check.v3.ui.base;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseFragment extends SherlockFragment {

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
