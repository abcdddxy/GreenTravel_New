package com.example.zero.fragment;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.activity.ShoppingCartActivity;
import com.example.zero.adapter.SaleMyCouponAdapter;
import com.example.zero.bean.SaleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jojo on 2017/9/13.
 */

public class SaleMyFragment extends Fragment {

    private String TAG = "SaleMyFragment";
    private View sale_my_frag;
    private RecyclerView my_recv;
    private List<SaleBean> dataList = new ArrayList<>();
    private Context context;
    private int coupon_type;
    private String coupon_price;
    private String coupon_content;
    private String coupon_time;
    private int coupon_status;
    private boolean coupon_giftEnable;
    private ArrayList<String> seller_id = new ArrayList<>();
    private ArrayList<String> shop_id = new ArrayList<>();
    private ArrayList<String> coupon_id = new ArrayList<>();
    private ArrayList<String> coupon_name = new ArrayList<>();
    private ArrayList<String> coupon_send = new ArrayList<>();
    private ArrayList<String> coupon_img = new ArrayList<>();
    private SaleMyCouponAdapter adapter;
    private String uid, token;
    private TextView textView1;
    private FrameLayout sale_my_ll;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    // 优惠券赠送方式
    private String[] gift_means = {"个人， 生成赠送码", "公开, 当前位置500m内可领取"};
    // 优惠券赠送方式选取
    private int gift_mean_selected = 0;
    private int succeed = 0;
    private String share_code;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sale_my_frag = inflater.inflate(R.layout.fragment_sale_my, container, false);
        context = sale_my_frag.getContext();
        initView();
        adapter = new SaleMyCouponAdapter(context, dataList);
        my_recv.setAdapter(adapter);
        getCouponData();
        adapter.setOnItemClickListener(new SaleMyCouponAdapter.onRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onBtnClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("shopId", shop_id.get(position));
                intent.putExtra("shopName", coupon_name.get(position));
                intent.putExtra("shopImg", coupon_img.get(position));
                intent.putExtra("sellerId", seller_id.get(position));
                intent.setClass(context, ShoppingCartActivity.class);
                startActivity(intent);
            }

