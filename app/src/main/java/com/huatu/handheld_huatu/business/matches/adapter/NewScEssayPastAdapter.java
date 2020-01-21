package com.huatu.handheld_huatu.business.matches.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationEssayPastPaperResult;

import java.util.ArrayList;

/**
 * Created by chq on 2019/1/23.
 */

public class NewScEssayPastAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<SimulationEssayPastPaperResult> data;
    private OnEsItemClickListener onEsItemClickListener;

    public NewScEssayPastAdapter(Context context, ArrayList<SimulationEssayPastPaperResult> data, OnEsItemClickListener onEsItemClickListener) {
        this.mContext = context;
        this.data = data;
        this.onEsItemClickListener = onEsItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_exam_paper_essay_new, parent, false);
        return new RealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderX, final int position) {
        RealViewHolder holder = (RealViewHolder) holderX;
        final SimulationEssayPastPaperResult item = data.get(position);

        if (!TextUtils.isEmpty(item.paperName)) {
            holder.text_name.setText(item.paperName);
        } else {
            holder.text_name.setText("");
        }
        if (item.correctSum != 0) {
            holder.text_number_people.setVisibility(View.VISIBLE);
            holder.text_number_people.setText(item.correctSum + "人已考");
        } else {
            holder.text_number_people.setVisibility(View.GONE);
        }
        if (item.limitTime != 0) {
            holder.text_number_time.setVisibility(View.VISIBLE);
            holder.text_number_time.setText("总时间" + item.limitTime / 60 + "分钟");
        } else {
            holder.text_number_time.setVisibility(View.GONE);
        }
        if (item.courseId != 0) {
            holder.iv_paper_course.setVisibility(View.VISIBLE);
        } else {
            holder.iv_paper_course.setVisibility(View.GONE);
        }
        if (item.correctNum != 0) {
            holder.rl_flag.setVisibility(View.VISIBLE);
            holder.rl_flag.setText("已批改" + item.correctNum + "次");
            holder.rl_flag.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
            holder.rl_flag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_new_paper_complete));

        } else {
            if (item.recentStatus == 0) {
                holder.rl_flag.setVisibility(View.INVISIBLE);
            } else if (item.recentStatus == 1) {
                holder.rl_flag.setVisibility(View.VISIBLE);
                holder.rl_flag.setText("未完成");
                holder.rl_flag.setTextColor(ContextCompat.getColor(mContext, R.color.red250));
                holder.rl_flag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_new_paper));

            } else if (item.recentStatus == 2) {
                holder.rl_flag.setVisibility(View.VISIBLE);
                holder.rl_flag.setText("已交卷");
                holder.rl_flag.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
                holder.rl_flag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_new_paper_complete));

            }

        }
        holder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEsItemClickListener != null) {
                    onEsItemClickListener.onEsItemClick(position);
                }
            }
        });

        holder.iv_paper_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BaseIntroActivity.class);
                intent.putExtra("rid", item.courseId+"");
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    private class RealViewHolder extends RecyclerView.ViewHolder {

        private View root_view;
        private TextView iv_paper_course;
        private TextView text_name;
        private TextView text_number_people;
        private TextView text_number_time;
        private TextView rl_flag;

//        private Button btn;

        private RealViewHolder(View itemView) {
            super(itemView);
            root_view = itemView.findViewById(R.id.root_view);
            iv_paper_course = itemView.findViewById(R.id.iv_paper_course);
            text_name = itemView.findViewById(R.id.text_name);
            text_number_people = itemView.findViewById(R.id.text_number_people);
            text_number_time = itemView.findViewById(R.id.text_number_time);
            rl_flag = itemView.findViewById(R.id.rl_flag);
//            btn = itemView.findViewById(R.id.btn);
        }
    }

    public interface OnEsItemClickListener {
        void onEsItemClick(int position);
    }

}
