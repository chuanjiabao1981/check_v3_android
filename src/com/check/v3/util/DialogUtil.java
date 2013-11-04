package com.check.v3.util;

import com.check.client.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class DialogUtil
{
//  public static Dialog createCustomProgressDialog(Context paramContext, String paramString)
//  {
//    View localView = LayoutInflater.from(paramContext).inflate(2130903042, null);
//    LinearLayout localLinearLayout = (LinearLayout)localView.findViewById(2131296270);
//    ImageView localImageView = (ImageView)localView.findViewById(2131296271);
//    TextView localTextView = (TextView)localView.findViewById(2131296272);
//    Animation localAnimation = AnimationUtils.loadAnimation(paramContext, 2130968576);
//    localImageView.startAnimation(localAnimation);
//    localTextView.setText(paramString);
//    return new CustomAlertDialog.Builder(paramContext).setTitle("正在登录...").setView(localView).setCancelable(true).create();
//  }

public static Dialog createLoadingDialog(Context context, String msgStr,
		int theme) {
	View dialogCustomView = LayoutInflater.from(context).inflate(
			R.layout.login_loading_dialog, null);
	LinearLayout ll = (LinearLayout) dialogCustomView
			.findViewById(R.id.dialog_view);
	ImageView imageView = (ImageView) dialogCustomView
			.findViewById(R.id.img);
	TextView msgTextView = (TextView) dialogCustomView
			.findViewById(R.id.tipTextView);
	Animation anim = AnimationUtils.loadAnimation(context,
			R.anim.loading_animation);
	imageView.startAnimation(anim);
	msgTextView.setText(msgStr);
	
	Dialog loadingDialog = new Dialog(context, theme);
	loadingDialog.setCancelable(false);
	loadingDialog.setCanceledOnTouchOutside(true);
	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	loadingDialog.setContentView(ll, layoutParams);
	
	return loadingDialog;
}
}