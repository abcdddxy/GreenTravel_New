package com.example.zero.bean;


import java.util.ArrayList;

/**
 * Created by kazu_0122 on 2017/9/22.
 */

public class AdvDestinSearchBean {
    private boolean isStation;
    private boolean isExtend;
    private String stationTag;
    private String title;
    private String img_url;
    private String address;
    private String phone;
    private String shopId;
    private String sellerId;
    private int comments;
    private int price;
    private float distance;
    private float rate;
    private ArrayList<String> labels;

    public void setText(String stationTag, int count) {
        isStation = true;
        isExtend = false;
        this.stationTag = stationTag;
        this.title = stationTag;
        this.address = "";
        this.img_url = "";
        this.phone = "";
        this.comments = count;
        this.distance = 0;
        this.price = 0;
        this.rate = 0;
        this.labels = null;
    }

    public void setText(boolean isStation, String stationTag, String title, String address, String phone, String img, int comments, int
            price, float distance, float rate, ArrayList<String> labels) {
        this.isStation = isStation;
        this.isExtend = false;
        this.stationTag = stationTag;
        this.title = title;
        this.address = address;
        this.comments = comments;
        this.phone = phone;
        this.distance = distance;
        this.img_url = img;
        this.price = price;
        this.rate = rate;
        this.labels = labels;
    }

    public void setText(boolean isStation,String shopId, String sellerId, String stationTag, String title, String address, String phone, String img, int comments, int
            price, float distance, float rate, ArrayList<String> labels) {
        this.shopId = shopId;
        this.sellerId = sellerId;
        this.isStation = isStation;
        this.isExtend = false;
        this.stationTag = stationTag;
        this.title = title;
        this.address = address;
        this.comments = comments;
        this.phone = phone;
        this.distance = distance;
        this.img_url = img;
        this.price = price;
        this.rate = rate;
        this.labels = labels;
    }

    public boolean isStation() {
        return isStation;
    }

    public boolean isStore() {
        return !isStation;
    }

    public boolean getToggle() {
        return isExtend;
    }

    public void setToggle(boolean toggle) {
        isExtend = toggle;
    }

    public String getShopId() {
        return shopId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getStationTag() {
        return stationTag;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public int getComments() {
        return comments;
    }

    public String getPhone() {
        return phone;
    }

    public String getImg() {
        return img_url;
    }

    public int getPrice() {
        return price;
    }

    public float getDistance() {
        return distance;
    }

    public float getRate() {
        return rate;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

}
