package com.huatu.handheld_huatu.business.lessons;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.business.lessons.adapter.CourseSuitAdapter;
import com.huatu.handheld_huatu.business.lessons.bean.CourseSuit;
import com.huatu.handheld_huatu.business.lessons.bean.CourseSuitChild;
import com.huatu.handheld_huatu.business.lessons.bean.CourseSuitData;
import com.huatu.handheld_huatu.business.lessons.bean.CourseSuitFather;
//import com.huatu.handheld_huatu.business.ztk_vod.activity.MediaPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.activity.TeacherDetailsActivity;
import com.huatu.handheld_huatu.business.ztk_vod.activity.TeacherListDetailActivity;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.QQGroupAddInfoBean;

import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.OnRefreshListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.RefreshListView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.view.CustomHeadView;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by CHQ on 2017/4/15.
 */
@Deprecated
public class CourseSuitActivity extends BaseActivity implements OnRefreshListener, View.OnClickListener {
    private static final String TAG = "CourseSuitActivity";
    private ArrayList<CourseSuitChild> mCourseSuitChild = new ArrayList<>();
    private String rid;
    private int courseId=0;
    private String NetClassId;
    private TopActionBar topActionBar;
    private TextView tv_course_suit_title;
    private TextView tv_course_suit_class;
    private RefreshListView rlv_course_suit;
    private LinearLayout layoutTeacher;
    private LinearLayout layoutTeacherImg;
    private View btnAddQQ;
    @BindView(R.id.course_suit_add_qq_group_btn)
    TextView tvAddQQ;
    @BindView(R.id.course_suit_add_qq_group_img)
    ImageView tivAddQQ;

    private CourseSuitAdapter mAdapter;

