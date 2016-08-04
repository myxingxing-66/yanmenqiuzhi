package com.example.ysulib.ui;

import com.example.ysulib.CustomApplcation;
import com.example.ysulib.R;
import com.example.ysulib.view.HeaderLayout;
import com.example.ysulib.view.HeaderLayout.HeaderStyle;
import com.example.ysulib.view.HeaderLayout.onLeftImageButtonClickListener;
import com.example.ysulib.view.HeaderLayout.onRightImageButtonClickListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;


//Fragmenet 基类
public abstract class FragmentBase extends Fragment {
	
	public BmobUserManager userManager;
	public BmobChatManager manager;
	
	//公用的Header布局
	public HeaderLayout mHeaderLayout;

	protected View contentView;
	
	public LayoutInflater mInflater;
	
	private Handler handler = new Handler();
	
	public void runOnWorkThread(Runnable action) {
		new Thread(action).start();
	}

	public void runOnUiThread(Runnable action) {
		handler.post(action);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mApplication = CustomApplcation.getInstance();
		userManager = BmobUserManager.getInstance(getActivity());
		manager = BmobChatManager.getInstance(getActivity());
		mInflater = LayoutInflater.from(getActivity());
	}
	
	public FragmentBase() {
		
	}

	Toast mToast;

	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowLog(String msg){
		BmobLog.i(msg);
	}
	
	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}

	public CustomApplcation mApplication;

	 //只有title initTopBarLayoutByTitle
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	//初始化标题栏-带左右按钮
	public void initTopBarForBoth(String titleName, int rightDrawableId,onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,R.drawable.base_action_bar_back_bg_selector,new onLeftImageButtonClickListener() {
			
			@Override
			public void onClick() {
				getActivity().finish();
			}
		});
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,listener);
	}

	public void initTopBarForBothAndMenu(String titleName,int leftDrawableId,onLeftImageButtonClickListener onLeftImageButtonClickListener,onRightImageButtonClickListener listener){
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName, R.drawable.base_action_bar_more_bg_selector, onLeftImageButtonClickListener);
		mHeaderLayout.setTitleAndRightImageButton(titleName, R.drawable.base_action_bar_add_bg_selector, listener);
	}
	
	//只有左边按钮和Title initTopBarLayout
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,R.drawable.base_action_bar_back_bg_selector,new onLeftImageButtonClickListener() {
			
			@Override
			public void onClick() {
				getActivity().finish();
			}
		});
	}

	public void initTopBarForLeftMenu(String titleName,onLeftImageButtonClickListener onLeftImageButtonClickListener){
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName, R.drawable.base_action_bar_more_bg_selector, onLeftImageButtonClickListener);
	}
	
	// 右边+title
	public void initTopBarForRight(String titleName,int rightDrawableId,onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,listener);
	}
	
	
	//动画启动页面 startAnimActivity
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	
	public void startAnimActivity(Class<?> cla) {
		getActivity().startActivity(new Intent(getActivity(), cla));
	}
	
	
	public static int dip2px(Context context,float dipValue){
		float scale=context.getResources().getDisplayMetrics().density;		
		return (int) (scale*dipValue+0.5f);		
	}
	
}
