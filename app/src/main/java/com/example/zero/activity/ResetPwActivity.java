package com.example.zero.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zero.greentravel_new.R;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SimpleTextView;

import java.util.HashMap;

/**
 * Created by kazu_0122 on 2017/10/31.
 */

public class ResetPwActivity extends AppCompatActivity {

    private TextView backArrow;
    private TextView confirm_button;
    private Button resetpw;
    private SimpleTextView phone, password, pw_confirm, confirm;

    private static final String TAG = "ResetPwActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_reset);
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
                    if (!RegisterActivity.isMobile(text)) {
                        Toast.makeText(getBaseContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getBaseContext(), "密码不能少于6个字符。", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getBaseContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
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
                if (RegisterActivity.isMobile(text)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("type", "1");
                    params.put("phone", phone.getText());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/send_verification_code", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            Toast.makeText(getBaseContext(), "已发送短信", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = phone.getText().trim();
                if (password.getText().toString().trim().length() < 6) {
                    Toast.makeText(getBaseContext(), "密码不能少于6个字符。", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().trim().equals(pw_confirm.getText().trim())) {
                    Toast.makeText(getBaseContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                } else if (RegisterActivity.isMobile(text)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("phone", phone.getText().trim());
                    params.put("psw", password.getText().trim());
                    params.put("verification_code", confirm.getText().trim());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/user_reset_psw", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            Intent userInfo = new Intent();
                            userInfo.putExtra("phone", phone.getText().trim());
                            setResult(RESULT_OK, userInfo);
                            Toast.makeText(getBaseContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "重置失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    public void innitView() {
        backArrow = (TextView) findViewById(R.id.resetpw_back_arrow);
        phone = (SimpleTextView) findViewById(R.id.resetpw_phone);
        phone.setHintText(" 请输入手机号码/邮箱");
        phone.setLeftImage(R.drawable.user_fill);
        confirm = (SimpleTextView) findViewById(R.id.resetpw_confirm);
        confirm.setHintText(" 验证码");
        confirm.setLeftImage(R.drawable.identify);
        password = (SimpleTextView) findViewById(R.id.resetpw_pw);
        password.setHintText(" 请输入新密码");
        password.setLeftImage(R.drawable.lock_fill);
        password.setPw();
        pw_confirm = (SimpleTextView) findViewById(R.id.resetpw_pw_confirm);
        pw_confirm.setHintText(" 请重复新密码");
        pw_confirm.setLeftImage(R.drawable.lock_fill);
        pw_confirm.setPw();
        confirm_button = (TextView) findViewById(R.id.resetpw_confirm_button);
        resetpw = (Button) findViewById(R.id.resetpw_button);

    }
}
