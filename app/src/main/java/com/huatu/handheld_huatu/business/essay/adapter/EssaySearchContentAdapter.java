package com.huatu.handheld_huatu.business.essay.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchContent;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.essay.EssaySearchAnswerCardStateInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chq on 2018/12/15.
 */

public class EssaySearchContentAdapter extends RecyclerView.Adapter<EssaySearchContentAdapter.ViewHold> {

    private Context mContext;
    private int type;
    private List<EssaySearchContent> mData = new ArrayList<>();
    private String mSingleTitle;
    private CustomConfirmDialog dialog;
    private String mKeyWord;
    private int mCurrentPage;
    private CompositeSubscription mCompositeSubscription;

    private CustomLoadingDialog loadingDialog;

    public EssaySearchContentAdapter(Context mContext, CompositeSubscription mCompositeSubscription) {
        this.mContext = mContext;
        this.mCompositeSubscription = mCompositeSubscription;
        loadingDialog = new CustomLoadingDialog((Activity) mContext);
    }

    public EssaySearchContentAdapter(Context context, CompositeSubscription mCompositeSubscription, List<EssaySearchContent> mCourseList, int type) {
        this.mContext = context;
        this.mCompositeSubscription = mCompositeSubscription;
        this.mData = mCourseList;
        this.type = type;
        loadingDialog = new CustomLoadingDialog((Activity) mContext);
    }

