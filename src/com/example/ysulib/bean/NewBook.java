package com.example.ysulib.bean;

public class NewBook {
	private String name="";//����
	private String isbn="";//������
	private String info="";//��Ŀ��Ϣ
	private String detailUrl="";//��ַ
	
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = baseurl + detailUrl;
	}
	public String getDetailUrl() {
		return detailUrl;
	}
	private String baseurl="http://202.206.242.99";
	
	public String getName(){
		return name;
	}
	public String getIsbn(){
		return isbn;
	}
	public String getInfo(){
		return info;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setIsbn(String isbn){
		this.isbn=isbn;
	}
	public void setInfo(String info){
		this.info=info;
	}
}
