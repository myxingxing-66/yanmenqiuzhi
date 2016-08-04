package com.example.ysulib.util.library;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.ysulib.bean.Lotinfo;

public class Location {
	private String  url;
	List<Lotinfo> infolists=new ArrayList<Lotinfo>();
	public Location(String url){
		this.url=url;
	}
	public List<Lotinfo> location(){
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
		Elements infolist=doc.getElementsByClass("whitetext");
		if(infolist.size()==0){
			Lotinfo location=new Lotinfo();
			location=null;
			infolists.add(location);
			return infolists;
		}
		else{
		for(int i =0;i<infolist.size();i++){
			Lotinfo location=new Lotinfo();
			location.setIsbn(infolist.get(i).getElementsByTag("td").get(0).text());
			location.setNumber(infolist.get(i).getElementsByTag("td").get(1).text());
			location.setYear(infolist.get(i).getElementsByTag("td").get(2).text());
			location.setLocal(infolist.get(i).getElementsByTag("td").get(3).text());
			location.setNow(infolist.get(i).getElementsByTag("td").get(4).text());
			infolists.add(location);
			}
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infolists;
	}
}
