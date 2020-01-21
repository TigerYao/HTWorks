package com.huatu.handheld_huatu.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.huatu.handheld_huatu.R;

/**
 */
public class PopWindowUtil {

//    if(popWindowChatBar==null) {
//        PopDialogUtil.showPopWindow(this, rl_mic_first_container, 13, 0, R.layout.chat_bar_tog_audio,
//                90, 65, R.style.chatbarpopwindowAnim, new PopDialogUtil.PopViewCall() {
//                    @Override
//                    public void popViewCall(View contentView, PopupWindow popWindow) {
//                        popWindowChatBar = popWindow;
//                        openFirstAudio = (TextView) contentView.findViewById(R.id.open_first_audio);
//                        openSecondAudio = (TextView) contentView.findViewById(R.id.open_second_audio);
//                        closeDownAudio = (TextView) contentView.findViewById(R.id.close_down_audio);
//                        if (null != openFirstAudio)
//                            openFirstAudio.setOnClickListener(AudioChatBarNormalActivity.this);
//                        if (null != openSecondAudio)
//                            openSecondAudio.setOnClickListener(AudioChatBarNormalActivity.this);
//                        if (null != closeDownAudio)
//                            closeDownAudio.setOnClickListener(AudioChatBarNormalActivity.this);
//                    }
//
//                    @Override
//                    public void popViewDismiss() {
//
//                    }
//                });
//    }else{
//        popWindowChatBar.showAsDropDown(rl_mic_first_container, DisplayUtil.dp2px(27), 0);
//    }

    public interface PopViewCall {
        void popViewCall(View contentView, PopupWindow popWindow);
        void popViewDismiss();
    }

