package com.example.zero.adapter;

import android.content.Context;

import com.example.zero.bean.RouteDetailBean;
import com.example.zero.bean.ScheduleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.CommonAdapter;
import com.example.zero.util.ViewHolder;

import java.util.List;

/**
 * Created by ZERO on 2018/1/16.
 */

public class RouteDetailDialogAdapter extends CommonAdapter<RouteDetailBean> {
    public RouteDetailDialogAdapter(Context context, List<RouteDetailBean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, int position) {
        if (mData.get(position).getStation().equals("abc")) {
            holder.setText(R.id.line, mData.get(position).getLine())
                    .setImageBitmap(R.id.route1_img, null)
                    .setText(R.id.stStation, null)
                    .setText(R.id.enStation, null)
                    .setText(R.id.stEdJ, null);
        } else {
            holder.setText(R.id.line, "（" + mData.get(position).getLine() + "）")
                    .setText(R.id.stStation, mData.get(position).getStation())
                    .setText(R.id.enStation, mData.get(position).getFinal_st());
        }
    }
}
