package com.huatu.test.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.test.CustomConfirmDialog;
import com.huatu.test.R;

/*import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.cusview.CorrectDialog;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.CustomShowRuleDialog;
import com.huatu.handheld_huatu.view.CustomSupDialog;
import com.huatu.handheld_huatu.view.EvaluateReportTipsDialog;
import com.huatu.handheld_huatu.view.LevelTrpsDialog;
import com.huatu.utils.StringUtils;*/

/**
 * Created by saiyuan on 2016/10/25.
 */
public class DialogUtils {
  /*  public static CustomConfirmDialog createExitConfirmDialog(final Activity mActivity, String title,
                                                              String content, String cancelText, String okText) {
        final CustomConfirmDialog dialog = createDialog(mActivity, title, content);
        dialog.setPositiveButton(okText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        dialog.setNegativeButton(cancelText, null);
        dialog.setCancelBtnVisibility(true);
        return dialog;
    }

    public static CustomConfirmDialog createExitConfirmDialog(final Activity mActivity, String title,
                                                              String content, String cancelText, String okText, View.OnClickListener onClickListener) {
        final CustomConfirmDialog dialog = createDialog(mActivity, title, content);
        dialog.setPositiveButton(okText, onClickListener);
        dialog.setNegativeButton(cancelText, null);
        dialog.setCancelBtnVisibility(true);
        dialog.setTitleBold();
        dialog.setTitleColor(Color.BLACK);
        dialog.setMessage(content, 14, Color.parseColor("#ff4a4a4a"));
        dialog.setMessageBold();
        return dialog;
    }

    public static CustomConfirmDialog createUpdateDialog(final Activity mActivity, String title, String content) {
        final CustomConfirmDialog dialog = createDialog(mActivity, title, content);
        return dialog;
    }


*/

    public static CustomConfirmDialog createDialog(final Activity mActivity, String title, String content) {
        CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(mActivity);
        builder.setTitle(title).setMessage(content).setCanceledOnTouchOutside(true);
        final CustomConfirmDialog dialog = builder.create();
        dialog.setBtnDividerVisibility(true);
        dialog.setCancelable(true);
        return dialog;
    }

    public static CustomConfirmDialog createDialog(final Activity mActivity, int layoutId, String title, String content) {
        CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(mActivity);
        builder.setLayoutId(layoutId);
        builder.setTitle(title).setMessage(content).setCanceledOnTouchOutside(true);
        final CustomConfirmDialog dialog = builder.create();
        dialog.setBtnDividerVisibility(true);
        dialog.setCancelable(true);
        return dialog;
    }
/*

    public static CustomConfirmDialog createEssayDialog(final Activity mActivity, String title, String content, int size, int color) {
        CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(mActivity);
        builder.setTitle(title).setMessage(content, size, color).setCanceledOnTouchOutside(true);
        final CustomConfirmDialog dialog = builder.create();
        dialog.setBtnDividerVisibility(true);
        dialog.setCancelable(true);
        return dialog;
    }

    public static CustomConfirmDialog createEssayDialog(final Context mActivity, String title, String content, int size, int color) {
        CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(mActivity);
        builder.setTitle(title).setMessage(content, size, color).setCanceledOnTouchOutside(true);
        final CustomConfirmDialog dialog = builder.create();
        dialog.setBtnDividerVisibility(true);
        dialog.setCancelable(true);
        return dialog;
    }
*/

  /*  public static CustomShowRuleDialog createShowRuleDialog(final Activity mActivity, String url, String knowText) {
        CustomShowRuleDialog.Builder builder = new CustomShowRuleDialog.Builder(mActivity);
        builder.setCanceledOnTouchOutside(true);
        final CustomShowRuleDialog dialog = builder.create();
        dialog.setCancelable(true);
        return dialog;
    }*/

