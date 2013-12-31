package com.check.v3.widget;

import com.check.client.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomIssueItemView extends LinearLayout {
	private TextView mTitleTv;
	private TextView mValueTv;
	private ImageView mDividerIv;
	private boolean mIsDividerVisible = true;

	public CustomIssueItemView(Context context) {
		super(context);
	}

	public CustomIssueItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(
				R.layout.custom_issue_item_display_view, this, true);
		mTitleTv = (TextView) view.findViewById(R.id.issue_item_title);
		mValueTv = (TextView) view.findViewById(R.id.issue_item_value_text);
		mDividerIv = (ImageView) view.findViewById(R.id.issue_item_divider);

		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.CustomIssueItem);
		CharSequence titleText = typedArr
				.getString(R.styleable.CustomIssueItem_customTitleText);
		if (titleText != null){
			mTitleTv.setText(titleText);
		}
		
		CharSequence valueText = typedArr
				.getString(R.styleable.CustomIssueItem_customValueText);
		if (valueText != null){
			mValueTv.setText(valueText);
		}

		Drawable drawable = typedArr
				.getDrawable(R.styleable.CustomIssueItem_customImageSrc);
		if (drawable != null){
			mDividerIv.setImageDrawable(drawable);
		}
		
		boolean mIsDividerVisible = typedArr
				.getBoolean(R.styleable.CustomIssueItem_customDividerVisible, true);
		if (mIsDividerVisible){
			mDividerIv.setVisibility(View.VISIBLE);
		}else{
			mDividerIv.setVisibility(View.GONE);
		}
		
		typedArr.recycle();
	}

	public void setTitleText(String text) {
		mTitleTv.setText(text);
	}

	public void setValueText(String text) {
		mValueTv.setText(text);
	}

	public void setImageResource(int resId) {
		mDividerIv.setImageResource(resId);
	}
}
