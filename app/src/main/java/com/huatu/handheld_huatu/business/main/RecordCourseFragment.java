package com.huatu.handheld_huatu.business.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.RecordingCourseAdapter;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseLazyListFragment;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.business.other.RecordCourseSearchFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.VodCourseCategorySelectFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.RecordCourseListResponse;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.viewpagerindicator.PagerSlidingArrayTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

//import com.huatu.handheld_huatu.business.lessons.MySingleTypeCourseFragment;

/**
 * Created by cjx on 2018\6\13 0013.
 */
@Deprecated
public  class RecordCourseFragment extends ABaseLazyListFragment<RecordCourseListResponse> implements OnRecItemClickListener,PagerSlidingArrayTabStrip.onSelectTabListener {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;

    @BindView(R.id.xi_activity_type_tab_strip)
    PagerSlidingArrayTabStrip mSlidingArrayTabStrip;


    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    private ArrayList<CourseCategoryBean> mSelCategoryList = new ArrayList<>();

    RecordingCourseAdapter mListAdapter;

    private String orderId = "1";                           // 最终排序Id
    private int mCategoryId= 0;                             // 最终考试Id
    private String mSubjectId="";                           // 最终科目Id

    private int mCurrentItem = 0;

    private CompositeSubscription mCompositeSubscription;

    @Override
    public int getContentView() {
        return R.layout.main_vodcourse_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return myPeopleListView.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(  inflater,   savedInstanceSate);
        mListResponse = new RecordCourseListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new RecordingCourseAdapter(getContext(), mListResponse.mLessionlist);
        mListAdapter.setOnRecyclerViewItemClickListener(this);
    }


    @Override
    public void onRefresh(){
        if (ArrayUtils.isEmpty(mSelCategoryList)) {
            getCategroylist();
        } else {
            superOnRefresh();
        }
    }

    private void superOnRefresh(){
        super.onRefresh();
    }

    private void refreshCurrent(){
        if (ArrayUtils.isEmpty(mSelCategoryList)) {
            getCategroylist();
        } else {
            superOnRefresh();
        }
    }

