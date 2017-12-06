package com.example.zero.delegate;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;
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
    public void convert(Context context, RecycleViewHolder holder, AdvDestinSearchBean bean, int position) {
        holder.setText(R.id.adv_destin_store_title, bean.getTitle());
        holder.setText(R.id.adv_destin_store_comments, bean.getComments() + "条评论");
        holder.setText(R.id.adv_destin_store_price, "￥" + bean.getPrice());
        holder.setText(R.id.adv_destin_store_address, bean.getAddress() + "  距离您" + bean.getDistance() + "米");
        // holder.setText(R.id.adv_destin_store_distance, );
        //  holder.setText(R.id.adv_destin_store_phone, "电话：" + bean.getPhone());
        holder.setRating(R.id.adv_destin_store_rate, bean.getRate());
        holder.setTextList(R.id.adv_destin_store_labels, bean.getLabels());

        final LabelsView lv = holder.getView(R.id.adv_destin_store_labels);
        lv.setSelects(0);
//        lv.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
//            @Override
//            public void onLabelClick(View label, String labelText, int position) {
//                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
//                lv.setSelects(0);
//            }
//        });

        ImageView img = holder.getView(R.id.adv_destin_store_img);
        Glide.with(context)
                .load(bean.getImg())
                .dontAnimate()
                .placeholder(R.drawable.loading)
                .into(img);
    }
}