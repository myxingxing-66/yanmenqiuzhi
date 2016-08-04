package com.example.ysulib.ui;

import com.example.ysulib.R;
import com.example.ysulib.config.BmobConstants;
import com.example.ysulib.util.CommonUtils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends BaseActivity implements OnClickListener {

	EditText et_username, et_password;
	Button btn_login;
	TextView btn_register;
	BmobChatUser currentUser;

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		//注册退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
	}
	
	private void init() {
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (TextView) findViewById(R.id.btn_register);
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && BmobConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
				finish();
			}
		}

	}
	
	@Override
	public void onClick(View v) {
		if (v == btn_register) {
			Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
			startActivity(intent);
		} else {
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if(!isNetConnected){
				ShowToast(R.string.network_tips);
				return;
			}
			login();
		}
	}
	
	private void login(){
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.login(name, password, new SaveListener() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progress.setMessage("正在获取好友列表...");
					}
				});
				//更新用户的地理位置以及好友的资料
				updateUserInfos();
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				progress.dismiss();
				BmobLog.i(arg0);
				ShowToast("用户名或密码错误");
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
}