    @Override
    public void setListener() {
        this.findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyPurchasedFragment.newInstance(2);
               // UIJumpHelper.jumpFragment(getContext(), MySingleTypeCourseFragment.class);
            }
        });
        this.findViewById(R.id.search_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // VodCourseSerachActivity.newIntent(getActivity(), mCategoryId, mSubjectId, orderId);
                RecordCourseSearchFragment.lanuch(getContext(),mCategoryId,mSubjectId,orderId);

            }
        });
        mSlidingArrayTabStrip.setOnSelectTabListener(this);
        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                refreshCurrent();
            }
        });
        this.findViewById(R.id.shopping_category_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VodCourseCategorySelectFragment.newInstance(getActivity(), 10035);
            }
        });
    }

    @Override
    public int getCurrentItem() {
        return mCurrentItem;
    }

    @Override
    public void setCurrentItem(int postion, boolean isAnima) {
        try {
            if (mCurrentItem == postion) return;
            mCurrentItem = postion;

            mCategoryId = mSelCategoryList.get(postion).cateId;
            mSubjectId = mSelCategoryList.get(postion).subjectId;

            SpUtils.setVodCourseExam(mCategoryId);
            SpUtils.setVodCourseSubject(mSubjectId);
            myPeopleListView.getRefreshableView().scrollToPosition(0);
            //myPeopleListView.setRefreshing(true);//会触发onRefresh事件
            ((BaseActivity)getActivity()).showProgress();

            superOnRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        myPeopleListView.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);

        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));
    }


    private void showCategory(List<CourseCategoryBean> titleList){
        mSelCategoryList.clear();
        for (CourseCategoryBean item : titleList) {
            if (item.checked) {
                mSelCategoryList.add(item);
            }
        }
        int size = ArrayUtils.size(mSelCategoryList);
        if(size>0){

            int vodCourseExam =  SpUtils.getVodCourseExam();
            mCurrentItem = 0;
            for (int i = 0; i < mSelCategoryList.size(); i++) {
                if (vodCourseExam == mSelCategoryList.get(i).cateId) {
                    mCurrentItem=i;
                    break;
                }
            }
            mCategoryId = mSelCategoryList.get(mCurrentItem).cateId;
            mSubjectId = mSelCategoryList.get(mCurrentItem).subjectId;
            SpUtils.setVodCourseExam(mCategoryId);

            String[] tmpArr = new String[size];
            for (int i = 0; i <ArrayUtils.size(mSelCategoryList) ; i++)
                tmpArr[i] = mSelCategoryList.get(i).name;
            mSlidingArrayTabStrip.setTabArray(tmpArr);
            if(isFirstLoad())   onFirstLoad();
            else{
                myPeopleListView.getRefreshableView().scrollToPosition(0);
                myPeopleListView.setRefreshing(true);//会触发onRefresh事件
            }

        }else {
            ToastUtils.makeText(getContext(), "加载分类出错").show();
            if (null != mCommloadingView) mCommloadingView.showServerError();
            if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
        }

    }

    @Override
    public void requestData() {
        super.requestData();
        String value = SpUtils.getVodCourseCategoryList();
        if(!TextUtils.isEmpty(value)) {
            List<CourseCategoryBean> allType=GsonUtil.jsonToList(value,new GsonUtil.TypeToken<List<CourseCategoryBean>>(){}.getType());
            if(ArrayUtils.isEmpty(allType)){
                getCategroylist();
            }
            else {
                showCategory(allType);
            }
            return;
        }
        getCategroylist();
    }

    private void getCategroylist(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ServiceProvider.getVodCourseCategoryList(mCompositeSubscription, new NetResponse(){
            @Override
            public void onError(Throwable e) {
//                showErrorView();
                ToastUtils.makeText(getContext(), "加载分类出错").show();
                if (null != mCommloadingView){
                   if(NetUtil.isConnected())
                      mCommloadingView.showServerError();
                   else
                       mCommloadingView.showNetworkTip();
                }
                if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                List<CourseCategoryBean> titleList = model.data;
                if(!ArrayUtils.isEmpty(titleList)){
                    IoExUtils.saveJsonFile(GsonUtil.toJsonStr(titleList), Constant.ALL_COUSRE_TYPE);
                    SpUtils.setVodCourseCategoryList(GsonUtil.GsonString(titleList));
                }
                showCategory(titleList);
             }
        });
     }

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            super.onError(throwable, type);
            /*if(mListAdapter.getItemCount()<=0){
                super.onError(throwable, type);
               // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }*/
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10035 && resultCode == Activity.RESULT_OK) {
            getCategroylist();
        } else if(requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            refreshCurrent();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mListAdapter)
            mListAdapter.clearCountDownTask();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
       CourseApiService.getApi().getVodListCourse(offset,mCategoryId,mSubjectId,orderId,"").enqueue(getCallback());
    }

    @Override
    public void onSuccess(RecordCourseListResponse response)   {
        if(isCurrentReMode()){
            if(null!=mListAdapter)
                mListAdapter.clearCountDownTask();
        }
        if(!ArrayUtils.isEmpty(response.getListResponse())){
            long beginTime= CountDownTask.elapsedRealtime();//毫秒级
            for(Lessons curLession :response.getListResponse()) {
                curLession.lSaleStart = Method.parseInt(curLession.saleStart);
                curLession.lSaleEnd = Method.parseInt(curLession.saleEnd);
                if(curLession.lSaleStart>0){
                    curLession.lSaleStart=beginTime+curLession.lSaleStart*1000;
                }
                if(curLession.lSaleEnd>0){
                    curLession.lSaleEnd=beginTime+curLession.lSaleEnd*1000;
                }
             }
        }
        super.onSuccess(response);
    }

    @Override
    protected void onRefreshCompleted(){
        if(null!=myPeopleListView) myPeopleListView.onRefreshComplete();
        ((BaseActivity)getActivity()).hideProgress();
    }

    @Override
    public  void onItemClick(int position,View view,int type){
        switch (type) {
            case EventConstant.EVENT_ALL:
                Lessons lesson = mListAdapter.getCurrentItem(position);
                if(lesson==null) {
                    return;
                }
                Intent intent;
                if (NetUtil.isConnected()) {
                    if (lesson.isCollect == 1) {
                        CourseCollectSubsetFragment.show(getActivity(),
                                lesson.collectId, lesson.ShortTitle, lesson.title,0);
                    } else if (lesson.isSeckill == 1) {
                        BaseFrgContainerActivity.newInstance(getActivity(),
                                SecKillFragment.class.getName(),
                                SecKillFragment.getArgs(lesson.rid, lesson.title,false));
                    } else {
//                        intent = new Intent(getActivity(), BuyDetailsActivity.class);
                        intent = new Intent(getActivity(), BaseIntroActivity.class);
                        intent.putExtra("rid", lesson.rid);
                        intent.putExtra("NetClassId", lesson.NetClassId);//lesson.NetClassId
                        intent.putExtra("course_type",0);
                        intent.putExtra("price",lesson.ActualPrice);
                        intent.putExtra("originalprice",lesson.Price);
                        intent.putExtra("saleout",lesson.isSaleOut);
                        intent.putExtra("rushout",lesson.isRushOut);
                        intent.putExtra("daishou",lesson.isTermined);
                        intent.putExtra("collageActiveId",lesson.collageActiveId);
                        //intent.putExtra("collageIsBuy", lesson.collageIsBuy);
                        startActivityForResult(intent, 10001);
                    }
                } else {
                    ToastUtils.showShort("网络错误，请检查您的网络");
                }
                break;
        }
    }

    @Override
    public void onUserVisible() {
        LogUtils.e("onliveCourseFragment","onUserVisible");
        super.onUserVisible();
        if(null!=mListAdapter){
            mListAdapter.checkPauseStatus();
        }
    }

    @Override
    public void onUserInvisible() {
        super.onUserInvisible();
        LogUtils.e("onliveCourseFragment","onUserInvisible");
        if(null!=mListAdapter){
            mListAdapter.pauseTicks();
        }
    }
}
