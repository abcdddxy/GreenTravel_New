package com.example.zero.fragment;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.zero.activity.*;
import com.example.zero.entity.CouponInfo;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.HttpUtil;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.makeramen.roundedimageview.RoundedImageView;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Created by jojo on 2017/9/22.
 */

public class PersonalInfoFragment extends Fragment implements View.OnClickListener {
    private View person_frag;
    private Context context;
    private TextView setting;
    private TextView login;
    private TextView register;
    private TextView user_name;
    private TextView order0;
    private TextView order1;
    private TextView order2;
    private TextView order3;
    private TextView order4;
    private RoundedImageView img;
    private LinearLayout order;
    private LinearLayout msg;
    private LinearLayout user;
    private LinearLayout favor;
    private LinearLayout address;
    private LinearLayout friend;
    private LinearLayout help_feedback;
    private LinearLayout subway;
    private LinearLayout couponReceive;

    private static final int START_LOGIN_ACTIVITY = 1;
    private static final int START_REGISTER_ACTIVITY = 2;
    private static final int START_USER_ACTIVITY = 3;
    private static final String TAG = "PersonalInfoFragment";
    private CouponInfo couponInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        person_frag = inflater.inflate(R.layout.fragment_personal_info, container, false);
        context = getContext();
        innitView();
        setting.setOnClickListener(this);
        user.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        order.setOnClickListener(this);
        order0.setOnClickListener(this);
        order1.setOnClickListener(this);
        order2.setOnClickListener(this);
        order3.setOnClickListener(this);
        order4.setOnClickListener(this);
        msg.setOnClickListener(this);
        favor.setOnClickListener(this);
        address.setOnClickListener(this);
        //friend.setOnClickListener(this);
        help_feedback.setOnClickListener(this);
        subway.setOnClickListener(this);
        couponReceive.setOnClickListener(this);
        context = person_frag.getContext();
        return person_frag;
    }

    public void innitView() {
        setting = (TextView) person_frag.findViewById(R.id.setting);
        user = (LinearLayout) person_frag.findViewById(R.id.user_info);
        login = (TextView) person_frag.findViewById(R.id.login);
        register = (TextView) person_frag.findViewById(R.id.register);
        user_name = (TextView) person_frag.findViewById(R.id.user_name);
        img = (RoundedImageView) person_frag.findViewById(R.id.user_img);
        order = (LinearLayout) person_frag.findViewById(R.id.user_order);
        order0 = (TextView) person_frag.findViewById(R.id.user_order0);
        order1 = (TextView) person_frag.findViewById(R.id.user_order1);
        order2 = (TextView) person_frag.findViewById(R.id.user_order2);
        order3 = (TextView) person_frag.findViewById(R.id.user_order3);
        order4 = (TextView) person_frag.findViewById(R.id.user_order4);
        subway = (LinearLayout) person_frag.findViewById(R.id.subway);
        msg = (LinearLayout) person_frag.findViewById(R.id.msg);
        favor = (LinearLayout) person_frag.findViewById(R.id.favor);
        address = (LinearLayout) person_frag.findViewById(R.id.address);
        //friend = (LinearLayout) person_frag.findViewById(R.id.friends);
        help_feedback = (LinearLayout) person_frag.findViewById(R.id.help_feedback);
        couponReceive = (LinearLayout) person_frag.findViewById(R.id.couponReceive);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        OnRefreshOnlineState();
    }

    public void OnRefreshOnlineState() {
        Log.d(TAG, "onrefresh");
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        if (mainApplication.isOnline()) {
            user_name.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            register.setVisibility(View.GONE);
            user_name.setText(mainApplication.getUsername());
            String avator = mainApplication.getAvator();
            if (avator != null && !avator.equals("")) {
                avator = RequestManager.getInstance(getContext()).getBaseUrl() + "/users/" + avator + "?type=0";
                Log.d(TAG, avator);
                Glide.with(getContext())
                        .load(avator)
                        .dontAnimate()
                        .placeholder(R.drawable.defult_user_img)
                        .into(img);
            }
        } else {
            user_name.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
            img.setImageResource(R.drawable.defult_user_img);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidd) {
        if (!hidd) {
            OnRefreshOnlineState();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SettingActivity.class);
                startActivityForResult(intent, START_USER_ACTIVITY);
                break;
            }
            case R.id.user_info: {
                MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                if (mainApplication.isOnline()) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), UserActivity.class);
                    startActivityForResult(intent, START_USER_ACTIVITY);
                    Log.d(TAG, "online");
                } else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "offline");
                }
                break;
            }
            case R.id.login: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivityForResult(intent, START_LOGIN_ACTIVITY);
                break;
            }
            case R.id.register: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RegisterActivity.class);
                startActivityForResult(intent, START_REGISTER_ACTIVITY);
                break;
            }
            case R.id.msg: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MsgActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.favor: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FavorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.address: {
                Intent intent = new Intent();
                intent.putExtra("type", "personalInfo");
                intent.setClass(getActivity(), AddressActivity.class);
                startActivity(intent);
                break;
            }