    private AsyncTask<Void, Void, Void> mAsyncTask;
    private boolean isConnected;
    private String courseName;
    private QQGroupAddInfoBean qqInfo;
    private int courseType = 1;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_course_suit;
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
    protected void onInitView() {
        Intent bIntent = getIntent();
        rid = bIntent.getStringExtra("rid");
        Log.e("RID", rid + "rid");
        NetClassId = bIntent.getStringExtra("NetClassId");
        Log.e("NetClassId", NetClassId + "NetClassId");
        topActionBar = (TopActionBar) findViewById(R.id.course_suit_title_bar);
        initTitleBar();
        tv_course_suit_title = (TextView) findViewById(R.id.tv_course_suit_title);
        tv_course_suit_class = (TextView) findViewById(R.id.tv_course_suit_class);
        btnAddQQ =  findViewById(R.id.course_suit_add_qq_group);
        layoutTeacher = (LinearLayout) findViewById(R.id.course_suit_teacher_layout);
        layoutTeacherImg = (LinearLayout) findViewById(R.id.course_suit_teacher_item_layout);
        mAdapter = new CourseSuitAdapter(getApplicationContext());
        rlv_course_suit = (RefreshListView) findViewById(R.id.rlv_course_suit);
        rlv_course_suit.setAdapter(mAdapter);
        rlv_course_suit.setOnRefreshListener(this);
        btnAddQQ.setOnClickListener(this);
        layoutTeacher.setOnClickListener(this);
        rlv_course_suit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseSuitChild child = mCourseSuitChild.get(position - 1);
                if (NetUtil.isConnected()) {
                    if(child.courseType == 0) {
                        //录播播放接口
                       // initData(position, child.rid);
                        //百家云录播播放页面
                        startBJYMediaPlay(child.rid);
                    } else if(child.courseType == 1) {
                        Intent intent = new Intent();
                        intent.setClass(CourseSuitActivity.this, BJRecordPlayActivity.class);
                        intent.putExtra("father_course_id", String.valueOf(courseId));
                        intent.putExtra("course_id", child.rid);
                        intent.putExtra("classid", String.valueOf(courseId));
                        intent.putExtra(ArgConstant.TYPE,1);
                        startActivity(intent);
                    }
                } else {
                    CommonUtils.showToast("网络错误，请检查您的网络");
                }
            }

        });
    }

    private void initTitleBar() {
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                onBackPressed();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                String webUrl = RetrofitManager.getInstance().getBaseUrl() + "c/v3/courses/" + courseId;
                Bundle bundle = new Bundle();
                bundle.putString(BaseWebViewFragment.ARGS_STRING_URL, webUrl);
                bundle.putString(BaseWebViewFragment.ARGS_STRING_TITLE, courseName);
                bundle.putBoolean("isShowButton", false);
                bundle.putBoolean("isShowTitle", true);
                bundle.putBoolean("isSupportBack", true);
                BaseFrgContainerActivity.newInstance(CourseSuitActivity.this,
                        BaseWebViewFragment.class.getName(), bundle);
            }
        });
    }

    @Override
    protected void onLoadData() {
        if(!TextUtils.isEmpty(NetClassId)) {
            courseId = Integer.parseInt(NetClassId);
        } else if(!TextUtils.isEmpty(rid)) {
            courseId = Integer.parseInt(rid);
        }
        showProgress();
        initData(courseId);
    }

    private void initData(int courseId) {
        Subscription subscribe= RetrofitManager.getInstance().getService().getCourseSuit(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSuit>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                    }

                    @Override
                    public void onNext(CourseSuit courseSuit) {
                        hideProgress();
                        if (courseSuit.code == 1000000) {
                            CourseSuitFather mCourseSuitFather = courseSuit.data.father;
                            courseType = mCourseSuitFather.courseType;
                            qqInfo = courseSuit.data.QQ;
                            if(qqInfo != null && !TextUtils.isEmpty(qqInfo.AndroidFunction)) {
                                tivAddQQ.setImageResource(R.drawable.add_qq_suit);
                                tvAddQQ.setTextColor(Color.parseColor("#e9304e"));
                            } else {
                                tivAddQQ.setImageResource(R.drawable.icon_course_suit_add_qq_disable);
                                tvAddQQ.setTextColor(Color.parseColor("#dfdfdf"));
                            }
                            courseName = mCourseSuitFather.Title;
                            tv_course_suit_title.setText(mCourseSuitFather.Title);
                            if(courseType == 1) {
                                tv_course_suit_class.setText(mCourseSuitFather.StartDate + "-"
                                        + mCourseSuitFather.EndDate + "(" + mCourseSuitFather.TimeLength + "课时" + ")");
                                btnAddQQ.setVisibility(View.VISIBLE);
                            } else {
                                tv_course_suit_class.setText(mCourseSuitFather.TimeLength + "课时");
                               // btnAddQQ.setVisibility(View.GONE);
                                btnAddQQ.setVisibility(View.VISIBLE);
                            }
                            if(!Method.isListEmpty(courseSuit.data.teacher)) {
                                layoutTeacher.setVisibility(View.VISIBLE);
                                addTeacherInfo(courseSuit.data.teacher);
                            } else {
                                layoutTeacher.setVisibility(View.GONE);
                            }
                            ArrayList<CourseSuitChild> result = courseSuit.data.child;
                            mCourseSuitChild.clear();
                            mCourseSuitChild.addAll(result);
                            mAdapter.setData(courseType, mCourseSuitChild);
                        }
                    }
                });
        if(compositeSubscription!=null) {
            compositeSubscription.add(subscribe);
        }

    }

    private void addTeacherInfo(List<CourseSuitData.TeacherInfo> teacherInfos) {
        layoutTeacherImg.removeAllViews();
        for(int i = 0; i < teacherInfos.size(); i++) {
            if(i >= 3) {
                break;
            }
            final CourseSuitData.TeacherInfo info = teacherInfos.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    DisplayUtil.dp2px(44), DisplayUtil.dp2px(44));
            lp.leftMargin = DisplayUtil.dp2px(10);
            CustomHeadView headView = new CustomHeadView(UniApplicationContext.getContext());
            headView.setEdgingColor(Color.parseColor("#00000000"));
            headView.setHeadUrl(info.roundPhoto);
            headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherListDetailActivity.newIntent(CourseSuitActivity.this, info.TeacherId,
                            info.TeacherName, info.roundPhoto);
                }
            });
            layoutTeacherImg.addView(headView, lp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_suit_teacher_layout:
                TeacherDetailsActivity.newIntent(CourseSuitActivity.this,courseId);
                break;
            case R.id.course_suit_add_qq_group:
                if(qqInfo != null && !TextUtils.isEmpty(qqInfo.AndroidFunction)) {
                    Method.joinQQGroup(qqInfo.AndroidFunction);
                }
                break;
        }
    }

    @Override
    public void onDownPullRefresh() {

        mAsyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (NetUtil.isConnected()) {
                    SystemClock.sleep(1000);
                    initData(courseId);
                    isConnected = true;
                } else {
                    isConnected = false;
                    SystemClock.sleep(1000);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (rlv_course_suit != null) {
                    rlv_course_suit.hideHeaderView();
                }
                if (!isConnected) {
                    CommonUtils.showToast("网络错误，请检查您的网络");
                }
            }
        };
        mAsyncTask.execute(new Void[]{});
    }

    @Override
    public void onLoadingMore() {
        Toast.makeText(CourseSuitActivity.this, "无更多课程！",
                Toast.LENGTH_SHORT).show();
        rlv_course_suit.hideFooterView();
    }

