package com.example.ysulib.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.ysulib.R;
import com.example.ysulib.adapter.AsordbookAdapter;
import com.example.ysulib.bean.BookAsord;
import com.example.ysulib.util.library.AsordBook;
import com.example.ysulib.view.xlist.XListView;
import com.example.ysulib.view.xlist.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AsordActivity extends BaseActivity implements OnClickListener {

	private int page = 0;
	private XListView asord;
	private TextView xianshi;
	private Button shang;
	private Button xia;
	private LinearLayout linear2;
	public static final int SHOW_LIST = 0;
	List<BookAsord> infos = new ArrayList<BookAsord>();
	List<BookAsord> infos1 = new ArrayList<BookAsord>();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LIST:
				if (infos.size() == 0) {
					if (AsordBook.COUNT == 0) {
						ShowToast("您没有荐购过任何书籍");
					} else {
						ShowToast("当前为最后一页");
//						page = page - 1;
					}
				} else {
					xianshi.setText("第" + page + "页");
					AsordbookAdapter adapter = new AsordbookAdapter(
							AsordActivity.this, R.layout.item_asord, infos1);
					asord.setAdapter(adapter);
					asord.setSelection(asord.getCount()-21);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_asord);
		asord = (XListView) findViewById(R.id.asord);
		shang = (Button) findViewById(R.id.shangyiye);
		xia = (Button) findViewById(R.id.xiayiye);
		xianshi = (TextView) findViewById(R.id.xianshi);
		linear2=(LinearLayout)findViewById(R.id.linear2);
		linear2.setVisibility(View.GONE);
		initXlistView();

		show1();
		shang.setOnClickListener(this);
		xia.setOnClickListener(this);

		initTopBarForLeft("荐购历史");
	}

	private void show() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				page--;
				if (page == 0) {
					page = 1;
					ShowToast("当前为第一页");
				}
				String pag = page + "";
				AsordBook infoa = new AsordBook(pag);
				infos = infoa.show();
				Message message = new Message();
				message.what = SHOW_LIST;
				handler.sendMessage(message);
			}
		}).start();
	}

	private void show1() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				page++;
				String pag = page + "";
				AsordBook infoa = new AsordBook(pag);
				infos = infoa.show();
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
		if (v.getId() == R.id.btn_mylibrary) {
			Intent intent = new Intent(AsordActivity.this,
					LoginLibraryActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.shangyiye) {
			show();
		} else {
			show1();
		}
	}
	
	private void initXlistView (){
		asord.setPullLoadEnable(true);
		asord.setPullRefreshEnable(true);
		asord.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				show();
				asord.stopRefresh();
				
			}
			
			@Override
			public void onLoadMore() {
				show1();
				
			}
		});
	}
}
