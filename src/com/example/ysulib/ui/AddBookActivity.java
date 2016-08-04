package com.example.ysulib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

import com.example.ysulib.R;
import com.example.ysulib.bean.BookDiscuss;
import com.example.ysulib.view.HeaderLayout.onRightImageButtonClickListener;

public class AddBookActivity extends ActivityBase {
	
	private EditText edit_title,edit_author,edit_describe;
	
	private String add_title,add_author,add_describe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_book);
		initView();
	}
	
	private void initView(){
		edit_title = (EditText)findViewById(R.id.edit_title);
		edit_author = (EditText)findViewById(R.id.edit_author);
		edit_describe = (EditText)findViewById(R.id.edit_describe);
		

		if (getIntent() != null) {
			edit_author.setText(getIntent().getStringExtra("book_auther"));
			edit_title.setText(getIntent().getStringExtra("book_name"));
		}
		
		initTopBarForBoth("发布", R.drawable.base_action_bar_true_bg_selector, new onRightImageButtonClickListener() {			
			@Override
			public void onClick() {
				checkText();
			}
		});
	}
	
	private void addBook(){
		
		BookDiscuss bookDiscuss = new BookDiscuss();
		bookDiscuss.setUp(0);
		bookDiscuss.setBook_title(add_title);
		bookDiscuss.setBook_author(add_author);
		bookDiscuss.setBook_describe(add_describe);
		bookDiscuss.setBook_user_ObjectId(BmobUser.getCurrentUser(this).getObjectId().toString());
		bookDiscuss.setBook_user(BmobUser.getCurrentUser(this).getUsername());
		bookDiscuss.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				ShowToast("发布成功");
				setResult(RESULT_OK, new Intent());
				finish();
			}
		
			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("发布失败:" + arg0);
			}
		});
	}
	
	private void findRepeat(){
		BmobQuery<BookDiscuss> query = new BmobQuery<BookDiscuss>();
		String sql = "select * from BookDiscuss Where book_title = ?";
		query.doSQLQuery(this, sql, new SQLQueryListener<BookDiscuss>() {
			
			@Override
			public void done(BmobQueryResult<BookDiscuss> arg0, BmobException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					if(arg0.getResults().size()>0&&arg0.getResults()!=null){
						ShowToast("该书已有人发布");
					}else{
						addBook();
					}
				}
				
			}
		},add_title);
	}
	
	private void checkText(){
		add_title = edit_title.getText().toString() ;
		add_author = edit_author.getText().toString() ;
		add_describe = edit_describe.getText().toString();
		
		if(TextUtils.isEmpty(add_title)){
			ShowToast("请填写书名");
			return;
		}
		if(TextUtils.isEmpty(add_author)){
			ShowToast("请填写作者");
			return;
		}
		if(TextUtils.isEmpty(add_describe)){
			ShowToast("请填写书籍简介");
			return;
		}
		findRepeat();
	}
}
