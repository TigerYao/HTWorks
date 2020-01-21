package com.huatu.handheld_huatu.business.matchsmall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.exercise.PointBean;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter {

    private String[] colorLight = {
            "#F8E31C", "#FFD321", "#FFBC07", "#FF8047", "#FF6028", "#FA314C", "#FC5898", "#FF3CA7", "#FF53EB", "#DA5AFF",
            "#9472FD", "#7121FF", "#7076FA", "#4E8AF9", "#2CBCFF", "#19BFA4", "#27D46B", "#86C935", "#C5DF26", "#E5ED08"
    };
    private String[] colorNight = {
            "#7C720F", "#796411", "#7F5E04", "#773D23", "#742C12", "#761623", "#721B3D", "#731047", "#3F123A", "#40184C",
            "#2F2256", "#340F76", "#242755", "#1D3664", "#0D3D53", "#0B4F44", "#0E4925", "#314914", "#464F0E", "#404202"
    };

    private Context mContext;
    private List<PointBean> datas;

    public PointAdapter(List<PointBean> datas, Context context) {
        this.datas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PointHolder(LayoutInflater.from(mContext).inflate(R.layout.s_match_report_point_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PointHolder holder = (PointHolder) viewHolder;
        PointBean pointBean = datas.get(i);
        if (SpUtils.getDayNightMode() == 1) {        // 夜间
            holder.drawable.setColor(Color.parseColor(colorNight[i % colorNight.length]));
        } else {
            holder.drawable.setColor(Color.parseColor(colorLight[i % colorLight.length]));
        }
        holder.tvHeader.setText(pointBean.name.substring(0, 1));
        holder.tvTitle.setText(pointBean.name);

        holder.tvContent.setText("共" + pointBean.qnum
                + "道，答对" + pointBean.rnum
                + "道，正确率" + pointBean.accuracy
                + "%，总计用时" + pointBean.times
                + "秒");
    }

    @Override
    public int getItemCount() {
        return (datas != null && datas.size() > 0) ? datas.size() : 0;
    }

    class PointHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;
        TextView tvTitle;
        TextView tvContent;

        GradientDrawable drawable;

        PointHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);

            drawable = (GradientDrawable) tvHeader.getBackground();
        }
    }
}
