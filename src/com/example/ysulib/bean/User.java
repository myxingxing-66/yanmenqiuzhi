package com.example.ysulib.bean;

import cn.bmob.im.bean.BmobChatUser;

// ����BmobChatUser����������������Ҫ���ӵ����Կ��ڴ����
public class User extends BmobChatUser {

	private static final long serialVersionUID = 1L;

	// ��ʾ����ƴ��������ĸ
	private String sortLetters;

	// �Ա�-true-��
	private boolean sex;

	// ����������ע��ʱĬ��Ϊ0
	private int lendnum = 0;
	// �û�����
	private String usernum = "";
	// �û�����
	private String piaming = "";
	// ��������
	private String maxlend="";
	// �͵�һ���Ĳ��
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
