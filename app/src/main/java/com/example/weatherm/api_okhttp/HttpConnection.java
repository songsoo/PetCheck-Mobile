package com.example.weatherm.api_okhttp;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpConnection {
    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }

    // 1. 현재 서울 날씨
    // api.openweathermap.org/data/2.5/weather?q=Seoul&APPID=10465524bfe7eaa71c4010cb81b84a11

    // 2. 서울 날씨 : 3시간 간격
    // api.openweathermap.org/data/2.5/forecast?q=Seoul&APPID=10465524bfe7eaa71c4010cb81b84a11

    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(Callback callback) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&APPID=10465524bfe7eaa71c4010cb81b84a11";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
