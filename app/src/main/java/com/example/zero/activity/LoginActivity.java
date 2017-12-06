package com.example.zero.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SimpleTextView;


import java.util.HashMap;

/**
 * Created by jojo on 2017/10/10.
 */

public class LoginActivity extends AppCompatActivity {

    private TextView backArrow;
    private TextView register, reset;
    private SimpleTextView phone, password;
    private Button login;

    private static final int START_REGISTER_ACTIVITY = 3;
    private static final int START_RESETPW_ACTIVITY = 4;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        innitView();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, START_REGISTER_ACTIVITY);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResetPwActivity.class);
                startActivityForResult(intent, START_RESETPW_ACTIVITY);
            }
        });
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean b) {

                if (!b) {
                    String text = phone.getText().toString().trim();
                    if (!RegisterActivity.isMobile(text)) {
                        Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                    phone.notFocused();
                } else {
                    phone.isFocus();
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    password.notFocused();
                } else {
                    password.isFocus();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> params = new HashMap<>();
                params.put("type", "0");
                params.put("phone", phone.getText().trim());
                params.put("psw", password.getText().trim());
                RequestManager.getInstance(getBaseContext()).requestAsyn("users/user_login", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                    @Override
                    public void onReqSuccess(String result) {
                        Log.d(TAG, result);
                        JSONObject jsonObj = JSON.parseObject(result);
                        String user_id = jsonObj.getString("user_id");
                        String token = jsonObj.getString("token");
                        String username = jsonObj.getString("username");
                        String avator = jsonObj.getString("avator_url");
                        //TODO 载入头像 简化代码
                        Intent userInfo = new Intent();
                        userInfo.putExtra("username", username);
                        userInfo.putExtra("avator", avator);
                        setResult(RESULT_OK, userInfo);
                        SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", phone.getText().trim());
                        editor.putString("user_id", user_id);
                        editor.putString("token", token);
                        editor.commit();
                        MainApplication mainApplication = (MainApplication) getApplication();
                        mainApplication.login(user_id, phone.getText().trim(), username, token, avator);

                        Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorMsg);
                    }
                });
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    public void innitView() {
        backArrow = (TextView) findViewById(R.id.login_back_arrow);
        register = (TextView) findViewById(R.id.login_register);
        reset = (TextView) findViewById(R.id.login_reset);
        phone = (SimpleTextView) findViewById(R.id.login_phone);
        phone.setHintText(" 请输入手机号码");
        phone.setLeftImage(R.drawable.user_fill);
        password = (SimpleTextView) findViewById(R.id.login_password);
        password.setLeftImage(R.drawable.lock_fill);
        password.setPw();
        login = (Button) findViewById(R.id.login_button);
        MainApplication mainApplication = (MainApplication) getApplication();
        phone.setText(mainApplication.getPhone());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_REGISTER_ACTIVITY:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case START_RESETPW_ACTIVITY:
                    phone.setText(data.getStringExtra("phone"));
                    break;
                default:
                    break;
            }
        }
    }
}
