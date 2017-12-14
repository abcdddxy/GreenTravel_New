package com.example.zero.delegate;

import android.content.Context;
import android.widget.TextView;

import com.example.zero.bean.ScheduleBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.ItemViewDelegate;
import com.example.zero.util.RecycleViewHolder;

/**
 * Created by kazu_0122 on 2017/12/6.
 */

public class ScheduleLineDelegate implements ItemViewDelegate<ScheduleBean>
{

    @Override
    public int getItemViewLayoutId()
    {
        return R.layout.dialog_schedule;
    }

    @Override
    public boolean isForViewType(ScheduleBean item, int position)
    {
        return !item.isStation();
    }

    @Override
    public void convert(Context context, final RecycleViewHolder holder, ScheduleBean bean, int position)
    {
        holder.setText(R.id.line,"（" + bean.getLine() + "）");
        holder.setText(R.id.stStation,bean.getStation());
        holder.setText(R.id.enStation,bean.getFinal_st());
        holder.setText(R.id.timeArr,bean.getArr_time_str());
    }
}
