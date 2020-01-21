package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseJudgeAdapter;
import com.huatu.handheld_huatu.business.play.adapter.TeacherCourseAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseItemBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherIntroInfoBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.business.play.bean.HistoryCourseBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.BJPlayerExView;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.FlowLayout;
import com.huatu.handheld_huatu.view.NoScrollListView;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

public class CourseTeacherDetailActivity extends Activity {
    private final String TAG = "httpCourseTeacherDetailActivity";
    @BindView(R.id.course_teacher_detail_name_tv)
    TextView tvName;
    @BindView(R.id.course_teacher_detail_subject_tv)
    TextView tvSubject;
    @BindView(R.id.course_teacher_detail_year_tv)
    TextView tvYear;
    @BindView(R.id.course_teacher_detail_year_info)
    TextView tvYearInfo;
    @BindView(R.id.divide_teacher)
    View divide_teacher;
    @BindView(R.id.course_teacher_detail_student_tv)
    TextView tvStudent;
    @BindView(R.id.course_teacher_detail_course_tv)
    TextView tvCourse;
    @BindView(R.id.course_teacher_detail_description_tv)
    TextView tvDes;
    @BindView(R.id.course_teacher_detail_tag_layout)
    FlowLayout tagLayout;
    @BindView(R.id.course_teacher_detail_judge_num_tv)
    TextView tvJudgeNum;
    @BindView(R.id.course_teacher_detail_judge_lv)
    NoScrollListView lvJudge;
    @BindView(R.id.course_teacher_detail_course_des_tv)
    TextView tvCourseDes;
    @BindView(R.id.course_teacher_detail_course_lv)
    NoScrollListView lvCourse;
    @BindView(R.id.course_teacher_detail_judge_all_layout)
    RelativeLayout commentMore;
    @BindView(R.id.course_teacher_detail_course_all_layout)
    RelativeLayout saleMore;
    @BindView(R.id.top_playcontainer)
    RelativeLayout top_playcontainer;
    @BindView(R.id.teacher_introduction_layout)
    LinearLayout teacher_introduction_layout;
    @BindView(R.id.student_comment_layout)
    LinearLayout comment_layout;
    @BindView(R.id.course_layout)
    LinearLayout course_layout;
    @BindView(R.id.teacher_videoview)
    BJPlayerExView mBjPlayerView;
    @BindView(R.id.play_cover_teacher)
    ImageView playBackground;
    @BindView(R.id.teacher_play)
    RelativeLayout playIcon;
    @BindView(R.id.teacher_detail_tab_back)
    ImageView mBack;
    @BindView(R.id.teacher_introduction)
    TextView tIntroduciton;
    @BindView(R.id.student_comment)
    TextView tCommned;
    @BindView(R.id.switch_btn)
    ImageView mSwitchBtn;
    @BindView(R.id.history_course_layout)
    HistoryCourseExpandLayout historyCourseExpandLayout;
    @BindView(R.id.title_layout)
    View mJudgeTileLayout;
    @BindView(R.id.teacher_detail_largin_line)
    View marginLine;
    @BindView(R.id.scroll_layout)
    ScrollView mScrollView;

    CourseJudgeAdapter judgeAdapter;
    TeacherCourseAdapter courseAdapter;

    private String teacherId;
    private String nickName;
    private String teacherName;
    private int courseType;
    private CourseTeacherIntroInfoBean introInfoBean;
    private CourseTeacherJudgeBean judgeBean;
    private CourseTeacherCourseBean courseBean;
    private int commentPage = 1;
    private int coursePage = 1;
    private int pageSize = 4;
    private long videoId;
    private String videoToken;
    private Unbinder unbinder;
    protected CompositeSubscription compositeSubscription;
    protected LayoutInflater mLayoutInflater;
    private long videoTrilSize;
    private boolean mIsJudged = true;

    public CustomLoadingDialog progressDlg;
    private HistoryCourseBean allCourses;

    public void showProgress() {
        if (!Method.isActivityFinished(this)) {
            if (progressDlg == null) {
                progressDlg = new CustomLoadingDialog(this);
            }
            progressDlg.show();
        }
    }

