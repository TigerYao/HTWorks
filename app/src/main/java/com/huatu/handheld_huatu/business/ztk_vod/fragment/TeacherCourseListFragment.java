package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseTeacherLessionsAdapter;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.PagePullToRefreshStatusBlock;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.base.fragment.IPageStripTabInitData;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.TeacherTimeTable;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\12\30 0030.
 * 双师课表
 */

public class TeacherCourseListFragment extends AbsSettingFragment implements
        ScrollableHelper.ScrollableContainer,OnRecItemClickListener, IPageStripTabInitData {


    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mLoadingView;

    private String mCourseId = "";
    private boolean mIsLocal=false;

    @Override
    public View getScrollableView(){
        return mWorksListView;
    }

    @Override
    public int getContentView() {
        return R.layout.comm_recyclerlist_nopull_fragment;
    }

    PagePullToRefreshStatusBlock<SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo>> mPageBlock;
    CourseTeacherLessionsAdapter mListAdapter;

    public static TeacherCourseListFragment getInstance(String courseId,boolean isLocal) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putBoolean(ArgConstant.IS_LOCAL_VIDEO,isLocal);
        TeacherCourseListFragment tmpFragment = new TeacherCourseListFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    @Override
    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mIsLocal=args.getBoolean(ArgConstant.IS_LOCAL_VIDEO);
    }

    @Override
    protected void setListener() {

        mLoadingView.setStatusStringId(R.string.xs_loading_text, R.string.xs_none_date);
        mLoadingView.setTipText(R.string.xs_my_empty);
        mLoadingView.setEmptyImg(R.drawable.down_no_num);

        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPageBlock=new PagePullToRefreshStatusBlock<>();
        mPageBlock.setLimit(1000000);
        mPageBlock.setListView(mWorksListView);
        mPageBlock.setloadingView(mLoadingView);

        SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo> response= new SimpleListResponse<>();
        response.mAdapterList = new ArrayList<>();
        mPageBlock.mListResponse=response;

        mPageBlock.setTypeFactory(new PagePullToRefreshStatusBlock.TypeFactory<SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo>>(){

            @Override
            public Observable<SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo>> getListObservable(int pageIndex, int limit){
                return CourseApiService.getApi().getTeacherTimeTable(mCourseId)
                        .map(new Func1<BaseListResponseModel<TeacherTimeTable>, SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo>>() {
                    @Override
                    public SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo> call(BaseListResponseModel<TeacherTimeTable> listResponse) {
                        SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo> response= new SimpleListResponse<>();
                        response.code=listResponse.code;
                        response.message=listResponse.message;
                        response.list=new ArrayList<>();
                        if(listResponse.data!=null&&(!ArrayUtils.isEmpty(listResponse.data))){

                            for(int i=0;i<listResponse.data.size();i++){
                                TeacherTimeTable groupInfo=listResponse.data.get(i);
                                response.list.add(groupInfo.toHeadBean());

                                if(!ArrayUtils.isEmpty(groupInfo.timeTableList)){
                                    for(int k=0;k<groupInfo.timeTableList.size();k++){
                                        TeacherTimeTable.OneTimeTable bean=groupInfo.timeTableList.get(k);

                                        if(!ArrayUtils.isEmpty(bean.lessonTimeList)){

                                             for(TeacherTimeTable.LessonTime lessionTime:bean.lessonTimeList){

                                                 TeacherTimeTable.TeacherTimeTypeInfo typeInfo=new TeacherTimeTable.TeacherTimeTypeInfo();
                                                 typeInfo.type=1;
                                                 typeInfo.lessonDate=bean.lessonDate;
                                                 typeInfo.lessonTableDetailId=bean.lessonTableDetailId;
                                                 typeInfo.lessonTableId=groupInfo.lessonTableId;//.l
                                                 typeInfo.teacherName=bean.teacherName;
                                                 typeInfo.startTime=lessionTime.startTime;
                                                 typeInfo.endTime=lessionTime.endTime;

                                                 response.list.add(typeInfo);
                                             }
                                        }
                                   }
                                }
                            }
                        }
                        return response;
                    }
                });
            }

            @Override
            public RecyclerView.Adapter getAdapter(SimpleListResponse<TeacherTimeTable.TeacherTimeTypeInfo> response){
                mListAdapter = new CourseTeacherLessionsAdapter(getContext(), response.mAdapterList);
                mListAdapter.setOnViewItemClickListener(TeacherCourseListFragment.this);
                return mListAdapter;
            }

            @Override
            public CompositeSubscription getComSubscription(){
                return getSubscription();
            }
        });
        mPageBlock.build();
        // block.onFirstLoad();
    }


    @Override
    public void onStripTabRequestData() {
        mPageBlock.onFirstLoad();
    }

    private TeacherTimeTable.TeacherTimeTypeInfo mCurrentItem ;
    private int mCurrentPostion;
    @Override
    public void onItemClick(int position,View view,int type) {
        mCurrentPostion=position;
        mCurrentItem = mListAdapter.getItem(position);
        if(null==mCurrentItem) return;
        switch (type) {

            case EventConstant.EVENT_MORE:
                mCurrentItem = mListAdapter.getItem(position);
                if(mCurrentItem.isClosed()){
                    AnimUtils.showOpenRotation(view);
                }else {
                    // LogUtils.e("onItemClick",view.getRotation()+"");
                    AnimUtils.showCloseRotation(view);
                }
                loadChild(mCurrentItem,position);
                // doCollectAction(view, String.valueOf(item.id), position);
                break;
        }
    }

    private void loadChild(TeacherTimeTable.TeacherTimeTypeInfo curItem, final int postion){
        if(!curItem.isClosed()){
            // List<PurchasedCourseBean.Data> tmplist= getAllCollapseChilds(curItem.childs);

            if(!ArrayUtils.isEmpty(curItem.childs)){
                mListAdapter.removeAllAt(postion+1,curItem.childs);
                curItem.setClosed(true);
            }else {
                ArrayList<TeacherTimeTable.TeacherTimeTypeInfo> treeAdapterItems = new ArrayList<>();
                String curParentId=curItem.lessonTableId;
                boolean isEndList=true;
                for(int i=(postion+1);i<mListAdapter.getItemCount();i++){
                    TeacherTimeTable.TeacherTimeTypeInfo curBean=mListAdapter.getItem(i);
                    if(curParentId.equals(curBean.lessonTableId)){
                        treeAdapterItems.add(curBean);
                    }else {
                        isEndList=false;
                        break;
                    }
                }

                curItem.childs=treeAdapterItems;
                mListAdapter.removeAllAt(postion+1,treeAdapterItems);
                curItem.setClosed(true);
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
                            if(null!=mWorksListView){
                                mWorksListView.smoothScrollToPosition(postion + 1);
                            }
                        }
                    }, 500);
                }
            }
        }
    }


}
