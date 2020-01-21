package com.huatu.handheld_huatu.business.arena.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseArenaCorrectActivity;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityPad;
import com.huatu.handheld_huatu.business.arena.activity.ArenaRestActivity;
import com.huatu.handheld_huatu.business.arena.downloadpaper.ArenaPaperLocalFileManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperInfoNet;
import com.huatu.handheld_huatu.business.arena.downloadpaper.listener.ArenaDownloadListener;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.arena.utils.BottomDialog;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 2019.1.9 新的行测做题 Title Fragment，
 * <p>
 * 负责功能
 * 调起草稿纸，试卷下载，收藏，答题卡，
 * 更多设置（日夜间，字体大小，纠错）
 * 时间，模考倒计时五分钟开始作答 && 模考倒计时自动交卷（以后会有正计时）
 * 五分钟不作答，自定暂停功能
 * <p>
 * 注意：这里数据不要onSaveInstanceState onRestoreState，
 * 因为从onRestoreState取出来的东西和ArenaExamActivityNew中onRestoreState取出来的不是一个对象，所以就有问题。
 * 所以只要ArenaExamActivityNew存储在onSaveInstanceState中，然后在set进来，这样就会保持数据一致。
 */
public class ArenaExamTitleFragmentNew extends BaseFragment implements NightSwitchInterface, TextSizeSwitchInterface {

    public static final int UPDATE_FIVE_TIME = 10010;           // 模考大赛更新，五分钟倒计时

    public static final int CARD_NOT_RECORD_BG_TIME = -5;       // 如果是去答题卡页面，就不记录backgroundTime，因为用的是同一个答题卡对象，答题卡页面也在倒计时或正计时

    @BindView(R.id.iv_back)
    ImageView ivBack;                   // 返回
    @BindView(R.id.tv_switch_style)
    TextView tvSwitch;                  // pad切换分屏还是通屏模式
    @BindView(R.id.iv_draft)
    ImageView ivDraft;                  // 草稿纸
    @BindView(R.id.iv_down)
    ImageView ivDown;                   // 下载按钮
    @BindView(R.id.iv_collect)
    ImageView ivCollect;                // 收藏
    @BindView(R.id.iv_answer_card)
    ImageView ivAnswerCard;             // 答题卡
    @BindView(R.id.iv_del)
    ImageView ivDel;                    // 错题背题，删除
    @BindView(R.id.ll_time)
    LinearLayout llTime;                // 时间layout
    @BindView(R.id.tv_time)
    TextView tvTime;                    // 时间
    @BindView(R.id.iv_more)
    ImageView ivMore;                   // 设置

    private boolean startFiveTimer;                                     // 考试开始五分钟开始计时了
    private Subscription beforeStartExamTimeSubscription;               // 考试开始前倒计时
    private boolean startTimer;                                         // 考试中开始计时了
    private Subscription startExamTimeSubscription;                     // 考试中计时

    private RealExamBeans.RealExamBean realExamBean;                    // 试卷
    private ArenaExamQuestionBean questionBean;                         // 当前问题

    private boolean hasNewPaper;                                        // 下载的试卷是否有新试卷

    private BottomDialog bottomSettingDialog;                           // 设置弹窗

