package com.example.ysulib.ui;

import java.util.List;

import com.example.ysulib.CustomApplcation;
import com.example.ysulib.R;
import com.example.ysulib.util.CollectionUtils;
import com.example.ysulib.view.HeaderLayout;
import com.example.ysulib.view.HeaderLayout.HeaderStyle;
import com.example.ysulib.view.HeaderLayout.onLeftImageButtonClickListener;
import com.example.ysulib.view.HeaderLayout.onRightImageButtonClickListener;
import com.example.ysulib.view.dialog.DialogTips;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

// 基类
public class BaseActivity extends FragmentActivity {

	BmobUserManager userManager;
	BmobChatManager manager;
	
	CustomApplcation mApplication;
	protected HeaderLayout mHeaderLayout;
	
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected static boolean YANZHENG=false;
	protected static boolean YAN=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplication = CustomApplcation.getInstance();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});	
		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {		
			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	// 打Log
	public void ShowLog(String msg){
		BmobLog.i(msg);
	}
	
	//只有title initTopBarLayoutByTitle
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	 // 初始化标题栏-带左右按钮
	public void initTopBarForBoth(String titleName, int rightDrawableId,String text,onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,R.drawable.base_action_bar_back_bg_selector,new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId,text,listener);
	}
	
	public void initTopBarForBoth(String titleName, int rightDrawableId,onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,R.drawable.base_action_bar_back_bg_selector,new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,listener);
	}

	 //只有左边按钮和Title initTopBarLayout
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,R.drawable.base_action_bar_back_bg_selector,new OnLeftButtonClickListener());
	}
	
	//显示下线的对话框
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this,"您的账号已在其他设备上登录!", "重新登录");
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				CustomApplcation.getInstance().logout();
				startActivity(new Intent(context, LoginActivity.class));
				finish();
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	// 左边按钮的点击事件
	public class OnLeftButtonClickListener implements onLeftImageButtonClickListener {

		@Override
		public void onClick() {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}
	
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	
	//用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	public void updateUserInfos(){
		//查询该用户的好友列表,目前查询好友个数为100，如需修改在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		//这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, String arg1) {
						if(arg0==BmobConfig.CODE_COMMON_NONE){
							ShowLog(arg1);
						}else{
							ShowLog("查询好友列表失败："+arg1);
						}
					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// 保存到application中方便比较
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(arg0));
					}
				});
	}
}
