package com.example.zero.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.zero.adapter.AdvDestinSearchAdapter;
import com.example.zero.bean.AdvDestinSearchBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kazu_0122 on 2017/9/22.
 */

public class AdvSearchActivity extends AppCompatActivity {
    private String keyword;
    private String station;
    private View spinnerSet;
    private TextView spinner1;
    private TextView spinner2;
    private TextView spinner3;
    private int[] choice;
    private List<String> choice1;
    private List<String> choice2;
    private List<String> choice3;
    private ArrayAdapter<String> choiceAdapter1;
    private ArrayAdapter<String> choiceAdapter2;
    private ArrayAdapter<String> choiceAdapter3;

    private RecyclerView adv_recv;
    private Map<String, ArrayList<AdvDestinSearchBean>> ssMap = new LinkedHashMap<String, ArrayList<AdvDestinSearchBean>>();
    private List<AdvDestinSearchBean> showList = new ArrayList<>();
    private Map<String, Boolean> toggleState = new HashMap<>();
    private AdvDestinSearchAdapter adapter;
    private ListPopupWindow popupWindow1;
    private ListPopupWindow popupWindow2;
    private ListPopupWindow popupWindow3;
    private Context context;

    private static final String TAG = "AdvSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getBaseContext();
        setContentView(R.layout.activity_adv_search);
        initView();
        initChoiceData();
        showData();
        onRefreshData();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initView() {
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keywords");
        station = intent.getStringExtra("station");
        if(keyword.equals("")) {
            keyword = "火锅";
        }
        Log.d(TAG,station+"-"+keyword);
        adv_recv = (RecyclerView) this.findViewById(R.id.adv_search_recv);
        adv_recv.setLayoutManager(new LinearLayoutManager(context));

        spinnerSet = (View) this.findViewById(R.id.adv_spinner_set);
        spinner1 = (TextView) this.findViewById(R.id.adv_spinner1);
        spinner2 = (TextView) this.findViewById(R.id.adv_spinner2);
        spinner3 = (TextView) this.findViewById(R.id.adv_spinner3);
        choice = new int[3];
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        popupWindow1 = new ListPopupWindow(this);
        popupWindow1.setAnchorView(spinner1);
        popupWindow1.setVerticalOffset(1);
        popupWindow1.setWidth(screenWidth/3);
        popupWindow1.setHeight(600);
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
                choice[0] = i;
                spinner1.setText(choice1.get(i));
                popupWindow1.dismiss();
            }
        });

        popupWindow2 = new ListPopupWindow(this);
        popupWindow2.setAnchorView(spinner2);
        popupWindow2.setVerticalOffset(1);
        popupWindow2.setWidth(screenWidth/3);
        popupWindow2.setHeight(600);
        popupWindow2.setModal(true);
        spinner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.show();
            }
        });
        popupWindow2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //TODO 排序更新
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice[1] = i;
                spinner2.setText(choice2.get(i));
                popupWindow2.dismiss();
            }
        });

        popupWindow3 = new ListPopupWindow(this);
        popupWindow3.setAnchorView(spinner3);
        popupWindow3.setVerticalOffset(1);
        popupWindow3.setWidth(screenWidth/3);
        popupWindow3.setHeight(600);
        popupWindow3.setModal(true);
        spinner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow3.show();
            }
        });
        popupWindow3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice[2] = i;
                spinner3.setText(choice3.get(i));
                popupWindow3.dismiss();
            }
        });
    }

    private void showData() {
        adapter = new AdvDestinSearchAdapter(this, showList);
        adv_recv.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (showList.get(position).isStation()) {
                    String s = showList.get(position).getStationTag();
                    toggleState.put(s, !toggleState.get(s));
                    showList.clear();
                    for (Map.Entry<String, ArrayList<AdvDestinSearchBean>> entry : ssMap.entrySet()) {
                        Boolean toggle = toggleState.get(entry.getKey());
                        AdvDestinSearchBean station = entry.getValue().get(0);
                        station.setToggle(toggle);
                        if (toggle) {
                            showList.addAll(entry.getValue());
                        } else {
                            showList.add(station);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                else if(showList.get(position).getSellerId().equals("")) {
                    Intent intent = new Intent();
                    intent.setClass(getBaseContext(),ShoppingCartActivity.class);
                    intent.putExtra("shopId",showList.get(position).getShopId());
                    intent.putExtra("sellerId",showList.get(position).getSellerId());
                    intent.putExtra("shopImg",showList.get(position).getImg());
                    intent.putExtra("shopName",showList.get(position).getTitle());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getBaseContext(),"缺少sellerId",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void initChoiceData() {
        choice1 = Arrays.asList(getResources().getStringArray(R.array.prefers));
        choice2 = Arrays.asList(getResources().getStringArray(R.array.prices));
        choice3 = Arrays.asList(getResources().getStringArray(R.array.distances));
        choiceAdapter1 = new ArrayAdapter<>(this, R.layout.adv_pop_item, R.id.textViewPop, choice1);
        choiceAdapter2 = new ArrayAdapter<>(this, R.layout.adv_pop_item, R.id.textViewPop, choice2);
        choiceAdapter3 = new ArrayAdapter<>(this, R.layout.adv_pop_item, R.id.textViewPop, choice3);
        popupWindow1.setAdapter(choiceAdapter1);
        popupWindow2.setAdapter(choiceAdapter2);
        popupWindow3.setAdapter(choiceAdapter3);
    }

    private String getSortWay() {
        switch(choice[0]){
            case 1:return "distance";
            case 2:return "comment";
            case 3:return "price";
            default:return "intelli";
        }
    }

    private String getDistanceFilters() {
        switch(choice[0]){
            case 1:return "5000";
            case 2:return "10000";
            case 3:return "15000";
            default:return "0";
        }
    }

    private String getPriceFilters() {
        switch(choice[0]){
            case 1:return "50";
            case 2:return "100";
            case 3:return "150";
            default:return "0";
        }
    }

    private void onRefreshData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_query", keyword);
        params.put("search_type", getSortWay());
        params.put("dis_filter", getDistanceFilters());
        params.put("price_filter", getPriceFilters());
        params.put("sub_station", station);
        RequestManager.getInstance(getBaseContext()).requestAsyn("search/keywordSearch", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                showList.clear();
                LinkedHashMap<String, String> jsonMap = JSON.parseObject(result, new TypeReference<LinkedHashMap<String, String>>() {
                });
                for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                    JSONArray stores = JSON.parseArray(entry.getValue());
                    ArrayList<AdvDestinSearchBean> arrayList = new ArrayList<>();
                    AdvDestinSearchBean station = new AdvDestinSearchBean();
                    station.setText(entry.getKey(),stores.size());
                    arrayList.add(station);
                    showList.add(station);
                    for (int i = 0; i < stores.size(); i++) {
                        JSONObject store = stores.getJSONObject(i);
                        AdvDestinSearchBean bean = new AdvDestinSearchBean();
                        bean.setText(false,
                                entry.getKey(),
                                store.getString("shop_id"),
                                store.getString("seller_id"),
                                store.getString("shop_name"),
                                store.getString("addr"),
                                store.getString("phone"),
                                store.getString("img_url"),
                                store.getInteger("comment_num"),
                                store.getInteger("ave_price"),
                                store.getFloat("distance"),
                                store.getFloat("star_num"),
                                new ArrayList<String>());
                        arrayList.add(bean);
                    }
                    ssMap.put(entry.getKey(),arrayList);
                    toggleState.put(entry.getKey(),false);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e(TAG,errorMsg);
            }
        });
    }

}