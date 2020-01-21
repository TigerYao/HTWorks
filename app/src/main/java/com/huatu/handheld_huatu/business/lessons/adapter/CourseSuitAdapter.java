package com.huatu.handheld_huatu.business.lessons.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.lessons.bean.CourseSuitChild;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CHQ on 2017/4/15.
 */

public class CourseSuitAdapter extends BaseAdapter {
    private Context mContext;
    private List<CourseSuitChild> mCourseSuitChild;
    private int courseType = 1;
    private LayoutInflater inflater;
    public CourseSuitAdapter(Context context){
        this.mContext=context;
        mCourseSuitChild=new ArrayList<>();
        inflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return mCourseSuitChild.size();
    }

    @Override
    public Object getItem(int position) {
        return mCourseSuitChild.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_activtiy_course_suit, parent, false);
            holder = new CourseSuitAdapter.ViewHolder(convertView);
        } else {
            holder = (CourseSuitAdapter.ViewHolder) convertView.getTag();
        }

        CourseSuitChild child=mCourseSuitChild.get(position);
        holder.tv_num.setText((position+1)+"");
        holder.tv_suit_item_title.setText(child.Title);
        if(courseType == 1) {
            holder.tv_suit_item_class.setText(child.StartDate + "-"
                    + child.EndDate + "(" + child.TimeLength + "课时" + ")");
        } else {
            holder.tv_suit_item_class.setText(child.TimeLength + "课时");
        }
        return convertView;
    }
    static class ViewHolder{
        @BindView(R.id.tv_num)
        TextView tv_num;
        @BindView(R.id.tv_suit_item_title)
        TextView tv_suit_item_title;
        @BindView(R.id.tv_suit_item_class)
        TextView tv_suit_item_class;
        @BindView(R.id.img_suit_item_go)
        ImageView img_suit_item_go;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
    public void setData(int type, ArrayList<CourseSuitChild> mChildList){
        courseType = type;
        mCourseSuitChild=mChildList;
        notifyDataSetChanged();
    }
}