//            case R.id.friends: {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), FriendActivity.class);
//                startActivity(intent);
//                break;
//            }
            case R.id.help_feedback: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), HelpFeedbackActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.subway: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SubwayScheduleActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.user_order: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserOrderActivity.class);
                intent.putExtra("type", -1);
                startActivity(intent);
                break;
            }
            case R.id.user_order0: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserOrderActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            }
            case R.id.user_order1: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserOrderActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            }
            case R.id.user_order2: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserOrderActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            }
            case R.id.user_order3: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserOrderActivity.class);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            }
            case R.id.user_order4: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserOrderActivity.class);
                intent.putExtra("type", 4);
                startActivity(intent);
                break;
            }
            case R.id.couponReceive:{
                MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                if (mainApplication.isOnline()) {
                    // 领取优惠券
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.coupon_receive);
                    builder.setTitle("请输入兑换码");
                    View v = LayoutInflater.from(context).inflate(R.layout.coupon_receive_code, null);
                    builder.setView(v);

                    final EditText ipt = (EditText) v.findViewById(R.id.coupon_receive_code);
                    builder.setPositiveButton("兑换", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            couponInfo=null;
                            String gift_code = ipt.getText().toString().trim();
                            httpThread(gift_code);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    Log.d(TAG, "online");
                } else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "offline");
                }
                break;
            }
            default:
                break;
        }
    }
    private ProgressDialog pd;
    private Handler httpHandler = new Handler(new Handler.Callback() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public boolean handleMessage(Message msg) {
            //只要执行到这里就关闭对话框
            pd.dismiss();
            // TODO 收到请求
            showCouponReceiveResult();
            return false;
        }
    });
    private void httpThread(final String gift_code) {
        //构建一个下载进度条
        pd = ProgressDialog.show(context, "领取中", "领取中，请稍候......");
        // 私人赠送
       new Thread() {
            @Override
            public void run() {
                //在新线程里执行长耗时方法
                couponReceive(gift_code);
                //执行完毕后给handler发送一个空消息
                httpHandler.sendEmptyMessage(1);
            }
        }.start();
    }
    private void showCouponReceiveResult(){
        if(couponInfo!=null){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
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
            Toast.makeText(context, "优惠券领取失败，请稍后再试", Toast.LENGTH_LONG).show();
        }
    }

    private void couponReceive(String gift_code){
        try {
            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
            String phone = mainApplication.getPhone();
            final Bundle mBundle = new Bundle();
            mBundle.putString("phone", phone);
            mBundle.putString("share_code", gift_code);
            HttpUtil.receiveGiftCouponPrivateOkHttpRequest(mBundle, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    // 处理返回数据
                    JSONObject rsp = JSON.parseObject(responseData);
                    if (rsp.containsKey("succeed") && rsp.getInteger("succeed") == 1) {
                        // 领取成功, 解析
                        JSONObject coupon = rsp.getJSONObject("coupon_info");
                        couponInfo = new CouponInfo(coupon.getString("id"),
                                coupon.getInteger("type"), coupon.getString("coupon_name"),
                                coupon.getString("shop_id"), coupon.getString("seller_id"),
                                coupon.getString("expire_at"), coupon.getString("shop_tag"),
                                coupon.getString("image_url"), coupon.getString("shop_name"));
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: ERROR!");
                    Toast.makeText(context, "连接服务器失败，请重新尝试！", Toast.LENGTH_LONG).show();
                }
            });
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}