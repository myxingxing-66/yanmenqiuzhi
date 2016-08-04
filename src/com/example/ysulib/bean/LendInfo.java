package com.example.ysulib.bean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.example.ysulib.ui.BaseActivityInterFace;

public class LendInfo implements BaseActivityInterFace {
	String paiming = "";
	Document doc;

	public String show() {
		try {
			HttpGet httpGet1 = new HttpGet(
					"http://202.206.242.99/reader/redr_info.php");
			HttpResponse Response1;

			Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
			HttpEntity entity1 = Response1.getEntity();
			String response1 = EntityUtils.toString(entity1, "utf-8");
			doc = Jsoup.parse(response1);
			Element table = doc.getElementsByClass("main-content").get(0)
					.getElementsByClass("row-fluid").get(2)
					.getElementsByClass("span3").get(0);
			paiming = table.getElementsByTag("span").get(0).text().toString();

		} catch (Exception e) {
			e.printStackTrace();
			return paiming;
		}
		return paiming;
	}
}
