package com.example.zero.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.zero.adapter.RouteDetailDialogAdapter;
import com.example.zero.adapter.RouteLineAdapter;
import com.example.zero.adapter.ScheduleAdapter;
import com.example.zero.adapter.ScheduleDialogAdapter;
import com.example.zero.bean.RouteDetailBean;
import com.example.zero.bean.RouteSearchBean;
import com.example.zero.bean.SaleBean;
import com.example.zero.bean.ScheduleBean;
import com.example.zero.entity.Route;
import com.example.zero.fragment.OverlayManager;
import com.example.zero.fragment.RouteFragmentDouble;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.BikingRouteOverlay;
import com.example.zero.util.CouponOverlay;
import com.example.zero.util.DrivingRouteOverlay;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MassTransitRouteOverlay;
import com.example.zero.util.RequestManager;
import com.example.zero.util.TransitRouteOverlay;
import com.example.zero.util.WalkingRouteOverlay;

import com.alibaba.fastjson.*;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteResultActivity extends AppCompatActivity implements BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener, SensorEventListener {

    private static final String TAG = "RouteResultActivity";
    // 路线节点
    // 浏览路线节点相关
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    MassTransitRouteLine massroute = null;
    OverlayManager routeOverlay = null;
    private TextView popupText = null; // 泡泡view

    MapView mMapView = null;// 地图View
    BaiduMap mBaidumap = null;

    // 搜索相关
    int JUD = 0; // 0单人，1多人
    String beginStation = null;
    String endStation = null;
    ArrayList<String> beginStationList = null;
    int beginNum = 0;

    // 搜索模块
    RoutePlanSearch mSearch = null;

    WalkingRouteResult nowResultwalk = null;
    BikingRouteResult nowResultbike = null;
    TransitRouteResult nowResultransit = null;
    DrivingRouteResult nowResultdrive = null;
    MassTransitRouteResult nowResultmass = null;

    // 定位相关
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    // UI相关
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    //周围优惠券查看
    private Button couponDisplayBtn;
    //替换站点
    private Button replaceBtn;
    //详细路线
    private Button detailBtn;

    // 路线节点
    private List<String> mPathList = new ArrayList<String>();

    //前后端接口
    //单人
    private ArrayList<String> fastStationList = new ArrayList<String>();
    private ArrayList<String> lessbusyStationList = new ArrayList<String>();
    private ArrayList<String> lesschangeStationList = new ArrayList<String>();
    private ArrayList<String> fastRouteList = new ArrayList<String>();
    private ArrayList<String> lessbusyRouteList = new ArrayList<String>();
    private ArrayList<String> lesschangeRouteList = new ArrayList<String>();
    private ArrayList<String> sellerList = new ArrayList<String>();
    private ArrayList<List<TransitRouteLine>> modelSelect = new ArrayList<List<TransitRouteLine>>();
    private ArrayList<String> fastStationDetailList = new ArrayList<String>();
    private ArrayList<String> lessbusyStationDetailList = new ArrayList<String>();
    private ArrayList<String> lesschangeStationDetailList = new ArrayList<String>();

    private double[] fastSellerLatList;
    private double[] fastSellerLngList;
    private double[] lessbusySellerLatList;
    private double[] lessbusySellerLngList;
    private double[] lesschangeSellerLatList;
    private double[] lesschangeSellerLngList;

    private int fastCount;
    private int lessbusyCount;
    private int lesschangeCount;

    private LatLng stNode;
    private LatLng edNode;

    private LinearLayout singleBtn = null;
    private Button fastBtn = null;
    private Button lessbusyBtn = null;
    private Button lesschangeBtn = null;

    private String stationName;

    private String replace;
    private ArrayList<String> busyList = new ArrayList<String>();
    private int busyCount;
    private double[] busyLngList;
    private double[] busyLatList;
    //多人
    private ArrayList<String> stationList1 = new ArrayList<String>();
    private ArrayList<String> stationList2 = new ArrayList<String>();
    private ArrayList<String> stationList3 = new ArrayList<String>();
    private ArrayList<String> routeList1 = new ArrayList<String>();
    private ArrayList<String> routeList2 = new ArrayList<String>();
    private ArrayList<String> routeList3 = new ArrayList<String>();
    private ArrayList<String> stationAfMeetList = new ArrayList<String>();
    private ArrayList<String> routeAfMeetList = new ArrayList<String>();

    private ArrayList<String> stationDetailList1 = new ArrayList<String>();
    private ArrayList<String> stationDetailList2 = new ArrayList<String>();
    private ArrayList<String> stationDetailList3 = new ArrayList<String>();
    private ArrayList<String> routeDetailList1 = new ArrayList<String>();
    private ArrayList<String> routeDetailList2 = new ArrayList<String>();
    private ArrayList<String> routeDetailList3 = new ArrayList<String>();
    private ArrayList<String> stationAfMeetDetailList = new ArrayList<String>();
    private ArrayList<String> routeAfMeetDetailList = new ArrayList<String>();

    private double[] firstSellerLatList;
    private double[] firstSellerLngList;
    private double[] secondSellerLatList;
    private double[] secondSellerLngList;
    private double[] thirdSellerLatList;
    private double[] thirdSellerLngList;
    private double[] afMeetSellerLatList;
    private double[] afMeetSellerLngList;

    private int firstCount;
    private int secondCount;
    private int thirdCount;
    private int afMeetCount;

    private LatLng stNodeM1;
    private LatLng stNodeM2;
    private LatLng stNodeM3;
    private LatLng edNodeM;

    private String[] multiStationList;

    //建议
    private LinearLayout textAdviceLayout = null;
    private TextView textAdvice = null;

    private ArrayList<String> stationList = new ArrayList<String>();
    private ArrayList<String> routeList = new ArrayList<String>();
    private ArrayList<String> stationDetailList = new ArrayList<String>();
    private ArrayList<String> routeDetailList = new ArrayList<String>();

    private double[] sellerLatList;
    private double[] sellerLngList;

    private int count;

    private Context context;

    private int stedFlag = 0;

    private Route.RouteType cModel;

    int nowSearchType = -1; // 当前节点

    boolean hasShownDialogue = false;

    private android.support.v7.app.AlertDialog selectDialog;

    private RecyclerView schedule;

    private ScheduleDialogAdapter scheduleDialogAdapter;
    private List<ScheduleBean> scheduleData;

    private RouteDetailDialogAdapter routeDetailDialogAdapter;
    private List<RouteDetailBean> fastRouteDetailBeanList;
    private List<RouteDetailBean> lessbusyRouteDetailBeanList;
    private List<RouteDetailBean> lesschangeRouteDetailBeanList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_result);
        Intent intent = getIntent();
        Bundle mBundle = intent.getExtras();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        context = RouteResultActivity.this;

        CharSequence titleLable = "路线";
        setTitle(titleLable);

        //定位
        requestLocButton = (Button) findViewById(R.id.route_result_location);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        Toast.makeText(RouteResultActivity.this, "当前模式:跟随", Toast.LENGTH_SHORT).show();
                        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        Toast.makeText(RouteResultActivity.this, "当前模式:正常", Toast.LENGTH_SHORT).show();
                        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        Toast.makeText(RouteResultActivity.this, "当前模式:罗盘", Toast.LENGTH_SHORT).show();
                        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);

        // 初始化地图
        mMapView = (MapView) findViewById(R.id.route_result_map);
        mMapView.removeViewAt(1);
        mBaidumap = mMapView.getMap();
        //设定中心点坐标
        LatLng cenpt = new LatLng(23.16, 113.23);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(14)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaidumap.setMapStatus(mMapStatusUpdate);

        // 开启定位图层
        mBaidumap.setMyLocationEnabled(true);
        //POI说明关闭
