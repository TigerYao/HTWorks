package com.huatu.handheld_huatu.business.essay.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.activity.ManualCheckReportActivity;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;
import com.huatu.handheld_huatu.business.essay.bean.MyCheckResult;
import com.huatu.handheld_huatu.business.essay.bean.Province;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamCheckDetailV2;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamRobotCheckReport;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomSupDialog;
import com.huatu.handheld_huatu.view.swiperecyclerview.swipemenu.SwipeMenuLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht-ldc on 2018/7/10.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private Activity mContext;
    private ArrayList<MyCheckResult> mData = new ArrayList<>();
    private EssayExamImpl mEssayExamImpl;
    private int mType;
    private CustomConfirmDialog dialog;
    private CompositeSubscription compositeSubscription;

    public CheckListAdapter(Activity context, CompositeSubscription compositeSubscription, EssayExamImpl essayExamImpl, int type) {
        mContext = context;
        this.compositeSubscription = compositeSubscription;
        this.mEssayExamImpl = essayExamImpl;
        this.mType = type;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_list_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.swipeMenuLayout.setSwipeEnable(true);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CheckListAdapter.ViewHolder holder, final int position) {
        if (mData != null && mData.size() != 0) {
            final MyCheckResult mResult = mData.get(position);
            SpannableStringBuilder mBuilder = new SpannableStringBuilder("");
            if (mResult.stem != null) {
                if (mResult.videoAnalyzeFlag) {
                    holder.iv_video.setVisibility(View.VISIBLE);
                    mBuilder.append("       " + mResult.stem);
                } else {
                    holder.iv_video.setVisibility(View.GONE);
                    mBuilder.append(mResult.stem);
                }

                if (mResult.areaName != null) {
                    mBuilder.append("（" + mResult.areaName + "）");
                }
            } else if (mResult.paperName != null) {
                if (mResult.videoAnalyzeFlag) {
                    holder.iv_video.setVisibility(View.VISIBLE);
                    mBuilder.append("       " + mResult.paperName);
                } else {
                    holder.iv_video.setVisibility(View.GONE);
                    mBuilder.append(mResult.paperName);
                }

//                mBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,
//                        R.color.gray_333333)), 0, mBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            }
            holder.tv_title.setText(mBuilder);
            if (mResult.correctMode != 1) {
                holder.tv_check_by_person.setVisibility(View.VISIBLE);
            } else {
                holder.tv_check_by_person.setVisibility(View.GONE);
            }
            if (mResult.bizStatus == 2 || mResult.bizStatus == 4) {
                holder.tv_score_text.setText("批改中...");
                holder.tv_score_text.setTextColor(ContextCompat.getColor(mContext, R.color.yellowA6d));
                holder.smContentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                holder.tv_score_text.setTypeface(Typeface.DEFAULT);
                holder.tv_score.setText("");
            } else if (mResult.bizStatus == 5) {
                holder.tv_score_text.setText("被退回");
                holder.tv_score_text.setTextColor(ContextCompat.getColor(mContext, R.color.red250));
                holder.smContentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.redAF9));
                holder.tv_score_text.setTypeface(Typeface.DEFAULT);
                holder.tv_score.setText("");
            } else {
                holder.tv_score.setText(mResult.examScore + "");
                holder.tv_score_text.setText("/" + mResult.score + "分");
                holder.tv_score_text.setTextColor(ContextCompat.getColor(mContext, R.color.black250));
                holder.smContentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                Typeface type = Typeface.createFromAsset(mContext.getAssets(), "font/851-CAI978.ttf");
                holder.tv_score.setTypeface(type);
                holder.tv_score_text.setTypeface(type);
            }


            if (!TextUtils.isEmpty(mResult.correctDate)) {
                holder.tv_time.setVisibility(View.VISIBLE);
                holder.tv_time.setText(mResult.correctDate);
            } else {
                holder.tv_time.setVisibility(View.GONE);
            }

            // 重做
            holder.tv_redo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mType != 1) {
                        // 单题
                        SingleExerciseResult singleExerciseResult = new SingleExerciseResult();

                        singleExerciseResult.showMsg = mResult.stem;
                        singleExerciseResult.similarId = mResult.similarId;
                        singleExerciseResult.questionType = mResult.questionType;
                        singleExerciseResult.essayQuestionBelongPaperVOList = new ArrayList<>();
                        Province province = new Province();
                        singleExerciseResult.essayQuestionBelongPaperVOList.add(province);

                        Bundle extraBundle = new Bundle();
                        extraBundle.putBoolean("isFromCollection", true);       // 这个是为了材料页，不可切换地区

                        if (mResult.areaName != null) {
                            province.questionBaseId = mResult.questionBaseId;
                            extraBundle.putString("areaName", mResult.areaName);
                        }

                        EssayRoute.navigateToAnswer(mContext, compositeSubscription, true, singleExerciseResult, null, extraBundle);

                    } else {
                        // 套题
                        MultiExerciseResult multiExerciseResult = new MultiExerciseResult();
                        multiExerciseResult.paperName = mResult.paperName;
                        multiExerciseResult.paperId = mResult.paperId;

                        Bundle extraBundle = new Bundle();
                        extraBundle.putString("areaName", mResult.areaName);
                        extraBundle.putInt("staticCorrectMode", mResult.paperType == 0 ? 1 : 0);     // 0、模考卷 1、真题卷 模考不能切换，只能智能答题

                        EssayRoute.navigateToAnswer(mContext, compositeSubscription, false, null, multiExerciseResult, extraBundle);

                    }
                    if (holder.swipeMenuLayout.isOpen()) {
                        holder.swipeMenuLayout.smoothCloseMenu();
                    }
                }
            });

            // 查看报告/查看批改详情
            holder.smContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetUtil.isConnected()) {
                        ToastUtils.showShort("网络未连接，请检查您的网络设置");
                        return;
                    }
                    if (holder.swipeMenuLayout.isOpen()) {
                        holder.swipeMenuLayout.smoothCloseMenu();
                    } else if (mResult.bizStatus == 5) {
                        // 被退回了
                        showBeBacked(mResult);
                    } else if (mResult.correctMode != 1 && (mResult.bizStatus == 2 || mResult.bizStatus == 4)) {
                        // 人工批改或智能转人工 批改中
                        if (!TextUtils.isEmpty(mResult.clickContent)) {
                            ToastUtils.showEssayToast(mResult.clickContent);
                        } else {
                            ToastUtils.showEssayToast("正在仔细批改中，请耐心等候");
                        }
                    } else {
                        // 进批改详情或报告
                        if (mType != 1) {
                            // 标准答案和文章写作
                            Bundle mEssayBundle = new Bundle();
                            mEssayBundle.putString("titleView", mResult.stem);
                            mEssayBundle.putBoolean("isSingle", true);
                            if (mType == 2) {
                                mEssayBundle.putBoolean("isFromArgue", true);
                            }
                            mEssayBundle.putLong("questionBaseId", mResult.questionBaseId);
                            mEssayBundle.putLong("paperId", mResult.paperId);
                            mEssayBundle.putLong("questionDetailId", mResult.questionDetailId);
                            mEssayBundle.putLong("similarId", 1);
                            mEssayBundle.putBoolean("isStartToCheckDetail", true);
                            mEssayBundle.putInt("type", 0);
                            mEssayBundle.putString("areaName", mResult.areaName);
                            mEssayBundle.putLong("answerId", mResult.answerId);
                            mEssayBundle.putInt("correctMode", mResult.correctMode);
                            mEssayBundle.putInt("questionType", mResult.questionType);
//                            EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent
//                                    .EssayExam_start_EssayExamCheckDetail));
                            // EssayExamActivity.show(mContext, EssayExamActivity.show_EssayCheckDetail, mEssayBundle);
                            if (mResult.correctMode == 1) {
                                //标准答案和文章写作的智能批改进批改详情
                                EssayExamCheckDetailV2.lanuch(mContext, mEssayBundle);
                            } else {
                                //标准答案和文章写作的人工和智能转人工批改进批改报告
                                if (!mResult.paperReportFlag) {
                                    //没有报告
                                    ToastUtils.showEssayToast("报告正在生成中");
                                } else {
                                    ManualCheckReportActivity.startManualCheckReport(mContext, mEssayBundle);
                                }
                            }
                        } else {
                            // 套题
                            Bundle mEssayBundle = new Bundle();
                            mEssayBundle.putString("titleView", mResult.paperName);
                            mEssayBundle.putBoolean("isSingle", false);
                            mEssayBundle.putBoolean("isFromArgue", false);
                            mEssayBundle.putLong("paperId", mResult.paperId);
                            mEssayBundle.putBoolean("isStartToCheckDetail", true);
                            mEssayBundle.putLong("questionBaseId", mResult.questionBaseId);
                            mEssayBundle.putLong("questionDetailId", mResult.questionDetailId);
                            mEssayBundle.putInt("type", 1);
                            mEssayBundle.putLong("answerId", mResult.answerId);
                            mEssayBundle.putString("areaName", mResult.areaName);
                            mEssayBundle.putBoolean("paperReportFlag", mResult.paperReportFlag);                // 是否有报告

                            mEssayBundle.putInt("correctMode", mResult.correctMode);
                            mEssayBundle.putInt("questionType", mResult.questionType);
//                            EventBusUtil.sendMessage(EssayExamMessageEvent.EssayExam_net_paperCommit);
                            //EssayExamActivity.show(mContext, EssayExamActivity.show_EssayCheckDetail, mEssayBundle);

                            if (mResult.correctMode == 1) {// 智能
                                if (!mResult.paperReportFlag) {
                                    //没有报告
                                    ToastUtils.showEssayToast("报告正在生成中");
                                } else {
                                    // 智能批改报告
                                    EssayExamRobotCheckReport.lanuch(mContext, mEssayBundle);
                                }
                            } else {                            // 人工
                                // 人工批改报告
                                if (!mResult.paperReportFlag) {
                                    //没有报告
                                    ToastUtils.showEssayToast("报告正在生成中");
                                } else {
                                    ManualCheckReportActivity.startManualCheckReport(mContext, mEssayBundle);
                                }
                            }
//                            EssayExamCheckDetailV2.lanuch(mContext, mEssayBundle);
                        }
                    }

                }

            });
            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetUtil.isConnected()) {
                        ToastUtils.showShort("网络未连接，请检查您的网络设置");
                        return;
                    }
