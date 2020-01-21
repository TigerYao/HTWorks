package com.huatu.handheld_huatu.business.guide;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ZtkSchemeTargetStartTo;
import com.huatu.handheld_huatu.business.login.PrivacyClickableSpan;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.business.other.TestwebActivity;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.helper.QbInitCallback;
import com.huatu.handheld_huatu.listener.AdvancedDownTimer;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseItem;
import com.huatu.handheld_huatu.mvpmodel.RewardInfoBean;
import com.huatu.handheld_huatu.mvpmodel.UpdateInfoBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.tinker.PatchUtils;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.PathConfigure;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author zhaodongdong
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SplashActivity";
    private ImageView mImg_splash;
    private AdvertiseConfig mConfig;
    private DownCountTime mDownCountTime;
    private boolean isShowPb = false;
    private TextView mSplash_tv_jump;
    private RelativeLayout mSplash_bg;
    private Handler mHandler = new Handler();
    private boolean isJump = false;
    private boolean isLoadSplash = false;
    boolean mIsFromNotice = false;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    protected void onInitView() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        super.onInitView();

        //防止出现双实例
        Activity activity = ActivityStack.getInstance().getActivity(MainTabActivity.class);
        if (activity != null && !activity.isFinishing()) {
            finish();
        }

        mIsFromNotice = getIntent().getBooleanExtra("fromNotice", false);

        // 获取服务器时间并储存
        ServerTimeUtil.newInstance().initServerTime();

        getRewardInfo();
        getUpdateInfo();
        initData();
        initView();
        initMethod();
    }

    private static void getRewardInfo() {
        ServiceProvider.getRewardInfo(new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                UniApplicationContext.setRewardInfoMap((Map<String, RewardInfoBean>) model.data);
            }
        });
    }

    private static void getUpdateInfo() {
        ServiceProvider.checkUpdate(AppUtils.getChannelIdBaiDu(), new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                SpUtils.clearUpdateFlag();
                SpUtils.clearUpdateLevelFlag();
                SpUtils.clearUpdateLatestVersion();
                SpUtils.clearUpdateMessage();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {

                UpdateInfoBean data = (UpdateInfoBean) model.data;
                UpdateInfoBean localPatch = PatchUtils.getLocalPatchConfig(UniApplicationContext.getContext());
                if (localPatch != null) {
                    data = localPatch;
                }
                //将UpdateInfoBean保存到本地
                String updateBeanString = GsonUtil.GsonString(data);
                SpUtils.setUpdateBean(updateBeanString);
//                SpUtils.setUpdateCommentStatus(data.commentStatus);
                SpUtils.setUpdatePhotoAnswer(data.photoAnswer);
                SpUtils.setUpdateVoiceAnswer(data.voiceAnswer);
                SpUtils.setUpdatePhotoAnswerMsg(data.photoAnswerMsg);
                SpUtils.setUpdatePhotoAnswerType(data.photoAnswerType);
                SpUtils.setEssayCorrectFree(data.essayCorrectFree);
                SpUtils.setAboutPhone(data.aboutPhone);
                SpUtils.setAboutEmail(data.aboutEmail);
                SpUtils.setCoursePhone(data.coursePhone);
                SpUtils.setSecKillUrl(data.seckillUrl);
                SpUtils.setIsWhite(data.fur);
                PrefStore.putSettingInt(Constant.APP_COURSETYPE_REMOTE_VERSION, data.courseVersion);
            }

        });
    }

    Runnable runnable;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i("zyw", "onPostCreate...isLoadSplash is : " + isLoadSplash);
        if (!isLoadSplash) {
            loadSplash();
        }
