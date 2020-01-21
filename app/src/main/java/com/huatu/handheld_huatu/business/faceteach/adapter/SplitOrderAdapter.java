package com.huatu.handheld_huatu.business.faceteach.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.faceteach.bean.SplitOrderBean;

import java.util.ArrayList;

public class SplitOrderAdapter extends RecyclerView.Adapter<SplitOrderAdapter.SplitHolder> {

    private ArrayList<SplitOrderBean> data;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isConfirm = false;

    public SplitOrderAdapter(Context context, ArrayList<SplitOrderBean> data, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(ArrayList<SplitOrderBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SplitHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new SplitHolder(LayoutInflater.from(context).inflate(R.layout.split_order_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SplitHolder holder, int position) {
        SplitOrderBean splitOrderBean = data.get(position);
        holder.tvTitle.setText("支付" + getUpNum(position + 1) + "：");
        holder.tvSplitPrice.setText("¥" + splitOrderBean.getPrice());
        if (isConfirm && splitOrderBean.getState() == -1){
            splitOrderBean.sonstate = "0";
        }else if (!isConfirm && splitOrderBean.getState() != 1){
            splitOrderBean.sonstate = "-1";
        }

        // -1、正在编辑 0、未支付 1、已支付
        switch (splitOrderBean.getState()) {
            case -1:
                holder.tvEdit.setText("编辑");
                holder.tvEdit.setTextColor(Color.parseColor("#88AAE1"));
                break;
            case 0:
                holder.tvEdit.setText("去支付");
                holder.tvEdit.setTextColor(Color.parseColor("#FF3F47"));
                break;
            case 1:
                holder.tvEdit.setText("已支付");
                holder.tvEdit.setTextColor(Color.parseColor("#E1E1E1"));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }

    class SplitHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvSplitPrice;
        TextView tvEdit;

        SplitHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSplitPrice = itemView.findViewById(R.id.tv_split_price);
            tvEdit = itemView.findViewById(R.id.tv_edit);

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    private String[] numStr = new String[]{"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private String getUpNum(int num) {
        if (num <= 10) {
            return numStr[num];
        } else if (num < 100) {
            return numStr[num / 10] + numStr[10] + numStr[num % 10];
        } else {
            return String.valueOf(num);
        }
    }
}
