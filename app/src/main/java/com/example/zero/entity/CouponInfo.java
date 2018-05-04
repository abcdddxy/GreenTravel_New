package com.example.zero.entity;

/**
 * Created by Jfcui on 2018/4/24
 */
public class CouponInfo {
    private String id;
    //1表示打折券，2表示满减券
    private Integer type;
    private String coupon_name;
    private String shop_id;
    private String seller_id;
    private String expire_at;
    private String shop_tag;
    private String image_url;
    private String shop_name;

    public CouponInfo() {
    }

    public CouponInfo(String id, Integer type, String coupon_name, String shop_id, String seller_id, String expire_at, String shop_tag, String image_url, String shop_name) {
        this.id = id;
        this.type = type;
        this.coupon_name = coupon_name;
        this.shop_id = shop_id;
        this.seller_id = seller_id;
        this.expire_at = expire_at;
        this.shop_tag = shop_tag;
        this.image_url = image_url;
        this.shop_name = shop_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getExpire_at() {
        return expire_at;
    }

    public void setExpire_at(String expire_at) {
        this.expire_at = expire_at;
    }

    public String getShop_tag() {
        return shop_tag;
    }

    public void setShop_tag(String shop_tag) {
        this.shop_tag = shop_tag;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

}
