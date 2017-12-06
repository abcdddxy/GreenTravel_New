package com.example.zero.util;

/**
 * Created by kazu_0122 on 2017/10/30.
 */

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.SDKInitializer;
import com.example.zero.bean.RouteSearchBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainApplication extends Application {
    private boolean isOnline;
    private String user_id;
    private String phone;
    private String username;
    private String token;
    private String avator;
    private boolean send_msg;
    private boolean apk;
    private boolean pic;
    private String address_id, address_name, address_phone;

    private List<RouteSearchBean> stationList = new ArrayList<>();
    private List<String> busyStationList = new ArrayList<>();

    private String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean defaultLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        token = sharedPreferences.getString("token", "");
        send_msg = sharedPreferences.getBoolean("send_msg", true);
        apk = sharedPreferences.getBoolean("apk_download", false);
        pic = sharedPreferences.getBoolean("pic_download", false);
        if ((!phone.equals("")) && (!token.equals(""))) {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "1");
            params.put("phone", phone);
            params.put("token", token);
            RequestManager.getInstance(getBaseContext()).requestAsyn("users/user_login", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                @Override
                public void onReqSuccess(String result) {
                    Log.d(TAG, result);
                    JSONObject jsonObj = JSON.parseObject(result);
                    user_id = jsonObj.getString("user_id");
                    token = jsonObj.getString("token");
                    username = jsonObj.getString("username");
                    avator = jsonObj.getString("avator_url");
                    SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone", phone);
                    editor.putString("user_id", user_id);
                    editor.putString("token", token);
                    editor.commit();
                    isOnline = true;
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.d(TAG, errorMsg);
                    isOnline = false;
                }
            });
        }
        Log.d(TAG, "sha");
        return isOnline;
    }

    public void logout() {
        isOnline = false;
    }

    public void login(String user_id, String phone, String username, String token, String avator) {
        isOnline = true;
        this.user_id = user_id;
        this.phone = phone;
        this.username = username;
        this.token = token;
        this.avator = avator;
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.putString("user_id", user_id);
        editor.putString("token", token);//TODO 是否还有其他需要的信息
        editor.commit();
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getToken() {
        return token;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getAvator() {
        return avator;
    }

    public void setMsgBtn(boolean msg) {
        this.send_msg = msg;
    }

    public boolean getMsgBtn() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        send_msg = sharedPreferences.getBoolean("send_msg", true);
        return send_msg;
    }

    public void setApkBtn(boolean apk) {
        this.apk = apk;
    }

    public boolean getApkBtn() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        apk = sharedPreferences.getBoolean("apk_download", false);
        return apk;
    }

    public void setPicBtn(boolean pic) {
        this.pic = pic;
    }

    public boolean getPicBtn() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        pic = sharedPreferences.getBoolean("pic_download", false);
        return pic;
    }

    public List<RouteSearchBean> getStationList() {
        if (stationList.size() == 0) {
            return null;
        } else {
            Log.d(TAG, "getStationList: " + stationList.size());
            return stationList;
        }
    }

    public void setStationList(List<RouteSearchBean> stationList) {
        this.stationList = stationList;
    }

    public List<String> getBusyStationList() {
        if (busyStationList.size() == 0) {
            return null;
        } else {
            return busyStationList;
        }
    }

    public void setBusyStationList(List<String> busyStationList) {
        this.busyStationList = busyStationList;
    }

    public void setAddressId(String id) {
        this.address_id = id;
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("default_addr_id", address_id);
        editor.commit();
    }

    public void setAddressName(String name) {
        this.address_name = name;
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("default_addr_name", address_name);
        editor.commit();
    }

    public void setAddressPhone(String phone) {
        this.address_phone = phone;
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("default_addr_phone", address_phone);
        editor.commit();
    }

    public String getAddressName() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        address_name = sharedPreferences.getString("default_addr_name", "");
        return address_name;
    }

    public String getAddressPhone() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        address_phone = sharedPreferences.getString("default_addr_phone", "");
        return address_phone;
    }

    public String getAddressId() {
        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
        address_id = sharedPreferences.getString("default_addr_id", "");
        return address_id;
    }
}