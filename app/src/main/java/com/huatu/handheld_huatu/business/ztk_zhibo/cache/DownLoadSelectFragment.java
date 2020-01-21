package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.DownloadSelectAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by cjx on 2018\8\4 0004.
 */

public class DownLoadSelectFragment extends ABaseListFragment<SimpleListResponse<DownLoadLesson>> implements  OnRecItemClickListener {
    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @Override
    protected int getLimit() {  return 200; }

    public int getContentView() {
        return R.layout.comm_recyclerlist_nopull_fragment;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView;
    }

    DownloadSelectAdapter mListAdapter;

    private String mCourseId = "";
    private boolean mIsLocal=false;

    List<DownHandout> mDownHandoutList;
    HashMap<String,DownHandout> mDownHandoutMap=new HashMap<>();

    public static void lanuch(Context context, CourseInfoBean mRecordCourseInfo){

        Bundle arg=new Bundle();
        arg.putSerializable(ArgConstant.COURSE_ID,mRecordCourseInfo);
        UIJumpHelper.jumpFragment(context,DownLoadSelectFragment.class,arg);
    }

    @Override
    protected void parserParams(Bundle args) {

        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mIsLocal=args.getBoolean(ArgConstant.IS_LOCAL_VIDEO);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new SimpleListResponse<DownLoadLesson>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new DownloadSelectAdapter(getContext(), mListResponse.mAdapterList);
        mListAdapter.setOnViewItemClickListener(this);
    }


    @Override
    public void requestData() {
        super.requestData() ;
        onFirstLoad();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_related_organize);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);

        mWorksListView.setPagesize(getLimit());
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.setRecyclerAdapter(mListAdapter);

    }

    @Override
    protected void setListener() {
        mWorksListView.setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        //ProductApiServer.getMainpageWorks(mSellerId, offset, limit).enqueue(getCallback());
       // CourseApiService.getApi().getAllCourseWarebyPage(StringUtils.parseLong(mCourseId),offset,limit).enqueue(getCallback());
        //MyRecylerCourseFragment
    }



    @Override
    public void onItemClick(int position,View view,int type) {

       /* HandoutBean.Course item = mListAdapter.getItem(position);
        if(null==item) return;
        switch (type) {
            case EventConstant.EVENT_JOIN_IN:


                break;
            case EventConstant.EVENT_ALL:


                break;
        }*/
    }






    @Override
    public void onDestroy() {
        mListAdapter=null;
        super.onDestroy();

        /*try {
            if (currenttTitlePosition != -1) {
                //savePlayProgress(currenttTitlePosition,true);
            }
        } catch (Exception e) {
            // Log.e("player error", e.getMessage());
        }*/
    }
}
