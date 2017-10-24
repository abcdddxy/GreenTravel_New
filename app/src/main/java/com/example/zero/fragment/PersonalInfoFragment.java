package com.example.zero.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zero.activity.FavorActivity;
import com.example.zero.activity.FriendActivity;
import com.example.zero.activity.HelpFeedbackActivity;
import com.example.zero.activity.LoginActivity;
import com.example.zero.activity.MsgActivity;
import com.example.zero.activity.RegisterActivity;
import com.example.zero.activity.SettingActivity;
import com.example.zero.activity.UserActivity;
import com.example.zero.greentravel_new.R;

/**
 * Created by jojo on 2017/9/22.
 */

public class PersonalInfoFragment extends Fragment {
    private View person_frag;
    private Context context;
    private TextView setting;
    private LinearLayout msg;
    private LinearLayout user;
    private TextView login, register, user_name;
    private LinearLayout favor;
    private LinearLayout friend;
    private LinearLayout help_feedback;

    private static final int START_LOGIN_ACTIVITY = 1;
    private static final int START_REGISTER_ACTIVITY = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        person_frag = inflater.inflate(R.layout.fragment_personal_info, container, false);
        innitView();
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivityForResult(intent, START_LOGIN_ACTIVITY);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RegisterActivity.class);
                startActivityForResult(intent, START_REGISTER_ACTIVITY);
            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MsgActivity.class);
                startActivity(intent);
            }
        });
        favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FavorActivity.class);
                startActivity(intent);
            }
        });
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FriendActivity.class);
                startActivity(intent);
            }
        });
        help_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), HelpFeedbackActivity.class);
                startActivity(intent);
            }
        });
        context = person_frag.getContext();
        return person_frag;
    }

    public void innitView() {
        setting = (TextView) person_frag.findViewById(R.id.setting);
        user = (LinearLayout) person_frag.findViewById(R.id.user_info);
        login = (TextView) person_frag.findViewById(R.id.login);
        register = (TextView) person_frag.findViewById(R.id.register);
        user_name = (TextView) person_frag.findViewById(R.id.user_name);
        msg = (LinearLayout) person_frag.findViewById(R.id.msg);
        favor = (LinearLayout) person_frag.findViewById(R.id.favor);
        friend = (LinearLayout) person_frag.findViewById(R.id.friends);
        help_feedback = (LinearLayout) person_frag.findViewById(R.id.help_feedback);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case START_LOGIN_ACTIVITY:
                    user_name.setText(data.getStringExtra("User name"));
                    user_name.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    register.setVisibility(View.GONE);
                    break;
                case START_REGISTER_ACTIVITY:
                    user_name.setText(data.getStringExtra("User name"));
                    user_name.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    register.setVisibility(View.GONE);
                    break;
            }
        }
    }
}