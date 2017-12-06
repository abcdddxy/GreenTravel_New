package com.example.zero.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zero.activity.AdvMapResultActivity;
import com.example.zero.activity.AdvSearchActivity;
import com.example.zero.activity.RouteResultActivity;
import com.example.zero.adapter.RouteSearchAdapter;
import com.example.zero.bean.RouteSearchBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SearchPopView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.zero.fragment.RouteFragmentDouble.Origin.ADVICE;
import static com.example.zero.fragment.RouteFragmentDouble.Origin.DATA;
import static com.example.zero.fragment.RouteFragmentDouble.Origin.SINGLE;
import static com.example.zero.greentravel_new.R.id.editText;

/**
 * Created by shuwan122 on 2017/9/7.
 * 出行规划fragment
 */

public class AdvPlanFragment extends Fragment implements SearchPopView.SearchPopViewListener {

    private TextView textView;
    private boolean seted;
    private int hour1;
    private int minute1;
    private int hour2;
    private int minute2;
    private Button searchButton;
    private Button btnChange;

    /**
     * 搜索结果列表view
     */
    private ListView lvResults1;
    private ListView lvResults2;
    /**
     * 搜索view
     */
    private SearchPopView searchPopView1;
    private SearchPopView searchPopView2;
    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<String> hintAdapter;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter1;
    private ArrayAdapter<String> autoCompleteAdapter2;
    /**
     * 搜索结果列表adapter
     */
    private RouteSearchAdapter resultAdapter1;
    private RouteSearchAdapter resultAdapter2;
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
    private List<String> autoCompleteData1;
    private List<String> autoCompleteData2;
    /**
     * 搜索结果的数据
     */
    private List<RouteSearchBean> resultData1;
    private List<RouteSearchBean> resultData2;

    /**
     * 默认提示框显示项的个数
     */
    private static int DEFAULT_HINT_SIZE = 4;
    /**
     * 提示框显示项的个数
     */
    private static int hintSize = DEFAULT_HINT_SIZE;

    private static final String TAG = "AdvPlanFragment";

    private RouteFragmentDouble.Origin origin;

    MainApplication application;

    final private String[] stationDe = {"广州塔", "广州火车站"};

    private Context context;

