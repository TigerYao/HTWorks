package com.huatu.handheld_huatu.business.other;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.CourseListAdapter;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CourseListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.InputMethodUtils;
import com.huatu.viewpagerindicator.PagerSlidingArrayTabStrip;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by cjx on 2018\8\28 0028.
 * 课程搜索列表
 */


public class OnLiveCourseSearchFragment extends ABaseListFragment<CourseListResponse> implements OnRecItemClickListener, PagerSlidingArrayTabStrip.onSelectTabListener {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;
    @BindView(R.id.tab_strip)
    PagerSlidingArrayTabStrip tab_strip;


    //    RecordingCourseAdapter mListAdapter;
    CourseListAdapter mListAdapter;
    // private String orderId = "1";                           // 最终排序Id
    private int mCategoryId = 0;                             // 最终考试Id
    private String mSubjectId = "";                           // 最终科目Id

    protected String mkeyword = "";

    protected String mOrderId = "0";
    protected int mPriceId = 1000;
    private int mCurrentItem = 0;

    private int mType;//0 为录播，1为直播
    private int isRecommend = -1;
    private int isHistory = -1;
    private ArrayList<CourseCategoryBean> categoryList = new ArrayList<>();

/*    public static Fragment getInstance( int categoryId, String subjectId, String orderId){
        Bundle Args=new Bundle();
        Args.putInt(ArgConstant.KEY_ID,categoryId);
        Args.putString(ArgConstant.TYPE,subjectId);
        Args.putString(ArgConstant.KEY_TITLE,orderId);

        OnLiveCourseSearchFragment tmpFragment=new OnLiveCourseSearchFragment();
        tmpFragment.setArguments(Args);
        return tmpFragment;
    }*/

    @Override
    protected void parserParams(Bundle arg) {
        mCategoryId = 10000;//进来后直接定位到全部
        mkeyword = arg.getString(ArgConstant.KEY_TITLE);
        isRecommend = arg.getInt(ArgConstant.IS_RECOMMEND);
        isHistory = arg.getInt(ArgConstant.IS_HISTORY);
        categoryList = (ArrayList<CourseCategoryBean>) arg.getSerializable(ArgConstant.CATEGORY_LIST);
        CourseCategoryBean mCategoryAll = new CourseCategoryBean();
        mCategoryAll.cateId = 10000;
        mCategoryAll.name = "全部";
        categoryList.add(0, mCategoryAll);
    }

    private void showCategory() {
        int size = ArrayUtils.size(categoryList);
        if (size > 0) {
            mCurrentItem = 0;
            for (int i = 0; i < categoryList.size(); i++) {
                if (mCategoryId == categoryList.get(i).cateId) {
                    mCurrentItem = i;
                    break;
                }
            }
            mCategoryId = categoryList.get(mCurrentItem).cateId;
            mSubjectId = categoryList.get(mCurrentItem).subjectId;

            String[] tmpArr = new String[size];
            for (int i = 0; i < ArrayUtils.size(categoryList); i++) {
                tmpArr[i] = categoryList.get(i).name;
            }
            if (tab_strip != null) {
                tab_strip.setTabArray(tmpArr);
            }
            if (isFirstLoad()) {
                onFirstLoad();
            }
            if (null != myPeopleListView) myPeopleListView.getRefreshableView().scrollToPosition(0);
            if (null != myPeopleListView) myPeopleListView.setRefreshing(true);//会触发onRefresh事件
        } else {
            ToastUtils.makeText(getContext(), "加载分类出错").show();
//            if (null != mCommloadingView) mCommloadingView.showServerError();
            if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
        }
    }

    @Override
    protected int getLimit() {
        return 10;
    }

