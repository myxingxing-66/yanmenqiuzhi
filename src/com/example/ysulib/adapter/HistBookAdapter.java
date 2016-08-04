package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.bean.BookHist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistBookAdapter extends ArrayAdapter<BookHist> {
	
	private int resourceId;

	public HistBookAdapter(Context context, int resource, List<BookHist> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BookHist bookItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView num = (TextView)view.findViewById(R.id.num);
		TextView name = (TextView)view.findViewById(R.id.name);
		TextView author = (TextView)view.findViewById(R.id.author);
		TextView isbn = (TextView)view.findViewById(R.id.isbn);
		TextView lendday = (TextView)view.findViewById(R.id.lendday);
		TextView giveday= (TextView)view.findViewById(R.id.giveday);
		TextView location = (TextView)view.findViewById(R.id.location);
				
		name.setText(bookItem.getName());
		num.setText(bookItem.getNum());
		author.setText(bookItem.getAuthor());
		isbn.setText(bookItem.getIsbn());
		lendday.setText(bookItem.getLendyear());
		giveday.setText(bookItem.getGiveyear());
		location.setText(bookItem.getLocation());
		
		return view;
	}
}
