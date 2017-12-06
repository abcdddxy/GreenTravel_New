package com.example.zero.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.donkingliang.labels.LabelsView;

import com.example.zero.activity.AdvSearchActivity;
import com.example.zero.activity.RouteResultActivity;
import com.example.zero.adapter.RouteSearchAdapter;
import com.example.zero.bean.RouteSearchBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SearchPopView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.zero.fragment.RouteFragmentDouble.Origin.DATA;

/**
 * Created by shuwan122 on 2017/9/7.
 * 目的地搜索fragment
 */

public class AdvDestinFragment extends Fragment implements SearchPopView.SearchPopViewListener {

    private EditText editText;
    private LabelsView labelsView;
    private ArrayList<String> labels;
    private Button searchButton;


    /**
     * 搜索结果列表view
     */
    private ListView lvResults;
    /**
     * 搜索view
     */
    private SearchPopView searchPopView;
    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<String> hintAdapter;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter;
    /**
     * 搜索结果列表adapter
     */
    private RouteSearchAdapter resultAdapter;
    /**
     * 数据库数据，总数据
     */
    private List<RouteSearchBean> dbData;
    /**
     * 热搜版数据
     */
    private List<String> hintData;
    /**
     * 搜索过程中自动补全数据
     */
    private List<String> autoCompleteData;
    /**
     * 搜索结果的数据
     */
    private List<RouteSearchBean> resultData;

    /**
     * 默认提示框显示项的个数
     */
    private static int DEFAULT_HINT_SIZE = 4;
    /**
     * 提示框显示项的个数
     */
    private static int hintSize = DEFAULT_HINT_SIZE;

    MainApplication application;

    private RouteFragmentDouble.Origin origin;

    private Context context;

    final private String[] stationDe = {"广州塔", "广州火车站"};

    private static final String TAG = "AdvDestinFragment";

    /**
     * 设置提示框显示项的个数
     *
     * @param hintSize 提示框显示个数
     */
    public static void setHintSize(int hintSize) {
        AdvDestinFragment.hintSize = hintSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adv_destin, container, false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        application = (MainApplication) getActivity().getApplication();
        initData();
    }


