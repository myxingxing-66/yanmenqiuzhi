package com.example.ysulib.bean;

public class BookHist {
	private String num="";//���
	private String isbn="";//�����
	private String name="";//����
	private String author="";//����
	private String lendyear="";//�������
	private String giveyear="";//�黹����
	private String location="";//�ݲص�
	private String baseurl="http://202.206.242.99";
	private String bookurl="";
	public String getBookurl() {
		return bookurl;
	}
	public void setBookurl(String bookurl) {
		this.bookurl = baseurl+bookurl;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getLendyear() {
		return lendyear;
	}
	public void setLendyear(String lendyear) {
		this.lendyear = lendyear;
	}
	public String getGiveyear() {
		return giveyear;
	}
	public void setGiveyear(String giveyear) {
		this.giveyear = giveyear;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
