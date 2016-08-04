package com.example.ysulib.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.adapter.BookInfoAdapter;
import com.example.ysulib.util.library.ReaderInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ReaderInfoActivity extends BaseActivity implements OnClickListener{

	private ListView info;
	public static final int SHOW_LIST = 0;
	List<String> infos=new ArrayList<String>();
			
	private Handler handler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) {
			case SHOW_LIST:
				if(infos.get(0)==null){
					Toast.makeText(ReaderInfoActivity.this,"请先登录!!!", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(ReaderInfoActivity.this, LoginLibraryActivity.class);
					startActivity(intent);
				}else{
				BookInfoAdapter adapter = new BookInfoAdapter(ReaderInfoActivity.this, R.layout.item_read_info, infos);
				info.setAdapter(adapter);
				}
				break;
			}
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_info);
		initTopBarForLeft("证件信息");
		info = (ListView)findViewById(R.id.info);				
		show();	
	}

	private void show(){
		new Thread(new Runnable(){  		  
			@Override  
			public void run() {  		
				ReaderInfo infoa=new ReaderInfo();
				infos=infoa.readInfo();
				Message message = new Message();
				message.what = SHOW_LIST;
				handler.sendMessage(message);
			}
		}).start();	
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_mylibrary){
			Intent intent = new Intent(ReaderInfoActivity.this, LoginLibraryActivity.class);
			startActivity(intent);
			}
	}
}
