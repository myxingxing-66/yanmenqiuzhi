package com.example.ysulib.bean;

public class SearchBookItem {
	
	private String titletextView;
	private String authortextView;
	private String ISBNtextView;
	private String borrowtextView;
	private String detailUrl;
	

	public SearchBookItem(String titletextView,String authortextView,String ISBNtextView,String borrowtextView,String detailUrl){
		this.titletextView = titletextView;
		this.authortextView = authortextView;
		this.ISBNtextView = ISBNtextView;
		this.borrowtextView = borrowtextView;
		this.detailUrl = detailUrl;
	}
	
	public String getDetailUrl() {
		return detailUrl;
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	public String getTitletextView() {
		return titletextView;
	}
	public void setTitletextView(String titletextView) {
		this.titletextView = titletextView;
	}
	public String getAuthortextView() {
		return authortextView;
	}
	public void setAuthortextView(String authortextView) {
		this.authortextView = authortextView;
	}
	public String getISBNtextView() {
		return ISBNtextView;
	}
	public void setISBNtextView(String iSBNtextView) {
		ISBNtextView = iSBNtextView;
	}
	public String getBorrowtextView() {
		return borrowtextView;
	}
	public void setBorrowtextView(String borrowtextView) {
		this.borrowtextView = borrowtextView;
	}

	
}
