package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.adapter.course.RecordCatalogAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.essay.activity.HomeworkSingleListActivity;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.matchsmall.activity.SmallMatchActivity;
import com.huatu.handheld_huatu.business.matchsmall.activity.StageReportActivity;
import com.huatu.handheld_huatu.business.play.fragment.VideoStudyReportFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.OnCoursePlaylistener;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadAssist;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDeleteCacheListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseDataConverter;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VideoStatisticsUtil;
import com.huatu.handheld_huatu.helper.image.MyPreloadTarget;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitStatusCallbackEx;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CourseStageBean;
import com.huatu.handheld_huatu.mvpmodel.EssayAfterStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.PurchaseCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.Sensor.CourseInfoForStatistic;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.InClassAnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.InClassEssayCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PeriodTestBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.ReportIntBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerViewTop;
import com.huatu.handheld_huatu.ui.StageLinearLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewTopEx;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.music.bean.Music;
import com.huatu.music.player.PlayManager;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Response;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\7\20 0020.
 */

public class CourseCatalogFragment extends ABaseListFragment<PurchaseCourseListResponse> implements AStripTabsFragment.IStripTabInitData, OnRecItemClickListener,
        ScrollableHelper.ScrollableContainer,OnPlaySelectListener ,OnDLVodListener,OnAfterSelectListener ,OnDeleteCacheListener {
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerViewTop mWorksListView;

    @Override
    public int getContentView() {
        return R.layout.comm_ptrlist_layout3;
    }

    @Override
    protected RecyclerViewTopEx getListView() {
        return mWorksListView.getRefreshableView();
    }

    @Override
    public View getScrollableView(){
         if(null!=mWorksListView) return mWorksListView.getRefreshableView();
        return null;
    }

    RecordCatalogAdapter mListAdapter;

    //改为 或  操作
    private boolean mFilterLocal=false;
    private boolean mFilterAfterClass=false;

    private boolean mIsLoadHead=false;// 加载头部更多
    private int     mPreSize = 0;//上次适配器个数
    private String  mCourseId = "";
    private int     mCourseWareId = 0;
    private String mNetClassName;

    StageLinearLayout mStageFilterLayout;

    private String mFilterTeacherIds="";
    private String mFilterTeacherNames="";
    private String mFilterClassNodeId="";

    private String mOnLiveLocationNodeId="";
    private String mNextClassNodeId;
    private String mStageNodeId;
    private boolean mHasPostion=false;
    private boolean mFormCalendar=false;

    private int mFilterType=0;//0 不过滤 1 课后 2 缓存  3 课后or缓存
    //private HashMap<String,DownLoadLesson> mDownLoadMap=new HashMap<>();
    private SparseArray<DownLoadLesson> mDownLoadMap = new SparseArray<DownLoadLesson>();

    @Override
    public  List<PurchasedCourseBean.Data> getCurrentCourseList(){
         if(null!=mListAdapter){
             return mListAdapter.getAllItems();
         }
         return null;
    }

    //保存已缓存的目录
    private SparseArray<PurchasedCourseBean.Data> mDownLoadFinishMap = new SparseArray<PurchasedCourseBean.Data>();


    private List<PurchasedCourseBean.Data> mDownLoadinglist=new ArrayList<>();

    public static CourseCatalogFragment getInstance(String courseId, int courseWareId) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putInt(ArgConstant.KEY_ID, courseWareId);
        CourseCatalogFragment tmpFragment = new CourseCatalogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;

    }

    public static CourseCatalogFragment getInstance(String courseId, int courseWareId,int locationNodeId,boolean isQuickPlay) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putInt(ArgConstant.KEY_ID, courseWareId);
        args.putInt(ArgConstant.FROM_ACTION,locationNodeId);
        args.putBoolean(ArgConstant.TYPE,isQuickPlay);
        CourseCatalogFragment tmpFragment = new CourseCatalogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    public static CourseCatalogFragment getInstance(String courseId) {
        return getInstance(courseId, 0);
    }
    @Override
    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mCourseWareId = args.getInt(ArgConstant.KEY_ID, 0);
        int fromLocation= args.getInt(ArgConstant.FROM_ACTION,0);
        if(fromLocation>0){
            mOnLiveLocationNodeId=String.valueOf(fromLocation);
            mFormCalendar=true;
        }
        if(args.getBoolean(ArgConstant.TYPE)){
            mFormCalendar=true;
        }
    }

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mListAdapter.destory();
        mListAdapter=null;
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);

        DownLoadAssist.getInstance().removeDownloadListener(this);
        DownLoadAssist.getInstance().removeDeleteCacheListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListResponse = new PurchaseCourseListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new RecordCatalogAdapter(getContext(), mListResponse.mLessionlist,false);
        mListAdapter.setOnViewItemClickListener(this);
        DownLoadAssist.getInstance().addDownloadListener(this);
        DownLoadAssist.getInstance().addDeleteCacheListener(this);
        if (mCourseWareId > 0)
            onSelectChange(mCourseWareId,0);

    }


    //  这里复用一下字段hasPlayTime
    // case1,-(课件 节点id)  直播点击重定向，刷新列表。case2,其它 0 选中。 case3,-1播放完成,time更新播放时长
    @Override
    public  void onSelectChange(int id,int hasPlayTime){
        if(mListAdapter!=null){
            if(hasPlayTime<-10){
                mOnLiveLocationNodeId=String.valueOf(Math.abs(hasPlayTime));
                LogUtils.e("mOnLiveLocationNodeId",mOnLiveLocationNodeId);
                mListAdapter.setLocationId(id);
                mIsLoadHead=false;
                super.onRefresh();
            }
            else if(hasPlayTime==0){
                 mListAdapter.setSelectId(id);
                 if(!mHasPostion&&(mListAdapter.getItemCount()>0)){
                     setRelocation(id);
                 }
            }
            else
                mListAdapter.setSelectId(id,hasPlayTime);
        }
    }

    private void setRelocation(int courseWareId){
        int postion=-1;
        mHasPostion=true;
        for(int i=0;i<mListAdapter.getAllItem().size();i++){
            if(mListAdapter.getItem(i).coursewareId==courseWareId){
                postion=i;
                mListAdapter.setPlayingIndex(postion);
                if(mFormCalendar&&(getActivity() instanceof BJRecordPlayActivity)){
                    ((BJRecordPlayActivity)getActivity()).setLocationCourseWare(mListAdapter.getItem(i));
               }
                break;
            }
        }
        if(postion!=-1&&(mWorksListView!=null)){
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mWorksListView.getRefreshableView().getLayoutManager();
            int absPostion=0;
            if(postion>4){
                absPostion=Math.min(mListAdapter.getItemCount()-postion,postion);
            }
            mLayoutManager.scrollToPositionWithOffset(postion,absPostion>4? DensityUtils.dp2px(getContext(), 60):0);
        }
    }

    @Override
    public void onStripTabRequestData() {
        onFirstLoad();
    }

    @Override
    public void requestData() {
        super.requestData() ;
        List<DownLoadLesson> mDownedCoursewares = SQLiteHelper.getInstance().getLessonsByStatus(mCourseId, "2");
        for(DownLoadLesson bean :mDownedCoursewares){
           mDownLoadMap.put(StringUtils.parseInt(bean.getSubjectID()),bean);
        }
        onFirstLoad();
        requestStageList();
    }

    private void requestStageList(){
        if(null!=mStageFilterLayout){
            if(mStageFilterLayout.hasLevel())
                return;
        }
         ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getSyllabusClasses(mCourseId,""),//StringUtils.parseInt(mCourseId)
                new NetObjResponse<CourseStageBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<CourseStageBean> model) {
                          if(getActivity() instanceof BJRecordPlayActivity){
                               if((model.data.hierarchy==2)&&(ArrayUtils.size(model.data.list)>1)){
                                  SyllabusClassesBean tmpBean=new SyllabusClassesBean();
                                  tmpBean.name= "全部课程";
                                  model.data.list.add(0,tmpBean);
                              }
                              ((BJRecordPlayActivity)getActivity()).mFilterSyllabusClass=model.data.list;
                          }
                          if(null!=mStageFilterLayout)
                             mStageFilterLayout.setStageLevel(model.data.hierarchy);
                    }

                    @Override
                    public void onError(String message, int type) { }
                });

    }

    private void showFilterDialog(String tag){

        if(null!=getEmptyLayout()){
            if(getEmptyLayout().isShownStatus(CommloadingView.StatusMode.loading)){
                return;
            }
        }
        if(CommonUtils.isFastDoubleClick()) return;
        Fragment oldfragment = getActivity().getSupportFragmentManager().findFragmentByTag("stage_filter");
        if(oldfragment!=null) return;

        StageFilterDialogFragment ratefragment = StageFilterDialogFragment.getInstance(mCourseId,StringUtils.parseInt(mFilterClassNodeId),mStageNodeId,mFilterTeacherIds,StringUtils.parseInt(tag));
        ratefragment.show(getActivity().getSupportFragmentManager(), "stage_filter");
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();

        String resultStr="暂无课程目录，了解下课程介绍吧";
        SpannableString spanStr = new SpannableString(resultStr);
        int Startindex=resultStr.indexOf("课程介绍");
        //设置字体前景色
        spanStr.setSpan(new ForegroundColorSpan(0xFFFF3F47),   Startindex, Startindex+"课程介绍".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(spanStr);

        FrameLayout rootView=this.findViewById(R.id.whole_content);
        LayoutInflater factory = LayoutInflater.from(getContext());
        mStageFilterLayout=(StageLinearLayout)factory.inflate(R.layout.course_catalogtitle_bar,rootView,false);
        for(int i=0;i<3;i++){
            mStageFilterLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterDialog(v.getTag().toString());
                }
            });
        }
        rootView.addView(mStageFilterLayout,0);
         if(null!=mWorksListView){
            FrameLayout.LayoutParams tmpParams= (FrameLayout.LayoutParams)mWorksListView.getLayoutParams();
            tmpParams.setMargins(0,DensityUtils.dp2px(getContext(),43),0,0);
            mWorksListView.setLayoutParams(tmpParams);
        }

        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        getEmptyLayout().findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseWebInfoFragment.lanuch(getContext(),mCourseId,0);
            }
        });
        getEmptyLayout().setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof BJRecordPlayActivity){
                    ((BJRecordPlayActivity)getActivity()).checkLoadCourseInfo();
                }
                mIsLoadHead=false;
                onRefresh();
            }
        });
       // mWorksListView.setCanPull(false);
       // mWorksListView.setPullLabel("");
        mWorksListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewTopEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewTopEx> refreshView) {
                mPreSize = mListAdapter.getItemCount();
                mIsLoadHead=true;
                CourseCatalogFragment.super.OnLoadMoreEvent(true);
            }
        });
       // getListView().setPagesize(1000);
        getListView().setOnLoadMoreListener(new IonLoadMoreListener(){
            @Override
            public void OnLoadMoreEvent(boolean isRetry){
                mIsLoadHead=false;
                CourseCatalogFragment.super.OnLoadMoreEvent(isRetry);
            }
        });
        getListView().setLayoutManager(new LinearLayoutManager(getActivity()));
        getListView().setRecyclerAdapter(mListAdapter);
    }

    public void showAfterClass(boolean isShowAfter){
        mFilterAfterClass=isShowAfter;
        showFilter(mFilterLocal,"","");
    }

    public void showFilterLocal(boolean isShowLocal){
        mFilterLocal=isShowLocal;
        showFilter(mFilterLocal,"","");
    }

    public void showFilter(String teacherId,String teacherNames,String NodeIds,int type){
     /*   if(TextUtils.isEmpty(teacherId)&&TextUtils.isEmpty(NodeIds)&&(type==0)){//重置状态
            mFilterClassNodeId="";
            mFilterTeacherIds="";
            mFilterType=0;
            mListAdapter.setShowAfterClass(true);
            //mCurrentPreIndex=mCurrentNexIndex=0;
            mIsLoadHead=false;
            showFirstLoading();
            mListAdapter.resetDataSource(mListResponse.mLessionlist);
            mWorksListView.setCanPull(true);
            super.onRefresh();
        }else if(type!=0){
            mWorksListView.setCanPull(false);
            boolean isLocal=type>=2;
            boolean isHideAfter=type==1||type==3;
            mFilterClassNodeId=NodeIds;
            mFilterTeacherIds=teacherId;
            mListAdapter.setShowAfterClass(!isHideAfter);
            mIsLoadHead=false;
            if(isLocal)
              showFilter(isLocal,teacherNames,NodeIds);
            else {
                mListAdapter.resetDataSource(mListResponse.mLessionlist);
                showFirstLoading();
                mWorksListView.setCanPull(true);
                super.onRefresh();
            }

        }else*/


        {
            mIsLoadHead=false;
            if(null!=getEmptyLayout()&&(!"1".equals(getEmptyLayout().getTag(R.id.reuse_tag)))){
                FrameLayout.LayoutParams tmpParams= (FrameLayout.LayoutParams)getEmptyLayout().getLayoutParams();
                tmpParams.setMargins(0,DensityUtils.dp2px(getContext(),43),0,0);
                getEmptyLayout().setLayoutParams(tmpParams);
                getEmptyLayout().setTag(R.id.reuse_tag,"1");
            }
            getEmptyLayout().setTipText(null);
            getEmptyLayout().setTipText(R.string.xs_none_date);
            if(type==0){
                if(!TextUtils.isEmpty(NodeIds)&&(NodeIds.contains("_"))){
                    String[] tmpNode=NodeIds.split("_");
                    mStageNodeId=tmpNode[0];
                    if(null!=mStageFilterLayout){
                        mStageFilterLayout.setStageName(tmpNode[1]);
                    }
                }else{
                    mStageNodeId="";
                    if(null!=mStageFilterLayout){
                        mStageFilterLayout.setStageName("全部阶段");
                    }
                }
                mFilterClassNodeId="";
                mFilterTeacherIds="";
                if(null!=mStageFilterLayout){
                    mStageFilterLayout.setCatalogName("全部课程");
                    mStageFilterLayout.setTeacherName("全部老师");
                }

            }else if(type==1){
                if(!TextUtils.isEmpty(NodeIds)&&(NodeIds.contains("_"))){
                    String[] tmpNode=NodeIds.split("_");
                    mFilterClassNodeId=tmpNode[0];
                    if(null!=mStageFilterLayout){
                        mStageFilterLayout.setCatalogName(tmpNode[1]);
                    }
                }else{
                    mFilterClassNodeId="";
                    if(null!=mStageFilterLayout){
                        mStageFilterLayout.setCatalogName("全部课程");
                    }
                }
                mFilterTeacherIds="";
                if(null!=mStageFilterLayout){
                     mStageFilterLayout.setTeacherName("全部老师");
                }
             }else {
                mFilterTeacherIds=teacherId;
                if(null!=mStageFilterLayout){
                    mStageFilterLayout.setTeacherName(TextUtils.isEmpty(teacherNames) ?"全部老师":teacherNames);
                }
            }

            mListAdapter.setCanExpland(TextUtils.isEmpty(mFilterClassNodeId)? true:false);
           // mCurrentPreIndex=mCurrentNexIndex=0;
            mListAdapter.setShowAfterClass(true);
          //  mListAdapter.resetDataSource(mListResponse.mLessionlist);
            showFirstLoading();
            mWorksListView.setCanPull(true);
            super.onRefresh();
        }
    }

    private final boolean filterTeacher(String teacherIds, PurchasedCourseBean.Data filterBean){
         if(TextUtils.isEmpty(teacherIds)) return true;
         if(TextUtils.isEmpty(filterBean.teacher)) return false;
         return teacherIds.contains(filterBean.teacher);

     }

     private final boolean filterNode(String NodeIds,PurchasedCourseBean.Data filterBean){
         if(TextUtils.isEmpty(NodeIds)) return true;
         return NodeIds.contains(filterBean.parentId);
     }

    //or 或操作
    private void showFilter(boolean isShowLocal,String teacherNames,String NodeIds){

        // PrefStore.putUserSettingInt(String.valueOf(mCourserInfo.courseId),1);
         int localAdd= PrefStore.getUserSettingInt(String.valueOf(mCourseId),0);
         if(localAdd==1){
            PrefStore.putUserSettingInt(String.valueOf(mCourseId),0);
            List<DownLoadLesson> mDownedCoursewares = SQLiteHelper.getInstance().getLessonsByStatus(mCourseId, "2");
            mDownLoadMap.clear();
            for(DownLoadLesson bean :mDownedCoursewares){
                mDownLoadMap.put(StringUtils.parseInt(bean.getSubjectID()),bean);
            }
            for(PurchasedCourseBean.Data item:mListResponse.mLessionlist){
                if(item.type==2) {
                    DownLoadLesson tmpLession=mDownLoadMap.get(item.coursewareId);
                    if(tmpLession!=null){
                        item.targetPath=tmpLession.getPlayPath();
                        item.offSignalFilePath=tmpLession.getSignalFilePath();
                        item.downStatus= DownBtnLayout.FINISH;
                        mDownLoadFinishMap.put(item.coursewareId,item);
                    }
                }/*else if("1".equals(item.hasChildren)&&(!item.isExpand())){
                     //todo暂时不实现多层的情况
                    setLocalCollapseChilds(item.childs);
                }*/
            }
         }
         List<PurchasedCourseBean.Data> filterList = new ArrayList<>();
         for (PurchasedCourseBean.Data bean : mListResponse.mLessionlist) {
            //0  阶段1课程2课件	number	@mock=0
            if (bean.type == 2) {
                filterList.add(bean);
                continue;
            } else {
                  /*  if("1".equals(bean.hasChildren)&&(!bean.isExpand()))
                      getAllCollapseChilds(filterList,bean.childs);*/
            }
         }
         List<PurchasedCourseBean.Data> tmpList = new ArrayList<>();
         for (PurchasedCourseBean.Data bean : filterList) {
            if (isShowLocal) {
                if (bean.downStatus == DownBtnLayout.FINISH) {
                    if (TextUtils.isEmpty(NodeIds) && filterTeacher(teacherNames, bean))
                        tmpList.add(bean);
                    else if (TextUtils.isEmpty(teacherNames) && filterNode(NodeIds, bean)) {
                        tmpList.add(bean);
                    } else {
                        if (filterTeacher(teacherNames, bean) && filterNode(NodeIds, bean))
                            tmpList.add(bean);
                    }
                    continue;
                }
            } else {
                if (TextUtils.isEmpty(NodeIds) && filterTeacher(teacherNames, bean))
                    tmpList.add(bean);
                else if (TextUtils.isEmpty(teacherNames) && filterNode(NodeIds, bean)) {
                    tmpList.add(bean);
                } else {
                    if (filterTeacher(teacherNames, bean) && filterNode(NodeIds, bean))
                        tmpList.add(bean);
                }

            }
         }
         mListAdapter.resetDataSource(tmpList);

    }

    @Override
    protected void setListener() {
        getListView().setOnLoadMoreListener(this);
    }

    private void loadHeadList( String beforeNodeId, int limit){
        CourseApiService.getApi().getPurchasedClassSyllabusV6(StringUtils.parseLong(mCourseId),mFilterClassNodeId,"","","",
                "",beforeNodeId, limit,"",mStageNodeId, mFilterTeacherIds,0).enqueue(
                new RetrofitStatusCallbackEx<PurchaseCourseListResponse>(this) {
                    @Override
                    protected void onSuccess(Response<PurchaseCourseListResponse> response) {
                        List<PurchasedCourseBean.Data> tmplist=response.body().getListResponse();
                        formatLocalList(tmplist);
                       // mCurrentPreIndex=offset;
                        if(ArrayUtils.isEmpty(tmplist)&&(null!=mWorksListView)){
                            mWorksListView.onRefreshComplete();
                            mWorksListView.setCanPull(false);
                            getListView().setHasTopMore(false);
                            return;
                        }
                        if(null!=mWorksListView){
                            mWorksListView.resetHeader();
                            if(ArrayUtils.size(tmplist)<getLimit()) {
                                mWorksListView.setCanPull(false);
                                getListView().setHasTopMore(false);
                            }
                            //mWorksListView.setCanPull(tmplist.size()>=getLimit());
                        }

                        mListResponse.mLessionlist.addAll(0,tmplist);
                        if(null!=mListAdapter) mListAdapter.notifyDataSetChanged();
                        mAllLimit += ArrayUtils.size(tmplist);
                        setListToPosition(mListAdapter.getItemCount() - mPreSize + 1);
                        addToMusicList(false,tmplist);
                    }

                    @Override
                    protected void onFailure(String error, int type) {
                        if(null!=mWorksListView){
                            mWorksListView.onRefreshComplete();
                            ToastUtils.showShort("加载数据失败");
                        }
                    }
                });
     }

    @Override
    protected void onLoadData(int offset, int limit) {
        if(mIsLoadHead){
           loadHeadList( mListAdapter.getItem(0).id,  limit);
        }

        else{
            int needPosition=0;
            if(mFilterType!=0||!TextUtils.isEmpty(mFilterClassNodeId)||!TextUtils.isEmpty(mFilterTeacherIds)||!TextUtils.isEmpty(mStageNodeId)){
               needPosition=0;
            }else {
               needPosition=isCurrentResetMode() ?1:0;
            }

            if(null==mListAdapter) return;
            String tmpAfterId=isCurrentReMode()? "":mListAdapter.getItem(mListAdapter.getItemCount()-1).id;
            if(!TextUtils.isEmpty(mNextClassNodeId))
                tmpAfterId="";

            CourseApiService.getApi().getPurchasedClassSyllabusV6(StringUtils.parseLong(mCourseId),mFilterClassNodeId, mOnLiveLocationNodeId,
                    mNextClassNodeId,"",tmpAfterId,"", limit, "",mStageNodeId,
                                mFilterTeacherIds,needPosition).enqueue(getCallback());
        }
    }

    private void setListToPosition(int position) {
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) mWorksListView.getRefreshableView().getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(position - 1, DensityUtils.dp2px(getContext(), 60));
    }

    @Override
    public void onError(String throwable, int type) {
         //LogUtils.e("onSuccess2","err_"+throwable+","+type);
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


    //添加至服务的播放列表
    private void addToMusicList(boolean isAfter,List<PurchasedCourseBean.Data> listData){
         if(getActivity() instanceof BJRecordPlayActivity){

            BJRecordPlayActivity curActivity=(BJRecordPlayActivity)getActivity();
            if(curActivity.isDestroyed()) return;
            if(curActivity.getServiceToken()==null) return;
            CourseInfoBean courseInfo=curActivity.getCourseInfo();
            if(null==courseInfo) return;
            if(PlayManager.mService==null) return;

            List<Music> musicList=new ArrayList<>();
            int index=0;
            for(PurchasedCourseBean.Data bean:listData){
                Music curDto =CourseDataConverter.convertCoursewareToMusic(bean,courseInfo);
                if(curDto!=null){
                    musicList.add(curDto);
                }
             }
            //id -1从头部添加，-2从尾部添加
            if(!ArrayUtils.isEmpty(musicList)){
                PlayManager.play( isAfter? -2:-1,musicList,String.valueOf(mCourseId));
            }
         }
    }

    @Override
    public void onSuccess(PurchaseCourseListResponse response){
         //LogUtils.e("onSuccess2","ok_"+ArrayUtils.size(response.getListResponse())+"");
         if(isCurrentReMode()){
             if(response.data!=null){
                List<PurchasedCourseBean.Data> tmpList=response.getListResponse();
                if(!ArrayUtils.isEmpty(tmpList)){
                    if(null!=mStageFilterLayout){
                        PurchasedCourseBean.Data curBean= tmpList.get(0);
                        mStageNodeId=curBean.stageNodeId>0? String.valueOf(curBean.stageNodeId):"";
                        mStageFilterLayout.setStageName(String.valueOf(curBean.stageName));
                    }
                }
                int mCurrentNexIndex=response.data.currentPage;
                if(mCurrentNexIndex==1&&(mWorksListView!=null)) {
                    mWorksListView.setCanPull(false);
                    getListView().setHasTopMore(false);
                }
            }
            if((!TextUtils.isEmpty(mOnLiveLocationNodeId))){
                mOnLiveLocationNodeId=null;
            }
           // LogUtils.e("onSuccess",mCurrentNexIndex+"");
           // showTip();
         }else {
            // mCurrentNexIndex++;
             if((!TextUtils.isEmpty(mNextClassNodeId))){
                 mNextClassNodeId=null;
             }

            // mCurrentNexIndex=response.data.currentPage;
         }
         formatLocalList(response.getListResponse());
         super.onSuccess(response);
         if(!isCurrentReMode()){
            addToMusicList(true,response.getListResponse());
         }
         if(isCurrentReMode()&&(!mHasPostion)){
              if(mListAdapter.getLocationId()>0)
               setRelocation(mListAdapter.getLocationId());
         }
         if(isCurrentReMode()&&(ArrayUtils.size(response.getListResponse())==getLimit())){
             List<PurchasedCourseBean.Data> tmplist= response.getListResponse();
             if(tmplist.get(tmplist.size()-1).type==1)
                 if(null!=getListView()) getListView().forceTriggerLoadMore();
         }
         if (response != null && response.data != null && TextUtils.isEmpty(mNetClassName))
             mNetClassName = response.data.netClassName;
    }

    private int mDownLoadingPostion=-1;
    private String mOperSyllabusIdId=null;//
    private int mOperStageTestIndex=-1;


    private static long lastClickTime;
    private static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long step = time - lastClickTime;
        if (0 < step && step < 600) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void onItemClick(int position,View view,int type) {
        LogUtils.e("onItemClick",position+","+type);
        PurchasedCourseBean.Data  item = mListAdapter.getItem(position);
        if(null==item) return;
         switch (type) {
             case EventConstant.EVENT_RELOAD://加载子元素

                 PurchasedCourseBean.Data  preitem = mListAdapter.getItem(position-1);
                 if(null==preitem) return;
                 loadMoreChildByParent(item,preitem.id,preitem.parentId,position,preitem.type==1);
                 break;
             case EventConstant.EVENT_MORE:
                 item = mListAdapter.getItem(position);
                  if(item.isClosed()){
                     AnimUtils.showOpenRotation(view);
                  }else {
                    // LogUtils.e("onItemClick",view.getRotation()+"");
                     AnimUtils.showCloseRotation(view);
                 }
                 loadChild(item,position);
               // doCollectAction(view, String.valueOf(item.id), position);
                break;

            case EventConstant.EVENT_ALL:
                //防止重复快速点击播放
                if(isFastDoubleClick()) return;
                //录播更新大纲列表学习进度
                 if(getActivity() instanceof BJRecordPlayActivity){
                     int playTime= (int)((BJRecordPlayActivity)getActivity()).getDanmaCurrentTime();
                     mListAdapter.refreshLastPlayTime(playTime);
                 }
                 //liveStatus	    ;//直播状态0未开始1进行中2已结束
                 if(item.videoType==2&&item.liveStatus==2){

                     String tipStr=item.isPlayback==1?"本节直播课已结束，图图正在努力上传回放中，敬请期待！"
                                                     :"本节直播课已结束，下次早点来哦！";

                     DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {  }
                     }, "", tipStr, "", "确定");
                     return;
                  }
                 mListAdapter.setSelectIndex(position);

                //有学习报告
                if(item.studyReport==1){
                   mOperStageTestIndex=position;
                   mOperSyllabusIdId=item.id;
                }

                ((OnCoursePlaylistener)getActivity()).onSelectPlayClick(item,true);
                 //UIHelper.showProductDetail(getContext(), String.valueOf(item.id), 1);
                 break;
             case EventConstant.EVENT_LIKE:
                 mDownLoadingPostion=position;
                 CommonUtils.checkPowerAndTraffic(getActivity(), new Action1<Boolean>() {
                     @Override
                     public void call(Boolean aBoolean) {
                         if(mDownLoadingPostion==-1) return;
                         PurchasedCourseBean.Data  item = mListAdapter.getItem(mDownLoadingPostion);
                         if(item==null) return;
                         if(item.videoType==1){
                             if(TextUtils.isEmpty(item.token)||TextUtils.isEmpty(item.videoId)){
                                 ToastUtils.showShort("下载数据出错，请提交至意见反馈~");
                                 return;
                             }
                         }
                         if(item.videoType==3){
                            if(TextUtils.isEmpty(item.token)||TextUtils.isEmpty(item.bjyRoomId)){
                                ToastUtils.showShort("下载数据出错，请提交至意见反馈~");
                                return;
                            }
                         }
                         item.downStatus=DownBtnLayout.DOWNLOADING;
                         mListAdapter.notifyItemChanged(mDownLoadingPostion);
                         if(ArrayUtils.isEmpty(mDownLoadinglist)){
                              DownLoadAssist.getInstance().stopAll(false);
                         }
                         mDownLoadinglist.add(item);
                         startDown(item);
                     }
                 });
                 break;
             case  EventConstant.EVENT_COMMENT://课后作业
                 PurchasedCourseBean.AnswerCard curAnsCard=item.answerCard;
                 mFcount=curAnsCard.fcount;
                 showAfterClassExam(item,position,(curAnsCard!=null?curAnsCard.status:-1),(curAnsCard!=null? curAnsCard.id:0));
                 break;
             case  EventConstant.EVENT_COLLECT://学习报告
                 if(item.reportStatus<=0){
                     ToastUtils.showShort("完成学习任务 查看学习报告~");
                     return;
                 }
                 VideoStudyReportFragment.lanuch(getContext(), mCourseId, item, mNetClassName);
                 break;
             case EventConstant.SELECT_LESSON://阶段测试
                  if(item.testStatus==1){//1未开始
                      DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {  }
                      }, "", "学完再练习，测试更精准\n考试入口暂未开启", "", "知道了");
                      return;
                  }else if(item.testStatus==6) {// 6查看报告
                   //  StageReportActivity.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, extraArgs);
                     showStageTest(String.valueOf(item.coursewareId),item.id,String.valueOf(item.classId),item.title);
                 }else {
                     if((item.testStatus==2)){//开始考试
                         mOperStageTestIndex=position;
                         mOperSyllabusIdId=item.id;
                         checkAnswerCardExsit(String.valueOf(item.coursewareId),item.id,(item.isEffective==1)&&(item.isExpired==0),String.valueOf(item.classId),item.title);
                        return;
                     }
                     //5继续考试
                     mOperStageTestIndex=position;
                     mOperSyllabusIdId=item.id;
                     showStageTest(String.valueOf(item.coursewareId),item.id,String.valueOf(item.classId),item.title);
                 }
                 break;
         }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //刷新阶段测试状态
        if((requestCode==2003||requestCode==2014)&&mOperStageTestIndex!=-1&&(null!=mListAdapter)){
            int curIndex=mOperStageTestIndex;
            mOperStageTestIndex=-1;
            PurchasedCourseBean.Data curItem= mListAdapter.getItem(curIndex);
            if(null!=curItem){
                if((!TextUtils.isEmpty(mOperSyllabusIdId))&&curItem.id.equals(mOperSyllabusIdId)){
                    int curStatus= PrefStore.getSettingInt(ArgConstant.KEY_ID+mOperSyllabusIdId,curItem.testStatus);
                    PrefStore.removeSettingkey(ArgConstant.KEY_ID+mOperSyllabusIdId);

                    if(curStatus!=curItem.testStatus){
                        curItem.testStatus=curStatus;
                        mListAdapter.notifyItemChanged(curIndex);
                    }
                }
            }
        }
    }

    private void checkAnswerCardExsit(final String paperId, final String syllabusId,final boolean hasAlert,final String classId,final String classTitle){
        ((SimpleBaseActivity)getActivity()).showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().checkhasAnswerCard(paperId,syllabusId),
                new NetObjResponse<Long>() {
                    @Override
                    public void onSuccess(BaseResponseModel<Long> model) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();
                        if(model.data>-1){//有答题卡 继续做答
                            Bundle bundle = new Bundle();
                            bundle.putLong("point_ids",  StringUtils.parseLong(paperId));
                            bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(bundle,classId,classTitle);
                            ArenaExamActivityNew.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                        }else {
                            if(!hasAlert){
                                Bundle bundle = new Bundle();
                                bundle.putLong("paperId", StringUtils.parseLong(paperId));
                                bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId) );
                                formatBundle(bundle,classId,classTitle);
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
                                    formatBundle(bundle,classId,classTitle);
                                    SmallMatchActivity.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                                }
                            }, "", "按时完成考试的你在考试结束后将会收到一份完备评测报告", "", "确定");
                        }
                    }

                    @Override
                    public void onError(String message, int type) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();
                        ToastUtils.showShort("请求出错");
                    }
                });
    }

    private void formatBundle(Bundle  extraArgs,String classId,String classTitle){
        if(null==extraArgs) return;
        String courseName="" ;
        if(getActivity() instanceof BJRecordPlayActivity&&(!(Method.isActivityFinished(getActivity())))){
            CourseInfoBean curWareInfo= ((BJRecordPlayActivity)getActivity()).getCourseInfo();
            courseName=curWareInfo==null?"":curWareInfo.title;
        }
        extraArgs.putString("course_id",mCourseId);
        extraArgs.putString("course_title",courseName);
        extraArgs.putString("class_id",classId);
        extraArgs.putString("class_title",classTitle);
    }

    private void showStageTest(final String paperId, final String syllabusId,final String classId,final String classTitle){
        ((SimpleBaseActivity)getActivity()).showProgress();


        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().createPeriodTestInfo(paperId,syllabusId),
                new NetObjResponse<PeriodTestBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<PeriodTestBean> model) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();
                        if(model.data.status==3){//
                            Bundle  extraArgs=new Bundle();
                            extraArgs.putLong("practice_id",StringUtils.parseLong(model.data.practiceId));
                            extraArgs.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(extraArgs,classId,classTitle);

                            StageReportActivity.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, extraArgs);
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putLong("point_ids",  StringUtils.parseLong(paperId));
                            bundle.putLong("syllabusId",StringUtils.parseLong(syllabusId));
                            formatBundle(bundle,classId,classTitle);
                            ArenaExamActivityNew.showForResult(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
                         }
                    }

                    @Override
                    public void onError(String message, int type) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();
                        ToastUtils.showShort("请求出错");
                    }
                });
    }

    //playingCourseWare
    @Override
    public  void onAfterViewClick(boolean isStart, CourseWareInfo playingCourseWare){
        if(isStart){
            if(playingCourseWare instanceof PurchasedCourseBean.Data){

                PurchasedCourseBean.Data item=(PurchasedCourseBean.Data)playingCourseWare;
                PurchasedCourseBean.AnswerCard curAnsCard=item.answerCard;
                mFcount=curAnsCard.fcount;
                showAfterClassExam(item,-1,(curAnsCard!=null?curAnsCard.status:-1),(curAnsCard!=null? curAnsCard.id:0));
            }else {
                showAfterClassExam(playingCourseWare,-1,-1,0);
            }
            // showAfterExam(playingCourseWare.coursewareId,playingCourseWare.videoType,playingCourseWare.id,playingCourseWare.classId,playingCourseWare.title);
        }
        /*else {
            if(playingCourseWare==null){
                PurchasedCourseBean.Data item=  mListAdapter.getNextCoursWare();
                if(item!=null) {
                    ((BJRecordPlayActivity)getActivity()).onSelectPlayClick(item,true);
                }
            }

        }*/
    }

    private int mFcount=0;//申论多个单题列表，完成数
    private void showAfterClassExam(CourseWareInfo item,int position,int answerCardStatus,long answerCardId){
        //申论
        if((item.subjectType==2)){
            mOperStageTestIndex=position;
            boolean isList=(item.buildType==0)&&(item.afterCoreseNum>1);
            if(isList&&(answerCardStatus==3)){
                HomeworkSingleListActivity.showListHomework(getActivity(),mCourseId,item.coursewareId,item.id,item.videoType);
                return;
            }
            if(answerCardStatus== EssayAfterStatusEnum.INIT.getValue()
                    ||answerCardStatus==EssayAfterStatusEnum.UNFINISHED.getValue()
                    || answerCardStatus==EssayAfterStatusEnum.CORRECT_RETURN.getValue()){//空白  "未完成"  //被退回

                mOperStageTestIndex=position;
                showEssayAfterExam(item.videoType,item.coursewareId,item.id,answerCardStatus,answerCardId,isList);
            }
            else if((answerCardStatus==3)||(answerCardStatus==-1)){//已批改  或者暂时找不到答题卡信息
                showEssayAfterExam(item.videoType,item.coursewareId,item.id,answerCardStatus,answerCardId,isList);
            }else if(answerCardStatus==EssayAfterStatusEnum.CORRECTING.getValue()
                    ||answerCardStatus==EssayAfterStatusEnum.COMMIT.getValue()){
                //ToastUtils.showEssayToast("老师正在仔细批改中，请耐心等候！");
                showEssayAfterExam(item.videoType,item.coursewareId,item.id,answerCardStatus,answerCardId,isList);
            }

            return;
        }
        else if((answerCardStatus==3)&&(answerCardId>0)){//行测

            String courseName="" ;
            if(getActivity() instanceof BJRecordPlayActivity&&(!(Method.isActivityFinished(getActivity())))){
                CourseInfoBean curWareInfo= ((BJRecordPlayActivity)getActivity()).getCourseInfo();
                courseName=curWareInfo==null?"":curWareInfo.title;
            }
            CourseAfterWorkReportFragment.lanuch(getContext(),answerCardId,mCourseId,courseName,String.valueOf(item.classId),item.title);
            return;
        }
        showAfterExam(item.coursewareId,item.videoType,item.id,item.classId,item.title);
    }

    private void showEssayAfterExam(final int videoType, final long courseWareId, final String syllabusId, final int curStatus, final long answerId, final boolean isList){
      /*  */
        ((SimpleBaseActivity)getActivity()).showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getEssayPagerInfo(videoType,courseWareId,answerId,syllabusId),
                new NetObjResponse<InClassEssayCardBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<InClassEssayCardBean> model) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();         //0 套题

                         int realCurStatus=model.data.bizStatus;
                         long recentAnswarCardId=model.data.answerCardId;
                         if(realCurStatus!=curStatus||(model.data.fcount!=mFcount)||(recentAnswarCardId!=answerId)){
                             mFcount=0;
                            if(mOperStageTestIndex!=-1){
                                int position=mOperStageTestIndex;
                                mOperStageTestIndex=-1;
                                PurchasedCourseBean.Data  item = mListAdapter.getItem(position);
                                if(null!=item) {
                                    if(item.coursewareId==courseWareId){
                                        item.answerCard.status=model.data.bizStatus;
                                        item.answerCard.fcount=model.data.fcount;
                                        item.answerCard.id=recentAnswarCardId;
                                        mListAdapter.notifyItemChanged(position);
                                    }
                                }
                            }
                        }
                        if(isList){
                            HomeworkSingleListActivity.showListHomework(getActivity(),mCourseId,courseWareId,syllabusId,videoType);
                        }else {

                            EssayRoute.showEssayHomework(getActivity(),model.data,mCourseId,courseWareId,syllabusId,realCurStatus,recentAnswarCardId,videoType);
                        }
                     }

                    @Override
                    public void onError(String message, int type) {
                         ((SimpleBaseActivity)getActivity()).hideProgess();
                        ToastUtils.showShort("请求出错");
                    }
                });
    }

    private void showAfterExam(final long lessionId,final int videoType,String syllabusId,final int classId,final String courseWareName){
        ((SimpleBaseActivity)getActivity()).showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().createAfterPracticesInfo(lessionId,videoType,mCourseId,syllabusId),
                new NetObjResponse<InClassAnswerCardBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<InClassAnswerCardBean> model) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();
                        boolean hasFinish=model.data.status==3;
                        String courseName="" ;
                        if(getActivity() instanceof BJRecordPlayActivity&&(!(Method.isActivityFinished(getActivity())))){
                            CourseInfoBean curWareInfo= ((BJRecordPlayActivity)getActivity()).getCourseInfo();
                            courseName=curWareInfo==null?"":curWareInfo.title;
                        }

                        if(hasFinish){
                            CourseAfterWorkReportFragment.lanuch(getContext(),StringUtils.parseLong(model.data.id),mCourseId
                                                                 ,courseName,String.valueOf(classId),courseWareName);
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putLong("practice_id",StringUtils.parseLong(model.data.id));
                        bundle.putBoolean("continue_answer",true);

                        bundle.putString(ArgConstant.COURSE_ID,mCourseId);
                        bundle.putString(ArgConstant.COURSE_NAME,courseName);
                        bundle.putString(ArgConstant.TYPE_ID,String.valueOf(classId));
                        bundle.putString(ArgConstant.TITLE,courseWareName);
                      //  bundle.putBoolean("show_statistic",hasFinish);
                        ArenaExamActivityNew.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_COURSE_EXERICE, bundle);

                        StudyCourseStatistic.sendStartAfterWork(mCourseId,courseName,String.valueOf(classId),courseWareName);
                      //   ArenaExamActivity.show(getActivity(),0, bundle);
                   }

                    @Override
                    public void onError(String message, int type) {
                        ((SimpleBaseActivity)getActivity()).hideProgess();
                        ToastUtils.showShort("请求出错");
                    }
                });
    }

    private CourseInfoForStatistic mTrackCourseInfo;
    private void sendDownTrack(String classId,final CourseWareInfo item){
        if(mTrackCourseInfo!=null){
            StudyCourseStatistic.sendDownloadCourseTrack(mTrackCourseInfo,item,true);
            return;
        }
        ServiceProvider.getSensorsStatistic(classId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                CourseInfoForStatistic courseInfoForStatistic = (CourseInfoForStatistic) model.data;
                mTrackCourseInfo=courseInfoForStatistic;
                StudyCourseStatistic.sendDownloadCourseTrack(courseInfoForStatistic,item,true);
           }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void startDown(PurchasedCourseBean.Data item){

        CourseInfoBean tmpCourse=null;
        if(getActivity() instanceof BJRecordPlayActivity){
            tmpCourse=((BJRecordPlayActivity)getActivity()).getCourseInfo();
        }
          if(tmpCourse==null) {
            ToastUtils.showShort("选择出错");
            return;
        }
        VideoStatisticsUtil.reportDownloadEvent(mCourseId,StringUtils.parseInt(item.id));
        sendDownTrack(mCourseId,item);

        LogUtils.e("startDown",tmpCourse==null?"": GsonUtil.GsonString(tmpCourse));
        DownLoadCourse downLoadCourse= CourseDataConverter.convertCatalogInfoListToDownCourse(tmpCourse,item);

        final String  imgDataPath = FileUtil.getDownloadCourseImagePath(Md5Util.toMD5(downLoadCourse.getCourseName()));//加密一次防止出现关键字"/"
        if(!FileUtil.isFileExist(imgDataPath)){
           ImageLoad.downloadPhotoCover(downLoadCourse.getImageURL(),new MyPreloadTarget(imgDataPath) {
                @Override
                public void onDownFinished(boolean isSuccess, String filePath) {
                    try {
                        IoExUtils.copyFile(new File(filePath), new File(imgDataPath), false);
                    } catch (IOException e) {  }
                }
            });
        }
        downLoadCourse.setImagePath(imgDataPath);
        for(DownLoadLesson bean:downLoadCourse.getLessonLists()){
            bean.setImagePath(imgDataPath);
        }

        LogUtils.e("startDown2", GsonUtil.GsonString(downLoadCourse));

        if(!ArrayUtils.isEmpty(downLoadCourse.getLessonLists())){
            DownLoadLesson downloadLession=downLoadCourse.getLessonLists().get(0);
            SQLiteHelper.getInstance().insertDB(downLoadCourse,downloadLession);
            DownLoadAssist.getInstance().addDownload(downloadLession,ArrayUtils.size(mDownLoadinglist)==1?true:false);//
        }
   }


   @Override
   public void onDLProgress(String s, int percent,long speed){  }

    @Override
    public void onDLError(String s, int errorCode){ }

    @Override
    public void onDeleteDownFile(String downloadId, final String subjectId){
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                int delCourseWareId=StringUtils.parseInt(subjectId);
                PurchasedCourseBean.Data item=mDownLoadFinishMap.get(delCourseWareId);
                if(null!=item){
                    item.downStatus=DownBtnLayout.NORMAL;
                    mListAdapter.notifyDataSetChanged();
                }
           }
        });
    }

    @Override
    public void onDLFinished(final String downID){
         Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                PurchasedCourseBean.Data findBean=null;
                for(PurchasedCourseBean.Data item:mDownLoadinglist){
                    if(downID.equals(item.getDownloadId())){
                       if(mDownLoadinglist!=null){
                          DownLoadLesson finshLession= SQLiteHelper.getInstance().getCourseWare(downID);
                          if(finshLession!=null){
                              mDownLoadMap.put(item.coursewareId,finshLession);
                              item.targetPath=finshLession.getPlayPath();
                              item.offSignalFilePath=finshLession.getSignalFilePath();
                              item.downStatus= DownBtnLayout.FINISH;
                              mDownLoadFinishMap.put(item.coursewareId,item);
                          }
                       }
                      findBean=item;
                      mListAdapter.notifyDataSetChanged();
                      EventBus.getDefault().post(item);
                      break;
                    }
                }
                if(null!=findBean)  mDownLoadinglist.remove(findBean);
            }
        });

    }

    @Override
    public void onDLPrepare(String downID){
         LogUtils.e("onDLPrepare",downID+"");
    }

    @Override
    public void onDLStop(String key,boolean keepWaiting){

    }

    @Override
    public void onDLFileStorage(String key, long space){
        LogUtils.e("onDLFileStorage",key+","+space);
    }





    private void getAllCollapseChilds(List<PurchasedCourseBean.Data> filterList,List<PurchasedCourseBean.Data> childs) {
/*
        if(ArrayUtils.isEmpty(childs)) return ;
        for (int i = 0; i < childs.size(); i++) {
            PurchasedCourseBean.Data treeAdapterItem = childs.get(i);
            if(treeAdapterItem.type==2){
                filterList.add(treeAdapterItem);
                continue;
            }
            if(treeAdapterItem.hasChildren.equals("1")) {
                List list = treeAdapterItem.childs;
                if (list != null && list.size() > 0) {
                    filterList.addAll(list);
                }
            }
        }*/
    }

    private void setLocalCollapseChilds(List<PurchasedCourseBean.Data> childs) {

       /* if(ArrayUtils.isEmpty(childs)) return ;
        for (int i = 0; i < childs.size(); i++) {
             PurchasedCourseBean.Data treeAdapterItem = childs.get(i);
            if(treeAdapterItem.type==2){
                DownLoadLesson tmpLession=mDownLoadMap.get(treeAdapterItem.coursewareId);
                if(tmpLession!=null){
                    treeAdapterItem.targetPath=tmpLession.getPlayPath();
                    treeAdapterItem.offSignalFilePath=tmpLession.getSignalFilePath();
                    treeAdapterItem.downStatus= DownBtnLayout.FINISH;
                    mDownLoadFinishMap.put(treeAdapterItem.coursewareId,treeAdapterItem);
                }
                continue;
            }
            if(treeAdapterItem.hasChildren.equals("1")) {
                List<PurchasedCourseBean.Data> list = treeAdapterItem.childs;
                if (list != null && list.size() > 0) {

                    for(PurchasedCourseBean.Data bean :list)
                    if(bean.type==2){
                        DownLoadLesson tmpLession=mDownLoadMap.get(bean.coursewareId);
                        if(tmpLession!=null){
                            bean.targetPath=tmpLession.getPlayPath();
                            bean.offSignalFilePath=tmpLession.getSignalFilePath();
                            bean.downStatus= DownBtnLayout.FINISH;
                            mDownLoadFinishMap.put(bean.coursewareId,bean);
                        }
                     }
                 }
            }
        }*/
    }

    private List<PurchasedCourseBean.Data> getAllCollapseChilds(List<PurchasedCourseBean.Data> childs) {
        ArrayList<PurchasedCourseBean.Data> treeAdapterItems = new ArrayList<>();
        if(ArrayUtils.isEmpty(childs)) return treeAdapterItems;
        for (int i = 0; i < childs.size(); i++) {
            PurchasedCourseBean.Data treeAdapterItem = childs.get(i);
            treeAdapterItems.add(treeAdapterItem);

          /*  if (treeAdapterItem.hasChildren.equals("1")) {
                treeAdapterItem.setExpand(false);
                List list = treeAdapterItem.childs;

                if (list != null && list.size() > 0) {

                    treeAdapterItems.addAll(list);
                }
            }*/
        }
        return treeAdapterItems;
       // return null;
    }


    private void formatLocalList(List<PurchasedCourseBean.Data> curList){
        if(mDownLoadMap.size()>0){
           // Boolean testFlag=true;
           // String parentId="";
            for(PurchasedCourseBean.Data item:curList){
                if(item.type==2) {//.equals("0")表示是课件类型，非目录
                    DownLoadLesson tmpLession=mDownLoadMap.get(item.coursewareId);
                    if(tmpLession!=null){
                        item.targetPath=tmpLession.getPlayPath();
                        item.offSignalFilePath=tmpLession.getSignalFilePath();
                        item.downStatus= DownBtnLayout.FINISH;
                        mDownLoadFinishMap.put(item.coursewareId,item);
                    }

                  /*  if(testFlag){
                        testFlag=false;
                        parentId=item.parentId;
                    }*/
                }
            }

          /* if(!TextUtils.isEmpty(parentId)){
               PurchasedCourseBean.Data tmpData=new PurchasedCourseBean.Data();
               tmpData.parentId=parentId;
               tmpData.videoType=4;
               curList.add(tmpData);
           }*/
         }
    }

    private void loadMoreChildByParent(final PurchasedCourseBean.Data curItem,String preId, String parentNodeId,final int postion,boolean isCatalog){
        ((SimpleBaseActivity)getActivity()).showProgress();


        CourseApiService.getApi().getPurchasedClassSyllabusV6(StringUtils.parseLong(mCourseId), "","","",isCatalog?"0":preId,"","",
                getLimit(),isCatalog?preId:parentNodeId,mStageNodeId,mFilterTeacherIds,0).enqueue(new RetrofitStatusCallbackEx<PurchaseCourseListResponse>(this) {
            @Override
            protected void onSuccess(Response<PurchaseCourseListResponse> response) {
                ((SimpleBaseActivity)getActivity()).hideProgess();
                List<PurchasedCourseBean.Data> curList=response.body().getListResponse();

                //mCurrentNexIndex++;
                formatLocalList(curList);

                int lastChildIndex=ArrayUtils.size(curItem.childs)-1;
                if(ArrayUtils.size(curList)<getLimit()){//如果已加载完  移出更多按钮()
                    curItem.childs.remove(lastChildIndex);
                    mListAdapter.removeAt(postion,curItem);
                }
                curItem.childs.addAll(lastChildIndex,curList);
                mListAdapter.addAllAt(postion , curList);
            }

            @Override
            protected void onFailure(String error, int type) {
                ((SimpleBaseActivity)getActivity()).hideProgess();
                ToastUtils.showShort("加载数据出错~");
            }
        });
    }


    private void loadChild(final PurchasedCourseBean.Data curItem,final int postion){
        if(!curItem.isClosed()){
           // List<PurchasedCourseBean.Data> tmplist= getAllCollapseChilds(curItem.childs);

            if(!ArrayUtils.isEmpty(curItem.childs)){
                mListAdapter.removeAllAt(postion+1,curItem.childs);
                curItem.setClosed(true);
            }else {
                ArrayList<PurchasedCourseBean.Data> treeAdapterItems = new ArrayList<>();
                String curParentId=curItem.id;
                boolean isEndList=true;
                for(int i=(postion+1);i<mListAdapter.getItemCount();i++){
                    PurchasedCourseBean.Data curBean=mListAdapter.getItem(i);
                    if(curParentId.equals(curBean.parentId)){
                       treeAdapterItems.add(curBean);
                    }else {
                        isEndList=false;
                        break;
                    }
                }
                //如果是最后一个目录，正在加载更多，同时点击收起就返回
                if(isEndList&&(null!=getListView())){//
                   if(getListView().isLoadingMore()){
                        ToastUtils.showShortToast(null,"正在加载中~~");
                        return;
                   }
                }
                if(isEndList){//插入一条更多按钮

                    ArrayList<PurchasedCourseBean.Data> moreAdapterItems = new ArrayList<>();
                    moreAdapterItems.addAll(treeAdapterItems);

                    PurchasedCourseBean.Data tmpData=new PurchasedCourseBean.Data();
                    tmpData.parentId=curParentId;
                    tmpData.type=3;
                    moreAdapterItems.add(tmpData);

                    curItem.childs=moreAdapterItems;

                    tmpData.childs=moreAdapterItems;//用更多的按钮，指向子项集合
                    mListAdapter.removeAllAt(postion+1,treeAdapterItems);

                    //((curItem.nextClassNodeId>0)||(ArrayUtils.size(treeAdapterItems)>=getLimit()))
                    //没有下个节点时禁止加载更多
                    if((curItem.nextClassNodeId<=0)){
                        if(null!=getListView()) getListView().checkloadMore(false);
                    }
                }else {
                    curItem.childs=treeAdapterItems;
                    mListAdapter.removeAllAt(postion+1,treeAdapterItems);
                }


                curItem.setClosed(true);
                if(isEndList&&(curItem.nextClassNodeId>0)&&(TextUtils.isEmpty(mFilterClassNodeId))){
                    mNextClassNodeId=String.valueOf(curItem.nextClassNodeId);
                    if(null!=getListView()) getListView().forceTriggerLoadMore();
                }
             }
        }else {//展开
            if (!ArrayUtils.isEmpty(curItem.childs)) {
                curItem.setClosed(false);

                boolean isLastPostion = postion == (mListAdapter.getItemCount() - 1);
                mListAdapter.addAllAt(postion + 1, curItem.childs);
                if (isLastPostion) {
                    mWorksListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getListView().smoothScrollToPosition(postion + 1);
                        }
                    }, 500);
                }
                return;
            }
            //((SimpleBaseActivity)getActivity()).showProgress();
           /* CourseApiService.getApi().getPurchasedClassSyllabus(StringUtils.parseLong(mCourseId), 1, 300, StringUtils.parseInt(curItem.id))
                    .enqueue(new RetrofitStatusCallbackEx<PurchaseCourseListResponse>(this) {
                        @Override
                        protected void onSuccess(Response<PurchaseCourseListResponse> response) {
                            ((SimpleBaseActivity)getActivity()).hideProgress();
                            if(curItem.childs==null){
                                curItem.childs=new ArrayList<>();
                            }

                            List<PurchasedCourseBean.Data> tmplist=response.body().getListResponse();
                            if(mDownLoadMap.size()>0){
                                for(PurchasedCourseBean.Data item:tmplist){
                                    if(item.hasChildren.equals("0")) {
                                        DownLoadLesson tmpLession=mDownLoadMap.get(item.coursewareId);
                                        if(tmpLession!=null){
                                            item.targetPath=tmpLession.getPlayPath();
                                            item.offSignalFilePath=tmpLession.getSignalFilePath();
                                            item.downStatus= DownBtnLayout.FINISH;
                                            mDownLoadFinishMap.put(item.coursewareId,item);
                                        }
                                    }
                                }
                            }

                            curItem.setExpand(true);
                            curItem.childs.addAll(tmplist);
                            mListAdapter.addAllAt(postion+1,tmplist);
                        }

                        @Override
                        protected void onFailure(String error, int type) {
                            ((SimpleBaseActivity)getActivity()).hideProgress();
                        }
                    });*/
          /* }*/
      }
    }

    public SparseArray<DownLoadLesson> getDownLoadMap() {
        return mDownLoadMap;
    }


    public void refreshLearnReportStatus(int videoType,String syllabusIdId,int courseWareId){

        if (null != mListAdapter) {
            PurchasedCourseBean.Data curData = mListAdapter.getPlayingItem();
            final int playIndex=mListAdapter.getPlayingIndex();
            //会出现目录折叠的情况   暂时不更新折叠数据

            if (null != curData && syllabusIdId.equals(curData.id) && curData.studyReport == 1) {
                ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getLearnReportStatus(videoType, courseWareId),
                        new NetObjResponse<ReportIntBean>() {
                            @Override
                            public void onSuccess(BaseResponseModel<ReportIntBean> model) {
                                if(null!=mListAdapter){
                                    PurchasedCourseBean.Data curData =  mListAdapter.getItem(playIndex);
                                    if(curData!=null&&(curData.studyReport==1)){
                                        curData.reportStatus=model.data.reportStatus;
                                        mListAdapter.notifyItemChanged(playIndex);
                                    }
                                }
                            }

                            @Override
                            public void onError(String message, int type) { }
                        });

            }
        }





    }

}

