package com.example.zero.adapter;

import android.content.Context;

import com.example.zero.bean.ScheduleBean;
import com.example.zero.delegate.ScheduleLineDelegate;
import com.example.zero.delegate.ScheduleStationDelegate;
import com.example.zero.util.MultiItemTypeAdapter;

import java.util.List;

/**
 * Created by ZERO on 2017/11/20.
 */

public class ScheduleAdapter extends MultiItemTypeAdapter<ScheduleBean> {
    public ScheduleAdapter(Context context, List<ScheduleBean> datas) {
        super(context, datas);
        addItemViewDelegate(new ScheduleLineDelegate());
        addItemViewDelegate(new ScheduleStationDelegate());
    }
}
