package com.example.zero.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zero.bean.AdvDestinSearchBean;
import com.example.zero.bean.RouteSearchBean;
import com.example.zero.bean.ScheduleBean;
import com.example.zero.delegate.AdvStationDelegate;
import com.example.zero.delegate.AdvStoreDelegate;
import com.example.zero.delegate.ScheduleLineDelegate;
import com.example.zero.delegate.ScheduleStationDelegate;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.CommonAdapter;
import com.example.zero.util.MultiItemTypeAdapter;
import com.example.zero.util.ViewHolder;

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
