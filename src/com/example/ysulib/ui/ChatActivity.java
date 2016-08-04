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


// �������
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
		// ��װ�������
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		targetId = targetUser.getObjectId();
		//ע��㲥������
		initNewMessageBroadCast();
		initView();
	}

	private void initView() {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mListView = (XListView) findViewById(R.id.mListView);
		initTopBarForLeft("��" + targetUser.getUsername() + "�Ի�");
		initBottomView();
		initXListView();
	}

	//������Ϣ��ʷ�������ݿ��ж���
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
		return list;
	}

	 //����ˢ��
	private void initOrRefresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// ���ڸ��µ���������������ڼ�������Ϣ����ʱ�ٻص�����ҳ���ʱ����Ҫ��ʾ��������Ϣ
				int news=  MyMessageReceiver.mNewNum;//�п��������ڼ䣬����N����Ϣ,�����Ҫ������ʾ�ڽ�����
				int size = initMsgData().size();
				for(int i=(news-1);i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// ������һ����Ϣ��������ʾ
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
		// �����
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_add.setOnClickListener(this);
		// ���ұ�
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		// ������
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		initAddView();

		// ���м�
		// �����
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
		// ���Ȳ�������ظ���
		mListView.setPullLoadEnable(false);
		// ��������
		mListView.setPullRefreshEnable(true);
		// ���ü�����
		mListView.setXListViewListener(this);
		mListView.pullRefreshing();
		mListView.setDividerHeight(0);
		// ��������
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

		// �ط���ť�ĵ���¼�
		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,new MessageChatAdapter.onInternalClickListener() {
					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						// �ط���Ϣ
						showResendDialog(parentV, v, values);
					}
				});
	}

	 //��ʾ�ط���ť showResendDialog
	public void showResendDialog(final View parentV, View v, final Object values) {
		DialogTips dialog = new DialogTips(this, "ȷ���ط�����Ϣ", "ȷ��", "ȡ��", "��ʾ",
				true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE) {// ͼƬ���������͵Ĳ���
					resendFileMsg(parentV, values);
				} else {
					resendTextMsg(parentV, values);
				}
				dialogInterface.dismiss();
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	// �ط��ı���Ϣ
	private void resendTextMsg(final View parentV, final Object values) {
		BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(
				targetUser, (BmobMsg) values, new PushListener() {

					@Override
					public void onSuccess() {
						ShowLog("���ͳɹ�");
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
						((TextView) parentV.findViewById(R.id.tv_send_status)).setText("�ѷ���");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowLog("����ʧ��:" + arg1);
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}

	//�ط�ͼƬ��Ϣ
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
							((TextView) parentV.findViewById(R.id.tv_send_status)).setText("�ѷ���");
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
		case R.id.edit_user_comment:// ����ı������
			mListView.setSelection(mListView.getCount() - 1);
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_add:// ��Ӱ�ť-��ʾͼƬ������
			if (layout_more.getVisibility() == View.GONE) {
				layout_more.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.VISIBLE);
				hideSoftInputView();
			}
			break;
		case R.id.btn_chat_send:// �����ı�
			final String msg = edit_user_comment.getText().toString();
			if (msg.equals("")) {
				ShowToast("�����뷢����Ϣ!");
				return;
			}
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected) {
				ShowToast(R.string.network_tips);
			}
			// ��װBmobMessage����
			BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
			// Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
			manager.sendTextMessage(targetUser, message);
			// ˢ�½���
			refreshMessage(message);
			break;
		case R.id.tv_camera:// ����
			try {
				selectImageFromCamera();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.tv_picture:// ͼƬ
			selectImageFromLocal();
			break;
		default:
			break;
		}
	}


	private String localCameraPath = "";// ���պ�õ���ͼƬ��ַ

	 //����������� startCamera
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

	//ѡ��ͼƬ
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
			case BmobConstants.REQUESTCODE_TAKE_CAMERA:// ��ȡ��ֵ��ʱ����ϴ�path·���µ�ͼƬ��������
				ShowLog("����ͼƬ�ĵ�ַ��" + localCameraPath);
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
							ShowToast("�Ҳ�������Ҫ��ͼƬ");
							return;
						}
						sendImageMessage(localSelectPath);
					}
				}
				break;
			}
		}
	}

	// Ĭ�����ϴ�����ͼƬ��֮�����ʾ���� sendImageMessage
	private void sendImageMessage(String local) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
		}
		manager.sendImageMessage(targetUser, local, new UploadListener() {

			@Override
			public void onStart(BmobMsg msg) {
				ShowLog("��ʼ�ϴ�onStart��" + msg.getContent() + ",״̬��"
						+ msg.getStatus());
				refreshMessage(msg);
			}

			@Override
			public void onSuccess() {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(int error, String arg1) {
				ShowLog("�ϴ�ʧ�� -->arg1��" + arg1);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	// ��ʾ�����
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edit_user_comment, 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ����Ϣ�������ˢ�½���
		initOrRefresh();
		MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
		// �п��������ڼ䣬������������֪ͨ������ʱ����Ҫ���֪ͨ�����δ����Ϣ��
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(targetId);
		//�����Ϣδ����-���Ҫ��ˢ��֮��
		MyMessageReceiver.mNewNum=0;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// �������͵���Ϣ
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
				if (!uid.equals(targetId))// ������ǵ�ǰ��������������Ϣ��������
					return;
				mAdapter.add(m);
				// ��λ
				mListView.setSelection(mAdapter.getCount() - 1);
				//ȡ����ǰ��������δ����ʾ
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
		}
	};

	public static final int NEW_MESSAGE = 0x001;// �յ���Ϣ
	
	NewBroadcastReceiver  receiver;
	
	private void initNewMessageBroadCast(){
		// ע�������Ϣ�㲥
		receiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//���ù㲥�����ȼ������Mainacitivity,���������Ϣ����ʱ��������chatҳ�棬ֱ����ʾ��Ϣ����������ʾ��Ϣδ��
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}
	
	// ����Ϣ�㲥������
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			// �յ�����㲥��ʱ��message�Ѿ�����Ϣ���У���ֱ�ӻ�ȡ
			BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
			if (!from.equals(targetId))// ������ǵ�ǰ��������������Ϣ��������
				return;
			//��ӵ���ǰҳ��
			mAdapter.add(msg);
			// ��λ
			mListView.setSelection(mAdapter.getCount() - 1);
			//ȡ����ǰ��������δ����ʾ
			BmobDB.create(ChatActivity.this).resetUnread(targetId);
			// �ǵðѹ㲥���ս��
			abortBroadcast();
		}
	}
	
	//ˢ�½���
	private void refreshMessage(BmobMsg msg) {
		// ���½���
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
		// �˴�Ӧ�ù��˵����Ǻ͵�ǰ�û�������Ļ�ִ��Ϣ�����ˢ��
		if (conversionId.split("&")[1].equals(targetId)) {
			// �޸Ľ�����ָ����Ϣ���Ķ�״̬
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
				BmobLog.i("��¼������" + total);
				int currents = mAdapter.getCount();
				if (total <= currents) {
					ShowToast("�����¼��������Ŷ!");
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
