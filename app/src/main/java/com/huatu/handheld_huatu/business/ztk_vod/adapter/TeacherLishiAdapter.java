package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.activity.TeacherEvaluateActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherLishiBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ht-djd on 2017/9/15.
 */

public class TeacherLishiAdapter extends BaseAdapter {
    private String TAG = "TeacherLishiAdapter";
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<TeacherLishiBean.history_course> mTeacherLishiList = new ArrayList<>();
    private String teacherId;

    public TeacherLishiAdapter(Context mContext, ArrayList<TeacherLishiBean.history_course> mTeacherLishiList, String teacherId) {
        this.mContext = mContext;
        this.mTeacherLishiList = mTeacherLishiList;
        inflater = LayoutInflater.from(mContext);
        this.teacherId = teacherId;
    }

    @Override
    public int getCount() {
        return mTeacherLishiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTeacherLishiList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       final TeacherLishiBean.history_course  history_course = mTeacherLishiList.get(position);
        ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_teacher_evaluate, parent, false);
            holder = new TeacherLishiAdapter.ViewHolder(convertView);
        } else {
            holder = (TeacherLishiAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_evaluate_title.setText(history_course.title);
        if (history_course.avgscore.equals("0")){
            holder.tv_evaluate_defen.setText("(暂无评分)");
        }else {
            holder.tv_evaluate_defen.setText(history_course.avgscore+"分");
        }
        if (mTeacherLishiList.get(position).star == 0) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 1) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 2) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 3) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 4) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 5) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 6) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 7) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 8) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mTeacherLishiList.get(position).star == 9) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_bbxing);
        } else if (mTeacherLishiList.get(position).star == 10) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_xing);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherEvaluateActivity.newIntent(mContext, history_course.NetClassId,history_course.rid,TAG);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_evaluate_title)
        TextView tv_evaluate_title;
        @BindView(R.id.iv_star1)
        ImageView iv_star1;
        @BindView(R.id.iv_star2)
        ImageView iv_star2;
        @BindView(R.id.iv_star3)
        ImageView iv_star3;
        @BindView(R.id.iv_star4)
        ImageView iv_star4;
        @BindView(R.id.iv_star5)
        ImageView iv_star5;
        @BindView(R.id.tv_evaluate_defen)
        TextView tv_evaluate_defen;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
