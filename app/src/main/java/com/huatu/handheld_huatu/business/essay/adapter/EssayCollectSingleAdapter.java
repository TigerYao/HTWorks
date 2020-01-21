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
import com.huatu.handheld_huatu.business.essay.bean.Province;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;
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
 * 申论单题收藏适配器
 */
public class EssayCollectSingleAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mContext;
    private List<SingleExerciseResult> mData;
    private CustomConfirmDialog dialog;
    private EssayExamImpl mEssayExamImpl;
    private int type;

    public EssayCollectSingleAdapter(Activity context, EssayExamImpl essayExamImpl) {
        this.mContext = context;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_fragment_single_exercise, parent, false);
            holder = new EssayCollectSingleAdapter.ViewHolder(convertView);
        } else {
            holder = (EssayCollectSingleAdapter.ViewHolder) convertView.getTag();
        }
        final SingleExerciseResult mResult = mData.get(position);
        final Province provinceData = mResult.essayQuestionBelongPaperVOList.get(0);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (mResult.showMsg != null) {
            if (mResult.videoAnalyzeFlag) {
                holder.iv_video.setVisibility(View.VISIBLE);
                builder.append("       ").append(mResult.showMsg);
            } else {
                holder.iv_video.setVisibility(View.GONE);
                builder.append(mResult.showMsg);
            }
        }
        if (provinceData.areaName != null) {
            builder.append("（").append(provinceData.areaName).append("）");
        }
        holder.tv_title.setText(builder);

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
                if (!mResult.isOnline) {
                    DialogUtils.onShowConfirmDialog(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mEssayExamImpl != null && !mResult.essayQuestionBelongPaperVOList.isEmpty()) {
                                mEssayExamImpl.deleteCollectEssay(true, mResult.similarId, provinceData.questionBaseId);
                            }
//                            EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_collection_essay_remove_single));
                        }
                    }, "试题已被下线", "是否移除收藏", "取消", "确认");
                } else {
                    if (mResult.essayQuestionBelongPaperVOList.isEmpty()) {
                        ToastUtils.showEssayToast("暂无试题，请稍后重试");
                        return;
                    }

                    Bundle extraBundle = new Bundle();
                    extraBundle.putBoolean("isFromCollection", true);           // 这个是为了材料页，不可切换地区
                    extraBundle.putString("areaName", provinceData.areaName);

                    EssayRoute.navigateToAnswer(mContext, mEssayExamImpl.getCompositeSubscription(), true, mResult, null, extraBundle);
                }
            }
        });

        return convertView;
    }

    public void setDataAndNotify(ArrayList<SingleExerciseResult> mSingleResult, int type) {
        this.type = type;
        mData.clear();
        mData.addAll(mSingleResult);
        notifyDataSetChanged();
    }

    static class ViewHolder {

        @BindView(R.id.iv_video)
        ImageView iv_video;             // 视频标志
        @BindView(R.id.tv_title)
        TextView tv_title;              // title
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
}
