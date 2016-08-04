package com.example.ysulib.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.ysulib.bean.NewBook;

public class NewbookUtil {
	List<NewBook>newlist=new ArrayList<NewBook>();
	private int page=1;
	public NewbookUtil(int page){
		this.page=page;
	}
	
	public List<NewBook> show(){
		
		try {
			String url="http://202.206.242.99/newbook/newbook_cls_book.php?back_days=30&s_doctype=ALL&loca_code=ALL&cls=ALL&clsname=%E5%85%A8%E9%83%A8%E6%96%B0%E4%B9%A6&page="+page;
			String html=GetResponse.sendGet(url,"");
			Document doc=Jsoup.parse(html);
			Elements listbook=doc.getElementsByClass("list_books");
			for(int i=0;i<listbook.size();i++){
				NewBook newbook=new NewBook();
				newbook.setName(listbook.get(i).getElementsByTag("a").text().toString());
				newbook.setDetailUrl(listbook.get(i).getElementsByTag("a").attr("href").substring(2, 35).toString());
				newbook.setIsbn(listbook.get(i).getElementsByTag("h3").get(0).childNode(1).toString());
				newbook.setInfo(listbook.get(i).getElementsByTag("p").get(0).childNode(2).toString());
				newlist.add(newbook);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return newlist;
		}
		return newlist;
	}

}
