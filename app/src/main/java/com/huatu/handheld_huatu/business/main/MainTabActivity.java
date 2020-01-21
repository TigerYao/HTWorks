package com.huatu.handheld_huatu.business.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.StateFragmentTabHost;
import com.huatu.handheld_huatu.business.arena.utils.ZtkSchemeTargetStartTo;
import com.huatu.handheld_huatu.business.essay.bhelper.ScreenshotDetector;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.me.MeFragment;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.event.me.MeMsgMessageEvent;
import com.huatu.handheld_huatu.helper.GlobalRouterInterceptor;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.XiaoNengAssist;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseItem;
import com.huatu.handheld_huatu.mvpmodel.UpdateInfoBean;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.DownloadManager;
import com.huatu.handheld_huatu.utils.FileDownloader;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.PathConfigure;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.handheld_huatu.view.CustomProgressDialog;
import com.huatu.handheld_huatu.view.MainPopDialog;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by saiyuan on 2016/11/22.
 */

public class MainTabActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    @BindView(android.R.id.tabhost)
    StateFragmentTabHost mTabHost;

    @BindView(R.id.rl_tip)
    RelativeLayout rl_tip;      // 首页设置按钮引导图
    @BindView(R.id.iv_tip_02)
    ImageView ivTip02;

    private final List<Class<? extends Fragment>> fragmentList = new ArrayList<>();
    private final String[] tabNames = new String[]{"题库", "课程", "学习", "我的"};
    private final int[] tabIcons = new int[]{R.drawable.main_tab_icon_one_selector,
            R.drawable.main_tab_icon_two_selector, R.drawable.main_tab_icon_three_selector, R.drawable.main_tab_icon_four_selector};

    private int currentIndex;

    private CustomConfirmDialog mUpdateDialog;              // 新版本弹窗
    private CustomProgressDialog updateProgressDialog;      // 可能是下载进度弹窗

    private int mAdverId;                                   // 弹窗广告id
    private View lianxiTab;
    private View tab_has_msg;
    private boolean isShowMeMsg = false;
    private int uMengJump = 0;
    private ScreenshotDetector mScreenshotDetector;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_main_tab_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(MessageEvent event) {
        if (event.message == MessageEvent.HOME_FRAGMENT_HAS_LOOPER) {
            changeTipSet(1, true);
        } else if (event.message == MessageEvent.HOME_FRAGMENT_NO_LOOPER) {
            changeTipSet(0, true);
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.typeExObject instanceof MeMsgMessageEvent) {
            if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_HAS) {
                if (tab_has_msg != null && !isShowMeMsg) {
                    isShowMeMsg = true;
                    tab_has_msg.setVisibility(View.GONE);
                }
            } else if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_CLOSE) {
                if (tab_has_msg != null) {
                    tab_has_msg.setVisibility(View.GONE);
                }
            } else if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_INTENT_HOMEFRAGMENT) {
                if (mTabHost != null) {
                    currentIndex = 0;
                    mTabHost.setCurrentTab(currentIndex);
                }
            } else if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB) {
                if (mTabHost != null) {
                    currentIndex = 1;
                    mTabHost.setCurrentTab(currentIndex);
                }
            } else if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_SEL_RECORDING_TAB) {

                if (!CommonUtils.checkLogin(this)) {
                    return;
                }
                if (mTabHost != null) {
                    if (mTabHost != null) {
                        currentIndex = 2;
                        mTabHost.setCurrentTab(currentIndex);
                    }
                }
            } else if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_SEL_ME) {
                if (mTabHost != null) {
                    if (mTabHost != null) {
                        currentIndex = 3;
                        mTabHost.setCurrentTab(currentIndex);
                    }
                }
            } else if (event.type == MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SHOW_TIP) {
//                showTips();
            }
        } else if (event.typeExObject instanceof ExamTypeAreaMessageEvent) {
            if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS) {
                // 切换考试类型，刷新Tab，取消刷新
                loadSplashConfig();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openNotificationEnabled();
    }

    // 引导打开通知
    private void openNotificationEnabled() {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(this).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }

        if (!isOpened) {
            boolean noAllowedTip= IoExUtils.checkFileExist("app_notice_tip_flag"+AppUtils.getAppVersion());
            if(noAllowedTip) return;
            DialogUtils.createExitConfirmDialog(this, null, "还没有开启通知提示，打开通知及时了解考试情况", "取消", "开启", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoOpenNotifySet();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IoExUtils.saveJsonFile("1","app_notice_tip_flag"+AppUtils.getAppVersion());
                }
            }).show();
        }
    }

    private void gotoOpenNotifySet() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int uMengJump = intent.getIntExtra("require_index", 0);
            if (uMengJump == 1 || uMengJump == 0) {
                if (mTabHost != null && (currentIndex != uMengJump)) {
                    currentIndex = uMengJump;
                    mTabHost.setCurrentTab(currentIndex);
                }
            } else if (uMengJump == 2) {
                if (!CommonUtils.checkLogin(this)) {
                    return;
                }
                if (mTabHost != null) {
                    currentIndex = 2;
                    mTabHost.setCurrentTab(currentIndex);
                }
            } else {
                if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_CHANGE_TO_ESSAY));
                }
            }
            ZtkSchemeTargetStartTo.startPageFromUri(this, intent.getData());
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // 获取服务器时间并储存
        ServerTimeUtil.newInstance().initServerTime();

        QMUIStatusBarHelper.setStatusBarLightMode(this);

        mScreenshotDetector = new ScreenshotDetector();
        mScreenshotDetector.start(this, new ScreenshotDetector.Call() {
            @Override
            public void callPath(String path) {
            }
        });

        if (rl_tip != null) {
            rl_tip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rl_tip.setVisibility(View.GONE);
                    SpUtils.setDownPaperTip(false);
                }
            });
        }
        mTabHost.setup(this, getSupportFragmentManager(), R.id.main_tab_real_content);
        mTabHost.getTabWidget().setShowDividers(0);

        mTabHost.setOnTabChangedListener(this);
        uMengJump = originIntent.getIntExtra("require_index", 0);

        initTabHost();

        mTabHost.setOnTabChangeIntercept(new StateFragmentTabHost.onTabChangeIntercept() {
            @Override
            public boolean OnBeforeTabIntercept(int index) {
                if (index == 2 && (!CommonUtils.checkLogin(MainTabActivity.this))) {
                    Runnable tmpRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(UniApplicationContext.getContext(), MainTabActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("require_index", 2);
                            UniApplicationContext.getContext().startActivity(intent);
                        }
                    };
                    GlobalRouterInterceptor.mOnLoginResultListener = new GlobalRouterInterceptor.SimpleLoginListener(tmpRunnable);
                    return true;
                }
                return false;
            }
        });

        ZtkSchemeTargetStartTo.startPageFromUri(this, originIntent.getData());
