package com.huatu.handheld_huatu.business.ztk_vod;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.AbsDialogFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.DetachableDialogDismissListener;
import com.huatu.handheld_huatu.listener.DetachableDialogShowListener;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by cjx on 2018\7\19 0019.
 */


public class ShareDialogFragment extends AbsDialogFragment implements View.OnClickListener {


    // private LoginDailogFragment fragment;
    private String mCourseId;
    private String mImgurl;
    private String shareSyllabusId;//大纲Id
    private long courseWareId;//课件Id

    private ShareInfo mCurrentShareInfo;
    private int classType;

    public static ShareDialogFragment getInstance(String courseId, String imgurl) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putString(ArgConstant.COURSE_IMG, imgurl);
        ShareDialogFragment tmpFragment = new ShareDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    public static ShareDialogFragment getInstance(String courseId, long courseWareId,  String imgurl, String shareSyllabusId, int classType) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putLong(ArgConstant.COURSE_WARE_ID,courseWareId);
        args.putString(ArgConstant.COURSE_IMG, imgurl);
        args.putString(ArgConstant.SYLLABUS_ID, shareSyllabusId);
        args.putInt("classType",classType);
        ShareDialogFragment tmpFragment = new ShareDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }


    public static ShareDialogFragment getInstance(String Id, String imgurl, String url, String title, String content) {
        return getInstance(Id, imgurl, 0, url, title, content);
    }

    public static ShareDialogFragment getInstance(String Id, String imgurl, int imgResource, String url, String title, String content) {
        ShareInfo tmpInfo = new ShareInfo();
        tmpInfo.imgUrl = imgurl;
        tmpInfo.id = Id;
        tmpInfo.url = url;
        tmpInfo.title = title;
        tmpInfo.desc = content;
        tmpInfo.imgResource = imgResource;
        Bundle args = new Bundle();
        args.putInt(ArgConstant.TYPE, 1);
        args.putSerializable(ArgConstant.BEAN, tmpInfo);
        ShareDialogFragment tmpFragment = new ShareDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    protected void parserParams(Bundle args) {

        int dialogType = args.getInt(ArgConstant.TYPE, 0);
        if (dialogType == 0) {
            mCourseId = args.getString(ArgConstant.COURSE_ID);
            mImgurl = args.getString(ArgConstant.COURSE_IMG);
            classType = args.getInt("classType", 0);
            courseWareId = args.getLong(ArgConstant.COURSE_WARE_ID);//课件id
            shareSyllabusId = args.getString(ArgConstant.SYLLABUS_ID);//大纲id
        } else {
            mCurrentShareInfo = (ShareInfo) args.getSerializable(ArgConstant.BEAN);
        }
    }

    public void setCourseId(String courseId) {
        this.mCourseId = courseId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private ArrayList<SpringAnimation> mLetterAnims = new ArrayList<>();
    ;

    ViewGroup mLettersLayout;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        boolean isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        View view = LayoutInflater.from(getActivity()).inflate(isLandscape ?
                R.layout.share_landscape_action_layout : R.layout.share_action_layout, null);
        Dialog dialog = new Dialog(getActivity(), isLandscape ? R.style.NoDimThemePopup : R.style.DimThemePopup);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        view.findViewById(R.id.cancel_action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ViewGroup actionLayout = (ViewGroup) view.findViewById(R.id.shart_root_layout);
        mLettersLayout = actionLayout;
        int distance = DensityUtils.dp2px(view.getContext(), 40);
        for (int i = 0; i < actionLayout.getChildCount(); i++) {

            View letterView = actionLayout.getChildAt(i);
            letterView.setOnClickListener(this);
            letterView.setTranslationY(distance);
            SpringAnimation letterAnimY = new SpringAnimation(letterView, SpringAnimation.TRANSLATION_Y, 0);
            letterAnimY.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);
            letterAnimY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
            mLetterAnims.add(letterAnimY);
        }

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ArrayUtils.isEmpty(mLetterAnims) || mLettersLayout == null) return;
        for (final SpringAnimation letterAnim : mLetterAnims) {
            mLettersLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    letterAnim.start();
                }
            }, 300 + 50 * mLetterAnims.indexOf(letterAnim));
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getTag() == null) return;
        int action = StringUtils.parseInt(v.getTag().toString());
