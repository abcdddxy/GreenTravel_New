package com.example.zero.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by ZERO on 2017/10/19.
 */

public class TitleRouteLayout extends LinearLayout {

    private RoundedImageView img;
    private Button more;

    public TitleRouteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_route, this);
        img = (RoundedImageView) findViewById(R.id.title_civ_icon);
        more = (Button) findViewById(R.id.title_btn_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "More info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setImg(Context context, String avator) {
        avator = RequestManager.getInstance(context).getBaseUrl() + "/users/" + avator + "?type=0";
        Glide.with(context)
                .load(avator)
                .dontAnimate()
                .placeholder(R.drawable.defult_user_img)
                .into(img);
    }
}
