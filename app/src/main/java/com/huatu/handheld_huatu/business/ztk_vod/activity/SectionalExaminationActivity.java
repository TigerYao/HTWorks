package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
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
import com.huatu.handheld_huatu.business.matchsmall.activity.SmallMatchActivity;
import com.huatu.handheld_huatu.business.matchsmall.activity.StageReportActivity;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.SectionalExaminationAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.SectionExamRecombinBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.SectionalExaminationBean;

import com.huatu.handheld_huatu.event.SectionalExaminationEvent;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PeriodTestBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.StringUtils;

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
 * @date 2019/2/22
 * <p>
 * 阶段考试
 */
@Deprecated
public class SectionalExaminationActivity extends SimpleBaseActivity implements SectionalExaminationAdapter.ItemClickListener {

    //接口传的固定参数
    public final String type = "periodTest";
    //进界面传的参数，未读数量
    public static final String UNREADNNCOUNT = "unreadCount";

    @BindView(R.id.mCommonErrorView)
    CommonErrorView mCommonErrorView;
    @BindView(R.id.mRelativeLayoutEmptyData)
    RelativeLayout mRelativeLayoutEmptyData;
    @BindView(R.id.mTvAllRead)
    TextView mTvAllRead;

    @BindView(R.id.mPullRefreshRecyclerView)
    PullRefreshRecyclerView mPullRefreshRecyclerView;

    private SectionalExaminationAdapter sectionalExaminationAdapter;

    //每页默认多少条数据，传给PullRefreshRecyclerView控件
    private int pageSize = 20;
    // 页码,接口请求的参数
    private int page = 1;
    //未读消息的数量
    private int unreadCount;

