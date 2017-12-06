package com.example.zero.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.zero.adapter.SaleHotCouponAdapter;
import com.example.zero.bean.SaleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jojo on 2017/9/13.
 */

public class SaleHotFragment extends Fragment {

    private String TAG = "SaleHotFragment";
    private View sale_hot_frag;
    private RecyclerView hot_recv;
    private List<SaleBean> dataList = new ArrayList<>();
    private List<SaleBean> dataList1 = new ArrayList<>();
    private List<SaleBean> dataList2 = new ArrayList<>();
    private List<SaleBean> dataList3 = new ArrayList<>();
    private List<SaleBean> dataList4 = new ArrayList<>();
    private SaleHotCouponAdapter adapter;
    private double longitude, latitude;
    private LocationClient mLocationClient;
    private BDLocationListener bdLocationListener = new MyLocationListener();
    private String uid, token;
    private Context context;
    private ArrayList<String> coupon_id = new ArrayList<>();
    private String coupon_name;
    private int coupon_type;
    private String shop_type;
    private String coupon_price;
    private String coupon_content;
    private String coupon_time;
    private String coupon_img;
    private RadioGroup saleHotGroup;
    private RadioButton saleHotFood, saleHotEntertainment, saleHotShopping, saleHotTravel;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoading;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        sale_hot_frag = inflater.inflate(R.layout.fragment_sale_hot, container, false);
        context = sale_hot_frag.getContext();
        initView();
        getCouponData();
        adapter = new SaleHotCouponAdapter(context, dataList);
        hot_recv.setAdapter(adapter);
        adapter.setOnItemClickListener(new SaleHotCouponAdapter.onRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(context, "点击条目 " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBtnClick(View view, final int position) {
                MainApplication application = (MainApplication) getActivity().getApplication();
                uid = application.getUser_id();
                token = application.getToken();
                if (!application.isOnline()) {
                    Toast.makeText(context, "请您先登录再进行操作", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("couponId", coupon_id.get(position));
                    params.put("userId", uid);
                    params.put("token", token);
                    RequestManager.getInstance(context).requestAsyn("users/me/coupons/" + coupon_id.get(position), RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {

                        @Override
                        public void onReqSuccess(String result) {
                            JSONObject jo = JSON.parseObject(result);
                            Toast.makeText(context, jo.getString("userMessage"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(context, "领取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return sale_hot_frag;
    }

    private void initView() {
        hot_recv = (RecyclerView) sale_hot_frag.findViewById(R.id.sale_hot_recv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        hot_recv.setLayoutManager(layoutManager);
        saleHotFood = (RadioButton) sale_hot_frag.findViewById(R.id.sale_hot_food);
        saleHotEntertainment = (RadioButton) sale_hot_frag.findViewById(R.id.sale_hot_entertainment);
        saleHotShopping = (RadioButton) sale_hot_frag.findViewById(R.id.sale_hot_shopping);
        saleHotTravel = (RadioButton) sale_hot_frag.findViewById(R.id.sale_hot_travel);
        swipeRefreshLayout = (SwipeRefreshLayout) sale_hot_frag.findViewById(R.id.sale_hot_swipeRefresh);
        saleHotFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food_checked, 0, 0, 0);
        saleHotEntertainment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.entertainment, 0, 0, 0);
        saleHotShopping.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping, 0, 0, 0);
        saleHotTravel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.travel, 0, 0, 0);
        saleHotGroup = (RadioGroup) sale_hot_frag.findViewById(R.id.sale_hot_rg);
        saleHotGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sale_hot_food:
                        saleHotFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food_checked, 0, 0, 0);//选中时为红色
                        saleHotEntertainment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.entertainment, 0, 0, 0);//未选中的为灰色
                        saleHotShopping.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping, 0, 0, 0);
                        saleHotTravel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.travel, 0, 0, 0);
                        showCouponData(0);
                        break;
                    case R.id.sale_hot_entertainment:
                        saleHotFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food, 0, 0, 0);
                        saleHotEntertainment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.entertainment_checked, 0, 0, 0);
                        saleHotShopping.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping, 0, 0, 0);
                        saleHotTravel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.travel, 0, 0, 0);
                        showCouponData(1);
                        break;
                    case R.id.sale_hot_shopping:
                        saleHotFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food, 0, 0, 0);
                        saleHotEntertainment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.entertainment, 0, 0, 0);
                        saleHotShopping.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping_checked, 0, 0, 0);
                        saleHotTravel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.travel, 0, 0, 0);
                        showCouponData(2);
                        break;
                    case R.id.sale_hot_travel:
                        saleHotFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food, 0, 0, 0);
                        saleHotEntertainment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.entertainment, 0, 0, 0);
                        saleHotShopping.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping, 0, 0, 0);
                        saleHotTravel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.travel_checked, 0, 0, 0);
                        showCouponData(3);
                    default:
                        break;
                }
                adapter.notifyDataSetChanged();
            }
        });

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
                        coupon_id.clear();
                        dataList.clear();
                        getCouponData();
                    }
                }, 2000);
            }
        });
        //上拉加载监听
