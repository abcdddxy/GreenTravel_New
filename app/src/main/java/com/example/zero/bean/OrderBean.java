package com.example.zero.bean;

import com.example.zero.greentravel_new.R;

/**
 * Created by jojo on 2017/11/13.
 */

public class OrderBean {

    private String shop_name, goods_name, goods_type, goods_pic;
    private int icon;
    private String goods_count1, goods_count2;
    private String goods_price1, goods_price2;
    private String addr_id, cus_name, cus_phone, cus_addr;
    private String item;
    private String order_msg;
    private String discount, deliver, cus_msg;
    private String addr_tip;
    private int addr_icon;
    private String addr_service;
    private boolean tip;
    public static final String ADDRESS = "addr";
    public static final String GOODSINFO = "goodsinfo";
    public static final String ORDEROTHER = "other";
    public static final String SHOP = "shop";
    public static final String GOODSOTHER = "goodsother";

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setShopName(String name) {
        this.shop_name = name;
    }

    public void setGoodsName(String name) {
        this.goods_name = name;
    }

    public void setGoodsType(String type) {
        this.goods_type = type;
    }

    public void setGoodsPic(String pic) {
        this.goods_pic = pic;
    }

    public void setGoodsCount1(String count) {
        this.goods_count1 = count;
    }

    public void setGoodsCount2(String count) {
        this.goods_count2 = count;
    }

    public void setGoodsPrice1(String price) {
        this.goods_price1 = price;
    }

    public void setGoodsPrice2(String price) {
        this.goods_price2 = price;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setAddrId(String id) {
        this.addr_id = id;
    }

    public void setCusName(String name) {
        this.cus_name = name;
    }

    public void setCusPhone(String phone) {
        this.cus_phone = phone;
    }

    public void setCusAddr(String addr) {
        this.cus_addr = addr;
    }

    public void setOrderMsg(String msg) {
        this.order_msg = msg;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setDeliver(String deliver) {
        this.deliver = deliver;
    }

    public void setCusMsg(String msg) {
        this.cus_msg = msg;
    }

    public void setAddrTip(String addr_tip) {
        this.addr_tip = addr_tip;
    }

    public void setTip(boolean tip) {
        this.tip = tip;
    }

    public void setAddrIcon(int icon) {
        this.addr_icon = icon;
    }

    public void setAddrService(String service) {
        this.addr_service = service;
    }

    public boolean getTip() {
        return tip;
    }

    public String getAddrTip() {
        return addr_tip;
    }

    public int getAddrIcon() {
        return addr_icon;
    }

    public String getAddrService() {
        return addr_service;
    }

    public String getAddrId() {
        return addr_id;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDeliver() {
        return deliver;
    }

    public String getCusMsg() {
        return cus_msg;
    }

    public String getOrdermsg() {
        return order_msg;
    }

    public String getCusName() {
        return cus_name;
    }

    public String getCusPhone() {
        return cus_phone;
    }

    public String getCusAddr() {
        return cus_addr;
    }

    public String getShopName() {
        return shop_name;
    }

    public String getGoodsName() {
        return goods_name;
    }

    public String getGoodsType() {
        return goods_type;
    }

    public String getGoodsPic() {
        return goods_pic;
    }

    public String getGoodsCount1() {
        return goods_count1;
    }

    public String getGoodsCount2() {
        return goods_count2;
    }

    public String getGoodsPrice1() {
        return goods_price1;
    }

    public String getGoodsPrice2() {
        return goods_price2;
    }

    public String getItem() {
        return item;
    }

    public int getIcon() {
        return icon;
    }
}
