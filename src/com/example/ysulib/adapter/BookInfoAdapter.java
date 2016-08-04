package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BookInfoAdapter extends ArrayAdapter<String>{
	private int resourceId;

	public BookInfoAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String bookItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView bookTitle = (TextView)view.findViewById(R.id.tx_info_item);	
		bookTitle.setText(bookItem);	
		return view;
	}

}
