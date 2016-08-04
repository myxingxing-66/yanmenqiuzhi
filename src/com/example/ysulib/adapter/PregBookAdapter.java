package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.bean.BookPreg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PregBookAdapter  extends ArrayAdapter<BookPreg>{
	
	private int resourceId;

	public 	PregBookAdapter(Context context, int resource, List<BookPreg> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BookPreg bookItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView author = (TextView)view.findViewById(R.id.author);
		TextView isbn = (TextView)view.findViewById(R.id.isbn);
		TextView pregday = (TextView)view.findViewById(R.id.pregday);
		TextView endday= (TextView)view.findViewById(R.id.endday);
		TextView havebook = (TextView)view.findViewById(R.id.havebook);
		TextView location = (TextView)view.findViewById(R.id.location);
		TextView now = (TextView)view.findViewById(R.id.now);
		
		author.setText(bookItem.getTit_aut());
		isbn.setText(bookItem.getIsbn());
		pregday.setText(bookItem.getPregday());
		endday.setText(bookItem.getEndday());
		havebook.setText(bookItem.getHavebook());
		location.setText(bookItem.getLocation());
		now.setText(bookItem.getNow());
		
		return view;
	}
}
