package com.example.zero.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.zero.bean.AddressBean;
import com.example.zero.greentravel_new.R;

import java.util.List;

/**
 * Created by jojo on 2017/11/15.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<AddressBean> dataList;
    private onRecycleItemClickListener mClickListener;
    private int lastSelectPos = -1;

    public AddressAdapter(Context context, List<AddressBean> addrDataList) {
        this.context = context;
        this.dataList = addrDataList;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isSelected()) {
                lastSelectPos = i;
            }
        }
    }

    public void setOnItemClickListener(onRecycleItemClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        AddressViewHolder holder = new AddressViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddressAdapter.AddressViewHolder holder, final int position) {
        holder.name.setText(dataList.get(position).getName());
        holder.phone.setText(dataList.get(position).getPhone());
        holder.radioButton.setText(dataList.get(position).getDefaultAddr());
        if (dataList.get(position).isSelected()) {
            holder.radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkbox_on, 0, 0, 0);
        } else {
            holder.radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkbox_off, 0, 0, 0);
        }
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (AddressBean addressBean : dataList) {
                    addressBean.setSelect(false);
                    addressBean.setChange(false);
                    addressBean.setDefaultAddr("设为默认");
                }
                if (lastSelectPos != position || lastSelectPos == -1) {
                    dataList.get(position).setChange(true);
                    lastSelectPos = position;
                }
                dataList.get(position).setSelect(true);
                dataList.get(position).setDefaultAddr("默认地址");
                notifyDataSetChanged();
                mClickListener.onRadioButton(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private onRecycleItemClickListener mListener;
        private TextView name, phone;
        private RadioButton radioButton;
        private TextView edit, delete;
        private LinearLayout address;

        public AddressViewHolder(View itemView, onRecycleItemClickListener listener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.address_customer_name);
            phone = (TextView) itemView.findViewById(R.id.address_customer_phone);
            radioButton = (RadioButton) itemView.findViewById(R.id.address_rb);
            edit = (TextView) itemView.findViewById(R.id.address_edit);
            delete = (TextView) itemView.findViewById(R.id.address_delete);
            address = (LinearLayout) itemView.findViewById(R.id.address_linearlayout);
            mListener = listener;
            address.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.address_linearlayout:
                    mListener.onItemClick(view, getAdapterPosition());
                    break;
                case R.id.address_edit:
                    mListener.onItem1Click(view, getAdapterPosition());
                    break;
                case R.id.address_delete:
                    mListener.onItem2Click(view, getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    public interface onRecycleItemClickListener {
        void onItemClick(View view, int position);

        void onItem1Click(View view, int position);

        void onItem2Click(View view, int position);

        void onRadioButton(View view, int position);
    }
}
