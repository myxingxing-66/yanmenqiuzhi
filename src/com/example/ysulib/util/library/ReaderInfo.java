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



import com.example.ysulib.ui.BaseActivityInterFace;

import android.util.Log;

public class ReaderInfo implements BaseActivityInterFace {
	List<String> info=new ArrayList<String>();
	
	public List<String> readInfo(){
		HttpGet httpGet1 = new HttpGet("http://202.206.242.99/reader/redr_info_rule.php");			
		HttpResponse Response1;
		try {
		Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
		HttpEntity entity1 = Response1.getEntity();
		String response1 = EntityUtils.toString(entity1, "utf-8");
		Document doc=Jsoup.parse(response1);
		Log.d("begin", doc.html());
		Elements mylib_msg=doc.getElementsByClass("mylib_msg");
		Elements a=mylib_msg.get(0).getElementsByTag("a");
		for(int i=0;i<4;i++){
			String s=a.get(i).text().toString();
			info.add(s);
		}
		Element mylib_info=doc.getElementById("mylib_info");
		Elements tr1=mylib_info.getElementsByTag("tr").get(0).getElementsByTag("td");
		Elements tr=mylib_info.getElementsByTag("tr");
		for(int i=1;i<tr1.size();i++){
			String ss=tr1.get(i).text().toString();
			info.add(ss);
		}
		for(int i=1;i<tr.size();i++){
			Elements td=tr.get(i).getElementsByTag("td");
			for(int j=0;j<td.size();j++){
				String sss=td.get(j).text().toString();
				info.add(sss);
			}
		}
		} catch (Exception e) {
			String book=new String();
			book=null;
			info.add(book);		
			e.printStackTrace();
			return info;
		}
		return info;
	}
}
