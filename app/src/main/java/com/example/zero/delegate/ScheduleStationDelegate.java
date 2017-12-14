package com.example.zero.delegate;

import android.content.Context;


import com.example.zero.bean.ScheduleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.ItemViewDelegate;
import com.example.zero.util.RecycleViewHolder;

/**
 * Created by kazu_0122 on 2017/12/6.
 */

public class ScheduleStationDelegate implements ItemViewDelegate<ScheduleBean>
{

    @Override
    public int getItemViewLayoutId()
    {
        return R.layout.item_schedule_station;
    }

    @Override
    public boolean isForViewType(ScheduleBean item, int position)
    {
        return item.isStation();
    }

    @Override
    public void convert(Context context, final RecycleViewHolder holder, ScheduleBean bean, int position)
    {
        holder.setText(R.id.schedule_station_line,"（" + bean.getLine() + "）");
        holder.setText(R.id.schedule_station_start,bean.getStation());
        holder.setText(R.id.schedule_station_final,bean.getFinal_st());
        holder.setImageResource(R.id.schedule_station_img, R.drawable.station);

        if(bean.getToggle()) {
            holder.setImageResource(R.id.schedule_station_toggle,R.drawable.slide_up);
        }
        else {
            holder.setImageResource(R.id.schedule_station_toggle,R.drawable.slide_down);
        }
    }
}