    /**
     * 设置提示框显示项的个数
     *
     * @param hintSize 提示框显示个数
     */
    public static void setHintSize(int hintSize) {
        AdvPlanFragment.hintSize = hintSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        seted = false;
        View view = inflater.inflate(R.layout.fragment_adv_plan, container, false);
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
        textView = (TextView) getView().findViewById(R.id.plan_search_time);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mCalendar = Calendar.getInstance();
                showTimeDialog();
            }
        });

        searchPopView1 = (SearchPopView) getActivity().findViewById(R.id.plan_search_start);
        searchPopView1.setAutoCompleteAdapter(autoCompleteAdapter1);
        searchPopView1.setTipsHintAdapter(hintAdapter);
        searchPopView1.setHintText("请输入起点");
        searchPopView1.setSearchPopViewListener(this);

        searchPopView2 = (SearchPopView) getActivity().findViewById(R.id.plan_search_end);
        searchPopView2.setAutoCompleteAdapter(autoCompleteAdapter2);
        searchPopView2.setTipsHintAdapter(hintAdapter);
        searchPopView2.setHintText("请输入终点");
        searchPopView2.setSearchPopViewListener(this);

        btnChange = (Button) getActivity().findViewById(R.id.adv_plan_btn_change);

        lvResults1 = (ListView) getView().findViewById(R.id.plan_search_results1);
        lvResults2 = (ListView) getView().findViewById(R.id.plan_search_results2);
        lvResults1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), "1-" + position + "", Toast.LENGTH_SHORT).show();
            }
        });
        lvResults2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), "2-" + position + "", Toast.LENGTH_SHORT).show();
            }
        });

        searchButton = (Button) getView().findViewById(R.id.plan_search_commit);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean JUD = false;
                Intent intent = new Intent();
                intent.setClass(getActivity(), AdvSearchActivity.class);
                //TODO 具体所需搜索信息
                String beginStation = searchPopView1.getText();
                String endStation = searchPopView2.getText();
                if (beginStation.equals(endStation)) {
                    JUD = true;
                }

                final Bundle mBundle = new Bundle();
                MainApplication application = (MainApplication) getActivity().getApplication();
                mBundle.putString("userId", application.getUser_id());
                mBundle.putString("beginStation", beginStation);
                mBundle.putString("endStation", endStation);
                mBundle.putString("time", textView.getText().toString().trim());
                if ((!beginStation.equals("")) & (!endStation.equals(""))) {
                    if (!JUD) {
                        // TODO: 2017/10/29  前后端联调
                        HttpUtil.sendAdviceOkHttpRequest(mBundle, new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                origin = ADVICE;
                                parseJSONWithJSONObject(responseData);
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: ERROR!");
                                Toast.makeText(getActivity(), "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "起终点信息不能相同，请重新输入！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "搜索框消息不完善，请填充完整后在开始搜索！", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text1 = searchPopView1.getText();
                String text2 = searchPopView2.getText();

                searchPopView1.setJUD(false);
                searchPopView2.setJUD(false);

                searchPopView1.setText(text2);
                searchPopView2.setText(text1);

                if (resultData1 == null) {
                    // 初始化
                    resultData1 = new ArrayList<>();
                } else {
                    resultData1.clear();
                    for (int i = 0; i < dbData.size(); i++) {
                        if (dbData.get(i).getTitle().equals(searchPopView1.getText().trim())) {
                            resultData1.add(dbData.get(i));
                        }
                    }
                }

                if (resultData2 == null) {
                    // 初始化
                    resultData2 = new ArrayList<>();
                } else {
                    resultData2.clear();
                    for (int i = 0; i < dbData.size(); i++) {
                        if (dbData.get(i).getTitle().equals(searchPopView2.getText().trim())) {
                            resultData2.add(dbData.get(i));
                        }
                    }
                }

                if (lvResults1.getAdapter() == null) {
                    //获取搜索数据 设置适配器
                    resultAdapter1.getItem(0).setComments("起点");
                    lvResults1.setAdapter(resultAdapter1);
                } else {
                    //更新搜索数据
                    resultAdapter1.getItem(0).setComments("起点");
                    resultAdapter1.notifyDataSetChanged();
                }
                if (lvResults2.getAdapter() == null) {
                    //获取搜索数据 设置适配器
                    resultAdapter2.getItem(0).setComments("终点");
                    lvResults2.setAdapter(resultAdapter2);
                } else {
                    //更新搜索数据
                    resultAdapter2.getItem(0).setComments("终点");
                    resultAdapter2.notifyDataSetChanged();
                }
                cvSearchBtn();
            }
        });
    }

    private void cvSearchBtn() {
        if ((lvResults1.getVisibility() == View.VISIBLE) && (lvResults2.getVisibility() == View.VISIBLE)) {
            searchButton.setVisibility(View.VISIBLE);
        } else {
            searchButton.setVisibility(View.GONE);
        }
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
        Bundle mBundleHttp = new Bundle();
        switch (origin) {
            case ADVICE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    String time = jsonObject.getString("time_advice");
                    JSONObject sellers = jsonObject.getJSONObject("sellers");
                    JSONArray route = jsonObject.getJSONArray("route");
                    JSONArray route_detail = jsonObject.getJSONArray("route_detail");
                    JSONArray busy = jsonObject.getJSONArray("busy");

                    ArrayList<String> stationList = new ArrayList<String>();
                    ArrayList<String> routeList = new ArrayList<String>();
                    ArrayList<String> stationDetailList = new ArrayList<String>();
                    ArrayList<String> routeDetailList = new ArrayList<String>();

                    mBundleHttp.putString("time", time);

                    for (int i = 0; i < route.length(); i++) {
                        if (i % 2 == 0) {
                            stationList.add(route.getString(i));
                        } else {
                            routeList.add(route.getString(i));
                        }
                    }
                    mBundleHttp.putStringArrayList("stationList", stationList);
                    mBundleHttp.putStringArrayList("routeList", routeList);
                    for (int i = 0; i < route_detail.length(); i++) {
                        if (i % 2 == 0) {
                            stationDetailList.add(route_detail.getString(i));
                        } else {
                            routeDetailList.add(route_detail.getString(i));
                        }
                    }
                    mBundleHttp.putStringArrayList("stationDetailList", stationDetailList);
                    mBundleHttp.putStringArrayList("routeDetailList", routeDetailList);

                    int size = 100;
                    double[] sellerLatList = new double[size];
                    double[] sellerLngList = new double[size];

                    int sellerRange = 2;
                    int count = 0;
                    for (int i = 0; i < stationList.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (sellers.has(stationList.get(i))) {
                                if (j < sellers.getJSONArray(stationList.get(i)).length()) {
                                    sellerLatList[count] = sellers.getJSONArray(stationList.get(i)).getJSONObject(j).getDouble("lat");
                                    sellerLngList[count] = sellers.getJSONArray(stationList.get(i)).getJSONObject(j).getDouble("lng");
                                    count++;
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("count", count);

                    mBundleHttp.putDoubleArray("sellerLatList", sellerLatList);
                    mBundleHttp.putDoubleArray("sellerLngList", sellerLngList);

                    mBundleHttp.putString("origin", "Advice");

                    Intent intent = new Intent(getActivity(), RouteResultActivity.class);
                    intent.putExtras(mBundleHttp);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case DATA:
                HashMap<String, String> params = new HashMap<>();
                params.put("userId", "guest");
                RequestManager.getInstance(context).requestAsyn("http://10.108.112.225:8080/route/station",
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
        if (autoCompleteData1 == null) {
            //初始化
            autoCompleteData1 = new ArrayList<>(hintSize);
        } else {
            // 根据text获取autodata
            autoCompleteData1.clear();
            for (int i = 0, count = 0; i < dbData.size()
                    && count < hintSize; i++) {
                if (dbData.get(i).getTitle().contains(searchPopView1.getText().trim())) {
                    autoCompleteData1.add(dbData.get(i).getTitle());
                    count++;
                }
            }
        }

        if (autoCompleteData2 == null) {
            //初始化
            autoCompleteData2 = new ArrayList<>(hintSize);
        } else {
            // 根据text获取autodata
            autoCompleteData2.clear();
            for (int i = 0, count = 0; i < dbData.size()
                    && count < hintSize; i++) {
                if (dbData.get(i).getTitle().contains(searchPopView2.getText().trim())) {
                    autoCompleteData2.add(dbData.get(i).getTitle());
                    count++;
                }
            }
        }

        if (autoCompleteAdapter1 == null) {
            autoCompleteAdapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData1);
        } else {
            if (searchPopView1.hasFocus()) {
                autoCompleteAdapter1.notifyDataSetChanged();
            }
        }

        if (autoCompleteAdapter2 == null) {
            autoCompleteAdapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData2);
        } else {
            if (searchPopView2.hasFocus()) {
                autoCompleteAdapter2.notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (resultData1 == null) {
            // 初始化
            resultData1 = new ArrayList<>();
        } else {
            resultData1.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().equals(searchPopView1.getText().trim())) {
                    resultData1.add(dbData.get(i));
                }
            }
        }

        if (resultData2 == null) {
            // 初始化
            resultData2 = new ArrayList<>();
        } else {
            resultData2.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().equals(searchPopView2.getText().trim())) {
                    resultData2.add(dbData.get(i));
                }
            }
        }

        if (resultAdapter1 == null) {
            resultAdapter1 = new RouteSearchAdapter(getActivity(), resultData1, R.layout.route_search_item_list);
        }

        if (resultAdapter2 == null) {
            resultAdapter2 = new RouteSearchAdapter(getActivity(), resultData2, R.layout.route_search_item_list);
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
        searchPopView1.setJUD(false);
        searchPopView2.setJUD(false);
        getResultData(text);
        if (!searchPopView1.getText().equals("")) {
            lvResults1.setVisibility(View.VISIBLE);
        }
        if (!searchPopView2.getText().equals("")) {
            lvResults2.setVisibility(View.VISIBLE);
        }

        if (searchPopView1.hasFocus()) {
            //第一次获取结果 还未配置适配器
            resultAdapter1.getItem(0).setComments("起点");
            if (lvResults1.getAdapter() == null) {
                //获取搜索数据 设置适配器
                lvResults1.setAdapter(resultAdapter1);
            } else {
                //更新搜索数据
                resultAdapter1.notifyDataSetChanged();
            }
        }

        if (searchPopView2.hasFocus()) {
            resultAdapter2.getItem(0).setComments("终点");
            if (lvResults2.getAdapter() == null) {
                //获取搜索数据 设置适配器
                lvResults2.setAdapter(resultAdapter2);
            } else {
                //更新搜索数据
                resultAdapter2.notifyDataSetChanged();
            }
        }

        cvSearchBtn();

        Toast.makeText(getActivity(), "完成搜索", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBack() {
        searchPopView1.setJUD(true);
        searchPopView2.setJUD(true);
        if (searchPopView1.getText().equals("")) {
            lvResults1.setVisibility(View.GONE);
            autoCompleteAdapter1.notifyDataSetChanged();
            resultAdapter1.notifyDataSetChanged();
        }
        if (searchPopView2.getText().equals("")) {
            lvResults2.setVisibility(View.GONE);
            autoCompleteAdapter2.notifyDataSetChanged();
            resultAdapter2.notifyDataSetChanged();
        }
        hintAdapter.notifyDataSetChanged();
    }

    /**
     * 自动补全提示框出现触发回调
     */
    @Override
    public void isFocus() {
        searchPopView1.setJUD(true);
        searchPopView2.setJUD(true);
        if (searchPopView1.hasFocus()) {
            lvResults1.setVisibility(View.GONE);
            autoCompleteAdapter1.notifyDataSetChanged();
            resultAdapter1.notifyDataSetChanged();
        }
        if (searchPopView2.hasFocus()) {
            lvResults2.setVisibility(View.GONE);
            autoCompleteAdapter2.notifyDataSetChanged();
            resultAdapter2.notifyDataSetChanged();
        }
        hintAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onHintClick(String text) {
        return true;
    }

    /**
     * 获取时间段的
     */
    private void showTimeDialog() {
        final AlertDialog.Builder timeDialog = new AlertDialog.Builder(this.getContext());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.fragment_adv_time_dialog, null);
        final TimePicker timePicker1 = (TimePicker) dialogView.findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        final TimePicker timePicker2 = (TimePicker) dialogView.findViewById(R.id.timePicker2);
        timePicker2.setIs24HourView(true);

        if (seted) {
            timePicker1.setHour(hour1);
            timePicker1.setMinute(minute1);
            timePicker2.setHour(hour2);
            timePicker2.setMinute(minute2);
        }
        timeDialog.setTitle("请选择出行时间段");
        timeDialog.setView(dialogView);
        timeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hour1 = timePicker1.getHour();
                        hour2 = timePicker2.getHour();
                        minute1 = timePicker1.getMinute();
                        minute2 = timePicker2.getMinute();
                        String h1 = timePicker1.getHour() + ":";
                        String m1 = timePicker1.getMinute() + " - ";
                        String h2 = timePicker2.getHour() + ":";
                        String m2 = String.valueOf(timePicker2.getMinute());
                        if (timePicker1.getHour() < 10) {
                            h1 = "0" + h1;
                        }
                        if (timePicker2.getHour() < 10) {
                            h2 = "0" + h2;
                        }
                        if (timePicker1.getMinute() < 10) {
                            m1 = "0" + m1;
                        }
                        if (timePicker2.getMinute() < 10) {
                            m2 = "0" + m2;
                        }
                        textView.setText(h1 + m1 + h2 + m2);
                        seted = true;
                    }
                });
        timeDialog.setNegativeButton("关闭", null);
        // 显示
        timeDialog.show();
    }
}
