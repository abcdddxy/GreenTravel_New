package com.example.zero.entity;

/**
 * Created by Jfcui on 2018/4/23
 */
public class GiftCoupon {
    private String Id;
    private double lat;
    private double lng;

    public void GiftCoupon(){}

    public GiftCoupon(String id, double lat, double lng) {
        Id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
