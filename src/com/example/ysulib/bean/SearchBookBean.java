package com.example.ysulib.bean;

/*
 * @Author ��ʯ��
 * @Date 2015.09.21
 * @ ÿ�������Ϣ
 * */

public class SearchBookBean {
	private String name; //��Ŀ
	private String auther; //����
	private String publish; //������
	private String save_num; //�ݲ�
	private String now_num; //�ɽ�����
	private String type; //�鿯����
	private String isbn; //�����
	private String base_url="http://202.206.242.99/opac/";
	private String book_url;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuther() {
		return auther;
	}
	public void setAuther(String auther) {
		this.auther = auther;
	}
	public String getPublish() {
		return publish;
	}
	public void setPublish(String publish) {
		this.publish = publish;
	}
	public String getSave_num() {
		return save_num;
	}
	public void setSave_num(String save_num) {
		this.save_num = save_num;
	}
	public String getNow_num() {
		return now_num;
	}
	public void setNow_num(String now_num) {
		this.now_num = now_num;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBase_url() {
		return base_url;
	}
	public void setBase_url(String base_url) {
		this.base_url = base_url;
		System.out.println("base_url"+base_url);
	}
	public String getBook_url() {
		return book_url;
	}
	public void setBook_url(String book_url) {
		//System.out.println("��ת����"+ base_url + book_url);
		this.book_url = base_url + book_url;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}