    /*public static CustomSupDialog createScCardSuccDialog(final Activity mActivity, final String endTime, final View.OnClickListener l) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(mActivity);
        builder.setRLayout(R.layout.layout_custom_sc_suc_card_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                if (!TextUtils.isEmpty(endTime)) {
                    ((TextView) mView.findViewById(R.id.custom_dialog_message_view)).setText(String.format("模考结束后出成绩报告,\n请及时关注。"));

                }
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });
                mView.findViewById(R.id.close_sc_card_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });
            }
        });
        CustomSupDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static CustomSupDialog createSignUpTipDialog(final Activity mActivity, final View.OnClickListener l) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(mActivity);
        builder.setRLayout(R.layout.layout_custom_sign_up_tip_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });

                SpannableStringBuilder builder = new SpannableStringBuilder("抱歉，您已错过本次模考，可在模考历史查看，或稍后报名参加下次模考");
//                ForegroundColorSpan yellowSpan = new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.common_style_text_color));
//                builder.setSpan(yellowSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                TextView yellowText = (TextView) mView.findViewById(R.id.sign_up_tip1_tvview);
                if (yellowText != null) {
                    yellowText.setText(builder);
                }

                mView.findViewById(R.id.sign_up_tip_know_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });
            }
        });
        CustomSupDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog dialogGl;

    public static CustomSupDialog createTipsDialog(final Activity mActivity, final String msg, final View.OnClickListener l) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        if (dialogGl != null) {
            dialogGl.dismiss();
            dialogGl = null;
        }
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(mActivity);
        builder.setRLayout(R.layout.layout_custom_tips_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                dialogGl = dialog;
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialogGl = null;
                    }
                });

//                SpannableStringBuilder builder = new SpannableStringBuilder("很遗憾! 您已错过本次模考，\n请及时关注下次模考。");
//                ForegroundColorSpan yellowSpan = new ForegroundColorSpan(ContextCompat.getColor(mView.getContext(), R.color.common_style_text_color));
//                builder.setSpan(yellowSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                TextView yellowText = (TextView) mView.findViewById(R.id.tip1_tv);
                if (yellowText != null) {
                    yellowText.setText(msg);
                }

                mView.findViewById(R.id.tip1_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });
            }
        });
        CustomSupDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static CustomSupDialog createAutoSubmitStDialog(final Activity mActivity, final View.OnClickListener l) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(mActivity);
        builder.setRLayout(R.layout.layout_custom_confirm_tip_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (l != null) {
                            l.onClick(mView);
                        }
                    }
                });

                TextView confirm_content_tv = (TextView) mView.findViewById(R.id.confirm_content_tv);
                TextView confirm_sure_tv = (TextView) mView.findViewById(R.id.confirm_sure_tv);
                if (confirm_content_tv != null) {
                    confirm_content_tv.setText("考试结束时间到，\n" +
                            "系统将自动为您交卷");
                    confirm_content_tv.setTextColor(Color.BLACK);
                }

                if (confirm_sure_tv != null) {
                    confirm_sure_tv.setTextColor(Color.BLACK);
                    confirm_sure_tv.setText("确定");
                    confirm_sure_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (l != null) {
                                l.onClick(mView);
                            }
                        }
                    });
                }
            }
        });
        CustomSupDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static boolean createShowEvaluateReportTipsDialog(final Activity mActivity, int left, int top) {
        if (isActive(mActivity)) {
            EvaluateReportTipsDialog.Builder builder = new EvaluateReportTipsDialog.Builder(mActivity);
            builder.setmLeft(left).setmTop(top).create().show();
            return true;
        }
        return false;
    }

    public static boolean createShowLevelTipsDialog(final Activity mActivity, int left, int top) {
        if (isActive(mActivity)) {
            LevelTrpsDialog.Builder builder = new LevelTrpsDialog.Builder(mActivity);
            builder.setmLeft(left).setmTop(top).create().show();
            return true;
        }
        return false;
    }*/

    private static boolean isActive(Activity context) {
        if (context != null && !context.isFinishing()) {
            return true;
        }
        return false;
    }