//        if (mHandler != null) {
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//                    if (!isLoadSplash) {
//                        loadSplash();
//                    } else {
//                        redirectTo();
//                    }
//                    LogUtils.d("SplashActivity", "onPostCreate loadSplash");
//                }
//            };
//            if (!isLoadSplash) {
//                mHandler.postDelayed(runnable,50);
//            } else {
//                mHandler.postDelayed(runnable,3200);
//            }
//        }
    }

    private void initData() {
        Log.i("zyw", "initData()");
        String splashConfig = PrefStore.getSplashConfig();
        if (!TextUtils.isEmpty(splashConfig)) {
            mConfig = GsonUtil.GsonToBean(splashConfig, AdvertiseConfig.class);
        }
        Log.i("zyw", "mConfig is : " + splashConfig);
        Log.i("zyw", "UserCatgory : " + SpUtils.getUserCatgory());
        SpUtils.setCommentPAIXU("综合排序");
        SpUtils.setCommentMINE_PAIXU("全部课程");
        SpUtils.setCommentMINE_PAIXUORIDER(0);
        SpUtils.setCOMMENT_LIVE_CATE(1000);
        SpUtils.setCOMMENT_LIVE_DATE(1000);
        SpUtils.setCOMMENT_LIVE_PRICE(1000);
        SpUtils.setCOMMENT_LIVE_PAIXU(1000);
        SpUtils.setCommentDATE("");
        SpUtils.setCommentKAOSHI("");
        SpUtils.setCommentPRICE("");
        SignUpTypeDataCache.getInstance().getCategoryListNet(0, compositeSubscription, null);
    }

    private void initView() {
        mImg_splash = findViewById(R.id.img_splash);
        mSplash_tv_jump = findViewById(R.id.splash_tv_jump);
        mSplash_bg = findViewById(R.id.rl_splash);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initMethod() {
        // ActivityPermissionDispatcher.getInstance().setPermissionCallback(this);
        // ActivityPermissionDispatcher.getInstance().checkedWithStorage(SplashActivity.this);
        mImg_splash.setOnClickListener(this);
        mSplash_tv_jump.setOnClickListener(this);
    }

    private void loadSplash() {
        Log.i("zyw", "loadSplash()");

        // 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案
        //https://x5.tencent.com/tbs/technical.html#/detail/sdk/1/edb47a0f-6923-4bd4-af1e-83a07fb1c6e9

        if (Build.VERSION.SDK_INT < 23) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
            QbSdk.initTbsSettings(map);
        } else {
            QbSdk.initX5Environment(UniApplicationContext.getContext(), new QbInitCallback());
        }

        if (!isLoadSplash) {
            isLoadSplash = true;
            if (mConfig != null && mConfig.params != null && mConfig.params.catgory == SpUtils.getUserCatgory()) {

                final boolean isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSplash_tv_jump.setVisibility(View.VISIBLE);
                        mImg_splash.setVisibility(View.VISIBLE);
                        isShowPb = true;
                        String fileName = CommonUtils.isPad(UniApplicationContext.getContext()) ? mConfig.params.padImageUrl : mConfig.params.image;
                        Log.i("zyw", "fileName is :" + fileName);
                        String parent = PathConfigure.getInstance(SplashActivity.this).getImgCachePath() + File.separator + "splash";
                        boolean canjump = CommonUtils.isPad(UniApplicationContext.getContext()) && isLandscape;
                        String path = "";
                        if (!TextUtils.isEmpty(fileName)) {
                            path = parent + File.separator + fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                        } else {
                            canjump = true;
                        }
                        Log.i("zyw", "path is : " + path);
                        Log.i("zyw", "FileUtil.isFileExist(path) is : " + FileUtil.isFileExist(path));

                        if ((!canjump) && FileUtil.isFileExist(path)) {
                            mDownCountTime = new DownCountTime(100, 3000, 3000, mSplash_tv_jump);
                            LogUtils.d("SplashActivity", "System.currentTimeMillis():" + System.currentTimeMillis());
                            mDownCountTime.setStep(100);
                            mDownCountTime.start();
                            File file = new File(path);
                            if (!Method.isActivityFinished(SplashActivity.this)) {
                                try {
                                    ImageLoad.load(SplashActivity.this, file, mImg_splash);
                                } catch (Exception e) {
                                    LogUtils.e(e);
                                }
                            }
                        } else {
                            mSplash_tv_jump.setVisibility(View.GONE);
                            mImg_splash.setVisibility(View.GONE);
                            isShowPb = false;
                            redirectTo();
                        }
                    }
                }, 100);
            } else {
                int hasPermit= PrefStore.getSettingInt(Constant.APP_PRIVACY_TIP,0);
                if(hasPermit==1){
                    redirectTo();
                }else {
                    showPrivacyDialog();
                }
            }
        }
    }

    private void showPrivacyDialog(){
        PrivacyDialog.create(SplashActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.this.finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefStore.putSettingInt(Constant.APP_PRIVACY_TIP,1);
                redirectTo();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_splash:
                if (mConfig != null) {
                    Intent intent;
                    AdvertiseItem params = mConfig.params;
                    if (params == null) return;
                    if (!SpUtils.getLoginState()) {
                        AppContextProvider.addFlags(AppContextProvider.GUESTSPLASH);
                    }
                    switch (mConfig.target) {
                     /*   case "ztk://arena/home":
                            intent = new Intent(SplashActivity.this, AthleticHomeActivity.class);
                            intent.putExtra("toHome", true);
                            startActivity(intent);
                            break;
                        case "ztk://h5/active":
                            intent = new Intent(SplashActivity.this, ActionDetailActivity.class);
                            intent.putExtra("type", 2);
                            intent.putExtra("activityTitle", params.title);
                            intent.putExtra("url", params.url);
                            intent.putExtra("toHome", true);
                            startActivity(intent);
                            break;
                        case "ztk://h5/simulate":
                            intent = new Intent(SplashActivity.this, AdvertiseActivity.class);
                            intent.putExtra("url", params.url);
                            intent.putExtra("type", 1);
                            intent.putExtra("name", params.title);
                            intent.putExtra("toHome", true);
                            startActivity(intent);
                            break;
                        case "ztk://course/detail":
//                            intent = new Intent(SplashActivity.this, BuyDetailsActivity.class);
                            intent = new Intent(SplashActivity.this, BaseIntroActivity.class);
                            intent.putExtra("AdvNetClassId", params.rid + "");
                            intent.putExtra("toHome", true);
                            startActivity(intent);
                            break;*/
                        default:
                            MatchCacheData.getInstance().matchPageFrom = "app启动页";
                            ZtkSchemeTargetStartTo.startTo(this, params, mConfig.target, true, compositeSubscription);
                            break;
                    }
                    isJump = true;
                    mDownCountTime.cancel();

                    // 这些跳转都需要访问网络，所以不直接finish，而在网络回来之后finish
                    ArrayList<String> noFinishTarget = new ArrayList<>(Arrays.asList("ztk://pastPaper", "ztk://match/detail", "ztk://match/essay"));
                    if (!noFinishTarget.contains(mConfig.target)) {
                        finish();
                    }
                }
                break;
            case R.id.splash_tv_jump:
                mDownCountTime.cancel();
                redirectTo();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(TAG);
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(TAG);
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        if (mDownCountTime != null) {
            mDownCountTime.cancel();
        }
        Intent launchDownIntent = new Intent("com.huatu.start_download_course");
        launchDownIntent.putExtra(ArgConstant.TYPE, true);
        sendBroadcast(launchDownIntent);
        if (mHandler != null && runnable != null) {
            LogUtils.d("SplashActivity", "removeCallbacks");
            mHandler.removeCallbacks(runnable);
        }
        super.onDestroy();

    }

    class DownCountTime extends AdvancedDownTimer {
        TextView mJump;

        DownCountTime(long mDownTimeInterval, long mTotalTime, long mRemainTime, TextView jump) {
            super(mDownTimeInterval, mTotalTime, mRemainTime);
            this.mJump = jump;
        }

        @Override
        public void onFinish() {
            redirectTo();
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onTick(long millisUnFinished, int percent) {
            if (isShowPb) {
                mJump.setText((millisUnFinished + 1000) / 1000 + "s 跳过");
            }
        }
    }


    private void redirectTo() {
        Log.i("zyw", "redirectTo(), isJump is : " + isJump);
        if (!isJump) {
            isJump = true;
            LogUtils.d("SplashActivity", "System.currentTimeMillis():" + System.currentTimeMillis());

            if (mIsFromNotice) {

                LogUtils.e("formNotice", "true");
                int stackSize = ActivityStack.getInstance().getSize();
                if (stackSize <= 1) {
                    MainTabActivity.newIntent(this);
                }
                SplashActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return;
            }
            //有可能pad引导oom出现异常
            if ((!CommonUtils.isPadv2(this)) && PrefStore.getGuideFirstRun()) {
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            } else if (SpUtils.getLoginState()) {
                if (SpUtils.getUserCatgory() <= 0 || SpUtils.getUserSubject() <= 0) {
                    ExamTargetAreaActivity.newIntent(SplashActivity.this, ExamTargetAreaActivity.NO_SET_REGISTER, false);
                } else {
                    MainTabActivity.newIntent(this);
                }
            } else {
                MainTabActivity.newIntent(this);
                // UIJumpHelper.startActivity(this,AboutActivity.class);
                //LoginByPasswordActivity.newIntent(this);
            }
            SplashActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ActivityPermissionDispatcher.getInstance().onRequestPermissionResult(SplashActivity.this, requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
}
