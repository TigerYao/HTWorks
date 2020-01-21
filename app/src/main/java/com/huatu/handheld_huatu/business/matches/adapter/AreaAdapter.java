package com.huatu.handheld_huatu.business.matches.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBean;

import java.util.ArrayList;

public class AreaAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<SimulationScDetailBean.Area> areas;
    private OnItemClickListener listener;

    public AreaAdapter(Context mContext, ArrayList<SimulationScDetailBean.Area> areas, OnItemClickListener listener) {
        this.mContext = mContext;
        this.areas = areas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new AreaHolder(LayoutInflater.from(mContext).inflate(R.layout.sc_area_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AreaHolder holder = (AreaHolder) viewHolder;
        if (areas == null || position >= areas.size()) return;
        SimulationScDetailBean.Area bean = areas.get(position);
        holder.tvArea.setText(bean.value);
        if (bean.isSelect) {
            holder.tvArea.setTextColor(Color.parseColor("#FF3F47"));
        } else {
            holder.tvArea.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return areas != null ? areas.size() : 0;
    }

    class AreaHolder extends RecyclerView.ViewHolder {

        TextView tvArea;

        AreaHolder(View itemView) {
            super(itemView);
            tvArea = itemView.findViewById(R.id.tv_area);
            tvArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