    public static CustomConfirmDialog createLayoutDialog(final Activity mActivity,int resLayoutId, View.OnClickListener onClickListener2,
                                                      View.OnClickListener onClickListener, String title,
                                                      CharSequence content, String cancelText, String okText) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        CustomConfirmDialog.SpannableBuilder builder = new CustomConfirmDialog.SpannableBuilder(mActivity);
        builder.setLayoutId(resLayoutId)
               .setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(content)) {
            if(content instanceof SpannableStringBuilder){
                builder.setContentStringBuilder((SpannableStringBuilder)content);
            }
            else {
                builder.setMessage(String.valueOf(content)) ;
            }
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        final CustomConfirmDialog mConfirmDialog = builder.create();

        if (!TextUtils.isEmpty(cancelText)) {
            mConfirmDialog.setNegativeButton(cancelText, onClickListener2);
            mConfirmDialog.setCancelBtnVisibility(true);
        } else {
            mConfirmDialog.setCancelBtnVisibility(false);
        }
        if (!TextUtils.isEmpty(okText)) {
            mConfirmDialog.setPositiveButton(okText, onClickListener);
        }
        if (!mConfirmDialog.isShowing()) {
            mConfirmDialog.show();
        }
        return mConfirmDialog;
    }

 /*   public static CustomConfirmDialog createSysDialog(final Activity mActivity, View.OnClickListener onClickListener2,
                                                      View.OnClickListener onClickListener, String title,
                                                      String content, String cancelText, String okText) {

        return createLayoutDialog(mActivity,R.layout.system_tip_dialog,onClickListener2,onClickListener,title,content,cancelText,okText);

    }*/

   /* public static CustomConfirmDialog onShowMarketDialog(Activity mActivity) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(mActivity);
        builder.setLayoutId(R.layout.market_score_dialog).setCanceledOnTouchOutside(false);
        final CustomConfirmDialog mConfirmDialog = builder.create();


        mConfirmDialog.setCancelBtnVisibility(true);
        mConfirmDialog.setNegativeButton("去吐槽", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackActivity.newInstance(v.getContext());
            }
        });
        mConfirmDialog.setPositiveButton("去鼓励", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefStore.putSettingInt(Constant.APPSTORE_CHECK_FLAG, -1);
                UIJumpHelper.goToMarket(v.getContext(), "com.huatu.handheld_huatu");
            }
        });
        if (!mConfirmDialog.isShowing()) {
            mConfirmDialog.show();
        }
        mConfirmDialog.findViewById(R.id.right_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfirmDialog != null) mConfirmDialog.dismiss();
            }
        });
        return mConfirmDialog;
    }*/


    public static CustomConfirmDialog onShowWarnTraffic(Activity mActivity, View.OnClickListener onClickListener) {
        return DialogUtils.onShowConfirmDialog(mActivity, onClickListener, "提示", ResourceUtils.getString(R.string.warn_network_traffic), "取消", "确定");
    }

    public static CustomConfirmDialog onShowConfirmDialog(Activity mActivity, View.OnClickListener onClickListener, String title,
                                                          String content, String cancelText, String okText) {
        return onShowConfirmDialog(mActivity, onClickListener, null, null, title, content, cancelText, okText);
    }

    public static CustomConfirmDialog onShowConfirmDialog(Activity mActivity, View.OnClickListener onClickListener, View.OnClickListener onClickListener2, String title,
                                                          String content, String cancelText, String okText) {
        return onShowConfirmDialog(mActivity, onClickListener, onClickListener2, null, title, content, cancelText, okText);
    }

    public static CustomConfirmDialog onShowConfirmDialog(Activity mActivity, View.OnClickListener onClickListener, View.OnClickListener onClickListener2, SpannableStringBuilder bTitle, String title,
                                                          String content, String cancelText, String okText) {
        if (Method.isActivityFinished(mActivity)) {
            return null;
        }
        CustomConfirmDialog mConfirmDialog = null;
        if (mConfirmDialog == null) {
            mConfirmDialog = DialogUtils.createDialog(mActivity, title, content);
        }
        if (!TextUtils.isEmpty(bTitle)) {
            mConfirmDialog.setTitle(bTitle);
        }
        if (!TextUtils.isEmpty(title)) {
            mConfirmDialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(content)) {
            mConfirmDialog.setMessage(content);
        }
        boolean showCancel = true;
        if (!TextUtils.isEmpty(cancelText)) {
            mConfirmDialog.setNegativeButton(cancelText, onClickListener2);
            mConfirmDialog.setCancelBtnVisibility(true);
        } else {
            showCancel = false;
            mConfirmDialog.setCancelBtnVisibility(false);
        }
        if (!TextUtils.isEmpty(okText)) {
            mConfirmDialog.setPositiveButton(okText, onClickListener);
            if (!showCancel)
                mConfirmDialog.adjustPositiveWidth();
        }

        if (!mConfirmDialog.isShowing()) {
            mConfirmDialog.show();
        }
        return mConfirmDialog;
    }

 /*   public static void onShowRuleDialog(Activity mActivity, View.OnClickListener onClickListener, String url,
                                        String knowText) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        CustomShowRuleDialog mShowRuleDialog = null;
        if (mShowRuleDialog == null) {
            mShowRuleDialog = DialogUtils.createShowRuleDialog(mActivity, url, knowText);
        }
        if (!TextUtils.isEmpty(url)) {
            mShowRuleDialog.loadWeb(url);
        }
        if (!TextUtils.isEmpty(knowText)) {
            mShowRuleDialog.setPositiveButton(knowText, onClickListener);
        }
        if (!mShowRuleDialog.isShowing()) {
            mShowRuleDialog.show();
        }
    }*/

    /**
     * 灰色取消圆角按钮，红色确定圆角按钮，的Dialog
     */
 /*   public static CustomConfirmDialog onShowRedConfirmDialog(Activity mActivity,
                                                             View.OnClickListener onPositiveClickListener,
                                                             View.OnClickListener onNegativeClickListener,
                                                             String title,
                                                             String content,
                                                             String cancelText,
                                                             String okText) {

        final CustomConfirmDialog dialog = DialogUtils.createDialog(mActivity, title, content);
        dialog.setContentGravity(Gravity.CENTER_HORIZONTAL);
        dialog.setOkBtnConfig(DisplayUtil.dp2px(10), DisplayUtil.dp2px(20), R.drawable.eva_explain_btn_bg);
        dialog.setCancleBtnConfig(DisplayUtil.dp2px(10), DisplayUtil.dp2px(20), R.drawable.eva_cancle_btn_bg);
        dialog.setNegativeColor(Color.parseColor("#6D7172"));
        dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
        dialog.setPositiveButton(okText, onPositiveClickListener);
        if (cancelText != null) {
            dialog.setNegativeButton(cancelText, onNegativeClickListener);
        } else {
            dialog.setCancelBtnVisibility(false);
        }
        dialog.setBtnDividerVisibility(false);
        dialog.setTitleSize(10);
        View contentView = dialog.getContentView();
        LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
        layoutParams.height = DisplayUtil.dp2px(80);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
*/
    /**
     * 红色确定圆角按钮的Dialog
     */
  /*  public static CustomConfirmDialog onShowOnlyConfirmRedDialog(Activity mActivity,
                                                                 String title,
                                                                 String content,
                                                                 String okText) {

        final CustomConfirmDialog dialog = DialogUtils.createDialog(mActivity, title, content);
        dialog.setBtnDividerVisibility(false);
        dialog.setCancelBtnVisibility(false);
        dialog.setMessage(content, 13);
        dialog.setOkBtnConfig(200, 50, R.drawable.eva_explain_btn_bg);
        dialog.setContentGravity(Gravity.START);
        dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
        dialog.setPositiveButton(okText, null);
        dialog.setTitleBold();
        View contentView = dialog.getContentView();
        LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
        layoutParams.height = DisplayUtil.dp2px(66);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }*/

    /**
     * 带图像布局的Dialog
     */
