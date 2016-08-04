package com.example.ysulib.ui;

import java.util.ArrayList;
import java.util.List;
import com.example.ysulib.R;
import com.example.ysulib.adapter.LocationAdapter;
import com.example.ysulib.bean.Lotinfo;
import com.example.ysulib.util.library.Location;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

public class BookDetailActivity extends ActivityBase {
	
	public static final int SHOW_LIST = 0;
	private ListView listView;
	private List<Lotinfo> initLoclist = new ArrayList<Lotinfo>();
	String detailUrl;
	private Handler handler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) {
			case SHOW_LIST:
				System.out.println("initLoclist size is " + initLoclist.size());
				LocationAdapter adapter = new LocationAdapter(BookDetailActivity.this, R.layout.item_location_info, initLoclist);
				listView.setAdapter(adapter);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_bookdetail);
		initTopBarForLeft("馆藏信息");
		Intent intent = getIntent();
		detailUrl = intent.getStringExtra("detailUrl");		
		listView = (ListView)findViewById(R.id.locationListView);
		search();
	}
	
	private void search(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Location local=new Location(detailUrl);
					initLoclist = local.location();
					if(initLoclist.get(0)==null){
						System.out.println("图书馆没有此书复本");
					}else {
						System.out.println("initLoclist size is " + initLoclist.size());
						Message message = new Message();
						message.what = SHOW_LIST;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
