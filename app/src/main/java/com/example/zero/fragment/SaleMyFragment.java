package com.example.zero.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.activity.ShoppingCartActivity;
import com.example.zero.adapter.SaleMyCouponAdapter;
import com.example.zero.bean.SaleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jojo on 2017/9/13.
 */

public class SaleMyFragment extends Fragment {

    private String TAG = "SaleMyFragment";
    private View sale_my_frag;
    private RecyclerView my_recv;
    private List<SaleBean> dataList = new ArrayList<>();
    private Context context;
    private ArrayList<String> coupon_id = new ArrayList<>();
    private ArrayList<String> coupon_name = new ArrayList<>();
    private int coupon_type;
    private String coupon_price;
    private String coupon_content;
    private String coupon_time;
    private ArrayList<String> coupon_img = new ArrayList<>();
    private int coupon_status;
    private ArrayList<String> shop_id = new ArrayList<>();
//    private Map<String, String> map = new HashMap<>();
//    private ArrayList<Map<String, String>> coupon = new ArrayList<>();
    private SaleMyCouponAdapter adapter;
    private String uid, token;
    private TextView textView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sale_my_frag = inflater.inflate(R.layout.fragment_sale_my, container, false);
        context = sale_my_frag.getContext();
        innitView();
        textView = new TextView(context);
        textView.setBackgroundColor(Color.parseColor("#86E6E6E6"));
        adapter = new SaleMyCouponAdapter(context, dataList);
        my_recv.setAdapter(adapter);
        getCouponData();
        adapter.setOnItemClickListener(new SaleMyCouponAdapter.onRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(), "点击条目 " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBtnClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("shopId", shop_id.get(position));
                intent.putExtra("shopName", "");
                intent.putExtra("shopImg", "");
                intent.setClass(context, ShoppingCartActivity.class);
                startActivity(intent);
//                MainApplication application = (MainApplication) getActivity().getApplication();
//                uid = application.getUser_id();
//                token = application.getToken();
//                if (application.isOnline() == false) {
//                    Toast.makeText(context, "请您先登录再进行操作", Toast.LENGTH_LONG).show();
//                } else {
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("userId", uid);
//                    params.put("token", token);
//                    RequestManager.getInstance(context).requestAsyn("users/me/coupons/" + coupon_id.get(position), RequestManager.TYPE_DEL, params, new RequestManager.ReqCallBack<String>() {
//
//                        @Override
//                        public void onReqSuccess(String result) {
//                            Toast.makeText(context, "优惠券使用成功", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onReqFailed(String errorMsg) {
//                            JSONObject jo = JSON.parseObject(errorMsg);
//                            Toast.makeText(context, jo.getString("userMessage"), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                // }
            }
        });
        return sale_my_frag;
    }

    /**
     * 初始化view
     */
    private void innitView() {
        my_recv = (RecyclerView) sale_my_frag.findViewById(R.id.sale_my_recv);
        my_recv.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getCouponData();
        }
    }

    /**
     * 优惠券内容加载
     */
    public void getCouponData() {
        MainApplication application = (MainApplication) getActivity().getApplication();
        uid = application.getUser_id();
        token = application.getToken();
        if (application.isOnline() == false) {
//            Toast.makeText(context, "请您先登录再进行操作", Toast.LENGTH_SHORT).show();
        } else {
            dataList.clear();
            coupon_id.clear();
            shop_id.clear();
            coupon_name.clear();
            coupon_price = "";
            coupon_content = "";
            coupon_type = 0;
            coupon_time = "";
            coupon_img.clear();
            HashMap<String, String> params = new HashMap<>();
            params.put("userId", uid);
            params.put("token", token);
            RequestManager.getInstance(context).requestAsyn("users/me/coupons", RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<String>() {

                @Override
                public void onReqSuccess(String result) {
                    Log.e("AAA", result);
                    JSONArray array = JSONArray.parseArray(result);
                    for (int i = 0; i < array.size(); i++) {
                        String s = array.get(i).toString();
                        JSONObject jo = JSON.parseObject(s);
                        coupon_id.add(jo.getString("id"));
                        shop_id.add(jo.getString("shop_id"));
                        coupon_name.add(jo.getString("shop_name"));
                        coupon_type = jo.getInteger("type");
                        if (coupon_type == 1) {
                            String[] str = jo.getString("coupon_name").split("减");
                            coupon_price = "¥" + str[1];
                            coupon_content = "消费金额" + str[0] + "可用";
                        } else if (coupon_type == 2) {
                            coupon_price = jo.getString("coupon_name").substring(0, 2);
                            coupon_content = "全场商品" + coupon_price + "优惠";
                        }
                        String[] string = jo.getString("expire_at").split(" ");
                        coupon_time = string[0] + "到期";
                        coupon_img.add(jo.getString("image_url"));
                        coupon_status = jo.getInteger("status");
                        SaleBean saleBean = new SaleBean();
                        if (coupon_status == 0) {
                            saleBean.setText(coupon_name.get(i), coupon_price, coupon_content, coupon_time, coupon_img.get(i));
                            dataList.add(saleBean);
                        } else if (coupon_status == 1) {
                            saleBean.setTextView(textView);
                            saleBean.setUseFlag("yes");
                            saleBean.setText(coupon_name.get(i), coupon_price, coupon_content, coupon_time, coupon_img.get(i));
                            dataList.add(saleBean);
                        }else {
                            saleBean.setText(coupon_name.get(i), coupon_price, coupon_content, coupon_time, coupon_img.get(i));
                            dataList.add(saleBean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e(TAG, errorMsg);
                    Toast.makeText(context, "我的优惠券请求失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
