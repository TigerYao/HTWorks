package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht on 2017/11/24.
 */
public class MultiExerciseAdapter extends BaseAdapter {

    private LayoutInflater mInflate;
    private Context mContext;
    private List<MultiExerciseResult> mData;

    public MultiExerciseAdapter(Context context) {
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflate.inflate(R.layout.item_fragment_mult_exercise, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MultiExerciseResult mResult = mData.get(position);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (mResult.paperName != null) {
            if (mResult.videoAnalyzeFlag) {
                holder.iv_video.setVisibility(View.VISIBLE);
                builder.append("       ").append(mResult.paperName);
            } else {
                holder.iv_video.setVisibility(View.GONE);
                builder.append(mResult.paperName);
            }
        }
        if (mResult.examDate != null) {
            builder.append("（").append(mResult.examDate).append("）");
        }
        holder.tv_title.setText(builder);

        holder.tv_total_time.setText(mResult.limitTime / 60 + "");

        EssayAdapterUIUtils.setItemData(
                mResult.correctSum, mResult.manualSum, holder.tvInfo,
                mResult.correctNum, holder.tvAi,
                mResult.manualNum, holder.tvPersonal);

        return convertView;
    }

    public void setData(List<MultiExerciseResult> dataList) {
        mData.clear();
        mData.addAll(dataList);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.iv_video)
        ImageView iv_video;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_info)
        TextView tvInfo;
        @BindView(R.id.tv_total_time)
        TextView tv_total_time;
        @BindView(R.id.tv_personal)
        TextView tvPersonal;            // 人工批改
        @BindView(R.id.tv_ai)
        TextView tvAi;                  // 智能批改

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
