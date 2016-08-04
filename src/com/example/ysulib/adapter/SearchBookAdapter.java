package com.example.ysulib.adapter;

import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.bean.SearchBookItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchBookAdapter extends ArrayAdapter<SearchBookItem> {
	
	private int resourceId;
	
	public SearchBookAdapter(Context context, int textViewResourceId,List<SearchBookItem> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchBookItem searchBookItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView bookTitle = (TextView)view.findViewById(R.id.title_textView);
		TextView bookAuthor = (TextView)view.findViewById(R.id.author_textView);
		TextView bookISBN = (TextView)view.findViewById(R.id.ISBN_textView);
		TextView bookBorrow = (TextView)view.findViewById(R.id.borrow_textView);
		
		bookTitle.setText(searchBookItem.getTitletextView());
		bookAuthor.setText(searchBookItem.getAuthortextView());
		bookISBN.setText(searchBookItem.getISBNtextView());
		bookBorrow.setText(searchBookItem.getBorrowtextView());
		
		return view;
	}
	
}