    //分页， 每页的总数据
    private List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_sectionalexamination;
    }

    @Override
    protected void onInitView() {
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        unreadCount = getIntent().getIntExtra(UNREADNNCOUNT, 0);
        mTvAllReadColorChange();

        //当出现加载失败时，点击view，会走此方法
        mCommonErrorView.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });

        requestData();

        initRecyclerView();
    }

    private void initRecyclerView() {
        // 设置每页加载的条数，判断是否是最后一页
        mPullRefreshRecyclerView.getRefreshableView().setPagesize(pageSize);
        mPullRefreshRecyclerView.getRefreshableView().setLayoutManager(new LinearLayoutManager(this));

        // 加载过程中是否可以滑动
        mPullRefreshRecyclerView.setPullToRefreshOverScrollEnabled(true);
        // 下拉刷新的回调
        mPullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                //回复页码数为1
                page = 1;
                //刷新的时候，清空次数据
                sectionalExaminationRecombinationBeans.clear();
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

    @OnClick({R.id.mViewBack, R.id.mTvAllRead, R.id.mCommonErrorView})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.mViewBack:
                finish();
                break;

            case R.id.mTvAllRead://全部已读
                if (unreadCount == 0) {
                    ToastUtil.showToast("暂无可清除的消息");
                } else {
                    getAllRead(type);
                }
                break;

                //点击空数据按钮，跳转到首页
//            case R.id.mIvEmptyData:
//                finish();
//                EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_INTENT_HOMEFRAGMENT, new MeMsgMessageEvent());
//                break;

            case R.id.mCommonErrorView:
                requestData();
                break;
            default:
                break;
        }
    }

    /**
     * 当点击全部已读，会更改本地缓存数据，变更为已读状态
     */
    public void setList_isAlert() {

        for (int i = 0; i < this.sectionalExaminationRecombinationBeans.size(); i++) {

            this.sectionalExaminationRecombinationBeans.get(i).isAlert = 0;

            setAdapter(this.sectionalExaminationRecombinationBeans);

        }

    }

    /**
     * 单条数据更改已读状态， 并且刷新界面
     *
     * @param position
     */
    public void setSingleData_isAlert(int position) {

        this.sectionalExaminationRecombinationBeans.get(position).isAlert = 0;

        setAdapter(this.sectionalExaminationRecombinationBeans);

    }

    /**
     * "全部已读" 颜色修改
     */
    public void mTvAllReadColorChange() {

        if (unreadCount == 0) {
            mTvAllRead.setTextColor(getResources().getColor(R.color.arena_exam_subject_name_text));
        } else {
            mTvAllRead.setTextColor(getResources().getColor(R.color.blackF4));
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {

        showProgress();
       /* ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getSectionalExaminationBeans(page, pageSize),
                new NetObjResponse<SectionalExaminationBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<SectionalExaminationBean> model) {

                        dataRecombination(model.data);
                        mPullRefreshRecyclerView.onRefreshComplete();
                        // 隐藏加载动画
                        mPullRefreshRecyclerView.getRefreshableView().hideloading();
                        hideProgess();

                    }

                    @Override
                    public void onError(String message, int type) {

                        hideProgess();
                        mPullRefreshRecyclerView.onRefreshComplete();
                        // 隐藏加载动画
                        mPullRefreshRecyclerView.getRefreshableView().hideloading();
                        showError(type==0?2:(type==3?0:1));
                    }
                });*/
   }

    /**
     * 数据重组，用于展示折叠数据
     *
     * @param sectionalExaminationBeans
     */
    private void dataRecombination(SectionalExaminationBean sectionalExaminationBeans) {

        List<SectionalExaminationBean.ListBean> listBean = null;

        if (sectionalExaminationBeans.list != null) {
            listBean = sectionalExaminationBeans.list;
        }

        if (listBean != null) {

            List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans = new ArrayList<>();

            for (SectionalExaminationBean.ListBean listBean1 : listBean) {

                List<SectionExamRecombinBean> childSectionalExaminationRecombinationBeans = new ArrayList<>();

                if (listBean1.child != null) {

                    int i = 1;

                    for (SectionalExaminationBean.ListBean.ChildBean childBean : listBean1.child) {

                        childSectionalExaminationRecombinationBeans.add(dataRecombinationPeriodTestListBean(childBean, listBean1.classId,listBean1.className, i));

                        i++;
                    }

                }

                sectionalExaminationRecombinationBeans.add(dataRecombinationHomeworkBean(listBean1, childSectionalExaminationRecombinationBeans));
                sectionalExaminationRecombinationBeans.addAll(childSectionalExaminationRecombinationBeans);

            }

            showList(sectionalExaminationRecombinationBeans);
        } else {

            showList(null);

        }

    }

    public SectionExamRecombinBean dataRecombinationHomeworkBean(SectionalExaminationBean.ListBean listBean, List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans) {

        SectionExamRecombinBean sectionalExaminationRecombinationBean = new SectionExamRecombinBean();

        sectionalExaminationRecombinationBean.classId = listBean.classId;
        sectionalExaminationRecombinationBean.className = listBean.className;
        sectionalExaminationRecombinationBean.undoCount = listBean.undoCount;
        sectionalExaminationRecombinationBean.type = SectionExamRecombinBean.CLASS_NAME;
        sectionalExaminationRecombinationBean.child = sectionalExaminationRecombinationBeans;

        return sectionalExaminationRecombinationBean;

    }

    public SectionExamRecombinBean dataRecombinationPeriodTestListBean(SectionalExaminationBean.ListBean.ChildBean childBean, int classId,String className, int index) {

        SectionExamRecombinBean sectionalExaminationRecombinationBean = new SectionExamRecombinBean();

        sectionalExaminationRecombinationBean.classId = classId;
        sectionalExaminationRecombinationBean.className=className;
        sectionalExaminationRecombinationBean.index = index;
        sectionalExaminationRecombinationBean.examName = childBean.examName;
        sectionalExaminationRecombinationBean.examId = childBean.examId;
        sectionalExaminationRecombinationBean.endTime = childBean.endTime;
        sectionalExaminationRecombinationBean.startTime = childBean.startTime;
        sectionalExaminationRecombinationBean.isAlert = childBean.isAlert;
        sectionalExaminationRecombinationBean.syllabusId = childBean.syllabusId;
        sectionalExaminationRecombinationBean.coursewareNum = childBean.coursewareNum;
        sectionalExaminationRecombinationBean.status = childBean.status;
        sectionalExaminationRecombinationBean.isEffective = childBean.isEffective;
        sectionalExaminationRecombinationBean.showTime = childBean.showTime;
        sectionalExaminationRecombinationBean.alreadyRead = childBean.alreadyRead;
        sectionalExaminationRecombinationBean.isExpired=childBean.isExpired;
        sectionalExaminationRecombinationBean.type = SectionExamRecombinBean.STUDY_DETAILS;



        return sectionalExaminationRecombinationBean;
    }

    /**
     * 列表展示数据
     *
     * @param sectionalExaminationRecombinationBeans
     */
    private void showList(List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans) {

        if (sectionalExaminationRecombinationBeans == null || sectionalExaminationRecombinationBeans.size() == 0) {

            //当是第一页的时候，需要展示空数据
            if (page == 1) {
                showError(2);
            }

        } else {

            mCommonErrorView.setVisibility(View.GONE);
            mPullRefreshRecyclerView.setVisibility(View.VISIBLE);
            mRelativeLayoutEmptyData.setVisibility(View.GONE);

            //页码数+1
            page++;

            if (this.sectionalExaminationRecombinationBeans == null) {
                this.sectionalExaminationRecombinationBeans = new ArrayList<>();
            }

            this.sectionalExaminationRecombinationBeans.addAll(sectionalExaminationRecombinationBeans);

            setAdapter(this.sectionalExaminationRecombinationBeans);

        }

    }

    public void setAdapter(List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans) {

        if (sectionalExaminationRecombinationBeans == null || sectionalExaminationRecombinationBeans.size() == 0) {
            showError(2);
        } else {

            if (sectionalExaminationAdapter == null) {
                sectionalExaminationAdapter = new SectionalExaminationAdapter(this, sectionalExaminationRecombinationBeans, this);
                mPullRefreshRecyclerView.getRefreshableView().setRecyclerAdapter(sectionalExaminationAdapter);
            } else {
                sectionalExaminationAdapter.setHomeworkBeans(sectionalExaminationRecombinationBeans);
                sectionalExaminationAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showError(int type) {
        mRelativeLayoutEmptyData.setVisibility(View.GONE);
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
                mRelativeLayoutEmptyData.setVisibility(View.VISIBLE);
                mCommonErrorView.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    /**
     * 列表点击之后的逻辑
     *
     * @param view
     * @param examInfo
     */
    @Override
    public void onClickCourse(View view, SectionExamRecombinBean examInfo, int position) {

        if (examInfo.isAlert == 1) {
            getRead(examInfo.syllabusId, examInfo.classId, position);
        }
        mCurrentItem=examInfo;
        showAction(examInfo.examId,String.valueOf(examInfo.syllabusId),examInfo.status,position, (examInfo.isEffective==1)&&(examInfo.isExpired==false));
    }

    private SectionExamRecombinBean mCurrentItem;

    private String mOperSyllabusIdId=null;//
    private int mOperStageTestIndex=-1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //刷新阶段测试状态
        if((requestCode==2003||requestCode==2014)&&mOperStageTestIndex!=-1&&(null!=sectionalExaminationAdapter)){
            int curIndex=mOperStageTestIndex;
            mOperStageTestIndex=-1;
            SectionExamRecombinBean curItem= sectionalExaminationAdapter.getItem(curIndex);
            if(null!=curItem){
                if((!TextUtils.isEmpty(mOperSyllabusIdId))&&String.valueOf(curItem.syllabusId).equals(mOperSyllabusIdId)){
                    int curStatus= PrefStore.getSettingInt(ArgConstant.KEY_ID+mOperSyllabusIdId,curItem.status);
                    PrefStore.removeSettingkey(ArgConstant.KEY_ID+mOperSyllabusIdId);

                    if(curStatus!=curItem.status){
                        curItem.status=curStatus;
                        sectionalExaminationAdapter.notifyItemChanged(curIndex);
                    }
                }
            }
        }
    }

    private void showAction(final long paperId,final String syllabusId,int testStatus,int position,  boolean hasAlert){
         if(testStatus==1){      // 1未开始
            DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {  }
            }, "", "学完再练习，测试更精准\n考试入口暂未开启", "", "知道了");
            return;
        }else if(testStatus==6) {// 6查看报告
            //  StageReportActivity.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, extraArgs);
            showStageTest(String.valueOf(paperId),syllabusId);
        }else {
            if((testStatus==2)){//开始考试

                mOperStageTestIndex=position;
                mOperSyllabusIdId=syllabusId;
                checkAnswerCardExsit(String.valueOf(paperId),syllabusId, hasAlert);
                return;
            }
            //5继续考试
             mOperStageTestIndex=position;
             mOperSyllabusIdId=syllabusId;
            showStageTest(String.valueOf(paperId),syllabusId);
        }
    }

    private void checkAnswerCardExsit(final String paperId, final String syllabusId, final boolean hasAlert){
       showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().checkhasAnswerCard(paperId,syllabusId),
                new NetObjResponse<Long>() {
                    @Override
                    public void onSuccess(BaseResponseModel<Long> model) {
                        hideProgess();
                        if(model.data>-1){//有答题卡 继续做答
                            Bundle bundle = new Bundle();
                            bundle.putLong("point_ids",  StringUtils.parseLong(paperId));
                            bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(bundle);
                            ArenaExamActivityNew.showForResult(SectionalExaminationActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                        }else {      //开始作答
                            if(!hasAlert){
                                Bundle bundle = new Bundle();
                                bundle.putLong("paperId", StringUtils.parseLong(paperId));
                                bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId) );
                                formatBundle(bundle);
                                SmallMatchActivity.showForResult(SectionalExaminationActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                                return;
                            }
                            //开始作答
                            DialogUtils.onShowConfirmDialog(SectionalExaminationActivity.this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //点击列表之后的逻辑
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("paperId", StringUtils.parseLong(paperId));
                                    bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId) );
                                    formatBundle(bundle);
                                    SmallMatchActivity.showForResult(SectionalExaminationActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                                }
                            }, "", "按时完成考试的你在考试结束后将会收到一份完备评测报告", "", "确定");
                        }
                    }

                    @Override
                    public void onError(String message, int type) {
                         hideProgess();
                        ToastUtils.showShort("请求出错");
                    }
                });
    }

    private void formatBundle(Bundle  extraArgs){
        if(null==mCurrentItem) return;

        extraArgs.putString("course_id",String.valueOf(mCurrentItem.classId));
        extraArgs.putString("course_title",mCurrentItem.className);
        extraArgs.putString("class_id",String.valueOf(mCurrentItem.examId));
        extraArgs.putString("class_title",mCurrentItem.examName);
    }

    private void showStageTest(final String paperId, final String syllabusId){
        showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().createPeriodTestInfo(paperId,syllabusId),
                new NetObjResponse<PeriodTestBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<PeriodTestBean> model) {
                        hideProgess();
                        if(model.data.status==3){
                            Bundle  extraArgs=new Bundle();
                            extraArgs.putLong("practice_id",StringUtils.parseLong(model.data.practiceId));
                            extraArgs.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(extraArgs);
                            StageReportActivity.show(SectionalExaminationActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, extraArgs);
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putLong("point_ids",  StringUtils.parseLong(paperId));
                            bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(bundle);
                            ArenaExamActivityNew.showForResult(SectionalExaminationActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                        }
                    }

                    @Override
                    public void onError(String message, int type) {
                        hideProgess();
                        ToastUtils.showShort("请求出错");
                    }
                });
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
                        PrefStore.putSettingInt("study_unread_message_change",1);
                        //未读消息数量值0
                        unreadCount = 0;
                        //更改全部已读颜色
                        mTvAllReadColorChange();

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
     * @param syllabusId
     * @param id
     * @param position 需要用次字段修改数据
     */
    private void getRead(int syllabusId, int id, final int position) {

        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().getRead(syllabusId, id),
                new NetObjResponse<String>() {
                    @Override
                    public void onSuccess(BaseResponseModel<String> model) {

                        //修改条数据，刷新列表
                        setSingleData_isAlert(position);
                        PrefStore.putSettingInt("study_unread_message_change",1);
                        //未读消息数量值 -1
                        unreadCount -= 1;
                        //更改全部已读颜色
                        mTvAllReadColorChange();

                    }

                    @Override
                    public void onError(String message, int type) {

                        ToastUtil.showToast(message);

                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(SectionalExaminationEvent sectionalExaminationEvent) {

        if (sectionalExaminationEvent == null) {
            return;
        }

        if (sectionalExaminationEvent.mBundle != null) {

            int status = sectionalExaminationEvent.mBundle.getInt("status");

            int position = sectionalExaminationEvent.mBundle.getInt("position", -1);

            if (position == -1) {
                return;
            }

            if (status == 6) {

                if (sectionalExaminationRecombinationBeans != null && sectionalExaminationRecombinationBeans.size() >= position) {

                    sectionalExaminationRecombinationBeans.remove(position);
                    if (sectionalExaminationRecombinationBeans.get(position - 1).child != null &&
                            sectionalExaminationRecombinationBeans.get(position - 1).child.size() == 1) {
                        sectionalExaminationRecombinationBeans.remove(position - 1);
                    }
                    setAdapter(sectionalExaminationRecombinationBeans);

                }

            } else if (status == 5) {

                if (sectionalExaminationRecombinationBeans != null && sectionalExaminationRecombinationBeans.size() >= position) {

                    sectionalExaminationRecombinationBeans.get(position).status = status;
                    setAdapter(sectionalExaminationRecombinationBeans);

                }

            } else {
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
