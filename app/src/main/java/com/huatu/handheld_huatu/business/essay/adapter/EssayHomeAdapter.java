package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.essay.bean.EssayHomeType;
import com.huatu.handheld_huatu.business.essay.mainfragment.ArgumentEssay;
import com.huatu.handheld_huatu.business.essay.mainfragment.CheckCorrectEssay;
import com.huatu.handheld_huatu.business.essay.mainfragment.EssayDownLoadFragment;
import com.huatu.handheld_huatu.business.essay.mainfragment.MultExamEssay;
import com.huatu.handheld_huatu.business.essay.mainfragment.fragment_more.EssayCollectionFolderFragment;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.widget.MessageImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht-ldc on 2018/6/28.
 */

public class EssayHomeAdapter extends RecyclerView.Adapter<EssayHomeAdapter.ViewHolder> {

    private List<EssayHomeType> mEssayHomeType;
    private Context mContext;
    private CompositeSubscription compositeSubscription;

    public EssayHomeAdapter(List<EssayHomeType> essayHomeType, Context context, CompositeSubscription compositeSubscription) {
        mEssayHomeType = essayHomeType;
        mContext = context;
        this.compositeSubscription = compositeSubscription;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.iv_icon)
        MessageImageView iv_icon;
        View typeView;

        public ViewHolder(View itemView) {
            super(itemView);
            typeView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_essay_home, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(EssayHomeAdapter.ViewHolder holder, int position) {
        if (mEssayHomeType != null && mEssayHomeType.size() != 0) {
            final EssayHomeType mData = mEssayHomeType.get(position);
            if (mData.icon != 0) {
                holder.iv_icon.setImageResource(mData.icon);
            }

            holder.iv_icon.setMessageNum(mData.tipNum);

            if (mData.name != null) {
                holder.tv_name.setText(mData.name);
            }

            holder.typeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (mData.targetId) {
                        case 0:
                            StudyCourseStatistic.clickStatistic("题库", "申论", "套题");
                            BaseFrgContainerActivity.newInstance(mContext, MultExamEssay.class.getName(), null);
                            break;
                        case 1:
                            StudyCourseStatistic.clickStatistic("题库", "申论", "文章写作");
                            BaseFrgContainerActivity.newInstance(mContext, ArgumentEssay.class.getName(), null);
                            break;
                        case 2:
                            StudyCourseStatistic.clickStatistic("题库", "申论", "模考大赛");
                            if (!CommonUtils.checkLogin(mContext)) {
                                return;
                            }
                            MatchCacheData.getInstance().matchPageFrom = "点击模考大赛";
                            Intent intent = new Intent(mContext, SimulationContestActivityNew.class);
                            intent.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
                            mContext.startActivity(intent);
                            break;
                        case 3:
                            StudyCourseStatistic.clickStatistic("题库", "申论", "批改记录");
                            if (!CommonUtils.checkLogin(mContext)) {
                                return;
                            }
                            BaseFrgContainerActivity.newInstance(mContext, CheckCorrectEssay.class.getName(), null);
                            break;
                        case 4:
                            StudyCourseStatistic.clickStatistic("题库", "申论", "收藏");
                            if (!CommonUtils.checkLogin(mContext)) {
                                return;
                            }
                            BaseFrgContainerActivity.newInstance(mContext, EssayCollectionFolderFragment.class.getName(), null);
                            break;
                        case 5:
                            StudyCourseStatistic.clickStatistic("题库", "申论", "下载");
                            if (!CommonUtils.checkLogin(mContext)) {
                                return;
                            }
                            BaseFrgContainerActivity.newInstance(mContext, EssayDownLoadFragment.class.getName(), null);
                            break;

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mEssayHomeType.size();
    }
}
