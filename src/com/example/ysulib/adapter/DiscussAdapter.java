package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.bean.Discuss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DiscussAdapter extends ArrayAdapter<Discuss> {

	private int resouredId;
	
	public DiscussAdapter(Context context, int textViewResourceId,List<Discuss> objects) {
		super(context, textViewResourceId, objects);
		resouredId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Discuss discuss = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resouredId, null);
		TextView tv_user_name = (TextView)view.findViewById(R.id.tv_user_name);
		TextView tv_discuss_item = (TextView)view.findViewById(R.id.tv_discuss_item);
		
		tv_user_name.setText(discuss.getDiscuss_maker());
		tv_discuss_item.setText(discuss.getDiscuss());
		
		return view;
	}

}
