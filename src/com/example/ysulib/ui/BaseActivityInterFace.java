package com.example.ysulib.ui;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public interface BaseActivityInterFace {
	public static HttpClient httpClient = new DefaultHttpClient();
}
