package com.example.ysulib.util.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.ysulib.bean.BookHist;
import com.example.ysulib.ui.BaseActivityInterFace;

public class Book_hist implements BaseActivityInterFace{
	public static int count=0;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	private String page="1";
	public Book_hist(String page){
		this.page=page;
	}
	List<BookHist> info=new ArrayList<BookHist>();
	public List<BookHist> show(){
		try {
		HttpGet httpGet1 = new HttpGet("http://202.206.242.99/reader/book_hist.php?page="+page);			
		HttpResponse Response1;

			Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
			HttpEntity entity1 = Response1.getEntity();
			String response1 = EntityUtils.toString(entity1, "utf-8");
			Document doc=Jsoup.parse(response1);
			Element table=doc.getElementsByClass("table_line").get(0);
			Elements tr=table.getElementsByTag("tr");
		
			for(int i=1;i<tr.size();i++){
				Elements td=tr.get(i).getElementsByTag("td");
				BookHist book=new BookHist();
				book.setNum(td.get(0).text().toString());
				book.setIsbn(td.get(1).text().toString());
				book.setName(td.get(2).text().toString());
				book.setBookurl(td.get(2).getElementsByTag("a").attr("href").substring(2, 35).toString());
				book.setAuthor(td.get(3).text().toString());
				book.setLendyear(td.get(4).text().toString());
				book.setGiveyear(td.get(5).text().toString());
				book.setLocation(td.get(6).text().toString());
				info.add(book);	
				count++;
			}
		} catch (Exception e) {	
			e.printStackTrace();
			return info;
		}
		return info;
	}
}
