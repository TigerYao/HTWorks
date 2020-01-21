package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht on 2017/11/23.
 */
public class SingleExerciseAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<SingleExerciseResult> mData;

    public SingleExerciseAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.item_fragment_single_exercise, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SingleExerciseResult mResult = mData.get(position);

        if (mResult.showMsg != null) {
            if (mResult.videoAnalyzeFlag) {
                holder.ivVideo.setVisibility(View.VISIBLE);
                holder.tvTitle.setText("       " + mResult.showMsg);
            } else {
                holder.ivVideo.setVisibility(View.GONE);
                holder.tvTitle.setText(mResult.showMsg);
            }
        }

        EssayAdapterUIUtils.setItemData(
                mResult.correctSum, mResult.manualSum, holder.tvInfo,
                mResult.correctNum, holder.tvAi,
                mResult.manualNum, holder.tvPersonal);

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.iv_video)
        ImageView ivVideo;             // 视频标志
        @BindView(R.id.tv_title)
        TextView tvTitle;              // title
        @BindView(R.id.tv_info)
        TextView tvInfo;               // 全站批改情况
        @BindView(R.id.tv_personal)
        TextView tvPersonal;            // 人工批改
        @BindView(R.id.tv_ai)
        TextView tvAi;                  // 智能批改

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }

    public void setData(ArrayList<SingleExerciseResult> mList) {
        mData.clear();
        mData.addAll(mList);
        notifyDataSetChanged();
    }
}
