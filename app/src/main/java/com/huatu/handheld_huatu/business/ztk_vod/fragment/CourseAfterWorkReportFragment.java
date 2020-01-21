package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.ExplandListAdapter;
import com.huatu.handheld_huatu.adapter.course.KnowsPointlistAdapter;
import com.huatu.handheld_huatu.adapter.course.StudyRankExplandAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.me.fragment.CourseCollectListFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.CourseWorkReportBean;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterNewImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainNewView;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.AnswerCardView;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.NoScrollListView;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.LinearLayoutListView;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\2\18 0018.
 */

public class CourseAfterWorkReportFragment extends MySupportFragment {

    @BindView(R.id.answerCard_report)
    AnswerCardView mListView;

    @BindView(R.id.xi_toolbar)
    TitleBar mTitleBar;

    @BindView(R.id.circleProgressBar)
    QMUIProgressBar mCircleProgressBar;

    @BindView(R.id.study_listview)
    NoScrollListView mStudylistview;

    @BindView(R.id.knowspoint_listView)
    LinearLayoutListView mknowspointsView;


    @BindView(R.id.cost_time_txt)
    TextView mCostTimeTxt;

    @BindView(R.id.score_max_txt)
    TextView mScoreMaxTxt;

    @BindView(R.id.average_score_txt)
    TextView mAverageScoreTxt;

    StudyRankExplandAdapter mStudyRankExplandAdapter;

    private long mPracticeId;


    private String mCourseId,mCourseName,mClassId,mClassName;



    @Override
    protected int getContentView() {
        return R.layout.study_report_layout;
    }

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // UMShareAPI.get(getActivity()).release();
        if(null!=mPresenter)
           mPresenter=null;
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public static void lanuch(Context context,long practice_id,Bundle extraArgs){
        Bundle tmpArg=new Bundle();
        tmpArg.putLong(ArgConstant.KEY_ID,practice_id);

        if(null!=extraArgs){
            tmpArg.putString(ArgConstant.COURSE_ID,extraArgs.getString(ArgConstant.COURSE_ID,""));
            tmpArg.putString(ArgConstant.COURSE_NAME,extraArgs.getString(ArgConstant.COURSE_NAME,""));
            tmpArg.putString(ArgConstant.TYPE_ID,extraArgs.getString(ArgConstant.TYPE_ID,""));
            tmpArg.putString(ArgConstant.TITLE,extraArgs.getString(ArgConstant.TITLE,""));
        }

        FragmentParameter tmpPar=new FragmentParameter(CourseAfterWorkReportFragment.class,tmpArg);
        UIJumpHelper.jumpSupportFragment(context,tmpPar,true);
    }

    public static void lanuch(Context context,long practice_id,String courseId,String courseName,String classId,String className){
        Bundle tmpArg=new Bundle();
        tmpArg.putLong(ArgConstant.KEY_ID,practice_id);
        tmpArg.putString(ArgConstant.COURSE_ID,courseId);
        tmpArg.putString(ArgConstant.COURSE_NAME,courseName);
        tmpArg.putString(ArgConstant.TYPE_ID,classId);
        tmpArg.putString(ArgConstant.TITLE,className);
        FragmentParameter tmpPar=new FragmentParameter(CourseAfterWorkReportFragment.class,tmpArg);
        UIJumpHelper.jumpSupportFragment(context,tmpPar,true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null!=getArguments()){
            mPracticeId=getArguments().getLong(ArgConstant.KEY_ID,0);

            mCourseId=getArguments().getString(ArgConstant.COURSE_ID);
            mCourseName=getArguments().getString(ArgConstant.COURSE_NAME);
            mClassId=getArguments().getString(ArgConstant.TYPE_ID);
            mClassName=getArguments().getString(ArgConstant.TITLE);
        }
    }

