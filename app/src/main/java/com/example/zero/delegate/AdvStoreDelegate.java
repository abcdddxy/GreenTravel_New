package com.example.zero.delegate;

import android.app.Activity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;
import com.example.zero.activity.StationDisplayActivity;
import com.example.zero.bean.AdvDestinSearchBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.ItemViewDelegate;
import com.example.zero.util.RecycleViewHolder;

/**
 * Created by kazu_0122 on 2017/10/19.
 */

public class AdvStoreDelegate implements ItemViewDelegate<AdvDestinSearchBean> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adv_destin_store;
    }

    @Override
    public boolean isForViewType(AdvDestinSearchBean item, int position) {
        return item.isStore();
    }

    @Override
    public void convert(RecycleViewHolder holder, AdvDestinSearchBean bean, int position) {
        holder.setText(R.id.adv_destin_store_title, bean.getTitle() + "商家");
        holder.setText(R.id.adv_destin_store_comments, bean.getComments() + "条评论");
        holder.setText(R.id.adv_destin_store_price, "￥" + bean.getPrice());
        holder.setText(R.id.adv_destin_store_keyword, bean.getTime());
        holder.setRating(R.id.adv_destin_store_rate, bean.getRate());
        holder.setTextList(R.id.adv_destin_store_labels, bean.getLabels());
        holder.setImageResource(R.id.adv_destin_store_img, bean.getImg());
        holder.getView(R.id.adv_destin_store_img);
        final LabelsView lv = holder.getView(R.id.adv_destin_store_labels);
        lv.setSelects(0);
        lv.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(View label, String labelText, int position) {
                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
                lv.setSelects(0);
            }
        });
    }
}