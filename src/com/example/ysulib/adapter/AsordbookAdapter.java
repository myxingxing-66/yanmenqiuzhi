package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.bean.BookAsord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AsordbookAdapter extends ArrayAdapter<BookAsord> {
	
	private int resourceId;

	public AsordbookAdapter(Context context, int resource, List<BookAsord> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BookAsord bookItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView name = (TextView)view.findViewById(R.id.name);
		TextView author = (TextView)view.findViewById(R.id.author);
		TextView publish = (TextView)view.findViewById(R.id.publish);
		TextView asordday = (TextView)view.findViewById(R.id.asordday);
		TextView now= (TextView)view.findViewById(R.id.now);
		TextView beizhu = (TextView)view.findViewById(R.id.beizhu);
				
		name.setText(bookItem.getName());
		publish.setText(bookItem.getPublish());
		author.setText(bookItem.getAuthor());
		asordday.setText(bookItem.getAsordday());
		now.setText(bookItem.getNow());
		beizhu.setText(bookItem.getBeizhu());
		
		return view;
	}
}
