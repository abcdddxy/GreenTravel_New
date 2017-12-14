package com.example.zero.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SimpleTextView;


import java.util.HashMap;


/**
 * Created by jojo on 2017/10/11.
 */

public class RegisterActivity extends AppCompatActivity {

    private TextView backArrow;
    private TextView confirm_button;
    private Button register;
    private SimpleTextView phone, password, pw_confirm, confirm;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        innitView();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean b) {
                if (!b) {
                    String text = phone.getText().toString().trim();
                    if (!isMobile(text)) {
                        Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                    phone.notFocused();
                } else {
                    phone.isFocus();
                }
            }
        });
        confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    confirm.notFocused();
                } else {
                    confirm.isFocus();
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (password.getText().toString().trim().length() < 6) {
                        Toast.makeText(RegisterActivity.this, "密码不能少于6个字符。", Toast.LENGTH_SHORT).show();
                    }
                    password.notFocused();
                } else {
                    password.isFocus();
                }
            }
        });
        pw_confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!password.getText().toString().trim().equals(pw_confirm.getText().toString().trim())) {
                        Toast.makeText(RegisterActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    }
                    pw_confirm.notFocused();
                } else {
                    pw_confirm.isFocus();
                }
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = phone.getText().toString().trim();
                if (isMobile(text)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("type", "0");
                    params.put("phone", phone.getText());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/send_verification_code", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            Toast.makeText(RegisterActivity.this, "已发送短信", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = phone.getText().trim();
                if (password.getText().toString().trim().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "密码不能少于6个字符", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().trim().equals(pw_confirm.getText().trim())) {
                    Toast.makeText(RegisterActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                } else if (isMobile(text)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("phone", phone.getText().trim());
                    params.put("psw", password.getText().trim());
                    params.put("verification_code", confirm.getText().trim());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/user_register", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            JSONObject jsonObj = JSON.parseObject(result);
                            String user_id = jsonObj.getString("user_id");
                            String token = jsonObj.getString("token");
                            String username = jsonObj.getString("username");
                            Intent userInfo = new Intent();
                            userInfo.putExtra("username", username);
                            userInfo.putExtra("avator", "");
                            setResult(RESULT_OK, userInfo);
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "注册失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void innitView() {
        backArrow = (TextView) findViewById(R.id.register_back_arrow);
        phone = (SimpleTextView) findViewById(R.id.register_phone);
        phone.setHintText("请输入手机号码");
        phone.setLeftImage(R.drawable.user_fill);
        confirm = (SimpleTextView) findViewById(R.id.register_confirm);
        confirm.setHintText("验证码");
        confirm.setLeftImage(R.drawable.identify);
        password = (SimpleTextView) findViewById(R.id.register_pw);
        password.setHintText("请输入密码");
        password.setLeftImage(R.drawable.lock_fill);
        password.setPw();
        pw_confirm = (SimpleTextView) findViewById(R.id.register_pw_confirm);
        pw_confirm.setHintText("确认密码");
        pw_confirm.setLeftImage(R.drawable.lock_fill);
        pw_confirm.setPw();
        confirm_button = (TextView) findViewById(R.id.register_confirm_button);
        register = (Button) findViewById(R.id.register_button);
    }

    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String num = "[1][358]\\d{9}";
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

}

