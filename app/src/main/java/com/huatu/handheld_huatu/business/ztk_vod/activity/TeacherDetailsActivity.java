package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.TeacherListAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherListBeans;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.XListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import io.vov.vitamio.utils.Log;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht-djd on 2017/9/5.
 * 教师列表
 */

public class TeacherDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        XListView.IXListViewListener, View.OnClickListener {
    @BindView(R.id.xlv_teacherdetails)
    XListView xlv_teacherdetails;
    @BindView(R.id.layout_TitleBar)
    TopActionBar layout_TitleBar;
    private CompositeSubscription compositeSubscription;
    private ArrayList<TeacherListBeans> mTeacherList = new ArrayList<>();
    private TeacherListAdapter adapter;
    private boolean isLoading = false;
    private int courseId;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_teacherdetails;
    }

    @Override
    protected void onInitView() {

        ButterKnife.bind(this);
        courseId = getIntent().getIntExtra("rid", 0);
        //Log.e("courseId",courseId+"");
        layout_TitleBar.showRightButton(false);
        initTitleBar();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        adapter = new TeacherListAdapter(TeacherDetailsActivity.this, mTeacherList) {
        };
        xlv_teacherdetails.setPullLoadEnable(false);
        xlv_teacherdetails.setPullRefreshEnable(true);
        xlv_teacherdetails.setXListViewListener(this);
        xlv_teacherdetails.setAdapter(adapter);
    }

    private void initTitleBar() {
        layout_TitleBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
               finish();
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
    protected void onLoadData() {
        showProgress();
        loadData(courseId);
    }

    private void loadData(int courseId) {

        DataController.getInstance().getTeacherList(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseListResponseModel<TeacherListBeans>>() {

                    @Override
                    public void onCompleted() {
                        hideProgress();
                        isLoading = false;
                        xlv_teacherdetails.stopLoadMore();
                        xlv_teacherdetails.stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        xlv_teacherdetails.stopLoadMore();
                        xlv_teacherdetails.stopRefresh();
                        isLoading = false;
                    }

                    @Override
                    public void onNext(BaseListResponseModel<TeacherListBeans> teacherListBeansBaseListResponseModel) {
                        if (teacherListBeansBaseListResponseModel.code == 1000000) {
                            List<TeacherListBeans> data = teacherListBeansBaseListResponseModel.data;
                            mTeacherList.clear();
                            mTeacherList.addAll(data);
                            hideProgress();
                        }
                    }
                });
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onRefresh() {
        if (NetUtil.isConnected()) {
            if (isLoading) return;
            loadData(courseId);
        } else {
            if (isLoading) return;
            xlv_teacherdetails.stopRefresh();
            CommonUtils.showToast("网络错误，请检查您的网络");
        }
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(TeacherDetailsActivity.this.getApplicationContext(), "无更多课程！",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    /**
     * 实例化
     *
     * @param context
     */
    public static void newIntent(Context context, int rid) {
        Intent intent = new Intent(context, TeacherDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("rid", rid);
        context.startActivity(intent);
    }
}
