package com.huatu.handheld_huatu.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_vod.ShareDialogFragment;
import com.huatu.handheld_huatu.mvpmodel.RewardInfoBean;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;


public class ShareUtil {
    public static void test(final Activity activity, final String id, final String desc, final String title, String url) {
        test(activity, id, desc, title, url, null, null);
    }

    public static void test(final Activity activity, final String id, final String desc, final String title, String url, String courseId) {
        test(activity, id, desc, title, url, null, courseId);
    }

    public static void test(final Activity activity, final String id, final String desc, final String title, String url, final String actionFrom, String courseId) {
        test(activity, id, desc, title, url, null, null, courseId);
    }

    public static void startShare(final Activity activity, final ShareInfo shareinfo, SHARE_MEDIA shareMedia) {
        startShare(activity, shareinfo, shareMedia, false);
    }

    public static void startShare(final Activity activity, final ShareInfo shareinfo, SHARE_MEDIA shareMedia, boolean isMusic) {
        if (activity == null || shareinfo == null || shareinfo.title == null || shareinfo.url == null) {
            CommonUtils.showToast("分享数据有误");
            return;
        }
        LogUtils.d(activity, GsonUtil.GsonString(shareinfo));

        UMShareListener tmpShareLisener = new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA var1) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                if ((!TextUtils.isEmpty(shareinfo.id))&&(SpUtils.getLoginState())) {
                    ServiceProvider.postShareReward(shareinfo.id, new NetResponse() {
                        @Override
                        public void onSuccess(BaseResponseModel model) {
                            super.onSuccess(model);
                            if (model != null && model.data instanceof RewardInfoBean) {
                                RewardInfoBean var = (RewardInfoBean) model.data;
                                if (var != null && (var.gold > 0 || var.experience > 0)) {
                                    ToastUtils.showRewardToast("SHARE");
//                                            SpUtils.setShareFlag(false);
                                }
                            }
                        }
                    });
                }
                ToastUtils.showShort("分享成功");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                ToastUtils.showShort("分享失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                //CommonUtils.showToast("分享取消");
            }
        };

        //音乐分享
        if (isMusic && shareinfo.videoInfo != null){
            shareUMMusic(activity,shareinfo, shareMedia, tmpShareLisener);
            return;
        }

        if (shareMedia == SHARE_MEDIA.SINA) {
            UMImage imagelocal;
            if (shareinfo.imgResource > 0) {
                imagelocal = new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), shareinfo.imgResource));
            } else if (TextUtils.isEmpty(shareinfo.imgUrl)) {
                imagelocal = new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_app));
            } else {
                imagelocal = new UMImage(activity, shareinfo.imgUrl);
            }
            imagelocal.setThumb(new UMImage(activity, R.drawable.icon_app));
            new ShareAction(activity).withText(shareinfo.title + "。" + shareinfo.desc + shareinfo.url)
                    .withMedia(imagelocal)
                    .setPlatform(SHARE_MEDIA.SINA)
                    .setCallback(tmpShareLisener).share();
            return;
        }
        UMWeb web = new UMWeb(shareinfo.url);
        web.setTitle(shareinfo.title);
        web.setDescription(shareinfo.desc + "  \n");
        if (shareinfo.imgResource > 0) {
            web.setThumb(new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), shareinfo.imgResource)));
        } else {
            web.setThumb(!TextUtils.isEmpty(shareinfo.imgUrl) ? new UMImage(activity, shareinfo.imgUrl) : new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_app)));
        }
        new ShareAction(activity).withMedia(web)
                .setPlatform(shareMedia)
                .setCallback(tmpShareLisener)
                .share();


    }

    public static void shareUMMusic(Activity activity, ShareInfo shareInfo, SHARE_MEDIA share_media, UMShareListener shareListener) {
        UMImage umImage = !TextUtils.isEmpty(shareInfo.videoInfo.thumbnail) ? new UMImage(activity, shareInfo.videoInfo.thumbnail) : new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_app));
        String musicUrl = shareInfo.videoInfo.videoUrl;// : "http://m.v.huatu.com/group51/M00/C9/F2/wKgKnlvRI2vA4IpbAFGRbrsenF8956.m4a";//"http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";//"http://m.v.huatu.com/aca3729e1411562b5799f2bd323f8db5/5d67e1f6/00-x-upload/video/25337252_90b3f9c66cd0b92fa8dcc10113b2a308_ZKS6q2zu.mp4?uuid=uuid-ed10ae70-8907-f094-9d96-dc8dcf66256b";
        UMusic umMusic = new UMusic(musicUrl);
        umMusic.setThumb(umImage);
        umMusic.setTitle(shareInfo.title);
        umMusic.setDescription(shareInfo.desc);
        umMusic.setmTargetUrl(shareInfo.url);//("http://m.v.huatu.com/wap/?#/class/classDetail?collageActiveId=0&classId=78247");
        new ShareAction(activity)
                .withMedia(umMusic)
                .setPlatform(share_media)
                .setCallback(shareListener).share();
    }

