package com.example.zero.delegate;

import android.content.Context;

import com.example.zero.bean.FavorItemBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.ItemViewDelegate;
import com.example.zero.util.RecycleViewHolder;

/**
 * Created by jojo on 2017/11/3.
 */

public class FavorLatterDelegate implements ItemViewDelegate<FavorItemBean> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.favor_item;
    }

    @Override
    public boolean isForViewType(FavorItemBean item, int position) {
        return item.getType().equals(FavorItemBean.ITEM) && position != 0;
    }

    @Override
    public void convert(Context context, RecycleViewHolder holder, FavorItemBean favorItemBean, int position) {
        holder.setText(R.id.spot_name, favorItemBean.getName());
        holder.setText(R.id.spot_content, favorItemBean.getContent());
    }

}
