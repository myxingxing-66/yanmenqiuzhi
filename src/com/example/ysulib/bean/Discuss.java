package com.example.ysulib.bean;

import cn.bmob.v3.BmobObject;

public class Discuss extends BmobObject{

	private static final long serialVersionUID = 1L;
	private BookDiscuss bookdiscuss;
	private String discuss_maker;
	private String bookdiscuss_objectid;
	private String discuss;
	public BookDiscuss getBookdiscuss() {
		return bookdiscuss;
	}
	public void setBookdiscuss(BookDiscuss bookdiscuss) {
		this.bookdiscuss = bookdiscuss;
	}
	public String getDiscuss_maker() {
		return discuss_maker;
	}
	public void setDiscuss_maker(String discuss_maker) {
		this.discuss_maker = discuss_maker;
	}
	public String getBookdiscuss_objectid() {
		return bookdiscuss_objectid;
	}
	public void setBookdiscuss_objectid(String bookdiscuss_objectid) {
		this.bookdiscuss_objectid = bookdiscuss_objectid;
	}
	public String getDiscuss() {
		return discuss;
	}
	public void setDiscuss(String discuss) {
		this.discuss = discuss;
	}
	

}
