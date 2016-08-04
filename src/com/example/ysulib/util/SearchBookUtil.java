package com.example.ysulib.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.ysulib.bean.SearchBookBean;

public class SearchBookUtil {
	private String url = "http://202.206.242.99/opac/openlink.php";
	private String strSearchType; // 搜寻种类
	private String match_flag; // 是否匹配，全部匹配
	private String historyCount; // 默认为1
	private String strText; // 检索关键字
	private String doctype; // 图书种类
	private String displaypg; // 默认20页
	private String showmode = "list";
	private String sort = "CATA_DATE";
	private String orderby = "desc";
	private String location = "ALL";
	private int page=1;
	
	public SearchBookUtil(int page){
		this.page=page;
	}

	public List<SearchBookBean> Search() {
		String params = "strSearchType=" + strSearchType + "&match_flag=forward&historyCount=1&strText="
				+ strText
				+ "&doctype=ALL&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&location=ALL&page="+page;
		String html = GetResponse.sendGet(url, params);
		Document doc = Jsoup.parse(html);
		Elements eles = doc.getElementsByClass("book_list_info");
		List<SearchBookBean> searchBookBeans = new ArrayList<SearchBookBean>();
		
		for (Element e : eles) {
			System.out.println(e.getElementsByTag("p").get(0).childNode(2).toString());

			SearchBookBean searchBookBean = new SearchBookBean();

			searchBookBean.setType(e.getElementsByTag("span").get(0).text());
			searchBookBean.setName(e.getElementsByTag("a").get(0).text());
			searchBookBean.setBook_url(e.getElementsByTag("a").attr("href"));
			searchBookBean.setIsbn(e.getElementsByTag("h3").get(0).childNode(2).toString());
			searchBookBean.setType(e.getElementsByTag("span").get(0).text());
			String s=e.getElementsByTag("p").get(0).childNode(4).toString();
			searchBookBean.setPublish(s.substring(0,s.length()-11)+" "+s.substring(s.length()-5, s.length()));
			searchBookBean.setSave_num(e.getElementsByTag("span").get(1).childNode(0).toString());
			searchBookBean.setNow_num(e.getElementsByTag("span").get(1).childNode(2).toString());
			searchBookBean.setAuther(e.getElementsByTag("p").get(0).childNode(2).toString());
			searchBookBeans.add(searchBookBean);
		}
		return searchBookBeans;
	}
	
	public String getStrSearchType() {
		return strSearchType;
	}

	public void setStrSearchType(String strSearchType) {
		this.strSearchType = strSearchType;
	}

	public String getMatch_flag() {
		return match_flag;
	}

	public void setMatch_flag(String match_flag) {
		this.match_flag = match_flag;
	}

	public String getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(String historyCount) {
		this.historyCount = historyCount;
	}

	public String getStrText() {
		return strText;
	}

	public void setStrText(String strText) {
		this.strText = strText;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDisplaypg() {
		return displaypg;
	}

	public void setDisplaypg(String displaypg) {
		this.displaypg = displaypg;
	}

	public String getShowmode() {
		return showmode;
	}

	public void setShowmode(String showmode) {
		this.showmode = showmode;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
