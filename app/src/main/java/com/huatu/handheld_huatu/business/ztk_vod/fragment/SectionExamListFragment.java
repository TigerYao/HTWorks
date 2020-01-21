package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.matchsmall.activity.SmallMatchActivity;
import com.huatu.handheld_huatu.business.matchsmall.activity.StageReportActivity;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.SectionalExaminationAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.SectionExamRecombinBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.SectionalExaminationBean;
import com.huatu.handheld_huatu.event.SectionalExaminationEvent;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.retrofit.KindRetrofitCallBack;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PeriodTestBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.PullNestRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class SectionExamListFragment extends ABaseListFragment<WarpListResponse<SectionExamRecombinBean>> implements OnRecItemClickListener
  , SectionalExaminationAdapter.ItemClickListener {
    @BindView(R.id.xi_comm_page_list)
    PullNestRefreshRecyclerView mWorksListView;


    public int getContentView() {
        return R.layout.comm_ptrlist_layout4;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView.getRefreshableView();
    }

    SectionalExaminationAdapter mListAdapter;


    private int mUnreadCount=0;


    public static void lanuch(Context context,  int unReadCount) {

        Bundle arg = new Bundle();

        arg.putInt(ArgConstant.TYPE,unReadCount);
        UIJumpHelper.jumpFragment(context, SectionExamListFragment.class, arg);
    }

    @Override
    protected void parserParams(Bundle args) {
       mUnreadCount=args.getInt(ArgConstant.TYPE,0);
    }

    CompositeSubscription mCompositeSubscription;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("阶段测评");
        EventBus.getDefault().register(this);
    }


    MenuItem mRightMenu;
    @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {
        super.onCreateTitleBarMenu(titleBar, container);
        titleBar.add("全部已读", ResourceUtils.getColor(R.color.blackF4), android.R.id.button1);
        mRightMenu = titleBar.findMenuItem(android.R.id.button1);
        mTvAllReadColorChange();
    }

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if (menuItem.getId() == android.R.id.button1) {
            //setList_isAlert();
            if (mUnreadCount == 0) {
                ToastUtil.showToast("暂无可清除的消息");
            } else {
                getAllRead("periodTest");
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new WarpListResponse<SectionExamRecombinBean>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new SectionalExaminationAdapter(getContext(), mListResponse.mAdapterList,this);

    }


    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }

    private void superOnRefresh(){
        super.onRefresh();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.drawable.htzx_sectional_examination_empty_pic);

        mWorksListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        mWorksListView.getRefreshableView().setPagesize(getLimit());
        mWorksListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.getRefreshableView().setRecyclerAdapter(mListAdapter);

    }


    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if(mListAdapter.getItemCount()<=0){
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }
    @Override
    protected void onRefreshCompleted(){
        if(null!=mWorksListView) mWorksListView.onRefreshComplete();
    }

    @Override
    protected void setListener() {
        mWorksListView.getRefreshableView().setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getMySectionalExamList(offset,limit).enqueue(getCallback3());
    }

    protected RetrofitCallback<WarpListResponse<SectionalExaminationBean.ListBean>> getCallback3() {
        return new RetrofitCallbackWrapper3(this);
    }

    private class RetrofitCallbackWrapper3 extends RetrofitCallback<WarpListResponse<SectionalExaminationBean.ListBean>> {

        final KindRetrofitCallBack<WarpListResponse<SectionExamRecombinBean>> mCallback;

        public RetrofitCallbackWrapper3(KindRetrofitCallBack<WarpListResponse<SectionExamRecombinBean>> callBack) {
            this.mCallback = callBack;
        }

        private SectionExamRecombinBean dataRecombinationHomeworkBean(SectionalExaminationBean.ListBean listBean, List<SectionExamRecombinBean> childs) {

            SectionExamRecombinBean HeadBean = new SectionExamRecombinBean();
            HeadBean.classId = listBean.classId;
            HeadBean.className = listBean.className;
            HeadBean.undoCount = listBean.undoCount;
            HeadBean.type = SectionExamRecombinBean.CLASS_NAME;
            HeadBean.child = childs;
            return HeadBean;
         }

        private SectionExamRecombinBean dataRecombinationPeriodTestListBean(SectionalExaminationBean.ListBean.ChildBean childBean, int classId,String className, int index) {

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

        @Override
        protected void onSuccess(WarpListResponse<SectionalExaminationBean.ListBean> response) {
            if (mCallback != null && !mCallback.isFragmentFinished()) {

                List<SectionalExaminationBean.ListBean> listBean=response.getListResponse();

                WarpListResponse<SectionExamRecombinBean> callbackResponse=new WarpListResponse<>();
                callbackResponse.data=new WarpListResponse.Data<>();
                callbackResponse.data.list=new ArrayList<>();

                if (!ArrayUtils.isEmpty(listBean)) {
                  for (SectionalExaminationBean.ListBean listBean1 : listBean) {

                        List<SectionExamRecombinBean> childSectionalExaminationRecombinationBeans = new ArrayList<>();

                        if (listBean1.child != null) {
                           int i = 1;
                            for (SectionalExaminationBean.ListBean.ChildBean childBean : listBean1.child) {
                                childSectionalExaminationRecombinationBeans.add(dataRecombinationPeriodTestListBean(childBean, listBean1.classId,listBean1.className, i));
                                i++;
                            }
                        }
                      callbackResponse.data.list.add(dataRecombinationHomeworkBean(listBean1, childSectionalExaminationRecombinationBeans));//添加头部
                      callbackResponse.data.list.addAll(childSectionalExaminationRecombinationBeans);
                    }
                }
                mCallback.onSuccess(callbackResponse);
             }
        }

        @Override
        protected void onFailure(String error, int type) {
            if (mCallback != null && !mCallback.isFragmentFinished()) {
                mCallback.onError(error, type);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            if (mCallback != null) {
                mCallback.onSubscriberStart();
            }
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //刷新阶段测试状态
        if((requestCode==2003||requestCode==2014)&&mOperStageTestIndex!=-1&&(null!=mListAdapter)){
            int curIndex=mOperStageTestIndex;
            mOperStageTestIndex=-1;
            SectionExamRecombinBean curItem= mListAdapter.getItem(curIndex);
            if(null!=curItem){
                if((!TextUtils.isEmpty(mOperSyllabusIdId))&&String.valueOf(curItem.syllabusId).equals(mOperSyllabusIdId)){
                    int curStatus= PrefStore.getSettingInt(ArgConstant.KEY_ID+mOperSyllabusIdId,curItem.status);
                    PrefStore.removeSettingkey(ArgConstant.KEY_ID+mOperSyllabusIdId);

                    if(curStatus!=curItem.status){
                        curItem.status=curStatus;
                        mListAdapter.notifyItemChanged(curIndex);
                    }
                }
            }
        }
    }

    private void showAction(final long paperId,final String syllabusId,int testStatus,int position,  boolean hasAlert){
        if(testStatus==1){      // 1未开始
            DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
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
                            ArenaExamActivityNew.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                        }else {      //开始作答
                            if(!hasAlert){
                                Bundle bundle = new Bundle();
                                bundle.putLong("paperId", StringUtils.parseLong(paperId));
                                bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId) );
                                formatBundle(bundle);
                                SmallMatchActivity.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                                return;
                            }
                            //开始作答
                            DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //点击列表之后的逻辑
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("paperId", StringUtils.parseLong(paperId));
                                    bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId) );
                                    formatBundle(bundle);
                                    SmallMatchActivity.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
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
                            StageReportActivity.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, extraArgs);
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putLong("point_ids",  StringUtils.parseLong(paperId));
                            bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(bundle);
                            ArenaExamActivityNew.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
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
                        mUnreadCount = 0;
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
                        mUnreadCount -= 1;
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

            if (status == 6) {//6查看报告
              /*     if (sectionalExaminationRecombinationBeans != null && sectionalExaminationRecombinationBeans.size() >= position) {

                    sectionalExaminationRecombinationBeans.remove(position);
                    if (sectionalExaminationRecombinationBeans.get(position - 1).child != null &&
                            sectionalExaminationRecombinationBeans.get(position - 1).child.size() == 1) {
                        sectionalExaminationRecombinationBeans.remove(position - 1);
                    }
                    setAdapter(sectionalExaminationRecombinationBeans);

                }*/

                if(mListAdapter!=null){
                    SectionExamRecombinBean curBean= mListAdapter.getItem(position);
                    if(null!=curBean){
                        mListAdapter.getAllItems().remove(position);

                        SectionExamRecombinBean precurBean= mListAdapter.getItem(position-1);
                        if(null!=precurBean){//如果上一个它的父节点
                            if (precurBean.child != null && precurBean.child.size() == 1) {
                                mListAdapter.getAllItems().remove(position - 1);
                            }
                        }
                        this.mListAdapter.notifyDataSetChanged();
                    }
                }

            } else if (status == 5) {

                if(mListAdapter!=null){
                    SectionExamRecombinBean curBean= mListAdapter.getItem(position);
                    if(null!=curBean){
                        curBean.status=status;
                        this.mListAdapter.notifyItemChanged(position);
                    }
                }
            }
        }

    }


    /**
     * 当点击全部已读，会更改本地缓存数据，变更为已读状态
     */
    public void setList_isAlert() {
        if(null!= this.mListAdapter){
            for (int i = 0; i < this.mListAdapter.getItemCount(); i++) {

                SectionExamRecombinBean curBean= mListAdapter.getItem(i);
                if(curBean!=null){
                    curBean.isAlert = 0;
                }
            }
            this.mListAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 单条数据更改已读状态， 并且刷新界面
    * @param position
     */
    private void setSingleData_isAlert(int position) {
        if(null!= this.mListAdapter){
            SectionExamRecombinBean curBean= mListAdapter.getItem(position);
            if(curBean!=null){
                curBean.isAlert = 0;
                mListAdapter.notifyItemChanged(position);
            }
        }
    }

    /**
     * "全部已读" 颜色修改
     */
    public void mTvAllReadColorChange() {
        if (mUnreadCount == 0) {
            mRightMenu.setText("全部已读",0xFF9B9B9B);
        } else {
            mRightMenu.setText("全部已读",0xFF4A4A4A);
        }
    }


    CustomLoadingDialog mLoadingDialog;
    private void showProgress(){
        mLoadingDialog= DialogUtils.showLoading(getActivity(),mLoadingDialog);
    }

    private void hideProgess(){
        DialogUtils.dismissLoading(mLoadingDialog);
    }
}
