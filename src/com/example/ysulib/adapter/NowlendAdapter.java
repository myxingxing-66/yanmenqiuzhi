package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.bean.NowLend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NowlendAdapter extends ArrayAdapter<NowLend>{
	private int resourceId;

	public NowlendAdapter(Context context, int resource, List<NowLend> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NowLend bookItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView author = (TextView)view.findViewById(R.id.author);
		TextView isbn = (TextView)view.findViewById(R.id.isbn);
		TextView lendday = (TextView)view.findViewById(R.id.lendday);
		TextView giveday= (TextView)view.findViewById(R.id.giveday);
		TextView canlend = (TextView)view.findViewById(R.id.canlend);
		TextView location = (TextView)view.findViewById(R.id.location);
		TextView other = (TextView)view.findViewById(R.id.other);
			
		author.setText(bookItem.getTit_aut());
		isbn.setText(bookItem.getIsbn());
		lendday.setText(bookItem.getLendday());
		giveday.setText(bookItem.getGiveday());
		canlend.setText(bookItem.getLennum());
		location.setText(bookItem.getLocation());
		other.setText(bookItem.getOther());
		
		return view;
	}
}
