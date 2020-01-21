package com.huatu.handheld_huatu.business.faceteach.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.faceteach.bean.F2fJobData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class F2fJobSelectAdapter extends RecyclerView.Adapter<F2fJobSelectAdapter.ViewHolder> {
    private Context mContext;
    private List<F2fJobData> mData = new ArrayList<>();
    private int selectedPosition = -1;

    public F2fJobSelectAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<F2fJobData> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public F2fJobSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_f2f_job, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull F2fJobSelectAdapter.ViewHolder holder, final int position) {
        final F2fJobData mResult = mData.get(position);
        if (mResult != null) {
            if (position == selectedPosition) {
                holder.ll_for_red_bg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_selected_job));
            } else {
                holder.ll_for_red_bg.setBackground(null);
            }
            if (!TextUtils.isEmpty(mResult.yrsj)) {
                holder.ll_bureau_of_employment.setVisibility(View.VISIBLE);
                holder.tv_bureau_of_employment.setText(mResult.yrsj);
            } else {
                holder.ll_bureau_of_employment.setVisibility(View.GONE);

            }

            if (!TextUtils.isEmpty(mResult.bmmc)) {
                holder.ll_department.setVisibility(View.VISIBLE);
                holder.tv_department.setText(mResult.bmmc);
            } else {
                holder.ll_department.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mResult.zwmc)) {
                holder.ll_position.setVisibility(View.VISIBLE);
                holder.tv_position.setText(mResult.zwmc);
            } else {
                holder.ll_position.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mResult.zhuanye)) {
                holder.ll_major.setVisibility(View.VISIBLE);
                holder.tv_major.setText(mResult.zhuanye);
            } else {
                holder.ll_major.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mResult.gfgbzwdm)) {
                holder.tv_gov_job_number.setVisibility(View.VISIBLE);
                holder.tv_gov_job_number.setText(mResult.gfgbzwdm);
            } else {
                holder.tv_gov_job_number.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mResult.xueli)) {
                holder.tv_educational.setVisibility(View.VISIBLE);
                holder.tv_educational.setText(mResult.xueli);
            } else {
                holder.tv_educational.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mResult.sex)) {
                holder.tv_sex.setVisibility(View.VISIBLE);
                holder.tv_sex.setText(mResult.sex);
            } else {
                holder.tv_sex.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mResult.jhrs)) {
                holder.tv_count.setVisibility(View.VISIBLE);
                holder.tv_count.setText(mResult.jhrs);
            } else {
                holder.tv_count.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != selectedPosition) {
                        setSelectedPosition(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }

    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_for_red_bg)
        LinearLayout ll_for_red_bg;
        @BindView(R.id.ll_bureau_of_employment)
        LinearLayout ll_bureau_of_employment;
        @BindView(R.id.tv_bureau_of_employment)
        TextView tv_bureau_of_employment;
        @BindView(R.id.ll_department)
        LinearLayout ll_department;
        @BindView(R.id.tv_department)
        TextView tv_department;
        @BindView(R.id.ll_position)
        LinearLayout ll_position;
        @BindView(R.id.tv_position)
        TextView tv_position;
        @BindView(R.id.ll_major)
        LinearLayout ll_major;
        @BindView(R.id.tv_major)
        TextView tv_major;
        @BindView(R.id.ll_bottom_require)
        LinearLayout ll_bottom_require;
        @BindView(R.id.tv_gov_job_number)
        TextView tv_gov_job_number;
        @BindView(R.id.tv_educational)
        TextView tv_educational;
        @BindView(R.id.tv_sex)
        TextView tv_sex;
        @BindView(R.id.tv_count)
        TextView tv_count;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
