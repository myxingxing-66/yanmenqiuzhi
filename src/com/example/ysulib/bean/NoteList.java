package com.example.ysulib.bean;

public class NoteList {
	private String number="";//���
	private String title="";//����
	private String author="";//����
	private String publish="";//������
	private String isbn="";//�����
	private String sav_num="";//�ݲ���Ŀ
	private String lend_num="";//�ɽ���Ŀ
	private String bookhref="";//��ĵ�ַ
	private String baseUrl="http://202.206.242.99";
	public String getBookhref(){
		return bookhref;
	}
	public String getNumber(){
		return number;
	}
	public String getTitle(){
		return title;
	}
	public String getAuthor(){
		return author;
	}
	public String getPublish(){
		return publish;
	}
	public String getIsbn(){
		return isbn;
	}
	public String getSav_num(){
		return sav_num;
	}
	public String getLend_num(){
		return lend_num;
	}
	public void setNumber(String number){
		this.number=number;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public void setAuthor(String author){
		this.author=author;
	}
	public void setPublish(String publish){
		this.publish=publish;
	}
	public void setIsbn(String isbn){
		this.isbn=isbn;
	}
	public void setSav_num(String save_num){
		this.sav_num=save_num;
	}
	public void setLend_num(String lend_num){
		this.lend_num=lend_num;
	}
	public void setBookhref(String bookhref){
		this.bookhref=baseUrl+bookhref;
	}
}
