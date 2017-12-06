package com.example.zero.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.adapter.MsgAdapter;
import com.example.zero.bean.MsgBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 消息activity
 */
public class MsgActivity extends AppCompatActivity {

    private String TAG = "MsgActivity";
    private RecyclerView msg_recv;
    private TextView backArrow;
    private List<MsgBean> dataList = new ArrayList<>();
    private String uid;
    private List<Integer> msg_id = new ArrayList<>();
    private List<String> msg_url = new ArrayList<>();
    private String msg_pic, msg_title, msg_content, msg_time;
    private MsgAdapter adapter;
    private MainApplication application;
    private TextView textView;
    private FrameLayout msg_fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        application = (MainApplication) getApplication();
        uid = application.getUser_id();
        innitView();
        getMsg();
        adapter = new MsgAdapter(this, dataList);
        msg_recv.setAdapter(adapter);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter.setOnItemClickListener(new MsgAdapter.onRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Uri uri = Uri.parse(msg_url.get(position));
                Log.e(TAG, uri.toString());
                Intent intent = new Intent();
                // intent.setAction("android.intent.action.VIEW");
                // intent.setData(uri);
                intent.setClass(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                Toast.makeText(MsgActivity.this, "click" + position, Toast.LENGTH_LONG).show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void innitView() {
        msg_recv = (RecyclerView) findViewById(R.id.msg_recv);
        msg_recv.setLayoutManager(new LinearLayoutManager(this));
        backArrow = (TextView) findViewById(R.id.msg_back_arrow);
        msg_fl = (FrameLayout) findViewById(R.id.msg_framelayout);
        textView = new TextView(this);
        textView.setText("您还未登录哦\n" + "请您登录后进行查看!");
        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.no_login, 0, 0);
        textView.setCompoundDrawablePadding(30);
        textView.setPadding(0, 500, 0, 0);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.gray1, null));
    }

    public void sendNewMsg() {
        if (application.getMsgBtn() == true) {
            Intent intent = new Intent();
            intent.setAction("MSG_SERVICE");
            intent.setPackage("com.example.zero.greentravel_new");
            startService(intent);
        } else {

        }
    }

    public void getMsg() {
        if (application.isOnline() == false) {
            msg_fl.addView(textView);
        } else {
            msg_fl.removeView(textView);
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", uid);
            params.put("offset", 0 + "");
            params.put("count", 10 + "");
            RequestManager.getInstance(this).requestAsyn("users/get_msgs", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                @Override
                public void onReqSuccess(String result) {
                    dataList.clear();
                    JSONObject jsonObject = JSON.parseObject(result);
                    JSONArray array = JSONArray.parseArray(jsonObject.getString("msgs"));
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject jo = JSON.parseObject(array.getString(i));
                        msg_id.add(jo.getInteger("msg_id"));
                        msg_pic = jo.getString("photo_url");
                        msg_content = jo.getString("content");
                        msg_url.add(jo.getString("url"));
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date d = new Date(jo.getLong("create_time"));
                        msg_time = sdf.format(d);
                        MsgBean msgBean = new MsgBean();
                        msgBean.setText("系统消息", msg_content, msg_time, msg_pic);
                        dataList.add(msgBean);
                    }
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e(TAG, errorMsg);
                }
            });
        }
    }
}