    public static void showPopWindow(Context mContext, View anchor, int xoffdp, int yoffdp, int layout, int widthdp,
                                     int heightdp, final PopViewCall mPopViewCall) {
        View contentView = View.inflate(mContext, layout, null);
        PopupWindow popWindow = null;
        popWindow = new PopupWindow(contentView, DisplayUtil.dp2px(widthdp), DisplayUtil.dp2px(heightdp));
        popWindow.setContentView(contentView);
        popWindow.setAnimationStyle(R.style.homePopwindowAnim);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mPopViewCall != null) {
                    mPopViewCall.popViewDismiss();
                }
            }
        });
        if (popWindow != null && !popWindow.isShowing()) {
            if (mPopViewCall != null) {
                mPopViewCall.popViewCall(contentView, popWindow);
            }
            int xoffVar = xoffdp > 0 ? DisplayUtil.dp2px(Math.abs(xoffdp)) : -DisplayUtil.dp2px(Math.abs(xoffdp));
            int yoffVar = yoffdp > 0 ? DisplayUtil.dp2px(Math.abs(yoffdp)) : -DisplayUtil.dp2px(Math.abs(yoffdp));
            popWindow.showAsDropDown(anchor, xoffVar, yoffVar);
        }
    }

    public static void showPopWindow_showAtLocation(Context mContext, View anchor, int xoffdp, int yoffdp, int layout, int widthdp,
                                     int heightdp, final PopViewCall mPopViewCall) {
        if (mContext != null && mContext instanceof Activity && !Method.isActivityFinished((Activity) mContext)) {
            View contentView = View.inflate(mContext, layout, null);
            PopupWindow popWindow = null;
            popWindow = new PopupWindow(contentView, DisplayUtil.dp2px(widthdp), DisplayUtil.dp2px(heightdp));
            popWindow.setContentView(contentView);
            popWindow.setAnimationStyle(R.style.homePopwindowAnim);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new BitmapDrawable());
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mPopViewCall != null) {
                        mPopViewCall.popViewDismiss();
                    }
                }
            });
            if (popWindow != null && !popWindow.isShowing()) {
                if (mPopViewCall != null) {
                    mPopViewCall.popViewCall(contentView, popWindow);
                }
                int xoffVar = xoffdp > 0 ? DisplayUtil.dp2px(Math.abs(xoffdp)) : -DisplayUtil.dp2px(Math.abs(xoffdp));
                int yoffVar = yoffdp > 0 ? DisplayUtil.dp2px(Math.abs(yoffdp)) : -DisplayUtil.dp2px(Math.abs(yoffdp));
                popWindow.showAtLocation(anchor, Gravity.CENTER, xoffVar, yoffVar);
            }
        }
    }

    public static void showPop_showAtLocation_match(Context mContext, View anchor, int xoffdp, int yoffdp, int layout, int widthdp,
                                     int heightdp, final PopViewCall mPopViewCall) {
        if (mContext != null && mContext instanceof Activity && !Method.isActivityFinished((Activity) mContext)) {
            View contentView = View.inflate(mContext, layout, null);
            PopupWindow popWindow = null;
//            popWindow = new PopupWindow(contentView, DisplayUtil.dp2px(widthdp), DisplayUtil.dp2px(heightdp));
            popWindow = new PopupWindow(contentView, widthdp, heightdp);
            popWindow.setContentView(contentView);
//            popWindow.setAnimationStyle(R.style.homePopwindowAnim);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new BitmapDrawable());
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mPopViewCall != null) {
                        mPopViewCall.popViewDismiss();
                    }
                }
            });
            if (popWindow != null && !popWindow.isShowing()) {
                if (mPopViewCall != null) {
                    mPopViewCall.popViewCall(contentView, popWindow);
                }
                int xoffVar = xoffdp > 0 ? DisplayUtil.dp2px(Math.abs(xoffdp)) : -DisplayUtil.dp2px(Math.abs(xoffdp));
                int yoffVar = yoffdp > 0 ? DisplayUtil.dp2px(Math.abs(yoffdp)) : -DisplayUtil.dp2px(Math.abs(yoffdp));
                popWindow.showAtLocation(anchor, Gravity.CENTER, xoffVar, yoffVar);
            }
        }
    }

    public static void showPopWindow(Context mContext, View anchor, int xoffdp, int yoffdp, int layout,
                                     int widthdp, int heightdp, int style, final PopViewCall mPopViewCall) {
        View contentView = View.inflate(mContext, layout, null);
        PopupWindow popWindow = null;
        popWindow = new PopupWindow(contentView, DisplayUtil.dp2px(widthdp), DisplayUtil.dp2px(heightdp));
        popWindow.setContentView(contentView);
        popWindow.setFocusable(true);
        popWindow.setAnimationStyle(style);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mPopViewCall != null) {
                    mPopViewCall.popViewDismiss();
                }
            }
        });
        if (popWindow != null && !popWindow.isShowing()) {
            if (mPopViewCall != null) {
                mPopViewCall.popViewCall(contentView, popWindow);
            }
            int xoffVar = xoffdp > 0 ? DisplayUtil.dp2px(Math.abs(xoffdp)) : -DisplayUtil.dp2px(Math.abs(xoffdp));
            int yoffVar = yoffdp > 0 ? DisplayUtil.dp2px(Math.abs(yoffdp)) : -DisplayUtil.dp2px(Math.abs(yoffdp));
            popWindow.showAsDropDown(anchor, xoffVar, yoffVar);
        }
    }

    public static void showPopInCenter(Context mContext, View anchor, int xoffdp, int yoffdp, int layout, int widthdp,
                                       int heightdp, final PopViewCall mPopViewCall) {
        if (mContext != null && mContext instanceof Activity) {
            View contentView = View.inflate(mContext, layout, null);
            PopupWindow popWindow = null;
            popWindow = new PopupWindow(contentView, DisplayUtil.dp2px(widthdp), DisplayUtil.dp2px(heightdp));
            popWindow.setContentView(contentView);
//            popWindow.setAnimationStyle(R.style.redBagAnim);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new BitmapDrawable());
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mPopViewCall != null) {
                        mPopViewCall.popViewDismiss();
                    }
                }
            });
            if (popWindow != null && !popWindow.isShowing()) {
                if (mPopViewCall != null) {
                    mPopViewCall.popViewCall(contentView, popWindow);
                }
                int xoffVar = xoffdp > 0 ? DisplayUtil.dp2px(Math.abs(xoffdp)) : -DisplayUtil.dp2px(Math.abs(xoffdp));
                int yoffVar = yoffdp > 0 ? DisplayUtil.dp2px(Math.abs(yoffdp)) : -DisplayUtil.dp2px(Math.abs(yoffdp));
                popWindow.showAtLocation(anchor, Gravity.CENTER, xoffVar, yoffVar);
            }
        }
    }

    public static void showPopInCenter(Context mContext, View anchor, int xoffdp, int yoffdp,View contentView, int widthdp,
                                       int heightdp, final PopViewCall mPopViewCall) {
        if (mContext != null && mContext instanceof Activity) {
            PopupWindow popWindow = null;
            popWindow = new PopupWindow(widthdp,heightdp);
            popWindow.setContentView(contentView);
//            popWindow.setAnimationStyle(R.style.redBagAnim);
            popWindow.setFocusable(true);
//            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new BitmapDrawable());
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mPopViewCall != null) {
                        mPopViewCall.popViewDismiss();
                    }
                }
            });
            if (popWindow != null && !popWindow.isShowing()) {
                if (mPopViewCall != null) {
                    mPopViewCall.popViewCall(contentView, popWindow);
                }
                int xoffVar = xoffdp > 0 ? DisplayUtil.dp2px(Math.abs(xoffdp)) : -DisplayUtil.dp2px(Math.abs(xoffdp));
                int yoffVar = yoffdp > 0 ? DisplayUtil.dp2px(Math.abs(yoffdp)) : -DisplayUtil.dp2px(Math.abs(yoffdp));
                popWindow.showAtLocation(anchor, Gravity.BOTTOM, xoffVar, yoffVar);
            }
        }
    }

}