            @Override
            public void onGiftClick(View view, int position){
                final String coupon = coupon_id.get(position);
                Log.d("gift", coupon);
                Log.d("gift", coupon_name.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.icon_coupon);
                builder.setTitle("请选择您的赠送方式：");
//                builder.setMessage("注意： 赠送操作会扣除您的这张优惠券");
                gift_mean_selected = 0;
                builder.setSingleChoiceItems(gift_means, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gift_mean_selected = i;
                        Log.d("GiftCouponMeans", "Selected:" + i);
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("gift", ""+gift_mean_selected);
                        succeed = 0;
                        share_code = null;
                        httpThread(gift_mean_selected, coupon);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
        return sale_my_frag;
    }

    /**
     * 初始化view
     */
    private void initView() {
        my_recv = (RecyclerView) sale_my_frag.findViewById(R.id.sale_my_recv);
        my_recv.setLayoutManager(new LinearLayoutManager(context));
        sale_my_ll = (FrameLayout) sale_my_frag.findViewById(R.id.sale_my_fragment_ll);
        textView1 = new TextView(context);
        textView1.setText("您还未登录哦\n" + "请您登录后进行查看!");
        textView1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.no_login, 0, 0);
        textView1.setCompoundDrawablePadding(30);
        textView1.setPadding(0, 500, 0, 0);
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setTextSize(14);
        textView1.setTextColor(getResources().getColor(R.color.gray1, null));
        swipeRefreshLayout = (SwipeRefreshLayout) sale_my_frag.findViewById(R.id.sale_my_swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.GreenTheme);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        //下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCouponData();
                    }
                }, 2000);
            }
        });
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
        swipeRefreshLayout.setRefreshing(false);
        MainApplication application = (MainApplication) getActivity().getApplication();
        uid = application.getUser_id();
        token = application.getToken();
        sale_my_ll.removeView(textView1);
        if (!application.isOnline()) {
            sale_my_ll.addView(textView1);
        } else {
            dataList.clear();
            coupon_id.clear();
            shop_id.clear();
            coupon_name.clear();
            coupon_price = "";
            coupon_content = "";
            coupon_type = 0;
            coupon_time = "";
            coupon_giftEnable = false;
            coupon_img.clear();
            seller_id.clear();
            coupon_send.clear();
            HashMap<String, String> params = new HashMap<>();
            params.put("userId", uid);
            params.put("token", token);
            RequestManager.getInstance(context).requestAsyn("users/me/coupons", RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<String>() {

                @Override
                public void onReqSuccess(String result) {
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
                            coupon_price = "¥ " + str[1];
                            coupon_content = "消费金额" + str[0] + "可用";
                        } else if (coupon_type == 2) {
                            coupon_price = jo.getString("coupon_name").substring(0, 2);
                            coupon_content = "全场商品" + coupon_price + "优惠";
                        }
                        String[] string = jo.getString("expire_at").split(" ");
                        coupon_time = string[0] + "到期";
                        coupon_img.add(jo.getString("image_url"));
                        coupon_status = jo.getInteger("status");
                        seller_id.add(jo.getString("seller_id"));

                        if(jo.getInteger("source") <= 2){
                            coupon_send.add("可赠送");
                            coupon_giftEnable = true;
                        } else {
                            coupon_send.add("");
                            coupon_giftEnable = false;
                        }
//                        coupon_send.add(jo.getString("source"));
                        SaleBean saleBean = new SaleBean();
                        if (coupon_status == 0) {
                            saleBean.setUseFlag(false);
                            saleBean.setText(coupon_name.get(i), coupon_price, coupon_content, coupon_time, coupon_img.get(i));
                            saleBean.setSend(coupon_send.get(i));
                            saleBean.setGiftEnable(coupon_giftEnable);
                            dataList.add(saleBean);
                        } else {
                            // todo
                            saleBean.setUseFlag(true);
                            saleBean.setText(coupon_name.get(i), coupon_price, coupon_content, coupon_time, coupon_img.get(i));
                            saleBean.setSend(coupon_send.get(i));
                            saleBean.setGiftEnable(coupon_giftEnable);
                            dataList.add(saleBean);
                        }
                    }
                    Log.d("AAA",dataList.toString());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e(TAG, errorMsg);
                    Toast.makeText(context, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private ProgressDialog pd;

    //定义Handler对象
    private Handler httpHandler = new Handler(new Handler.Callback() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public boolean handleMessage(Message msg) {
            //只要执行到这里就关闭对话框
            pd.dismiss();
            // TODO 收到请求
            switch (msg.what){
                case 1:showGiftPrivateResult();break;
                case 2:showGiftPublicResult();break;
            }
            return false;
        }
    });

    private void httpThread(final int gift_mean_selected, final String coupon_id) {
        //构建一个下载进度条
        pd = ProgressDialog.show(context, "赠送中", "赠送中，请稍候......");
        switch (gift_mean_selected){
            // 私人赠送
            case 0:new Thread() {
                @Override
                public void run() {
                    //在新线程里执行长耗时方法
                    giftCoupon(coupon_id, gift_mean_selected);
                    //执行完毕后给handler发送一个空消息
                    httpHandler.sendEmptyMessage(1);
                }
            }.start();break;
            // 公开赠送
            case 1:new Thread() {
                @Override
                public void run() {
                    //在新线程里执行长耗时方法
                    giftCoupon(coupon_id, gift_mean_selected);
                    //执行完毕后给handler发送一个空消息
                    httpHandler.sendEmptyMessage(2);
                }
            }.start();break;
        }
    }

    private void showGiftPrivateResult(){
        if(succeed==1 && share_code!=null){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.icon_coupon);
            builder.setTitle("赠送成功!");
            View view = LayoutInflater.from(context).inflate(R.layout.coupon_gift_code, null);
            EditText editText = view.findViewById(R.id.coupon_gift_copy);
            editText.setText(share_code);
            builder.setView(view);
            builder.setPositiveButton("点击复制", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(share_code);
                    Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                    Log.d("copy", "复制");
                }
            });
            builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();

        }else{
            Toast.makeText(context, "赠送失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGiftPublicResult(){
        if(succeed == 1){
            Toast.makeText(context, "赠送成功", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "赠送失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void giftCoupon(String coupon_id, int gift_mean_selected) {
        // 私人赠送
        try{
            if(gift_mean_selected == 0){
                MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                final Bundle mBundle = new Bundle();
                mBundle.putString("user_id", mainApplication.getUser_id());
                mBundle.putString("token", mainApplication.getToken());
                mBundle.putString("coupon_id", coupon_id);
                HttpUtil.sendGiftCouponPrivateOkHttpRequest(mBundle, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        // 处理返回数据
                        JSONObject rsp = JSON.parseObject(responseData);
                        if(rsp.containsKey("succeed") && rsp.getInteger("succeed") == 1){
                            succeed = 1;
                            share_code = rsp.getString("gifturl");
                        }
                        else{
                            succeed = 0;
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: ERROR!");
                        Toast.makeText(context, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                    }
                });
                Thread.sleep(1500);
            }
            // 公开赠送
            else{
                MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                final Bundle mBundle = new Bundle();
                mBundle.putString("user_id", mainApplication.getUser_id());
                mBundle.putString("token", mainApplication.getToken());
                mBundle.putString("coupon_id", coupon_id);
                mBundle.putString("lat", String.valueOf(mainApplication.getLongitude()));
                mBundle.putString("lng", String.valueOf(mainApplication.getLatitude()));
                HttpUtil.sendGiftCouponPublicOkHttpRequest(mBundle, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        // 处理返回数据
                        JSONObject rsp = JSON.parseObject(responseData);
                        if(rsp.containsKey("succeed") && rsp.getInteger("succeed") == 1)
                            succeed = 1;
                        else
                            succeed = 0;
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: ERROR!");
                        Toast.makeText(context, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                    }
                });
                Thread.sleep(1500);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