/*    private void initData(int position, final String rid) {
        DataController.getInstance().getVodCoursePlay(Integer.parseInt(rid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VodCoursePlayBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(VodCoursePlayBean vodCoursePlayBean) {
                        if (vodCoursePlayBean.code == 1000000) {
                            ArrayList<VodCoursePlayBean.LessionBean> livelessionBean = new ArrayList<VodCoursePlayBean.LessionBean>();
                            ArrayList<VodCoursePlayBean.LessionBean> lession = vodCoursePlayBean.data.lession;
                            ArrayList<VodCoursePlayBean.LessionBean> live = vodCoursePlayBean.data.live;
                            livelessionBean.addAll(live);
                            livelessionBean.addAll(lession);
                            VodCoursePlayBean.CourseBean course = vodCoursePlayBean.data.course;
                            VodCoursePlayBean.QQBean qq = vodCoursePlayBean.data.QQ;
                            //跳转录播播放页
                                startMediaPlay(lession,course,livelessionBean,qq);
                        }
                    }
                });
    }*/


   /* private void startMediaPlay(ArrayList<VodCoursePlayBean.LessionBean> lession,
                                VodCoursePlayBean.CourseBean course,
                                ArrayList<VodCoursePlayBean.LessionBean> livelessionBean,
                                VodCoursePlayBean.QQBean qqInfoBean) {
        Intent intent = new Intent(CourseSuitActivity.this, MediaPlayActivity.class);
        Bundle bundle = new Bundle();
            intent.putExtra("current", -1);
            intent.putExtra("classid",course.NetClassId);
            intent.putExtra("ClassTitle", course.title);
            intent.putExtra("ClassScaleimg", course.scaleimg);
            intent.putExtra("free",course.free);
            intent.putExtra("qqInfoBean",qqInfoBean.AndroidFunction);
            bundle.putSerializable("data", lession);
            bundle.putSerializable("livelession", livelessionBean);

        intent.putExtras(bundle);
        startActivity(intent);
    }*/
    private void startBJYMediaPlay(String rid) {
   /*     Intent intent = new Intent(CourseSuitActivity.this, BJYMediaPlayActivity.class);
        intent.putExtra("current", -1);
        intent.putExtra("classid", rid);
        startActivity(intent);*/

    }
}
