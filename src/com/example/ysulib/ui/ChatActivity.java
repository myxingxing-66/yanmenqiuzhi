package com.example.ysulib.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.ysulib.MyMessageReceiver;
import com.example.ysulib.R;
import com.example.ysulib.adapter.MessageChatAdapter;
import com.example.ysulib.config.BmobConstants;
import com.example.ysulib.util.CommonUtils;
import com.example.ysulib.util.ScarddUrlUtil;
import com.example.ysulib.view.HeaderLayout;
import com.example.ysulib.view.dialog.DialogTips;
import com.example.ysulib.view.xlist.XListView;
import com.example.ysulib.view.xlist.XListView.IXListViewListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;


// 聊天界面
public class ChatActivity extends ActivityBase implements OnClickListener,IXListViewListener, EventListener {

	private Button  btn_chat_send, btn_chat_add;

	XListView mListView;

	EditText edit_user_comment;

	String targetId = "";
	BmobChatUser targetUser;

	private static int MsgPagerNum;

	private LinearLayout layout_more, layout_add;

	private TextView tv_picture, tv_camera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		manager = BmobChatManager.getInstance(this);
		MsgPagerNum = 0;
		// 组装聊天对象
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		targetId = targetUser.getObjectId();
		//注册广播接收器
		initNewMessageBroadCast();
		initView();
	}

	private void initView() {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mListView = (XListView) findViewById(R.id.mListView);
		initTopBarForLeft("与" + targetUser.getUsername() + "对话");
		initBottomView();
		initXListView();
	}

	//加载消息历史，从数据库中读出
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
		return list;
	}

	 //界面刷新
	private void initOrRefresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
				int size = initMsgData().size();
				for(int i=(news-1);i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
				}
				mListView.setSelection(mAdapter.getCount() - 1);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		} else {
			mAdapter = new MessageChatAdapter(this, initMsgData());
			mListView.setAdapter(mAdapter);
		}
	}

	private void initAddView() {
		tv_picture = (TextView) findViewById(R.id.tv_picture);
		tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_picture.setOnClickListener(this);
		tv_camera.setOnClickListener(this);
	}

	private void initBottomView() {
		// 最左边
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_add.setOnClickListener(this);
		// 最右边
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		// 最下面
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		initAddView();

		// 最中间
		// 输入框
		edit_user_comment = (EditText) findViewById(R.id.edit_user_comment);
		edit_user_comment.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				if (!TextUtils.isEmpty(s)) {
					btn_chat_send.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	MessageChatAdapter mAdapter;
	
	private void initXListView() {
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		mListView.pullRefreshing();
		mListView.setDividerHeight(0);
		// 加载数据
		initOrRefresh();
		mListView.setSelection(mAdapter.getCount() - 1);
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				hideSoftInputView();
				layout_more.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
				btn_chat_send.setVisibility(View.GONE);
				return false;
			}
		});

		// 重发按钮的点击事件
		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,new MessageChatAdapter.onInternalClickListener() {
					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						// 重发消息
						showResendDialog(parentV, v, values);
					}
				});
	}

	 //显示重发按钮 showResendDialog
	public void showResendDialog(final View parentV, View v, final Object values) {
		DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示",
				true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE) {// 图片和语音类型的采用
					resendFileMsg(parentV, values);
				} else {
					resendTextMsg(parentV, values);
				}
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	// 重发文本消息
	private void resendTextMsg(final View parentV, final Object values) {
		BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(
				targetUser, (BmobMsg) values, new PushListener() {

					@Override
					public void onSuccess() {
						ShowLog("发送成功");
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
						((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowLog("发送失败:" + arg1);
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}

	//重发图片消息
	private void resendFileMsg(final View parentV, final Object values) {
		BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(
				targetUser, (BmobMsg) values, new UploadListener() {

					@Override
					public void onStart(BmobMsg msg) {
					}

					@Override
					public void onSuccess() {
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
							parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
							((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_user_comment:// 点击文本输入框
			mListView.setSelection(mListView.getCount() - 1);
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_add:// 添加按钮-显示图片、拍照
			if (layout_more.getVisibility() == View.GONE) {
				layout_more.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.VISIBLE);
				hideSoftInputView();
			}
			break;
		case R.id.btn_chat_send:// 发送文本
			final String msg = edit_user_comment.getText().toString();
			if (msg.equals("")) {
				ShowToast("请输入发送消息!");
				return;
			}
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected) {
				ShowToast(R.string.network_tips);
			}
			// 组装BmobMessage对象
			BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
			// 默认发送完成，将数据保存到本地消息表和最近会话表中
			manager.sendTextMessage(targetUser, message);
			// 刷新界面
			refreshMessage(message);
			break;
		case R.id.tv_camera:// 拍照
			try {
				selectImageFromCamera();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		default:
			break;
		}
	}


	private String localCameraPath = "";// 拍照后得到的图片地址

	 //启动相机拍照 startCamera
	public void selectImageFromCamera() throws IOException {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(ScarddUrlUtil.getExternalSdCardPath()+"/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())+ ".jpg");
		localCameraPath = file.getAbsolutePath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,BmobConstants.REQUESTCODE_TAKE_CAMERA);
	}

	//选择图片
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCAL);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case BmobConstants.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
				ShowLog("本地图片的地址：" + localCameraPath);
				sendImageMessage(localCameraPath);
				break;
			case BmobConstants.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null|| localSelectPath.equals("null")) {
							ShowToast("找不到您想要的图片");
							return;
						}
						sendImageMessage(localSelectPath);
					}
				}
				break;
			}
		}
	}

	// 默认先上传本地图片，之后才显示出来 sendImageMessage
	private void sendImageMessage(String local) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
		}
		manager.sendImageMessage(targetUser, local, new UploadListener() {

			@Override
			public void onStart(BmobMsg msg) {
				ShowLog("开始上传onStart：" + msg.getContent() + ",状态："
						+ msg.getStatus());
				refreshMessage(msg);
			}

			@Override
			public void onSuccess() {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(int error, String arg1) {
				ShowLog("上传失败 -->arg1：" + arg1);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	// 显示软键盘
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edit_user_comment, 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 新消息到达，重新刷新界面
		initOrRefresh();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(targetId);
		//清空消息未读数-这个要在刷新之后
		MyMessageReceiver.mNewNum=0;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 监听推送的消息
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
				if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				mAdapter.add(m);
				// 定位
				mListView.setSelection(mAdapter.getCount() - 1);
				//取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
		}
	};

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	
	NewBroadcastReceiver  receiver;
	
	private void initNewMessageBroadCast(){
		// 注册接收消息广播
		receiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}
	
	// 新消息广播接收者
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			// 收到这个广播的时候，message已经在消息表中，可直接获取
			BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
			if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
				return;
			//添加到当前页面
			mAdapter.add(msg);
			// 定位
			mListView.setSelection(mAdapter.getCount() - 1);
			//取消当前聊天对象的未读标示
			BmobDB.create(ChatActivity.this).resetUnread(targetId);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	//刷新界面
	private void refreshMessage(BmobMsg msg) {
		// 更新界面
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}
	
	@Override
	public void onMessage(BmobMsg message) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		handler.sendMessage(handlerMsg);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation invite) {
	}

	@Override
	public void onOffline() {
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
		if (conversionId.split("&")[1].equals(targetId)) {
			// 修改界面上指定消息的阅读状态
			for (BmobMsg msg : mAdapter.getList()) {
				if (msg.getConversationId().equals(conversionId)
						&& msg.getMsgTime().equals(msgTime)) {
					msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				MsgPagerNum++;
				int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
				BmobLog.i("记录总数：" + total);
				int currents = mAdapter.getCount();
				if (total <= currents) {
					ShowToast("聊天记录加载完了哦!");
				} else {
					List<BmobMsg> msgList = initMsgData();
					mAdapter.setList(msgList);
					mListView.setSelection(mAdapter.getCount() - currents - 1);
				}
				mListView.stopRefresh();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout_more.getVisibility() == 0) {
				layout_more.setVisibility(View.GONE);
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideSoftInputView();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		
	}
}