//        hot_recv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            //当RecyclerView的滑动状态改变时触发
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
//                    Log.d("test", "loading executed");
//                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
//                    if (isRefreshing) {
//                        adapter.notifyItemRemoved(adapter.getItemCount());
//                        return;
//                    }
//                    if (!isLoading) {
//                        isLoading = true;
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getCouponData();
//                                Log.d("test", "load more completed");
//                                isLoading = false;
//                            }
//                        }, 1000);
//                    }
//                }
//            }
//        });
    }

    public void isLocationOpen() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(context, "PERMISSION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getCouponData() {
        isLocationOpen();
        mLocationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        mLocationClient.registerLocationListener(bdLocationListener);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            try {
                swipeRefreshLayout.setRefreshing(false);
                if (bdLocation != null) {
                    latitude = bdLocation.getLatitude();
                    longitude = bdLocation.getLongitude();
                    if (mLocationClient.isStarted()) {
                        mLocationClient.stop();
                    }
                }
                HashMap<String, String> params = new HashMap<>();
//            Toast.makeText(context, "纬度" + latitude, Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "经度" + longitude, Toast.LENGTH_SHORT).show();
                params.put("longitude", "" + 113.332205);
                params.put("latitude", "" + 23.156263);
//            params.put("longitude", "" + longitude);
//            params.put("latitude", "" + latitude);
                RequestManager.getInstance(context).requestAsyn("users/me/coupons/near", RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<String>() {

                    @Override
                    public void onReqSuccess(String result) {
                        coupon_id.clear();
                        dataList.clear();
                        dataList1.clear();
                        dataList2.clear();
                        dataList3.clear();
                        dataList4.clear();
                        JSONArray array = JSONArray.parseArray(result);
                        for (int i = 0; i < array.size(); i++) {
                            String s = array.get(i).toString();
                            JSONObject jo = JSON.parseObject(s);
                            coupon_id.add(jo.getString("id"));
                            coupon_name = jo.getString("shop_name");
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
                            coupon_img = jo.getString("image_url");
                            shop_type = jo.getString("shop_tag");
                            SaleBean saleBean = new SaleBean();
                            saleBean.setText(coupon_name, coupon_price, coupon_content, coupon_time, coupon_img);
                            switch (shop_type) {
                                case "餐饮":
                                    dataList1.add(saleBean);
                                    break;
                                case "娱乐":
                                    dataList2.add(saleBean);
                                    break;
                                case "购物":
                                    dataList3.add(saleBean);
                                    break;
                                case "旅游":
                                    dataList4.add(saleBean);
                                    break;
                                default:
                                    break;
                            }
                        }
                        showCouponData(0);
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Log.e(TAG, errorMsg);
                        Toast.makeText(context, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                });
                Thread.sleep(1000);//todo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void showCouponData(int type) {
        dataList.clear();
        switch (type) {
            case 0:
                dataList.addAll(dataList1);
                break;
            case 1:
                dataList.addAll(dataList2);
                break;
            case 2:
                dataList.addAll(dataList3);
                break;
            case 3:
                dataList.addAll(dataList4);
                break;
            default:
                break;
        }
    }
}