//        Ntalker.getInstance().getPermissions(MainTabActivity.this, 200, permissions);
//        int initSDK = Ntalker.getInstance().initSDK(getApplicationContext(),
//                XiaoNengHomeActivity.siteid, XiaoNengHomeActivity.sdkkey);
//        if (0 == initSDK) {
//            Log.e("initSDK", "初始化SDK成功");
//        } else {
//            Log.e("initSDK", "初始化SDK失败，错误码:" + initSDK);
//        }
//        try {
//            XNSDKCore.getInstance().setReceiveUnReadMsgTime(5);
//            int logIn = Ntalker.getInstance().login(
//                    String.valueOf(SpUtils.getUid()), SpUtils.getUname(), 0);// 登录时调
//            if (0 == logIn) {
//                Log.e("login", "登录成功");
//            } else {
//                Log.e("login", "登录失败，错误码:" + logIn);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtils.e("login", "登录失败，错误码: XNSDKCore");
//        }
        //复制so
        // Utils.copySoFile(getApplicationContext());
        showCommentDialog();
    }

    // 初始化Tab页面
    private void initTabHost() {
        if (mTabHost != null) {
            initFragment();
            initTabs();
            refreshTabHost();
        }
    }

    private void initFragment() {
        fragmentList.clear();
        fragmentList.add(TiKuFragment.class);
        fragmentList.add(AllCourseFragment.class);
        fragmentList.add(StudyCourseFragment.class);
        fragmentList.add(MeFragment.class);
    }

    private void initTabs() {
        int i = 0;
        mTabHost.clearAllTabs();
        for (Class<?> fragment : fragmentList) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabNames[i]);
            View indicator = mLayoutInflater.inflate(R.layout.view_tab_main_indicator, null);
            if (i == 0) {
                lianxiTab = indicator;
            }
            tab_has_msg = (View) indicator.findViewById(R.id.tab_has_msg);
            ImageView icon = (ImageView) indicator.findViewById(R.id.tab_icon);
            icon.setImageResource(tabIcons[i]);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            title.setText(tabNames[i]);
            tabSpec.setIndicator(indicator);
            tabSpec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(MainTabActivity.this);
                }
            });
            mTabHost.addTab(tabSpec, fragment, new Bundle());
            i++;
        }
    }

    private void refreshTabHost() {
        if (mTabHost != null && lianxiTab != null) {
            if (uMengJump == 1) {
                if (mTabHost != null) {
                    currentIndex = 1;
                    mTabHost.setCurrentTab(currentIndex);
                }
            } else if (uMengJump == 2) {
                if (!CommonUtils.checkLogin(MainTabActivity.this)) return;
                if (mTabHost != null) {
                    currentIndex = 2;
                    mTabHost.setCurrentTab(currentIndex);
                }
            }
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        final int tabCount = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < tabCount; i++) {
            View tab = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                tab.findViewById(R.id.tab_icon).setSelected(true);
                tab.findViewById(R.id.tab_title).setSelected(true);
                currentIndex = i;
                if (currentIndex == 0) {
                    StudyCourseStatistic.clickStatistic("题库", "底部页面", "题库");
                } else if (currentIndex == 1) {
                    StudyCourseStatistic.clickStatistic("课程", "底部页面", "课程");
                } else if (currentIndex == 2) {
                    StudyCourseStatistic.clickStatistic("学习", "底部页面", "学习");
                } else if (currentIndex == 3) {
                    StudyCourseStatistic.clickStatistic("我的", "底部页面", "我的");
                }
            } else {
                tab.findViewById(R.id.tab_icon).setSelected(false);
                tab.findViewById(R.id.tab_title).setSelected(false);
            }
        }
