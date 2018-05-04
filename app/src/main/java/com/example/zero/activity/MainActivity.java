package com.example.zero.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.example.zero.entity.CouponInfo;
import com.example.zero.entity.GiftCoupon;
import com.example.zero.fragment.FragmentController;

import com.example.zero.greentravel_new.R;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MainApplication;
import com.example.zero.view.TitleRouteLayout;
import okhttp3.Call;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, SensorEventListener {

    /**
     * 内容区域
     */
    private LinearLayout bottom_nav_content;
    /**
     * 底部导航栏
     */
    private BottomNavigationBar bottom_navigation_bar_container;

    private BottomNavigationItem personItem;
    private BottomNavigationItem adviceItem;
    private BottomNavigationItem routeItem;
    private BottomNavigationItem salesItem;
    private BadgeItem badgeItem;

    /**
     * 标题栏
     */
    private TitleRouteLayout titleRouteLayout;

    /**
     * Fragment控制类
     */
    private FragmentController fragmentController;

    /**
     * 定位SDK的核心类
     */
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
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
    private boolean mEnableCustomStyle = true;
    /**
     * 定位按钮
     */
    private Button btnLocation;
    //优惠券
    private Button stationCouponBtn;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // 附近的优惠券
    private List<GiftCoupon> giftCouponList;
    private android.support.v7.app.AlertDialog selectDialog;
    // 领到的优惠券
    private CouponInfo couponInfo;

    // UI相关
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    private static String PATH = "style_json.json";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication application = (MainApplication) getApplication();
        application.defaultLogin();
        setMapCustomFile(this, PATH);
        SDKInitializer.initialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        initBottomNavBar();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        sendNotification();
        new MyThread().start();
        Log.d(TAG, "onCreate: success");
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //延迟两秒更新
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainApplication application = (MainApplication) getApplication();
                    titleRouteLayout.setImg(MainActivity.this, application.getAvator());
                }
            });
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        bottom_nav_content = (LinearLayout) findViewById(R.id.bottom_nav_content);
        bottom_navigation_bar_container = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_container);

        titleRouteLayout = (TitleRouteLayout) findViewById(R.id.route_title);

        btnLocation = (Button) findViewById(R.id.btn_map_main_location);
        stationCouponBtn = (Button) findViewById(R.id.station_coupon_by);
        stationCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainApplication mainApplication = (MainApplication) getApplication();
                giftCouponList = new ArrayList<>();
                if(!mainApplication.isOnline()){
                    Toast.makeText(MainActivity.this, "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
                }else{
                    httpThread(1, null);
                }
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMode();
            }
        });
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.map_main);
        mBaiduMap = mMapView.getMap();
        MapView.setMapCustomEnable(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //POI说明关闭
//        mBaiduMap.showMapPoi(false);
//        mBaiduMap.setTrafficEnabled(true);

        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 设置返回结果包含手机的方向
        option.setLocationNotify(true); // 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true); // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true); // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocClient.registerLocationListener(myListener);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();
        // 点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // 附近的优惠券活动
                if (marker.getExtraInfo().getString("id").equals("gift")){
                    final Bundle mBundle = marker.getExtraInfo();
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(R.drawable.icon_coupon);
                    builder.setTitle("捡到了一个券包");
                    builder.setMessage("您在周围捡到了一个券包，是否打开?");
                    builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            marker.remove();
                            MainApplication mainApplication = (MainApplication) getApplication();
                            if(!mainApplication.isOnline()){
                                Toast.makeText(MainActivity.this, "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // 发送请求，领取优惠券
                                String share_code = mBundle.getString("share_code");
                                httpThread(2, share_code);
                                // 显示结果
                            }
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
                return false;
            }
        });
    }

    public void sendNotification() {
        MainApplication application = (MainApplication) getApplication();
        if (application.getMsgBtn() == true) {
            Intent intent = new Intent();
            intent.setAction("MSG_SERVICE");
            intent.setPackage("com.example.zero.greentravel_new");
            startService(intent);
        } else {

        }
    }

    private void changeMode() {
        switch (mCurrentMode) {
            case NORMAL:
                mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                Toast.makeText(MainActivity.this, "当前模式:跟随", Toast.LENGTH_SHORT).show();
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.overlook(0);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                break;
            case COMPASS:
                mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                Toast.makeText(MainActivity.this, "当前模式:正常", Toast.LENGTH_SHORT).show();
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                MapStatus.Builder builder1 = new MapStatus.Builder();
                builder1.overlook(0);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                break;
            case FOLLOWING:
                mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                Toast.makeText(MainActivity.this, "当前模式:罗盘", Toast.LENGTH_SHORT).show();
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                break;
            default:
                break;
        }
    }

    /**
     * 初始化底部导航栏
     */
    private void initBottomNavBar() {
        //自动隐藏
        bottom_navigation_bar_container.setAutoHideEnabled(true);

        bottom_navigation_bar_container.setMode(BottomNavigationBar.MODE_FIXED);
        bottom_navigation_bar_container.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        //背景颜色
        bottom_navigation_bar_container.setBarBackgroundColor(R.color.white);
        //未选中时的颜色
        bottom_navigation_bar_container.setInActiveColor(R.color.nav_gray);
        //选中时的颜色
        bottom_navigation_bar_container.setActiveColor(R.color.GreenTheme13);

        //角标
        //badgeItem = new BadgeItem().setBackgroundColor(Color.RED).setText("99").setHideOnSelect(true);

        routeItem = new BottomNavigationItem(R.drawable.route, "路线");
        adviceItem = new BottomNavigationItem(R.drawable.advice, "建议");
        salesItem = new BottomNavigationItem(R.drawable.sale, "促销");
        personItem = new BottomNavigationItem(R.drawable.person, "个人");
        personItem.setBadgeItem(badgeItem);

        bottom_navigation_bar_container.addItem(routeItem).addItem(adviceItem).addItem(salesItem).addItem(personItem);
        bottom_navigation_bar_container.initialise();
        bottom_navigation_bar_container.setTabSelectedListener(this);

        fragmentController = FragmentController.getInstance(this, R.id.bottom_nav_content);
        fragmentController.showFragment(0);
    }

    /**
     * 底部NaV监听
     *
     * @param position Fragment位置
     */
    @Override
    public void onTabSelected(int position) {
        fragmentController.hideFragments();//先隐藏所有frag
        switch (position) {
            case 0:
                fragmentController.showFragment(0);
                getSupportActionBar().setTitle("路线");
                titleRouteLayout.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.VISIBLE);
                btnLocation.setVisibility(View.VISIBLE);
                break;

            case 1:
                fragmentController.showFragment(1);
                getSupportActionBar().setTitle("建议");
                titleRouteLayout.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                btnLocation.setVisibility(View.GONE);
                break;

            case 2:
                fragmentController.showFragment(2);
                getSupportActionBar().setTitle("促销");
                titleRouteLayout.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                btnLocation.setVisibility(View.GONE);
                break;

            case 3:
                fragmentController.showFragment(3);
                getSupportActionBar().setTitle("个人");
                mMapView.setVisibility(View.GONE);
                btnLocation.setVisibility(View.GONE);
                titleRouteLayout.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

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
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(locData);

            MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfiguration(config);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();

            MainApplication application = (MainApplication) getApplication();
            application.setLongitude(mCurrentLon);
            application.setLatitude(mCurrentLat);

            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfiguration(config);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mLocClient.isStarted()) {
            mLocClient.start();// 开启定位
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能运行本程序", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "PERMISSION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 设置个性化地图config文件路径
     */
    private void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open("customConfigdir/" + PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MapView.setCustomMapStylePath(moduleName + "/" + PATH);
    }

    //todo 站点周围优惠券


    private void addGiftMarker() {
        BitmapDescriptor coupon_icon = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_coupon);
        if(giftCouponList != null)
            Log.i("addCouponMarker", giftCouponList.size() + "");
        else
            Log.i("addCouponMarker", "0");
        if(giftCouponList!=null && giftCouponList.size() >0){
            // 清空地图上的marker
            mBaiduMap.clear();
            List<OverlayOptions> options = new ArrayList<>();
            for (GiftCoupon gift:giftCouponList) {
                Bundle mBundle = new Bundle();
                mBundle.putString("id", "gift");
                mBundle.putString("share_code", gift.getId());
                mBundle.putString("latitude", String.valueOf(gift.getLat()));
                mBundle.putString("longitude", String.valueOf(gift.getLng()));

                OverlayOptions option = new MarkerOptions()
                        .position(new LatLng(gift.getLat(), gift.getLng()))
                        .icon(coupon_icon)
                        .extraInfo(mBundle)
                        .zIndex(9);
                options.add(option);
            }
            mBaiduMap.addOverlays(options);
            MainApplication mainApplication = (MainApplication) getApplication();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(new LatLng(mainApplication.getLatitude(), mainApplication.getLongitude()),16);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
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
                // 描绘周边的优惠券
                case 1:addGiftMarker();break;
                case 2:showReceivedCouponInfo();break;
            }
            return false;
        }
    });


    // TODO 进度条
    private void httpThread(int flag, final String code) {
        //构建一个下载进度条
        pd = ProgressDialog.show(MainActivity.this, "加载数据", "数据加载中，请稍后......");
        switch (flag){
            // 读取周边的优惠券
            case 1:new Thread() {
                @Override
                public void run() {
                    //在新线程里执行长耗时方法
                    getNearByCouponGifts();
                    //执行完毕后给handler发送一个空消息
                    httpHandler.sendEmptyMessage(1);
                }
            }.start();break;
            // 领取选定优惠券
            case 2:new Thread() {
                @Override
                public void run() {
                    //在新线程里执行长耗时方法
                    receiveCouponGift(code);
                    //执行完毕后给handler发送一个空消息
                    httpHandler.sendEmptyMessage(2);
                }
            }.start();break;
        }
    }

    //加载周边优惠券
    private void getNearByCouponGifts() {
        try {
            MainApplication mainApplication = (MainApplication) getApplication();
            final Bundle mBundle = new Bundle();
            mBundle.putString("user_id", mainApplication.getUser_id());
            mBundle.putString("token", mainApplication.getToken());
            mBundle.putString("lat", String.valueOf(mainApplication.getLatitude()));
            mBundle.putString("lng", String.valueOf(mainApplication.getLongitude()));
            HttpUtil.nearByGiftCouponDisplayOkHttpRequest(mBundle, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    parseNearByCouponGifts(responseData);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: ERROR!");
                    Toast.makeText(MainActivity.this, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                }
            });
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 读取附近的优惠券
    private void parseNearByCouponGifts(String jsonData) {
        try {
            JSONObject rep = new JSONObject(jsonData);
            JSONArray coupons = rep.getJSONArray("coupons");
            if (coupons.length() != 0) {
                for (int i = 0; i < coupons.length(); i++) {
                    JSONObject coupon = coupons.getJSONObject(i);
                    GiftCoupon giftCoupon = new GiftCoupon(coupon.getString("id"), coupon.getDouble("coupon_lat"), coupon.getDouble("coupon_lng"));
                    giftCouponList.add(giftCoupon);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 领取优惠券
    private void receiveCouponGift(String code) {
        try {
            MainApplication mainApplication = (MainApplication) getApplication();
            final Bundle mBundle = new Bundle();
            mBundle.putString("user_id", mainApplication.getUser_id());
            mBundle.putString("token", mainApplication.getToken());
            mBundle.putString("share_code", code);
            HttpUtil.receiveGiftCouponOkHttpRequest(mBundle, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    parseCouponGift(responseData);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: ERROR!");
                    Toast.makeText(MainActivity.this, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                }
            });
            Thread.sleep(1500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    // 解析领取到的优惠券
    private void parseCouponGift(String jsonData){
        try{
            JSONObject rsp = new JSONObject(jsonData);
            Log.d("sb", String.valueOf(rsp.getInt("succeed")));
            if(rsp.has("succeed") && rsp.getInt("succeed")==1){
                JSONObject coupon = rsp.getJSONObject("coupon");
                couponInfo = new CouponInfo(coupon.getString("id"),
                        coupon.getInt("type"), coupon.getString("coupon_name"),
                        coupon.getString("shop_id"), coupon.getString("seller_id"),
                        coupon.getString("expire_at"), coupon.getString("shop_tag"),
                        coupon.getString("image_url"), coupon.getString("shop_name"));
            }else{
                Toast.makeText(MainActivity.this, "优惠券领取失败，请稍后再试", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 显示优惠券信息
    private void showReceivedCouponInfo() {
        if(couponInfo!=null){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.icon_coupon);
            builder.setTitle("领取成功");
            builder.setMessage("名称:" + couponInfo.getCoupon_name() + "\n" +
                    "商家名称:" + couponInfo.getShop_name() + "\n" +
                    "过期时间:" + couponInfo.getExpire_at());
            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        }
        else{
            Toast.makeText(MainActivity.this, "优惠券领取失败，请稍后再试", Toast.LENGTH_LONG).show();
        }
    }
}
