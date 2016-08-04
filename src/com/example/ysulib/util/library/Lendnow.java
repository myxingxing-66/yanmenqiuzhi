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

import com.example.ysulib.bean.NowLend;
import com.example.ysulib.ui.BaseActivityInterFace;

import android.util.Log;

public class Lendnow implements BaseActivityInterFace {
	List<NowLend> info = new ArrayList<NowLend>();

	public List<NowLend> show() {
		try {
			HttpGet httpGet1 = new HttpGet(
					"http://202.206.242.99/reader/book_lst.php");
			HttpResponse Response1;
			Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
			HttpEntity entity1 = Response1.getEntity();
			String response1 = EntityUtils.toString(entity1, "utf-8");
			Document doc = Jsoup.parse(response1);
			Log.d("begin", doc.html());
			Element table = doc.getElementsByClass("table_line").get(0);
			Elements tr = table.getElementsByTag("tr");

			for (int i = 1; i < tr.size(); i++) {
				Elements td = tr.get(i).getElementsByTag("td");
				NowLend book = new NowLend();
				book.setIsbn(td.get(0).text().toString());
				book.setTit_aut(td.get(1).text().toString());
				book.setLendday(td.get(2).text().toString());
				book.setGiveday(td.get(3).text().toString());
				book.setLennum(td.get(4).text().toString());
				book.setLocation(td.get(5).text().toString());
				book.setOther(td.get(6).text().toString());
				info.add(book);

			}
		} catch (Exception e) {
			e.printStackTrace();
			return info;
		}
		return info;
	}
}
