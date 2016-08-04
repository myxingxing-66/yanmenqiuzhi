package com.example.ysulib.bean;

import cn.bmob.v3.BmobObject;

public class BookDiscuss extends BmobObject{
	
	private static final long serialVersionUID = 2L;
	
	private String book_title;//书名
	private String book_author;//作者
	private String book_user;//发布用户
	private String book_describe;//书描述
	private String book_user_ObjectId;//发布用户的ObjectID
	private int book_discuss_num;//评论数量
	private int up;//赞
	
	public int getBook_discuss_num() {
		return book_discuss_num;
	}

	public void setBook_discuss_num(int book_discuss_num) {
		this.book_discuss_num = book_discuss_num;
	}

	public String getBook_user_ObjectId() {
		return book_user_ObjectId;
	}

	public void setBook_user_ObjectId(String book_user_ObjectId) {
		this.book_user_ObjectId = book_user_ObjectId;
	}

	public String getBook_title() {
		return book_title;
	}
	
	public void setBook_title(String book_title) {
		this.book_title = book_title;
	}
	
	public String getBook_author() {
		return book_author;
	}
	
	public void setBook_author(String book_author) {
		this.book_author = book_author;
	}
	
	public String getBook_user() {
		return book_user;
	}
	
	public void setBook_user(String book_user) {
		this.book_user = book_user;
	}
	
	public String getBook_describe() {
		return book_describe;
	}
	
	public void setBook_describe(String book_describe) {
		this.book_describe = book_describe;
	}
	
	public int getUp() {
		return up;
	}
	
	public void setUp(int up) {
		this.up = up;
	}
}
