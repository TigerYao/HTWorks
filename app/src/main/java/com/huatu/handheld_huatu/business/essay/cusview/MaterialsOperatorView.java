package com.huatu.handheld_huatu.business.essay.cusview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamMaterials;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 材料页底部 收藏、改变字体 的栏目
 */

public class MaterialsOperatorView extends LinearLayout {

    Context mContext;
    @BindView(R.id.ll_collection)
    LinearLayout llCollection;                      // 收藏布局
    @BindView(R.id.essay_collect_iv)
    ImageView ivCollection;                         // 收藏五角星
    @BindView(R.id.tv_collect)
    TextView tvCollection;                          // 收藏文字
    @BindView(R.id.view_collect_divider)
    View leftDivider;                               // 左边的分割线
    @BindView(R.id.tv_see_answer)
    TextView tvSeeAnswer;                           // 查看答案
    @BindView(R.id.view_see_answer_divider)
    View rightDivider;                              // 查看答案右边的分割线
    @BindView(R.id.ll_download)
    LinearLayout llDownload;                        // 下载布局
    @BindView(R.id.essay_txt_sp_iv)
    ImageView ivDownload;                           // 下载图标
    @BindView(R.id.tv_down)
    TextView tvDownload;                            // 下载文字
    @BindView(R.id.bottom_operator_llayout)
    LinearLayout bottomOperatorLlayout;

    private static final String TAG = "MaterialsOperatorView";
    private int resId = R.layout.essay_exam_materials_operator_layout;
    private View rootView;
    public boolean isCollect;                                                                       // 是否收藏
    private boolean isSingle;                                                                       // 是否是单提
    private long similarId, questionBaseId, paperId;                                                // 各种Id
    public EssayExamImpl mEssayExamImpl;                                                            // 收藏网络操作
    public int requestType;                                                            // 收藏网络操作
    private OnCusViewCallBack mOnCusViewCallBack;                                                   // 收藏回调
    private long baseId = 0;                                                                        // 根据不同种类题，要收藏的Id
    private EssayExamMaterials parentFragment;

    public MaterialsOperatorView(Context context) {
        super(context);
        initView(context);
    }

    public MaterialsOperatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MaterialsOperatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * Impl收藏EventBus发送的事件回调
     */
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null || mEssayExamImpl == null || mContext == null) {
            LogUtils.e(TAG, "event == null || mEssayExamImpl==null " + mEssayExamImpl);
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
        if (event.type == EssayExamMessageEvent.EssayExam_net_setCollectEssay_success) {
            isCollect = true;
            refreshBottomView(true);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_setCollectEssay_fail) {
            isCollect = false;
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_deleteCollectEssay_success) {
            isCollect = false;
            refreshBottomView(false);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_deleteCollectEssay_fail) {
            isCollect = true;
            refreshBottomView(true);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_checkCollectEssay_success) {
            if (mEssayExamImpl != null) {
                if (mEssayExamImpl.isCollect != null) {
                    isCollect = mEssayExamImpl.isCollect.flag;
                }
            }
            refreshBottomView(isCollect);
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_operator_oc) {   // 动画显示不显示此栏
            startAnim(!isOpen);
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_mMaterials_scroll_up) {
            startAnim(false);
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_mMaterials_scroll_down) {
            startAnim(true);
        }
        return true;
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this, rootView);
    }

    /**
     * 本题目的所有信息传递过来，用于收藏操作
     */
    public void setData(boolean isCollect, boolean isSingle, long similarId, long questionBaseId, long paperId, EssayExamImpl mEssayExamImpl, int requestType, OnCusViewCallBack varCb) {
        this.isCollect = isCollect;

        this.isSingle = isSingle;
        this.similarId = similarId;
        this.questionBaseId = questionBaseId;

        this.paperId = paperId;
        this.mEssayExamImpl = mEssayExamImpl;

        this.requestType = requestType;

        this.mOnCusViewCallBack = varCb;

        if (isSingle) {
            baseId = questionBaseId;
        } else {
            baseId = paperId;
        }

        if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {                   // 如果是模考大赛，隐藏查看答案按钮
            tvSeeAnswer.setVisibility(GONE);
            rightDivider.setVisibility(GONE);
        } else if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {      // 课后作业，隐藏收藏、查看答案按钮
            llCollection.setVisibility(GONE);
            leftDivider.setVisibility(GONE);
            tvSeeAnswer.setVisibility(GONE);
            rightDivider.setVisibility(GONE);
        }

        if (requestType != EssayExamActivity.ESSAY_EXAM_HOMEWORK) {
            onLoadData();
        }
    }

    public void setParentFragment(EssayExamMaterials parentFragment) {
        this.parentFragment = parentFragment;
    }

    /**
     * 网络获取是否收藏过本题
     */
    private void onLoadData() {
        TimeUtils.delayTask(new Runnable() {
            @Override
            public void run() {
                if (mEssayExamImpl != null) {
                    mEssayExamImpl.checkCollectEssay(isSingle, similarId, baseId);
                }
            }
        }, 500);
    }

    @OnClick({R.id.ll_collection, R.id.ll_download, R.id.tv_see_answer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_collection:         // 点击收藏
                if (!isCollect) {
                    if (mEssayExamImpl != null) {
                        mEssayExamImpl.setCollectEssay(isSingle, similarId, baseId);
                    }
                } else {
                    if (mEssayExamImpl != null) {
                        mEssayExamImpl.deleteCollectEssay(isSingle, similarId, baseId);
                    }
                }
                break;
            case R.id.ll_download:      // 点击下载资料
                parentFragment.downLoadPdf();
                break;
            case R.id.tv_see_answer:        // 查看答案
                if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {
                    ToastUtils.showMessage("模考大赛无法查看答案");
                    return;
                }
//                DialogUtils.onShowConfirmDialog((Activity) mContext, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
                EssayExamMessageEvent var = new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_start_EssayExamAnswerDetail);
                var.extraBundle = new Bundle();
                var.extraBundle.putInt("curPos", parentFragment.getCurrentQuestion());
                EventBus.getDefault().post(var);

                // 查看答案按钮，清除材料页，问题页内容的选中等状态
                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));
                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
