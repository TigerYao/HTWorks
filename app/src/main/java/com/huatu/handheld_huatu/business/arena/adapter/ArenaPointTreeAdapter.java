package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.exercise.PointBean;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/10/21.
 */
public class ArenaPointTreeAdapter extends BaseAdapter {
    private Context mContext;
    private List<PointBean> mList;
    private LayoutInflater layoutInflater;
    private int requestType;

    public ArenaPointTreeAdapter(Context mActivity, List<PointBean> points) {
        this.mContext = mActivity;
        this.mList = points;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public ArenaPointTreeAdapter(Context mActivity, int type, List<PointBean> points) {
        this.mContext = mActivity;
        this.mList = points;
        this.requestType = type;
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
                convertView = layoutInflater.inflate(R.layout.item_level_0_point_listview_answer_report, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(R.id.first_level_point_holder_tag_id, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.first_level_point_holder_tag_id);
            }
        } else if (mPointBean.level == 1) {
            if (convertView == null || convertView.getTag(R.id.second_level_point_holder_tag_id) == null) {
                convertView = layoutInflater.inflate(R.layout.item_level_1_point_listview_answer_report, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(R.id.second_level_point_holder_tag_id, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.second_level_point_holder_tag_id);
            }
        } else {
            if (convertView == null || convertView.getTag(R.id.third_level_point_holder_tag_id) == null) {
                convertView = layoutInflater.inflate(R.layout.item_level_2_point_listview_answer_report, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(R.id.third_level_point_holder_tag_id, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.third_level_point_holder_tag_id);
            }
        }

        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {
            viewHolder.tv_point_specific.setTextSize(12);
            viewHolder.tv_point_name.setTextSize(14);
        }

        viewHolder.tv_point_name.setText(mPointBean.name);
        String time = TimeUtils.getSecond2HourMinTimeOther(mPointBean.times);
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {
            viewHolder.tv_point_specific.setText("共" + mPointBean.qnum + "道，" +
                    "答对" + mPointBean.rnum + "道，" +
                    "未答" + mPointBean.unum + "道，" +
                    "正确率" + mPointBean.accuracy + "%" + "，" +
                    "用时" + time);
        } else {
            viewHolder.tv_point_specific.setText("共" + mPointBean.qnum + "道，" +
                    "答对" + mPointBean.rnum + "道，" +
                    "正确率" + mPointBean.accuracy + "%，" +
                    "用时" + time);
        }
        if (nextPointBean == null || nextPointBean.level <= mPointBean.level) { // 收缩状态
            if (mPointBean.level == 0) {
                viewHolder.line_below.setVisibility(View.INVISIBLE);
                if (mPointBean.children == null || mPointBean.children.size() == 0) {
                    viewHolder.iv_indicator.setImageResource(R.mipmap.tree_indicator1_none);
                } else {
                    viewHolder.iv_indicator.setImageResource(R.mipmap.tree_indicator1_fold);
                }
            } else if (mPointBean.level == 1) {
                if (mPointBean.children == null || mPointBean.children.size() == 0) {
                    viewHolder.iv_indicator.setImageResource(R.mipmap.tree_indicator2_none);
                } else {
                    viewHolder.iv_indicator.setImageResource(R.mipmap.tree_indicator2_fold);
                }
                if (nextPointBean != null && nextPointBean.level == mPointBean.level) {
                    viewHolder.line_below.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.line_below.setVisibility(View.INVISIBLE);
                }
            } else {
                if (nextPointBean == null || nextPointBean.level == 0) {
                    viewHolder.line_below.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.line_below.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (mPointBean.level == 0) {
                if (nextPointBean.level > mPointBean.level) {
                    viewHolder.iv_indicator.setImageResource(R.mipmap.tree_indicator1_expand);
                    viewHolder.line_below.setVisibility(View.VISIBLE);
                }
            } else if (mPointBean.level == 1) {
                if (nextPointBean.level > mPointBean.level) {
                    viewHolder.iv_indicator.setImageResource(R.mipmap.tree_indicator2_expand);
                    viewHolder.line_below.setVisibility(View.VISIBLE);
                }
            }
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

        int dayNightMode = SpUtils.getDayNightMode();
        if (dayNightMode == 1) {
            viewHolder.tv_point_name.setTextColor(Color.parseColor("#999999"));
            viewHolder.tv_point_specific.setTextColor(Color.parseColor("#999999"));
        } else {
            viewHolder.tv_point_name.setTextColor(mContext.getResources().getColor(R.color.arena_exam_common_text));
            viewHolder.tv_point_specific.setTextColor(mContext.getResources().getColor(R.color.arena_exam_common_text));
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.line_above)
        View line_above;
        @BindView(R.id.item_level_point_list_layout)
        LinearLayout llLine;
        @BindView(R.id.iv_indicator)
        ImageView iv_indicator;
        @BindView(R.id.line_below)
        View line_below;
        @BindView(R.id.tv_point_name)
        TextView tv_point_name;
        @BindView(R.id.tv_point_specific)
        TextView tv_point_specific;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