//        mBaidumap.showMapPoi(false);
//        mBaidumap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//        mBaidumap.setTrafficEnabled(true);

        mBtnPre = (Button) findViewById(R.id.route_result_pre);
        mBtnNext = (Button) findViewById(R.id.route_result_next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        mBaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getExtraInfo().getBoolean("isMarker")) {
                    // TODO: 2017/11/18 列车时刻表弹出窗口
                    scheduleData = new ArrayList<ScheduleBean>();

                    final Bundle mBundle = new Bundle();
                    mBundle.putString("station", marker.getTitle());
                    HttpUtil.sendScheduleOkHttpRequest(mBundle, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            parseJSONWithJSONObject(responseData);

                            scheduleDialogAdapter = new ScheduleDialogAdapter(context, scheduleData, R.layout.dialog_schedule);
                            Looper.prepare();
                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("列车时刻表")
                                    .setView(R.layout.dialog_content_circle)
                                    .setAdapter(scheduleDialogAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(context, scheduleData.get(i).getStation(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create();

                            Window window = dialog.getWindow();
                            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
                            window.setWindowAnimations(R.style.popWindow);  //添加动画
                            dialog.show();

                            WindowManager m = getWindowManager();
                            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                            WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
                            p.width = d.getWidth(); // 宽度设置为屏幕宽
                            window.setAttributes(p);
                            Looper.loop();
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: ERROR!");
                            Toast.makeText(context, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return false;
            }
        });

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        replaceBtn = (Button) findViewById(R.id.route_replace);
        replaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(RouteResultActivity.this)
                        .setTitle("可替换车站")
                        .setMessage(replace)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });

        detailBtn = (Button) findViewById(R.id.route_detail);
        fastRouteDetailBeanList = new ArrayList<RouteDetailBean>();
        lessbusyRouteDetailBeanList = new ArrayList<RouteDetailBean>();
        lesschangeRouteDetailBeanList = new ArrayList<RouteDetailBean>();
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (cModel) {
                    case FAST:
                        routeDetailDialogAdapter = new RouteDetailDialogAdapter(context, fastRouteDetailBeanList, R.layout.dialog_route_detail);
                        new AlertDialog.Builder(context)
                                .setTitle("路线详情")
                                .setView(R.layout.dialog_content_circle)
                                .setAdapter(routeDetailDialogAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context, fastRouteDetailBeanList.get(i).getStation(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                        break;
                    case LESSBUSY:
                        routeDetailDialogAdapter = new RouteDetailDialogAdapter(context, lessbusyRouteDetailBeanList, R.layout.dialog_route_detail);
                        new AlertDialog.Builder(context)
                                .setTitle("路线详情")
                                .setView(R.layout.dialog_content_circle)
                                .setAdapter(routeDetailDialogAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context, lessbusyRouteDetailBeanList.get(i).getStation(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                        break;
                    case LESSCHANGE:
                        routeDetailDialogAdapter = new RouteDetailDialogAdapter(context, lesschangeRouteDetailBeanList, R.layout.dialog_route_detail);
                        new AlertDialog.Builder(context)
                                .setTitle("路线详情")
                                .setView(R.layout.dialog_content_circle)
                                .setAdapter(routeDetailDialogAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context, lesschangeRouteDetailBeanList.get(i).getStation(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                        break;
                    default:
                        break;
                }
            }
        });

        couponDisplayBtn = (Button) findViewById(R.id.route_coupon_display);
        couponDisplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017/11/4 站点周围优惠信息展示
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setIcon(R.drawable.android);
                builder.setTitle("请选择查看站点");
                String[] scene = {"广州塔", "体育西路", "广州东站", "金洲"};
                switch (cModel) {
                    case FAST:
                        scene = (String[]) fastStationDetailList.toArray(new String[fastStationDetailList.size()]);
                        break;
                    case LESSBUSY:
                        scene = (String[]) lessbusyStationDetailList.toArray(new String[lessbusyStationDetailList.size()]);
                        break;
                    case LESSCHANGE:
                        scene = (String[]) lesschangeStationDetailList.toArray(new String[lesschangeStationDetailList.size()]);
                        break;
                    case MULTI:
                        //去重
                        Set<String> set = new HashSet<>();
                        for (int i = 0; i < multiStationList.length; i++) {
                            set.add(multiStationList[i]);
                        }
                        String[] arrayResult = (String[]) set.toArray(new String[set.size()]);
                        scene = (String[]) arrayResult;
                        break;
                    case ADVICE:
                        scene = (String[]) stationDetailList.toArray(new String[stationDetailList.size()]);
                        break;
                    default:
                        break;
                }

                final String[] finalScene = scene;
                stationName = finalScene[0];

                /**
                 * 设置一个单项选择下拉框
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                builder.setSingleChoiceItems(finalScene, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stationName = finalScene[which];
                        Toast.makeText(context, "选择了" + stationName + "站", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectDialog.dismiss();
                        Intent intent = new Intent(RouteResultActivity.this, StationDisplayActivity.class);
                        intent.putExtra("stationName", stationName);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                selectDialog = builder.create();
                selectDialog.show();
            }
        });

        singleBtn = (LinearLayout) findViewById(R.id.route_result_single_btn);

        fastBtn = (Button) findViewById(R.id.fastStation);
        lessbusyBtn = (Button) findViewById(R.id.lessbusyStation);
        lesschangeBtn = (Button) findViewById(R.id.lesschangeStation);

        textAdviceLayout = (LinearLayout) findViewById(R.id.route_advice_text_layout);
        textAdvice = (TextView) findViewById(R.id.route_advice_text);

        if (mBundle.getString("origin").equals("Single")) {
            singleBtn.setVisibility(View.VISIBLE);
            fastBtn.setVisibility(View.VISIBLE);
            lessbusyBtn.setVisibility(View.VISIBLE);
            lesschangeBtn.setVisibility(View.VISIBLE);
            textAdviceLayout.setVisibility(View.GONE);
            textAdvice.setVisibility(View.GONE);

            fastStationList = mBundle.getStringArrayList("fastStationList");
            lessbusyStationList = mBundle.getStringArrayList("lessbusyStationList");
            lesschangeStationList = mBundle.getStringArrayList("lesschangeStationList");

            fastRouteList = mBundle.getStringArrayList("fastRouteList");
            lessbusyRouteList = mBundle.getStringArrayList("lessbusyRouteList");
            lesschangeRouteList = mBundle.getStringArrayList("lesschangeRouteList");

            fastStationDetailList = mBundle.getStringArrayList("fastStationDetailList");
            lessbusyStationDetailList = mBundle.getStringArrayList("lessbusyStationDetailList");
            lesschangeStationDetailList = mBundle.getStringArrayList("lesschangeStationDetailList");

            fastSellerLatList = mBundle.getDoubleArray("fastSellerLatList");
            fastSellerLngList = mBundle.getDoubleArray("fastSellerLngList");
            lessbusySellerLatList = mBundle.getDoubleArray("lessbusySellerLatList");
            lessbusySellerLngList = mBundle.getDoubleArray("lessbusySellerLngList");
            lesschangeSellerLatList = mBundle.getDoubleArray("lesschangeSellerLatList");
            lesschangeSellerLngList = mBundle.getDoubleArray("lesschangeSellerLngList");

            fastCount = mBundle.getInt("fastCount");
            lessbusyCount = mBundle.getInt("lessbusyCount");
            lesschangeCount = mBundle.getInt("lesschangeCount");

            replace = mBundle.getString("replace_plan");

            busyList = mBundle.getStringArrayList("busyList");
            busyCount = mBundle.getInt("busyCount");
            busyLngList = mBundle.getDoubleArray("busyLngList");
            busyLatList = mBundle.getDoubleArray("busyLatList");

            for (int i = 0; i < fastStationList.size() - 1; i++) {
                fastRouteDetailBeanList.add(new RouteDetailBean(fastStationList.get(i), fastRouteList.get(i), fastStationList.get(i + 1)));
            }
            for (int i = 0; i < lessbusyStationList.size() - 1; i++) {
                lessbusyRouteDetailBeanList.add(new RouteDetailBean(lessbusyStationList.get(i), lessbusyRouteList.get(i), lessbusyStationList.get(i + 1)));
            }
            for (int i = 0; i < lesschangeStationList.size() - 1; i++) {
                lesschangeRouteDetailBeanList.add(new RouteDetailBean(lesschangeStationList.get(i), lesschangeRouteList.get(i), lesschangeStationList.get(i + 1)));
            }

            JUD = 2;

            fastBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detailBtn.setVisibility(View.VISIBLE);
                    replaceBtn.setVisibility(View.VISIBLE);
                    couponDisplayBtn.setVisibility(View.VISIBLE);
                    cModel = Route.RouteType.FAST;
                    mBaidumap.clear();
                    TransitRoutePlanOption transitRouteFast = new TransitRoutePlanOption();
                    transitRouteFast.mPolicy = TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST;
                    if (fastStationList.size() > 1) {
                        for (int i = 0; i < fastStationList.size() - 1; i++) {
                            if (i == 0) {
                                stedFlag = 1;
                            } else if (i == fastStationList.size() - 2) {
                                stedFlag = 2;
                            } else {
                                stedFlag = 0;
                            }
                            mSearch.transitSearch(transitRouteFast
                                    .from(PlanNode.withCityNameAndPlaceName("广州", fastStationList.get(i) + "地铁站"))
                                    .city("广州")
                                    .to(PlanNode.withCityNameAndPlaceName("广州", fastStationList.get(i + 1) + "地铁站")));
                        }
                        addMarker();
                        addBusy();
                        addReplace();

                        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(11);
                        mBaidumap.animateMapStatus(mapStatusUpdate);
                    } else {
                        Toast.makeText(RouteResultActivity.this, "路线结果太少，请重新搜索。", Toast.LENGTH_SHORT).show();
                    }
                    nowSearchType = 2;
                }
            });
            lessbusyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detailBtn.setVisibility(View.VISIBLE);
                    replaceBtn.setVisibility(View.VISIBLE);
                    couponDisplayBtn.setVisibility(View.VISIBLE);
                    cModel = Route.RouteType.LESSBUSY;
                    mBaidumap.clear();

                    TransitRoutePlanOption transitRouteLessbusy = new TransitRoutePlanOption();
                    transitRouteLessbusy.mPolicy = TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST;
                    if (lessbusyStationList.size() > 1) {
                        for (int i = 0; i < lessbusyStationList.size() - 1; i++) {
                            if (i == 0) {
                                stedFlag = 1;
                            } else if (i == lessbusyStationList.size() - 2) {
                                stedFlag = 2;
                            } else {
                                stedFlag = 0;
                            }
                            mSearch.transitSearch(transitRouteLessbusy
                                    .from(PlanNode.withCityNameAndPlaceName("广州", lessbusyStationList.get(i) + "地铁站"))
                                    .city("广州")
                                    .to(PlanNode.withCityNameAndPlaceName("广州", lessbusyStationList.get(i + 1) + "地铁站")));
                        }
                        addMarker();
                        addBusy();
                        addReplace();

                        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(11);
                        mBaidumap.animateMapStatus(mapStatusUpdate);
                    } else {
                        Toast.makeText(RouteResultActivity.this, "路线结果太少，请重新搜索。", Toast.LENGTH_SHORT).show();
                    }
                    nowSearchType = 2;
                }
            });
            lesschangeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detailBtn.setVisibility(View.VISIBLE);
                    replaceBtn.setVisibility(View.VISIBLE);
                    couponDisplayBtn.setVisibility(View.VISIBLE);
                    cModel = Route.RouteType.LESSCHANGE;
                    mBaidumap.clear();

                    TransitRoutePlanOption transitRouteLesschange = new TransitRoutePlanOption();
                    transitRouteLesschange.mPolicy = TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST;
                    if (lesschangeStationList.size() > 1) {
                        for (int i = 0; i < lesschangeStationList.size() - 1; i++) {
                            if (i == 0) {
                                stedFlag = 1;
                            } else if (i == lesschangeStationList.size() - 2) {
                                stedFlag = 2;
                            } else {
                                stedFlag = 0;
                            }
                            mSearch.transitSearch(transitRouteLesschange
                                    .from(PlanNode.withCityNameAndPlaceName("广州", lesschangeStationList.get(i) + "地铁站"))
                                    .city("广州")
                                    .to(PlanNode.withCityNameAndPlaceName("广州", lesschangeStationList.get(i + 1) + "地铁站")));
                        }
                        addMarker();
                        addBusy();
                        addReplace();

                        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(11);
                        mBaidumap.animateMapStatus(mapStatusUpdate);
                    } else {
                        Toast.makeText(RouteResultActivity.this, "路线结果太少，请重新搜索。", Toast.LENGTH_SHORT).show();
                    }
                    nowSearchType = 2;
                }
            });
        } else if (mBundle.getString("origin").equals("Multi")) {
            singleBtn.setVisibility(View.GONE);
            fastBtn.setVisibility(View.GONE);
            lessbusyBtn.setVisibility(View.GONE);
            lesschangeBtn.setVisibility(View.GONE);
            textAdviceLayout.setVisibility(View.GONE);
            textAdvice.setVisibility(View.GONE);
            cModel = Route.RouteType.MULTI;

//            replaceBtn.setVisibility(View.VISIBLE);
            couponDisplayBtn.setVisibility(View.VISIBLE);

            stationList1 = mBundle.getStringArrayList("stationList1");
            stationList2 = mBundle.getStringArrayList("stationList2");
            stationList3 = mBundle.getStringArrayList("stationList3");
            routeList1 = mBundle.getStringArrayList("routeList1");
            routeList2 = mBundle.getStringArrayList("routeList2");
            routeList3 = mBundle.getStringArrayList("routeList3");
            stationAfMeetList = mBundle.getStringArrayList("stationAfMeetList");
            routeAfMeetList = mBundle.getStringArrayList("routeAfMeetList");

            stationDetailList1 = mBundle.getStringArrayList("stationDetailList1");
            stationDetailList2 = mBundle.getStringArrayList("stationDetailList2");
            stationDetailList3 = mBundle.getStringArrayList("stationDetailList3");
            routeDetailList1 = mBundle.getStringArrayList("routeDetailList1");
            routeDetailList2 = mBundle.getStringArrayList("routeDetailList2");
            routeDetailList3 = mBundle.getStringArrayList("routeDetailList3");
            stationAfMeetDetailList = mBundle.getStringArrayList("stationAfMeetDetailList");
            routeAfMeetDetailList = mBundle.getStringArrayList("routeAfMeetDetailList");

            multiStationList = concatAll(stationDetailList1.toArray(new String[stationDetailList1.size()]),
                    stationDetailList2.toArray(new String[stationDetailList2.size()]),
                    stationDetailList3.toArray(new String[stationDetailList3.size()]),
                    stationAfMeetDetailList.toArray(new String[stationAfMeetDetailList.size()]));

            firstSellerLatList = mBundle.getDoubleArray("firstSellerLatList");
            firstSellerLngList = mBundle.getDoubleArray("firstSellerLngList");
            secondSellerLatList = mBundle.getDoubleArray("secondSellerLatList");
            secondSellerLngList = mBundle.getDoubleArray("secondSellerLngList");
            thirdSellerLatList = mBundle.getDoubleArray("thirdSellerLatList");
            thirdSellerLngList = mBundle.getDoubleArray("thirdSellerLngList");
            afMeetSellerLatList = mBundle.getDoubleArray("afMeetSellerLatList");
            afMeetSellerLngList = mBundle.getDoubleArray("afMeetSellerLngList");

            firstCount = mBundle.getInt("firstCount");
            secondCount = mBundle.getInt("secondCount");
            thirdCount = mBundle.getInt("thirdCount");
            afMeetCount = mBundle.getInt("afMeetCount");

            TransitRoutePlanOption transitRouteFast = new TransitRoutePlanOption();
            transitRouteFast.mPolicy = TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST;
            if (stationList1.size() > 1) {
                for (int i = 0; i < stationList1.size() - 1; i++) {
                    if (i == 0) {
                        stedFlag = 1;
                    } else {
                        stedFlag = 0;
                    }
                    mSearch.transitSearch(transitRouteFast
                            .from(PlanNode.withCityNameAndPlaceName("广州", stationList1.get(i)))
                            .city("广州")
                            .to(PlanNode.withCityNameAndPlaceName("广州", stationList1.get(i + 1))));
                }
            }
            if (stationList2.size() > 1) {
                for (int i = 0; i < stationList2.size() - 1; i++) {
                    if (i == 0) {
                        stedFlag = 1;
                    } else {
                        stedFlag = 0;
                    }
                    mSearch.transitSearch(transitRouteFast
                            .from(PlanNode.withCityNameAndPlaceName("广州", stationList2.get(i)))
                            .city("广州")
                            .to(PlanNode.withCityNameAndPlaceName("广州", stationList2.get(i + 1))));
                }
            }
            if (stationList3.size() > 1) {
                for (int i = 0; i < stationList3.size() - 1; i++) {
                    if (i == 0) {
                        stedFlag = 1;
                    } else {
                        stedFlag = 0;
                    }
                    mSearch.transitSearch(transitRouteFast
                            .from(PlanNode.withCityNameAndPlaceName("广州", stationList3.get(i)))
                            .city("广州")
                            .to(PlanNode.withCityNameAndPlaceName("广州", stationList3.get(i + 1))));
                }
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(12);
                mBaidumap.animateMapStatus(mapStatusUpdate);
            }
            if (stationAfMeetList.size() > 1) {
                for (int i = 0; i < stationAfMeetList.size() - 1; i++) {
                    if (i == stationAfMeetList.size() - 2) {
                        stedFlag = 2;
                    } else {
                        stedFlag = 0;
                    }
                    mSearch.transitSearch(transitRouteFast
                            .from(PlanNode.withCityNameAndPlaceName("广州", stationAfMeetList.get(i)))
                            .city("广州")
                            .to(PlanNode.withCityNameAndPlaceName("广州", stationAfMeetList.get(i + 1))));
                }
            }
            addMarker();

            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(14);
            mBaidumap.animateMapStatus(mapStatusUpdate);
            nowSearchType = 2;
            JUD = 1;
        } else if (mBundle.getString("origin").equals("Advice")) {
            singleBtn.setVisibility(View.GONE);
            fastBtn.setVisibility(View.GONE);
            lessbusyBtn.setVisibility(View.GONE);
            lesschangeBtn.setVisibility(View.GONE);
            textAdviceLayout.setVisibility(View.VISIBLE);
            textAdvice.setVisibility(View.VISIBLE);
            cModel = Route.RouteType.ADVICE;

//            replaceBtn.setVisibility(View.VISIBLE);
            couponDisplayBtn.setVisibility(View.VISIBLE);

            JUD = 1;
            textAdvice.setText("建议您出行的时间：" + mBundle.getString("time"));

            stationList = mBundle.getStringArrayList("stationList");
            stationDetailList = mBundle.getStringArrayList("stationDetailList");
            sellerLatList = mBundle.getDoubleArray("sellerLatList");
            sellerLngList = mBundle.getDoubleArray("sellerLngList");

            count = mBundle.getInt("count");

            mBaidumap.clear();
            TransitRoutePlanOption transitRouteFast = new TransitRoutePlanOption();
            transitRouteFast.mPolicy = TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST;
            if (stationList.size() > 1) {
                for (int i = 0; i < stationList.size() - 1; i++) {
                    if (i == 0) {
                        stedFlag = 1;
                    } else if (i == stationList.size() - 2) {
                        stedFlag = 2;
                    } else {
                        stedFlag = 0;
                    }
                    mSearch.transitSearch(transitRouteFast
                            .from(PlanNode.withCityNameAndPlaceName("广州", stationList.get(i)))
                            .city("广州")
                            .to(PlanNode.withCityNameAndPlaceName("广州", stationList.get(i + 1))));
                }
                addMarker();

                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(12);
                mBaidumap.animateMapStatus(mapStatusUpdate);
            } else {
                Toast.makeText(RouteResultActivity.this, "路线结果太少，请重新搜索。", Toast.LENGTH_SHORT).show();
            }
            nowSearchType = 2;
        }
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            int showBound = 2;
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                for (int j = 0; j < showBound; j++) {
                    scheduleData.add(new ScheduleBean(array.getJSONObject(j).getString("station"),
                            array.getJSONObject(j).getString("line"),
                            array.getJSONObject(j).getString("final_st"),
                            array.getJSONObject(j).getString("arr_time_str")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private void addstEdNode() {
//        HashMap<String, String> params = new HashMap<>();
//        switch (cModel) {
//            case FAST:
//                params.clear();
//                params.put("address", "广州" + fastStationList.get(0) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    stNode = new LatLng(lat, lng);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                params.clear();
//                params.put("address", "广州" + fastStationList.get(fastStationList.size() - 1) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    edNode = new LatLng(lat, lng);
//                                    addMarker();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                break;
//
//            case LESSBUSY:
//                params.clear();
//                params.put("address", "广州" + lessbusyStationList.get(0) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    stNode = new LatLng(lat, lng);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                params.clear();
//                params.put("address", "广州" + lessbusyStationList.get(lessbusyStationList.size() - 1) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    edNode = new LatLng(lat, lng);
//                                    addMarker();
//                                } catch (Exception e) {
//                                    addMarker();
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                addMarker();
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                break;
//
//            case LESSCHANGE:
//                params.clear();
//                params.put("address", "广州" + lesschangeStationList.get(0) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    stNode = new LatLng(lat, lng);
//                                } catch (Exception e) {
//                                    addMarker();
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                addMarker();
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                params.clear();
//                params.put("address", "广州" + lesschangeStationList.get(lesschangeStationList.size() - 1) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    edNode = new LatLng(lat, lng);
//                                    addMarker();
//                                } catch (Exception e) {
//                                    addMarker();
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                addMarker();
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                break;
//
//            case MULTI:
//                if (stationList1.size() > 0) {
//                    params.clear();
//                    params.put("address", "广州" + stationList1.get(0) + "地铁站");
//                    params.put("output", "json");
//                    params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                    params.put("callback", "0");
//                    RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                            RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                                @Override
//                                public void onReqSuccess(String result) {
//                                    try {
//                                        JSONObject jsonData = new JSONObject(result);
//
//                                        JSONObject res = jsonData.getJSONObject("result");
//                                        JSONObject location = res.getJSONObject("location");
//
//                                        double lat = location.getDouble("lat");
//                                        double lng = location.getDouble("lng");
//                                        stNodeM1 = new LatLng(lat, lng);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onReqFailed(String errorMsg) {
//                                    Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//                if (stationList2.size() > 0) {
//                    params.clear();
//                    params.put("address", "广州" + stationList2.get(0) + "地铁站");
//                    params.put("output", "json");
//                    params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                    params.put("callback", "0");
//                    RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                            RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                                @Override
//                                public void onReqSuccess(String result) {
//                                    try {
//                                        JSONObject jsonData = new JSONObject(result);
//
//                                        JSONObject res = jsonData.getJSONObject("result");
//                                        JSONObject location = res.getJSONObject("location");
//
//                                        double lat = location.getDouble("lat");
//                                        double lng = location.getDouble("lng");
//                                        stNodeM2 = new LatLng(lat, lng);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onReqFailed(String errorMsg) {
//                                    Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//                if (stationList3.size() > 0) {
//                    params.clear();
//                    params.put("address", "广州" + stationList3.get(0) + "地铁站");
//                    params.put("output", "json");
//                    params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                    params.put("callback", "0");
//                    RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                            RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                                @Override
//                                public void onReqSuccess(String result) {
//                                    try {
//                                        JSONObject jsonData = new JSONObject(result);
//
//                                        JSONObject res = jsonData.getJSONObject("result");
//                                        JSONObject location = res.getJSONObject("location");
//
//                                        double lat = location.getDouble("lat");
//                                        double lng = location.getDouble("lng");
//                                        stNodeM3 = new LatLng(lat, lng);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onReqFailed(String errorMsg) {
//                                    Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//                if (stationAfMeetList.size() > 0) {
//                    params.clear();
//                    params.put("address", "广州" + stationAfMeetList.get(stationAfMeetList.size() - 1) + "地铁站");
//                    params.put("output", "json");
//                    params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                    params.put("callback", "0");
//                    RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                            RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                                @Override
//                                public void onReqSuccess(String result) {
//                                    try {
//                                        JSONObject jsonData = new JSONObject(result);
//
//                                        JSONObject res = jsonData.getJSONObject("result");
//                                        JSONObject location = res.getJSONObject("location");
//
//                                        double lat = location.getDouble("lat");
//                                        double lng = location.getDouble("lng");
//                                        edNodeM = new LatLng(lat, lng);
//                                        addMarker();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onReqFailed(String errorMsg) {
//                                    Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                } else {
//                    params.clear();
//                    params.put("address", "广州" + stationList1.get(stationList1.size() - 1) + "地铁站");
//                    params.put("output", "json");
//                    params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                    params.put("callback", "0");
//                    RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                            RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                                @Override
//                                public void onReqSuccess(String result) {
//                                    try {
//                                        JSONObject jsonData = new JSONObject(result);
//
//                                        JSONObject res = jsonData.getJSONObject("result");
//                                        JSONObject location = res.getJSONObject("location");
//
//                                        double lat = location.getDouble("lat");
//                                        double lng = location.getDouble("lng");
//                                        edNodeM = new LatLng(lat, lng);
//                                        addMarker();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onReqFailed(String errorMsg) {
//                                    Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//
//            case ADVICE:
//                params.clear();
//                params.put("address", "广州" + stationList.get(0) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    stNode = new LatLng(lat, lng);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                params.clear();
//                params.put("address", "广州" + stationList.get(stationList.size() - 1) + "地铁站");
//                params.put("output", "json");
//                params.put("ak", "5r5Yuqvnb9NQGoiGLszs9SvMT32r1bBn");
//                params.put("callback", "0");
//                RequestManager.getInstance(RouteResultActivity.this).requestAsyn("http://api.map.baidu.com/geocoder/v2/",
//                        RequestManager.TYPE_GET_Z, params, new RequestManager.ReqCallBack<String>() {
//                            @Override
//                            public void onReqSuccess(String result) {
//                                try {
//                                    JSONObject jsonData = new JSONObject(result);
//
//                                    JSONObject res = jsonData.getJSONObject("result");
//                                    JSONObject location = res.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//                                    edNode = new LatLng(lat, lng);
//                                    addMarker();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onReqFailed(String errorMsg) {
//                                Toast.makeText(RouteResultActivity.this, "起终点经纬度请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                break;
//
//            default:
//                break;
//        }
    }

    private void addMarker() {
        //图标
        BitmapDescriptor stS = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_st);
        BitmapDescriptor enS = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_en);
        BitmapDescriptor pop = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);

        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        switch (cModel) {
            case FAST:
                //seller
                options.clear();
                for (int i = 0; i < fastCount; i++) {
                    if (fastSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(fastSellerLatList[i], fastSellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                break;

            case LESSBUSY:
                //seller
                options.clear();
                for (int i = 0; i < lessbusyCount; i++) {
                    if (lessbusySellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(lessbusySellerLatList[i], lessbusySellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                break;

            case LESSCHANGE:
                //seller
                options.clear();
                for (int i = 0; i < lesschangeCount; i++) {
                    if (lesschangeSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(lesschangeSellerLatList[i], lesschangeSellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                break;

            case MULTI:
                //seller
                options.clear();
                for (int i = 0; i < firstCount; i++) {
                    if (fastSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(firstSellerLatList[i], firstSellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                for (int i = 0; i < secondCount; i++) {
                    if (fastSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(secondSellerLatList[i], secondSellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                for (int i = 0; i < thirdCount; i++) {
                    if (fastSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(thirdSellerLatList[i], thirdSellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                for (int i = 0; i < afMeetCount; i++) {
                    if (fastSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(afMeetSellerLatList[i], afMeetSellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                break;

            case ADVICE:
                //seller
                options.clear();
                for (int i = 0; i < count; i++) {
                    if (fastSellerLatList[i] != 0.0) {
                        Marker mMarker;
                        MarkerOptions ooA = new MarkerOptions()
                                .position(new LatLng(sellerLatList[i], sellerLngList[i]))
                                .icon(pop)
                                .zIndex(9)
                                .draggable(true);
                        // 掉下动画
                        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                        mMarker = (Marker) (mBaidumap.addOverlay(ooA));
                    }
                }
                break;

            default:
                Toast.makeText(context, "无掉落动画", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void addBusy() {
        for (int i = 0; i < busyCount; i++) {
            String busyStation = busyList.get(i);
            LatLng nodeLocation = new LatLng(busyLatList[i], busyLngList[i]);
            popupText = new TextView(RouteResultActivity.this);
            popupText.setBackgroundResource(R.drawable.popup);
            popupText.setTextColor(0xFF000000);
            popupText.setText(busyStation + "繁忙");
            mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
        }
    }

    private void addReplace() {
        replace = replace.replace(",", "\n");
    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */

    public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();

        // 处理搜索按钮响应
        if (JUD == 0) {
            PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", beginStation);
            PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", endStation);

            if (v.getId() == R.id.mass) {
                PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("北京", "天安门");
                PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("上海", "东方明珠");

                mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));
                nowSearchType = 0;
            } else if (v.getId() == R.id.drive) {
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode).to(enNode));
                nowSearchType = 1;
            } else if (v.getId() == R.id.transit) {
                mSearch.transitSearch((new TransitRoutePlanOption())
                        .from(stNode).city("广州").to(enNode));
                nowSearchType = 2;
            } else if (v.getId() == R.id.walk) {
                mSearch.walkingSearch((new WalkingRoutePlanOption())
                        .from(stNode).to(enNode));
                nowSearchType = 3;
            } else if (v.getId() == R.id.bike) {
                mSearch.bikingSearch((new BikingRoutePlanOption())
                        .from(stNode).to(enNode));
                nowSearchType = 4;
            }
        } else {
            ArrayList<PlanNode> stNodeList = new ArrayList<PlanNode>();
            PlanNode enNode = PlanNode.withCityNameAndPlaceName("广州", endStation);
            stNodeList.add(PlanNode.withCityNameAndPlaceName("广州", beginStationList.get(0)));
            stNodeList.add(PlanNode.withCityNameAndPlaceName("广州", beginStationList.get(1)));
            stNodeList.add(PlanNode.withCityNameAndPlaceName("广州", beginStationList.get(2)));

            if (v.getId() == R.id.mass) {
                Toast.makeText(RouteResultActivity.this, "抱歉，多人界面不提供该功能", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.drive) {
                DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
                if (!beginStationList.get(0).equals("")) {
                    mSearch.drivingSearch(drivingRoutePlanOption
                            .from(stNodeList.get(0)).to(enNode));
                }
                if (!beginStationList.get(1).equals("")) {
                    mSearch.drivingSearch(drivingRoutePlanOption
                            .from(stNodeList.get(1)).to(enNode));
                }
                if (!beginStationList.get(2).equals("")) {
                    mSearch.drivingSearch(drivingRoutePlanOption
                            .from(stNodeList.get(2)).to(enNode));
                }
                nowSearchType = 1;
            } else if (v.getId() == R.id.transit) {
                TransitRoutePlanOption transitRoutePlanOption = new TransitRoutePlanOption();
                if (!beginStationList.get(0).equals("")) {
                    mSearch.transitSearch(transitRoutePlanOption
                            .from(stNodeList.get(0)).city("广州").to(enNode));
                }
                if (!beginStationList.get(1).equals("")) {
                    mSearch.transitSearch(transitRoutePlanOption
                            .from(stNodeList.get(1)).city("广州").to(enNode));
                }
                if (!beginStationList.get(2).equals("")) {
                    mSearch.transitSearch(transitRoutePlanOption
                            .from(stNodeList.get(2)).city("广州").to(enNode));
                }
                nowSearchType = 2;
            } else if (v.getId() == R.id.walk) {
                WalkingRoutePlanOption walkingRoutePlanOption = new WalkingRoutePlanOption();
                if (!beginStationList.get(0).equals("")) {
                    mSearch.walkingSearch(walkingRoutePlanOption
                            .from(stNodeList.get(0)).to(enNode));
                }
                if (!beginStationList.get(1).equals("")) {
                    mSearch.walkingSearch(walkingRoutePlanOption
                            .from(stNodeList.get(1)).to(enNode));
                }
                if (!beginStationList.get(2).equals("")) {
                    mSearch.walkingSearch(walkingRoutePlanOption
                            .from(stNodeList.get(2)).to(enNode));
                }
                nowSearchType = 3;
            } else if (v.getId() == R.id.bike) {
                BikingRoutePlanOption bikingRoutePlanOption = new BikingRoutePlanOption();
                if (!beginStationList.get(0).equals("")) {
                    mSearch.bikingSearch(bikingRoutePlanOption
                            .from(stNodeList.get(0)).to(enNode));
                }
                if (!beginStationList.get(1).equals("")) {
                    mSearch.bikingSearch(bikingRoutePlanOption
                            .from(stNodeList.get(1)).to(enNode));
                }
                if (!beginStationList.get(2).equals("")) {
                    mSearch.bikingSearch(bikingRoutePlanOption
                            .from(stNodeList.get(2)).to(enNode));
                }
                nowSearchType = 4;
            }
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;

        if (nowSearchType != 0 && nowSearchType != -1) {
            // 非跨城综合交通
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.route_result_pre) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.route_result_next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.route_result_pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            step = route.getAllStep().get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
        } else if (nowSearchType == 0) {
            // 跨城综合交通  综合跨城公交的结果判断方式不一样

            if (massroute == null || massroute.getNewSteps() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.route_result_pre) {
                return;
            }
            boolean isSamecity = nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId();
            int size = 0;
            if (isSamecity) {
                size = massroute.getNewSteps().size();
            } else {
                for (int i = 0; i < massroute.getNewSteps().size(); i++) {
                    size += massroute.getNewSteps().get(i).size();
                }
            }

            // 设置节点索引
            if (v.getId() == R.id.route_result_next) {
                if (nodeIndex < size - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.route_result_pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            if (isSamecity) {
                // 同城
                step = massroute.getNewSteps().get(nodeIndex).get(0);
            } else {
                // 跨城
                int num = 0;
                for (int j = 0; j < massroute.getNewSteps().size(); j++) {
                    num += massroute.getNewSteps().get(j).size();
                    if (nodeIndex - num < 0) {
                        int k = massroute.getNewSteps().get(j).size() + nodeIndex - num;
                        step = massroute.getNewSteps().get(j).get(k);
                        break;
                    }
                }
            }
            nodeLocation = ((MassTransitRouteLine.TransitStep) step).getStartLocation();
            nodeTitle = ((MassTransitRouteLine.TransitStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }

        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RouteResultActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteResultActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteResultActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (JUD == 0) {
                nodeIndex = -1;
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);

                if (result.getRouteLines().size() > 1) {
                    nowResultwalk = result;
                    if (!hasShownDialogue) {
                        MyTransitDlg myTransitDlg = new MyTransitDlg(RouteResultActivity.this,
                                result.getRouteLines(),
                                RouteLineAdapter.Type.WALKING_ROUTE);
                        myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hasShownDialogue = false;
                            }
                        });
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                route = nowResultwalk.getRouteLines().get(position);
                                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                                mBaidumap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                overlay.setData(nowResultwalk.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }
                        });
                        myTransitDlg.show();
                        hasShownDialogue = true;
                    }
                } else if (result.getRouteLines().size() == 1) {
                    // 直接显示
                    route = result.getRouteLines().get(0);
                    WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    Log.d("route result", "结果数<0");
                    return;
                }
            } else {
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteResultActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (JUD == 0) {
                nodeIndex = -1;
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);

                if (result.getRouteLines().size() > 1) {
                    nowResultransit = result;
                    if (!hasShownDialogue) {
                        MyTransitDlg myTransitDlg = new MyTransitDlg(RouteResultActivity.this,
                                result.getRouteLines(),
                                RouteLineAdapter.Type.TRANSIT_ROUTE);
                        myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hasShownDialogue = false;
                            }
                        });
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                route = nowResultransit.getRouteLines().get(position);
                                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                                mBaidumap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                overlay.setData(nowResultransit.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }
                        });
                        myTransitDlg.show();
                        hasShownDialogue = true;
                    }
                } else if (result.getRouteLines().size() == 1) {
                    // 直接显示
                    route = result.getRouteLines().get(0);
                    TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    Log.d("route result", "结果数<0");
                }
            } else {
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setStedFlag(stedFlag);
                stedFlag = 0;
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteResultActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点模糊，获取建议列表
            result.getSuggestAddrInfo();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nowResultmass = result;

            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (!hasShownDialogue) {
                // 列表选择
                MyTransitDlg myTransitDlg = new MyTransitDlg(RouteResultActivity.this,
                        result.getRouteLines(),
                        RouteLineAdapter.Type.MASS_TRANSIT_ROUTE);
                nowResultmass = result;
                myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hasShownDialogue = false;
                    }
                });
                myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        MyMassTransitRouteOverlay overlay = new MyMassTransitRouteOverlay(mBaidumap);
                        mBaidumap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        massroute = nowResultmass.getRouteLines().get(position);
                        overlay.setData(nowResultmass.getRouteLines().get(position));

                        MassTransitRouteLine line = nowResultmass.getRouteLines().get(position);
                        overlay.setData(line);
                        if (nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId()) {
                            // 同城
                            overlay.setSameCity(true);
                        } else {
                            // 跨城
                            overlay.setSameCity(false);
                        }
                        mBaidumap.clear();
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }
                });
                myTransitDlg.show();
                hasShownDialogue = true;
            }
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteResultActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (JUD == 0) {
                if (result.getRouteLines().size() > 1) {
                    nowResultdrive = result;
                    if (!hasShownDialogue) {
                        MyTransitDlg myTransitDlg = new MyTransitDlg(RouteResultActivity.this,
                                result.getRouteLines(),
                                RouteLineAdapter.Type.DRIVING_ROUTE);
                        myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hasShownDialogue = false;
                            }
                        });
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                route = nowResultdrive.getRouteLines().get(position);
                                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                                mBaidumap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                overlay.setData(nowResultdrive.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }

                        });
                        myTransitDlg.show();
                        hasShownDialogue = true;
                    }
                } else if (result.getRouteLines().size() == 1) {
                    route = result.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                    routeOverlay = overlay;
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    Log.d("route result", "结果数<0");
                    return;
                }
            } else {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteResultActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteResultActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (JUD == 0) {
                if (result.getRouteLines().size() > 1) {
                    nowResultbike = result;
                    if (!hasShownDialogue) {
                        MyTransitDlg myTransitDlg = new MyTransitDlg(RouteResultActivity.this,
                                result.getRouteLines(),
                                RouteLineAdapter.Type.DRIVING_ROUTE);
                        myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hasShownDialogue = false;
                            }
                        });
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                route = nowResultbike.getRouteLines().get(position);
                                BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                                mBaidumap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                overlay.setData(nowResultbike.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }
                        });
                        myTransitDlg.show();
                        hasShownDialogue = true;
                    }
                } else if (result.getRouteLines().size() == 1) {
                    route = result.getRouteLines().get(0);
                    BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                    routeOverlay = overlay;
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    Log.d("route result", "结果数<0");
                    return;
                }
            } else {
                route = result.getRouteLines().get(0);
                BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {

        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

    private class MyMassTransitRouteOverlay extends MassTransitRouteOverlay {
        public MyMassTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }


    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 关闭定位图层
        mBaidumap.setMyLocationEnabled(false);
        if (mSearch != null) {
            mSearch.destroy();
        }
        mMapView.onDestroy();

        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaidumap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }
    }
}
