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
import com.huatu.handheld_huatu.business.ztk_vod.activity.CourseEvaluateActivity;
import com.huatu.handheld_huatu.business.ztk_vod.activity.TeacherListDetailActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherListBeans;
import com.huatu.handheld_huatu.utils.CircleTransform;
import com.huatu.handheld_huatu.utils.ImageLoad;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht-djd on 2017/9/5.
 *
 */

public class TeacherListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TeacherListBeans> mTeacherList;

    private LayoutInflater inflater;

    public TeacherListAdapter(Context context, ArrayList<TeacherListBeans> mTeacherList) {
        this.mContext = context;
        this.mTeacherList = mTeacherList != null ? mTeacherList
                : new ArrayList<TeacherListBeans>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTeacherList.size();
    }

    @Override
    public Object getItem(int postion) {
        return postion;
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TeacherListBeans resultBean = mTeacherList.get(position);
        TeacherListAdapter.ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_teacher_list, parent, false);
            holder = new TeacherListAdapter.ViewHolder(convertView);
        } else {
            holder = (TeacherListAdapter.ViewHolder) convertView.getTag();
        }
      /*  Glide.with(mContext)
                .load(resultBean.roundPhoto)
                .transform(new CircleTransform(mContext))
                .placeholder(R.mipmap.image11)
                .error(R.mipmap.image11)
                .skipMemoryCache(false)
                .placeholder(R.mipmap.image11)
                .crossFade()
                .error(R.mipmap.image11)
                .into(holder.iv_teacher);*/

        ImageLoad.displaynoCacheUserAvater(mContext,resultBean.roundPhoto,holder.iv_teacher,R.mipmap.user_default_avater);
        if (!TextUtils.isEmpty(resultBean.TeacherName)) {

            holder.tv_teachername.setText(resultBean.TeacherName);
        }
        if (!TextUtils.isEmpty(resultBean.goodat)) {

            holder.tv_teachergoodat.setText(resultBean.goodat);
        }
        if (!TextUtils.isEmpty(resultBean.style)) {

            holder.tv_teacherstyle.setText(resultBean.style);
        }
        if (!TextUtils.isEmpty(resultBean.score) && !"0".equals(resultBean.score)){
            holder.tv_teachernum.setText(resultBean.score+"分");
        }else{
            holder.tv_teachernum.setText("(暂无评分)");
        }
            /*if (resultBean.score.equals("0")){
                holder.tv_teachernum.setText("(暂无评分)");
            }else{
                holder.tv_teachernum.setText(resultBean.score+"分");
            }
*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherListDetailActivity.newIntent(mContext, mTeacherList.get(position).TeacherId,
                        mTeacherList.get(position).TeacherName, mTeacherList.get(position).roundPhoto);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_teacher)
        ImageView iv_teacher;
        @BindView(R.id.tv_teachername)
        TextView tv_teachername;
        @BindView(R.id.tv_teachernum)
        TextView tv_teachernum;
        @BindView(R.id.tv_teachergoodat)
        TextView tv_teachergoodat;
        @BindView(R.id.tv_teacherstyle)
        TextView tv_teacherstyle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }

}