//                    Log.i("CheckListAdapter","position-----"+position);
//                    Log.i("CheckListAdapter","getItemCount-----"+getItemCount());
//                    Log.i("CheckListAdapter","holder.getAdapterPosition-----"+holder.getAdapterPosition());
                    if (mResult.bizStatus == 2 || mResult.bizStatus == 4) {
                        ToastUtils.showEssayToast("正在批改中，暂不可删除");
                        return;
                    }
                    //删除
                    DialogUtils.onShowConfirmDialog(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mEssayExamImpl != null) {
                                mEssayExamImpl.deleteCheckEssay(mType, mResult.answerId);
                            }
                            if (getItemCount() > position) {
                                mData.remove(position);
//                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        }

                    }, null, "删除后不能恢复，确认删除吗？", "取消", "确认");
                }
            });

        }
    }


    private void showBeBacked(final MyCheckResult mResult) {
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(mContext);
        builder.setRLayout(R.layout.essay_back_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                TextView tv_tips = mView.findViewById(R.id.tv_tips);
                if (!TextUtils.isEmpty(mResult.correctMemo)) {
                    tv_tips.setText(mResult.correctMemo);
                } else {
                    tv_tips.setText("本次人工批改申请因“卷面不整洁，无法批改”被驳回，申请时消耗的批改次数已退回账户，如需继续申请批改请修改后重新提交。");

                }
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mView.findViewById(R.id.tv_feedback).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        // 意见反馈
                        FeedbackActivity.newInstance(mContext);
                    }
                });
                mView.findViewById(R.id.tv_change_answer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        if (mType != 1) {

                            SingleExerciseResult singleExerciseResult = new SingleExerciseResult();

                            singleExerciseResult.showMsg = mResult.stem;
                            singleExerciseResult.similarId = mResult.similarId;
                            singleExerciseResult.questionType = mResult.questionType;
                            singleExerciseResult.essayQuestionBelongPaperVOList = new ArrayList<>();
                            Province province = new Province();
                            singleExerciseResult.essayQuestionBelongPaperVOList.add(province);

                            Bundle extraBundle = new Bundle();
                            extraBundle.putBoolean("isFromCollection", true);       // 这个是为了材料页，不可切换地区
                            extraBundle.putLong("answerId", mResult.answerId);      // 退回修改答案的时候，用答题卡id请求问题
                            extraBundle.putInt("bizStatus", mResult.bizStatus);     // 退回修改答案的时候，需要用批改状态

                            if (mResult.areaName != null) {
                                province.questionBaseId = mResult.questionBaseId;
                                province.lastType = 2;
                                extraBundle.putString("areaName", mResult.areaName);
                            }

                            EssayRoute.navigateToAnswer(mContext, compositeSubscription, true, singleExerciseResult, null, extraBundle);

                        } else {

                            MultiExerciseResult multiExerciseResult = new MultiExerciseResult();
                            multiExerciseResult.paperName = mResult.paperName;
                            multiExerciseResult.paperId = mResult.paperId;
                            multiExerciseResult.lastType = 2;

                            Bundle extraBundle = new Bundle();

                            extraBundle.putString("areaName", mResult.areaName);
                            extraBundle.putLong("answerId", mResult.answerId);      // 退回修改答案的时候，用答题卡id请求问题
                            extraBundle.putInt("bizStatus", mResult.bizStatus);    // 退回修改答案的时候，需要用批改状态

                            EssayRoute.navigateToAnswer(mContext, compositeSubscription, false, null, multiExerciseResult, extraBundle);

                        }
                    }
                });
            }
        });
        CustomSupDialog dialog = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<MyCheckResult> dataList) {
        mData.clear();
        mData.addAll(dataList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.smContentView)
        View smContentView;
        @BindView(R.id.smMenuView)
        View smMenuView;
        @BindView(R.id.iv_video)
        ImageView iv_video;
        @BindView(R.id.tv_score)
        TextView tv_score;
        @BindView(R.id.tv_score_text)
        TextView tv_score_text;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_check_by_person)
        TextView tv_check_by_person;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_redo)
        TextView tv_redo;
        @BindView(R.id.tv_delete)
        TextView tv_delete;
        SwipeMenuLayout swipeMenuLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeMenuLayout = (SwipeMenuLayout) itemView;
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
