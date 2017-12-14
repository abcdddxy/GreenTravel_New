package com.example.zero.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MainApplication;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.RequestManager;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by kazu_0122 on 2017/11/15.
 */

public class UserOrderActivity extends Activity {

    private List<UserOrderBean> dataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView backArrow;
    private int type;
    private static final String TAG = "UserOrderActivity";
    private static final String appId = "wx075d26ce1edffcc8";
    private String money;
    private JSONObject order;
    private JSONObject good;
    private MainApplication mainApplication;
    private String order_type;
    private UserOrderBean bean1;
    private UserOrderBean bean2;
    private UserOrderBean bean3;
    private int orderCount;
    private int goodCount;

    private int size = 100;
    private String[] shopNameList = new String[size];
    private Integer[] stateList = new Integer[size];
    private Integer[] receiveMethodList = new Integer[size];
    private String[] orderIdList = new String[size];

    HashMap<Integer, UserOrderBean> orderGood = new HashMap<>();
    private String[] goodMoneyList = new String[size];
    private Double[] goodPriceList = new Double[size];
    private Integer[] goodCountList = new Integer[size];
    private String[] goodNameList = new String[size];
    private String[] goodPic = new String[size];
    private Integer[] goodNumList = new Integer[size];

    private ProgressDialog pd;

