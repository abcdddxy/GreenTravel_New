package com.example.zero.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;
import com.example.zero.adapter.AdvDestinMultiAdapter;
import com.example.zero.adapter.AdvDestinSearchAdapter;
import com.example.zero.bean.AdvDestinMultiBean;
import com.example.zero.bean.AdvDestinSearchBean;
import com.example.zero.bean.RouteSearchBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.RequestManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class StationDisplayActivity extends AppCompatActivity {

    private View spinnerSet;
    private TextView textView;
    private TextView spinner1;
    private TextView spinner2;
    private List<String> choice1;
    private ArrayList<AdvDestinMultiBean> choice2;
    private ArrayAdapter<String> choiceAdapter1;
    private AdvDestinMultiAdapter choiceAdapter2;
    private RecyclerView adv_recv;
    private List<AdvDestinSearchBean> dataList = new ArrayList<>();
    private List<AdvDestinSearchBean> showList = new ArrayList<>();
    private ListPopupWindow popupWindow1;
    private ListPopupWindow popupWindow2;

    //前后端接口
    private String stationName;
    private int size = 100;
    private String[] shopIdList = new String[size];
    private double[] sellerLatList = new double[size];
    private double[] sellerLngList = new double[size];
    private String[] tagList = new String[size];
    private String[] posterList = new String[size];
    private String[] shopNameList = new String[size];
    private double[] starList = new double[size];
    private boolean[] hasCouponList = new boolean[size];

    double lat;
    double lng;
    private static double EARTH_RADIUS = 6371.393;

    private ImageView shopImg;

    private int stationShopCount = 0;

    private Context context;

    private static final String TAG = "StationDisplayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_display);
        context = getBaseContext();
        Intent intent = getIntent();
        stationName = intent.getStringExtra("stationName");

        final Bundle mBundle = new Bundle();
        mBundle.putString("userId", "guest");
        mBundle.putString("stationName", stationName);
        // TODO: 2017/10/29  前后端联调
        HttpUtil.sendStationDisplayOkHttpRequest(mBundle, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJSONWithJSONObject(responseData);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ERROR!");
                Toast.makeText(context, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject sellers = jsonObject.getJSONObject("seller");
            JSONArray shop = sellers.getJSONArray(stationName);

            int count = 0;
            for (int i = 0; i < shop.length(); i++) {
                shopIdList[count] = shop.getJSONObject(i).getString("shop_id");
                sellerLatList[count] = shop.getJSONObject(i).getDouble("lat");
                sellerLngList[count] = shop.getJSONObject(i).getDouble("lng");
                tagList[count] = shop.getJSONObject(i).getString("tag");
                posterList[count] = shop.getJSONObject(i).getString("poster_url");
                shopNameList[count] = shop.getJSONObject(i).getString("shop_name");
                starList[count] = shop.getJSONObject(i).getDouble("star_num");
                hasCouponList[count] = shop.getJSONObject(i).getBoolean("doesHaveCoupon");
                count++;
            }
            stationShopCount = count;
            getLatlng();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        textView = (TextView) this.findViewById(R.id.station_name);
        textView.setText("现在为" + stationName + "站");
        adv_recv = (RecyclerView) this.findViewById(R.id.station_search_recv);
        adv_recv.setLayoutManager(new LinearLayoutManager(context));

        shopImg = (ImageView) this.findViewById(R.id.adv_destin_store_img);

        spinnerSet = (View) this.findViewById(R.id.station_spinner_set);
        spinner1 = (TextView) this.findViewById(R.id.station_spinner1);
        spinner2 = (TextView) this.findViewById(R.id.station_spinner2);

        DisplayMetrics dm = new DisplayMetrics();// 获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        popupWindow1 = new ListPopupWindow(this);
        popupWindow1.setAnchorView(spinnerSet);
        popupWindow1.setVerticalOffset(1);
        popupWindow1.setWidth(screenWidth);
        popupWindow1.setHeight(500);
        popupWindow1.setModal(true);
        spinner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow1.show();
            }
        });
        popupWindow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //TODO 排序更新
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                spinner1.setText(choice1.get(i));
                popupWindow1.dismiss();
            }
        });

        popupWindow2 = new ListPopupWindow(this);
        popupWindow2.setAnchorView(spinnerSet);
        popupWindow2.setVerticalOffset(1);
        popupWindow2.setWidth(screenWidth);
        popupWindow2.setHeight(500);
        popupWindow2.setModal(true);
        spinner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.show();
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //TODO 多标签搜索更新
                String s = "";
                for (int i = 0; i < 3; i++) {
                    LabelsView labelsView = popupWindow2.getListView().getChildAt(i).findViewById(R.id.labelsViewCheck);
                    choice2.get(i).setSelected(labelsView.getSelectLabels());
                    s = s + choice2.get(i).getLabels();
                }
                Toast.makeText(getBaseContext(), "dismiss" + s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getLatlng() {
        HashMap<String, String> params = new HashMap<>();
        params.put("address", "广州" + stationName + "地铁站");
        params.put("output", "json");
        params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
        params.put("callback", "0");
        RequestManager.getInstance(context).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
                RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
                    @Override
                    public void onReqSuccess(String result) {
                        try {
                            JSONObject jsonData = new JSONObject(result);

                            JSONObject res = jsonData.getJSONObject("result");
                            JSONObject location = res.getJSONObject("location");

                            lat = location.getDouble("lat");
                            lng = location.getDouble("lng");

                            initView();
                            initData();
                            showData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Toast.makeText(context, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 1000);
        return s;
    }

    private void showData() {
        ArrayList labels = new ArrayList<String>();
        labels.add("快捷");
        labels.add("便宜");
        labels.add("凯德广场");
        labels.add("学生党");
        for (int i = 0; i < stationShopCount; i++) {
            // TODO: 2017/11/7 均价、评论数量、关键词（label）
            double distance = GetDistance(lat, lng, sellerLatList[i], sellerLngList[i]);
            DecimalFormat df = new DecimalFormat(".##");
            AdvDestinSearchBean searchBean = new AdvDestinSearchBean();
            searchBean.setText(false, tagList[i], shopNameList[i], stationName, "123456789", posterList[i],
                    999, 50, (float)distance, (float) starList[i], labels);
            showList.add(searchBean);
        }

        final AdvDestinSearchAdapter adapter1 = new AdvDestinSearchAdapter(this, showList);
        adv_recv.setAdapter(adapter1);
        adapter1.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(context, "1" + shopNameList[position], Toast.LENGTH_SHORT).show();
                Bundle mBundle = new Bundle();
                Intent intent = new Intent(context, ShoppingCartActivity.class);
                mBundle.putString("shopName", shopNameList[position]);
                mBundle.putString("shopImg", posterList[position]);
                intent.putExtras(mBundle);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(context, "2" + shopNameList[position], Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void initData() {
        choice1 = Arrays.asList(getResources().getStringArray(R.array.prefers));
        choice2 = new ArrayList<>();
        ArrayList<String> sub1 = new ArrayList<>();
        sub1.add("预定");
        sub1.add("排队");
        sub1.add("点菜");
        sub1.add("外卖");
        ArrayList<String> sub2 = new ArrayList<>();
        sub2.add("50以下");
        sub2.add("50-100");
        sub2.add("100-300");
        sub2.add("300以上");
        ArrayList<String> sub3 = new ArrayList<>();
        sub3.add("新店");
        sub3.add("WiFi");
        sub3.add("团购");
        sub3.add("营业中");
        choice2.add(new AdvDestinMultiBean("价格", true, new ArrayList<Integer>(), sub2));
        choice2.add(new AdvDestinMultiBean("服务", false, new ArrayList<Integer>(), sub1));
        choice2.add(new AdvDestinMultiBean("更多", false, new ArrayList<Integer>(), sub3));
        choiceAdapter1 = new ArrayAdapter<>(this, R.layout.adv_pop_item, R.id.textViewPop, choice1);
        choiceAdapter2 = new AdvDestinMultiAdapter(choice2, this);
        popupWindow1.setAdapter(choiceAdapter1);
        popupWindow2.setAdapter(choiceAdapter2);
    }
}
