package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.exercise.PointBean;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/10/21.
 */
public class ArenaStagePointTreeAdapter extends BaseAdapter {
    private Context mContext;
    private List<PointBean> mList;
    private LayoutInflater layoutInflater;

    public ArenaStagePointTreeAdapter(Context mActivity, List<PointBean> points) {
        this.mContext = mActivity;
        this.mList = points;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int dayNightMode = SpUtils.getDayNightMode();
        ViewHolder viewHolder;
        final PointBean mPointBean = mList.get(position);
        final PointBean nextPointBean;
        if (position == mList.size() - 1) {
            nextPointBean = null;
        } else {
            nextPointBean = mList.get(position + 1);
        }
        if (mPointBean.level == 0) {
            if (convertView == null || convertView.getTag(R.id.first_level_point_holder_tag_id) == null) {
                convertView = layoutInflater.inflate(R.layout.stage_level_0_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(R.id.first_level_point_holder_tag_id, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.first_level_point_holder_tag_id);
            }
        } else if (mPointBean.level == 1) {
            if (convertView == null || convertView.getTag(R.id.second_level_point_holder_tag_id) == null) {
                convertView = layoutInflater.inflate(R.layout.stage_level_1_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(R.id.second_level_point_holder_tag_id, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.second_level_point_holder_tag_id);
            }
        } else {
            if (convertView == null || convertView.getTag(R.id.third_level_point_holder_tag_id) == null) {
                convertView = layoutInflater.inflate(R.layout.stage_level_2_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(R.id.third_level_point_holder_tag_id, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.third_level_point_holder_tag_id);
            }
            if (dayNightMode == 0) {    // 日间
                viewHolder.rootView.setBackgroundColor(Color.parseColor("#F9F9F9"));
            } else {                     // 夜间
                viewHolder.rootView.setBackgroundColor(Color.parseColor("#222222"));
            }
        }

        if (dayNightMode == 0) {
            viewHolder.tv_point_name.setTextColor(Color.parseColor("#000000"));
        } else {
            viewHolder.tv_point_name.setTextColor(Color.parseColor("#7E7E7E"));
        }

        viewHolder.tv_point_name.setText(mPointBean.name);
        if (mPointBean.qnum != 0) {
            viewHolder.tv_point_specific.setText("共" + mPointBean.qnum + "道，答对" + mPointBean.rnum + "道，正确率" + mPointBean.accuracy + "%，总计用时" + TimeUtils.getSecond2MinTime(mPointBean.times));
        } else {
            viewHolder.tv_point_specific.setText("共" + mPointBean.qnum + "道，答对" + mPointBean.rnum + "道，正确率" + 0 + "%，总计用时" + TimeUtils.getSecond2MinTime(mPointBean.times));
        }
        if (mPointBean.children == null || mPointBean.children.size() == 0) {
            viewHolder.iv_indicator.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.iv_indicator.setVisibility(View.VISIBLE);
        }
        if (nextPointBean == null || nextPointBean.level <= mPointBean.level) { // 收缩状态
            viewHolder.iv_indicator.setRotation(180);
        } else {
            viewHolder.iv_indicator.setRotation(0);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextPointBean == null || nextPointBean.level <= mPointBean.level) { // 收缩状态
                    if (mPointBean.level != 2) {
                        if (mPointBean.children != null && mPointBean.children.size() > 0) {
                            mList.addAll(position + 1, mPointBean.children);
                        }
                    }
                } else {
                    if (mPointBean.level != 2) {
                        int i = position + 1;
                        while (mList.size() > i && mList.get(i) != null && mList.get(i).level > mPointBean.level) {
                            mList.remove(i);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class ViewHolder {

        View rootView;
        @BindView(R.id.iv_indicator)
        ImageView iv_indicator;
        @BindView(R.id.tv_point_name)
        TextView tv_point_name;
        @BindView(R.id.tv_point_specific)
        TextView tv_point_specific;

        public ViewHolder(View view) {
            rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
