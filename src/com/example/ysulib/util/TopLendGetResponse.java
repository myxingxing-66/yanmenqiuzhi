package com.example.ysulib.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/*
 * @Author 崔石垒
 * @Date 2015.09.21
 * */

public class TopLendGetResponse {
	// LiXing 将HttpURLConnection方法改为HttpClient 2015.09.22
	public static String sendGet(String url, String param) {
		String response = "";
		try {
			String urlNameString = url  + param;
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urlNameString);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			response = EntityUtils.toString(entity, "utf-8");
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return response;
	}
}
