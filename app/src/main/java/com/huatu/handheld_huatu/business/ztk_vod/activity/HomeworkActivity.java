package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.essay.activity.HomeworkSingleListActivity;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.HomeworkAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HomeworkBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HomeworkData;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HomeworkRecombinationBean;
import com.huatu.handheld_huatu.event.SectionalExaminationEvent;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomSupDialog;
import com.huatu.library.PullToRefreshBase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * 课后作业页
 */
public class HomeworkActivity extends SimpleBaseActivity implements HomeworkAdapter.ItemClickListener {

    //接口传的固定参数
    public final String type = "courseWork";
    //进界面传的参数，未读数量
    public static final String UNREADNNCOUNT = "unreadCount";

    @BindView(R.id.mCommonErrorView)
    CommonErrorView mCommonErrorView;
    @BindView(R.id.mTvAllRead)
    TextView mTvAllRead;
    @BindView(R.id.iv_tab)
    ImageView iv_tab;
    @BindView(R.id.tv_civil_service)
    TextView tv_civil_service;
    @BindView(R.id.tv_civil_service_num)
    TextView tv_civil_service_num;
    @BindView(R.id.tv_essay)
    TextView tv_essay;
    @BindView(R.id.tv_essay_num)
    TextView tv_essay_num;

    @BindView(R.id.mPullRefreshRecyclerView)
    PullRefreshRecyclerView mPullRefreshRecyclerView;

    private HomeworkAdapter homeworkAdapter;

    //分页， 每页的总数据
    private List<HomeworkRecombinationBean> homeworkRecombinationBeans;

    //每页默认多少条数据，传给PullRefreshRecyclerView控件
    private int pageSize = 20;
    // 页码,接口请求的参数
    private int page = 1;

    //科目1是行测 2是申论
    private int category = 1;
    private List<TextView> listTV = new ArrayList<>();
    int currIndex;
    private int one;                        // 两个相邻页面的偏移量
    private int offset;                     // 图片移动的偏移量
    private int currentState = 0;
    //未读行测消息的数量
    private int civilUnRead;
    //未读申论消息的数量
    private int essayUnRead;

