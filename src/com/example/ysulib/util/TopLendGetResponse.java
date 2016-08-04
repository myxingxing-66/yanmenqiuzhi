package com.example.ysulib.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/*
 * @Author ��ʯ��
 * @Date 2015.09.21
 * */

public class TopLendGetResponse {
	// LiXing ��HttpURLConnection������ΪHttpClient 2015.09.22
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
			System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		return response;
	}
}
