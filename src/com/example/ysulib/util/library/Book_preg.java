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

import com.example.ysulib.bean.BookPreg;
import com.example.ysulib.ui.BaseActivityInterFace;

public class Book_preg implements BaseActivityInterFace{
	
	List<BookPreg> info=new ArrayList<BookPreg>();
	public List<BookPreg> show(){
		HttpGet httpGet1 = new HttpGet("http://202.206.242.99/reader/preg.php");			
		HttpResponse Response1;
		try {
		Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
		HttpEntity entity1 = Response1.getEntity();
		String response1 = EntityUtils.toString(entity1, "utf-8");
		Document doc=Jsoup.parse(response1);
		Element table=doc.getElementsByClass("table_line").get(0);
		Elements tr=table.getElementsByTag("tr");
		
		for(int i=1;i<tr.size();i++){
			Elements td=tr.get(i).getElementsByTag("td");
			BookPreg book=new BookPreg();
			book.setIsbn(td.get(0).text().toString());
			book.setTit_aut(td.get(1).text().toString());
			book.setLocation(td.get(2).text().toString());
			book.setPregday(td.get(3).text().toString());
			book.setEndday(td.get(4).text().toString());
			book.setHavebook(td.get(5).text().toString());
			book.setNow(td.get(6).text().toString());
			info.add(book);
			
		}
		
		
		} catch (Exception e) {				
			e.printStackTrace();
			return info;
		}
		return info;
	}
}