    //定义Handler对象
    private Handler httpHandler = new Handler(new Handler.Callback() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public boolean handleMessage(Message msg) {
            //只要执行到这里就关闭对话框
            initView();

            pd.dismiss();
            return false;
        }
    });

    private void httpThread() {
        //构建一个下载进度条
        pd = ProgressDialog.show(UserOrderActivity.this, "加载数据", "数据加载中，请稍后......");

        new Thread() {
            @Override
            public void run() {
                //在新线程里执行长耗时方法
                longTimeMethod();
                //执行完毕后给handler发送一个空消息
                httpHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    //加载数据
    private void longTimeMethod() {
        try {
            final String userId = mainApplication.getUser_id();
            final String token = mainApplication.getToken();
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("token", token);
            params.put("order_type", order_type);
            RequestManager.getInstance(getBaseContext()).requestAsyn("order/userorderlist", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                @Override
                public void onReqSuccess(String result) {
                    JSONObject object = JSON.parseObject(result);
                    parseJSONWithJSONObject(object);
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e(TAG, errorMsg);
                }
            });
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject(JSONObject object) {
        orderCount = 0;
        goodCount = 0;
        JSONArray orders = JSON.parseArray(object.getString("orders"));
        for (int i = 0; i < orders.size(); i++) {
            order = orders.getJSONObject(i);

            shopNameList[i] = order.getString("seller_name");
            stateList[i] = order.getInteger("order_status");
            receiveMethodList[i] = order.getInteger("receive_method");
            orderIdList[i] = order.getString("order_no");
            orderCount++;

            JSONArray goods = order.getJSONArray("orderDetails");
            goodNumList[i] = goods.size();
            for (int j = 0; j < goods.size(); j++) {
                good = goods.getJSONObject(j);

                goodMoneyList[goodCount + j] = good.getDouble("product_price").toString();
                goodPriceList[goodCount + j] = good.getDouble("product_price");
                goodCountList[goodCount + j] = good.getInteger("product_num");
                goodNameList[goodCount + j] = good.getString("product_name");
                goodPic[goodCount + j] = good.getString("pic_url");
            }
            goodCount += goods.size();
        }
    }

    private void initView() {
        int goodNum = 0;

        for (int i = 0; i < orderCount; i++) {
            bean1 = new UserOrderBean();
            bean3 = new UserOrderBean();
            bean1.setShopName(shopNameList[i]);
            bean1.setState(stateList[i]);
            bean3.setState(stateList[i]);
            if (stateList[i] == 3 && receiveMethodList[i] == 1) {
                bean1.setState(8);
                bean3.setState(8);
            }
            bean1.setOrderId(orderIdList[i]);
            bean3.setOrderId(orderIdList[i]);
            bean1.setIcon(R.drawable.shopcart_shop);
            bean1.setItem(UserOrderBean.SHOP);
            dataList.add(bean1);

            int count = 0;
            double sum = 0;
            for (int j = 0; j < goodNumList[i]; j++) {
                bean2 = new UserOrderBean();
                money = goodMoneyList[goodNum + j];
                bean2.setGoodsPrice(goodPriceList[goodNum + j]);
                bean2.setGoodsCount(goodCountList[goodNum + j]);
                bean2.setGoodsName(goodNameList[goodNum + j]);
                if (!goodPic[goodNum + j].contains("http")) {
                    bean2.setGoodsPic("http://10.108.120.225:8080/" + goodPic[goodNum + j]);
                } else {
                    bean2.setGoodsPic(goodPic[goodNum + j]);
                }

                bean2.setItem(UserOrderBean.GOODS);
                dataList.add(bean2);

                count += goodCountList[goodNum + j];
                sum += bean2.getGoodsPrice() * bean2.getGoodsCount();
            }
            goodNum += goodNumList[i];
            bean3.setGoodsCount(count);
            bean3.setGoodsPrice(sum);
            bean3.setItem(UserOrderBean.ACTION);
            dataList.add(bean3);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);
        type = getIntent().getIntExtra("type", -1);
        recyclerView = (RecyclerView) findViewById(R.id.user_order_recy);
        Log.d("typeeee", type + "");
        mainApplication = (MainApplication) getApplication();
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
            public void onInnerItemClick(View view, RecyclerView.ViewHolder holder, final int position) {
                switch (view.getId()) {
                    case R.id.user_order_cancel: {
                        MainApplication mainApplication = (MainApplication) getApplication();
                        final String userId = mainApplication.getUser_id();
                        final String token = mainApplication.getToken();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id", userId);
                        params.put("token", token);
                        params.put("order_no", dataList.get(position).getOrderId());
                        RequestManager.getInstance(getBaseContext()).requestAsyn("order/usercancel", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                            @Override
                            public void onReqSuccess(String result) {
                                initData(0);
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {
                                Log.e(TAG, errorMsg);
                                Toast.makeText(getBaseContext(), "无法取消该订单", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                    case R.id.user_order_pay:
                        final IWXAPI msgApi = WXAPIFactory.createWXAPI(UserOrderActivity.this, null);
                        msgApi.registerApp(appId);
                        if (!msgApi.isWXAppInstalled()) {
                            Toast.makeText(UserOrderActivity.this, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                        } else {
                            final MainApplication application = (MainApplication) getApplication();
                            HashMap<String, String> params = new HashMap<>();
                            params.put("user_id", application.getUser_id());
                            params.put("token", application.getToken());
                            params.put("order_no", dataList.get(position).getOrderId());
                            RequestManager.getInstance(UserOrderActivity.this).requestAsyn("order/getprepayid", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                                @Override
                                public void onReqSuccess(String result) {
                                    JSONObject jo = JSON.parseObject(result);
                                    try {
                                        PayReq request = new PayReq();
                                        request.packageValue = jo.getString("package");
                                        request.appId = jo.getString("appid");
                                        request.partnerId = jo.getString("partnerid");
                                        request.prepayId = jo.getString("prepayid");
                                        request.nonceStr = jo.getString("noncestr");
                                        request.timeStamp = jo.getString("timestamp");
                                        request.sign = jo.getString("sign");
                                        msgApi.sendReq(request);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                    Log.d(TAG, "发起微信支付申请");
                                    application.setOrder(dataList.get(position).getOrderId(), money);
                                }

                                @Override
                                public void onReqFailed(String errorMsg) {
                                    Log.e(TAG, errorMsg);
                                    Toast.makeText(UserOrderActivity.this, "支付失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
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
                        break;
                }
            }
        });
    }

    public void initData(int type) {
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

        httpThread();
    }
}
