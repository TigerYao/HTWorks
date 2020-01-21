package com.huatu.handheld_huatu.business.play.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseTeacherListAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;

import java.util.List;

import butterknife.BindView;
/** *
 * 新老师介绍页
 */


public class CourseTeacherFragment extends BaseFragment {
    private final String TAG = "httpCourseTeacherFragment";
    @BindView(R.id.teacher_course_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.server_error_layout)
    RelativeLayout server_error_layout;
    @BindView(R.id.no_network_layout)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.no_detail_tv)
    TextView mEmptyTv;
    @BindView(R.id.no_detail_image)
    ImageView mEmptyImg;

    private String courseId;
    private int courseType;
    private Bundle args;
    private XiaoNengHomeActivity mActivity;
    private CourseTeacherListAdapter mAdapter;
    private List<CourseTeacherListItemBean> mList;
    private boolean mIsChanged = true;


    public static CourseTeacherFragment getInstance(String classID,int classTYPE) {
        Bundle ids=new Bundle();
        ids.putString("course_id",classID);
        ids.putInt("course_type",classTYPE);
        CourseTeacherFragment tempCourseTeacherFragmentt = new CourseTeacherFragment();
        tempCourseTeacherFragmentt.setArguments(ids);
        return tempCourseTeacherFragmentt;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_teacher_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG,"onCreate...");
        this.mActivity = (XiaoNengHomeActivity) getActivity();
        args = getArguments();
        if (args != null) {
            courseId = args.getString("course_id");
            courseType = args.getInt("course_type",0);
        }
        LogUtils.d(TAG,"courseId is : "+courseId+" ;courseType is:"+courseType);
    }

    private void initLoad(){
        if (NetUtil.isConnected()) {
            onLoadData();
        } else {
           showErrorView(0);
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if(isResumed() && isVisibleToUser)
            initLoad();
    }

    public void initAdapter() {
        LogUtils.d(TAG,"initAdapter");
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CourseTeacherListAdapter(mActivity.getApplicationContext(),mList,courseType,mActivity);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.onelayer_reclyview_divider, null));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    protected void reLoadData(String courseId, int courseType){
        this.courseId = courseId;
        this.courseType = courseType;
        mIsChanged = true;
    }

    protected void onLoadData() {
        LogUtils.d(TAG,"onLoadData...");
        if(!isResumed() || !getUserVisibleHint())
            return;
        if((mList != null && !mList.isEmpty() && !mIsChanged))
            return;
        showLoadingView(true);
        NetResponse netResponse = new NetResponse(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtils.d(TAG,"onError...");
                showLoadingView(false);
               showErrorView(1);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                LogUtils.d(TAG,"onSuccess...");

            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                showLoadingView(false);
                LogUtils.d(TAG,"onListSuccess...");
                mList = (List<CourseTeacherListItemBean>)model.data;
                if(mList == null || mList.isEmpty()){
                    showErrorView(2);
                }else {
                    LogUtils.d(TAG, "mList size is :" + mList.size());
                    mRecyclerView.setVisibility(View.VISIBLE);
                    layout_net_unconnected.setVisibility(View.GONE);
                    server_error_layout.setVisibility(View.GONE);
                    initAdapter();
                }
                mIsChanged = false;
            }
        };
        ServiceProvider.getCourseTeacherList(getCompositeSubscription(), courseId, netResponse);
    }

    private void showErrorView(int type) {
        if (type != 0 && !NetUtil.isConnected())
            type = 0;
        switch (type) {
            case 0://无网络
                server_error_layout.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                layout_net_unconnected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetUtil.isConnected()) {
                            initLoad();
                        }
                    }
                });
                break;
            case 1: //加载失败
                mRecyclerView.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.GONE);
                server_error_layout.setVisibility(View.VISIBLE);
                server_error_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initLoad();
                    }
                });
                mEmptyImg.setImageResource(R.drawable.img_network_error);
                mEmptyTv.setText("数据加载失败, 点击重试");
                break;
            case 2://空页面
                mRecyclerView.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.GONE);
                server_error_layout.setVisibility(View.VISIBLE);
                mEmptyImg.setImageResource(R.drawable.nonet);
                mEmptyTv.setText("暂无老师介绍");
                server_error_layout.setOnClickListener(null);
                break;

        }
    }

    private void showLoadingView(boolean show){
        if(mActivity == null)
            return;
        if(show && isResumed() && getUserVisibleHint())
            mActivity.showProgress();
        else if(!show)
            mActivity.hideProgess();
    }
}