    /**
     * 用于刷新界面，答题数量
     *
     * @param sectionalExaminationEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(SectionalExaminationEvent sectionalExaminationEvent) {

        if (sectionalExaminationEvent == null) {
            return;
        }

        if (sectionalExaminationEvent.mBundle != null) {

            //当前列表的position
            int position = sectionalExaminationEvent.mBundle.getInt("position", -1);
            //答过多少题的数量
            int answerCount = sectionalExaminationEvent.mBundle.getInt("answerCount", -1);

            if (position == -1) {
                return;
            }

            if (answerCount == -1) {
                if (homeworkRecombinationBeans != null && homeworkRecombinationBeans.size() >= position) {
                    homeworkRecombinationBeans.remove(position);
                    if (homeworkRecombinationBeans.get(position - 1).child != null &&
                            homeworkRecombinationBeans.get(position - 1).child.size() == 1) {
                        homeworkRecombinationBeans.remove(position - 1);
                    }
                    setAdapter(homeworkRecombinationBeans);
                }
            } else {

                //剩余答题的数量
                int ucount = homeworkRecombinationBeans.get(position).qcount - answerCount;
                homeworkRecombinationBeans.get(position).ucount = ucount;

                setAdapter(homeworkRecombinationBeans);

            }
        }
    }

    /**
     * 刷新申论的列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEvent(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave                 // 保存试卷
                || event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
            // 提交成功
            page=1;
            if (homeworkRecombinationBeans != null) homeworkRecombinationBeans.clear();
            requestData();
        }
        return true;
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_homework;
    }

    @Override
    protected void onInitView() {

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

//        unreadCount = getIntent().getIntExtra(UNREADNNCOUNT, 0);

        //当出现加载失败时，点击view，会走此方法
        mCommonErrorView.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });

        initRecyclerView();
        requestData();

//        mTvAllReadColorChange();

        initTVList();
        this.getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        initTab();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            HomeworkActivity.this.getWindow()
                                    .getDecorView()
                                    .getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            HomeworkActivity.this.getWindow()
                                    .getDecorView()
                                    .getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });
        startAnimation(0);
    }

    private void initTab() {
        iv_tab.setVisibility(View.VISIBLE);
    }

    private void initTVList() {
        listTV.add(tv_civil_service);
        listTV.add(tv_essay);
    }

    private void initRecyclerView() {
        homeworkAdapter = new HomeworkAdapter(this,  this);

        // 设置每页加载的条数，判断是否是最后一页
        mPullRefreshRecyclerView.getRefreshableView().setPagesize(pageSize);
        mPullRefreshRecyclerView.getRefreshableView().setLayoutManager(new LinearLayoutManager(this));

        // 加载过程中是否可以滑动
        mPullRefreshRecyclerView.setPullToRefreshOverScrollEnabled(false);
        mPullRefreshRecyclerView.getRefreshableView().setRecyclerAdapter(homeworkAdapter);

        // 下拉刷新的回调
        mPullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                //回复页码数为1
                page = 1;
                //刷新的时候，清空次数据
                homeworkRecombinationBeans.clear();
                requestData();
            }
        });
        // 自动加载更多的回调
        mPullRefreshRecyclerView.getRefreshableView().setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                requestData();
            }
        });

    }

    /**
     * "全部已读" 颜色修改
     */
    public void mTvAllReadColorChange() {
        if (civilUnRead <= 0 && essayUnRead <= 0) {
            mTvAllRead.setTextColor(getResources().getColor(R.color.arena_exam_subject_name_text));
        } else {
            mTvAllRead.setTextColor(getResources().getColor(R.color.blackF4));
        }

    }

    @OnClick({R.id.mViewBack, R.id.mTvAllRead, R.id.tv_civil_service, R.id.tv_essay})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.mViewBack://返回键
                finish();
                break;

            case R.id.mTvAllRead://全部已读
                if (civilUnRead <= 0 && essayUnRead <= 0) {
                    ToastUtil.showToast("暂无可清除的消息");
                } else {
                    getAllRead(type);
                }
                break;
            case R.id.tv_civil_service://行测
                if (category != 1) {
                    category = 1;
                    page = 1;
                    startAnimation(0);
                    tv_civil_service.setTextColor(ContextCompat.getColor(this, R.color.redF3));
                    tv_essay.setTextColor(ContextCompat.getColor(this, R.color.stage_remark_text));
                    if (homeworkRecombinationBeans != null) homeworkRecombinationBeans.clear();
                    requestData();
                }

                break;
            case R.id.tv_essay://申论
                if (category != 2) {
                    category = 2;
                    page = 1;
                    startAnimation(1);
                    tv_essay.setTextColor(ContextCompat.getColor(this, R.color.redF3));
                    tv_civil_service.setTextColor(ContextCompat.getColor(this, R.color.stage_remark_text));
                    if (homeworkRecombinationBeans != null) homeworkRecombinationBeans.clear();
//                    initRecyclerView();
                    requestData();
                }
                break;

