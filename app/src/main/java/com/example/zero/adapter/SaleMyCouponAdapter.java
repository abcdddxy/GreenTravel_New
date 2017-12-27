package com.example.zero.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zero.bean.SaleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.view.CouponView;

import java.util.HashMap;
import java.util.List;

/**
 * MyCoupon适配器
 */

public class SaleMyCouponAdapter extends RecyclerView.Adapter<SaleMyCouponAdapter.SaleViewHolder> {

    private Context context;
    private List<SaleBean> dataList;
    private onRecycleItemClickListener mClickListener;

    public SaleMyCouponAdapter(Context context, List<SaleBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void setOnItemClickListener(onRecycleItemClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public SaleMyCouponAdapter.SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_sale_my_coupon, parent, false);
        SaleViewHolder holder = new SaleViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(SaleMyCouponAdapter.SaleViewHolder holder, int position) {
        holder.name.setText(dataList.get(position).getName());
        holder.price.setText(dataList.get(position).getPrice());
        holder.content.setText(dataList.get(position).getContent());
        holder.time.setText(dataList.get(position).getTime());
        Glide.with(context).load(dataList.get(position).getImage()).placeholder(R.drawable.loading).into(holder.img);
        if (dataList.get(position).getUseFlag()) {
            holder.btn.setText("已使用");
            holder.view.setVisibility(View.VISIBLE);
            holder.coupon.setClickable(false);
            holder.btn.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class SaleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private onRecycleItemClickListener mListener;
        private TextView name;
        private TextView price;
        private TextView content;
        private TextView time;
        private ImageView img;
        private FrameLayout coupon;
        private CouponView view;
        private Button btn;

        public SaleViewHolder(View itemView, onRecycleItemClickListener listener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.sale_my_name);
            price = (TextView) itemView.findViewById(R.id.sale_my_price);
            content = (TextView) itemView.findViewById(R.id.sale_my_content);
            time = (TextView) itemView.findViewById(R.id.sale_my_time);
            img = (ImageView) itemView.findViewById(R.id.sale_my_img);
            coupon = (FrameLayout) itemView.findViewById(R.id.sale_my_coupon);
            view = (CouponView) itemView.findViewById(R.id.sale_my_coupon_view);
            btn = (Button) itemView.findViewById(R.id.sale_my_btn);
            mListener = listener;
            coupon.setOnClickListener(this);
            btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sale_my_coupon:
                    mListener.onItemClick(view, getAdapterPosition());
                    break;
                case R.id.sale_my_btn:
                    mListener.onBtnClick(view, getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 创建一个回调接口
     */
    public interface onRecycleItemClickListener {
        void onItemClick(View view, int position);

        void onBtnClick(View view, int position);
    }

}
