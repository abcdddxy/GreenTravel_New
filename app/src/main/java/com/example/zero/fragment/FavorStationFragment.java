package com.example.zero.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.zero.adapter.FavorItemAdapter;
import com.example.zero.bean.FavorItemBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jojo on 2017/11/3.
 */

public class FavorStationFragment extends Fragment {

    private String TAG = "FavorStationFragment";
    private View sta_frag;
    private Context context;
    private RecyclerView sta_recy;
    private List<FavorItemBean> dataList = new ArrayList<>();
    private String uid;
    private String name, content;
    private float lat, lng;
    private FavorItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sta_frag = inflater.inflate(R.layout.fragment_sta_collect, container, false);
        context = sta_frag.getContext();
        innitView();
        MainApplication application = (MainApplication) getActivity().getApplication();
        uid = application.getUser_id();
        //getCollection(0);
        FavorItemBean bean = new FavorItemBean();
        bean.setText("北京西站（公交站）", "出行：21路，387路，694路");
        bean.setType(FavorItemBean.ITEM);
        FavorItemBean beanB = new FavorItemBean();
        beanB.setType(FavorItemBean.BUTTON);
        dataList.add(bean);
        dataList.add(beanB);
        adapter = new FavorItemAdapter(context, dataList);
        sta_recy.setAdapter(adapter);
        //sta_recy.addItemDecoration(new RecycleItemDecoration(context, RecycleItemDecoration.VERTICAL_LIST));
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //TODO:根据收藏站点经纬度在地图上定位显示
                Toast.makeText(getContext(), "click" + position, Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return sta_frag;
    }

    private void innitView() {
        sta_recy = (RecyclerView) sta_frag.findViewById(R.id.sta_collect);
        sta_recy.setLayoutManager(new LinearLayoutManager(context));
    }

    private String getAddress(float lat, float lng) {
        GeoCoder mSearch = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                ReverseGeoCodeResult.AddressComponent addressDetail = reverseGeoCodeResult.getAddressDetail();
                //countryName：国家、province：省、city：市、district：区、street：街、streetNumber：街号
                content = addressDetail.province + addressDetail.city + addressDetail.district
                        + addressDetail.street + addressDetail.streetNumber;
            }
        };
        mSearch.setOnGetGeoCodeResultListener(listener);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(lat, lng)));
        mSearch.destroy();
        return content;
    }

    private void getCollection(int type) {
        if (uid == null) {
            Toast.makeText(context, "请您先登录再进行操作", Toast.LENGTH_SHORT).show();
            FavorItemBean favorItemBean = new FavorItemBean();
            favorItemBean.setType("Button");
            dataList.add(favorItemBean);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", type + "");
            params.put("user_id", uid);
            params.put("offset", 0 + "");
            params.put("count", 10 + "");
            RequestManager.getInstance(context).requestAsyn("users/get_collect_items", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                @Override
                public void onReqSuccess(String result) {
                    dataList.clear();
                    FavorItemBean favorItemBean = new FavorItemBean();
                    JSONArray array = JSONArray.parseArray(result);
                    for (int i = 0; i < array.size(); i++) {
                        String s = array.get(i).toString();
                        JSONObject jo = JSON.parseObject(s);
                        name = jo.getString("station_name");
                        lng = jo.getFloat("lng");
                        lat = jo.getFloat("lat");
                        getAddress(lat, lng);
                        favorItemBean.setText(name, content);
                        favorItemBean.setType("Item");
                        dataList.add(favorItemBean);
                    }
                    favorItemBean.setType("Button");
                    dataList.add(favorItemBean);
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e(TAG, errorMsg);
                    Toast.makeText(context, "收藏站点内容请求失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
