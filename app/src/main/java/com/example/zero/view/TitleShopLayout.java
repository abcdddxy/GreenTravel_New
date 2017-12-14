package com.example.zero.view;

import android.content.ClipData;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zero.fragment.RouteFragment;
import com.example.zero.fragment.RouteFragmentController;
import com.example.zero.greentravel_new.R;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by ZERO on 2017/10/19.
 */

public class TitleShopLayout extends LinearLayout {
    private TextView text;
    private RoundedImageView img;
    private RelativeLayout rl;

    public TitleShopLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_shop, this);

        text = (TextView) findViewById(R.id.title_shop_text);
        img = (RoundedImageView) findViewById(R.id.title_shop_img);
        rl = (RelativeLayout) findViewById(R.id.title_shop);
    }

    public TextView getText() {
        return text;
    }

    public void setText(String t) {
        text.setText(t);
    }

    public RoundedImageView getImg() {
        return img;
    }

    public void setImg(Context context, String imgUrl) {
        Glide.with(context)
                .load(imgUrl)
                .dontAnimate()
                .transform(new GlideCircleTransform(getContext()))
                .placeholder(R.drawable.defult_user_img)
                .into(img);
    }

}
