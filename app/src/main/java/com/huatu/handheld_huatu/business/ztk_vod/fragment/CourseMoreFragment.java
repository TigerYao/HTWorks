package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseHandoutAdapter;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by cjx on 2018\7\20 0020.
 */

public class CourseMoreFragment extends AbsFragment implements ScrollableHelper.ScrollableContainer {

    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    @Override
    public View getScrollableView(){
        return mWorksListView;
    }

    private String mCourseId = "";
    private int mCourseType=0;

    //String[] titleArr=new String[]{"课程详情","老师介绍","课程评价"};//课程详情，老师介绍，课程评价
    int[]  resArr=new int[]{R.mipmap.course_info_icon,R.mipmap.course_teacher_icon,R.mipmap.course_evalute_icon};


    public static CourseMoreFragment getInstance(String courseId,int courseType) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putInt(ArgConstant.TYPE,courseType);
        CourseMoreFragment tmpFragment = new CourseMoreFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mCourseType=args.getInt(ArgConstant.TYPE,0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }
    }


    @Override
    public int getContentView() {
        return R.layout.comm_recyclerlist_nopull_fragment;
    }


    @Override
    public void requestData() {
        super.requestData();

        mCommloadingView.hide();

        List<String> titleArr = Arrays.asList("课程详情","老师介绍","课程评价");
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CourseHandoutAdapter mListAdapter=new CourseHandoutAdapter(getActivity(),titleArr);
        mWorksListView.setRecyclerAdapter(mListAdapter);
        mListAdapter.setOnViewItemClickListener(new OnRecItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int type) {
                if(!NetUtil.isConnected()){
                    ToastUtils.showShort("当前网络不可用");
                    return;
                }
                if(position==0){
                    CourseWebInfoFragment.lanuch(getContext(),mCourseId,mCourseType);
                    //UIJumpHelper.jumpFragment(getContext(),CourseWebInfoFragment.class);
                }
                else if(position==1){
                   CourseTeacherListFragment.lanuch(getContext(),mCourseId,mCourseType);
                }else {
                    CourseEvaluateListFragment.lanuch(getContext(),mCourseId,mCourseType);
                }

            }
        });

    }


    public class CourseHandoutAdapter extends SimpleBaseRecyclerAdapter<String> {

        public CourseHandoutAdapter(Context context, List<String> items) {
            super(context, items);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_more_list_item, parent, false);
            return new MoreViewHolder(collectionView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            MoreViewHolder holderfour = (MoreViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }


        protected class MoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mTitle;
            ImageView mTypeImg;


            MoreViewHolder(View itemView) {
                super(itemView);

                mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
                mTypeImg = (ImageView) itemView.findViewById(R.id.type_img);
                itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.whole_content:
                        if (onRecyclerViewItemClickListener != null)
                            onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);
                        break;
                }
            }

            public void bindUI(String title, int pos) {
                mTitle.setText(title);
                mTypeImg.setImageResource(resArr[pos]);

            }
        }


    }
}
