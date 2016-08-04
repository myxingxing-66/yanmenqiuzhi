package com.example.ysulib.bean;

import cn.bmob.im.bean.BmobChatUser;

// 重载BmobChatUser对象：若还有其他需要增加的属性可在此添加
public class User extends BmobChatUser {

	private static final long serialVersionUID = 1L;

	// 显示数据拼音的首字母
	private String sortLetters;

	// 性别-true-男
	private boolean sex;

	// 借书数量，注册时默认为0
	private int lendnum = 0;
	// 用户数量
	private String usernum = "";
	// 用户排名
	private String piaming = "";
	// 最大借阅量
	private String maxlend="";
	// 和第一名的差距
	private String cahju="";

	public String getUsernum() {
		return usernum;
	}

	public void setUsernum(String usernum) {
		this.usernum = usernum;
	}

	public String getPiaming() {
		return piaming;
	}

	public void setPiaming(String piaming) {
		this.piaming = piaming;
	}

	public String getMaxlend() {
		return maxlend;
	}

	public void setMaxlend(String maxlend) {
		this.maxlend = maxlend;
	}

	public String getCahju() {
		return cahju;
	}

	public void setCahju(String cahju) {
		this.cahju = cahju;
	}

	public int getLendnum() {
		return lendnum;
	}

	public void setLendnum(int lendnum) {
		this.lendnum = lendnum;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