    /**
     * 初始化视图
     */
    private void initViews() {
        editText = getView().findViewById(R.id.editText);
        //使用了网上的labelView
        labelsView = getView().findViewById(R.id.labels);
        labels = getLabels();
        labelsView.setLabels(labels);
        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(View label, String labelText, int position) {
                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
                ArrayList<Integer> selected = labelsView.getSelectLabels();
                String s = "";
                for (Integer i : selected) {
                    s = s + labels.get(i) + " ";
                }
                editText.setText(s);
            }
        });

        searchPopView = (SearchPopView) getActivity().findViewById(R.id.destin_search);
        searchPopView.setAutoCompleteAdapter(autoCompleteAdapter);
        searchPopView.setTipsHintAdapter(hintAdapter);
        searchPopView.setHintText("请输入站点名称");
        searchPopView.setSearchPopViewListener(this);

        lvResults = (ListView) getActivity().findViewById(R.id.destin_search_results);
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), "1-" + position + "", Toast.LENGTH_SHORT).show();
            }
        });

        searchButton = (Button) getView().findViewById(R.id.destin_search_commit);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AdvSearchActivity.class);
                //TODO 具体所需搜索信息
                String data = searchPopView.getText() + editText.getText().toString();
                intent.putExtra("position",searchPopView.getText());
                intent.putExtra("keywords",editText.getText().toString());
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (application.getStationList() == null) {
            origin = DATA;
            parseJSONWithJSONObject(null);
        } else {
            dbData = application.getStationList();
            hintData = application.getBusyStationList();
        }
    }

    private void parseJSONWithJSONObject(String jsonData) {
        switch (origin) {
            case DATA:
                HashMap<String, String> params = new HashMap<>();
                params.put("userId", "guest");
                RequestManager.getInstance(context).requestAsyn("http://10.108.120.225:8080/route/station",
                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
                            @Override
                            public void onReqSuccess(String result) {
                                com.alibaba.fastjson.JSONObject jsonData = JSON.parseObject(result);

                                com.alibaba.fastjson.JSONArray station = jsonData.getJSONArray("station");
                                com.alibaba.fastjson.JSONArray busy = jsonData.getJSONArray("busy");

                                dbData = new ArrayList<>(station.size());
                                for (int i = 0; i < station.size(); i++) {
                                    dbData.add(new RouteSearchBean(R.drawable.title_icon, station.getString(i),
                                            "周围简介\n热门吃、喝、玩、乐", ""));
                                }
                                application.setStationList(dbData);
                                Log.d(TAG, "parseJSONWithJSONObject: " + dbData.size());

                                setHintSize(busy.size());
                                hintData = new ArrayList<>(hintSize);
                                for (int i = 0; i < hintSize; i++) {
                                    hintData.add(busy.getString(i));
                                }
                                application.setBusyStationList(hintData);

                                hintAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hintData);
                                //初始化自动补全数据
                                getAutoCompleteData(null);
                                //初始化搜索结果数据
                                getResultData(null);

                                initViews();
                                Log.d(TAG, "parseJSONWithJSONObject: " + hintData.size());
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {
                                dbData = new ArrayList<>();
                                for (int i = 0; i < stationDe.length; i++) {
                                    dbData.add(new RouteSearchBean(R.drawable.title_icon, stationDe[i],
                                            "周围简介\n热门吃、喝、玩、乐", ""));
                                }
                                Log.d(TAG, "parseJSONWithJSONObject: " + dbData.size());

                                hintData = new ArrayList<>();
                                hintData.add("广州塔");
                                hintData.add("体育西路");
                                hintData.add("广州火车站");
                                hintAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hintData);
                                //初始化自动补全数据
                                getAutoCompleteData(null);
                                //初始化搜索结果数据
                                getResultData(null);
                                initViews();
                                Toast.makeText(getContext(), "站点List请求失败,使用默认列表页。", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            default:
                break;
        }
    }

    /**
     * 获取自动补全data和adapter
     */
    private void getAutoCompleteData(String text) {
        if (autoCompleteData == null) {
            //初始化
            autoCompleteData = new ArrayList<>(hintSize);
        } else {
            // 根据text获取autodata
            autoCompleteData.clear();
            for (int i = 0, count = 0; i < dbData.size()
                    && count < hintSize; i++) {
                if (dbData.get(i).getTitle().contains(text.trim())) {
                    autoCompleteData.add(dbData.get(i).getTitle());
                    count++;
                }
            }
        }
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData);
        } else {
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (resultData == null) {
            // 初始化
            resultData = new ArrayList<>();
        } else {
            resultData.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().equals(text.trim())) {
                    resultData.add(dbData.get(i));
                }
            }
        }
        if (resultAdapter == null) {
            resultAdapter = new RouteSearchAdapter(getActivity(), resultData, R.layout.route_search_item_list);
        } else {
            resultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 当搜索框文本改变时触发的回调 ,更新自动补全数据
     *
     * @param text
     */
    @Override
    public void onRefreshAutoComplete(String text) {
        //更新数据
        getAutoCompleteData(text);
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text
     */
    @Override
    public void onSearch(String text) {
        //更新result数据
        getResultData(text);
        lvResults.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        resultAdapter.getItem(0).setComments("");
        if (lvResults.getAdapter() == null) {
            //获取搜索数据 设置适配器
            lvResults.setAdapter(resultAdapter);
        } else {
            //更新搜索数据
            resultAdapter.notifyDataSetChanged();
        }
        Toast.makeText(getActivity(), "完成搜素", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBack() {
        if (searchPopView.getText().equals("")) {
            lvResults.setVisibility(View.GONE);
            autoCompleteAdapter.notifyDataSetChanged();
            resultAdapter.notifyDataSetChanged();
        }
        hintAdapter.notifyDataSetChanged();
    }

    /**
     * 自动补全提示框出现触发回调
     */
    @Override
    public void isFocus() {
        if (searchPopView.hasFocus()) {
            lvResults.setVisibility(View.GONE);
            autoCompleteAdapter.notifyDataSetChanged();
            resultAdapter.notifyDataSetChanged();
        }
        hintAdapter.notifyDataSetChanged();
    }

    /**
     * 获取当前热门关键字
     * @return
     */
    public ArrayList<String> getLabels() {
        labels = new ArrayList<String>();
        labels.add("烤鸭");
        labels.add("日料");
        labels.add("凯德广场");
        labels.add("万达");
        labels.add("汉堡王");
        labels.add("螺蛳粉");
        labels.add("火锅");
        return labels;
    }

    @Override
    public boolean onHintClick(String text) {
        return false;
    }
}