    public void hideProgess() {
        if (!Method.isActivityFinished(this)) {
            progressDlg.dismiss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CommonUtils.isPad(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_course_teacher_detail);
        unbinder = ButterKnife.bind(this);
        mLayoutInflater = LayoutInflater.from(getApplicationContext());
        initTeacherInfo();
        int distanceHeight = (int) (DensityUtils.getScreenWidth(this) * 0.56f);
        LinearLayout.LayoutParams curParams = (LinearLayout.LayoutParams) top_playcontainer.getLayoutParams();
        curParams.height = distanceHeight;
        curParams.weight = DensityUtils.getScreenWidth(this);
        top_playcontainer.setLayoutParams(curParams);
        Button mStartPlayBtn = (Button) mBjPlayerView.findViewById(R.id.start_play_btn);
        ImageView imageViewBack = (ImageView) mBjPlayerView.findViewById(R.id.rl_back);
        ImageView imageViewIvt = (ImageView) mBjPlayerView.findViewById(R.id.iv_titlebt);
        ImageView fullScreen = (ImageView) mBjPlayerView.findViewById(R.id.image_change_screen);
        imageViewBack.getLayoutParams().height = 0;
        imageViewIvt.getLayoutParams().height = 0;
        fullScreen.getLayoutParams().height = 0;
        mBjPlayerView.findViewById(R.id.lock_screen_btn).getLayoutParams().height = 0;
        mBjPlayerView.findViewById(R.id.textView_TitleBar_Info).getLayoutParams().height = 0;
        mBjPlayerView.setVisibility(View.GONE);
    }

    private void initTeacherInfo(){
        teacherId = getIntent().getStringExtra("teacher_id");
        nickName = getIntent().getStringExtra("nick_name");
        teacherName = getIntent().getStringExtra("teacher_name");
        courseType = getIntent().getIntExtra("course_type", 0);
        LogUtils.d(TAG, "teacherName is " + teacherName);
        LogUtils.d(TAG, "courseType is " + courseType);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (TextUtils.isEmpty(nickName)) {
            tvCourseDes.setText("由" + teacherName + "讲授的在线课程");
        } else {
            tvCourseDes.setText("由“" + nickName + "”讲授的在线课程");
        }
        setBoldText(tvCourseDes);
        if (NetUtil.isConnected()) {
            showProgress();
            getDetailInfo();
        }
        initTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBjPlayerView != null) {
            mBjPlayerView.onPause();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String id = getIntent().getStringExtra("teacher_id");
        if (!TextUtils.equals(id, teacherId))
            initTeacherInfo();
        mScrollView.scrollTo(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBjPlayerView != null) {
            mBjPlayerView.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int distanceHeight = (int) (DensityUtils.getScreenWidth(this) * 0.56f);
        LinearLayout.LayoutParams curParams = (LinearLayout.LayoutParams) top_playcontainer.getLayoutParams();
        curParams.height = distanceHeight;
        curParams.weight = DensityUtils.getScreenWidth(this);
        top_playcontainer.setLayoutParams(curParams);
//        LogUtils.d(TAG, "onConfigurationChanged,newConfig.orientation is :" + newConfig.orientation);
//        LogUtils.d(TAG, "newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE is :" + (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE));
//        LogUtils.d(TAG, "newConfig.orientation == Configuration.ORIENTATION_PORTRAIT is :" + (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT));
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            teacher_introduction_layout.setVisibility(View.GONE);
//            comment_layout.setVisibility(View.GONE);
//            course_layout.setVisibility(View.GONE);
//            LogUtils.d(TAG, "videoOriginalHeight is : " + videoOriginalHeight);
//            LogUtils.d(TAG, "videoOriginalWidth is : " + videoOriginalWidth);
//            lp.width = DisplayUtil.getScreenHeight();
//            LogUtils.d(TAG, "lp.width : " + lp.width);
//            lp.height = DisplayUtil.getScreenWidth();
//            LogUtils.d(TAG, "lp.height : " + lp.height);
//            mBjPlayerView.setLayoutParams(lp);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            ViewGroup.LayoutParams lp = mBjPlayerView.getLayoutParams();
//            lp.height = videoOriginalHeight;
//            lp.width = videoOriginalWidth;
//            mBjPlayerView.setLayoutParams(lp);
//            teacher_introduction_layout.setVisibility(View.VISIBLE);
//            comment_layout.setVisibility(View.VISIBLE);
//            course_layout.setVisibility(View.VISIBLE);
//        }
//
        if (mBjPlayerView != null) {
            mBjPlayerView.onConfigurationChanged(newConfig);
        }
    }

    private void initTitle() {
        LogUtils.d(TAG, "initTitle ....");
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "back clicked...");
                finish();
            }
        });
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }

    private void initPlayer() {
        mBjPlayerView.initBJYPlayer();
        mBjPlayerView.setVideoSize(videoTrilSize);
        Button mStartPlayBtn = (Button) mBjPlayerView.findViewById(R.id.start_play_btn);
        ImageView imageViewBack = (ImageView) mBjPlayerView.findViewById(R.id.rl_back);
        ImageView imageViewIvt = (ImageView) mBjPlayerView.findViewById(R.id.iv_titlebt);
        ImageView fullScreen = (ImageView) mBjPlayerView.findViewById(R.id.image_change_screen);
        mStartPlayBtn.setVisibility(View.GONE);
//        imageViewBack.setVisibility(View.GONE);
        imageViewIvt.setVisibility(View.GONE);
        fullScreen.setVisibility(View.GONE);
        playIcon.setClickable(true);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBjPlayerView.setVisibility(View.VISIBLE);
                mBjPlayerView.setVideoId(videoId, videoToken);
                mBjPlayerView.playVideo(0);
            }
        });

    }

    private void getCourseInfo() {
        ServiceProvider.getCourseTeacherCourseList(compositeSubscription, teacherName, coursePage, pageSize, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG, "getCourseTeacherCourseList onOnError");
                course_layout.setVisibility(View.GONE);
                getJudgeInfo();
//                hideProgress();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                courseBean = (CourseTeacherCourseBean) model.data;
                refreshCourseView();
                LogUtils.d(TAG, "getCourseTeacherCourseList onSuccess");
            }
        });
    }

    private void getJudgeInfo() {
        ServiceProvider.getCourseTeacherJudgeList(compositeSubscription, teacherId, commentPage, pageSize, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG, "getCourseTeacherJudgeList onError");
                comment_layout.setVisibility(View.GONE);
                hideProgess();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgess();
                judgeBean = (CourseTeacherJudgeBean) model.data;
                LogUtils.d(TAG, "getCourseTeacherJudgeList onSuccess");
                refreshJudgeView();
//                historyCourseExpandLayout.loadData(compositeSubscription, teacherId, 4);
            }
        });
    }

    private void getDetailInfo() {
        ServiceProvider.getCourseTeacherIntroInfo(compositeSubscription, teacherId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG, "getCourseTeacherIntroInfo onError");
                getCourseInfo();
//                hideProgress();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                LogUtils.d(TAG, "getCourseTeacherIntroInfo onSuccess");
                introInfoBean = (CourseTeacherIntroInfoBean) model.data;
                refreshIntroView();
                initPlayingView();
                getCourseInfo();
            }
        });
    }

    private void switchLayout() {
        if (mIsJudged) {
            lvJudge.setVisibility(View.GONE);
            historyCourseExpandLayout.setVisibility(View.VISIBLE);
            historyCourseExpandLayout.displayLayout();
            mJudgeTileLayout.setBackgroundResource(R.drawable.teacher_detail_course_list_bg);
            marginLine.setBackgroundResource(R.drawable.top_down_shadow_line);
        } else {
            lvJudge.setVisibility(View.VISIBLE);
            historyCourseExpandLayout.setVisibility(View.GONE);
            mJudgeTileLayout.setBackgroundResource(R.drawable.course_bottom_line_bg);
            marginLine.setBackgroundResource(R.color.teacher_detail_margin_color);
        }
        mIsJudged = !mIsJudged;
    }


    private void refreshIntroView() {
        LogUtils.d(TAG, "refreshIntroView...");
        tvName.setText(introInfoBean.teacherName);
        setBoldText(tvName);
        tIntroduciton.setText("老师介绍");
        setBoldText(tIntroduciton);
//        tvJudge.setText(introInfoBean.teacherRank + "%好评");
        tvSubject.setText(introInfoBean.SubjectType);
        Typeface type = Typeface.createFromAsset(this.getAssets(), "font/851-CAI978.ttf");
        int originalYear = Integer.valueOf(introInfoBean.teacherYear).intValue();
        if (originalYear > 0) {
            String year = originalYear + "年";
            SpannableStringBuilder ssbYear = new SpannableStringBuilder(year);
            ssbYear.setSpan(new ForegroundColorSpan(Color.parseColor("#EC74A0")), 0,
                    (introInfoBean.teacherYear + "年").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssbYear.setSpan(new RelativeSizeSpan(1.25f), 0,
                    introInfoBean.teacherYear.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssbYear.setSpan(new RelativeSizeSpan(0.75f), introInfoBean.teacherYear.length(),
                    (introInfoBean.teacherYear + "年").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvYear.setText(ssbYear);
            tvYear.setTypeface(type);
        } else {
            tvYear.setVisibility(View.GONE);
            tvYearInfo.setVisibility(View.GONE);
            divide_teacher.setVisibility(View.GONE);
        }
        int originalNumber;
        if (introInfoBean.allStudentNum == null) {
            originalNumber = 0;
        } else {
            originalNumber = Integer.valueOf(introInfoBean.allStudentNum).intValue();
        }
        String studentNum = originalNumber + "个";
        SpannableStringBuilder ssbStudent = new SpannableStringBuilder(studentNum);
        ssbStudent.setSpan(new ForegroundColorSpan(Color.parseColor("#EC74A0")), 0,
                (introInfoBean.allStudentNum + "个").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbStudent.setSpan(new RelativeSizeSpan(1.25f), 0,
                introInfoBean.allStudentNum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbStudent.setSpan(new RelativeSizeSpan(0.75f), introInfoBean.allStudentNum.length(),
                (introInfoBean.allStudentNum + "个").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStudent.setText(ssbStudent);
        tvStudent.setTypeface(type);
        int originalPayClass = Integer.valueOf(introInfoBean.payClasses).intValue();
        String courseNum = originalPayClass + "个";
        SpannableStringBuilder ssbCourse = new SpannableStringBuilder(courseNum);
        ssbCourse.setSpan(new ForegroundColorSpan(Color.parseColor("#EC74A0")), 0,
                (introInfoBean.payClasses + "个").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbCourse.setSpan(new RelativeSizeSpan(1.25f), 0,
                introInfoBean.payClasses.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbCourse.setSpan(new RelativeSizeSpan(0.75f), introInfoBean.payClasses.length(),
                (introInfoBean.payClasses + "个").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCourse.setText(ssbCourse);
        tvCourse.setTypeface(type);
        tvDes.setText(introInfoBean.Brief);
        //加入特点
        String[] teacherPoint = introInfoBean.teachingstyle.split("，");
        if (teacherPoint != null && teacherPoint.length > 0 && !TextUtils.isEmpty(teacherPoint[0])) {
            LogUtils.d("httpCourseTeacherDetailFragment", "teacherPoint length is :" + teacherPoint.length);
            tagLayout.removeAllViews();
            for (int i = 0; i < teacherPoint.length; i++) {
                TextView tvWord = (TextView) mLayoutInflater.inflate(R.layout.techer_point_text_bg, tagLayout, false);
                final String strWord = teacherPoint[i];
                tvWord.setText(strWord);
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.rightMargin = DisplayUtil.dp2px(15);
                lp.bottomMargin = DisplayUtil.dp2px(15);
                tagLayout.addView(tvWord, lp);
            }
        } else {
            LogUtils.d(TAG, "teacherPoint is empty");
            tagLayout.removeAllViews();
            tagLayout.setVisibility(View.GONE);
        }
    }

    private void initPlayingView() {
        LogUtils.d(TAG, "initPlayingView...");
        String backVideoId = introInfoBean.videointro;
        videoToken = introInfoBean.token;
      //  Glide.with(this).load(introInfoBean.videoURL).into(playBackground);

        ImageLoad.load(this,introInfoBean.videoURL,playBackground);
        if (TextUtils.isEmpty(videoToken)) {
            playIcon.setVisibility(View.GONE);
        } else {
            videoId = StringUtils.parseLong(backVideoId);
            LogUtils.d(TAG, "videoToken is : " + videoToken);
            videoTrilSize = introInfoBean.videoSize;
            LogUtils.d(TAG, "videoTrilSize is : " + videoTrilSize);
            initPlayer();
        }
    }

    private void refreshJudgeView() {
        LogUtils.d(TAG, "refreshJudgeView ....");
        tCommned.setText("学员评价");
        setBoldText(tCommned);
        tvJudgeNum.setText("(" + judgeBean.total + ")");
        List<CourseTeacherJudgeItemBean> threeItemList = new ArrayList<>();
        LogUtils.d(TAG, "judgeBean.data.size() is :" + judgeBean.data.size());
        hideProgess();
        if (judgeBean.data.size() == 0) {
            comment_layout.setVisibility(View.GONE);
            return;
        }
        if (judgeBean.data.size() > 3) {
            for (int i = 0; i < 3; i++) {
                threeItemList.add(judgeBean.data.get(i));
            }
        } else {
            threeItemList.addAll(judgeBean.data);
            commentMore.setVisibility(View.GONE);
        }
        judgeAdapter = new CourseJudgeAdapter();
        lvJudge.setAdapter(judgeAdapter);
        LogUtils.d(TAG, "threeItemList size is : " + threeItemList.size());
        judgeAdapter.setDataAndNotify(threeItemList);;
    }

    public void refreshCourseView() {
        LogUtils.d(TAG, "refreshCourseView...");
        List<CourseTeacherCourseItemBean> threeItemSaleList = new ArrayList<>();
        LogUtils.d(TAG, "sale couse bean size is :" + courseBean.data.size());
        if (courseBean.data.size() == 0) {
            course_layout.setVisibility(View.GONE);
            getJudgeInfo();
            return;
        }
        if (courseBean.data.size() > 3) {
            for (int i = 0; i < 3; i++) {
                threeItemSaleList.add(courseBean.data.get(i));
            }
        } else {
            threeItemSaleList.addAll(courseBean.data);
            saleMore.setVisibility(View.GONE);
        }
        courseAdapter = new TeacherCourseAdapter(courseType, this.getApplicationContext());
        lvCourse.setAdapter(courseAdapter);
        LogUtils.d(TAG, "threeItemSaleList size is : " + threeItemSaleList.size());
        courseAdapter.setDataAndNotify(threeItemSaleList);
        getJudgeInfo();
    }

    @OnClick(R.id.course_teacher_detail_judge_all_layout)
    void onClickAllJudge() {
        //需要跳新页面
        if (!mIsJudged) {
            JudgeCourseListActivity.startJudgeCourseListActivity(this, teacherId);
            return;
        }
        Intent mCommentIntent = new Intent(this, TecherCommentActivity.class);
        mCommentIntent.putExtra("teachername", teacherName);
        if (!TextUtils.isEmpty(nickName)) {
            mCommentIntent.putExtra("nickname", nickName);
        }
        mCommentIntent.putExtra("teacherid", teacherId);
        mCommentIntent.putExtra("count", judgeBean.total);
        this.startActivity(mCommentIntent);
    }


    @OnClick(R.id.course_teacher_detail_course_all_layout)
    void onClickAllCourse() {
        //跳转到老师在售课程页面
        Intent mCourseIntent = new Intent(this, CourseSaleTeacherActivity.class);
        mCourseIntent.putExtra("teachername", teacherName);
        if (!TextUtils.isEmpty(nickName)) {
            mCourseIntent.putExtra("nickname", nickName);
        }
        mCourseIntent.putExtra("course_type", courseType);
        this.startActivity(mCourseIntent);
    }

    public static void show(Context context, String teacherId, String nickName, String teacherName, int coursetype) {
        Bundle arg = new Bundle();
        arg.putString("teacher_id", teacherId);
        arg.putString("nick_name", nickName);
        arg.putString("teacher_name", teacherName);
        arg.putInt("course_type", coursetype);
        BaseFrgPreContainerActivity.newInstance(context,
                CourseTeacherDetailFragment.class.getName(), arg);
    }
}