//                    }
//
//                }, null, "确认放弃批改只查看答案？", "继续答题", "查看答案");
                break;
        }
    }

    private void setBg(ImageView iv, int id) {
        if (iv != null) {
            iv.setImageResource(id);
        }
    }

    /**
     * @param flag true、已下载 false、未下载
     */
    public void showDownloadButtonImage(boolean flag) {
        if (flag) {
            ivDownload.setImageResource(R.mipmap.download_essay);
            tvDownload.setText("预览");
        } else {
            ivDownload.setImageResource(R.mipmap.download_paper_icon);
            tvDownload.setText("下载");
        }
    }

    /**
     * 刷新收藏状态UI
     */
    public void refreshBottomView(boolean isCollect) {
        this.isCollect = isCollect;
        if (ivCollection != null) {
            if (isCollect) {
                tvCollection.setText("已收藏");
                setBg(ivCollection, R.mipmap.essay_collect);
            } else {
                tvCollection.setText("收藏");
                setBg(ivCollection, R.mipmap.essay_collect_n);
            }
        }
        if (mOnCusViewCallBack != null) {
            mOnCusViewCallBack.isCollect(isCollect);
        }
    }

    boolean isRuning;
    boolean isOpen = true;
    long lockTime;
    ValueAnimator vValueAnimator;
//    LinearLayout bottom_view;

    /**
     * 隐藏显示相关动画操作
     */
    public void startAnim(boolean open) {
//        if (isOpen == open) {
//            return;
//        }
//
//        if (System.currentTimeMillis() - lockTime < 300) {
//            return;
//        }
//        if (!isRuning) {
//            int v1 = 0;
//            int v2 = 500;
//            lockTime = System.currentTimeMillis();
//            if (open) {
//                isOpen = true;
//                v1 = 0;
//                v2 = DisplayUtil.dp2px(50);
//            } else {
//                isOpen = false;
//                v1 = DisplayUtil.dp2px(50);
//                v2 = 0;
//            }
//            vValueAnimator = ValueAnimator.ofInt(v1, v2);
//            vValueAnimator.setDuration(300).start();
//            vValueAnimator.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    isRuning = true;
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    isRuning = false;
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
//            vValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    if (bottomOperatorLlayout != null) {
//                        int v = (int) animation.getAnimatedValue();
//                        LogUtils.d(TAG, "v:" + v);
//                        ViewGroup.LayoutParams layoutParams = bottomOperatorLlayout.getLayoutParams();
//                        if (layoutParams != null) {
//                            layoutParams.height = (int) v;
//                            bottomOperatorLlayout.setLayoutParams(layoutParams);
//                        }
//                    }
//                }
//            });
//        }
    }

    public interface OnCusViewCallBack {
        boolean isCollect(boolean isCollect);
    }
}
