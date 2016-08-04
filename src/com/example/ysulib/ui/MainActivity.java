package com.example.ysulib.ui;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.ysulib.CustomApplcation;
import com.example.ysulib.MyMessageReceiver;
import com.example.ysulib.R;
import com.example.ysulib.bean.LendInfo;
import com.example.ysulib.bean.NowLend;
import com.example.ysulib.bean.User;
import com.example.ysulib.ui.fragment.BookFragment;
import com.example.ysulib.ui.fragment.ContactFragment;
import com.example.ysulib.ui.fragment.RecentFragment;
import com.example.ysulib.util.library.Lendnow;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends ActivityBase implements EventListener,OnItemClickListener, OnClickListener {

	private Button btn_mylibrary,btn_set_list;
	private ListView list_library;
	private Button[] mTabs;
	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private BookFragment bookFragment;
	private Fragment[] fragments;
	private int index = 2;
	private int currentTabIndex = 2;
	private String[] myItem = { "证件信息", "当前借阅", "借阅历史", "荐购历史", "预约信息"};
	private int[] myImgID={R.drawable.idd,R.drawable.now,R.drawable.history,R.drawable.tuijian,R.drawable.yuyue};
	ImageView iv_recent_tips, iv_contact_tips;// 消息提示
	public static final int HIDE_MYLIBRARY_BUTTON = 1;
	private static String[] chaju;// 和第一名的差距
	public static final int SHOW_LIST = 0;
	public static final int REFESH_DISCUSS=9;
	public static String rank;
	
	//到期提醒
	String date="";//时间
	int givemonth=0;//归还月份
	int giveday=0;//归还日
	int nowmonth=0;//现在的月份
	int nowday=0;//现在日
	List<NowLend> infos = new ArrayList<NowLend>();
	
	
	//到期提醒
	private Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LIST:
				if(infos.size()!=0){
					for(int i=0;i<infos.size();i++){
						givemonth=Integer.parseInt(infos.get(i).getGiveday().substring(5, 7));
						giveday=Integer.parseInt(infos.get(i).getGiveday().substring(8, 10));
						if((givemonth-nowmonth)==1){
							giveday=giveday+30;
							if((giveday-nowday)<=5){
								new AlertDialog.Builder(MainActivity.this).setTitle("图书到期").setItems(
									     new String[] {"你有图书"+(giveday - nowday)+"天后到期"}, null).setNegativeButton(
									     "确定", null).show();
							}
							
						}else if((givemonth-nowmonth)==0){
							if ((giveday - nowday) <=5 &&0<=giveday-nowday) {
								new AlertDialog.Builder(MainActivity.this).setTitle("图书到期").setItems(
									     new String[] {"你有图书"+(giveday - nowday)+"天内后到期"}, null).setNegativeButton(
									     "确定", null).show();
							}else if((giveday - nowday)<0){
								new AlertDialog.Builder(MainActivity.this).setTitle("图书超期").setItems(
									     new String[] {"你有图书已经超期"}, null).setNegativeButton(
									     "确定", null).show();
							}							
						}else{
							new AlertDialog.Builder(MainActivity.this).setTitle("图书超期").setItems(
								     new String[] {"你有图书已经超期"}, null).setNegativeButton(
								     "确定", null).show();
						}
						break;
					}
						
				}
				break;
			}
		}
	};
	private Handler handler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) {
			case SHOW_LIST:
				chaju=(String[]) msg.obj;
				for(String a:chaju){
					Log.d("每条数据",a);
				}
				
				new AlertDialog.Builder(MainActivity.this).setTitle("借阅情况及排名")
				.setItems(chaju, null)
				.setNegativeButton("确定", null).show();
				
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BmobChat.getInstance(this).startPollService(30);// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		// 开启广播接收器

		InputStream ins = getResources().openRawResource(R.raw.eng);
		BreakYzM.createFile(ins);

		initNewMessageBroadCast();
		initTagMessageBroadCast();
		initView();
		initTab();

		
	}

	
	@Override
	protected void onRestart() {
		super.onRestart();
		try {
			if(YAN){
				btn_mylibrary.setText("已登录");
				btn_mylibrary.setEnabled(false);
				getLendNumber();
				YAN=false;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
				date=df.format(new Date());// new Date()为获取当前系统时间
				nowmonth=Integer.parseInt(date.substring(5, 7));
				nowday=Integer.parseInt(date.substring(8, 10));
				show();		//到期提醒		
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	//检验是否借阅信息即将到期
	private void show() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Lendnow infoa = new Lendnow();
				infos = infoa.show();
				Message message = new Message();
				message.what = SHOW_LIST;
				handler1.sendMessage(message);
			}
		}).start();
	}
	
	

	public void showMenu() {
		menu.showMenu();
	}
	
	SlidingMenu menu;

	private void initView() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_person_library);

		btn_mylibrary = (Button) findViewById(R.id.btn_mylibrary);
		btn_set_list = (Button) findViewById(R.id.btn_set_list);
		list_library = (ListView) findViewById(R.id.list_library);
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < myItem.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", R.drawable.goo);
			map.put("title", myItem[i]);
			map.put("img",myImgID[i]);
			mlist.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, mlist,
				R.layout.item_library, new String[] { "image", "title","img" },
				new int[] { R.id.lib_img, R.id.lib_til ,R.id.lib_image});


		list_library.setAdapter(adapter);
		btn_mylibrary.setOnClickListener(this);
		btn_set_list.setOnClickListener(this);
		list_library.setOnItemClickListener(this);

		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_message);
		mTabs[1] = (Button) findViewById(R.id.btn_contract);
		mTabs[2] = (Button) findViewById(R.id.btn_book);
		iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);
		mTabs[2].setSelected(true);// 把第一个tab设为选中状态
	}

	private void initTab() {
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		bookFragment = new BookFragment();
		fragments = new Fragment[] { recentFragment, contactFragment,
				bookFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, recentFragment)
				.add(R.id.fragment_container, contactFragment)
				.add(R.id.fragment_container, bookFragment)
				.hide(recentFragment)
				.hide(contactFragment)
				.show(bookFragment)
				.commit();
	}

	public void onTabSelect(View view) {// button点击事件
		switch (view.getId()) {
		case R.id.btn_message:
			index = 0;
			break;
		case R.id.btn_contract:
			index = 1;
			break;
		case R.id.btn_book:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		mTabs[index].setSelected(true);// 把当前tab设为选中状态
		currentTabIndex = index;
	}

	// 查询所有用户的借书量并将结果保存在一个数组中
	private User getLendNumber() {
		User user = new User();
		BmobQuery<User> query = new BmobQuery<User>();
		// 执行查询，第一个参数为上下文，第二个参数为查找的回调
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onSuccess(List<User> losts) {
				Log.d("用户的数量", losts.size() + "");
				final int usernum = losts.size();
				final String usernums = "用户的数量："+usernum + "";

				int lendarry[] = new int[usernum];
				// 将结果保存在数组中
				for (int i = 0; i < losts.size(); i++) {

					int a = losts.get(i).getLendnum();
					lendarry[i] = a;
					Log.d("未排序的借阅量", a + "");
				}
				// 得到最大借书数目
				int highestlend = lendarry[0];
				for (int i = 1; i < usernum; i++) {
					if (lendarry[i] > highestlend) {
						highestlend = lendarry[i];
					}
				}
				Log.d("最大借书数目是", highestlend + "");
				final String maxlend ="最大借书数目是："+ highestlend + "";

				// 获取用户当前排行
				int tmp;
				for (int i = 0; i < lendarry.length; i++) {
					for (int j = 0; j < lendarry.length - 1; j++) {
						if (lendarry[j] < lendarry[j + 1]) {
							tmp = lendarry[j];
							lendarry[j] = lendarry[j + 1];
							lendarry[j + 1] = tmp;
						}
					}
				}
				final User user = userManager.getCurrentUser(User.class);
				int userlend = user.getLendnum();
				final String userlends = "我的借书量是："+userlend;
				int paiming = 0;
				for (int i = 0; i < usernum; i++) {
					if (userlend == lendarry[i]) {
						paiming = i + 1;
						break;
					}
				}
				Log.d("用户当前的借阅排名是", paiming + "");
				final String nowpaihang = "我的借阅排名是:"+paiming + "";

				// 获取和第一名的差距
				final String chaju ="和第一名的借阅差距为"+ (highestlend - userlend) + "";
				
				
				
				new Thread(new Runnable() {

					@Override
					public void run() {

						//获取全校的排名
						LendInfo lendInfo=new LendInfo();
						rank=lendInfo.show();
//						Toast.makeText(MainActivity.this, rank, Toast.LENGTH_SHORT).show();
						final String rank1="排名在全校"+rank+"的人之前";
						
						final String[] suoyou = {usernums, userlends,  nowpaihang,maxlend,chaju,rank1};

						Message message = new Message();
						message.obj = suoyou;
						message.what = SHOW_LIST;
						handler.sendMessage(message);
					}
				}).start();

			}

			@Override
			public void onError(int code, String arg0) {

			}
		});
		return user;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (BmobDB.create(this).hasUnReadMsg()) {// 小圆点提示
			iv_recent_tips.setVisibility(View.VISIBLE);
		} else {
			iv_recent_tips.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_contact_tips.setVisibility(View.VISIBLE);
		} else {
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		MyMessageReceiver.mNewNum = 0;// 清空
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);
	}

	private void refreshNewMsg(BmobMsg message) {// 刷新界面
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();// 声音提示
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		if (message != null) {// 也要存储起来
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (currentTabIndex == 0) {
			if (recentFragment != null) {// 当前页面如果为会话页面，刷新此页面
				recentFragment.refresh();
			}
		}
	}

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		newReceiver = new NewBroadcastReceiver();// 注册接收消息广播
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		intentFilter.setPriority(3);// 优先级要低于ChatActivity
		registerReceiver(newReceiver, intentFilter);
	}

	private class NewBroadcastReceiver extends BroadcastReceiver {// 新消息广播接收者
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshNewMsg(null);// 刷新界面
			abortBroadcast();// 记得把广播给终结掉
		}
	}

	TagBroadcastReceiver userReceiver;

	private void initTagMessageBroadCast() {
		userReceiver = new TagBroadcastReceiver();// 注册接收消息广播
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		intentFilter.setPriority(3);// 优先级要低于ChatActivity
		registerReceiver(userReceiver, intentFilter);
	}

	private class TagBroadcastReceiver extends BroadcastReceiver {// 标签消息广播接收者
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			abortBroadcast();// 记得把广播给终结掉
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		refreshInvite(message);
	}

	private void refreshInvite(BmobInvitation message) {// 刷新好友请求
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if (currentTabIndex == 1) {
			if (contactFragment != null) {
				contactFragment.refresh();
			}
		} else {// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
					.isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
	}

	private static long firstTime;

	@Override
	public void onBackPressed() {// 连续按两次返回键就退出
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		BmobChat.getInstance(this).stopPollService();// 取消定时检测服务
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_mylibrary:
			intent = new Intent(MainActivity.this, LoginLibraryActivity.class);
			/*startActivityForResult(intent, HIDE_MYLIBRARY_BUTTON);*/
			break;
		case R.id.btn_set_list:
			intent = new Intent(MainActivity.this, SettingsActivity.class);
			
		}
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(MainActivity.this,
				LoginLibraryActivity.class);
		;
		switch (position) {
		case 0:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, ReaderInfoActivity.class);
			} else {
				ShowToast("请先登录");
			}
			break;
		case 1:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, NowLendActivity.class);
			} else {
				ShowToast("请先登录");
			}

			break;
		case 2:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, HistBookActivity.class);
			} else {
				ShowToast("请先登录");
			}
			break;
		case 3:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, AsordActivity.class);
			} else {
				ShowToast("请先登录");
			}
			break;
		case 4:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, PregBookActivity.class);
			} else {
				ShowToast("请先登录");
			}
			break;
		}
		startActivity(intent);
	}

}
