package com.example.ysulib.ui;

import java.util.ArrayList;
import java.util.List;
import com.example.ysulib.R;
import com.example.ysulib.adapter.NowlendAdapter;
import com.example.ysulib.bean.NowLend;
import com.example.ysulib.util.library.Lendnow;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

@SuppressLint("HandlerLeak")
public class NowLendActivity extends BaseActivity implements OnClickListener {

	private ListView info;
	public static final int SHOW_LIST = 0;
	List<NowLend> infos = new ArrayList<NowLend>();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LIST:
				if (infos.size() == 0) {
					ShowToast("你当前没有借任何书籍");
				} else {
					NowlendAdapter adapter = new NowlendAdapter(
							NowLendActivity.this, R.layout.item_lend_now, infos);
					info.setAdapter(adapter);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nowlend);
		initTopBarForLeft("当前借阅");
		info = (ListView) findViewById(R.id.nowlend);
		show();
	}

	private void show() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Lendnow infoa = new Lendnow();
				infos = infoa.show();
				Message message = new Message();
				message.what = SHOW_LIST;
				handler.sendMessage(message);
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_mylibrary) {
			Intent intent = new Intent(NowLendActivity.this,
					LoginLibraryActivity.class);
			startActivity(intent);
		}
	}
}
