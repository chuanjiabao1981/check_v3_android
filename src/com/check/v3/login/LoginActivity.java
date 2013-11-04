package com.check.v3.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.check.client.R;
import com.check.v3.preferences.DataPreference;
import com.check.v3.preferences.PrefConstant;
import com.check.v3.register.RegisterActivity;

public class LoginActivity extends Activity implements View.OnClickListener {
	private static final int URL_CONFIG_DIALOG = 1;
	private final String URL_PREFER_ITEM_URL_DEFAULT = "42.121.55.211";
	private CheckBox autoLoginCheckBox;
	private Button btn_login_regist;
	private Button changeServerUrlButton;
	private RelativeLayout check_login_container;
	private ImageView img_more_up;
	private boolean isShowMenu = false;
	private Button loginButton;
	private DataPreference mDataPreference;
	TextWatcher mPasswordTextWatcher;
	private AlertDialog.Builder mUrlConfBuilder;
	private View mUrlSettingView;
	TextWatcher mUserNameTextWatcher;
	private View menu_more;
	private Button passwordClearButton;
	private EditText passwordEditText;
	private CheckBox savePasswordCheckBox;
	private Button userNameClearButton;
	private EditText userNameEditText;
	private View view_more;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initView();

		mDataPreference = new DataPreference(this.getApplicationContext());
		if (mDataPreference.getBoolean("SAVE_PASSWORD", false)) {
			savePasswordCheckBox.setChecked(true);
		}

		String storedUserNameStr = mDataPreference.getString(
				PrefConstant.USER_NAME, "");
		if (storedUserNameStr != null) {
			userNameEditText.setText(storedUserNameStr);
		}

		String storedPasswdStr = mDataPreference.getString(
				PrefConstant.PASSWORD, "");
		if (storedPasswdStr != null) {
			passwordEditText.setText(storedPasswdStr);
		}

