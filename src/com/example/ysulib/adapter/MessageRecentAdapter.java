package com.example.ysulib.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

import com.example.ysulib.R;
import com.example.ysulib.adapter.base.ViewHolder;
import com.example.ysulib.util.ImageLoadOptions;
import com.example.ysulib.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

// 会话适配器
public class MessageRecentAdapter extends ArrayAdapter<BmobRecent> implements Filterable{
	
	private LayoutInflater inflater;
	private List<BmobRecent> mData;
	private Context mContext;
	
	public MessageRecentAdapter(Context context, int textViewResourceId, List<BmobRecent> objects) {
		super(context, textViewResourceId, objects);
		inflater = LayoutInflater.from(context);
		this.mContext = context;
		mData = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BmobRecent item = mData.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_conversation, parent, false);
		}
		ImageView iv_recent_avatar = ViewHolder.get(convertView, R.id.iv_recent_avatar);
		TextView tv_recent_name = ViewHolder.get(convertView, R.id.tv_recent_name);
		TextView tv_recent_msg = ViewHolder.get(convertView, R.id.tv_recent_msg);
		TextView tv_recent_time = ViewHolder.get(convertView, R.id.tv_recent_time);
		TextView tv_recent_unread = ViewHolder.get(convertView, R.id.tv_recent_unread);
		
		//填充数据
		String avatar = item.getAvatar();
		if(avatar!=null&& !avatar.equals("")){
			ImageLoader.getInstance().displayImage(avatar, iv_recent_avatar, ImageLoadOptions.getOptions());
		}else{
			iv_recent_avatar.setImageResource(R.drawable.head);
		}
		
		tv_recent_name.setText(item.getUserName());
		tv_recent_time.setText(TimeUtil.getChatTime(item.getTime()));
		//显示内容
		if(item.getType()==BmobConfig.TYPE_TEXT){
			tv_recent_msg.setText(item.getMessage());
		}else if(item.getType()==BmobConfig.TYPE_IMAGE){
			tv_recent_msg.setText("[图片]");
		}
		
		int num = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
		if (num > 0) {
			tv_recent_unread.setVisibility(View.VISIBLE);
			tv_recent_unread.setText(num + "");
		} else {
			tv_recent_unread.setVisibility(View.GONE);
		}
		return convertView;
	}

}
