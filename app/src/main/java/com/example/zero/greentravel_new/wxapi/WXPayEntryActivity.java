package com.example.zero.greentravel_new.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.activity.ShoppingCartActivity;
import com.example.zero.activity.UserOrderActivity;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;

/**
 * Created by jojo on 2017/12/3.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    private TextView complete, text, back, money, check;
    private ImageView img;
    private int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        initView();

        api = WXAPIFactory.createWXAPI(this, "wx075d26ce1edffcc8");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            MainApplication application = (MainApplication) getApplication();
            money.setText("￥ " + application.getOrderMoney());
            switch (resp.errCode) {
                case 0:
                    type = 1;
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", application.getUser_id());
                    params.put("token", application.getToken());
                    params.put("order_no", application.getOrderId());
                    Log.d("PARAMS", params.toString());
                    RequestManager.getInstance(WXPayEntryActivity.this).requestAsyn("order/getpayresult", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d("SUCCESS", result);
                            JSONObject jo = JSON.parseObject(result);
                            int flag = jo.getInteger("succeed");
                            if (flag == 1) {
                                img.setImageResource(R.drawable.pay_success);
                                text.setText(R.string.pay_result_success);
                            } else if (flag == 0) {
                                img.setImageResource(R.drawable.pay_fail);
                                text.setText(R.string.pay_result_fail);
                            }
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Log.e(TAG, errorMsg);
                        }
                    });
                    break;
                case -1:
                    type = 0;
                    img.setImageResource(R.drawable.pay_fail);
                    text.setText(R.string.pay_result_fail);
                    break;
                case -2:
                    type = 0;
                    img.setImageResource(R.drawable.pay_fail);
                    text.setText(R.string.pay_cancel);
                    break;
                default:
                    break;
            }
//            AlertDialog.Builder builder = new AlertDialog.Builder(WXPayEntryActivity.this);
//            builder.setTitle(R.string.app_tip);
//            switch (resp.errCode) {
//                case 0:
//                    builder.setMessage(R.string.pay_result_callback_msg + R.string.pay_result_success);
//                    img.setImageResource(R.drawable.pay_success);
//                    text.setText(R.string.pay_result_success);
//                    break;
//                case -1:
//                    builder.setMessage(R.string.pay_result_callback_msg + R.string.pay_result_fail);
//                    img.setImageResource(R.drawable.pay_fail);
//                    text.setText(R.string.pay_result_fail);
//                    break;
//                case -2:
//                    builder.setMessage(R.string.pay_cancel);
//                    img.setImageResource(R.drawable.pay_fail);
//                    text.setText(R.string.pay_cancel);
//                    break;
//                default:
//                    break;
//            }
//            builder.show();
        }
    }

    public void initView() {
        back = (TextView) findViewById(R.id.pay_back_arrow);
        complete = (TextView) findViewById(R.id.pay_complete);
        text = (TextView) findViewById(R.id.pay_result_text);
        img = (ImageView) findViewById(R.id.pay_result_img);
        money = (TextView) findViewById(R.id.pay_result_money);
        check = (TextView) findViewById(R.id.pay_check_order);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("type", type);
                intent.setClass(WXPayEntryActivity.this, UserOrderActivity.class);
                startActivity(intent);
            }
        });
    }
}
