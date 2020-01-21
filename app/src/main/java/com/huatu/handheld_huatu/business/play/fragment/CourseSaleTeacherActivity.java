package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.TeacherCourseAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseItemBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

/**
 *   单个老师所以在售课程页
 */
public class CourseSaleTeacherActivity extends Activity implements XListView.IXListViewListener,View.OnClickListener{
    private final String TAG = "httpCourseSaleTeacherActivity";
    @BindView(R.id.course_sale_back)
    ImageView backIcon;
    @BindView(R.id.course_sale_title)
    TextView title;
    @BindView(R.id.couse_sale_list)
    XListView mList;

    protected CompositeSubscription compositeSubscription;
    private Unbinder unbinder;
    private CustomLoadingDialog progressDlg;
    private String teacherName;
    private String nickName;
    private int courseType;
    private TeacherCourseAdapter courseAdapter;
    private CourseTeacherCourseBean courseBean;
    private List<CourseTeacherCourseItemBean> courseList = new ArrayList<>();
    private int coursePage = 1;
    private int pageSize = 20;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_sale_teacher);
        unbinder = ButterKnife.bind(this);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        teacherName = getIntent().getStringExtra("teachername");
        nickName = getIntent().getStringExtra("nickname");
        courseType = getIntent().getIntExtra("course_type",0);
        LogUtils.d(TAG,"teacherName is :"+teacherName);
        LogUtils.d(TAG,"nickname is :"+nickName);
        LogUtils.d(TAG,"courseType is :"+courseType);
        initView();
        initData();
    }

    public void initView(){
        courseAdapter = new TeacherCourseAdapter(courseType,getApplicationContext());
        mList.setAdapter(courseAdapter);
        if(TextUtils.isEmpty(nickName)) {
            title.setText("由"+teacherName+"讲授的在线课程");
        } else {
            title.setText("由“"+nickName+"”讲授的在线课程");
        }
        backIcon.setClickable(true);
        backIcon.setOnClickListener(this);
        mList.setXListViewListener(this);
    }

    private void initData() {
        showProgress();
        ServiceProvider.getCourseTeacherCourseList(compositeSubscription, teacherName, coursePage,pageSize,new NetResponse(){
            @Override
            public void onError(Throwable e) {
                hideProgess();
                LogUtils.d(TAG,"getCourseTeacherCourseList onOnError");
                mList.stopLoadMore();
                mList.stopRefresh();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgess();
                LogUtils.d(TAG,"getCourseTeacherCourseList onSuccess");
                courseBean = (CourseTeacherCourseBean) model.data;
                mList.stopLoadMore();
                mList.stopRefresh();
                refreshCourseView();
            }
        });
    }

    public void refreshCourseView() {
        LogUtils.d(TAG,"refreshCourseView ...");
        if (courseBean.data != null && courseBean.data.size() > 0) {
            courseList.addAll(courseBean.data);
            LogUtils.d(TAG,"sale couse bean size is :"+courseBean.data.size());
        }
        courseAdapter.setDataAndNotify(courseList);
        coursePage++;
        LogUtils.d(TAG,"coursePage is : "+coursePage+" ;last page is :"+courseBean.last_page);
        if (coursePage > courseBean.last_page) {            ;
            mList.setLoadEndInfo();
        } else {
            mList.setPullLoadEnable(true);
            mList.setPullRefreshEnable(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public void showProgress() {
        if(!Method.isActivityFinished(this)) {
            if(progressDlg == null) {
                progressDlg = new CustomLoadingDialog(this);
            }
            progressDlg.show();
        }
    }

    public void hideProgess() {
        if(!Method.isActivityFinished(this)) {
            progressDlg.dismiss();
        }
    }

    @Override
    public void onRefresh() {
        LogUtils.d(TAG,"xlist onRefresh....");
        loadData();
    }

    @Override
    public void onLoadMore() {
        LogUtils.d(TAG,"xlist onloadmore...");
        loadData();
    }

    private void loadData() {
        showProgress();
        LogUtils.d(TAG,"loaddata , coursePage is :"+coursePage+" ;last page is :"+courseBean.last_page);
        if (coursePage <= courseBean.last_page){
            ServiceProvider.getCourseTeacherCourseList(compositeSubscription, teacherName, coursePage,pageSize,new NetResponse(){
                @Override
                public void onError(Throwable e) {
                    hideProgess();
                    LogUtils.d(TAG,"getCourseTeacherCourseList onOnError at page "+coursePage);
                    mList.stopLoadMore();
                    mList.stopRefresh();
                }

                @Override
                public void onSuccess(BaseResponseModel model) {
                    hideProgess();
                    courseBean = (CourseTeacherCourseBean) model.data;
                    mList.stopLoadMore();
                    mList.stopRefresh();
                    refreshAllCourseView();
                    LogUtils.d(TAG,"getCourseTeacherCourseList onSuccess at page "+coursePage);
                }
            });
        } else {
            hideProgess();
//            mList.setPullLoadEnable(false);
//            mList.setPullRefreshEnable(false);
            mList.stopLoadMore();
            mList.stopRefresh();
            mList.setLoadEndInfo();
        }
    }

    private void refreshAllCourseView() {
        ++coursePage;
        courseList.addAll(courseBean.data);
        courseAdapter.setDataAndNotify(courseList);
    }

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG,"onclick,v.getId() == R.id.course_sale_back is : "+(v.getId() == R.id.course_sale_back));
        if (v.getId() == R.id.course_sale_back) {
            finish();
        }
    }
}
