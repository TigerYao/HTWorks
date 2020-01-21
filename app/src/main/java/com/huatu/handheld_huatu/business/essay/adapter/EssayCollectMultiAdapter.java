package com.huatu.handheld_huatu.business.essay.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 套题收藏列表适配器
 */
public class EssayCollectMultiAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mContext;
    private List<MultiExerciseResult> mData;
    private CustomConfirmDialog dialog;
    private EssayExamImpl mEssayExamImpl;

    public EssayCollectMultiAdapter(Activity mContext, EssayExamImpl essayExamImpl) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mData = new ArrayList<>();
        this.mEssayExamImpl = essayExamImpl;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_fragment_mult_exercise, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MultiExerciseResult mResult = mData.get(position);
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        if (mResult.paperName != null) {
            if (mResult.videoAnalyzeFlag) {
                holder.iv_video.setVisibility(View.VISIBLE);
                builder.append("       " + mResult.paperName);
            } else {
                holder.iv_video.setVisibility(View.GONE);
                builder.append(mResult.paperName);
            }
        }
        if (mResult.examDate != null) {
            builder.append("（" + mResult.examDate + "）");
        }
        holder.tv_title.setText(builder);

        holder.tv_total_time.setText(mResult.limitTime / 60 + "");

        EssayAdapterUIUtils.setItemData(
                mResult.correctSum, mResult.manualSum, holder.tvInfo,
                mResult.correctNum, holder.tvAi,
                mResult.manualNum, holder.tvPersonal);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (position - 1 >= mData.size()) {
                    return;
                }
                if (!mResult.isAvailable) {
                    //模考中，不可从此进入答题
                    ToastUtils.showEssayToast("模考结束后才可查看哦~");
                } else {
                    if (!mResult.isOnline) {
                        DialogUtils.onShowConfirmDialog(mContext, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mEssayExamImpl != null) {
                                    mEssayExamImpl.deleteCollectEssay(false, 0, mResult.paperId);
                                }
                            }
                        }, "试题已被下线", "是否移除收藏", "取消", "确认");
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("staticCorrectMode", mResult.type == 0 ? 1 : 0);       // 0、模考卷 1、真题卷 模考不能切换，只能智能答题

                        EssayRoute.navigateToAnswer(mContext, mEssayExamImpl.getCompositeSubscription(), false, null, mResult, bundle);
                    }
                }
            }
        });
        return convertView;
    }

    public void setDataAndNotify(ArrayList<MultiExerciseResult> mMultiResult) {
        mData.clear();
        mData.addAll(mMultiResult);
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
