package com.example.zero.util;

import android.os.Bundle;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ZERO on 2017/10/29.
 */

public class HttpUtil {

    private static String CJF = "10.108.120.225";
    private static String ZMQ = "10.108.112.96";
    private static String CJY = "10.108.120.31";

    private static String server = CJF;

    public static void sendSingleOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("User_id", mBundle.getString("userId"))
                .add("Start", mBundle.getString("beginStation"))
                .add("End", mBundle.getString("endStation"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server +":8080/route/single")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendAdviceOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("User_id", mBundle.getString("userId"))
                .add("Start", mBundle.getString("beginStation"))
                .add("End", mBundle.getString("endStation"))
                .add("timeStart", mBundle.getString("time").substring(0, 5))
                .add("timeEnd", mBundle.getString("time").substring(8, 13))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/route/advice")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendStationDisplayOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("User_id", mBundle.getString("userId"))
                .add("stationName", mBundle.getString("stationName"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/shops/" + mBundle.getString("stationName") + "/near")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendStationDisplayRefreshOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("User_id", mBundle.getString("userId"))
                .add("stationName", mBundle.getString("stationName"))
                .add("searchType", mBundle.getString("searchType"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/shops/" + mBundle.getString("stationName") + "/near?searchType=" + mBundle.getString("searchType"))
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendMultiOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        int JUD = 0;
        OkHttpClient client = new OkHttpClient();
        StringBuilder starts = new StringBuilder("");
        ArrayList<String> stList = mBundle.getStringArrayList("beginStationList");
        for (int i = 0; i < stList.size(); i++) {
            if (!stList.get(i).equals("")) {
                if (JUD == 0) {
                    starts.append(stList.get(i));
                    JUD = 1;
                } else {
                    starts.append("+");
                    starts.append(stList.get(i));
                }
            }
        }
        RequestBody requestBody = new FormBody.Builder()
                .add("User_id", mBundle.getString("userId"))
                .add("Starts", starts.toString())
                .add("End", mBundle.getString("endStation"))
                .add("Scene", mBundle.getString("Scene"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/route/multi")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendDataOkHttpRequest(okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/route/station")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendGoodsOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("User_id", mBundle.getString("userId"))
                .add("shopId", mBundle.getString("shopId"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/shops/" + mBundle.getString("shopId"))
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendScheduleOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("city", "广州")
                .add("station", mBundle.getString("station"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/route/station_timelist?city=广州&station=" + mBundle.getString("station"))
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendScheduleFullOkHttpRequest(Bundle mBundle, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("city", "广州")
                .add("station", mBundle.getString("station"))
                .build();
        Request request = new Request.Builder()
                .url("http://" + server + ":8080/route/station_timelist?city=广州&station=" + mBundle.getString("station") + "&type=1")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