//        if (tabNames[3].equals(tabId)) {
//            int meLookTime = PrefStore.getSettingInt(Constant.APPSTORE_CHECK_FLAG, 0);
//            if (meLookTime == -1) return;
//            int intervalDay = CommonUtils.checkIntervalDay();
//            meLookTime++;
//            PrefStore.putSettingInt(Constant.APPSTORE_CHECK_FLAG, meLookTime);
//            if (meLookTime == 2 || (intervalDay >= 15)) {
//                if (intervalDay >= 15) {
//                    PrefStore.putSettingString(Constant.APPSTORE_INTERVALDAY_FLAG, System.currentTimeMillis() + "");
//                }
//                DialogUtils.onShowMarketDialog(this);
//            }
//        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        SignUpTypeDataCache.getInstance().getCategoryListNet(1, compositeSubscription, null);

        loadSplashConfig();
        loadAdv();
        checkUpdate();
    }

    /**
     * 缓存获取Splash倒计时广告
     */
    private void loadSplashConfig() {
        PrefStore.setStorageState(1);
        DataController.getInstance().getSplashConfig(SpUtils.getUserCatgory())
                .map(new Func1<BaseListResponseModel<AdvertiseConfig>, Boolean>() {
                    @Override
                    public Boolean call(BaseListResponseModel<AdvertiseConfig> splashConfigBaseResponseModel) {
                        String parent = PathConfigure.getInstance(UniApplicationContext.getContext()).getImgCachePath() + File.separator + "splash";
                        if (splashConfigBaseResponseModel.code == 1000000) {
                            String str = PrefStore.getSplashConfig();
                            List<AdvertiseConfig> data = splashConfigBaseResponseModel.data;
                            if (data != null && data.size() > 0) {
                                AdvertiseConfig splashConfig = data.get(0);
                                splashConfig.params.catgory = SpUtils.getUserCatgory();
                                AdvertiseItem params = splashConfig.params;
                                AdvertiseConfig config = null;
                                if (!TextUtils.isEmpty(str)) {
                                    config = GsonUtil.GsonToBean(str, AdvertiseConfig.class);
                                }
                                String url = CommonUtils.isPad(UniApplicationContext.getContext()) ? params.padImageUrl : params.image;
                                if (TextUtils.isEmpty(url)) return true;
                                String path = parent + File.separator + url.substring(url.lastIndexOf(File.separator) + 1);
                                if (config != null) {
                                    if (FileUtil.isFileExist(path) && params.id == config.params.id) {
                                        PrefStore.setSplashConfig(GsonUtil.GsonString(splashConfig));//防止服务端更改，没有更新
                                        return true;
                                    }
                                }
                                FileUtil.clearFileWithPath(new File(parent));
                                FileDownloader.download(url, path);
                                PrefStore.setSplashConfig(GsonUtil.GsonString(splashConfig));
                                return true;
                            } else {
                                FileUtil.clearFileWithPath(new File(parent));
                                return false;
                            }
                        } else {
                            FileUtil.clearFileWithPath(new File(parent));
                            return false;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean o) {
                        if (!o) {
                            PrefStore.clearSplashConfig();
                        }
                    }
                });


    }

    /**
     * 首页弹窗广告
     */
    private void loadAdv() {
        DataController.getInstance().getAdvertise().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new Observer<BaseListResponseModel<AdvertiseConfig>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(final BaseListResponseModel<AdvertiseConfig> advertise) {
                if (advertise.code == 1000000) {
                    if (advertise.data == null || advertise.data.size() <= 0) {
                        return;
                    }

                    final AdvertiseConfig config = advertise.data.get(0);
                    int advId = PrefStore.getAdvertiseHomeId();
                    mAdverId = config.params.id;
                    if (advId == mAdverId) {
                        return;
                    }
                    if (PrefStore.getUserSettingInt(Constant.APP_COUPON_CHECK, 0) == 1) {
                        return;
                    }

                    // 广告弹窗
                    final MainPopDialog advDialog = new MainPopDialog(MainTabActivity.this, R.layout.home_advertise);
                    ImageView main_rl_adv_bg = advDialog.findViewById(R.id.main_img_adv_bg);
                    advDialog.reSize(config, MainTabActivity.this, main_rl_adv_bg);

                    main_rl_adv_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            advDialog.dismiss();
                            if (config == null) return;
                            AdvertiseItem params = config.params;
                            if (params == null) return;
                            ZtkSchemeTargetStartTo.doPopDialogAction(MainTabActivity.this, config, compositeSubscription);
                        }
                    });

                    advDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            PrefStore.setAdvertiseHomeId(mAdverId);
                        }
                    });
                    advDialog.showAnim();
                }
            }
        });
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        ServiceProvider.checkUpdate(AppUtils.getChannelIdBaiDu(), new NetResponse() {
            @Override
            public void onError(final Throwable e) {
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                UpdateInfoBean data = (UpdateInfoBean) model.data;
                if (data != null && (data.appVersionBean != null)) {
                    UpdateInfoBean.AppVersion curAppVersion = data.appVersionBean;
                    if (AppUtils.isVersionLower(AppUtils.getAppVersion(), curAppVersion.latestVersion)) {
                        if (curAppVersion.update) {
                            SpUtils.setUpdateUrl(data.appVersionBean.full);
                            SpUtils.setUpdateMessage(data.appVersionBean.message);
                            if (curAppVersion.level == 2) {
                                showFirmUpdateDlg();
                            } else {
                                showUpdateDlg();
                            }
                        }
                    }
                }
            }
        });
    }

    // 强制更新
    private void showFirmUpdateDlg() {
        if (Method.isActivityFinished(this)) {
            return;
        }
        if (mUpdateDialog == null) {
            mUpdateDialog = DialogUtils.createUpdateDialog(this, "发现新版本", SpUtils.getUpdateMessage());
            mUpdateDialog.setPositiveButton("立即更新", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateApp();
                }
            });
            mUpdateDialog.setCancelBtnVisibility(false);
            mUpdateDialog.setBtnDividerVisibility(false);
            mUpdateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    }
                    return false;
                }
            });
        }
        if (!mUpdateDialog.isShowing()) {
            mUpdateDialog.show();
            mUpdateDialog.setCanceledOnTouchOutside(false);
        }
    }

    // 一般更新
    private void showUpdateDlg() {
        if (Method.isActivityFinished(this)) {
            return;
        }
        if (mUpdateDialog == null) {
            mUpdateDialog = DialogUtils.createUpdateDialog(this, "发现新版本", SpUtils.getUpdateMessage());
            mUpdateDialog.setNegativeButton("稍后再说", null);
            mUpdateDialog.setPositiveButton("立即更新", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateApp();
                }
            });
        }
        if (!mUpdateDialog.isShowing()) {
            mUpdateDialog.show();
        }
    }

    // 开始下载
    private void updateApp() {
        DownloadBaseInfo info = new DownloadBaseInfo(SpUtils.getUpdateUrl());
        final String filePath = FileUtil.getDownloadFilePath("netschool", "Update.apk");
        if (FileUtil.isFileExist(filePath))
            FileUtil.deleteFile(filePath);

        if (updateProgressDialog == null) {
            updateProgressDialog = new CustomProgressDialog(MainTabActivity.this);
        }
        updateProgressDialog.setCancelable(false);
        DownloadManager.getInstance().addDownloadTask(info, filePath, new OnFileDownloadListener() {
            @Override
            public void onStart(DownloadBaseInfo baseInfo) {

            }

            @Override
            public void onProgress(DownloadBaseInfo baseInfo, final int percent, final int byteCount, final int totalCount) {
                Method.runOnUiThread(MainTabActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        updateProgressDialog.setMax(totalCount);
                        updateProgressDialog.setProgress(byteCount);
                    }
                });
            }

            @Override
            public void onCancel(DownloadBaseInfo baseInfo) {
                Method.runOnUiThread(MainTabActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        updateProgressDialog.dismiss();
                        CommonUtils.showToast(R.string.download_error);
                    }
                });
            }

            @Override
            public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                Method.runOnUiThread(MainTabActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        updateProgressDialog.dismiss();
                        installApp(filePath);
                    }
                });
            }

            @Override
            public void onFailed(DownloadBaseInfo baseInfo) {
                Method.runOnUiThread(MainTabActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        updateProgressDialog.dismiss();
                        CommonUtils.showToast(R.string.download_error);
                    }
                });
            }
        }, false);
    }

    private String apkFilePath;

    // 安装
    private void installApp(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        apkFilePath = filePath;
        if (shouldRequstInstallPermision())
            return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(UniApplicationContext.getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        UniApplicationContext.getContext().startActivity(intent);
        // finish();
    }

    // 安装软件时 8.0需要判断是否有安装权限
    private boolean shouldRequstInstallPermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                //一定要申请，不然跳转到权限列表，找不到当前的软件
                ActivityCompat.requestPermissions(MainTabActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, Constant.PERMISSION_INSTALL_REUEST_CODE);
                return true;
            }
        }
        return false;
    }

    /**
     * 显示引导图
     */
    private int mTipSetIndex = 0;

    public void changeTipSet() {
        changeTipSet(mTipSetIndex, false);
    }

    public void changeTipSet(int i, boolean hasCheck) {
        int hasFlag = PrefStore.getUserSettingInt(Constant.APP_COUPON_CHECK, 0);
        if ((hasCheck && hasFlag == 1)) {
            mTipSetIndex = i;
            CouponReceiveDialog.checkShow(this);
            return;
        }
        if (SpUtils.getHomeGuideState() != AppUtils.getVersionCode()
                && SpUtils.getUserSubject() == Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST) {
            //行测，事业单位-公基和职测，教师，招警有试卷下载
            //公务员行测
            if (SignUpTypeDataCache.getInstance().getCurSubject() == Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST
                    //事业单位公基
                    || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.PUBLIC_BASE
                    //事业单位职测
                    || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.JOB_TEST
                    //公安招警
                    || SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.PUBLIC_SECURITY
                    //教师
                    || SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.TEACHER_T) {

                // 根据有没有轮播图，改变引导图的位置
                RelativeLayout.LayoutParams params02 = (RelativeLayout.LayoutParams) ivTip02.getLayoutParams();
                if (i == 0) {
                    params02.setMargins(0, DisplayUtil.dp2px(285), 0, 0);
                } else {
                    params02.setMargins(0, DisplayUtil.dp2px(445), 0, 0);
                }

                rl_tip.setVisibility(View.VISIBLE);
                rl_tip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rl_tip.setVisibility(View.GONE);
                    }
                });
                SpUtils.setHomeGuideState();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 跳应用市场五秒钟，说明评价过，就送图币
        if (hasComment && System.currentTimeMillis() - goMarketTime > 5000) {
            SpUtils.setAppCommented(true);
            // 首次评价送图币接口
            ServiceProvider.firstComment(new NetResponse() {
                @Override
                public void onSuccess(BaseResponseModel model) {
                    ToastUtils.showAddCoinTopToast("评价成功，已赠送您30图币。");
                }
            });
        }
        hasComment = false;
    }

    private CustomDialog commentDialog;                     // 评价弹窗
    private boolean hasComment = false;                     // 是否去应用市场评价
    private long goMarketTime = 0;                          // 去应用市场的时间戳

    /**
     * 评价弹窗
     */
    private void showCommentDialog() {
        if (!SpUtils.getAppCommented()) {   // 如果没有评价
            int appLaunchNumber = SpUtils.getAppLaunchNumber() + 1;
            if ((appLaunchNumber == 10 || appLaunchNumber % 50 == 0) && appLaunchNumber > 0) {
                if (!SpUtils.getCommentStatus()) return;
                if (commentDialog == null) {
                    commentDialog = new CustomDialog(this, R.layout.dialog_comment);
                }
                ImageView ivClose = commentDialog.findViewById(R.id.iv_close);
                ImageView ivMarket = commentDialog.findViewById(R.id.iv_market);    // 去评价
                ImageView ivWrong = commentDialog.findViewById(R.id.iv_wrong);      // 去吐槽

                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentDialog.dismiss();
                    }
                });

                ivMarket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentDialog.dismiss();
                        UIJumpHelper.goToMarket(v.getContext(), "com.huatu.handheld_huatu");
                        hasComment = true;
                        goMarketTime = System.currentTimeMillis();
                    }
                });

                ivWrong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentDialog.dismiss();
                        FeedbackActivity.newInstance(MainTabActivity.this);
                    }
                });

                if (!commentDialog.isShowing()) {
                    commentDialog.show();
                }
            }
            SpUtils.setAppLaunchNumber(appLaunchNumber);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 安装下载好的app
        if (requestCode == Constant.PERMISSION_INSTALL_REUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApp(apkFilePath);
            } else {
                Uri packageURI = Uri.parse("package:" + getPackageName());//设置包名，可直接跳转当前软件的设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                startActivityForResult(intent, Constant.PERMISSION_INSTALL_REUEST_CODE);
            }
        }
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
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
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.PERMISSION_INSTALL_REUEST_CODE)
            installApp(apkFilePath);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_index", currentIndex);
        outState.putBoolean("has_comment", hasComment);
        outState.putLong("goMarketTime", goMarketTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentIndex = savedInstanceState.getInt("current_index");
        hasComment = savedInstanceState.getBoolean("has_comment");
        goMarketTime = savedInstanceState.getLong("goMarketTime");
        mTabHost.setCurrentTab(currentIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mScreenshotDetector != null) {
            mScreenshotDetector.unregisterContentObserver();
        }
        if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
            mUpdateDialog.dismiss();
        }
        if (commentDialog != null && commentDialog.isShowing()) {
            commentDialog.dismiss();
        }
        if (updateProgressDialog != null) {
            updateProgressDialog.dismiss();
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
        EventBus.getDefault().removeAllStickyEvents();
        XiaoNengAssist.getInstance().logout();
    }

    public static void newIntent(Context context) {
        newIntent(context, 0);
    }

    public static void newIntent(Context context, int requestIndex) {
        Intent intent = new Intent(context, MainTabActivity.class);
        intent.putExtra("require_index", requestIndex);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
