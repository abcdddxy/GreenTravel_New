package com.example.zero.delegate;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zero.bean.OrderBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.ItemViewDelegate;
import com.example.zero.util.RecycleViewHolder;

/**
 * Created by jojo on 2017/11/14.
 */

public class OrderAdressDelegate implements ItemViewDelegate<OrderBean> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_order_address;
    }

    @Override
    public boolean isForViewType(OrderBean item, int position) {
        return item.getItem().equals(OrderBean.ADDRESS) && position == 0;
    }

    @Override
    public void convert(final Context context, final RecycleViewHolder holder, OrderBean orderBean, final int position) {
        holder.setText(R.id.customer_name, orderBean.getCusName());
        holder.setText(R.id.customer_phone, orderBean.getCusPhone());
        holder.setImageResource(R.id.order_addr_icon, orderBean.getAddrIcon());
        holder.setText(R.id.free_receive_service, orderBean.getAddrService());
        TextView tip = holder.getView(R.id.order_addr_tip);
        if (orderBean.getTip()) {
            tip.setVisibility(View.VISIBLE);
            tip.setText(orderBean.getAddrTip());
        } else {
            tip.setVisibility(View.GONE);
        }
        final FrameLayout address = holder.getView(R.id.order_cus_addr);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.getOnInnerItemClickListener().onInnerItemClick(address, holder, position);
            }
        });
    }
}