		if (mDataPreference.getBoolean(PrefConstant.AUTO_LOGIN, false)) {
			autoLoginCheckBox.setChecked(true);
			launchLogin();
		}
	}
  
	private void initView() {
		userNameEditText = (EditText) findViewById(R.id.login_user_name);
		passwordEditText = (EditText) findViewById(R.id.login_passwd);
		userNameClearButton = (Button) findViewById(R.id.user_name_clear_btn);
		passwordClearButton = (Button) findViewById(R.id.passwd_clear_btn);
		loginButton = (Button) findViewById(R.id.btn_login);

		img_more_up = (ImageView) findViewById(R.id.img_more_up);

		changeServerUrlButton = (Button) findViewById(R.id.btn_change_server);

		btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
		btn_login_regist.setOnClickListener(this);
		menu_more = findViewById(R.id.menu_more);

		view_more = findViewById(R.id.view_more);

		autoLoginCheckBox = (CheckBox) findViewById(R.id.checkbox_auto_login);

		savePasswordCheckBox = (CheckBox) findViewById(R.id.checkbox_save_passwd);

		check_login_container = (RelativeLayout) findViewById(R.id.check_login_container);

		loginButton.setOnClickListener(this);

		savePasswordCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (savePasswordCheckBox.isChecked()) {
							mDataPreference.saveData(
									PrefConstant.SAVE_PASSWORD, true);
						} else {
							mDataPreference.saveData(
									PrefConstant.SAVE_PASSWORD, false);
						}
					}
				});

		autoLoginCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (autoLoginCheckBox.isChecked()) {
							mDataPreference.saveData(
									PrefConstant.AUTO_LOGIN, true);
							if (!savePasswordCheckBox.isChecked()) {
								savePasswordCheckBox.setChecked(true);
							}
						} else {
							mDataPreference.saveData(
									PrefConstant.AUTO_LOGIN, false);
						}
					}
				});

		userNameClearButton.setOnClickListener(this);
		passwordClearButton.setOnClickListener(this);

		mUserNameTextWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if ((userNameEditText.getText().toString() != null)
						&& (!userNameEditText.getText().toString().equals(""))
						&& (userNameEditText.hasFocus())) {
					userNameClearButton.setVisibility(View.VISIBLE);
				} else {
					userNameClearButton.setVisibility(View.GONE);
					passwordEditText.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

		};

		mPasswordTextWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if ((passwordEditText.getText().toString() != null)
						&& (!passwordEditText.getText().toString().equals(""))
						&& (passwordEditText.hasFocus())) {
					passwordClearButton.setVisibility(View.VISIBLE);
				} else {
					passwordClearButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

		};

		userNameEditText.addTextChangedListener(mUserNameTextWatcher);
		passwordEditText.addTextChangedListener(mPasswordTextWatcher);

		userNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					userNameClearButton.setVisibility(View.GONE);
				} else {
					if ((userNameEditText.getText().toString() == null)
							|| (userNameEditText.getText().toString()
									.equals(""))) {
						userNameClearButton.setVisibility(View.GONE);
					} else {
						userNameClearButton.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		passwordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					passwordClearButton.setVisibility(View.GONE);
				} else {
					if ((passwordEditText.getText().toString() == null)
							|| (passwordEditText.getText().toString()
									.equals(""))) {
						passwordClearButton.setVisibility(View.GONE);
					} else {
						passwordClearButton.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		view_more.setOnClickListener(this);
		
		changeServerUrlButton.setOnClickListener(this);
	}

	private boolean validate() {
		String userNameStr = userNameEditText.getText().toString().trim();
		String passwdStr = passwordEditText.getText().toString().trim();
		if (userNameStr == null || "".equals(userNameStr)) {
			userNameEditText.setError("用户名不能为空");
			userNameEditText.requestFocus();
			return false;
		}

		if ((userNameStr.length() < 4) || (userNameStr.length() > 20)) {
			userNameEditText.setError("请输入长度在4－20之间的用户名");
			userNameEditText.requestFocus();
			return false;
		}

		if (passwdStr == null || "".equals(passwdStr)) {
			passwordEditText.setError("密码不能为空");
			passwordEditText.requestFocus();
			return false;
		}
		return true;
	}

	public void launchLogin() {
		if (validate()) {
			saveLoginInfo();
			LoginForm loginForm = new LoginForm(this);
			LoginTask loginTask = new LoginTask(loginForm);
			loginTask.go();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:

			launchLogin();
			break;
		case R.id.user_name_clear_btn:
			userNameEditText.setText("");
			break;
		case R.id.passwd_clear_btn:
			passwordEditText.setText("");
			break;
		case R.id.btn_login_regist:
			Intent localIntent = new Intent(this, RegisterActivity.class);
			startActivity(localIntent);
			break;
		case R.id.view_more:
			showMoreMenu(isShowMenu);
			check_login_container.invalidate();
			break;
		case R.id.btn_change_server:
			showDialog(URL_CONFIG_DIALOG);
		default:
			break;
		}
	}
  
	public void saveLoginInfo() {
		if (savePasswordCheckBox.isChecked()) {
			String userNameStr = userNameEditText.getText().toString();
			String passwdStr = passwordEditText.getText().toString();

			mDataPreference.saveData(PrefConstant.USER_NAME, userNameStr);
			mDataPreference.saveData(PrefConstant.PASSWORD, passwdStr);
		}

	}

	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog alertDlg = null;
		switch (id) {
		case URL_CONFIG_DIALOG:
			mUrlConfBuilder = new AlertDialog.Builder(this);
			mUrlSettingView = getLayoutInflater().inflate(
					R.layout.url_config_layout, null);
			
			mUrlConfBuilder.setTitle("设置服务器地址")
			.setCancelable(false)
			.setView(mUrlSettingView);
			
			final EditText serverUrlEditText = (EditText) this.mUrlSettingView
					.findViewById(R.id.url_edit);
			String serverUrlStr = mDataPreference.getString(PrefConstant.SERVER_HOST,
					URL_PREFER_ITEM_URL_DEFAULT);
			serverUrlEditText.setText(serverUrlStr);
			
			mUrlConfBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { 
					String newServerUrlStr = serverUrlEditText.getText().toString();
					mDataPreference.saveData(PrefConstant.SERVER_HOST, newServerUrlStr);
				}
			})
			.setNegativeButton("取消", 
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	
                }
            });
		}
		alertDlg = this.mUrlConfBuilder.create();
		return alertDlg;
	}

	public void showMoreMenu(boolean show) {
		if (show) {
			menu_more.setVisibility(View.GONE);
			img_more_up.setImageResource(R.drawable.login_more_up);
			isShowMenu = false;
		} else {
			menu_more.setVisibility(View.VISIBLE);
			img_more_up.setImageResource(R.drawable.login_more);
			isShowMenu = true;
		}
	}
}
