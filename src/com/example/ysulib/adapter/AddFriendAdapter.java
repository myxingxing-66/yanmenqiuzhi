package com.example.ysulib.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;

import com.example.ysulib.R;
import com.example.ysulib.adapter.base.BaseListAdapter;
import com.example.ysulib.adapter.base.ViewHolder;
import com.example.ysulib.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//���Һ���
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {

	public AddFriendAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobChatUser contract = getList().get(arg0);
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);
		
		Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

		String avatar = contract.getAvatar();

		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}

		name.setText(contract.getUsername());
		btn_add.setText("���");
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final ProgressDialog progress = new ProgressDialog(mContext);
				progress.setMessage("�������...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				//����tag����
				BmobChatManager.getInstance(mContext).sendTagMessage(MsgTag.ADD_CONTACT, contract.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("��������ɹ����ȴ��Է���֤!");
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
						progress.dismiss();
						ShowToast("��������ʧ�ܣ����������!");
						ShowLog("��������ʧ��:"+arg1);
					}
				});
			}
		});
		return convertView;
	}
}
