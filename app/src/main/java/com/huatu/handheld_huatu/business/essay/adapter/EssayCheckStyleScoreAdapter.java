package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;

import java.util.ArrayList;

public class EssayCheckStyleScoreAdapter extends RecyclerView.Adapter<EssayCheckStyleScoreAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CheckDetailBean.ScoreListEntity> datas;
    private final Typeface fontType;

    public EssayCheckStyleScoreAdapter(@NonNull Context context, ArrayList<CheckDetailBean.ScoreListEntity> datas) {
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
        fontType = Typeface.createFromAsset(context.getAssets(), "font/Heavy.ttf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_essay_check_style_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckDetailBean.ScoreListEntity data = datas.get(position);
        holder.tvContent.setText("(" + (position + 1) + ")  (" + data.scorePoint + ")");
        if (data.type != 3) {
            holder.tvScore.setText(("+" + data.score + "分").replace(".0", ""));
            holder.tvScore.setBackgroundResource(R.drawable.ess_check_score_content_bg);
        } else {
            holder.tvScore.setText(("-" + data.score + "分").replace(".0", ""));
            holder.tvScore.setBackgroundResource(R.drawable.ess_check_score_style_bg);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        TextView tvScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvScore = (TextView) itemView.findViewById(R.id.tv_score);
            tvContent.setTypeface(fontType);
        }
    }
}