/*    public static CustomConfirmDialog onShowOnlyConfirmRedDialog(Activity mActivity, int layoutId, String content, int imgId) {
        final CustomConfirmDialog dialog = DialogUtils.createDialog(mActivity, layoutId, "", content);
        dialog.setBtnDividerVisibility(false);
        dialog.setCancelBtnVisibility(false);
        View contentView = dialog.getContentView();
        if (imgId > 0) ((ImageView) contentView.findViewById(R.id.tip_img)).setImageResource(imgId);
        dialog.show();
        return dialog;
    }*/

/*
    public static CustomLoadingDialog showLoading(Activity context){
        CustomLoadingDialog  dialog = new CustomLoadingDialog(context);
        dialog.setCancelable(true);

        dialog.show();
        return dialog;
    }
*/

   /* public static CustomLoadingDialog showLoading(Activity context, CustomLoadingDialog dialog) {
        if (dialog == null) {
            dialog = new CustomLoadingDialog(context);
            dialog.setCancelable(true);
        }
        dialog.show();
        return dialog;
    }

    public static void dismissLoading(Dialog dialog) {
        if ((null != dialog) && dialog.isShowing())
            dialog.dismiss();

    }

    public static void showEssaySendBackDialog(Activity context, String backReason, final View.OnClickListener goClickListener) {
        final CorrectDialog essaySendBackDialog = new CorrectDialog(context, R.layout.essay_sendback_msg);
        View mContentView = essaySendBackDialog.mContentView;
        TextView tvTip = mContentView.findViewById(R.id.tv_tip);
        TextView tvCancel = mContentView.findViewById(R.id.tv_cancel);
        TextView tvGo = mContentView.findViewById(R.id.tv_go);
        if (!StringUtils.isEmpty(backReason)) {
            tvTip.setText(backReason);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                essaySendBackDialog.dismiss();
            }
        });
        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goClickListener.onClick(v);
                essaySendBackDialog.dismiss();
            }
        });
        essaySendBackDialog.show();
    }*/
}

