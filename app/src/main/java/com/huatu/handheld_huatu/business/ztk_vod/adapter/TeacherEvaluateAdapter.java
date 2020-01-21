package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherPingjiaBean;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht-djd on 2017/9/16.
 */

public class TeacherEvaluateAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<TeacherPingjiaBean.DataBean.ResultBean.
            EvaluationBean> mEvaluateList = new ArrayList<>();
    private List<ImageView> starList = new ArrayList<>();
    public TeacherEvaluateAdapter(Context context, ArrayList<TeacherPingjiaBean.DataBean.ResultBean.
            EvaluationBean> mEvaluateList) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.mEvaluateList = mEvaluateList;


    }

    @Override
    public int getCount() {
        return mEvaluateList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TeacherPingjiaBean.DataBean.ResultBean.EvaluationBean evaluationBean = mEvaluateList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_teacher_evaluate_list, parent, false);
            holder = new TeacherEvaluateAdapter.ViewHolder(convertView);
        } else {
            holder = (TeacherEvaluateAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_username.setText(evaluationBean.getUsername());
        if (!StringUtils.isEmpty(evaluationBean.getCourseRemark())){
            holder.tv_content.setText(evaluationBean.getCourseRemark());
            holder.tv_content.setVisibility(View.VISIBLE);
        }else{
            holder.tv_content.setVisibility(View.GONE);
        }
        holder.tv_date.setText(evaluationBean.getRateDate());
        /*try {
           Boolean isToday = IsToday(evaluationBean.getRateDate());
            if (isToday){
               String date =  evaluationBean.getRateDate().substring(10);
                holder.tv_date.setText("今天 "+date);
            }else{
                holder.tv_date.setText(evaluationBean.getRateDate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        starList.add(holder.iv_star1);
        starList.add(holder.iv_star2);
        starList.add(holder.iv_star3);
        starList.add(holder.iv_star4);
        starList.add(holder.iv_star5);
        if (mEvaluateList.get(position).getStar() == 0) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 1) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 2) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 3) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 4) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 5) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 6) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_kxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 7) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_bbxing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 8) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_kxing);
        } else if (mEvaluateList.get(position).getStar() == 9) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_bbxing);
        } else if (mEvaluateList.get(position).getStar() == 10) {
            holder.iv_star1.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star2.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star3.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star4.setBackgroundResource(R.drawable.teacher_xing);
            holder.iv_star5.setBackgroundResource(R.drawable.teacher_xing);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_username)
        TextView tv_username;
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
        @BindView(R.id.tv_date)
        TextView tv_date;
        @BindView(R.id.tv_content)
        TextView tv_content;

        ViewHolder(View view) {

            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
