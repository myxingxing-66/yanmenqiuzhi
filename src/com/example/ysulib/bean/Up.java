package com.example.ysulib.bean;

import cn.bmob.v3.BmobObject;

public class Up extends BmobObject{

	private static final long serialVersionUID = 1L;
	private String bookid;
	private String userid;
	public String getBookid() {
		return bookid;
	}
	public void setBookid(String bookid) {
		this.bookid = bookid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	

}