    private AtomicInteger unDoTime = new AtomicInteger(0);    // 未操作时间，为了记录五分钟

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_arena_exam_title_fragment_new;
    }

    @Override
    protected void onInitView() {
        ArenaViewSettingManager.getInstance().registerNightSwitcher(this);
        ArenaViewSettingManager.getInstance().registerTextSizeSwitcher(this);
        nightSwitch();
        compositeSubscription = new CompositeSubscription();

        requestType = args.getInt("request_from", -1);
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {                // 错题背题才显示删除按钮
            ivDel.setVisibility(View.VISIBLE);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE) {                // 收藏解析、单体解析、不显示答题卡
            ivAnswerCard.setVisibility(View.GONE);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_PREVIEW) {               // 老师预览，隐藏答题卡按钮、收藏按钮
            ivCollect.setVisibility(View.GONE);
        }

        if (ArenaExamActivityPad.doStyle == 0) {
            tvSwitch.setText("切换分屏");
        } else {
            tvSwitch.setText("切换通屏");
        }
    }

    @Override
    protected void onLoadData() {

    }

    @OnClick(R.id.iv_back)
    public void onBackClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        mActivity.onBackPressed();
    }

    @OnClick(R.id.tv_switch_style)
    public void switchStyle() {
        if (ArenaExamActivityPad.doStyle == 1) {
            tvSwitch.setText("切换分屏");
        } else {
            tvSwitch.setText("切换通屏");
        }
        mActivity.onFragmentClickEvent(R.id.tv_switch_style, null);
    }

    @OnClick(R.id.iv_draft)
    public void onDraftClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (realExamBean == null) return;
        mActivity.onFragmentClickEvent(R.id.iv_draft, null);
        reSetUnDo();
    }

    @OnClick(R.id.iv_down)
    public void onDownClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (realExamBean == null) return;
        final int paperId = realExamBean.paper.id;
        if (paperId <= 0) {
            ToastUtils.showShort("试卷Id错误");
            return;
        }

        if (ArenaPaperLocalFileManager.newInstance().isDownLoadedPaper(paperId)) {
            if (hasNewPaper && NetUtil.isConnected()) {
                CustomConfirmDialog exitConfirmDialog = DialogUtils.createDialog(mActivity, "", "该试卷内容有新修正，可以重新下载最新试卷");
                exitConfirmDialog.setPositiveButton("重新下载", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, paperId, realExamBean.paper.name, SignUpTypeDataCache.getInstance().getCurSubject(), new ArenaDownloadListener() {
                            @Override
                            public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hasNewPaper = false;
                                        ArenaPaperLocalFileManager.newInstance().openPaper(mActivity, paperId);
                                    }
                                });
                            }
                        });
                    }
                });
                exitConfirmDialog.setNegativeButton("继续打开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArenaPaperLocalFileManager.newInstance().openPaper(mActivity, paperId);
                    }
                });
                exitConfirmDialog.setCancelBtnVisibility(true);
                exitConfirmDialog.show();
            } else {
                ArenaPaperLocalFileManager.newInstance().openPaper(mActivity, paperId);
            }
        } else {
            if (!NetUtil.isConnected()) {
                ToastUtils.showShort("无网络连接");
                return;
            }
            ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, paperId, realExamBean.paper.name, SignUpTypeDataCache.getInstance().getCurSubject(), new ArenaDownloadListener() {
                @Override
                public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                    if (!Method.isActivityFinished(mActivity)) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ivDown != null) {
                                    ivDown.setImageResource(R.mipmap.download_essay);
                                    ToastUtil.showToast("下载完成", 2500);
                                }
                            }
                        });
                    }
                }
            });
        }
        reSetUnDo();
    }

    @OnClick(R.id.iv_collect)
    public void onCollectClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (questionBean == null) return;
        if (questionBean.isFaverated) {
            cancelCollection(questionBean.id);
        } else {
            collectQuestion(questionBean.id);
        }
        reSetUnDo();
    }

    @OnClick(R.id.iv_answer_card)
    public void onAnswerCardClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (realExamBean == null) return;
        if (!ArenaDataCache.getInstance().isEnableExam()) {
            return;
        }
        ArenaExamMessageEvent event = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION);
        event.extraBundle = new Bundle();
        event.tag = "ShowAnswerCardView";
        EventBus.getDefault().post(event);
    }

    @OnClick(R.id.iv_del)
    public void onDelClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (realExamBean == null) return;
        deleteWrongQuestion();
        reSetUnDo();
    }

    @OnClick(R.id.ll_time)
    public void onTimeClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (realExamBean == null) return;
        // 休息一下
        // 如果不是模考也不是小模考，就可以休息，跳转休息页面
        if (ArenaHelper.isCanRest(requestType)) {
            takeRest(0);
        }
        reSetUnDo();
    }

    @OnClick(R.id.iv_more)
    public void onMoreClick() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (realExamBean == null) return;
        showSettingView();
        reSetUnDo();
    }

    /**
     * 把整个试卷set进来，以便进行操作
     */
    public void setExamBeans(RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null) return;
        this.realExamBean = realExamBean;

        // 试卷下载判断
        setDownView();

        // 计时系统启动
        initTime();

        setTimeText();
    }

    /**
     * 初始化下载按钮
     */
    private void setDownView() {
        final int paperId = realExamBean.paper.id;
        if (ArenaHelper.isPaperSCType(realExamBean)) {
            ivDown.setVisibility(View.VISIBLE);
            if (ArenaPaperLocalFileManager.newInstance().isDownLoadedPaper(paperId)) {
                ivDown.setImageResource(R.mipmap.download_essay);
                if (!NetUtil.isConnected()) {
                    return;
                }
                ServiceProvider.getArenaPaperInfoList(compositeSubscription, String.valueOf(paperId), new NetResponse() {
                    @Override
                    public void onListSuccess(BaseListResponseModel model) {
                        if (model.data.size() > 0) {
                            List<ArenaPaperInfoNet> paperInfoNets = model.data;
                            ArenaPaperInfoNet arenaPaperInfoNet = paperInfoNets.get(0);
                            if (arenaPaperInfoNet.getId() == paperId && ArenaPaperLocalFileManager.newInstance().hasNewPaper(paperId, arenaPaperInfoNet.getGmtModify())) {
                                hasNewPaper = true;
                            }
                        }
                    }
                });
            } else {
                ivDown.setImageResource(R.mipmap.download_paper_icon);
            }
        } else {
            ivDown.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化计时系统
     * (realExamBean.type >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL)，解析不需要计时
     * 模考大赛：是否到了做题时间，五分钟开始倒计时开始做题，不可作答。然后倒计时结束后，开始做题倒计时。然后做题倒计时后自动交卷。
     * 真题演练、往期模考，进来就倒计时做题，到时间交卷
     * 其他做题，只有正计时，没有自动交卷
     */
    private void initTime() {
        if (realExamBean.type >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {            // 解析、背题不需要计时
            llTime.setVisibility(View.GONE);
            return;
        }
        llTime.setVisibility(View.VISIBLE);
        if (ArenaHelper.isPaperSCType(realExamBean)
                && (realExamBean.paper.startTime - (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime)) / 1000 > 0) {        // 是模考，还没到考试开始时间
            startFiveTip();
        } else {                                                                            // 开始考试
            startTime();
        }
    }

    /**
     * 停止时间
     */
    public void finishTimer() {
        startFiveTimer = false;
        startTimer = false;
        if (beforeStartExamTimeSubscription != null && !beforeStartExamTimeSubscription.isUnsubscribed()) {
            beforeStartExamTimeSubscription.unsubscribe();
        }
        if (startExamTimeSubscription != null && !startExamTimeSubscription.isUnsubscribed()) {
            startExamTimeSubscription.unsubscribe();
        }
        // 模考大赛开始后 || 小模考
        // 得记录后台时间（如果是去答题卡，就不用记录，因为答题卡会对同一个对象进行及时）
        if ((ArenaHelper.isRecordBackgroundTime(realExamBean)) && realExamBean.backGroundTime != CARD_NOT_RECORD_BG_TIME) {
            realExamBean.backGroundTime = System.currentTimeMillis();               // 暂停的时候记录当前时间
        }
    }

    /**
     * 开始考试前倒计时
     */
    private void startFiveTip() {
        if (startFiveTimer) return;
        startFiveTimer = true;
        // 没到考试时间
        CommonUtils.showToast("正在等待开始考试");
        ArenaDataCache.getInstance().setEnableExam(false);
        beforeStartExamTimeSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        updateWaitExamTime();
                    }
                });
    }

    /**
     * 考试开始前更新时间
     */
    private void updateWaitExamTime() {
        long remainTime = (realExamBean.paper.startTime - (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime)) / 1000;
        if (remainTime >= 0) {           // 还没到开始时间
            Bundle bundle = new Bundle();
            bundle.putLong("remainTime", remainTime);
            mActivity.onFragmentClickEvent(UPDATE_FIVE_TIME, bundle);
        }
        if (remainTime <= 0) {
            CommonUtils.showToast("开始考试");
            startFiveTimer = false;
            if (beforeStartExamTimeSubscription != null && !beforeStartExamTimeSubscription.isUnsubscribed()) {
                beforeStartExamTimeSubscription.unsubscribe();
            }
            initTime();
        }
    }

    /**
     * 开始计时
     */
    private void startTime() {
        if (realExamBean == null || startTimer) return;

        // 再次通知Activity隐藏考试考试倒计时。因为如果开始考试倒计时中，离开了页面，再回来，就直接开始做题倒计时了，就缺少了隐藏开始考试倒计时。
        if (ArenaHelper.isPaperSCType(realExamBean)
                && (System.currentTimeMillis() - (realExamBean.paper.startTime + SignUpTypeDataCache.getInstance().dTime)) / 1000 >= 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("remainTime", 0);
            mActivity.onFragmentClickEvent(UPDATE_FIVE_TIME, bundle);
        }

        startTimer = true;

        ArenaDataCache.getInstance().setEnableExam(true);

        // 模考大赛单独计算剩余时间
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {
            int remain = (int) ((realExamBean.endTime - (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime)) / 1000);
            int all = (int) ((realExamBean.endTime - realExamBean.startTime) / 1000);

            int min = remain < all ? remain : all;

            realExamBean.remainingTime = min > 0 ? min : 0;
        } else if (realExamBean.backGroundTime > 0) {
            if (ArenaHelper.isCountDown(realExamBean.type)) {       // 是倒计时
                realExamBean.remainingTime -= (System.currentTimeMillis() - realExamBean.backGroundTime) / 1000;
            } else {
                realExamBean.expendTime += (System.currentTimeMillis() - realExamBean.backGroundTime) / 1000;
            }
        }
        realExamBean.backGroundTime = 0;

        setTimeText();

        startExamTimeSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (questionBean != null)           // 本题用时增加
                            questionBean.usedTime++;
                        if (ArenaHelper.isCountDown(realExamBean.type)) {                                   // 是倒计时
                            realExamBean.remainingTime--;
                            if (realExamBean.remainingTime == 10 * 60 && ArenaHelper.isPaperSCType(realExamBean)) {       // 模考倒计时十分钟
                                DialogUtils.createTipsDialog(mActivity, "考试还剩10分钟,\n结束时将自动交卷!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });
                            }
                            if (realExamBean.remainingTime <= 0) {                                              // 时间到，交卷
                                CommonUtils.showToast("时间到，交卷啦!");
                                ArenaExamMessageEvent event = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION);
                                event.extraBundle = new Bundle();
                                event.tag = "ShowAnswerCardView";
                                event.extraBundle.putBoolean("autoSubmit", true);
                                EventBus.getDefault().post(event);
                                finishTimer();
                            }
                        } else {
                            realExamBean.expendTime++;
                        }
                        setTimeText();
                        // 检查是否五分钟没有操作，跳暂停页
                        checkUnDoFive();
                    }
                });
    }

    /**
     * 检查是否五分钟没有操作
     */
    private void checkUnDoFive() {
        if (ArenaHelper.isCanRest(requestType)) {
            int unDo = unDoTime.incrementAndGet();
            if (unDo >= 60 * 5) {        // 如果五分钟没有动
                // 跳五分钟没做页面（休息页面）
                takeRest(1);
            }
        }
    }

    // 初始化未做时间
    private void reSetUnDo() {
        unDoTime.set(0);
    }

    /**
     * 设置显示时间
     */
    private void setTimeText() {
        int time;
        if (ArenaHelper.isCountDown(realExamBean.type)) {   // 是倒计时
            time = realExamBean.remainingTime;                  // 剩余时间
        } else {
            time = realExamBean.expendTime;                     // 花费的时间
        }
        tvTime.setText(TimeUtils.getSecond22MinTime(time));
    }

    /**
     * Activity把对应的问题set过来，以便进行操作
     */
    public void setQuestionBean(ArenaExamQuestionBean questionBean) {
        if (questionBean == null) return;
        this.questionBean = questionBean;
        setCollectIcon();
        reSetUnDo();

//        if (CommonUtils.isPad(mActivity) && questionBean.isMaterial) {
//            tvSwitch.setVisibility(View.VISIBLE);
//        } else {
//            tvSwitch.setVisibility(View.GONE);
//        }
    }

    // 收藏问题
    public void collectQuestion(int questionId) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            return;
        }
        if (questionId == 0)
            return;
        mActivity.showProgress();
        ServiceProvider.addFavorPractice(compositeSubscription, questionId, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                if (e instanceof ApiException) {
                    String errorMsg = ((ApiException) e).getErrorMsg();
                    CommonUtils.showToast(errorMsg);
                } else {
                    CommonUtils.showToast("收藏失败");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                questionBean.isFaverated = true;
                setCollectIcon();
                CommonUtils.showToast("收藏成功");
            }
        });
    }

    // 取消收藏问题
    public void cancelCollection(int questionId) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            return;
        }
        if (questionId == 0)
            return;
        mActivity.showProgress();
        ServiceProvider.deleteFavorPractice(compositeSubscription, questionId, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                if (e instanceof ApiException) {
                    String errorMsg = ((ApiException) e).getErrorMsg();
                    CommonUtils.showToast(errorMsg);
                } else {
                    CommonUtils.showToast("取消收藏失败");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                questionBean.isFaverated = false;
                setCollectIcon();
                CommonUtils.showToast("取消收藏成功");
            }
        });
    }

    // 错题背题、删除问题
    private void deleteWrongQuestion() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            return;
        }
        if (realExamBean.paper.questionBeanList.contains(questionBean)) {
            mActivity.showProgress();
            ServiceProvider.deleteWrongExerciseV4(compositeSubscription, questionBean.id, new NetResponse() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    mActivity.hideProgress();
                    if (e instanceof ApiException) {
                        ToastUtils.showShort(((ApiException) e).getErrorMsg());
                    } else {
                        ToastUtils.showShort("删除失败");
                    }
                }

                @Override
                public void onSuccess(BaseResponseModel model) {
                    super.onSuccess(model);
                    mActivity.hideProgress();
                    ArenaDataCache.getInstance().isDeletePaper = true;
                    ToastUtils.showShort("此题已经从错题本删除");
                }
            });
        }
    }

    // 设置的各种View 日夜间，字体，纠错
    private LinearLayout llAll;
    private LinearLayout llLight;
    private LinearLayout llNight;
    private TextView tvSmall;
    private TextView tvMiddle;
    private TextView tvBig;
    private View viewDivider;
    private View tvCorrect;

    /**
     * 设置-显示设置BottomView
     */
    private void showSettingView() {
        if (bottomSettingDialog == null) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.arena_setting_new_layout, null);
            bottomSettingDialog = new BottomDialog(mActivity, view, true, true);

            llAll = view.findViewById(R.id.ll_all);
            llLight = view.findViewById(R.id.ll_light);
            llNight = view.findViewById(R.id.ll_night);

            tvSmall = view.findViewById(R.id.tv_small);
            tvMiddle = view.findViewById(R.id.tv_middle);
            tvBig = view.findViewById(R.id.tv_big);

            viewDivider = view.findViewById(R.id.view_divider);

            tvCorrect = view.findViewById(R.id.tv_correct);

            llLight.setOnClickListener(settingListener);
            llNight.setOnClickListener(settingListener);

            tvSmall.setOnClickListener(settingListener);
            tvMiddle.setOnClickListener(settingListener);
            tvBig.setOnClickListener(settingListener);

            tvCorrect.setOnClickListener(settingListener);
        }

        updateSettingView();

        bottomSettingDialog.show();
    }

    // 设置-各种按钮监听
    private View.OnClickListener settingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_light:
                    if (SpUtils.getDayNightMode() != 0) {
                        SpUtils.setDayNightMode(0);
                        ArenaViewSettingManager.getInstance().nightSwitch();
                    }
                    break;
                case R.id.ll_night:
                    if (SpUtils.getDayNightMode() != 1) {
                        SpUtils.setDayNightMode(1);
                        ArenaViewSettingManager.getInstance().nightSwitch();
                    }
                    break;
                case R.id.tv_small:
                    if (SpUtils.getFontSizeMode() != 1) {
                        SpUtils.setFontSizeMode(1);
                        ArenaViewSettingManager.getInstance().sizeSwitch();
                    }
                    break;
                case R.id.tv_middle:
                    if (SpUtils.getFontSizeMode() != 0) {
                        SpUtils.setFontSizeMode(0);
                        ArenaViewSettingManager.getInstance().sizeSwitch();
                    }
                    break;
                case R.id.tv_big:
                    if (SpUtils.getFontSizeMode() != 2) {
                        SpUtils.setFontSizeMode(2);
                        ArenaViewSettingManager.getInstance().sizeSwitch();
                    }
                    break;
                case R.id.tv_correct:
                    if (questionBean != null) {
                        Bundle correctBundle = new Bundle();
                        int practiseId = questionBean.id;
                        correctBundle.putInt("practice_id", practiseId);
                        BaseArenaCorrectActivity.newInstance(mActivity, ArenaQuestionCorrectFragment.class.getName(), correctBundle);
                        bottomSettingDialog.dismiss();
                    }
                    break;
            }
        }
    };

    /**
     * 设置-更新设置View
     */
    private void updateSettingView() {
        if (llAll == null) return;
        // 0、日间 1、夜间
        int modeNight = SpUtils.getDayNightMode();
        // 1、小号 0、中号 2、大号
        int modeFont = SpUtils.getFontSizeMode();
        int sizeBgNomal, sizeBgSelect;
        if (modeNight == 0) {       // 日间
            llAll.setBackgroundResource(R.drawable.arena_setting_bottom_bg);
            llLight.setBackgroundResource(R.drawable.arena_setting_light_bg_yes);
            llNight.setBackgroundResource(R.drawable.arena_setting_night_bg_no);
            viewDivider.setBackgroundColor(Color.parseColor("#C4C4C4"));
            sizeBgNomal = R.drawable.arena_setting_text_size_bg_no;
            sizeBgSelect = R.drawable.arena_setting_text_size_bg_yes;
        } else {                    // 夜间
            llAll.setBackgroundResource(R.drawable.arena_setting_bottom_bg_night);
            llLight.setBackgroundResource(R.drawable.arena_setting_light_bg_no);
            llNight.setBackgroundResource(R.drawable.arena_setting_night_bg_yes);
            viewDivider.setBackgroundColor(Color.parseColor("#000000"));
            sizeBgNomal = R.drawable.arena_setting_text_size_bg_no_night;
            sizeBgSelect = R.drawable.arena_setting_text_size_bg_yes_night;
        }
        tvSmall.setBackgroundResource(sizeBgNomal);
        tvMiddle.setBackgroundResource(sizeBgNomal);
        tvBig.setBackgroundResource(sizeBgNomal);
        if (modeFont == 1) {
            tvSmall.setBackgroundResource(sizeBgSelect);
        } else if (modeFont == 0) {
            tvMiddle.setBackgroundResource(sizeBgSelect);
        } else {
            tvBig.setBackgroundResource(sizeBgSelect);
        }
    }

    /**
     * 设置收藏按钮样式
     */
    private void setCollectIcon() {
        if (questionBean == null) return;
        int dayNightMode = SpUtils.getDayNightMode();
        if (dayNightMode == 0) {         // 日间
            ivCollect.setImageResource(questionBean.isFaverated ? R.mipmap.nav_icon_collect_yes : R.mipmap.nav_icon_collect);
        } else {
            ivCollect.setImageResource(questionBean.isFaverated ? R.mipmap.nav_icon_collect_yes_night : R.mipmap.nav_icon_collect_night);
        }
    }

    /**
     * 休息一下
     * type 0、休息页面  1、五分钟没做提示页面
     */
    public void takeRest(int type) {
        Intent intent = new Intent(mActivity, ArenaRestActivity.class);
        intent.putExtra("type", type);
        if (type == 0) {
            int count = realExamBean.paper.questionBeanList.size();
            int doneCount = ArenaHelper.getDoneCount(realExamBean);
            // 显示休息一会儿吧
            intent.putExtra("count", count);
            intent.putExtra("doneCount", doneCount);
        }
        startActivity(intent);
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void nightSwitch() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            rootView.setBackgroundResource(R.color.arena_top_title_bg);
            ivBack.setImageResource(R.mipmap.icon_back_black_new);
            ivMore.setImageResource(R.mipmap.nav_icon_more_black);
        } else {
            rootView.setBackgroundResource(R.color.arena_top_title_bg_night);
            ivBack.setImageResource(R.mipmap.icon_back_white_new_night);
            ivMore.setImageResource(R.mipmap.nav_icon_more_white);
        }
        setCollectIcon();
        updateSettingView();
    }

    @Override
    public void sizeSwitch() {
        updateSettingView();
    }

    @Override
    public void onStop() {
        super.onStop();
        finishTimer();
        reSetUnDo();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (realExamBean == null) return;
        initTime();
    }

    @Override
    public boolean onBackPressed() {
        if (bottomSettingDialog != null && bottomSettingDialog.isShowing()) {
            bottomSettingDialog.dismiss();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ArenaViewSettingManager.getInstance().unRegisterNightSwitcher(this);
        ArenaViewSettingManager.getInstance().unRegisterTextSizeSwitcher(this);
        if (beforeStartExamTimeSubscription != null && !beforeStartExamTimeSubscription.isUnsubscribed()) {
            beforeStartExamTimeSubscription.unsubscribe();
        }
    }
}
