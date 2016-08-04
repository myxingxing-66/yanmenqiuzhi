package com.example.ysulib.ui;
import java.util.ArrayList;
import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.adapter.HistBookAdapter;
import com.example.ysulib.bean.BookHist;
import com.example.ysulib.util.library.Book_hist;
import com.example.ysulib.view.xlist.XListView;
import com.example.ysulib.view.xlist.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HistBookActivity extends BaseActivity implements OnClickListener {

	private int page=0;
	private XListView hist;
	private Button shang;
	private Button xia;
	private TextView xianshi;
	private LinearLayout linear1;
	public static final int SHOW_LIST = 0;
	List<BookHist> infos=new ArrayList<BookHist>();
	List<BookHist> infos1=new ArrayList<BookHist>();
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) {
			case SHOW_LIST:
				if(infos.size()==0){
					if(Book_hist.count == 0){
						ShowToast("你还未借过书");
					}else {
						ShowToast("已经是最后一页");
					}
				}else{
				xianshi.setText("第"+page+"页");
				HistBookAdapter adapter = new HistBookAdapter(HistBookActivity.this, R.layout.item_hist, infos1);
				hist.setAdapter(adapter);
				hist.setSelection(hist.getCount()-21);
				}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_book_his);	
		hist= (XListView)findViewById(R.id.hist);
		shang=(Button)findViewById(R.id.shangyiye);
		xia=(Button)findViewById(R.id.xiayiye);
		xianshi=(TextView)findViewById(R.id.xianshi);
		linear1=(LinearLayout)findViewById(R.id.linear1);
		linear1.setVisibility(View.GONE);
		initTopBarForLeft("借阅历史");
		
		show1();
		shang.setOnClickListener(this);
		xia.setOnClickListener(this);
		initXlistView();
		hist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent(HistBookActivity.this, BookDetailActivity.class);
				intent.putExtra("detailUrl", infos.get(position).getBookurl().toString());
				startActivity(intent);
			}
		});
	}

	private void show(){
		new Thread(new Runnable(){  
			  
			@Override  
			public void run() {  
				page--;
				if(page==0){
					page=1;
					ShowToast("已经是第一页");
				}
				String pag=page+"";
				Book_hist infoa=new Book_hist(pag);
				infos=infoa.show();
				
				Log.d("haha",infos.size()+"");

				Message message = new Message();
				message.what = SHOW_LIST;
				handler.sendMessage(message);
			}
		}).start();
	}
	
	private void show1(){
		new Thread(new Runnable(){  		  
			@Override  
			public void run() {  
				page++;
				String pag=page+"";
				Book_hist infoa=new Book_hist(pag);
				infos=infoa.show();
				for (int i = 0; i < infos.size(); i++) {
					infos1.add(infos.get(i));
				}
				Message message = new Message();
				message.what = SHOW_LIST;
				handler.sendMessage(message);
			}
		}).start();		
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_mylibrary){
			Intent intent = new Intent(HistBookActivity.this, LoginLibraryActivity.class);
			startActivity(intent);
		}else if(v.getId()==R.id.shangyiye){
		show();
		}else{
			show1();
		}
	}
	
	private void initXlistView (){
		hist.setPullLoadEnable(true);
		hist.setPullRefreshEnable(true);
		hist.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				show();
				hist.stopRefresh();
				
			}
			
			@Override
			public void onLoadMore() {
				show1();
				
			}
		});
	}
}
