package com.example.zero.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.example.zero.activity.RouteResultActivity;
import com.example.zero.adapter.RouteLineAdapter;
import com.example.zero.adapter.RouteSearchAdapter;
import com.example.zero.bean.RouteSearchBean;
import com.example.zero.bean.SaleBean;
import com.example.zero.entity.Route;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SearchPopView;
import com.example.zero.view.SearchView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.example.zero.fragment.RouteFragmentDouble.Origin.DATA;
import static com.example.zero.fragment.RouteFragmentDouble.Origin.MULTI;
import static com.example.zero.fragment.RouteFragmentDouble.Origin.SINGLE;

public class RouteFragmentDouble extends Fragment implements SearchPopView.SearchPopViewListener {

    /**
     * 搜索结果列表view
     */
    private ListView lvResults;
    /**
     * 搜索view
     */
    private SearchPopView searchView;
    /**
     * 搜索结果列表view
     */
    private ListView lvResults2;
    /**
     * 搜索view
     */
    private SearchPopView searchView2;
    /**
     * 搜索按钮
     */
    private Button btnSearch;
    /**
     * 交换按钮
     */
    private Button btnChange;
    /**
     * 抽屉按钮
     */
    private Button btnDrawer1;
    /**
     * 抽屉
     */
    private SlidingDrawer slidingDrawer1;

    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<String> hintAdapter;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter2;
    /**
     * 搜索结果列表adapter
     */
    private RouteSearchAdapter resultAdapter;
    /**
     * 搜索结果列表adapter
     */
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
    private List<String> autoCompleteData;
    /**
     * 搜索结果的数据
     */
    private List<RouteSearchBean> resultData;
    /**
     * 搜索过程中自动补全数据
     */
    private List<String> autoCompleteData2;
    /**
     * 搜索结果的数据
     */
    private List<RouteSearchBean> resultData2;
    /**
     * 默认提示框显示项的个数
     */
    private static int DEFAULT_HINT_SIZE = 4;
    /**
     * 提示框显示项的个数
     */
    private static int hintSize = DEFAULT_HINT_SIZE;

    private static final String TAG = "RouteFragmentDouble";

    /**
     * 搜索view
     */
    private SearchPopView endSearchView0;
    /**
     * 搜索结果列表view
     */
    private ListView lvResults0;
    /**
     * 起点搜索view
     */
    private ArrayList<SearchPopView> searchViewList0;
    /**
     * 起点添加按钮
     */
    private ArrayList<Button> btnaddList0;
    /**
     * 搜索按钮
     */
    private Button btnSearch0;
    /**
     * 抽屉按钮
     */
    private Button btnDrawer2;
    /**
     * 抽屉
     */
    private SlidingDrawer slidingDrawer2;

    /**
     * 数据库数据，总数据
     */
    private List<RouteSearchBean> dbData0;
    /**
     * 热搜版数据
     */
    private List<String> hintData0;
    /**
     * 搜索过程中自动补全数据
     */
    private List<String> autoCompleteData0;
    /**
     * 搜索结果的数据
     */
    private List<RouteSearchBean> resultData0;
    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<String> hintAdapter0;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter00;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter01;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter02;
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter03;
    /**
     * 搜索结果列表adapter
     */
    private RouteSearchAdapter resultAdapter0;

    boolean hasShownDialogue = false;

    MainApplication application;

    private Context context;

    private int cModel = 1;

    int sceneWhich;

    String sceneSel;

    private Origin origin;

    public enum Origin {
        SINGLE,
        MULTI,
        DATA,
        ADVICE
    }

    final private String[] stationDe = {"广州塔", "广州火车站"};

    /**
     * 设置提示框显示项的个数
     *
     * @param hintSize 提示框显示个数
     */
    public static void setHintSize(int hintSize) {
        RouteFragmentDouble.hintSize = hintSize;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route_double, container, false);
        context = v.getContext();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        application = (MainApplication) getActivity().getApplication();
        initData();
//        initViews();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        final Button model = (Button) getActivity().findViewById(R.id.title_btn_model);

        btnDrawer1 = (Button) getView().findViewById(R.id.drawer_handle1);
        slidingDrawer1 = (SlidingDrawer) getView().findViewById(R.id.route_drawer1);
        btnDrawer2 = (Button) getView().findViewById(R.id.drawer_handle2);
        slidingDrawer2 = (SlidingDrawer) getView().findViewById(R.id.route_drawer2);

        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建弹出式菜单对象，第二个参数是绑定的那个view
                PopupMenu popup = new PopupMenu(getContext(), view);
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.route_model_menu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.single_model:
                                Toast.makeText(getContext(), "Single", Toast.LENGTH_SHORT).show();
                                model.setText("单人模式");
                                btnDrawer1.setVisibility(View.VISIBLE);
                                slidingDrawer1.setVisibility(View.VISIBLE);
                                btnDrawer2.setVisibility(View.GONE);
                                slidingDrawer2.setVisibility(View.GONE);
                                btnDrawer2.setClickable(false);
                                cModel = 1;
                                break;
                            case R.id.multi_model:
                                Toast.makeText(getContext(), "Multi", Toast.LENGTH_SHORT).show();
                                model.setText("多人模式");
                                btnDrawer1.setVisibility(View.GONE);
                                slidingDrawer1.setVisibility(View.GONE);
                                btnDrawer2.setVisibility(View.VISIBLE);
                                slidingDrawer2.setVisibility(View.VISIBLE);
                                btnDrawer1.setClickable(false);
                                cModel = 2;
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        searchView = (SearchPopView) getView().findViewById(R.id.route_search_single2);
        lvResults = (ListView) getView().findViewById(R.id.route_lv_search_single_results2);
        searchView2 = (SearchPopView) getView().findViewById(R.id.route_search_single22);
        lvResults2 = (ListView) getView().findViewById(R.id.route_lv_search_single_results22);
        btnSearch = (Button) getView().findViewById(R.id.route_btn_single_search2);
        btnChange = (Button) getView().findViewById(R.id.search_btn_change2);

