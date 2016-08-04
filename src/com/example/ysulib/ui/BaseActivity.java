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

// ����
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

	// ��Log
	public void ShowLog(String msg){
		BmobLog.i(msg);
	}
	
	//ֻ��title initTopBarLayoutByTitle
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	 // ��ʼ��������-�����Ұ�ť
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

	 //ֻ����߰�ť��Title initTopBarLayout
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,R.drawable.base_action_bar_back_bg_selector,new OnLeftButtonClickListener());
	}
	
	//��ʾ���ߵĶԻ���
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this,"�����˺����������豸�ϵ�¼!", "���µ�¼");
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				CustomApplcation.getInstance().logout();
				startActivity(new Intent(context, LoginActivity.class));
				finish();
				dialogInterface.dismiss();
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}
	
	// ��߰�ť�ĵ���¼�
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
	
	//���ڵ�½�����Զ���½����µ��û����ϼ��������ϵļ�����
	public void updateUserInfos(){
		//��ѯ���û��ĺ����б�,Ŀǰ��ѯ���Ѹ���Ϊ100�������޸��ڵ����������ǰ����BmobConfig.LIMIT_CONTACTS���ɡ�
		//����Ĭ�ϲ�ȡ���ǵ�½�ɹ�֮�󼴽������б�洢�����ݿ��У������µ���ǰ�ڴ���,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, String arg1) {
						if(arg0==BmobConfig.CODE_COMMON_NONE){
							ShowLog(arg1);
						}else{
							ShowLog("��ѯ�����б�ʧ�ܣ�"+arg1);
						}
					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// ���浽application�з���Ƚ�
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(arg0));
					}
				});
	}
}
