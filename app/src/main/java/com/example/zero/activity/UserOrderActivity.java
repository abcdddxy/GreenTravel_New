package com.example.zero.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.adapter.UserOrderAdapter;
import com.example.zero.bean.UserOrderBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kazu_0122 on 2017/11/15.
 */

public class UserOrderActivity extends Activity {

    private List<UserOrderBean> dataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView backArrow;
    private int type;
    private static final String TAG = "UserOrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);
        type = getIntent().getIntExtra("type", -1);
        recyclerView = (RecyclerView) findViewById(R.id.user_order_recy);
        initData(type);
        UserOrderAdapter adapter = new UserOrderAdapter(UserOrderActivity.this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        backArrow = (TextView) findViewById(R.id.user_order_back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter.setOnInnerItemClickListener(new MultiItemTypeAdapter.OnInnerItemClickListener() {
            @Override
            public void onInnerItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                switch (view.getId()) {
                    case R.id.user_order_cancel: {
                        MainApplication mainApplication = (MainApplication) getApplication();
                        final String userId = "A7F171027153611";
                        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJncmVlbl90cmF2ZWwiLCJpYXQiOjE1MTEzNDE1MjQsInN1YiI6IntcImF2YXRvcl91cmxcIjpcIkE3RjE3MTAyNzE1MzYxMTE3MTAzMDIyMjg1NS5wbmdcIixcImNyZWF0ZV90aW1lXCI6XCIyMDE3LTEwLTI3IDE1OjM2OjU3LjBcIixcImVtYWlsX3ZlcmlmeVwiOjAsXCJwaG9uZVwiOlwiMTg4MTEzMTk2OTNcIixcInBob25lX21zZ19zZW5kX3RpbWVcIjpcIjIwMTctMTEtMDEgMTA6MTE6NDMuMFwiLFwicGhvbmVfdmVyaWZ5XCI6MSxcInBob25lX3ZlcmlmeV9jb2RlXCI6NTU4MSxcInRva2VuX2tleVwiOlwiR3dkWTRuNDRab2ZSNjZzOVQ2RlJBaWlRYWJHSTFSXCIsXCJ1c2VyX2lkXCI6XCJBN0YxNzEwMjcxNTM2MTFcIixcInVzZXJuYW1lXCI6XCJ5eXV1YWFcIixcInVzZXJwc3dcIjpcIjEyM2J1cHRcIn0iLCJleHAiOjE1MTI1NTExMjR9.9lhIQn4VDOG95ZhpmWsPsbZiLGKCfYlkv2uaQIdhLCE";

//        final String userId = mainApplication.getUser_id();
//        final String token = mainApplication.getToken();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id", userId);
                        params.put("token", token);
                        params.put("order_no", dataList.get(position).getOrderId());
                        RequestManager.getInstance(getBaseContext()).requestAsyn("order/usercancel", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                            @Override
                            public void onReqSuccess(String result) {
                                Toast.makeText(getBaseContext(), "canceled", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {
                                Toast.makeText(getBaseContext(), "cancel failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                    case R.id.user_order_pay:
                        Toast.makeText(getBaseContext(), "pay", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.user_order_prolong:
                        Toast.makeText(getBaseContext(), "prolong", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.user_order_receive: {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("order_no", dataList.get(position).getOrderId());
                        RequestManager.getInstance(getBaseContext()).requestAsyn("order/gen_QRcode_content", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                            @Override
                            public void onReqSuccess(String result) {
                                JSONObject object = JSON.parseObject(result);
                                Intent intent = new Intent();
                                intent.setClass(UserOrderActivity.this, QRcodeActivity.class);
                                intent.putExtra("content", object.getString("qr_content"));
                                startActivity(intent);
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {
                                Toast.makeText(getBaseContext(), "failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                    }
                    case R.id.user_order_comment:
                        Toast.makeText(getBaseContext(), "comment", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "default", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void initData(int type) {
        final String order_type;
        switch (type) {
            case -1:
                order_type = "all";
                break;
            case 0:
                order_type = "ordered";
                break;
            case 1:
                order_type = "payed";
                break;
            case 2:
                order_type = "topick";
                break;
            case 3:
                order_type = "finish";
                break;
            case 4:
                order_type = "canceled";
                break;
            default:
                order_type = "all";
        }

        MainApplication mainApplication = (MainApplication) getApplication();
        final String userId = "A7F171027153611";
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJncmVlbl90cmF2ZWwiLCJpYXQiOjE1MTEzNDE1MjQsInN1YiI6IntcImF2YXRvcl91cmxcIjpcIkE3RjE3MTAyNzE1MzYxMTE3MTAzMDIyMjg1NS5wbmdcIixcImNyZWF0ZV90aW1lXCI6XCIyMDE3LTEwLTI3IDE1OjM2OjU3LjBcIixcImVtYWlsX3ZlcmlmeVwiOjAsXCJwaG9uZVwiOlwiMTg4MTEzMTk2OTNcIixcInBob25lX21zZ19zZW5kX3RpbWVcIjpcIjIwMTctMTEtMDEgMTA6MTE6NDMuMFwiLFwicGhvbmVfdmVyaWZ5XCI6MSxcInBob25lX3ZlcmlmeV9jb2RlXCI6NTU4MSxcInRva2VuX2tleVwiOlwiR3dkWTRuNDRab2ZSNjZzOVQ2RlJBaWlRYWJHSTFSXCIsXCJ1c2VyX2lkXCI6XCJBN0YxNzEwMjcxNTM2MTFcIixcInVzZXJuYW1lXCI6XCJ5eXV1YWFcIixcInVzZXJwc3dcIjpcIjEyM2J1cHRcIn0iLCJleHAiOjE1MTI1NTExMjR9.9lhIQn4VDOG95ZhpmWsPsbZiLGKCfYlkv2uaQIdhLCE";

//        final String userId = mainApplication.getUser_id();
//        final String token = mainApplication.getToken();
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("token", token);
        params.put("order_type", order_type);
        RequestManager.getInstance(getBaseContext()).requestAsyn("order/userorderlist", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                JSONObject object = JSON.parseObject(result);
                JSONArray orders = JSON.parseArray(object.getString("orders"));
                for (int i = 0; i < orders.size(); i++) {
                    JSONObject order = orders.getJSONObject(i);
                    UserOrderBean bean1 = new UserOrderBean();
                    UserOrderBean bean3 = new UserOrderBean();
                    bean1.setShopName(order.getString("seller_name"));
                    bean1.setState(order.getInteger("order_status"));
                    bean3.setState(order.getInteger("order_status"));
                    if (order.getInteger("order_status") == 3 && order.getInteger("receive_method") == 1) {
                        bean1.setState(8);
                        bean3.setState(8);
                    }
                    bean1.setOrderId(order.getString("order_no"));
                    bean3.setOrderId(order.getString("order_no"));
                    bean1.setIcon(R.drawable.t_mall);
                    bean1.setItem(UserOrderBean.SHOP);
                    dataList.add(bean1);
                    JSONArray goods = order.getJSONArray("orderDetails");
                    int count = 0;
                    double sum = 0;
                    for (int j = 0; j < goods.size(); j++) {
                        JSONObject good = goods.getJSONObject(j);
                        UserOrderBean bean2 = new UserOrderBean();
                        bean2.setGoodsPrice(good.getDouble("product_price"));
                        bean2.setGoodsCount(good.getInteger("product_num"));
                        bean2.setGoodsName(good.getString("product_name"));
                        bean2.setGoodsPic(good.getString("pic_url"));
                        bean2.setItem(UserOrderBean.GOODS);
                        dataList.add(bean2);
                        count += good.getInteger("product_num");
                        sum += bean2.getGoodsPrice() * bean2.getGoodsCount();
                    }
                    bean3.setGoodsCount(count);
                    bean3.setGoodsPrice(sum);
                    bean3.setItem(UserOrderBean.ACTION);
                    dataList.add(bean3);
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e(TAG, errorMsg);
            }
        });
    }
}
