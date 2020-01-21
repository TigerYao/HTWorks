package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.DownloadSelectAdapter;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.business.play.fragment.CourseSaleTeacherActivity;
import com.huatu.handheld_huatu.business.play.fragment.CourseTeacherDetailActivity;
import com.huatu.handheld_huatu.business.play.fragment.CourseTeacherDetailFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadSelectFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.CustomRatingBar;
import com.huatu.widget.ImprovedSwipeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class CourseTeacherListFragment extends ABaseListFragment<SimpleListResponse<CourseTeacherListItemBean>> implements OnRecItemClickListener {
    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @Override
    protected int getLimit() {
        return 200;
    }

    public int getContentView() {
        return R.layout.comm_recyclerlist_fragment;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView;
    }

    TeachlistAdapter mListAdapter;

    private String mCourseId = "";
    private int mCourseType=0;

    public static void lanuch(Context context, String courseId,int courseType) {

        Bundle arg = new Bundle();
        arg.putString(ArgConstant.COURSE_ID, courseId);
        arg.putInt(ArgConstant.TYPE,courseType);
        UIJumpHelper.jumpFragment(context, CourseTeacherListFragment.class, arg);
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
        setTitle("老师介绍");

     }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new SimpleListResponse<CourseTeacherListItemBean>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new TeachlistAdapter(getContext(), mListResponse.mAdapterList);
        mListAdapter.setOnViewItemClickListener(this);
    }


    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_related_organize);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);

        ImprovedSwipeLayout tmpSwipeLayout=(ImprovedSwipeLayout)this.findViewById(R.id.xi_swipe_pull_to_refresh);
        tmpSwipeLayout.setEnabled(false);
        tmpSwipeLayout.setBackgroundColor(Color.WHITE);
        mWorksListView.setPadding(0, DensityUtils.dp2px(getContext(),15),0,0);
        mWorksListView.setClipToPadding(false);
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
        CourseApiService.getApi().getCourseTeacherList(mCourseId).enqueue(getCallback());
     }


    @Override
    public void onItemClick(int position, View view, int type) {
        CourseTeacherListItemBean item = mListAdapter.getItem(position);
        if(null==item) return;
        switch (type) {

            case EventConstant.EVENT_ALL:
                Intent teIntent = new Intent(getContext(),CourseTeacherDetailActivity.class);
                teIntent.putExtra("teacher_id",item.teacherId);
                teIntent.putExtra("nick_name",item.nickname);
                teIntent.putExtra("teacher_name",item.teacherName);
                teIntent.putExtra("course_type",mCourseType);
                teIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(teIntent);
                // CourseTeacherDetailFragment.show(getContext(), item.teacherId,item.nickname,item.teacherName,mCourseType);
                break;
        }
    }

    public class TeachlistAdapter extends SimpleBaseRecyclerAdapter<CourseTeacherListItemBean> {
       public TeachlistAdapter(Context context, List<CourseTeacherListItemBean> items) {
            super(context, items);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.play_teache_info_list_item, parent, false);
            return new ViewHolder(collectionView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            ViewHolder holderfour = (ViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.user_avater_img)
            ImageView mAvaterImg;

            @BindView(R.id.teacher_name_txt)
            TextView mTeachNameTxt;

            @BindView(R.id.teacher_comment_item_rating)
            CustomRatingBar mstarBar;

            @BindView(R.id.course_teacher_list_item_score_tv)
            TextView mstarBarTxt;

            @BindView(R.id.techer_typedes_txt)
            TextView mTypedesTxt;

            @BindView(R.id.course_teacher_list_item_lesson_num_tv)
            TextView mLessionTxt;

            @BindView(R.id.techer_des_txt)
            TextView mTecherDesTxt;

            @BindView(R.id.detail_btn)
            TextView mDetailBtn;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mDetailBtn.setOnClickListener(this);
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

            public void bindUI(CourseTeacherListItemBean item, int position) {

                ImageLoad.displaynoCacheUserAvater(mContext,item.roundPhoto,mAvaterImg,R.mipmap.user_default_avater);
                mTeachNameTxt.setText(item.teacherName);
                float  starNum=item.star/2;
                mstarBar.setStar(starNum>5 ?5:starNum);
                mstarBar.setClickable(false);
                mstarBarTxt.setText(String.valueOf(item.teacherRank)+"分");
                mTypedesTxt.setText(item.SubjectType);
                mLessionTxt.setText(item.allStudentNum+"个学生"+"\u3000\u3000\u3000\u3000"+item.payClasses+"个在售课程");
                mTecherDesTxt.setText(item.Brief);
                mDetailBtn.setText(String.format("查看“%s”的详细介绍", TextUtils.isEmpty(item.nickname)?item.teacherName:item.nickname));
             }
          }

      }
}