    @Override
    public int getContentView() {
        return R.layout.comm_ptrlist_layout5;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return myPeopleListView.getRefreshableView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //出现 从热词和搜索历史进来也定位到全部
            tab_strip.showSelectChange(0, 0);
            setCurrentItem(0, false);
            Log.d("tab_strip", "-----出现----");

        } else {
            //消失
        }
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        mListResponse = new CourseListResponse();
        mListResponse.mCourseList = new ArrayList<>();
        mListAdapter = new CourseListAdapter(getContext(), mListResponse.mCourseList);
        mListAdapter.setPageSource("app课程搜索结果页");
//        mListAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void superOnRefresh() {
        super.onRefresh();
    }

    @Override
    public void setListener() {
        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        showCategory();
        tab_strip.setOnSelectTabListener(this);

    }

    public void refresh(String keyword, int recommend, int history) {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        if (mkeyword.equals(keyword)) return;
        mkeyword = keyword;
        this.isRecommend = recommend;
        this.isHistory = history;
        // resultHeader.setKewWord(mkeyword);
        showFirstLoading();

        myPeopleListView.getRefreshableView().scrollToPosition(0);
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mListAdapter)
            mListAdapter.clearCountDownTask();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        myPeopleListView.getRefreshableView().setPagesize(getLimit());
        myPeopleListView.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);

        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));
    }

    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
        //if(isFirstLoad())   onFirstLoad();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getAllCoursesSearch(mkeyword, mCategoryId, offset, limit, isRecommend, isHistory).enqueue(getCallback());
        mListAdapter.setCurrentPage(offset);
        mListAdapter.setKeyWord(mkeyword);
        mListAdapter.setFromSearch(true);
    }


    @Override
    public void onError(String throwable, int type) {
        ((SimpleBaseActivity) getActivity()).hideProgess();
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if (mListAdapter.getItemCount() <= 0) {
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            } else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(), "网络加载出错~");
            }
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
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    @Override
    protected void onGoBack() {
        super.onGoBack();
        ((SimpleBaseActivity) getActivity()).hideProgess();
        InputMethodUtils.hideMethod(getContext(), getContainerView());
    }

    @Override
    public void onSuccess(CourseListResponse response) {
        ((SimpleBaseActivity) getActivity()).hideProgess();
        if (!ArrayUtils.isEmpty(response.getListResponse())) {
            long beginTime = CountDownTask.elapsedRealtime();//毫秒级
            for (CourseListData curLesson : response.getListResponse()) {

                // 根据服务器时间，计算剩余时间（这里用的还是剩余时间，不是开始时间）
                if (curLesson.startTimeStamp != 0) {
                    long startR = (curLesson.startTimeStamp * 1000 - ServerTimeUtil.newInstance().getServerTime()) / 1000;
                    curLesson.lSaleStart = startR > 0 ? startR : 0;
                } else {
                    curLesson.lSaleStart = Method.parseLong(curLesson.saleStart);
                }
                if (curLesson.stopTimeStamp != 0) {
                    long endR = (curLesson.stopTimeStamp * 1000 - ServerTimeUtil.newInstance().getServerTime()) / 1000;
                    curLesson.lSaleEnd = endR > 0 ? endR : 0;
                } else {
                    curLesson.lSaleEnd = Method.parseLong(curLesson.saleEnd);
                }

                if (curLesson.lSaleStart > 0) {
                    curLesson.lSaleStart = beginTime + curLesson.lSaleStart * 1000;
                }
                if (curLesson.lSaleEnd > 0) {
                    curLesson.lSaleEnd = beginTime + curLesson.lSaleEnd * 1000;
                }
            }
        }
        super.onSuccess(response);
    }

    @Override
    protected void onRefreshCompleted() {
        if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
    }

    @Override
    public void onItemClick(int position, View view, int type) {

    }

    @Override
    public int getCurrentItem() {
        return mCurrentItem;
    }

    @Override
    public void setCurrentItem(int position, boolean isAnima) {
        try {
            if (mCurrentItem == position) return;
            mCurrentItem = position;

            mCategoryId = categoryList.get(position).cateId;
            mSubjectId = categoryList.get(position).subjectId;
            ((SimpleBaseActivity) getActivity()).showProgress();
            myPeopleListView.getRefreshableView().scrollToPosition(0);
            superOnRefresh();


            // myPeopleListView.getRefreshableView().scrollToPosition(0);
            // myPeopleListView.setRefreshing(true);//会触发onRefresh事件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
