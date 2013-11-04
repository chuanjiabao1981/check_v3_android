package com.check.v3.register;

import com.check.client.R;
import com.check.v3.util.ClassPathResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements android.view.View.OnClickListener{
	public static final int REGION_SELECT = 1;
	private TextView tv_check_server,tv_region_modify,tv_region_show,tv_top_title;
	private Button btn_title_left,btn_title_right,btn_send_code;
	private CheckBox chk_agree;
	private EditText et_mobileNo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
	}
	
	private void initView(){
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		tv_top_title.setText("注册新账号");
		
		btn_title_right = (Button) findViewById(R.id.btn_title_right);
		btn_title_right.setVisibility(View.GONE);
		
		btn_title_left = (Button) findViewById(R.id.btn_title_left);
		btn_title_left.setOnClickListener(this);
		
		btn_send_code = (Button) findViewById(R.id.btn_send_code);
		btn_send_code.setOnClickListener(this);
		
		tv_check_server = (TextView) findViewById(R.id.tv_check_server);
		tv_check_server.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		tv_region_show = (TextView) findViewById(R.id.tv_region_show);
		
		tv_region_modify = (TextView) findViewById(R.id.tv_region_modify);
		tv_region_modify.setOnClickListener(this);
		
		chk_agree = (CheckBox) findViewById(R.id.chk_agree);
		et_mobileNo = (EditText) findViewById(R.id.et_mobileNo);
	}
	

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch(id){
		case REGION_SELECT:
			final Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择所在地");
			builder.setSingleChoiceItems(
					new String[]{"+86中国大陆","+853中国澳门","+852中国香港","+886中国台湾"},
					0, 
					new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch(which){
							case 0:
								tv_region_show.setText("+86中国大陆");
								
								break;
							case 1:
								tv_region_show.setText("+853中国澳门");
								break;
							case 2:
								tv_region_show.setText("+852中国香港");
								break;
							case 3:
								tv_region_show.setText("+886中国台湾");
								break;
							}
							dismissDialog(REGION_SELECT);
						}
					});
			return builder.create();
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tv_region_modify:
			showDialog(REGION_SELECT);
			break;
		case R.id.btn_title_left:
			RegisterActivity.this.finish();
			break;
		case R.id.btn_send_code:
			String mobiles = et_mobileNo.getText().toString();
			if(chk_agree.isChecked()== false)
				Toast.makeText(this, "请确认是否已经阅读《云检服务条款》", Toast.LENGTH_LONG).show();
			if(ClassPathResource.isMobileNO(mobiles)==false)
				Toast.makeText(this, "正确填写手机号，我们将向您发送一条验证码短信", Toast.LENGTH_LONG).show();
			if(ClassPathResource.isMobileNO(mobiles)==true&&chk_agree.isChecked()==true){
				Toast.makeText(this, "已经向您手机发送验证码，请查看", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(RegisterActivity.this, RegisterConfirmActivity.class);
				startActivity(intent);
			}
		}
		
	}

	

}
