package com.example.zero.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zero.adapter.FriendItemAdapter;
import com.example.zero.bean.FriendItemBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.view.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojo on 2017/10/24.
 */

public class FriendActivity extends AppCompatActivity {

    private TextView backArrow;
    private ImageView friend_img;
    private TextView friend_name;
    private TextView friend_phone;
    private RecyclerView friend_recv;
    private List<FriendItemBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        innitView();
        showFriendData();
        /**
         *  监听器
         */
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void innitView() {
        backArrow = (TextView) findViewById(R.id.friends_back_arrow);
        friend_recv = (RecyclerView) findViewById(R.id.friends_recv);
        friend_recv.setLayoutManager(new LinearLayoutManager(this));
        friend_img = (ImageView) findViewById(R.id.friends_img);
        friend_name = (TextView) findViewById(R.id.spot_name);
        friend_phone = (TextView) findViewById(R.id.spot_content);
    }

    private void showFriendData() {
        FriendItemBean friendItemBean = new FriendItemBean();
        friendItemBean.setText("jojo", "手机号：14235467636", R.drawable.haidilao);
        for (int i = 0; i < 20; i++) {
            dataList.add(friendItemBean);
        }
        FriendItemAdapter adapter = new FriendItemAdapter(this, dataList);
        friend_recv.setAdapter(adapter);
        friend_recv.addItemDecoration(new DividerDecoration(this));
    }
}
