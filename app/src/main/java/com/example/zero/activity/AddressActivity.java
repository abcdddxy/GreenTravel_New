package com.example.zero.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import com.example.zero.adapter.AddressAdapter;

import com.example.zero.bean.AddressBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jojo on 2017/11/15.
 */

public class AddressActivity extends AppCompatActivity {

    private String TAG = "AddressActivity";
    private FrameLayout addr_content;
    private RecyclerView addr_recy;
    private TextView add;
    private List<AddressBean> dataList = new ArrayList<>();
    private TextView back;
    private String uid;
    private Map<String, String> map = new HashMap<>();
    private ArrayList<Map<String, String>> dataList1 = new ArrayList<>();
    private AddressAdapter addressAdapter;
    private static final int ADD = 1;
    private static final int EDIT = 2;
    private TextView textView, textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initView();
        addressAdapter = new AddressAdapter(this, dataList);
        addr_recy.setAdapter(addressAdapter);
        showAddress();
        addressAdapter.setOnItemClickListener(new AddressAdapter.onRecycleItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = getIntent();
                if (intent.getStringExtra("type").equals("orderConfirm")) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("id", dataList1.get(position).get("id"));
                    intent1.putExtra("name", dataList1.get(position).get("mailing_name"));
                    intent1.putExtra("phone", dataList1.get(position).get("mailing_phone"));
                    setResult(1, intent1);
                    finish();
                }
            }

            @Override
            public void onItem1Click(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("id", dataList1.get(position).get("id"));
                intent.putExtra("type", "edit");
                intent.putExtra("is_default", dataList1.get(position).get("is_default"));
                intent.putExtra("name", dataList1.get(position).get("mailing_name"));
                intent.putExtra("phone", dataList1.get(position).get("mailing_phone"));
                intent.setClass(AddressActivity.this, AddressEditActivity.class);
                startActivityForResult(intent, EDIT);
            }

            @Override
            public void onItem2Click(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);
                AlertDialog alertDialog = builder.setMessage("确定要删除该地址吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final MainApplication application = (MainApplication) getApplication();
                                uid = application.getUser_id();
                                HashMap<String, String> params = new HashMap<>();
                                params.put("id", dataList1.get(position).get("id"));
                                params.put("user_id", uid);
                                RequestManager.getInstance(AddressActivity.this).requestAsyn("users/del_mailing_info", RequestManager.TYPE_DEL, params, new RequestManager.ReqCallBack<String>() {
                                    @Override
                                    public void onReqSuccess(String result) {
                                        showAddress();
                                        application.setAddressId("");
                                        application.setAddressName("");
                                        application.setAddressPhone("");
                                    }

                                    @Override
                                    public void onReqFailed(String errorMsg) {
                                        Log.e(TAG, errorMsg);
                                        Toast.makeText(AddressActivity.this, "内部服务器错误", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).create();
                alertDialog.show();
            }

            @Override
            public void onRadioButton(View view, int position) {
                if (dataList.get(position).isChanged()) {
                    final MainApplication application = (MainApplication) getApplication();
                    uid = application.getUser_id();
                    final int clickPos = position;
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", dataList1.get(position).get("id"));
                    params.put("user_id", uid);
                    params.put("is_default", "1");
                    params.put("mailing_name", dataList1.get(position).get("mailing_name"));
                    params.put("mailing_phone", dataList1.get(position).get("mailing_phone"));
                    RequestManager.getInstance(AddressActivity.this).requestAsyn("users/update_mailing_info", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, "默认地址修改成功");
                            application.setAddressId(dataList1.get(clickPos).get("id"));
                            application.setAddressName(dataList1.get(clickPos).get("mailing_name"));
                            application.setAddressPhone(dataList1.get(clickPos).get("mailing_phone"));
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Log.e(TAG, errorMsg);
                            Toast.makeText(AddressActivity.this, "内部服务器错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainApplication application = (MainApplication) getApplication();
                if (application.isOnline()) {
                    if (dataList1.size() == 10) {
                        Toast.makeText(AddressActivity.this, "您最多只能添加十个收货信息", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(AddressActivity.this, AddressEditActivity.class);
                        intent.putExtra("type", "add");
                        if (dataList1.size() == 0) {
                            intent.putExtra("is_default", "1");
                        } else {
                            intent.putExtra("is_default", "0");
                        }
                        intent.putExtra("name", "");
                        intent.putExtra("phone", "");
                        startActivityForResult(intent, ADD);
                    }
                }
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initView() {
        addr_content = (FrameLayout) findViewById(R.id.address_content);
        addr_recy = (RecyclerView) findViewById(R.id.address_recy);
        addr_recy.setLayoutManager(new LinearLayoutManager(this));
        back = (TextView) findViewById(R.id.address_back_arrow);
        add = (TextView) findViewById(R.id.address_add);
        textView = new TextView(AddressActivity.this);
        textView1 = new TextView(AddressActivity.this);
        textView.setText("还没有收货地址哦，快去添加吧！");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.gray1, null));
        textView1.setText("您还未登录哦\n" + "请您登录后进行查看!");
        textView1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.no_login, 0, 0);
        textView1.setCompoundDrawablePadding(30);
        textView1.setPadding(0, 500, 0, 0);
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setTextSize(14);
        textView1.setTextColor(getResources().getColor(R.color.gray1, null));
    }

    public void showAddress() {
        dataList.clear();
        dataList1.clear();
        final MainApplication application = (MainApplication) getApplication();
        uid = application.getUser_id();
        if (application.isOnline() == false) {
            addr_content.addView(textView1);
        } else {
            addr_content.removeView(textView1);
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", uid);
            RequestManager.getInstance(this).requestAsyn("users/get_mailing_infos", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                @Override
                public void onReqSuccess(String result) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("mailing_infos"));
                    if (jsonArray.size() == 0) {
                        addr_content.addView(textView);
                    } else {
                        addr_content.removeView(textView);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jo = JSON.parseObject(jsonArray.getString(i));
                            map = new HashMap<>();
                            map.put("id", jo.getString("id"));
                            map.put("user_id", jo.getString("user_id"));
                            map.put("mailing_name", jo.getString("mailing_name"));
                            map.put("mailing_phone", jo.getString("mailing_phone"));
                            map.put("is_default", jo.getString("is_default"));
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d = new Date(jo.getLong("create_time"));
                            map.put("create_time", sdf.format(d));
                            AddressBean addressBean = new AddressBean();
                            if (map.get("is_default").equals("1")) {
                                application.setAddressId(map.get("id"));
                                application.setAddressName(map.get("mailing_name"));
                                application.setAddressPhone(map.get("mailing_phone"));
                                addressBean.setName(map.get("mailing_name"));
                                addressBean.setPhone(map.get("mailing_phone"));
                                addressBean.setSelect(true);
                                addressBean.setDefaultAddr("默认地址");
                                dataList.add(0, addressBean);
                                dataList1.add(0, map);
                            } else {
                                addressBean.setName(map.get("mailing_name"));
                                addressBean.setPhone(map.get("mailing_phone"));
                                addressBean.setSelect(false);
                                addressBean.setDefaultAddr("设为默认");
                                dataList.add(addressBean);
                                dataList1.add(map);
                            }
                        }
                        addressAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e(TAG, errorMsg);
                    Toast.makeText(AddressActivity.this, "内部服务器错误", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                MainApplication application = (MainApplication) getApplication();
                uid = application.getUser_id();
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", uid);
                params.put("is_default", data.getStringExtra("is_default"));
                params.put("mailing_name", data.getStringExtra("name"));
                params.put("mailing_phone", data.getStringExtra("phone"));
                RequestManager.getInstance(AddressActivity.this).requestAsyn("users/add_mailing_info", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                    @Override
                    public void onReqSuccess(String result) {
                        showAddress();
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Log.e(TAG, errorMsg);
                        Toast.makeText(AddressActivity.this, "内部服务器错误", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else if (requestCode == 2) {
            if (resultCode == 2) {
                showAddress();
            }
        }
    }
}
