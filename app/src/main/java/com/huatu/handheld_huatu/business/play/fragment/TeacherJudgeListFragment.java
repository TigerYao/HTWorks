package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.view.View;

import com.huatu.handheld_huatu.base.BaseDetailListFragment;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseJudgeAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.TopActionBar;

/**
 * Created by saiyuan on 2018/7/10.老师介绍页
 */

public class TeacherJudgeListFragment extends BaseDetailListFragment {
    private String teacherId;
    private String nickName;
    private int curPage;

    @Override
    protected void onInitView() {
        super.onInitView();
        teacherId = args.getString("teacher_id");
        nickName = args.getString("nick_name");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void initAdapter() {
        mAdapter = new CourseJudgeAdapter();
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        topActionBar.setTitle("“" + nickName + "”全部课程评价");
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_CANCELED);
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    public void onRefresh() {
        getData(true);
    }

    @Override
    public void onLoadMore() {
        getData(false);
    }

    @Override
    protected void onLoadData() {
        onRefresh();
    }

    private void getData(final boolean isRefresh) {
        mActivity.showProgress();
        if(isRefresh) {
            curPage = 1;
        } else {
            curPage++;
        }
        ServiceProvider.getCourseTeacherJudgeList(compositeSubscription,
                teacherId, curPage, 20, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                mActivity.hideProgess();
//                TeacherJudgeListFragment.this.onLoadDataFailed();
                if (curPage == 1) {
                    listView.setVisibility(View.GONE);
                    noServer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgess();
                CourseTeacherJudgeBean judgeBean = (CourseTeacherJudgeBean) model.data;
                TeacherJudgeListFragment.this.onSuccess(judgeBean.data, isRefresh);
                if(judgeBean.next == 1) {
                    listView.setPullLoadEnable(true);
                } else {
                    listView.setPullLoadEnable(true);
                }
            }
        });
    }
}
