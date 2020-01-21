package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.business.play.fragment.CourseTeacherDetailFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.CustomRatingBar;
import com.huatu.widget.ImprovedSwipeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class CourseEvaluateListFragment  extends ABaseListFragment<WarpListResponse<CourseTeacherJudgeItemBean>> implements OnRecItemClickListener {
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView mWorksListView;


    public int getContentView() {
        return R.layout.comm_ptrlist_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView.getRefreshableView();
    }

    EvaluateAdapter mListAdapter;

    private String mCourseId = "";
    private int mCourseType=0;
    TextView mTotalTxtView;
    View mHeadView;

    public static void lanuch(Context context, String courseId, int courseType) {

        Bundle arg = new Bundle();
        arg.putString(ArgConstant.COURSE_ID, courseId);
        arg.putInt(ArgConstant.TYPE,courseType);
        UIJumpHelper.jumpFragment(context, CourseEvaluateListFragment.class, arg);
    }

    @Override
    protected void parserParams(Bundle args) {

        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mCourseType=args.getInt(ArgConstant.TYPE,0);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("课程评价");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new WarpListResponse<CourseTeacherJudgeItemBean>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new EvaluateAdapter(getContext(), mListResponse.mAdapterList);
        mListAdapter.setOnViewItemClickListener(this);
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
        getEmptyLayout().setTipText(R.string.xs_none_related_organize);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);


        mHeadView=View.inflate(getContext(),R.layout.course_evaluate_title,null);
        mTotalTxtView=(TextView) mHeadView.findViewById(R.id.course_judge_count);
        mHeadView.setVisibility(View.INVISIBLE);
        ((FrameLayout)findViewById(R.id.whole_content)).addView(mHeadView,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT));

        FrameLayout.LayoutParams tmpParam= (FrameLayout.LayoutParams)mWorksListView.getLayoutParams();
        tmpParam.topMargin=DensityUtils.dp2px(getContext(),30);

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
    public void onSuccess(WarpListResponse<CourseTeacherJudgeItemBean> response)   {
        super.onSuccess(response);
        if(isCurrentReMode()){
            if(mTotalTxtView!=null) {
                mTotalTxtView.setText("("+response.data.total+")");
                mHeadView.setVisibility(View.VISIBLE);
            }
        }
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
        CourseApiService.getApi().getCourseJudgeList(mCourseId,mCourseType,offset,limit).enqueue(getCallback());
    }


    @Override
    public void onItemClick(int position, View view, int type) {

    }

    public class EvaluateAdapter extends SimpleBaseRecyclerAdapter<CourseTeacherJudgeItemBean> {
        public EvaluateAdapter(Context context, List<CourseTeacherJudgeItemBean> items) {
            super(context, items);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_teacher_judge_item_layout2, parent, false);
            return new  ViewHolder(collectionView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            ViewHolder holderfour = (ViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.teacher_judge_item_header)
            ImageView mAvaterImg;

            @BindView(R.id.teacher_judge_item_name)
            TextView mNameTxt;

            @BindView(R.id.teacher_judge_item_section)
            TextView msectionTxt;

            @BindView(R.id.teacher_judge_item_rating)
            CustomRatingBar mstarBar;

            @BindView(R.id.teacher_judge_item_content)
            TextView mContentTxt;

            @BindView(R.id.teacher_judge_item_time)
            TextView mTimeTxt;

            @BindView(R.id.teacher_judge_item_course)
            TextView mCourseDesTxt;

            @BindView(R.id.divider_flag)
            View mDividerView;


            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.detail_btn:
                        if (onRecyclerViewItemClickListener != null)
                            onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);
                        break;
                }
            }

            public void bindUI(CourseTeacherJudgeItemBean item, int position) {

                 ImageLoad.displayUserAvater(mContext,item.userFace,mAvaterImg,R.mipmap.user_default_avater);
                 mNameTxt.setText(item.userName);
                 if(TextUtils.isEmpty(item.periods)){
                      mDividerView.setVisibility(View.GONE);
                 } else {
                     mDividerView.setVisibility(View.VISIBLE);
                 }
                 msectionTxt.setText(TextUtils.isEmpty(item.periods)?"":item.periods+"期");
                 float curStar=item.star/2;
                 mstarBar.setStar(curStar>5?5:curStar);
                 mstarBar.setClickable(false);
                 mContentTxt.setText(item.courseRemark);
                 mTimeTxt.setText(item.rateDate);
                 mCourseDesTxt.setText(item.lessonTitle);
            }
        }

    }
}