    @NonNull
    @Override
    public EssaySearchContentAdapter.ViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_esssay_search_content, parent, false);
        ViewHold hold = new ViewHold(view);
        return hold;
    }

    @Override
    public void onBindViewHolder(@NonNull EssaySearchContentAdapter.ViewHold holder, final int position) {
        final EssaySearchContent mResult = mData.get(position);
        if (type != 1) {
            holder.iv_line.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mResult.showMsg)) {
            holder.tv_title.setText(Html.fromHtml(mResult.showMsg));
        } else if (!TextUtils.isEmpty(mResult.paperName)) {
            holder.tv_title.setText(Html.fromHtml(mResult.paperName));
        } else {
            holder.tv_title.setText("");
        }
        if (mResult.questionList != null && mResult.questionList.size() != 0) {
            if (!TextUtils.isEmpty(mResult.questionList.get(0).stem)) {
                holder.tv_question.setVisibility(View.VISIBLE);
                holder.tv_question.setText(Html.fromHtml("问题" + mResult.questionList.get(0).sort + "：" + mResult.questionList.get(0).stem));
            } else {
                holder.tv_question.setText("");
                holder.tv_question.setVisibility(View.GONE);
            }
        } else if (mResult.stemList != null && mResult.stemList.size() != 0) {
            if (!TextUtils.isEmpty(mResult.stemList.get(0).content)) {
                holder.tv_question.setVisibility(View.VISIBLE);
                holder.tv_question.setText(Html.fromHtml("问题" + mResult.stemList.get(0).sort + "：" + mResult.stemList.get(0).content));
            } else {
                holder.tv_question.setText("");
                holder.tv_question.setVisibility(View.GONE);
            }
        } else {
            holder.tv_question.setText("");
            holder.tv_question.setVisibility(View.GONE);
        }
        if (mResult.materialList != null && mResult.materialList.size() != 0) {
            holder.iv_line.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mResult.materialList.get(0).content)) {
                holder.tv_material.setVisibility(View.VISIBLE);
                holder.iv_line.setVisibility(View.VISIBLE);
                holder.tv_material.setText(Html.fromHtml("资料" + mResult.materialList.get(0).sort + "：" + mResult.materialList.get(0).content));
            } else {
                holder.tv_material.setText("");
                holder.iv_line.setVisibility(View.GONE);
                holder.tv_material.setVisibility(View.GONE);
            }
        } else {
            holder.iv_line.setVisibility(View.GONE);
            holder.tv_material.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }

                if (type == 1) {
                    // 跳转套题
                    if (mResult.paperName != null) {
                        String mTitle = mResult.paperName.replaceAll("<font color='#e9304e'>", "");
                        mSingleTitle = mTitle.replaceAll("</font>", "");
                    }
                    StudyCourseStatistic.clickTiKuSearchResult(mResult.paperId + "", mKeyWord, getCurrentPage(), position + "");

                    if (!NetUtil.isConnected()) {
                        ToastUtils.showEssayToast("无网络连接！");
                        return;
                    }
                    if (loadingDialog != null)
                        loadingDialog.show();
                    // 请求网络，获取答题卡状态 & 批改次数
                    ServiceProvider.getAnswerCardState(mCompositeSubscription, 0, mResult.paperId, new NetResponse() {
                        @Override
                        public void onError(Throwable e) {
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                            if (e instanceof ApiException) {
                                ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                            } else {
                                ToastUtils.showEssayToast("获取数据失败，请重试");
                            }
                        }

                        @Override
                        public void onSuccess(BaseResponseModel model) {
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                            if (model.data != null) {
                                final EssaySearchAnswerCardStateInfo answerCardState = (EssaySearchAnswerCardStateInfo) model.data;
                                EssayRoute.checkCanCorrect(mContext,
                                        0,
                                        answerCardState,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Bundle extraBundle = new Bundle();
                                                extraBundle.putString("areaName", mResult.areaName);
                                                if (!mResult.stemList.isEmpty()) {
                                                    extraBundle.putLong("showQuestionId", mResult.stemList.get(0).baseId);
                                                }
                                                if (!mResult.materialList.isEmpty()) {
                                                    extraBundle.putLong("showMaterialId", mResult.materialList.get(0).id);
                                                }
                                                extraBundle.putInt("staticCorrectMode", mResult.paperType == 0 ? 1 : 0);
                                                EssayRoute.goEssayAnswer(
                                                        mContext,
                                                        false,
                                                        mSingleTitle,
                                                        0,
                                                        0,
                                                        mResult.paperId,
                                                        0,
                                                        answerCardState.lastType,
                                                        0,
                                                        extraBundle
                                                );
                                            }
                                        },
                                        0,
                                        ""
                                );
                            } else {
                                ToastUtils.showEssayToast("获取数据失败，请重试");
                            }
                        }
                    });
                } else {
                    // 跳转单题
                    if (mResult.questionList.isEmpty() && mResult.areaList.isEmpty()) {
                        ToastUtils.showMessage("暂无试题");
                        return;
                    }
                    StudyCourseStatistic.clickTiKuSearchResult(mResult.groupId + "", mKeyWord, getCurrentPage(), position + "");
                    if (mResult.showMsg != null) {
                        String mTitle = mResult.showMsg.replaceAll("<font color='#e9304e'>", "");
                        mSingleTitle = mTitle.replaceAll("</font>", "");
                    }

                    if (!NetUtil.isConnected()) {
                        ToastUtils.showEssayToast("无网络连接！");
                        return;
                    }

                    final Bundle extraBundle = new Bundle();
                    long questionBaseId = 0;
                    if (!mResult.questionList.isEmpty()) {
                        questionBaseId = mResult.questionList.get(0).baseId;
                        extraBundle.putString("areaName", mResult.questionList.get(0).areaName);
                    } else if (!mResult.areaList.isEmpty()) {
                        questionBaseId = mResult.areaList.get(0).questionBaseId;
                        extraBundle.putString("areaName", mResult.areaList.get(0).areaName);
                    }

                    if (!mResult.materialList.isEmpty()) {
                        extraBundle.putLong("showMaterialId", mResult.materialList.get(0).id);
                    }

                    if (loadingDialog != null)
                        loadingDialog.show();
                    // 请求网络，获取答题卡状态 & 批改次数
                    final long finalQuestionBaseId = questionBaseId;
                    ServiceProvider.getAnswerCardState(mCompositeSubscription, mResult.type, questionBaseId, new NetResponse() {
                        @Override
                        public void onError(Throwable e) {
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                            if (e instanceof ApiException) {
                                ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                            } else {
                                ToastUtils.showEssayToast("获取数据失败，请重试");
                            }
                        }

                        @Override
                        public void onSuccess(BaseResponseModel model) {
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                            if (model.data != null) {
                                final EssaySearchAnswerCardStateInfo answerCardState = (EssaySearchAnswerCardStateInfo) model.data;
                                // 单题
                                EssayRoute.checkCanCorrect(mContext,
                                        0,
                                        answerCardState,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                EssayRoute.goEssayAnswer(
                                                        mContext,
                                                        true,
                                                        mSingleTitle,
                                                        finalQuestionBaseId,
                                                        mResult.groupId,
                                                        0,
                                                        0,
                                                        answerCardState.lastType,
                                                        mResult.type,
                                                        extraBundle
                                                );
                                            }
                                        },
                                        0,
                                        ""
                                );
                            } else {
                                ToastUtils.showEssayToast("获取数据失败，请重试");
                            }
                        }
                    });
                }

            }
        });
    }

    public void setData(List<EssaySearchContent> mData, int type, String mKeyWord) {
        this.mKeyWord = mKeyWord;
        this.type = type;
        this.mData.clear();
        this.mData = mData;
        notifyDataSetChanged();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHold extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.iv_line)
        View iv_line;
        @BindView(R.id.tv_question)
        TextView tv_question;
        @BindView(R.id.tv_material)
        TextView tv_material;

        public ViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
