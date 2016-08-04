package com.example.ysulib.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ysulib.R;
import com.example.ysulib.bean.BookDiscuss;

public class BookDiscussListAdapter extends ArrayAdapter<BookDiscuss> {
	
	private int resourceId;
	private MyClickListener mListener;
	
	public BookDiscussListAdapter(Context context, int textViewResourceId,List<BookDiscuss> objects,MyClickListener listener) {
		super(context, textViewResourceId, objects);
		mListener = listener;
		resourceId = textViewResourceId;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BookDiscuss bookDiscuss = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		
		//ImageView user_avatar = (ImageView)view.findViewById(R.id.book_user_avatar);
		TextView tv_book_user = (TextView)view.findViewById(R.id.tv_book_user);
		TextView tv_book_title = (TextView)view.findViewById(R.id.tv_book_title);
		TextView tv_book_author = (TextView)view.findViewById(R.id.tv_book_author);
		TextView tv_discuss_num = (TextView)view.findViewById(R.id.tv_discuss_num);
		TextView tv_up = (TextView)view.findViewById(R.id.tv_up);
		TextView tv_discuss = (TextView)view.findViewById(R.id.tv_discuss);
		
		Button btn_up = (Button)view.findViewById(R.id.btn_up);
		btn_up.setOnClickListener(mListener);
		btn_up.setTag(position);
		
		tv_book_user.setText(bookDiscuss.getBook_user());
		tv_book_title.setText(bookDiscuss.getBook_title());
		tv_book_author.setText(bookDiscuss.getBook_author());
		tv_discuss_num.setText(bookDiscuss.getBook_discuss_num()+"");
		tv_up.setText(bookDiscuss.getUp()+"");
		tv_discuss.setText(bookDiscuss.getBook_describe());
		
		return view;
	}
	public static abstract class MyClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			myOnClick((Integer)v.getTag(), v);
		}
		public abstract void myOnClick(final int position,View v);
	}
}
