package com.example.ysulib.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.ysulib.R;
import com.example.ysulib.adapter.DiscussAdapter;
import com.example.ysulib.bean.BookDiscuss;
import com.example.ysulib.bean.Discuss;
import com.example.ysulib.bean.User;

public class DiscussActivity extends ActivityBase implements OnClickListener,
		OnItemClickListener {
	private TextView tv_title, tv_author, tv_describe, tv_user;
	private Button btn_book_discuss_send;
	private EditText edit_user_discuss;
	private String objectId, author, describe, title;
	private int up;
	private List<Discuss> discussList = new ArrayList<Discuss>();
	private DiscussAdapter discussadapter;
	private ListView discussView;
	private User user = new User();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);

		initView();
		queryDiscuss();

	}

	private void initView() {

		initTopBarForLeft("书籍详情");
		
		objectId = getIntent().getStringExtra("objectId");
		Log.d("eeeeeeeeeeee", objectId);
		author = getIntent().getStringExtra("author");
		describe = getIntent().getStringExtra("describe");
		title = getIntent().getStringExtra("title");
		up = getIntent().getIntExtra("up", 0);
		tv_title = (TextView) findViewById(R.id.tv_book_title);
		tv_author = (TextView) findViewById(R.id.tv_book_author);
		tv_describe = (TextView) findViewById(R.id.tv_book_describe);
		edit_user_discuss = (EditText) findViewById(R.id.edit_user_discuss);
		btn_book_discuss_send = (Button) findViewById(R.id.btn_book_discuss_send);
		tv_user = (TextView) findViewById(R.id.tv_book_user);
		tv_user.setOnClickListener(this);
		btn_book_discuss_send.setOnClickListener(this);
		discussView = (ListView) findViewById(R.id.discuss_list);
		discussView.setOnItemClickListener(this);
		Log.d("tt", author + describe + title + "hh");

		tv_title.setText(title);
		tv_author.setText(author);
		tv_describe.setText(describe);
		tv_user.setText(getIntent().getStringExtra("user"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_book_discuss_send:

			String discuss_maker;
			String bookdiscuss_objectid;
			String discuss;

			discuss = edit_user_discuss.getText().toString();
			bookdiscuss_objectid = objectId;
			discuss_maker = user.getCurrentUser(this, User.class).getUsername();
			edit_user_discuss.setText("");
			final Discuss book_discuss = new Discuss();
			book_discuss.setBookdiscuss_objectid(bookdiscuss_objectid);
			book_discuss.setDiscuss(discuss);
			book_discuss.setDiscuss_maker(discuss_maker);
			book_discuss.save(this, new SaveListener() {

				@Override
				public void onSuccess() {
					if (discussList.size() == 0) {
						discussadapter = new DiscussAdapter(
								DiscussActivity.this, R.layout.item_discuss,
								discussList);
					}
					discussList.add(book_discuss);
					discussView.setAdapter(discussadapter);
					discussadapter.notifyDataSetChanged();
					ShowToast("评论成功");
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					ShowToast("onFailure");
				}
			});
			Update();
			break;

		case R.id.tv_book_user:
			Intent intent = new Intent(this, SetMyInfoActivity.class);
			if (tv_user.getText().equals(
					user.getCurrentUser(this, User.class).getUsername())) {
				intent.putExtra("from", "me");
			} else {
				intent.putExtra("from", "add");
			}
			intent.putExtra("username", tv_user.getText());
			startActivity(intent);
			break;
		}

	}

	private void queryDiscuss() {
		BmobQuery<Discuss> query = new BmobQuery<Discuss>();
		String sql = "select * from Discuss Where bookdiscuss_objectid = ?";

		query.doSQLQuery(this, sql, new SQLQueryListener<Discuss>() {

			@Override
			public void done(BmobQueryResult<Discuss> arg0, BmobException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					discussList.clear();
					discussList = (List<Discuss>) arg0.getResults();
					if (discussList != null && discussList.size() > 0) {
						discussadapter = new DiscussAdapter(
								DiscussActivity.this, R.layout.item_discuss,
								discussList);
						discussView.setAdapter(discussadapter);
						discussadapter.notifyDataSetChanged();
					}
				}
			}
		}, objectId);

	}

	private void Update() {
		BookDiscuss newDiscuss_num = new BookDiscuss();
		newDiscuss_num.setBook_discuss_num(discussList.size() + 1);
		newDiscuss_num.setUp(up);
		newDiscuss_num.update(this, objectId, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, SetMyInfoActivity.class);
		if (discussList.get(position).getDiscuss_maker()
				.equals(user.getCurrentUser(this, User.class).getUsername())) {
			intent.putExtra("from", "me");
		} else {
			intent.putExtra("from", "add");
		}

		intent.putExtra("username", discussList.get(position)
				.getDiscuss_maker());
		startActivity(intent);

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		super.onBackPressed();
	}

}