/*
        ShareInfo  tmpShareInfo= new ShareInfo();
        tmpShareInfo.id="e2823988801843c68d71f2197bf36ae1";
        tmpShareInfo.title="【华图在线】2018黑龙江省考面试备考指导";
        tmpShareInfo.desc="华图在线直播课程，汇聚名师大咖，为你公考路上保驾护航。";
        tmpShareInfo.url="http://weixin.htexam.com/pc/v2/share/course/e2823988801843c68d71f2197bf36ae1";
        tmpShareInfo.imgUrl="http://upload.htexam.net/classimg/class/1518146604.jpg";*/
        switch (action) {
            case 0:
                getSendClass(SHARE_MEDIA.WEIXIN);
                break;
            case 1:
                getSendClass(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case 2:
                //  ShareUtil.startShare(getActivity(),tmpShareInfo, SHARE_MEDIA.QQ);

                getSendClass(SHARE_MEDIA.QQ);
                break;
            case 3:
                //ShareUtil.startShare(getActivity(),tmpShareInfo, SHARE_MEDIA.SINA);
                //dismiss();
                getSendClass(SHARE_MEDIA.SINA);
                break;
            case 4:
      /*          ClipboardManager cmb = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tmpShareInfo.url); // 复制
                ToastUtils.makeText(UniApplicationContext.getContext(), "复制链接成功").show();
                dismiss();*/
                getSendClass(SHARE_MEDIA.SMS);

                break;
        }
    }

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
       Tencent.onActivityResultData(requestCode, resultCode, data, mUiListener);
        if(requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE){
            if (resultCode == Constants.ACTIVITY_OK) {
               // Tencent.handleResultData(data, mUiListener);
                ToastUtils.showShort("ok");
            }
        }
    }*/


    private void shareMusic(final SHARE_MEDIA sharemedia, final CustomDialog customDialog){
        ServiceProvider.getAudioShareInfo(getSubscription(), StringUtils.parseLong(mCourseId),courseWareId, StringUtils.parseLong(shareSyllabusId), classType, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                CommonUtils.showToast("分享数据有误");
                customDialog.dismiss();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                ShareInfo shareInfo = (ShareInfo) model.data;
                ShareUtil.startShare(getActivity(), shareInfo, sharemedia, true);
                customDialog.dismiss();
                dismiss();
            }
        });

    }

    private void getSendClass(final SHARE_MEDIA sharemedia) {
        // 分享渠道回传
        ArenaExamMessageEvent event = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE_WAY);
        Bundle bundle = new Bundle();
        bundle.putString("share_way", getShareWay(sharemedia));
        event.extraBundle = bundle;
        EventBus.getDefault().post(event);

        if (mCurrentShareInfo != null) {
            if (sharemedia == SHARE_MEDIA.SMS) {
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(mCurrentShareInfo.url); // 复制
                ToastUtils.makeText(UniApplicationContext.getContext(), "复制链接成功").show();
                dismiss();
            } else {
                ShareUtil.startShare(getActivity(), mCurrentShareInfo, sharemedia, classType == 5);
                dismiss();
            }

            String shareId=TextUtils.isEmpty(mCourseId) ? mCurrentShareInfo.id : mCourseId;
            if(!TextUtils.isEmpty(shareId))
               StudyCourseStatistic.shareCourse(shareId, sharemedia);
            return;
        }
        final CustomDialog customDialog = new CustomDialog(getContext(), R.layout.dialog_feedback_commit);
        TextView tv_notify_message = (TextView) customDialog.mContentView.findViewById(R.id.tv_notify_message);
        tv_notify_message.setText("获取分享数据中...");
        customDialog.show();

        if (classType == 5){
            shareMusic(sharemedia, customDialog);
            return;
        }

        Subscription imageSubscription = DataController.getInstance()
                .sendClass(StringUtils.parseLong(mCourseId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<ShareInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        customDialog.dismiss();
                        CommonUtils.showToast("获取分享数据失败");
                    }

                    @Override
                    public void onNext(BaseResponseModel<ShareInfo> testBean) {
                        customDialog.dismiss();
                        long code = testBean.code;
                        if (code == 1000000) {
                            //ToDo 送金币提示
//                            if (SpUtils.getFreeCourseListenFlag()) {
//                                ToastUtils.showRewardToast("WATCH_FREE");
//                                SpUtils.setFreeCourseListenFlag(false);
//                            }
                            if (sharemedia == SHARE_MEDIA.SMS) {
                                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                cmb.setText(testBean.data.url); // 复制
                                ToastUtils.makeText(UniApplicationContext.getContext(), "复制链接成功").show();
                                dismiss();
                            } else {

                                if ((null != testBean.data) && TextUtils.isEmpty(testBean.data.imgUrl))
                                    testBean.data.imgUrl = mImgurl;

                                ShareUtil.startShare(getActivity(), testBean.data, sharemedia);
                                dismiss();
                            }
                        } else {
                            CommonUtils.showToast("获取分享数据失败");
                        }
                    }
                });
        getSubscription().add(imageSubscription);
    }

    private String getShareWay(SHARE_MEDIA sharemedia) {
        switch (sharemedia) {
            case WEIXIN:
                return "微信";
            case WEIXIN_CIRCLE:
                return "朋友圈";
            case QQ:
                return "QQ";
            case SINA:
                return "微博";
            case SMS:
                return "复制链接";
            default:
                return "";
        }
    }

    //{"data":{"id":"e2823988801843c68d71f2197bf36ae1","title":"【华图在线】2018黑龙江省考面试备考指导","desc":"华图在线直播课程，汇聚名师大咖，为你公考路上保驾护航。","url":"http://weixin.htexam.com/pc/v2/share/course/e2823988801843c68d71f2197bf36ae1","reportInfo":null,"answerCard":null},"code":1000000}
}