            case R.id.mCommonErrorView:
                requestData();
                break;
            default:
                break;
        }
    }

    /**
     * @param position 标题行测，申论下的红线
     */

    public void startAnimation(int position) {
        int scrollX;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_tab.getLayoutParams();
        params.width = DisplayUtil.dp2px(44);
        iv_tab.setLayoutParams(params);
        DisplayMetrics dm = new DisplayMetrics();
        HomeworkActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        one = screenW / 2;
        offset = (screenW / 2 - DisplayUtil.dp2px(44)) / 2;
        scrollX = position * one + offset;

        Animation animation = new TranslateAnimation(currentState, scrollX, 0, 0);
        currentState = scrollX;
        currIndex = position;
        animation.setFillAfter(true);       // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
        animation.setDuration(200);         // 动画持续时间0.2秒
        iv_tab.startAnimation(animation);   // 是用ImageView来显示动画的
        setColor();
        listTV.get(position).setSelected(true);
    }

    public void setColor() {
        for (int i = 0; i < listTV.size(); i++) {
            listTV.get(i).setSelected(false);
        }
    }

    /**
     * 数据重组，用于展示折叠数据
     * 把多层合并成1层数据。
     *
     * @param homeworkBeans
     */
    private void dataRecombination(List<HomeworkBean> homeworkBeans) {

        if (homeworkBeans != null) {

            List<HomeworkRecombinationBean> homeworkRecombinationBeans = new ArrayList<>();

            for (HomeworkBean homeworkBean : homeworkBeans) {

                List<HomeworkRecombinationBean> childHomeworkRecombinationBean = new ArrayList<>();

                if (homeworkBean.wareInfoList != null) {

                    int i = 1;

                    for (HomeworkBean.WareInfoListBean wareInfoListBean : homeworkBean.wareInfoList) {
                        //跳转申论做题页或者多题列表的时候需要courseId 所以将homeworkBean也传入
                        childHomeworkRecombinationBean.add(dataRecombinationPeriodTestListBean(wareInfoListBean, i, homeworkBean));

                        i++;
                    }

                }

                homeworkRecombinationBeans.add(dataRecombinationHomeworkBean(homeworkBean, childHomeworkRecombinationBean));
                homeworkRecombinationBeans.addAll(childHomeworkRecombinationBean);

            }

            showList(homeworkRecombinationBeans);
        } else {

            showList(null);

        }

    }

    /**
     * 重组 数据
     *
     * @param homeworkBean
     * @param homeworkRecombinationBeans
     * @return
     */
    public HomeworkRecombinationBean dataRecombinationHomeworkBean(HomeworkBean homeworkBean, List<HomeworkRecombinationBean> homeworkRecombinationBeans) {

        HomeworkRecombinationBean homeworkRecombinationBean = new HomeworkRecombinationBean();

        homeworkRecombinationBean.courseTitle = homeworkBean.courseTitle;
        homeworkRecombinationBean.courseId = homeworkBean.courseId;
        homeworkRecombinationBean.undoCount = homeworkBean.undoCount;
        homeworkRecombinationBean.type = HomeworkRecombinationBean.CLASS_NAME;
        homeworkRecombinationBean.child = homeworkRecombinationBeans;

        return homeworkRecombinationBean;

    }

    /**
     * 重组 数据
     *
     * @param wareInfoListBean
     * @param homeworkBean
     * @return
     */
    public HomeworkRecombinationBean dataRecombinationPeriodTestListBean(HomeworkBean.WareInfoListBean wareInfoListBean, int index, HomeworkBean homeworkBean) {

        HomeworkRecombinationBean homeworkRecombinationBean = new HomeworkRecombinationBean();

        homeworkRecombinationBean.index = index;
        homeworkRecombinationBean.courseWareTitle = wareInfoListBean.courseWareTitle;
        homeworkRecombinationBean.questionTitle = wareInfoListBean.questionTitle;
        homeworkRecombinationBean.courseId = homeworkBean.courseId;
        homeworkRecombinationBean.questionType = wareInfoListBean.questionType;
        homeworkRecombinationBean.buildType = wareInfoListBean.buildType;
        homeworkRecombinationBean.questionIds = wareInfoListBean.questionIds;
        homeworkRecombinationBean.courseWareId = wareInfoListBean.courseWareId;
        homeworkRecombinationBean.videoType = wareInfoListBean.videoType;
        homeworkRecombinationBean.videoLength = wareInfoListBean.videoLength;
        homeworkRecombinationBean.serialNumber = wareInfoListBean.serialNumber;
        homeworkRecombinationBean.answerCardId = wareInfoListBean.answerCardId;
        homeworkRecombinationBean.syllabusId = wareInfoListBean.syllabusId;
        homeworkRecombinationBean.isAlert = wareInfoListBean.isAlert;
        homeworkRecombinationBean.type = HomeworkRecombinationBean.STUDY_DETAILS;

        if (wareInfoListBean.answerCardInfo != null) {
            homeworkRecombinationBean.correctMemo = wareInfoListBean.answerCardInfo.correctMemo;
            homeworkRecombinationBean.status = wareInfoListBean.answerCardInfo.status;
            homeworkRecombinationBean.wcount = wareInfoListBean.answerCardInfo.wcount;
            homeworkRecombinationBean.ucount = wareInfoListBean.answerCardInfo.ucount;
            homeworkRecombinationBean.rcount = wareInfoListBean.answerCardInfo.rcount;
            homeworkRecombinationBean.qcount = wareInfoListBean.answerCardInfo.qcount;
            homeworkRecombinationBean.fcount = wareInfoListBean.answerCardInfo.fcount;
            homeworkRecombinationBean.categoryId = category;
            homeworkRecombinationBean.paperId = wareInfoListBean.answerCardInfo.paperId;
            homeworkRecombinationBean.questionBaseId = wareInfoListBean.answerCardInfo.questionBaseId;
            homeworkRecombinationBean.areaName = wareInfoListBean.answerCardInfo.areaName;

        }

        return homeworkRecombinationBean;
    }

    /**
     * 列表展示数据
     *
     * @param homeworkRecombinationBeans
     */
    private void showList(List<HomeworkRecombinationBean> homeworkRecombinationBeans) {

        if (homeworkRecombinationBeans == null || homeworkRecombinationBeans.size() == 0) {

            //当是第一页的时候，需要展示空数据
            if (page == 1) {
                showError(2);
            }

        } else {

            //页码数+1
            page++;

            if (this.homeworkRecombinationBeans == null) {
                this.homeworkRecombinationBeans = new ArrayList<>();
            }

            this.homeworkRecombinationBeans.addAll(homeworkRecombinationBeans);

            mCommonErrorView.setVisibility(View.GONE);
            mPullRefreshRecyclerView.setVisibility(View.VISIBLE);

            setAdapter(this.homeworkRecombinationBeans);
        }

    }

    /**
     * 更新列表
     *
     * @param homeworkRecombinationBeans
     */
    public void setAdapter(List<HomeworkRecombinationBean> homeworkRecombinationBeans) {

        if (homeworkRecombinationBeans == null || homeworkRecombinationBeans.size() == 0) {

            showError(2);

        } else {

            homeworkAdapter.setHomeworkBeans(homeworkRecombinationBeans);

        }
    }

    /**
     * 当点击全部已读，会更改本地缓存数据，变更为已读状态
     */
    public void setList_isAlert() {

        for (int i = 0; i < this.homeworkRecombinationBeans.size(); i++) {

            this.homeworkRecombinationBeans.get(i).isAlert = 0;

            setAdapter(this.homeworkRecombinationBeans);

        }

    }

    /**
     * 单条数据更改已读状态， 并且刷新界面
     *
     * @param position
     */
    public void setSingleData_isAlert(int position) {

        this.homeworkRecombinationBeans.get(position).isAlert = 0;

        setAdapter(this.homeworkRecombinationBeans);

    }

    private void showError(int type) {
        mCommonErrorView.setVisibility(View.VISIBLE);
        mPullRefreshRecyclerView.setVisibility(View.GONE);
        mCommonErrorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                mCommonErrorView.setErrorImage(R.drawable.no_server_service);
                mCommonErrorView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 无数据
                mCommonErrorView.setErrorImage(R.drawable.no_data_bg);
                mCommonErrorView.setErrorText("获取数据是失败，点击重试");
                break;
            case 2:                         // 无数据
                mCommonErrorView.setErrorImage(R.drawable.htzx_homework_empty_pic);
                mCommonErrorView.setErrorText("不拖沓是你本人吗，作业已完成");
                break;

            default:
                break;
        }
    }

    /**
     * 列表点击之后的响应回调
     *
     * @param view
     * @param homeworkRecombinationBean
     */
    @Override
    public void onClickCourse(View view, HomeworkRecombinationBean homeworkRecombinationBean, int position) {

        //如果此数据是未读状态，需要变更已读
        if (homeworkRecombinationBean.isAlert == 1) {
            //请求接口，单条已读接口调用
            getRead(category, homeworkRecombinationBean.syllabusId, position);
        }

        //跳转界面
        if (category == 1) {
            //行测
            Bundle bundle = new Bundle();
            bundle.putLong("practice_id", homeworkRecombinationBean.answerCardId);
            bundle.putBoolean("continue_answer", true);
            bundle.putInt("position", position);
            //  bundle.putBoolean("show_statistic",hasFinish);
            ArenaExamActivityNew.show(this, ArenaConstant.EXAM_ENTER_FORM_TYPE_COURSE_EXERICE, bundle);
            //   ArenaExamActivity.show(getActivity(),0, bundle);
        } else {
            // 申论
            if (homeworkRecombinationBean.qcount > 1) {
                //跳多道单题列表
                Bundle bundle = new Bundle();
                bundle.putInt("courseType", homeworkRecombinationBean.videoType);
                bundle.putLong("courseWareId", homeworkRecombinationBean.courseWareId);
                bundle.putLong("syllabusId", homeworkRecombinationBean.syllabusId);
                bundle.putLong("courseId", homeworkRecombinationBean.courseId);
                HomeworkSingleListActivity.show(this, bundle);
            } else {
                //跳做题页
                //questionType  0标准答案 1套题 2文章写作
                if (homeworkRecombinationBean.status == 5) {
                    showBeBacked(homeworkRecombinationBean);
                } else {
                    Bundle bundle=new Bundle();
                    bundle.putString("areaName", homeworkRecombinationBean.areaName);// 地区名称
                    bundle.putLong("answerId", homeworkRecombinationBean.answerCardId);//答题卡id
                    EssayRoute.goEssayHomeworkAnswer(HomeworkActivity.this,
                            homeworkRecombinationBean.buildType != 1,
                            null, homeworkRecombinationBean.questionBaseId, homeworkRecombinationBean.paperId,
                            homeworkRecombinationBean.videoType,
                            homeworkRecombinationBean.courseId, homeworkRecombinationBean.courseWareId,
                            homeworkRecombinationBean.syllabusId, bundle);
                }
            }

        }
    }


    /**
     * 请求列表数据
     */
    private void requestData() {

        showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getHomeworkLists(category, page, pageSize),
                new NetObjResponse<HomeworkData>() {
                    @Override
                    public void onSuccess(BaseResponseModel<HomeworkData> model) {
                        civilUnRead = model.data.civilUnRead;
                        essayUnRead = model.data.essayUnRead;
                        setUnreadNum();
                        mTvAllReadColorChange();
                        dataRecombination(model.data.list);
                        hideProgess();
                        if (mPullRefreshRecyclerView!=null){
                        mPullRefreshRecyclerView.onRefreshComplete();
                        // 隐藏加载动画
                        mPullRefreshRecyclerView.getRefreshableView().hideloading();
                        }

                    }

                    @Override
                    public void onError(String message, int type) {

                        hideProgess();
                        if (mPullRefreshRecyclerView!=null){
                        mPullRefreshRecyclerView.onRefreshComplete();
                        // 隐藏加载动画
                        mPullRefreshRecyclerView.getRefreshableView().hideloading();
                        }
                        showError(type);

                    }
                });

    }

    private void setUnreadNum() {
        if (tv_civil_service_num!=null&&civilUnRead > 0) {
            tv_civil_service_num.setVisibility(View.VISIBLE);
            tv_civil_service_num.setText(civilUnRead + "");
        } else {
         if (tv_civil_service_num!=null) tv_civil_service_num.setVisibility(View.GONE);
        }

        if (tv_essay_num!=null && essayUnRead > 0) {
            tv_essay_num.setVisibility(View.VISIBLE);
            tv_essay_num.setText(essayUnRead + "");
        } else {
            if (tv_essay_num!=null) tv_essay_num.setVisibility(View.GONE);
        }
    }

    /**
     * 全部已读，请求接口服务器
     *
     * @param type
     */
    private void getAllRead(String type) {

        showProgress();
        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().getAllRead(type),
                new NetObjResponse<String>() {
                    @Override
                    public void onSuccess(BaseResponseModel<String> model) {

                        //未读消息数量值0
//                        unreadCount = 0;
                        civilUnRead = 0;
                        essayUnRead = 0;
                        //更改全部已读颜色

                        setUnreadNum();
                        mTvAllReadColorChange();
                        PrefStore.putSettingInt("study_unread_message_change", 1);
                        //更改数据，刷新列表
                        setList_isAlert();
                        hideProgess();

                    }

                    @Override
                    public void onError(String message, int type) {

                        ToastUtil.showToast(message);
                        hideProgess();

                    }
                });

    }

    /**
     * 单条已读，请求接口服务器
     *
     * @param type
     * @param id
     * @param position 需要用次字段修改数据
     */
    private void getRead(int type, long id, final int position) {

        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().getHomeworkRead(type, id),
                new NetObjResponse<String>() {
                    @Override
                    public void onSuccess(BaseResponseModel<String> model) {

                        //修改条数据，刷新列表
                        setSingleData_isAlert(position);

                        //未读消息数量值 -1
//                        unreadCount -= 1;
                        if (category == 1) {
                            civilUnRead -= 1;
                        } else {
                            essayUnRead -= 1;
                        }
                        setUnreadNum();
                        PrefStore.putSettingInt("study_unread_message_change", 1);
                        //更改全部已读颜色
                        mTvAllReadColorChange();

                    }

                    @Override
                    public void onError(String message, int type) {

                        ToastUtil.showToast(message);

                    }
                });

    }

    private void showBeBacked(final HomeworkRecombinationBean mResult) {
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(HomeworkActivity.this);
        builder.setRLayout(R.layout.essay_back_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                TextView tv_tips = mView.findViewById(R.id.tv_tips);
                TextView tv_feedback = mView.findViewById(R.id.tv_feedback);
                tv_feedback.setText("知道了");
                if (!TextUtils.isEmpty(mResult.correctMemo)) {
                    tv_tips.setText(mResult.correctMemo);
                } else {
                    tv_tips.setText("本次人工批改申请因“卷面不整洁，无法批改”被驳回,如需继续申请批改请修改后重新提交。");

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
//                        FeedbackActivity.newInstance(HomeworkActivity.this);
                    }
                });
                mView.findViewById(R.id.tv_change_answer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Bundle extraBundle = new Bundle();
                        extraBundle.putString("areaName", mResult.areaName);      // 地区名称
                        extraBundle.putLong("answerId", mResult.answerCardId);      // 退回修改答案的时候，用答题卡id请求问题
                        extraBundle.putInt("bizStatus", mResult.status);     // 退回修改答案的时候，需要用批改状态
                        EssayRoute.goEssayHomeworkAnswer(HomeworkActivity.this,
                                mResult.buildType != 1,
                                null,
                                mResult.questionBaseId,
                                mResult.paperId,
                                mResult.videoType,
                                mResult.courseId,
                                mResult.courseWareId,
                                mResult.syllabusId,
                                extraBundle);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}