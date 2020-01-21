package com.huatu.handheld_huatu.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.checkfragment.CheckOrderFragment;
import com.huatu.handheld_huatu.business.essay.cusview.CorrectDialog;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.mvpmodel.essay.TeacherBusyStateInfo;
import com.huatu.handheld_huatu.mvpmodel.me.CoverManulResBean;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import rx.subscriptions.CompositeSubscription;

/**
 * 智能转人工view
 * 直接布局在智能报告，智能报告详情页
 * 需要调用方法 setData 来设置数据
 */
public class CovertManulView extends FrameLayout {
    private int questionType;
    private long paperId;
    private long answerCardId;
    private boolean isSingle;
    private int convertCount;

    public CovertManulView(Context context) {
        this(context, null);
    }

    public CovertManulView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CovertManulView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context ctx) {
        View.inflate(ctx, R.layout.layout_covert_manuel, this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeManualCheck();
            }
        });
    }

    public void setData(int questionType, long id, long answerCardId, boolean isSingle, int convertCount) {
        this.questionType = questionType;
        this.paperId = id;
        this.answerCardId = answerCardId;
        this.isSingle = isSingle;
        this.convertCount = convertCount;
    }

    CustomConfirmDialog changeToManualCheckDialog;

    private void changeManualCheck() {
        String message = convertCount > 0 ? "本题已转为人工批改" +
                + convertCount + "次, 继续提交将消耗1次人工批改次数，确认提交？": "申论批改可采用智能批改或者人工批改，人工批改为名师一对一批改，确认选择后将使用本次作答答案进行人工批改";
        String posBtn = convertCount > 0 ?"确认" :"确认选择";
        if (changeToManualCheckDialog == null) {
            changeToManualCheckDialog = DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkTeachCanWork();
                }
            }, null, null, message, "取消", posBtn);
        } else if (!changeToManualCheckDialog.isShowing()) {
            changeToManualCheckDialog.setMessage(message);
            changeToManualCheckDialog.setPositiveButton(posBtn, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkTeachCanWork();
                }
            });
            changeToManualCheckDialog.show();
        }
    }

    private CompositeSubscription getSubscription() {
        CompositeSubscription compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(null);
        return compositeSubscription;
    }

    private Activity getActivity() {
        Activity activity = (Activity) getContext();
        return activity;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus)
            AnimUtils.startTopDownAnim(this, 10f, 10f, 3800);
        else
            this.clearAnimation();
    }

    private void checkTeachCanWork() {
        ServiceProvider.getTeacherBusyState(getSubscription(), answerCardId, 2, questionType, paperId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showEssayToast("转人工失败，请重试");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.data != null) {
                    TeacherBusyStateInfo teacherState = (TeacherBusyStateInfo) model.data;
                    teacherState.manual = teacherState.correct;     // 人工批改次数
                    if (teacherState.canCorrect) {  // 能批改 -> 老师工作不饱和，去检查批改次数
                        if (EssayRoute.checkCount(2, teacherState)) {       // 检查批改次数，足够，直接交卷
                            covert(0);
                        } else {                                                        // 批改次数不足，购买弹窗
                            showBuyCheck();
                        }
                    } else {                        // 不能批改 -> 老师工作饱和，弹窗
                        showTeacherBusyDialog(teacherState);
                    }
                } else {
                    ToastUtils.showEssayToast("转人工失败，请重试");
                }
            }
        });
    }

    private void showBuyCheck() {
        final CustomConfirmDialog mDialog;
        mDialog = DialogUtils.createDialog(getActivity(), null, "批改次数不足");
        mDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setPositiveButton("去购买", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 去购买页面
                mDialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("page_source", "转人工");
                BaseFrgContainerActivity.newInstance(getActivity(),
                        CheckOrderFragment.class.getName(),
                        bundle);
            }
        });
        mDialog.show();
    }

    /**
     * 老师工作饱和弹窗
     */
    private void showTeacherBusyDialog(final TeacherBusyStateInfo teacherState) {
        final CorrectDialog teacherBusyDialog = new CorrectDialog(getActivity(), R.layout.essay_rabbit_dialog);
        View contentView = teacherBusyDialog.mContentView;

        ImageView ivRabbit = contentView.findViewById(R.id.iv_rabbit);
        TextView tvTips = contentView.findViewById(R.id.tv_tips);
        TextView tvLeft = contentView.findViewById(R.id.tv_left);
        TextView tvRight = contentView.findViewById(R.id.tv_right);

        ivRabbit.setImageResource(R.mipmap.rabbit_sad);
        tvTips.setText(teacherState.correctDesc);
        tvLeft.setText("我再想想");
        tvRight.setText("确认批改");

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherBusyDialog.dismiss();
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherBusyDialog.dismiss();
                // 人工批改才检查老师是否饱和
                if (EssayRoute.checkCount(2, teacherState)) {       // 检查批改次数，足够
                    covert(1);
                } else {                                                                    // 批改次数不足，弹窗
                    showBuyCheck();
                }
            }
        });
        teacherBusyDialog.show();
    }

    private void covert(int delayStatus) {
        ServiceProvider.postConvertManualCheck(getSubscription(), answerCardId, isSingle ? 0 : 1, delayStatus, new NetResponse() {
            @Override
            public void onSuccess(final BaseResponseModel model) {
                super.onSuccess(model);
                UniApplicationLike.getApplicationHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        CoverManulResBean resBean = (CoverManulResBean) model.data;
                        String message = resBean != null && !Utils.isEmptyOrNull(resBean.msg) ? resBean.msg : model.message;
                        changeToManualCheckDialog.dismiss();
                        ToastUtils.showMessage(message);
                        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CONVERT_CORRECT_MODE));
                        convertCount += 1;
                    }
                });

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e == null || !(e instanceof ApiException)){
                    return;
                }
                final ApiException exception = (ApiException) e;
                UniApplicationLike.getApplicationHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CONVERT_CORRECT_MODE));
                        ToastUtils.showMessage(exception.getErrorMsg());
                    }
                });
            }
        });
    }
}

