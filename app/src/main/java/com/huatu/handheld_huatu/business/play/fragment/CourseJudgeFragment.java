package com.huatu.handheld_huatu.business.play.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.CustomRatingBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseJudgeFragment extends ABaseListFragment<WarpListResponse<CourseTeacherJudgeItemBean>> {
    @BindView(R.id.course_judge_page_list)
    RecyclerViewEx mWorksListView;

    @BindView(R.id.comment_info)
    TextView mTotalTxtView;


    public int getContentView() {
        return R.layout.fragment_course_judge;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView;
    }

    CourseJudgeFragment.EvaluateAdapter mListAdapter;

    private String mCourseId = "";
    private int mCourseType = 0;
    // TextView mTotalTxtView;
    //View mHeadView;
    private Bundle args;

    private boolean mIsChanged = false;

    public static CourseJudgeListFragment getInstance(String classID, int courseTYPE) {
        Bundle ids = new Bundle();
        ids.putString("course_id", classID);
        ids.putInt("course_type", courseTYPE);
        CourseJudgeListFragment tempCourseJudgeListFragment = new CourseJudgeListFragment();
        tempCourseJudgeListFragment.setArguments(ids);
        return tempCourseJudgeListFragment;
    }

/*    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("课程评价");
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
        if (args != null) {
            mCourseId = args.getString("course_id");
            mCourseType = args.getInt("course_type");
        }
        mListResponse = new WarpListResponse<CourseTeacherJudgeItemBean>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new CourseJudgeFragment.EvaluateAdapter(getContext(), mListResponse.mAdapterList);
    }

    public void reLoad(String courseId, int courseType) {
        mCourseId = courseId;
        mCourseType = courseType;
        mIsChanged = true;
        if (mListAdapter != null)
            mListAdapter.clearAndRefresh();
        if (getListView() != null)
            getListView().resetAll();
    }

    @Override
    public synchronized void onFirstLoad() {
        if (isResumed() && getUserVisibleHint()) {
            if (mIsChanged) {
                mCurrentPage = 1;
                showFirstLoading();
                onRefresh();
            } else
                super.onFirstLoad();
        }

    }

    @Override
    public void requestData() {
        super.requestData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        onFirstLoad();
    }

    private void superOnRefresh() {
        super.onRefresh();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.no_commnet);
        getEmptyLayout().setEmptyImg(R.drawable.nonet);


/*        mWorksListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });*/
        mWorksListView.setPagesize(getLimit());
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.setRecyclerAdapter(mListAdapter);

    }


    @Override
    public void onSuccess(WarpListResponse<CourseTeacherJudgeItemBean> response) {
        super.onSuccess(response);
        if (isCurrentReMode()) {
            if (mTotalTxtView != null) {
                mTotalTxtView.setText("学员评价(" + response.data.total + ")");
            }
        }
    }

    @Override
    public void onError(String throwable, int type) {
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
            mListAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }
/*    @Override
    protected void onRefreshCompleted(){
        if(null!=mWorksListView) mWorksListView.onRefreshComplete();
    }*/

    @Override
    protected void setListener() {
        mWorksListView.setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getCourseJudgeList(mCourseId, mCourseType, offset, limit).enqueue(getCallback());
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }


    public class EvaluateAdapter extends SimpleBaseRecyclerAdapter<CourseTeacherJudgeItemBean> {
        public EvaluateAdapter(Context context, List<CourseTeacherJudgeItemBean> items) {
            super(context, items);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_teacher_judge_item_layout2, parent, false);
            return new CourseJudgeFragment.EvaluateAdapter.ViewHolder(collectionView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            CourseJudgeFragment.EvaluateAdapter.ViewHolder holderfour = (CourseJudgeFragment.EvaluateAdapter.ViewHolder) holder;
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

                ImageLoad.displayUserAvater(mContext, item.userFace, mAvaterImg, R.mipmap.user_default_avater);
                mNameTxt.setText(item.userName);
                if (TextUtils.isEmpty(item.periods)) {
                    mDividerView.setVisibility(View.GONE);
                } else {
                    mDividerView.setVisibility(View.VISIBLE);
                }
                msectionTxt.setText(TextUtils.isEmpty(item.periods) ? "" : item.periods + "期");
                float curStar = item.star / 2;
                mstarBar.setStar(curStar > 5 ? 5 : curStar);
                mstarBar.setClickable(false);
                mContentTxt.setText(item.courseRemark);
                mTimeTxt.setText(item.rateDate);
                mCourseDesTxt.setText(item.lessonTitle);
            }
        }

    }
}