        searchView.setHintText("请输入起点：");
        searchView2.setHintText("请输入终点：");

        endSearchView0 = (SearchPopView) getView().findViewById(R.id.route_search_multi2);
        lvResults0 = (ListView) getView().findViewById(R.id.route_lv_search_multi_results2);
        btnSearch0 = (Button) getView().findViewById(R.id.route_btn_multi_search2);

        int size = 4;
        searchViewList0 = new ArrayList<SearchPopView>(size);
        searchViewList0.add((SearchPopView) getView().findViewById(R.id.route_search_multi02));
        searchViewList0.add((SearchPopView) getView().findViewById(R.id.route_search_multi12));
        searchViewList0.add((SearchPopView) getView().findViewById(R.id.route_search_multi22));

        btnaddList0 = new ArrayList<Button>(size);
        btnaddList0.add((Button) getView().findViewById(R.id.route_btn_add_multi02));
        btnaddList0.add((Button) getView().findViewById(R.id.route_btn_add_multi12));

        endSearchView0.setHintText("请输入终点：");
        searchViewList0.get(0).setHintText("情输入起点：");
        searchViewList0.get(1).setHintText("情输入起点：");
        searchViewList0.get(2).setHintText("情输入起点：");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean JUD = false;
                final String beginStation = searchView.getText();
                final String endStation = searchView2.getText();
                if (beginStation.equals(endStation)) {
                    JUD = true;
                }
                final Bundle mBundle = new Bundle();
                if (application.isOnline()) {
                    mBundle.putString("userId", application.getUser_id());
                } else {
                    mBundle.putString("userId", "guest");
                }
                mBundle.putString("beginStation", beginStation);
                mBundle.putString("endStation", endStation);
                if ((!beginStation.equals("")) & (!endStation.equals(""))) {
                    if (!JUD) {
                        // TODO: 2017/10/29  前后端联调
                        HttpUtil.sendSingleOkHttpRequest(mBundle, new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                origin = SINGLE;
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

        btnSearch0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.android);
                builder.setTitle("请选择汇聚情景");
                final String[] scene = {"智能推荐", "直接到达", "购物", "餐饮", "其他"};
                final String[] sceneChar = {"auto", "direct", "shopping", "eatting"};
                /**
                 * 设置一个单项选择下拉框
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                builder.setSingleChoiceItems(scene, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sceneWhich = which;
                        Toast.makeText(context, "选择了：" + scene[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (scene[sceneWhich].equals("其他")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setIcon(R.drawable.android);
                            builder.setTitle("请输入您的汇聚情景关键词");
                            View view = LayoutInflater.from(context).inflate(R.layout.route_scene_pop, null);
                            builder.setView(view);

                            final EditText other_scene = (EditText) view.findViewById(R.id.other_scene);

                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sceneSel = other_scene.getText().toString().trim();
                                    int beginNum = 0;
                                    boolean JUD = false;
                                    String endStation = endSearchView0.getText();
                                    ArrayList<String> beginStationList = new ArrayList<String>();
                                    beginStationList.add(searchViewList0.get(0).getText());
                                    beginStationList.add(searchViewList0.get(1).getText());
                                    beginStationList.add(searchViewList0.get(2).getText());
                                    for (String str : beginStationList) {
                                        if (!str.equals("")) {
                                            beginNum++;
                                        }
                                    }
                                    if (endStation.equals(beginStationList.get(0)) | endStation.equals(beginStationList.get(1)) | endStation.equals(beginStationList.get(2))) {
                                        JUD = true;
                                    }
                                    Bundle mBundle = new Bundle();
                                    if (application.isOnline()) {
                                        mBundle.putString("userId", application.getUser_id());
                                    } else {
                                        mBundle.putString("userId", "guest");
                                    }
                                    mBundle.putString("endStation", endStation);
                                    mBundle.putStringArrayList("beginStationList", beginStationList);
                                    mBundle.putInt("beginNum", beginNum);
                                    mBundle.putString("Scene", sceneSel);
                                    if ((!endStation.equals("")) & (beginNum != 0)) {
                                        if (!JUD) {
                                            HttpUtil.sendMultiOkHttpRequest(mBundle, new okhttp3.Callback() {
                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    String responseData = response.body().string();
                                                    origin = MULTI;
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
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        } else {
                            sceneSel = sceneChar[sceneWhich];
                            int beginNum = 0;
                            boolean JUD = false;
                            String endStation = endSearchView0.getText();
                            ArrayList<String> beginStationList = new ArrayList<String>();
                            beginStationList.add(searchViewList0.get(0).getText());
                            beginStationList.add(searchViewList0.get(1).getText());
                            beginStationList.add(searchViewList0.get(2).getText());
                            for (String str : beginStationList) {
                                if (!str.equals("")) {
                                    beginNum++;
                                }
                            }
                            if (endStation.equals(beginStationList.get(0)) | endStation.equals(beginStationList.get(1)) | endStation.equals(beginStationList.get(2))) {
                                JUD = true;
                            }
                            Bundle mBundle = new Bundle();
                            if (application.isOnline()) {
                                mBundle.putString("userId", application.getUser_id());
                            } else {
                                mBundle.putString("userId", "guest");
                            }
                            mBundle.putString("endStation", endStation);
                            mBundle.putStringArrayList("beginStationList", beginStationList);
                            mBundle.putInt("beginNum", beginNum);
                            mBundle.putString("Scene", sceneSel);
                            if ((!endStation.equals("")) & (beginNum != 0)) {
                                if (!JUD) {
                                    HttpUtil.sendMultiOkHttpRequest(mBundle, new okhttp3.Callback() {
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String responseData = response.body().string();
                                            origin = MULTI;
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
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text1 = searchView.getText();
                String text2 = searchView2.getText();
                if ((!text1.equals("")) && (!text2.equals(""))) {
                    searchView.setJUD(false);
                    searchView2.setJUD(false);

                    searchView.setText(text2);
                    searchView2.setText(text1);

                    if (resultData == null) {
                        // 初始化
                        resultData = new ArrayList<>();
                    } else {
                        resultData.clear();
                        for (int i = 0; i < dbData.size(); i++) {
                            if (dbData.get(i).getTitle().equals(searchView.getText().trim())) {
                                resultData.add(dbData.get(i));
                            }
                        }
                    }

                    if (resultData2 == null) {
                        // 初始化
                        resultData2 = new ArrayList<>();
                    } else {
                        resultData2.clear();
                        for (int i = 0; i < dbData.size(); i++) {
                            if (dbData.get(i).getTitle().equals(searchView2.getText().trim())) {
                                resultData2.add(dbData.get(i));
                            }
                        }
                    }

                    if (lvResults.getAdapter() == null) {
                        //获取搜索数据 设置适配器
                        resultAdapter.getItem(0).setComments("起点");
                        lvResults.setAdapter(resultAdapter);
                    } else {
                        //更新搜索数据
                        resultAdapter.getItem(0).setComments("起点");
                        resultAdapter.notifyDataSetChanged();
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
            }
        });

        //设置监听
        searchView.setSearchPopViewListener(this);
        searchView2.setSearchPopViewListener(this);
        //设置adapter
        if (hintAdapter != null) {
            searchView.setTipsHintAdapter(hintAdapter);
            searchView2.setTipsHintAdapter(hintAdapter);
        }
        if ((autoCompleteAdapter != null) && (autoCompleteAdapter2 != null)) {
            searchView.setAutoCompleteAdapter(autoCompleteAdapter);
            searchView2.setAutoCompleteAdapter(autoCompleteAdapter2);
        }

        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        //设置监听
        endSearchView0.setSearchPopViewListener(this);
        searchViewList0.get(0).setSearchPopViewListener(this);
        searchViewList0.get(1).setSearchPopViewListener(this);
        searchViewList0.get(2).setSearchPopViewListener(this);
        //设置adapter
        if (hintAdapter0 != null) {
            endSearchView0.setTipsHintAdapter(hintAdapter0);
            searchViewList0.get(0).setTipsHintAdapter(hintAdapter0);
            searchViewList0.get(1).setTipsHintAdapter(hintAdapter0);
            searchViewList0.get(2).setTipsHintAdapter(hintAdapter0);
        }
        if ((autoCompleteAdapter00 != null) && (autoCompleteAdapter01 != null)
                && (autoCompleteAdapter02 != null) && (autoCompleteAdapter03 != null)) {
            endSearchView0.setAutoCompleteAdapter(autoCompleteAdapter00);
            searchViewList0.get(0).setAutoCompleteAdapter(autoCompleteAdapter01);
            searchViewList0.get(1).setAutoCompleteAdapter(autoCompleteAdapter02);
            searchViewList0.get(2).setAutoCompleteAdapter(autoCompleteAdapter03);
        }

        lvResults0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

        btnaddList0.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewList0.get(1).setVisibility(View.VISIBLE);
                RelativeLayout rl2 = (RelativeLayout) getActivity().findViewById(R.id.route_btn_add_rl2);
                rl2.setVisibility(View.VISIBLE);
                btnaddList0.get(1).setVisibility(View.VISIBLE);
            }
        });
        btnaddList0.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewList0.get(2).setVisibility(View.VISIBLE);
            }
        });

        //完全打开
        slidingDrawer1.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                btnDrawer1.setBackgroundResource(R.drawable.drawer_down);
            }
        });

        //完全关闭
        slidingDrawer1.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                btnDrawer1.setBackgroundResource(R.drawable.drawer_up);
            }
        });

        slidingDrawer1.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {
            //开始滚动时的操作
            @Override
            public void onScrollStarted() {
                btnDrawer1.setBackgroundResource(R.drawable.drawer_loading);
            }

            //结束滚动时的操作
            @Override
            public void onScrollEnded() {
                if (slidingDrawer1.isOpened()) {
                    btnDrawer1.setBackgroundResource(R.drawable.drawer_down);
                } else {
                    btnDrawer1.setBackgroundResource(R.drawable.drawer_up);
                }
            }

        });

        //完全打开
        slidingDrawer2.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                btnDrawer2.setBackgroundResource(R.drawable.drawer_down);
            }
        });

        //完全关闭
        slidingDrawer2.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                btnDrawer2.setBackgroundResource(R.drawable.drawer_up);
            }
        });

        slidingDrawer2.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {
            //开始滚动时的操作
            @Override
            public void onScrollStarted() {
                btnDrawer2.setBackgroundResource(R.drawable.drawer_loading);
            }

            //结束滚动时的操作
            @Override
            public void onScrollEnded() {
                if (slidingDrawer2.isOpened()) {
                    btnDrawer2.setBackgroundResource(R.drawable.drawer_down);
                } else {
                    btnDrawer2.setBackgroundResource(R.drawable.drawer_up);
                }
            }

        });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        //从数据库获取数据
        getData();
    }

    /**
     * json处理函数
     *
     * @param jsonData json字符串
     */
    private void parseJSONWithJSONObject(String jsonData) {
        Bundle mBundleHttp = new Bundle();
        switch (origin) {
            case SINGLE:
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject seller = jsonObject.getJSONObject("seller");
                    JSONObject route = jsonObject.getJSONObject("route");
                    JSONObject busy = jsonObject.getJSONObject("busy");

                    JSONObject routeFastX = route.getJSONObject("fast");
                    JSONObject routeLessbusyX = route.getJSONObject("lessbusy");
                    JSONObject routeLesschangeX = route.getJSONObject("lesschange");

                    JSONArray routeFast = routeFastX.getJSONArray("route");
                    JSONArray routeLessbusy = routeLessbusyX.getJSONArray("route");
                    JSONArray routeLesschange = routeLesschangeX.getJSONArray("route");
                    JSONArray routeFastDetail = routeFastX.getJSONArray("route_detail");
                    JSONArray routeLessbusyDetail = routeLessbusyX.getJSONArray("route_detail");
                    JSONArray routeLesschangeDetail = routeLesschangeX.getJSONArray("route_detail");

                    ArrayList<String> fastStationList = new ArrayList<String>();
                    ArrayList<String> lessbusyStationList = new ArrayList<String>();
                    ArrayList<String> lesschangeStationList = new ArrayList<String>();
                    ArrayList<String> fastRouteList = new ArrayList<String>();
                    ArrayList<String> lessbusyRouteList = new ArrayList<String>();
                    ArrayList<String> lesschangeRouteList = new ArrayList<String>();

                    ArrayList<String> fastStationDetailList = new ArrayList<String>();
                    ArrayList<String> lessbusyStationDetailList = new ArrayList<String>();
                    ArrayList<String> lesschangeStationDetailList = new ArrayList<String>();

                    for (int i = 0; i < routeFast.length(); i++) {
                        if (i % 2 == 0) {
                            fastStationList.add(routeFast.getString(i));
                        } else {
                            fastRouteList.add(routeFast.getString(i));
                        }
                    }
                    for (int i = 0; i < routeFastDetail.length(); i++) {
                        if (i % 2 == 0) {
                            fastStationDetailList.add(routeFastDetail.getString(i));
                        }
                    }
                    mBundleHttp.putStringArrayList("fastStationList", fastStationList);
                    mBundleHttp.putStringArrayList("fastRouteList", fastRouteList);
                    mBundleHttp.putStringArrayList("fastStationDetailList", fastStationDetailList);

                    for (int i = 0; i < routeLessbusy.length(); i++) {
                        if (i % 2 == 0) {
                            lessbusyStationList.add(routeLessbusy.getString(i));
                        } else {
                            lessbusyRouteList.add(routeLessbusy.getString(i));
                        }
                    }
                    for (int i = 0; i < routeLessbusyDetail.length(); i++) {
                        if (i % 2 == 0) {
                            lessbusyStationDetailList.add(routeLessbusyDetail.getString(i));
                        }
                    }
                    mBundleHttp.putStringArrayList("lessbusyStationList", lessbusyStationList);
                    mBundleHttp.putStringArrayList("lessbusyRouteList", lessbusyRouteList);
                    mBundleHttp.putStringArrayList("lessbusyStationDetailList", lessbusyStationDetailList);

                    for (int i = 0; i < routeLesschange.length(); i++) {
                        if (i % 2 == 0) {
                            lesschangeStationList.add(routeLesschange.getString(i));
                        } else {
                            lesschangeRouteList.add(routeLesschange.getString(i));
                        }
                    }
                    for (int i = 0; i < routeLesschangeDetail.length(); i++) {
                        if (i % 2 == 0) {
                            lesschangeStationDetailList.add(routeLesschangeDetail.getString(i));
                        }
                    }
                    mBundleHttp.putStringArrayList("lesschangeStationList", lesschangeStationList);
                    mBundleHttp.putStringArrayList("lesschangeRouteList", lesschangeRouteList);
                    mBundleHttp.putStringArrayList("lesschangeStationDetailList", lesschangeStationDetailList);

                    int size = 100;
                    double[] fastSellerLatList = new double[size];
                    double[] fastSellerLngList = new double[size];
                    double[] lessbusySellerLatList = new double[size];
                    double[] lessbusySellerLngList = new double[size];
                    double[] lesschangeSellerLatList = new double[size];
                    double[] lesschangeSellerLngList = new double[size];

                    int sellerRange = 2;
                    int count = 0;
                    for (int i = 0; i < fastStationList.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (seller.has(fastStationList.get(i))) {
                                if (j < seller.getJSONArray(fastStationList.get(i)).length()) {
                                    if(!seller.getJSONArray(fastStationList.get(i)).getJSONObject(j).getString("lat").equals("null")){
                                        fastSellerLatList[count] = seller.getJSONArray(fastStationList.get(i)).getJSONObject(j).getDouble("lat");
                                        fastSellerLngList[count] = seller.getJSONArray(fastStationList.get(i)).getJSONObject(j).getDouble("lng");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("fastCount", count);

                    count = 0;
                    for (int i = 0; i < lessbusyStationList.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (seller.has(lessbusyStationList.get(i))) {
                                if (j < seller.getJSONArray(lessbusyStationList.get(i)).length()) {
                                    if(!seller.getJSONArray(lessbusyStationList.get(i)).getJSONObject(j).getString("lat").equals("null")) {
                                        lessbusySellerLatList[count] = seller.getJSONArray(lessbusyStationList.get(i)).getJSONObject(j).getDouble("lat");
                                        lessbusySellerLngList[count] = seller.getJSONArray(lessbusyStationList.get(i)).getJSONObject(j).getDouble("lng");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("lessbusyCount", count);

                    count = 0;
                    for (int i = 0; i < lesschangeStationList.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (seller.has(lesschangeStationList.get(i))) {
                                if (j < seller.getJSONArray(lesschangeStationList.get(i)).length()) {
                                    if(!seller.getJSONArray(lesschangeStationList.get(i)).getJSONObject(j).getString("lat").equals("null")){
                                    lesschangeSellerLatList[count] = seller.getJSONArray(lesschangeStationList.get(i)).getJSONObject(j).getDouble("lat");
                                    lesschangeSellerLngList[count] = seller.getJSONArray(lesschangeStationList.get(i)).getJSONObject(j).getDouble("lng");
                                    count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("lesschangeCount", count);

                    mBundleHttp.putDoubleArray("fastSellerLatList", fastSellerLatList);
                    mBundleHttp.putDoubleArray("fastSellerLngList", fastSellerLngList);
                    mBundleHttp.putDoubleArray("lessbusySellerLatList", lessbusySellerLatList);
                    mBundleHttp.putDoubleArray("lessbusySellerLngList", lessbusySellerLngList);
                    mBundleHttp.putDoubleArray("lesschangeSellerLatList", lesschangeSellerLatList);
                    mBundleHttp.putDoubleArray("lesschangeSellerLngList", lesschangeSellerLngList);

                    mBundleHttp.putString("origin", "Single");

                    Intent intent = new Intent(getActivity(), RouteResultActivity.class);
                    intent.putExtras(mBundleHttp);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case MULTI:
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);

                    JSONObject sellers = jsonObject.getJSONObject("sellers");
                    JSONArray routes = jsonObject.getJSONArray("routes");
                    JSONObject busy = jsonObject.getJSONObject("busy");
                    JSONArray routeBfMeet = routes.getJSONArray(0);
                    JSONArray routeAfMeet = routes.getJSONArray(1);

                    ArrayList<String> stationList1 = new ArrayList<String>();
                    ArrayList<String> stationList2 = new ArrayList<String>();
                    ArrayList<String> stationList3 = new ArrayList<String>();
                    ArrayList<String> routeList1 = new ArrayList<String>();
                    ArrayList<String> routeList2 = new ArrayList<String>();
                    ArrayList<String> routeList3 = new ArrayList<String>();

                    ArrayList<String> stationDetailList1 = new ArrayList<String>();
                    ArrayList<String> stationDetailList2 = new ArrayList<String>();
                    ArrayList<String> stationDetailList3 = new ArrayList<String>();
                    ArrayList<String> routeDetailList1 = new ArrayList<String>();
                    ArrayList<String> routeDetailList2 = new ArrayList<String>();
                    ArrayList<String> routeDetailList3 = new ArrayList<String>();

                    ArrayList<String> stationAfMeetList = new ArrayList<String>();
                    ArrayList<String> routeAfMeetList = new ArrayList<String>();
                    ArrayList<String> stationAfMeetDetailList = new ArrayList<String>();
                    ArrayList<String> routeAfMeetDetailList = new ArrayList<String>();

                    for (int i = 0; i < routeBfMeet.length(); i++) {
                        for (int j = 0; j < routeBfMeet.getJSONObject(i).getJSONArray("route").length(); j++) {
                            if (j % 2 == 0) {
                                switch (i) {
                                    case 0:
                                        stationList1.add(routeBfMeet.getJSONObject(i).getJSONArray("route").getString(j));
                                        break;
                                    case 1:
                                        stationList2.add(routeBfMeet.getJSONObject(i).getJSONArray("route").getString(j));
                                        break;
                                    case 2:
                                        stationList3.add(routeBfMeet.getJSONObject(i).getJSONArray("route").getString(j));
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                switch (i) {
                                    case 0:
                                        routeList1.add(routeBfMeet.getJSONObject(i).getJSONArray("route").getString(j));
                                        break;
                                    case 1:
                                        routeList2.add(routeBfMeet.getJSONObject(i).getJSONArray("route").getString(j));
                                        break;
                                    case 2:
                                        routeList3.add(routeBfMeet.getJSONObject(i).getJSONArray("route").getString(j));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        for (int j = 0; j < routeBfMeet.getJSONObject(i).getJSONArray("route_detail").length(); j++) {
                            if (j % 2 == 0) {
                                switch (i) {
                                    case 0:
                                        stationDetailList1.add(routeBfMeet.getJSONObject(i).getJSONArray("route_detail").getString(j));
                                        break;
                                    case 1:
                                        stationDetailList2.add(routeBfMeet.getJSONObject(i).getJSONArray("route_detail").getString(j));
                                        break;
                                    case 2:
                                        stationDetailList3.add(routeBfMeet.getJSONObject(i).getJSONArray("route_detail").getString(j));
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                switch (i) {
                                    case 0:
                                        routeDetailList1.add(routeBfMeet.getJSONObject(i).getJSONArray("route_detail").getString(j));
                                        break;
                                    case 1:
                                        routeDetailList2.add(routeBfMeet.getJSONObject(i).getJSONArray("route_detail").getString(j));
                                        break;
                                    case 2:
                                        routeDetailList3.add(routeBfMeet.getJSONObject(i).getJSONArray("route_detail").getString(j));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    if (!routeAfMeet.isNull(0)) {
                        for (int i = 0; i < routeAfMeet.getJSONObject(0).getJSONArray("route").length(); i++) {
                            if (i % 2 == 0) {
                                stationAfMeetList.add(routeAfMeet.getJSONObject(0).getJSONArray("route").getString(i));
                            } else {
                                routeAfMeetList.add(routeAfMeet.getJSONObject(0).getJSONArray("route").getString(i));
                            }
                        }
                        for (int i = 0; i < routeAfMeet.getJSONObject(0).getJSONArray("route_detail").length(); i++) {
                            if (i % 2 == 0) {
                                stationAfMeetDetailList.add(routeAfMeet.getJSONObject(0).getJSONArray("route_detail").getString(i));
                            } else {
                                routeAfMeetDetailList.add(routeAfMeet.getJSONObject(0).getJSONArray("route_detail").getString(i));
                            }
                        }
                    }

                    mBundleHttp.putStringArrayList("stationList1", stationList1);
                    mBundleHttp.putStringArrayList("stationList2", stationList2);
                    mBundleHttp.putStringArrayList("stationList3", stationList3);
                    mBundleHttp.putStringArrayList("routeList1", routeList1);
                    mBundleHttp.putStringArrayList("routeList2", routeList2);
                    mBundleHttp.putStringArrayList("routeList3", routeList3);
                    mBundleHttp.putStringArrayList("stationDetailList1", stationDetailList1);
                    mBundleHttp.putStringArrayList("stationDetailList2", stationDetailList2);
                    mBundleHttp.putStringArrayList("stationDetailList3", stationDetailList3);
                    mBundleHttp.putStringArrayList("routeDetailList1", routeDetailList1);
                    mBundleHttp.putStringArrayList("routeDetailList2", routeDetailList2);
                    mBundleHttp.putStringArrayList("routeDetailList3", routeDetailList3);
                    mBundleHttp.putStringArrayList("stationAfMeetList", stationAfMeetList);
                    mBundleHttp.putStringArrayList("routeAfMeetList", routeAfMeetList);
                    mBundleHttp.putStringArrayList("stationAfMeetDetailList", stationAfMeetDetailList);
                    mBundleHttp.putStringArrayList("routeAfMeetDetailList", routeAfMeetDetailList);

                    int size = 100;
                    double[] firstSellerLatList = new double[size];
                    double[] firstSellerLngList = new double[size];
                    double[] secondSellerLatList = new double[size];
                    double[] secondSellerLngList = new double[size];
                    double[] thirdSellerLatList = new double[size];
                    double[] thirdSellerLngList = new double[size];

                    double[] afMeetSellerLatList = new double[size];
                    double[] afMeetSellerLngList = new double[size];

                    int sellerRange = 2;
                    int count = 0;
                    for (int i = 0; i < stationList1.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (sellers.has(stationList1.get(i))) {
                                if (j < sellers.getJSONArray(stationList1.get(i)).length()) {
                                    if (!sellers.getJSONArray(stationList1.get(i)).getJSONObject(j).getString("lat").equals("null")) {
                                        firstSellerLatList[count] = sellers.getJSONArray(stationList1.get(i)).getJSONObject(j).getDouble("lat");
                                        firstSellerLngList[count] = sellers.getJSONArray(stationList1.get(i)).getJSONObject(j).getDouble("lng");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("firstCount", count);

                    count = 0;
                    for (int i = 0; i < stationList2.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (sellers.has(stationList2.get(i))) {
                                if (j < sellers.getJSONArray(stationList2.get(i)).length()) {
                                    if (!sellers.getJSONArray(stationList2.get(i)).getJSONObject(j).getString("lat").equals("null")) {
                                        secondSellerLatList[count] = sellers.getJSONArray(stationList2.get(i)).getJSONObject(j).getDouble("lat");
                                        secondSellerLngList[count] = sellers.getJSONArray(stationList2.get(i)).getJSONObject(j).getDouble("lng");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("secondCount", count);

                    count = 0;
                    for (int i = 0; i < stationList3.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (sellers.has(stationList3.get(i))) {
                                if (j < sellers.getJSONArray(stationList3.get(i)).length()) {
                                    if (!sellers.getJSONArray(stationList3.get(i)).getJSONObject(j).getString("lat").equals("null")) {
                                        thirdSellerLatList[count] = sellers.getJSONArray(stationList3.get(i)).getJSONObject(j).getDouble("lat");
                                        thirdSellerLngList[count] = sellers.getJSONArray(stationList3.get(i)).getJSONObject(j).getDouble("lng");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("thirdCount", count);

                    count = 0;
                    for (int i = 0; i < stationAfMeetList.size(); i++) {
                        for (int j = 0; j < sellerRange; j++) {
                            if (sellers.has(stationAfMeetList.get(i))) {
                                if (j < sellers.getJSONArray(stationAfMeetList.get(i)).length()) {
                                    if (!sellers.getJSONArray(stationAfMeetList.get(i)).getJSONObject(j).getString("lat").equals("null")) {
                                        afMeetSellerLatList[count] = sellers.getJSONArray(stationAfMeetList.get(i)).getJSONObject(j).getDouble("lat");
                                        afMeetSellerLngList[count] = sellers.getJSONArray(stationAfMeetList.get(i)).getJSONObject(j).getDouble("lng");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    mBundleHttp.putInt("afMeetCount", count);

                    mBundleHttp.putDoubleArray("firstSellerLatList", firstSellerLatList);
                    mBundleHttp.putDoubleArray("firstSellerLngList", firstSellerLngList);
                    mBundleHttp.putDoubleArray("secondSellerLatList", secondSellerLatList);
                    mBundleHttp.putDoubleArray("secondSellerLngList", secondSellerLngList);
                    mBundleHttp.putDoubleArray("thirdSellerLatList", thirdSellerLatList);
                    mBundleHttp.putDoubleArray("thirdSellerLngList", thirdSellerLngList);
                    mBundleHttp.putDoubleArray("afMeetSellerLatList", afMeetSellerLatList);
                    mBundleHttp.putDoubleArray("afMeetSellerLngList", afMeetSellerLngList);

                    mBundleHttp.putString("origin", "Multi");

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
                RequestManager.getInstance(context).requestAsyn("http://10.108.120.225:8080/route/station",
                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
                            @Override
                            public void onReqSuccess(String result) {
                                com.alibaba.fastjson.JSONObject jsonData = JSON.parseObject(result);

                                com.alibaba.fastjson.JSONArray station = jsonData.getJSONArray("station");
                                com.alibaba.fastjson.JSONArray busy = jsonData.getJSONArray("busy");

                                dbData = new ArrayList<>(station.size());
                                dbData0 = new ArrayList<>(station.size());
                                for (int i = 0; i < station.size(); i++) {
                                    dbData.add(new RouteSearchBean(R.drawable.title_icon, station.getString(i),
                                            "周围简介\n热门吃、喝、玩、乐", ""));
                                    dbData0.add(new RouteSearchBean(R.drawable.title_icon, station.getString(i),
                                            "周围简介\n热门吃、喝、玩、乐", ""));
                                }
                                application.setStationList(dbData);
                                Log.d(TAG, "parseJSONWithJSONObject: " + dbData.size());

                                setHintSize(busy.size());
                                hintData = new ArrayList<>(hintSize);
                                hintData0 = new ArrayList<>(hintSize);
                                hintData.add("广州东站");
                                hintData.add("体育西路");
                                hintData.add("金洲");
                                hintData0.add("广州东站");
                                hintData0.add("体育西路");
                                hintData0.add("金洲");
                                for (int i = 0; i < hintSize; i++) {
                                    hintData.add(busy.getString(i));
                                    hintData0.add(busy.getString(i));
                                }
                                application.setBusyStationList(hintData);

                                hintAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hintData);
                                hintAdapter0 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hintData0);
                                //初始化自动补全数据
                                getAutoCompleteData(null);
                                //初始化搜索结果数据
                                getResultData(null);
                                //初始化自动补全数据
                                getAutoCompleteData0(null);
                                //初始化搜索结果数据
                                getResultData0(null);

                                initViews();
                                Log.d(TAG, "parseJSONWithJSONObject: " + hintData.size());
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {
                                dbData = new ArrayList<>();
                                dbData0 = new ArrayList<>();
                                for (int i = 0; i < stationDe.length; i++) {
                                    dbData.add(new RouteSearchBean(R.drawable.title_icon, stationDe[i],
                                            "周围简介\n热门吃、喝、玩、乐", ""));
                                    dbData0.add(new RouteSearchBean(R.drawable.title_icon, stationDe[i],
                                            "周围简介\n热门吃、喝、玩、乐", ""));
                                }
                                Log.d(TAG, "parseJSONWithJSONObject: " + dbData.size());

                                hintData = new ArrayList<>();
                                hintData0 = new ArrayList<>();
                                hintData.add("广州塔");
                                hintData.add("体育西路");
                                hintData.add("广州火车站");
                                hintAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hintData);
                                hintAdapter0 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hintData0);
                                //初始化自动补全数据
                                getAutoCompleteData(null);
                                //初始化搜索结果数据
                                getResultData(null);
                                //初始化自动补全数据
                                getAutoCompleteData0(null);
                                //初始化搜索结果数据
                                getResultData0(null);

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
     * 获取db数据
     */
    private void getData() {
        if (application.getStationList() == null) {
            origin = DATA;
            parseJSONWithJSONObject(null);
        } else {
            dbData = application.getStationList();
            dbData0 = application.getStationList();
            hintData = application.getBusyStationList();
            hintData0 = application.getBusyStationList();
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
                if (dbData.get(i).getTitle().contains(searchView.getText().trim())) {
                    autoCompleteData.add(dbData.get(i).getTitle());
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
                if (dbData.get(i).getTitle().contains(searchView2.getText().trim())) {
                    autoCompleteData2.add(dbData.get(i).getTitle());
                    count++;
                }
            }
        }

        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData);
        } else {
            if (searchView.hasFocus()) {
                autoCompleteAdapter.notifyDataSetChanged();
            }
        }

        if (autoCompleteAdapter2 == null) {
            autoCompleteAdapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData2);
        } else {
            if (searchView2.hasFocus()) {
                autoCompleteAdapter2.notifyDataSetChanged();
            }
        }

        Log.d(TAG, "getAutoCompleteData: finish");
    }

    private void getAutoCompleteData0(String text) {
        if (autoCompleteData0 == null) {
            //初始化
            autoCompleteData0 = new ArrayList<>(hintSize);
        } else {
            // 根据text获取autodata
            autoCompleteData0.clear();
            for (int i = 0, count = 0; i < dbData0.size()
                    && count < hintSize; i++) {
                if (dbData0.get(i).getTitle().contains(text.trim())) {
                    autoCompleteData0.add(dbData0.get(i).getTitle());
                    count++;
                }
            }
        }

        if (autoCompleteAdapter00 == null) {
            autoCompleteAdapter00 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData0);
        } else {
            autoCompleteAdapter00.notifyDataSetChanged();
        }

        if (autoCompleteAdapter01 == null) {
            autoCompleteAdapter01 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData0);
        } else {
            autoCompleteAdapter01.notifyDataSetChanged();
        }

        if (autoCompleteAdapter02 == null) {
            autoCompleteAdapter02 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData0);
        } else {
            autoCompleteAdapter02.notifyDataSetChanged();
        }

        if (autoCompleteAdapter03 == null) {
            autoCompleteAdapter03 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, autoCompleteData0);
        } else {
            autoCompleteAdapter03.notifyDataSetChanged();
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
                if (dbData.get(i).getTitle().equals(searchView.getText().trim())) {
                    resultData.add(dbData.get(i));
                }
            }
        }

        if (resultData2 == null) {
            // 初始化
            resultData2 = new ArrayList<>();
        } else {
            resultData2.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().equals(searchView2.getText().trim())) {
                    resultData2.add(dbData.get(i));
                }
            }
        }

        if (resultAdapter == null) {
            resultAdapter = new RouteSearchAdapter(getActivity(), resultData, R.layout.route_search_item_list);
        }

        if (resultAdapter2 == null) {
            resultAdapter2 = new RouteSearchAdapter(getActivity(), resultData2, R.layout.route_search_item_list);
        }

        Log.d(TAG, "getResultData: finish");
    }

    private void getResultData0(String text) {
        if (resultData0 == null) {
            // 初始化
            resultData0 = new ArrayList<>();
        } else {
            resultData0.clear();
            for (int i = 0; i < dbData0.size(); i++) {
                if (dbData0.get(i).getTitle().equals(text.trim())) {
                    resultData0.add(dbData0.get(i));
                }
            }
        }

        if (resultAdapter0 == null) {
            resultAdapter0 = new RouteSearchAdapter(getActivity(), resultData0, R.layout.route_search_item_list);
        }
    }

    private void cvSearchBtn() {
        if ((lvResults.getVisibility() == View.VISIBLE) && (lvResults2.getVisibility() == View.VISIBLE) && (cModel == 1)) {
            btnSearch.setVisibility(View.VISIBLE);
        } else {
            btnSearch.setVisibility(View.GONE);
        }
    }

    private void cvSearchBtn0() {
        if ((lvResults0.getVisibility() == View.VISIBLE) && ((!searchViewList0.get(0).getText().equals("") |
                (!searchViewList0.get(1).getText().equals("")) | (!searchViewList0.get(2).getText().equals(""))))
                && (cModel == 2)) {
            btnSearch0.setVisibility(View.VISIBLE);
        } else {
            btnSearch0.setVisibility(View.GONE);
        }
    }

    /**
     * 当搜索框文本改变时触发的回调 ,更新自动补全数据
     *
     * @param text 搜索栏文本
     */
    @Override
    public void onRefreshAutoComplete(String text) {
        //更新数据
        if (cModel == 1) {
            if (searchView.getText().equals("")) {
                searchView.setJUD(false);
            }
            if (searchView2.getText().equals("")) {
                searchView2.setJUD(false);
            }
            getAutoCompleteData(text);
            cvSearchBtn();
        } else {
            getAutoCompleteData0(text);
            cvSearchBtn0();
        }
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text 搜索栏文本
     */
    @Override
    public void onSearch(String text) {
        if (cModel == 1) {
            //更新result数据
            searchView.setJUD(false);
            searchView2.setJUD(false);
            getResultData(text);
            if (!searchView.getText().equals("")) {
                lvResults.setVisibility(View.VISIBLE);
            }
            if (!searchView2.getText().equals("")) {
                lvResults2.setVisibility(View.VISIBLE);
            }

            if (searchView.hasFocus()) {
                //第一次获取结果 还未配置适配器
                resultAdapter.getItem(0).setComments("起点");
                if (lvResults.getAdapter() == null) {
                    //获取搜索数据 设置适配器
                    lvResults.setAdapter(resultAdapter);
                } else {
                    //更新搜索数据
                    resultAdapter.notifyDataSetChanged();
                }
            }

            if (searchView2.hasFocus()) {
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
        } else {
            //更新result数据
            getResultData0(text);
            lvResults0.setVisibility(View.VISIBLE);

            //第一次获取结果 还未配置适配器
            if (endSearchView0.isFocus()) {
                if (lvResults0.getAdapter() == null) {
                    //获取搜索数据 设置适配器
                    resultAdapter0.getItem(0).setComments("终点");
                    lvResults0.setAdapter(resultAdapter0);
                } else {
                    //更新搜索数据
                    resultAdapter0.getItem(0).setComments("终点");
                    resultAdapter0.notifyDataSetChanged();
                }
            }
            cvSearchBtn0();
            Toast.makeText(getActivity(), "完成搜索", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击返回键触发回调
     */
    @Override
    public void onBack() {
        if (cModel == 1) {
            searchView.setJUD(true);
            searchView2.setJUD(true);
            Log.d(TAG, "onBack: start");
            if (searchView.getText().equals("")) {
                autoCompleteAdapter.notifyDataSetChanged();
                resultAdapter.notifyDataSetChanged();
                lvResults.setVisibility(View.GONE);
            }
            if (searchView2.getText().equals("")) {
                autoCompleteAdapter2.notifyDataSetChanged();
                resultAdapter2.notifyDataSetChanged();
                lvResults2.setVisibility(View.GONE);
            }
            hintAdapter.notifyDataSetChanged();
            cvSearchBtn();
            Log.d(TAG, "onBack: finish");
        } else {
            if (endSearchView0.hasFocus()) {
                autoCompleteAdapter00.notifyDataSetChanged();
                resultAdapter0.notifyDataSetChanged();
                lvResults0.setVisibility(View.GONE);
            }
            if (searchViewList0.get(0).hasFocus()) {
                autoCompleteAdapter01.notifyDataSetChanged();
            }
            if (searchViewList0.get(1).hasFocus()) {
                autoCompleteAdapter02.notifyDataSetChanged();
            }
            if (searchViewList0.get(2).hasFocus()) {
                autoCompleteAdapter03.notifyDataSetChanged();
            }
            hintAdapter0.notifyDataSetChanged();
            cvSearchBtn0();
        }
    }

    /**
     * 自动补全提示框出现触发回调
     */
    @Override
    public void isFocus() {
        if (cModel == 1) {
            searchView.setJUD(true);
            searchView2.setJUD(true);
            Log.d(TAG, "isFocus: start");
            if (searchView.hasFocus()) {
                autoCompleteAdapter.notifyDataSetChanged();
                resultAdapter.notifyDataSetChanged();
                lvResults.setVisibility(View.GONE);
            }
            if (searchView2.hasFocus()) {
                autoCompleteAdapter2.notifyDataSetChanged();
                resultAdapter2.notifyDataSetChanged();
                lvResults2.setVisibility(View.GONE);
            }
            hintAdapter.notifyDataSetChanged();
            cvSearchBtn();
            Log.d(TAG, "isFocus: finish");
        } else {
            if (endSearchView0.hasFocus()) {
                autoCompleteAdapter00.notifyDataSetChanged();
                resultAdapter0.notifyDataSetChanged();
                lvResults0.setVisibility(View.GONE);
            }
            if (searchViewList0.get(0).hasFocus()) {
                autoCompleteAdapter01.notifyDataSetChanged();
            }
            if (searchViewList0.get(1).hasFocus()) {
                autoCompleteAdapter02.notifyDataSetChanged();
            }
            if (searchViewList0.get(2).hasFocus()) {
                autoCompleteAdapter03.notifyDataSetChanged();
            }
            hintAdapter0.notifyDataSetChanged();
            cvSearchBtn0();
        }
    }

    /**
     * 热门提示数据在数据库中是否存在
     *
     * @param text 热门提示
     * @return true存在，false不存在
     */
    @Override
    public boolean onHintClick(String text) {
        if (cModel == 1) {
            searchView.setJUD(true);
            searchView2.setJUD(true);
            boolean JUD2 = false;
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().equals(text.trim())) {
                    JUD2 = true;
                }
            }
            hintAdapter.notifyDataSetChanged();
            cvSearchBtn();
            return JUD2;
        } else {
            boolean JUD = false;
            for (int i = 0; i < dbData0.size(); i++) {
                if (dbData0.get(i).getTitle().equals(text.trim())) {
                    JUD = true;
                }
            }
            hintAdapter0.notifyDataSetChanged();
            cvSearchBtn0();
            return JUD;
        }
    }
}
