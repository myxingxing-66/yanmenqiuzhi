package com.example.ysulib.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

import com.example.ysulib.R;
import com.example.ysulib.adapter.base.BaseListAdapter;
import com.example.ysulib.adapter.base.ViewHolder;
import com.example.ysulib.ui.ImageBrowserActivity;
import com.example.ysulib.ui.SetMyInfoActivity;
import com.example.ysulib.util.ImageLoadOptions;
import com.example.ysulib.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


//聊天适配器
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {

	//文本
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;
	//图片
	private final int TYPE_SEND_IMAGE = 2;
	private final int TYPE_RECEIVER_IMAGE = 3;
	
	String currentObjectId = "";

	DisplayImageOptions options;
	
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public MessageChatAdapter(Context context,List<BmobMsg> msgList) {
		super(context, msgList);
		currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
		
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
	}

	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = list.get(position);
		if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
		}else{
		    return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}
	
	private View createViewByType(BmobMsg message, int position) {
		int type = message.getMsgType();
	   if(type==BmobConfig.TYPE_IMAGE){//图片类型
			return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? mInflater.inflate(R.layout.item_chat_received_image, null) :mInflater.inflate(R.layout.item_chat_sent_image, null);
		}else{//剩下默认的都是文本
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? mInflater.inflate(R.layout.item_chat_received_message, null) :mInflater.inflate(R.layout.item_chat_sent_message, null);
		}
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		final BmobMsg item = list.get(position);
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		//文本类型
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);//失败重发
		final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);//发送状态
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		//图片
		ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
		final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);//进度条
		
		//点击头像进入个人资料
		String avatar = item.getBelongAvatar();
		if(avatar!=null && !avatar.equals("")){//加载头像-为了不每次都加载头像
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions(),animateFirstListener);
		}else{
			iv_avatar.setImageResource(R.drawable.head);
		}
		
		iv_avatar.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				Intent intent =new Intent(mContext,SetMyInfoActivity.class);
				if(getItemViewType(position) == TYPE_RECEIVER_TXT ||getItemViewType(position) == TYPE_RECEIVER_IMAGE){
					intent.putExtra("from", "other");
					intent.putExtra("username", item.getBelongUsername());
				}else{
					intent.putExtra("from", "me");
				}
				mContext.startActivity(intent);
			}
		});
		
		tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));
		
		if(getItemViewType(position)==TYPE_SEND_TXT			//||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
				){//只有自己发送的消息才有重发机制
			//状态描述
			if(item.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){//发送成功
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已发送");
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_FAIL){//服务器无响应或者查询失败等原因造成的发送失败，均需要重发
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){//对方已接收到
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已接收");
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_START){//开始上传
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}
		}
		//根据类型显示内容
		final String text = item.getContent();
		switch (item.getMsgType()) {
		case BmobConfig.TYPE_TEXT:
			try {
				tv_message.setText(text);
			} catch (Exception e) {
			}
			break;
		case BmobConfig.TYPE_IMAGE://图片类
			try {
				if (text != null && !text.equals("")) {//发送成功之后存储的图片类型的content和接收到的是不一样的
					dealWithImage(position, progress_load, iv_fail_resend, tv_send_status, iv_picture, item);
				}
				iv_picture.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View arg0) {
						Intent intent =new Intent(mContext,ImageBrowserActivity.class);
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(getImageUrl(item));
						intent.putStringArrayListExtra("photos", photos);
						intent.putExtra("position", 0);
						mContext.startActivity(intent);
					}
				});			
			} catch (Exception e) {
			}
			break;
		default:
			break;
		}
		return convertView;
	}
	
	//获取图片的地址--
	private String getImageUrl(BmobMsg item){
		String showUrl = "";
		String text = item.getContent();
		if(item.getBelongId().equals(currentObjectId)){//
			if(text.contains("&")){
				showUrl = text.split("&")[0];
			}else{
				showUrl = text;
			}
		}else{//如果是收到的消息，则需要从网络下载
			showUrl = text;
		}
		return showUrl;
	}
	
	// 处理图片
	private void dealWithImage(int position,final ProgressBar progress_load,ImageView iv_fail_resend,TextView tv_send_status,ImageView iv_picture,BmobMsg item){
		String text = item.getContent();
		if(getItemViewType(position)==TYPE_SEND_IMAGE){//发送的消息
			if(item.getStatus()==BmobConfig.STATUS_SEND_START){
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已发送");
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_FAIL){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已接收");
			}
//			如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
			String showUrl = "";
			if(text.contains("&")){
				showUrl = text.split("&")[0];
			}else{
				showUrl = text;
			}
			//为了方便每次都是取本地图片显示
			ImageLoader.getInstance().displayImage(showUrl, iv_picture);
		}else{
			ImageLoader.getInstance().displayImage(text, iv_picture,options,new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					progress_load.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
					progress_load.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					progress_load.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					progress_load.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
