package com.sorry.api.util;

import android.content.Context;

import java.util.Map;

import okhttp3.Cache;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpEngine {
    private static final String TAG = "HttpEngine";
    private static final String url = "http://s0ch.cn/Stalker/api.php";
    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static HttpEngine instance = null;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private static Cache cache;

    private HttpEngine(Context context){
        cache = new Cache(context.getCacheDir(), cacheSize);
    }

    public static HttpEngine getInstance(Context context){
        if(instance == null){
            instance = new HttpEngine(context);
        }
        return instance;
    }


    public void get(Map<String, String> paramsMap, Callback callback){

        Request request = new Request.Builder().url(joinGetParam(url, paramsMap)).build();
        okHttpClient.newCall(request).enqueue(callback);

    }

    public void getUrl(String url, Callback callback){
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void post(Map<String, String> paramsMap, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .post(joinPostParam(paramsMap))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public String joinGetParam(String url, Map<String, String> paramsMap){
        url = url + "?";
        for (String key : paramsMap.keySet()) {
            url = url + key + "=" + paramsMap.get(key) + "&";
        }
        return url;
    }

    public RequestBody joinPostParam(Map<String, String> paramsMap){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, paramsMap.get(key));
        }
        return builder.build();
    }
}
