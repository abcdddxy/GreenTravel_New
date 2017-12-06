package com.example.zero.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.adapter.OrderAdapter;
import com.example.zero.bean.OrderBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.RequestManager;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jojo on 2017/11/13.
 */

public class OrderConfirmActivity extends AppCompatActivity {

    private static final String TAG = "OrderConfirmActivity";
    private static final String appId = "wx075d26ce1edffcc8";
    private List<OrderBean> dataList = new ArrayList<>();
    private RecyclerView order_recy;
    private TextView finalPrice;
    private Button submit;
    private TextView backArrow;
    private String shopName;
    private String shopId;
    private String coupons;
    private String mailing;
    private int count;
    private int size = 100;
    private String addr_id, addr_name, addr_phone;
    private String[] idList = new String[size];
    private String[] nameList = new String[size];
    private String[] posterList = new String[size];
    private double[] priceList = new double[size];
    private int[] numList = new int[size];
    private PopupWindow popupWindow;
    private View contentView;
    private RadioButton rb1, rb2;
    private TextView cancel;
    private View other;
    private OrderAdapter adapter;
    private int flag1 = 1, flag2 = 1;
    private String coupon_name = "";
    private ArrayList<Map<String, String>> couponInfo = new ArrayList<>();

    IWXAPI msgApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        initView();
        Intent intent = getIntent();
        final Bundle mBundle = intent.getExtras();
        shopName = mBundle.getString("shopName");
        shopId = mBundle.getString("shopId");
        count = mBundle.getInt("size");
        idList = mBundle.getStringArray("idList");
        nameList = mBundle.getStringArray("nameList");
        posterList = mBundle.getStringArray("posterList");
        priceList = mBundle.getDoubleArray("priceList");
        numList = mBundle.getIntArray("numList");
        coupons = mBundle.getString("coupons");
        mailing = mBundle.getString("mailing");
        getCouponInfo();
        adapter = new OrderAdapter(OrderConfirmActivity.this, dataList);
        order_recy.setAdapter(adapter);
        MainApplication application = (MainApplication) getApplication();
        addr_id = application.getAddressId();
        addr_name = application.getAddressName();
        addr_phone = application.getAddressPhone();
        if (couponInfo.size() != 0) {
            coupon_name = couponInfo.get(0).get("name");
        }
        showGoodsList(coupon_name, flag2);
        adapter.setOnInnerItemClickListener(new MultiItemTypeAdapter.OnInnerItemClickListener() {
            @Override
            public void onInnerItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                switch (view.getId()) {
                    case R.id.order_discount:
                        showPopupwindow1();
                        break;
                    case R.id.order_deliver:
                        showPopupwindow2();
                        break;
                    case R.id.order_cus_addr:
                        Intent intent1 = new Intent();
                        intent1.putExtra("type", "orderConfirm");
                        intent1.setClass(OrderConfirmActivity.this, AddressActivity.class);
                        startActivityForResult(intent1, 1);
                    default:
                        break;
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                JSONArray goodsList = new JSONArray();
//                for (int i = 0; i < size; i++) {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("good_id", idList[i]);
//                    jsonObject.put("goods_num", numList[i]);
//                    jsonObject.put("good_price", priceList[i]);
//                    goodsList.add(jsonObject);
//                }
//                MainApplication mainApplication = (MainApplication) getApplication();
//                JSONObject order = new JSONObject();
//                order.put("coupon_id", "0");
//                order.put("products", goodsList.toString());
//                order.put("receive_id", "0");
//                order.put("remark", "hahaha");
//                order.put("seller_id", shopId);
//                order.put("token", mainApplication.getToken());
//                order.put("user_id", mainApplication.getUser_id());
//                order.put("user_source", "ticket");
//                Log.d(TAG, order.toString());
//                HashMap<String, String> params = new HashMap<>();
//                params.put("order", order.toString());
//                RequestManager.getInstance(getBaseContext()).requestAsyn("/order/submit", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
//                    @Override
//                    public void onReqSuccess(String result) {
//                        JSONObject object = JSONObject.parseObject(result);
//                        Toast.makeText(getBaseContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                //msgApi = WXAPIFactory.createWXAPI(OrderConfirmActivity.this, null);
                // msgApi.registerApp(appId);// 将该app注册到微信
                msgApi = WXAPIFactory.createWXAPI(OrderConfirmActivity.this, appId);
                if (!msgApi.isWXAppInstalled()) {
                    Toast.makeText(OrderConfirmActivity.this, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                } else {
                    MainApplication application = (MainApplication) getApplication();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", application.getUser_id());
                    params.put("token", application.getToken());
                    params.put("order_no", "1512454872376edZogoPqZ7"); //object.getString("order_no")
                    RequestManager.getInstance(OrderConfirmActivity.this).requestAsyn("order/getprepayid", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            //todo:获取prepayid，调起微信支付
                            Toast.makeText(OrderConfirmActivity.this, "开始支付", Toast.LENGTH_LONG).show();
                            JSONObject jo = JSON.parseObject(result);
                            try {
                                PayReq request = new PayReq();
                                request.packageValue = jo.getString("package");
                                request.appId = jo.getString("appid");
                                request.partnerId = jo.getString("partnerid");
                                request.prepayId = jo.getString("prepayId");
                                // request.packageValue = "Sign=WXPay";
                                request.nonceStr = jo.getString("noncestr");
                                request.timeStamp = jo.getString("timestamp");
                                request.sign = jo.getString("sign");
                                request.extData = "app data";
                                Log.i(TAG, "onReqSuccess: " + msgApi.sendReq(request));
//                                msgApi.sendReq(request);
                            } catch (Exception e) {
                                Log.i(TAG, "onReqSuccess: " + e.getMessage());
                            }
                            Log.d(TAG, "发起微信支付申请");
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Log.e(TAG, errorMsg);
                            Toast.makeText(OrderConfirmActivity.this, "支付失败", Toast.LENGTH_LONG).show();
                        }
                    });
                }
//                    }
//
//                    @Override
//                    public void onReqFailed(String errorMsg) {
//                        Log.e(TAG, errorMsg);
//                    }
//                });
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initView() {
        order_recy = (RecyclerView) findViewById(R.id.order_recy);
        order_recy.setLayoutManager(new LinearLayoutManager(this));
        finalPrice = (TextView) findViewById(R.id.order_final_price);
        backArrow = (TextView) findViewById(R.id.order_confirm_back_arrow);
        submit = (Button) findViewById(R.id.order_button_submit);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                MainApplication application = (MainApplication) getApplication();
                addr_id = application.getAddressId();
                addr_name = application.getAddressName();
                addr_phone = application.getAddressPhone();
                if (resultCode == 1) {
                    addr_id = data.getStringExtra("id");
                    addr_name = data.getStringExtra("name");
                    addr_phone = data.getStringExtra("phone");
                }
                showGoodsList(coupon_name, flag2);
                break;
            default:
                break;
        }
    }

    private void getCouponInfo() {
        couponInfo.clear();
        JSONObject couponObject = JSON.parseObject(coupons);
        //JSONArray couponArray = JSON.parseArray(couponObject.getString("couponList"));
        JSONObject bestCoupon = JSONObject.parseObject(couponObject.getString("bestCoupon"));
        if (bestCoupon == null) {
            Log.d(TAG, "bestCoupon = null");
        } else {
            String expire = bestCoupon.getString("expire_at");
            Timestamp ts1 = new Timestamp(System.currentTimeMillis());
            Timestamp ts2 = new Timestamp(System.currentTimeMillis());
            try {
                ts2 = Timestamp.valueOf(expire);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bestCoupon.getBoolean("owned") && ts2.getTime() > ts1.getTime()) {
                if (bestCoupon.getInteger("status") == 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", bestCoupon.getString("id"));
                    map.put("type", bestCoupon.getString("type"));
                    map.put("name", bestCoupon.getString("coupon_name"));
                    couponInfo.add(map);
                }
            }
        }
    }

    private void showGoodsList(String coupon_name, int type) {
        dataList.clear();
        OrderBean orderBean = new OrderBean();
        if (addr_id.equals("") || addr_name.equals("") || addr_phone.equals("")) {
            orderBean.setTip(true);
            orderBean.setAddrTip("还没有收货地址,请添加您的收货信息");
            orderBean.setItem(OrderBean.ADDRESS);
        } else {
            orderBean.setTip(false);
            orderBean.setAddrIcon(R.drawable.address);
            orderBean.setAddrService("(收货不便时，可选择免费代收服务)");
            orderBean.setAddrId(addr_id);
            orderBean.setCusName("收货人：" + addr_name);
            orderBean.setCusPhone(addr_phone);
            orderBean.setItem(OrderBean.ADDRESS);
        }
        dataList.add(orderBean);
        OrderBean orderBean1 = new OrderBean();
        orderBean1.setShopName(shopName);
        orderBean1.setIcon(R.drawable.shopcart_shop);
        orderBean1.setItem(OrderBean.SHOP);
        dataList.add(orderBean1);
        double sum = 0;
        int allCount = 0;
        for (int i = 0; i < count; i++) {
            OrderBean orderBean2 = new OrderBean();
            orderBean2.setGoodsName(nameList[i]);
            orderBean2.setGoodsPrice1("￥ " + priceList[i]);
            orderBean2.setGoodsCount1("x" + numList[i]);
            orderBean2.setGoodsPic(posterList[i]);
            orderBean2.setItem(OrderBean.GOODSINFO);
            sum += priceList[i] * numList[i];
            allCount += numList[i];
            dataList.add(orderBean2);
        }
        OrderBean orderBean3 = new OrderBean();
        if (coupon_name.equals("无可用优惠")) {
            orderBean3.setDiscount("");
        } else {
            orderBean3.setDiscount(coupon_name);
        }
        if (type == 1) {
            orderBean3.setDeliver("门店自取");
        } else if (type == 2) {
            orderBean3.setDeliver("自提柜取货");
        }
        orderBean3.setGoodsCount2("共" + allCount + "件商品   小计:   ");
        orderBean3.setGoodsPrice2("￥ " + sum);
        orderBean3.setItem(OrderBean.GOODSOTHER);
        dataList.add(orderBean3);
        OrderBean orderBean4 = new OrderBean();
        orderBean4.setOrderMsg("温馨提示");
        orderBean4.setItem(OrderBean.ORDEROTHER);
        dataList.add(orderBean4);
        if (coupon_name.equals("") || coupon_name.equals("无可用优惠")) {

        } else if (couponInfo.size() != 0) {
            if (couponInfo.get(0).get("type").equals("1")) {
                String[] str = coupon_name.split("减");
                sum = sum - Integer.parseInt(str[str.length - 1]);
            } else if (couponInfo.get(0).get("type").equals("2")) {
                String[] str = coupon_name.split("折");
                sum = sum * Integer.parseInt(str[0]) * 0.1;
            }
        }
        finalPrice.setText("￥ " + new java.text.DecimalFormat("0.00").format(sum));
        adapter.notifyDataSetChanged();
    }

    public void showPopupwindow1() {
        contentView = LayoutInflater.from(OrderConfirmActivity.this).inflate(R.layout.popwindow_order_coupon, null);
        rb1 = (RadioButton) contentView.findViewById(R.id.popwindow1_rb1);
        rb2 = (RadioButton) contentView.findViewById(R.id.popwindow1_rb2);
        cancel = (TextView) contentView.findViewById(R.id.popwindow1_close);
        other = (View) contentView.findViewById(R.id.popwindow1_other);
        RelativeLayout rl = (RelativeLayout) contentView.findViewById(R.id.popwindow1_rl);
        if (couponInfo.size() != 0) {
            rb1.setText(couponInfo.get(0).get("name"));
        } else {
            rb1.setText("无可用优惠");
        }
        if (flag1 == 1) {
            rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
            rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
        } else if (flag1 == 2) {
            rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
            rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
        }
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag1 = 1;
                rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
                rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
                coupon_name = (String) rb1.getText();
                showGoodsList(coupon_name, flag2);
                popupWindow.dismiss();
            }
        });
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag1 = 2;
                rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
                rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
                coupon_name = "";
                showGoodsList(coupon_name, flag2);
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);// 取得焦点
        popupWindow.setOutsideTouchable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        popupWindow.setAnimationStyle(R.style.bottom_popwindow);
        rl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    public void showPopupwindow2() {
        contentView = LayoutInflater.from(OrderConfirmActivity.this).inflate(R.layout.popwindow_order_deliver, null);
        rb1 = (RadioButton) contentView.findViewById(R.id.popwindow2_rb1);
        rb2 = (RadioButton) contentView.findViewById(R.id.popwindow2_rb2);
        cancel = (TextView) contentView.findViewById(R.id.popwindow2_close);
        other = (View) contentView.findViewById(R.id.popwindow2_other);
        RelativeLayout rl = (RelativeLayout) contentView.findViewById(R.id.popwindow2_rl);
        if (flag2 == 1) {
            rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
            rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
        } else if (flag2 == 2) {
            rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
            rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
        }
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag2 = 1;
                rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
                rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
                showGoodsList(coupon_name, flag2);
                popupWindow.dismiss();
            }
        });
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag2 = 2;
                rb1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_off, 0);
                rb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_on, 0);
                showGoodsList(coupon_name, flag2);
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setAnimationStyle(R.style.bottom_popwindow);
        rl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    public void wxPay() {

    }
}
