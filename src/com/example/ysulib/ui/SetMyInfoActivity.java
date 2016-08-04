package com.example.ysulib.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.example.ysulib.R;
import com.example.ysulib.bean.LendInfo;
import com.example.ysulib.bean.User;
import com.example.ysulib.config.BmobConstants;
import com.example.ysulib.util.ImageLoadOptions;
import com.example.ysulib.util.PhotoUtil;
import com.example.ysulib.util.ScarddUrlUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


 //个人资料页面
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("SimpleDateFormat")
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

	TextView tv_set_name, tv_set_nick, tv_set_gender,tv_set_lendnum,tv_set_lendnumb,tv_set_usernum,tv_set_paiming,tv_set_maxlend,tv_set_chaju,tv_set_usernu,tv_set_paimin,tv_set_maxlen,tv_set_chaj,tv_set_rank;
	ImageView iv_set_avator, iv_arraw, iv_nickarraw;
	LinearLayout layout_all;
	
	Button btn_chat, btn_add_friend;
	RelativeLayout layout_head, layout_nick, layout_gender, layout_black_tips;

	String from = "";
	String username = "";
	User user;
	
	public static String rank;
	
	private static String[] suoyou;//和第一名的差距
	
	public static final int SHOW_LIST = 0;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) {
			case SHOW_LIST:
				suoyou=(String[]) msg.obj;
				for(String a:suoyou){
					Log.d("每条数据",a);
				}
				tv_set_usernum.setText(suoyou[1]);
				tv_set_maxlend.setText(suoyou[2]);
				tv_set_paiming.setText(suoyou[3]);
				tv_set_chaju.setText(suoyou[4]);
				tv_set_rank.setText(suoyou[5]);
				break;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
//		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//		if (currentapiVersion >= 14) {
//			getWindow().getDecorView().setSystemUiVisibility(
//					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//		}
		setContentView(R.layout.activity_set_info);
		from = getIntent().getStringExtra("from");
		username = getIntent().getStringExtra("username");
		initView();
		getLendNumber();		
	}
	
	
	//查询所有用户的借书量并将结果保存在一个数组中
	private User getLendNumber(){
		User user=new User();
		BmobQuery<User> query = new BmobQuery<User>();
		 //执行查询，第一个参数为上下文，第二个参数为查找的回调
        query.findObjects(this, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> losts) {
            	Log.d("用户的数量",losts.size()+"");
            	final int usernum=losts.size();
            	final String usernums=usernum+"";
            	
            	
            	
            	int lendarry[]=new int[usernum];
                //将结果保存在数组中
                for(int i=0;i<losts.size();i++){
                	
                	int a=losts.get(i).getLendnum();     
                	lendarry[i]=a;
                	Log.d("未排序的借阅量",a+"");
                }
             //得到最大借书数目   
                int highestlend=lendarry[0];
        		for(int i=1;i<usernum;i++){
        			if(lendarry[i]>highestlend){
        				highestlend=lendarry[i];
        			}
        		}
                Log.d("最大借书数目是",highestlend+"");
                final String maxlend=highestlend+"";
               
             //获取用户当前排行
                int tmp;
           	 for (int i = 0; i < lendarry.length; i++) {
           	  for (int j = 0; j < lendarry.length-1; j++) {
           	   if(lendarry[j]<lendarry[j+1]){
           	    tmp=lendarry[j];
           	 lendarry[j]=lendarry[j+1];
           	lendarry[j+1]=tmp;
           	   }
           	  }
           	 }	 
           	final User user = userManager.getCurrentUser(User.class);
    		int userlend=user.getLendnum();
    		final String userlends=userlend+"";
    		int paiming=0;
    		for(int i=0;i<usernum;i++){
    			if(userlend==lendarry[i]){
    				paiming=i+1;
    				break;
    			}
    		} 
             Log.d("用户当前的借阅排名是",paiming+"");  
             final String nowpaihang=paiming+"";
           
             //获取和第一名的差距
            final String chaju=(highestlend-userlend)+"";
            new Thread(new Runnable(){  
     			  
     			@Override  
     			public void run() {  
     				
     				//获取全校的排名
					LendInfo lendInfo=new LendInfo();
					rank=lendInfo.show();
//					Toast.makeText(MainActivity.this, rank, Toast.LENGTH_SHORT).show();
					rank="排名在全校"+rank+"的人之前";
					
					final String[] suoyou = {userlends,usernums, maxlend,  nowpaihang,chaju,rank};
     				
     				Message message = new Message();
     				message.obj=suoyou;
     				message.what =SHOW_LIST;
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
	


	private void initView() {
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
		iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		tv_set_lendnum=(TextView) findViewById(R.id.tv_set_lendnum);
		tv_set_usernum=(TextView) findViewById(R.id.tv_set_usernum);
		tv_set_maxlend=(TextView) findViewById(R.id.tv_set_maxlend);
		tv_set_paiming=(TextView) findViewById(R.id.tv_set_paiming);
		tv_set_chaju=(TextView) findViewById(R.id.tv_set_chaju);
		tv_set_rank=(TextView) findViewById(R.id.tv_set_rank);
		tv_set_usernu=(TextView) findViewById(R.id.tv_set_usernu);
		tv_set_maxlen=(TextView) findViewById(R.id.tv_set_maxlen);
		tv_set_paimin=(TextView) findViewById(R.id.tv_set_paimin);
		tv_set_chaj=(TextView) findViewById(R.id.tv_set_chaj);
		tv_set_lendnumb=(TextView) findViewById(R.id.tv_set_lendnumb);
		layout_head = (RelativeLayout) findViewById(R.id.layout_head);
		layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
		
	//设置借阅量	
		final User user = userManager.getCurrentUser(User.class);
		String s=user.getLendnum()+"";
		

		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		if (from.equals("me")) {
			initTopBarForLeft("个人资料");
			layout_head.setOnClickListener(this);
			layout_nick.setOnClickListener(this);
			layout_gender.setOnClickListener(this);
			iv_nickarraw.setVisibility(View.VISIBLE);
			iv_arraw.setVisibility(View.VISIBLE);
			tv_set_lendnum.setText(s);
			btn_chat.setVisibility(View.GONE);
			btn_add_friend.setVisibility(View.GONE);
		} else {
			initTopBarForLeft("详细资料");
			iv_nickarraw.setVisibility(View.INVISIBLE);
			iv_arraw.setVisibility(View.INVISIBLE);
			tv_set_lendnumb.setVisibility(View.GONE);
			tv_set_usernum.setVisibility(View.GONE);
			tv_set_usernu.setVisibility(View.GONE);
			tv_set_maxlend.setVisibility(View.GONE);
			tv_set_maxlen.setVisibility(View.GONE);
			tv_set_paiming.setVisibility(View.GONE);
			tv_set_paimin.setVisibility(View.GONE);
			tv_set_chaju.setVisibility(View.GONE);
			tv_set_chaj.setVisibility(View.GONE);
			tv_set_rank.setVisibility(View.GONE);
			//不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
			btn_chat.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
				btn_add_friend.setVisibility(View.VISIBLE);
				btn_add_friend.setOnClickListener(this);
			} 
			initOtherData(username);
		}
	}

	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		initOtherData(user.getUsername());
	}

	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {
			@Override
			public void onError(int arg0, String arg1) {
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					btn_chat.setEnabled(true);
					btn_add_friend.setEnabled(true);
					updateUser(user);
				} else {
					ShowLog("onSuccess 查无此人");
				}
			}
		});
	}

	private void updateUser(User user) {
		// 更改
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");
	}

	 //更新头像 refreshAvatar
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,ImageLoadOptions.getOptions());
		} else {
			iv_set_avator.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (from.equals("me")) {
			initMeData();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_chat:// 发起聊天
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", user);
			startAnimActivity(intent);
			finish();
			break;
		case R.id.layout_head:
			showAvatarPop();
			break;
		case R.id.layout_nick:
			startAnimActivity(UpdateInfoActivity.class);
			break;
		case R.id.layout_gender:// 性别
			showSexChooseDialog();
			break;
		case R.id.btn_add_friend:// 添加好友
			addFriend();
			break;
		}
	}
	String[] sexs = new String[]{ "男", "女" };
	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
		.setTitle("单选框")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(sexs, 0,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						BmobLog.i("点击的是"+sexs[which]);
						updateInfo(which);
						dialog.dismiss();
					}
				})
		.setNegativeButton("取消", null)
		.show();
	}

	
	// 修改资料
	private void updateInfo(int which) {
		final User user = userManager.getCurrentUser(User.class);
		BmobLog.i("updateInfo 性别："+user.getSex());
		if(which==0){
			user.setSex(true);
		}else{
			user.setSex(false);
		}
		user.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				ShowToast("修改成功");
				final User u = userManager.getCurrentUser(User.class);
				BmobLog.i("修改成功后的sex = "+u.getSex());
				tv_set_gender.setText(user.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	//添加好友请求
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求
		BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT,
				user.getObjectId(), new PushListener() {

					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
						ShowLog("发送请求失败:" + arg1);
					}
				});
	}

	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	@SuppressWarnings("deprecation")
	private void showAvatarPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,null);
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		layout_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("点击拍照");
				layout_choose.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
				layout_photo.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg_press));
				File dir;
				try {
					dir = new File(ScarddUrlUtil.getExternalSdCardPath()+"/");

				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 原图
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
				filePath = file.getAbsolutePath();// 获取相片的保存路径
				Log.d("存储路径",filePath);
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("点击相册");
				layout_photo.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
				layout_choose.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg_press));
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});

		avatorPop = new PopupWindow(view, mScreenWidth, 600);
		avatorPop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatorPop.dismiss();
					return true;
				}
				return false;
			}
		});

		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
		
	}
	
	
    

	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, requestCode);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				try {
					if (ScarddUrlUtil.getExternalSdCardPath()==null) {
						ShowToast("SD不可用");
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				try {
					if (ScarddUrlUtil.getExternalSdCardPath()==null) {
						ShowToast("SD不可用");
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				return;
			} else {
				try {
					saveCropAvator(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;
		}
	}

	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(mApplication);
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {

			}

			@Override
			public void onFailure(int arg0, String msg) {

				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	private void updateUserAvatar(final String url) {
		User user = (User) userManager.getCurrentUser(User.class);
		user.setAvatar(url);
		user.update(this, new UpdateListener() {
			@Override
			public void onSuccess() {
				ShowToast("头像更新成功！");
				// 更新头像
				refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				ShowToast("头像更新失败：" + msg);
			}
		});
	}

	String path;

	 //保存裁剪的头像

	private void saveCropAvator(Intent data) throws IOException {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg";
				path = ScarddUrlUtil.getExternalSdCardPath()+"/"+ filename;
				PhotoUtil.saveBitmap(ScarddUrlUtil.getExternalSdCardPath(), filename,bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}
}
