package com.example.zero.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zero.activity.AddressActivity;
import com.example.zero.activity.FavorActivity;
import com.example.zero.activity.FriendActivity;
import com.example.zero.activity.HelpFeedbackActivity;
import com.example.zero.activity.LoginActivity;
import com.example.zero.activity.MsgActivity;
import com.example.zero.activity.RegisterActivity;
import com.example.zero.activity.SettingActivity;
import com.example.zero.activity.SubwayScheduleActivity;
import com.example.zero.activity.UserActivity;
import com.example.zero.activity.UserOrderActivity;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.makeramen.roundedimageview.RoundedImageView;

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

    private static final int START_LOGIN_ACTIVITY = 1;
    private static final int START_REGISTER_ACTIVITY = 2;
    private static final int START_USER_ACTIVITY = 3;
    private static final String TAG = "PersonalInfoFragment";

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
        friend.setOnClickListener(this);
        help_feedback.setOnClickListener(this);
        subway.setOnClickListener(this);
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
        friend = (LinearLayout) person_frag.findViewById(R.id.friends);
        help_feedback = (LinearLayout) person_frag.findViewById(R.id.help_feedback);
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
                avator = "http://10.108.120.31:8080/users/" + avator + "?type=0";
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
            case R.id.friends: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FriendActivity.class);
                startActivity(intent);
                break;
            }
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
            default:
                break;
        }
    }
}