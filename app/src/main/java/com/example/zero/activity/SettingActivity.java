package com.example.zero.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.CacheDataManager;

import com.alibaba.fastjson.JSONObject;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;

import java.io.File;
import java.util.HashMap;

/**
 * Created by jojo on 2017/9/25.
 */

public class SettingActivity extends AppCompatActivity {

    private String TAG = "SettingActivity";
    private LinearLayout location, map, city, about, safety, update;
    private ToggleButton apk, newMsg, pic;
    private Button clean;
    private TextView backArrow;
    private TextView location_text, current_ver;
    private Context mContext;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private int versionCode;
    private int serviceVersionCode = 0; //服务器端apk版本号
    private String description;   //新版本功能描述
    private String download_url;  //apk下载url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = SettingActivity.this;
        innitView();
        //checkLocationPermission();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 转到手机设置界面，用户设置权限
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
                // 设置完成后返回到原来的界面startActivityForResult(intent, 0);
            }
        });
//        map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SettingActivity.this, "map", Toast.LENGTH_SHORT).show();
//            }
//        });
//        city.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SettingActivity.this, "city", Toast.LENGTH_SHORT).show();
//            }
//        });
        MainApplication application = (MainApplication) getApplication();
        newMsg.setChecked(application.getMsgBtn());
        newMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (compoundButton.isChecked()) {
                    // 启动Service
                    editor.putBoolean("send_msg", true);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setAction("MSG_SERVICE");
                    intent.setPackage("com.example.zero.greentravel_new");
                    startService(intent);
                } else {
                    // 关闭Service
                    editor.putBoolean("send_msg", false);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setAction("MSG_SERVICE");
                    intent.setPackage("com.example.zero.greentravel_new");
                    stopService(intent);
                }
            }
        });
        apk.setChecked(application.getApkBtn());
        apk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (compoundButton.isChecked()) {
                    editor.putBoolean("apk_download", true);
                    editor.commit();
                } else {
                    editor.putBoolean("apk_download", false);
                    editor.commit();
                }
            }
        });
//        pic.setChecked(application.getPicBtn());
//        pic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", Activity.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                if (compoundButton.isChecked()) {
//                    editor.putBoolean("pic_download", true);
//                    editor.commit();
//                    if (isWifiConnected(SettingActivity.this)) {
//                        //由于当前图片均使用本地的，所以该部分代码待写//TODO:下载图片
//                    } else {
//                        //不下载图片，显示图片占位符
//                    }
//                } else {
//                    editor.putBoolean("pic_download", true);
//                    editor.commit();
//                    //直接下载图片
//                }
//            }
//        });
//        safety.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(SettingActivity.this, "safety", Toast.LENGTH_SHORT).show();
//            }
//        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        try {
            current_ver.setText("当前版本：" + getVersionName());
            getVersionCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    current_ver.setText("当前版本：" + getVersionName());
                    checkVersion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            clean.setText("清除缓存 " + CacheDataManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(mContext);
                alertDialog = builder.setMessage("确定要清除缓存吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new clearCache()).start();
                            }
                        }).create();
                alertDialog.show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void innitView() {
        backArrow = (TextView) findViewById(R.id.setting_back_arrow);
        location = (LinearLayout) findViewById(R.id.setting_location);
        location_text = (TextView) findViewById(R.id.location_text);
        //map = (LinearLayout) findViewById(R.id.setting_map);
        //city = (LinearLayout) findViewById(R.id.setting_city);
        newMsg = (ToggleButton) findViewById(R.id.msg_toggle);
        apk = (ToggleButton) findViewById(R.id.apk_toggle);
        //pic = (ToggleButton) findViewById(R.id.pic_toggle);
        //safety = (LinearLayout) findViewById(R.id.setting_safety);
        about = (LinearLayout) findViewById(R.id.about);
        update = (LinearLayout) findViewById(R.id.setting_update);
        current_ver = (TextView) findViewById(R.id.current_ver);
        clean = (Button) findViewById(R.id.setting_clean);
    }

//    /**
//     * 刷新界面
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        checkLocationPermission();
//    }

//    /**
//     * 检查位置权限是否打开
//     */
//    public void checkLocationPermission() {
//        int network = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//        int gps = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (network == PermissionChecker.PERMISSION_GRANTED && gps == PermissionChecker.PERMISSION_GRANTED) {
//            location_text.setText("已开启");
//        } else {
//            location_text.setText("去设置");
//        }
//    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGPSOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断wifi是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前程序的版本号
     */
    private int getVersionCode() throws Exception {
        //获取packagemanager的实例
        versionCode = 0;
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        versionCode = packInfo.versionCode;
        return packInfo.versionCode;
    }

    /**
     * 获取当前程序的版本名
     */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }

    /**
     * 获取服务器端版本号
     */
    public void checkVersion() {
        HashMap<String, String> params = new HashMap<>();
        RequestManager.getInstance(this).requestAsyn("users/get_latest_version", RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                JSONObject jo = JSON.parseObject(result);
                serviceVersionCode = jo.getInteger("version");
                description = jo.getString("desc");
                download_url = jo.getString("download_url");
                if (versionCode < serviceVersionCode) {
                    builder = new AlertDialog.Builder(mContext);
                    alertDialog = builder.setTitle("版本更新提示").setMessage("检查到有最新版本,是否更新?\n" + "新版本功能描述：\n"+description)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, getFilesDir() + "");
                                    RequestManager.getInstance(SettingActivity.this).downLoadFile(download_url, "http://10.108.120.91:8080/users/" + download_url + "?type=1", getFilesDir(), new RequestManager.ReqProgressCallBack<File>() {
                                        @Override
                                        public void onProgress(long total, long current) {
                                            Log.e("progress", current + "/" + total + "");
                                        }

                                        @Override
                                        public void onReqSuccess(File result) {
                                            Toast.makeText(SettingActivity.this, "下载成功", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onReqFailed(String errorMsg) {
                                            Toast.makeText(SettingActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).create();
                    alertDialog.show();
                } else {
                    Toast.makeText(SettingActivity.this, "当前应用已是最新版本", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e(TAG, errorMsg);
            }
        });
    }

    /**
     * 内部类，用于清理内存，实现了一个Runnable，清理完后发一个消息
     */
    class clearCache implements Runnable {

        @Override
        public void run() {
            try {
                CacheDataManager.clearAllCache(SettingActivity.this);
                Thread.sleep(3000);
                if (CacheDataManager.getTotalCacheSize(SettingActivity.this).startsWith("0")) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                return;
            }
        }
    }

    /**
     * handler函数接收消息，处理结果，其用意是清理完了就弹一个Toast，清理完成
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        clean.setText("清除缓存 " + CacheDataManager.getTotalCacheSize(SettingActivity.this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SettingActivity.this, "清理完成", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