//    public static void shareWXMusic(Activity activity, ShareInfo shareInfo){
//        IWXAPI api =  WXAPIFactory.createWXAPI(activity, "wxd0611111b31aa452");
//        WXMusicObject music = new WXMusicObject();
//        //music.musicUrl = "http://www.baidu.com";
//        String musicUrl = "http://m.v.huatu.com/aca3729e1411562b5799f2bd323f8db5/5d67e1f6/00-x-upload/video/25337252_90b3f9c66cd0b92fa8dcc10113b2a308_ZKS6q2zu.mp4?uuid=uuid-ed10ae70-8907-f094-9d96-dc8dcf66256b";//"http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
//
//        music.musicUrl=musicUrl;//"http://dalbj-video.baijiayun.com/8d3eaae4a931d8bb44c63664c0fcd557/5d651653/00-x-upload/video/24854336_fa0485db1d39a1f9b071134be868808f_STzs8tP3.mp4?uuid=uuid-ed10ae70-8907-f094-9d96-dc8dcf66256b";//"http://m801.music.126.net/20190827192253/b6aacced401148cf495c87a8b611ce7c/jdyyaac/5153/020b/070f/f96cdc5c8e5ae945924f0ec513061b98.m4a";//"http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
//        //music.musicUrl="http://120.196.211.49/XlFNM14sois/AKVPrOJ9CBnIN556OrWEuGhZvlDF02p5zIXwrZqLUTti4o6MOJ4g7C6FPXmtlh6vPtgbKQ==/31353278.mp3";
//
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = music;
//        msg.title = shareInfo.title;//"Music Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
//        msg.description = shareInfo.desc;//"Music Album Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
////        msg.messageAction = shareInfo.url;
//        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_app);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
//        bmp.recycle();
//        msg.thumbData = ImageUtil.bmpToByteArray(thumbBmp, true);
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = "music"+System.currentTimeMillis();
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneSession;
//        api.sendReq(req);
//    }


/*    public void shareMINApp(){
        UMMin umMin = new UMMin(Defaultcontent.url);
        umMin.setThumb(new UMImage(this,R.drawable.thumb));
        umMin.setTitle(Defaultcontent.title);
        umMin.setDescription(Defaultcontent.text);
        umMin.setPath("pages/page10007/page10007");
        umMin.setUserName("gh_3ac2059ac66f");
        new ShareAction(ShareDetailActivity.this)
                .withMedia(umMin)
                .setPlatform(share_media)
                .setCallback(shareListener).share();
    }*/

    public static void test(final Activity activity, final String id, final String desc, final String title, final String url, final String imgUrl, final String actionFrom, String courseId) {
        test(activity, id, desc, title, url, imgUrl, 0, actionFrom, courseId);
    }

    public static void test(final Activity activity, final String id, final String desc, final String title, final String url, final String imgUrl, final int imgResource, final String actionFrom, String courseId) {
        test(activity, id, desc, title, url, imgUrl, 0, actionFrom, courseId, false);
    }

    public static void test(final Activity activity, final String id, final String desc, final String title, final String url, final String imgUrl, final int imgResource, final String actionFrom, String courseId, boolean isMusic) {
        if (activity == null || desc == null || title == null || url == null) {
            CommonUtils.showToast("分享数据有误");
            return;
        }

        LogUtils.d(activity, desc, title, url);
        if (!Method.isActivityFinished(activity)) {
            if (activity instanceof FragmentActivity) {
                ShareDialogFragment fragment = ShareDialogFragment.getInstance(id, imgUrl, imgResource, url, title, desc);
                // fragment.setTargetFragment(MainFragment.this, REQUEST_CODE);
                fragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "share");
                fragment.setCourseId(courseId);
                return;
            }
        }

        ShareAction mShareAction = new ShareAction(activity).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

                        UMShareListener tmpShareLisener = new UMShareListener() {
                            @Override
                            public void onStart(SHARE_MEDIA var1) {
                            }

                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                if (!TextUtils.isEmpty(id)) {
                                    ServiceProvider.postShareReward(id, new NetResponse() {
                                        @Override
                                        public void onSuccess(BaseResponseModel model) {
                                            super.onSuccess(model);
                                            if (model != null && model.data instanceof RewardInfoBean) {
                                                RewardInfoBean var = (RewardInfoBean) model.data;
                                                if (var != null && (var.gold > 0 || var.experience > 0)) {
                                                    ToastUtils.showRewardToast("SHARE");
//                                            SpUtils.setShareFlag(false);
                                                }
                                            }
                                        }
                                    });
                                }
                                ToastUtils.showShort("分享成功");
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                LogUtils.e(throwable.getMessage());
                                ToastUtils.showShort("分享失败");
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                //CommonUtils.showToast("分享取消");
                            }
                        };

                        if (share_media == SHARE_MEDIA.SINA) {
                            UMImage imagelocal;
                            if (TextUtils.isEmpty(imgUrl))
                                imagelocal = new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_app));
                            else
                                imagelocal = new UMImage(activity, imgUrl);
                            imagelocal.setThumb(new UMImage(activity, R.drawable.icon_app));
                            new ShareAction(activity).withText(title + "。" + desc + url)
                                    .withMedia(imagelocal)
                                    .setPlatform(SHARE_MEDIA.SINA)
                                    .setCallback(tmpShareLisener).share();
                            return;
                        }
                        UMWeb web = new UMWeb(url);
                        web.setTitle(title);
                        web.setDescription(desc);
                        web.setThumb(!TextUtils.isEmpty(imgUrl) ? new UMImage(activity, imgUrl)
                                : new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_app)));
                        new ShareAction(activity).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(tmpShareLisener)
                                .share();
                    }

                });

        ShareBoardConfig config = new ShareBoardConfig();//新建ShareBoardConfig               config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);//设置位置
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setIndicatorVisibility(false);
        config.setShareboardBackgroundColor(Color.WHITE);
        mShareAction.open(config);


    }
}
