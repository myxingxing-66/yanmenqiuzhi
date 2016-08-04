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
	private String[] myItem = { "֤����Ϣ", "��ǰ����", "������ʷ", "������ʷ", "ԤԼ��Ϣ"};
	private int[] myImgID={R.drawable.idd,R.drawable.now,R.drawable.history,R.drawable.tuijian,R.drawable.yuyue};
	ImageView iv_recent_tips, iv_contact_tips;// ��Ϣ��ʾ
	public static final int HIDE_MYLIBRARY_BUTTON = 1;
	private static String[] chaju;// �͵�һ���Ĳ��
	public static final int SHOW_LIST = 0;
	public static final int REFESH_DISCUSS=9;
	public static String rank;
	
	//��������
	String date="";//ʱ��
	int givemonth=0;//�黹�·�
	int giveday=0;//�黹��
	int nowmonth=0;//���ڵ��·�
	int nowday=0;//������
	List<NowLend> infos = new ArrayList<NowLend>();
	
	
	//��������
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
								new AlertDialog.Builder(MainActivity.this).setTitle("ͼ�鵽��").setItems(
									     new String[] {"����ͼ��"+(giveday - nowday)+"�����"}, null).setNegativeButton(
									     "ȷ��", null).show();
							}
							
						}else if((givemonth-nowmonth)==0){
							if ((giveday - nowday) <=5 &&0<=giveday-nowday) {
								new AlertDialog.Builder(MainActivity.this).setTitle("ͼ�鵽��").setItems(
									     new String[] {"����ͼ��"+(giveday - nowday)+"���ں���"}, null).setNegativeButton(
									     "ȷ��", null).show();
							}else if((giveday - nowday)<0){
								new AlertDialog.Builder(MainActivity.this).setTitle("ͼ�鳬��").setItems(
									     new String[] {"����ͼ���Ѿ�����"}, null).setNegativeButton(
									     "ȷ��", null).show();
							}							
						}else{
							new AlertDialog.Builder(MainActivity.this).setTitle("ͼ�鳬��").setItems(
								     new String[] {"����ͼ���Ѿ�����"}, null).setNegativeButton(
								     "ȷ��", null).show();
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
					Log.d("ÿ������",a);
				}
				
				new AlertDialog.Builder(MainActivity.this).setTitle("�������������")
				.setItems(chaju, null)
				.setNegativeButton("ȷ��", null).show();
				
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BmobChat.getInstance(this).startPollService(30);// ������ʱ�����񣨵�λΪ�룩-���������̨�Ƿ���δ������Ϣ���еĻ���ȡ����
		// �����㲥������

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
				btn_mylibrary.setText("�ѵ�¼");
				btn_mylibrary.setEnabled(false);
				getLendNumber();
				YAN=false;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
				date=df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
				nowmonth=Integer.parseInt(date.substring(5, 7));
				nowday=Integer.parseInt(date.substring(8, 10));
				show();		//��������		
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	//�����Ƿ������Ϣ��������
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
		mTabs[2].setSelected(true);// �ѵ�һ��tab��Ϊѡ��״̬
	}

	private void initTab() {
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		bookFragment = new BookFragment();
		fragments = new Fragment[] { recentFragment, contactFragment,
				bookFragment };
		// �����ʾ��һ��fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, recentFragment)
				.add(R.id.fragment_container, contactFragment)
				.add(R.id.fragment_container, bookFragment)
				.hide(recentFragment)
				.hide(contactFragment)
				.show(bookFragment)
				.commit();
	}

	public void onTabSelect(View view) {// button����¼�
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
		mTabs[index].setSelected(true);// �ѵ�ǰtab��Ϊѡ��״̬
		currentTabIndex = index;
	}

	// ��ѯ�����û��Ľ������������������һ��������
	private User getLendNumber() {
		User user = new User();
		BmobQuery<User> query = new BmobQuery<User>();
		// ִ�в�ѯ����һ������Ϊ�����ģ��ڶ�������Ϊ���ҵĻص�
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onSuccess(List<User> losts) {
				Log.d("�û�������", losts.size() + "");
				final int usernum = losts.size();
				final String usernums = "�û���������"+usernum + "";

				int lendarry[] = new int[usernum];
				// �����������������
				for (int i = 0; i < losts.size(); i++) {

					int a = losts.get(i).getLendnum();
					lendarry[i] = a;
					Log.d("δ����Ľ�����", a + "");
				}
				// �õ���������Ŀ
				int highestlend = lendarry[0];
				for (int i = 1; i < usernum; i++) {
					if (lendarry[i] > highestlend) {
						highestlend = lendarry[i];
					}
				}
				Log.d("��������Ŀ��", highestlend + "");
				final String maxlend ="��������Ŀ�ǣ�"+ highestlend + "";

				// ��ȡ�û���ǰ����
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
				final String userlends = "�ҵĽ������ǣ�"+userlend;
				int paiming = 0;
				for (int i = 0; i < usernum; i++) {
					if (userlend == lendarry[i]) {
						paiming = i + 1;
						break;
					}
				}
				Log.d("�û���ǰ�Ľ���������", paiming + "");
				final String nowpaihang = "�ҵĽ���������:"+paiming + "";

				// ��ȡ�͵�һ���Ĳ��
				final String chaju ="�͵�һ���Ľ��Ĳ��Ϊ"+ (highestlend - userlend) + "";
				
				
				
				new Thread(new Runnable() {

					@Override
					public void run() {

						//��ȡȫУ������
						LendInfo lendInfo=new LendInfo();
						rank=lendInfo.show();
//						Toast.makeText(MainActivity.this, rank, Toast.LENGTH_SHORT).show();
						final String rank1="������ȫУ"+rank+"����֮ǰ";
						
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
		if (BmobDB.create(this).hasUnReadMsg()) {// СԲ����ʾ
			iv_recent_tips.setVisibility(View.VISIBLE);
		} else {
			iv_recent_tips.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_contact_tips.setVisibility(View.VISIBLE);
		} else {
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
		MyMessageReceiver.mNewNum = 0;// ���
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// ȡ���������͵���Ϣ
	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);
	}

	private void refreshNewMsg(BmobMsg message) {// ˢ�½���
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();// ������ʾ
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		if (message != null) {// ҲҪ�洢����
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (currentTabIndex == 0) {
			if (recentFragment != null) {// ��ǰҳ�����Ϊ�Ựҳ�棬ˢ�´�ҳ��
				recentFragment.refresh();
			}
		}
	}

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		newReceiver = new NewBroadcastReceiver();// ע�������Ϣ�㲥
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		intentFilter.setPriority(3);// ���ȼ�Ҫ����ChatActivity
		registerReceiver(newReceiver, intentFilter);
	}

	private class NewBroadcastReceiver extends BroadcastReceiver {// ����Ϣ�㲥������
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshNewMsg(null);// ˢ�½���
			abortBroadcast();// �ǵðѹ㲥���ս��
		}
	}

	TagBroadcastReceiver userReceiver;

	private void initTagMessageBroadCast() {
		userReceiver = new TagBroadcastReceiver();// ע�������Ϣ�㲥
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		intentFilter.setPriority(3);// ���ȼ�Ҫ����ChatActivity
		registerReceiver(userReceiver, intentFilter);
	}

	private class TagBroadcastReceiver extends BroadcastReceiver {// ��ǩ��Ϣ�㲥������
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			abortBroadcast();// �ǵðѹ㲥���ս��
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

	private void refreshInvite(BmobInvitation message) {// ˢ�º�������
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
		} else {// ͬʱ����֪ͨ
			String tickerText = message.getFromname() + "������Ӻ���";
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
	public void onBackPressed() {// ���������η��ؼ����˳�
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("�ٰ�һ���˳�����");
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
		BmobChat.getInstance(this).stopPollService();// ȡ����ʱ������
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
				ShowToast("���ȵ�¼");
			}
			break;
		case 1:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, NowLendActivity.class);
			} else {
				ShowToast("���ȵ�¼");
			}

			break;
		case 2:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, HistBookActivity.class);
			} else {
				ShowToast("���ȵ�¼");
			}
			break;
		case 3:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, AsordActivity.class);
			} else {
				ShowToast("���ȵ�¼");
			}
			break;
		case 4:
			if (YANZHENG) {
				intent = new Intent(MainActivity.this, PregBookActivity.class);
			} else {
				ShowToast("���ȵ�¼");
			}
			break;
		}
		startActivity(intent);
	}

}
