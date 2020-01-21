package com.huatu.handheld_huatu.business.other;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseCollectlistAdapter;
import com.huatu.handheld_huatu.base.PagePullToRefreshStatusBlock;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.mvpmodel.CourseCollectBean;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2019\10\14 0014.
 */

public class TestListFragment extends AbsSettingFragment {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView mWorksListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mLoadingView;


    public int getContentView() {
        return R.layout.course_mycollect_list_layout;
    }
    CourseCollectlistAdapter mListAdapter;
    @Override
    protected void setListener() {
        mWorksListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        PagePullToRefreshStatusBlock<WarpListResponse<CourseCollectBean>> block=new PagePullToRefreshStatusBlock<>();

        block.setPullRefreshView(mWorksListView);
        block.setloadingView(mLoadingView);
        block.setOnRefreshFinishListener(new PagePullToRefreshStatusBlock.OnRefreshFinishListener() {
            @Override
            public void onRefreshCompleted() {
                mWorksListView.onRefreshComplete();
            }
        });
        WarpListResponse<CourseCollectBean> response= new WarpListResponse<CourseCollectBean>();
        response.mAdapterList = new ArrayList<>();
        block.mListResponse=response;

        block.setTypeFactory(new PagePullToRefreshStatusBlock.TypeFactory<WarpListResponse<CourseCollectBean>>(){

           @Override
           public Observable<WarpListResponse<CourseCollectBean>> getListObservable(int pageIndex, int limit){
               return CourseApiService.getApi().getMycollectionList3(pageIndex,limit);
           }

            @Override
            public RecyclerView.Adapter getAdapter(WarpListResponse<CourseCollectBean> response){
               mListAdapter = new CourseCollectlistAdapter(getContext(), response.mAdapterList);
               return mListAdapter;
            }

            @Override
            public CompositeSubscription getComSubscription(){
                return getSubscription();
            }
        });
        block.build();
        block.onFirstLoad();
    }
}
