package com.example.ysulib.ui;

import java.util.ArrayList;
import java.util.List;
import com.example.ysulib.R;
import com.example.ysulib.adapter.PregBookAdapter;
import com.example.ysulib.bean.BookPreg;
import com.example.ysulib.util.library.Book_preg;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class PregBookActivity extends BaseActivity implements OnClickListener {

	private ListView info;
	public static final int SHOW_LIST = 0;
	List<BookPreg> infos=new ArrayList<BookPreg>();
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) {
			case SHOW_LIST:
				if(infos.size()==0){
					ShowToast("您没有预约任何信息");
				}else{
				PregBookAdapter adapter = new PregBookAdapter(PregBookActivity.this, R.layout.item_preg, infos);
				info.setAdapter(adapter);
				}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_preg);	
		info = (ListView)findViewById(R.id.info);
		initTopBarForLeft("预约信息");
		show();
	}

	private void show(){
		new Thread(new Runnable(){  			  
			@Override  
			public void run() {  			
				Book_preg infoa=new Book_preg();
				infos=infoa.show();
				Message message = new Message();
				message.what = SHOW_LIST;
				handler.sendMessage(message);
			}
		}).start();	
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_mylibrary){
			Intent intent = new Intent(PregBookActivity.this, LoginLibraryActivity.class);
			startActivity(intent);
		}
	}
}