    @Override
    protected void setListener() {
         mTitleBar.setBackgroundColor(0X00000000);

       // mTitleBar.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.bg_studyreport_gradient));
        mTitleBar.setTitle("课后作业报告");
        mTitleBar.setShadowVisibility(View.GONE);
        mTitleBar.setTitleTextColor(Color.WHITE);
        mTitleBar.setDisplayHomeAsUpEnabled(true,R.drawable.video_play_title_back);

        mTitleBar.setDisplayHomeAsUpEnabled(true);
        mTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if (menuItem.getId() == R.id.xi_title_bar_home) {
                    getActivity().finish();
                }else if (menuItem.getId() == android.R.id.button1){
                    getSendClass();
                }
            }
        });

       // mTitleBar.add(ResourceUtils.getDrawable(R.mipmap.check_report_share),android.R.id.button1);
        getView().findViewById(R.id.list_expland_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStudyRankExplandAdapter==null) return;
                if (mStudyRankExplandAdapter.getCount() == ExplandListAdapter.ExplandCount) {
                    mStudyRankExplandAdapter.setItemNum(mStudyRankExplandAdapter.getRealCount());
                   ((TextView)v).setText("收起");
                    mStudyRankExplandAdapter.notifyDataSetChanged();
                } else {
                    mStudyRankExplandAdapter.setItemNum(ExplandListAdapter.ExplandCount);
                    ((TextView)v).setText("查看更多");
                    mStudyRankExplandAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       // mCircleProgressBar.setProgress(85,true);


        mCostTimeTxt.setText(getSpannableString("0"+" 分钟"+"\n"+"平均用时",1,"分钟",2));
        mScoreMaxTxt.setText(getSpannableString("0"+" 道"+"\n"+"最高答对题数",1,"道",1));
        mAverageScoreTxt.setText(getSpannableString("0"+" 道"+"\n"+"平均答对题数",1,"道",1));
        loadData();
    }


    private SpannableString getSpannableString(String content,int startlen,String key,int middlen){
        SpannableString msp = new SpannableString(content);
        msp.setSpan(new ForegroundColorSpan(0XFFFF6D73), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色

        msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(getContext(),18)), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体


        int index=content.indexOf(key);
        if(index>0){
            msp.setSpan(new ForegroundColorSpan(0XFF4A4A4A), index, index+middlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
            msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(getContext(),12)), index, index+middlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
        }
         return msp;
    }


    private void sendLookAfterWorkReport(CourseWorkReportBean reportBean){

        StudyCourseStatistic.sendLookAfterWorkReport(mCourseId,mCourseName,mClassId,mClassName,reportBean);
    }

    private void bindUI(CourseWorkReportBean workReportBean){

       try{

           String formatTime=workReportBean.avgTimeCost<60? "1":Math.round((float)workReportBean.avgTimeCost/60)+"";
           mCostTimeTxt.setText(getSpannableString(formatTime+" 分钟"+"\n"+"平均用时",String.valueOf(formatTime).length(),"分钟",2));
           mScoreMaxTxt.setText(getSpannableString(workReportBean.maxCorrect+" 道"+"\n"+"最高答对题数",String.valueOf(workReportBean.maxCorrect).length(),"道",1));
           mAverageScoreTxt.setText(getSpannableString(workReportBean.avgCorrect+" 道"+" "+"\n"+"平均答对题数",workReportBean.avgCorrect.length(),"道",1));

           List<AnswerCardView.CardAnswer> pointList=new ArrayList<>();
           int size= ArrayUtils.size(workReportBean.corrects);
           for(int i=0;i<size;i++){
               AnswerCardView.CardAnswer tmpData=new AnswerCardView.CardAnswer();
               tmpData.status= workReportBean.corrects.get(i) ;
               tmpData.index=String.valueOf(i+1);
               tmpData.day="";
               pointList.add(tmpData);
           }
           mListView.setDateList(pointList);
           mListView.setOnClickCardViewListener(new AnswerCardView.OnClickCardViewListener(){

               public void onClickCurrentCard(AnswerCardView.CardAnswer data){

                   int index=StringUtils.parseInt(data.index)-1;
                   showAnswerAnalysis(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, index<0?0:index);
               }
           });

           getView().findViewById(R.id.list_expland_img).setVisibility(ArrayUtils.size(workReportBean.allScoreRank)>3?View.VISIBLE:View.GONE);
           mStudyRankExplandAdapter=new StudyRankExplandAdapter(getContext(), workReportBean.allScoreRank);
           mStudylistview.setAdapter(mStudyRankExplandAdapter);
           mknowspointsView.setAdapter(new KnowsPointlistAdapter(getContext(), workReportBean.points));
           ((TextView)getView().findViewById(R.id.tv_total_course)).setText("共"+workReportBean.tcount+"题");
           ((TextView)getView().findViewById(R.id.tv_my_rank)).setText("我的排名"+workReportBean.myrank+" 名");
           ((TextView)getView().findViewById(R.id.answerCard_createtime_tv)).setText("交卷时间："+ DateUtils.getFormatData("yyyy.MM.dd  HH:mm",workReportBean.createTime));

           //((TextView)getView().findViewById(R.id.tv_right_num)).setText(+workReportBean.rcount+"题");
           final  int rcount=workReportBean.rcount;
           mCircleProgressBar.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
               @Override
               public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                   //return 100 * value / maxValue + "";
                   return  String.valueOf(rcount);
               }
           });
           if(workReportBean.tcount>0){
                mCircleProgressBar.setProgress((int)((float)workReportBean.rcount)*100/workReportBean.tcount,true);
           }

       }catch (Exception e){
           LogUtils.e("afterwork",e.getMessage()+"");
           e.printStackTrace();
       }
    }

    @OnClick({R.id.allanalys_btn,R.id.wronganalys_btn})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.allanalys_btn:
                showAnswerAnalysis(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL,0);
                break;
            case R.id.wronganalys_btn:
                showAnswerAnalysis(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG,0);
                break;
        }
    }

    private ArenaExamPresenterNewImpl  mPresenter;                       // 网络访问
    private RealExamBeans.RealExamBean mCurRealExamBean;

    private void showAnswerAnalysis(final int requestType,final  int showIndex) {
        if(null!=mCurRealExamBean){
            goArenaExamActivityNew(requestType, mCurRealExamBean,showIndex);
            return;
        }
        if (null == mPresenter) {
            mPresenter = new ArenaExamPresenterNewImpl(getSubscription(), new ArenaExamMainNewView() {
                // 得到试卷信息
                public void onGetPractiseData(RealExamBeans.RealExamBean beans, List<ArenaExamQuestionBean> questionBeans) {
                    mCurRealExamBean=beans;
                    goArenaExamActivityNew(requestType, beans,showIndex);
                }

                // 返回退出，保存答案成功
                public void onSavedAnswerCardSuccess(int saveType) { }

                // 获取数据失败
                public void onLoadDataFailed(int flag) {
                    CommonUtils.showToast(flag<=1? "无网络,请检查网络":"获取练习信息失败，请您重试");
                }

                public void showProgressBar() {
                    mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
                }

                public void dismissProgressBar() {
                    DialogUtils.dismissLoading(mLoadingDialog);
                }

                public void onSetData(Object respData) {   }

                public void onLoadDataFailed() {   }
            });
        }
        mPresenter.getPractiseDetails(mPracticeId);
    }

    /**
     * 错题解析、全部解析 ArenaExamPresenterNewImpl 做题获取答题卡&题的类，576行就是获取到报告数据之后的处理。
     * 去答题/看题页面
     * 全部解析type ：ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
     * 全部解析type ：ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG
     */
     private final void goArenaExamActivityNew(int requestType,RealExamBeans.RealExamBean realExamBean,int showIndex) {
        if (realExamBean == null) return;
        Bundle bundle = new Bundle();
        ArenaDataCache.getInstance().realExamBean = realExamBean;
        boolean isClickCard=false;
        if (showIndex>0) {
            isClickCard = false;
            bundle.putInt("showIndex", showIndex);
        } else {
            bundle.putInt("showIndex", 0);
        }
        ArenaExamActivityNew.show(getActivity(), requestType, bundle);
    }

    CustomLoadingDialog mLoadingDialog;
    private void loadData(){
        mLoadingDialog= DialogUtils.showLoading(getActivity(),mLoadingDialog);
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getCourseWorkReport(mPracticeId),
                new NetObjResponse<CourseWorkReportBean>() {
            @Override
            public void onSuccess(BaseResponseModel<CourseWorkReportBean> model) {
                DialogUtils.dismissLoading(mLoadingDialog);
                bindUI(model.data);
                sendLookAfterWorkReport(model.data);
            }

            @Override
            public void onError(String message, int type) {
                DialogUtils.dismissLoading(mLoadingDialog);
            }
        });

    }

    private void getSendClass() {
       final CustomDialog customDialog = new CustomDialog(getActivity(), R.layout.dialog_feedback_commit);
        TextView tv_notify_message = (TextView) customDialog.mContentView.findViewById(R.id.tv_notify_message);
        tv_notify_message.setText("获取分享数据中...");
        customDialog.show();
        final long id = 97289;
        final String classScaleimg="";

        Subscription imageSubscription = DataController.getInstance()
                .sendClass(id)
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
//                            if (SpUtils.getFreeCourseListenFlag()) {
////                                ToastUtils.showRewardToast("WATCH_FREE");
//                                SpUtils.setFreeCourseListenFlag(false);
//                            }
                            if (!TextUtils.isEmpty(classScaleimg)) {
                                ShareUtil.test(getActivity(), testBean.data.id, testBean.data.desc,
                                        testBean.data.title, testBean.data.url, classScaleimg, null, String.valueOf(id));
                            } else {
                                ShareUtil.test(getActivity(), testBean.data.id, testBean.data.desc,
                                        testBean.data.title, testBean.data.url,  String.valueOf(id));
                            }
                        } else {
                            CommonUtils.showToast("获取分享数据失败");
                        }
                    }
                });
        getSubscription().add(imageSubscription);
    }
}
