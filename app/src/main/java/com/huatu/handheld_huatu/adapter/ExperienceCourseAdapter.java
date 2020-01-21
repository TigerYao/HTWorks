package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.lessons.bean.AllCourseData;
import com.huatu.handheld_huatu.utils.CommonUtils;

import java.util.List;

/**
 * Created by saiyuan on 2019/2/26.
 */

public class ExperienceCourseAdapter extends RecyclerView.Adapter<ExperienceCourseAdapter.ViewHolder> {

    private List<AllCourseData> mData;
    private Context mContext;

    public ExperienceCourseAdapter(Context context, List<AllCourseData> mLessionlist) {
        mContext = context;
        mData = mLessionlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExperienceCourseAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fragment_course_experience, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AllCourseData mResult = mData.get(position);
        if (mResult != null) {
            if (mResult.title != null) {
                holder.tv_type.setText(mResult.title);
            }
            if (mResult.data != null && mResult.data.size() != 0) {
                holder.adapter.setDate(mResult.data);
            } else {
                holder.adapter.clear();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clearAndRefresh() {
        mData.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_type;
        RecyclerView gv_course;

        ExperienceChildAdapter adapter;

        ViewHolder(View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            gv_course = itemView.findViewById(R.id.gv_course);

            int colum;
            if (CommonUtils.isPad(mContext)) {
                colum = 3;
            } else {
                colum = 2;
            }

            gv_course.setNestedScrollingEnabled(false);
            gv_course.setLayoutManager(new GridLayoutManager(mContext, colum));

            adapter = new ExperienceChildAdapter(mContext);

            gv_course.setAdapter(adapter);
        }
    }
}
