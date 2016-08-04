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

import com.example.ysulib.bean.BookAsord;
import com.example.ysulib.ui.BaseActivityInterFace;

public class AsordBook implements BaseActivityInterFace{
	public static int COUNT=0;
	private String page="1";
	public AsordBook(String page){
		this.page=page;
	}
	List<BookAsord> info=new ArrayList<BookAsord>();
	public List<BookAsord> show(){
		try {
		HttpGet httpGet1 = new HttpGet("http://202.206.242.99/reader/asord_lst.php?page="+page);			
		HttpResponse Response1;
		
			Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
			HttpEntity entity1 = Response1.getEntity();
			String response1 = EntityUtils.toString(entity1, "utf-8");
			Document doc=Jsoup.parse(response1);
			Element table=doc.getElementsByClass("table_line").get(0);
			Elements tr=table.getElementsByTag("tr");
		
			for(int i=1;i<tr.size();i++){
				Elements td=tr.get(i).getElementsByTag("td");
				BookAsord book=new BookAsord();
				book.setName(td.get(0).text().toString());
				book.setAuthor(td.get(1).text().toString());
				book.setPublish(td.get(2).text().toString());
				book.setAsordday(td.get(3).text().toString());
				book.setNow(td.get(4).text().toString());
				book.setBeizhu(td.get(5).text().toString());
				info.add(book);
				COUNT++;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			return info;
			
		}
		return info;
	}
}
