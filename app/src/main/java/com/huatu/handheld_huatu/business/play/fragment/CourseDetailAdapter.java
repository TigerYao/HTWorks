package com.huatu.handheld_huatu.business.play.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;

public class CourseDetailAdapter extends BaseAdapter{
    private CourseDetailBean courseDetailBean;
    private Context mCtx;
    private boolean mIsExpand = false;

    public CourseDetailAdapter(Context ctx, CourseDetailBean courseDetailBean) {
        this.courseDetailBean = courseDetailBean;
        mCtx = ctx;
    }

    public void setCourseDetailBean(CourseDetailBean courseDetailBean) {
        this.courseDetailBean = courseDetailBean;
        mIsExpand = false;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int length = courseDetailBean == null ? 0 : courseDetailBean.columnHeaders.size();
        return length > 3 && !mIsExpand ? 3 : length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mCtx).inflate(R.layout.layout_course_detial_item, null);
        TextView nameView = view.findViewById(R.id.name);
        TextView valueView = view.findViewById(R.id.value);
        String name = courseDetailBean.columnHeaders.get(i);
        String value = courseDetailBean.columnDetails.get(i);
        nameView.setText(name);
        valueView.setText(value);
        return view;
    }

    public boolean isExpand() {
        return mIsExpand;
    }

    public void expandList(){
        mIsExpand = !mIsExpand;
        notifyDataSetChanged();
    }

}
