package com.example.ysulib.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Window;
import cn.bmob.im.BmobChat;

import com.example.ysulib.R;
import com.example.ysulib.config.Config;

//引导页
public class SplashActivity extends BaseActivity {

	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		//BmobIM SDK初始化--只需要这一段代码即可完成初始化
		BmobChat.getInstance(this).init(Config.applicationId);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean isRemember=pref.getBoolean("first_login", false);
		if(isRemember){		
		if (userManager.getCurrentUser() != null) {		
			updateUserInfos();
			mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
		}
		}else{
			editor=pref.edit();
			editor.putBoolean("first_login", true);
			editor.commit();
			startAnimActivity(GuideActivity.class);
			finish();
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				startAnimActivity(MainActivity.class);
				finish();
				break;
			case GO_LOGIN:
				startAnimActivity(LoginActivity.class);
				finish();
				break;
			}
		}
	};
}
