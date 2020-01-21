package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseDetailFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseJudgeAdapter;
import com.huatu.handheld_huatu.business.play.adapter.TeacherCourseAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseItemBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherIntroInfoBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.BJPlayerExView;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.FlowLayout;
import com.huatu.handheld_huatu.view.NoScrollListView;
import com.huatu.popup.QuickListAction;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2018/7/5.
 */

public class CourseTeacherDetailFragment extends BaseDetailFragment {
    private final String TAG = "httpCourseTeacherDetailFragment";
//    @BindView(R.id.course_teacher_detail_title_view)
//    TopActionBar topActionBar;
    @BindView(R.id.course_teacher_detail_name_tv)
    TextView tvName;
//    @BindView(R.id.course_teacher_detail_judge_tv)
//    TextView tvJudge;
    @BindView(R.id.course_teacher_detail_subject_tv)
    TextView tvSubject;
    @BindView(R.id.course_teacher_detail_year_tv)
    TextView tvYear;
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

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_teacher_detail_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        teacherId = args.getString("teacher_id");
        nickName = args.getString("nick_name");
        teacherName = args.getString("teacher_name");
        courseType = args.getInt("course_type",0);
        LogUtils.d(TAG,"teacherName is "+teacherName);
        LogUtils.d(TAG,"courseType is "+courseType);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (TextUtils.isEmpty(nickName)) {
            tvCourseDes.setText("由" + teacherName + "讲授的在线课程");
        } else {
            tvCourseDes.setText("由“" + nickName + "”讲授的在线课程");
        }
        setBoldText(tvCourseDes);

    }

    private void initTitle() {
        LogUtils.d(TAG,"initTitle ....");
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG,"back clicked...");
                setResultForTargetFrg(Activity.RESULT_CANCELED);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initTitle();
        if (mBjPlayerView != null) {
            mBjPlayerView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBjPlayerView != null) {
            mBjPlayerView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
        if (mBjPlayerView != null) {
            mBjPlayerView.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d(TAG,"onConfigurationChanged,newConfig.orientation is :"+newConfig.orientation);
        LogUtils.d(TAG,"newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE is :"+(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        LogUtils.d(TAG,"newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT is :"+(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));


        if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            teacher_introduction_layout.setVisibility(View.GONE);
            comment_layout.setVisibility(View.GONE);
            course_layout.setVisibility(View.GONE);
            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            });
        } else if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            teacher_introduction_layout.setVisibility(View.VISIBLE);
            comment_layout.setVisibility(View.VISIBLE);
            course_layout.setVisibility(View.VISIBLE);
            initTitle();
        }

        if (mBjPlayerView != null) {
            mBjPlayerView.onConfigurationChanged(newConfig);
        }
    }

    private void initPlayer() {
        mBjPlayerView.initBJYPlayer();
        Button mStartPlayBtn=(Button) mBjPlayerView.findViewById(R.id.start_play_btn);
        ImageView imageViewBack = (ImageView) mBjPlayerView.findViewById(R.id.rl_back);
        ImageView imageViewIvt = (ImageView) mBjPlayerView.findViewById(R.id.iv_titlebt);
        ImageView fullScreen = (ImageView) mBjPlayerView.findViewById(R.id.image_change_screen);
        mStartPlayBtn.setVisibility(View.GONE);
        imageViewBack.setVisibility(View.GONE);
        imageViewIvt.setVisibility(View.GONE);
        fullScreen.setVisibility(View.GONE);
        playIcon.setClickable(true);
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playIcon.setVisibility(View.GONE);
                playBackground.setVisibility(View.GONE);
                mBjPlayerView.setVisibility(View.VISIBLE);
                mBjPlayerView.setVideoId(videoId,videoToken);
                mBjPlayerView.playVideo(0);
            }
        });
    }

    private void getCourseInfo() {
        ServiceProvider.getCourseTeacherCourseList(compositeSubscription, teacherName, coursePage,pageSize,new NetResponse(){
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG,"getCourseTeacherCourseList onOnError");
                mActivity.hideProgess();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                courseBean = (CourseTeacherCourseBean) model.data;
                refreshCourseView();
                LogUtils.d(TAG,"getCourseTeacherCourseList onSuccess");
            }
        });
    }

    private void getJudgeInfo() {
        ServiceProvider.getCourseTeacherJudgeList(compositeSubscription, teacherId, commentPage,pageSize,new NetResponse(){
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG,"getCourseTeacherJudgeList onError");
                mActivity.hideProgess();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgess();
                judgeBean = (CourseTeacherJudgeBean) model.data;
                LogUtils.d(TAG,"getCourseTeacherJudgeList onSuccess");
                refreshJudgeView();
            }
        });
    }

    private void getDetailInfo() {
        ServiceProvider.getCourseTeacherIntroInfo(compositeSubscription, teacherId, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG,"getCourseTeacherIntroInfo onError");
                mActivity.hideProgess();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                LogUtils.d(TAG,"getCourseTeacherIntroInfo onSuccess");
                introInfoBean = (CourseTeacherIntroInfoBean)model.data;
                refreshIntroView();
                initPlayingView();
                getCourseInfo();
            }
        });
    }

    @Override
    protected void onLoadData() {
        mActivity.showProgress();
        getDetailInfo();
    }

    private void refreshIntroView() {
        LogUtils.d(TAG,"refreshIntroView...");
        tvName.setText(introInfoBean.teacherName);
        setBoldText(tvName);
        tIntroduciton.setText("老师介绍");
        setBoldText(tIntroduciton);
//        tvJudge.setText(introInfoBean.teacherRank + "%好评");
        tvSubject.setText(introInfoBean.SubjectType);
        Typeface type = Typeface.createFromAsset(mActivity.getAssets(), "font/851-CAI978.ttf");
        int originalYear = Integer.valueOf(introInfoBean.teacherYear).intValue();
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
        String [] teacherPoint = introInfoBean.teachingstyle.split("，");
        if (teacherPoint != null && teacherPoint.length > 0 && !TextUtils.isEmpty(teacherPoint[0])) {
            LogUtils.d("httpCourseTeacherDetailFragment","teacherPoint length is :"+teacherPoint.length);
            tagLayout.removeAllViews();
            for (int i = 0; i < teacherPoint.length; i++) {
                TextView tvWord =(TextView) mLayoutInflater.inflate(R.layout.techer_point_text_bg, tagLayout,false);
                final String strWord = teacherPoint[i];
                tvWord.setText(strWord);
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.rightMargin = DisplayUtil.dp2px(15);
                lp.bottomMargin = DisplayUtil.dp2px(15);
                tagLayout.addView(tvWord, lp);
            }
        } else {
            LogUtils.d("httpCourseTeacherDetailFragment","teacherPoint is empty");
            tagLayout.removeAllViews();
            tagLayout.setVisibility(View.GONE);
        }
    }

    private void initPlayingView() {
        LogUtils.d(TAG,"initPlayingView...");
        String backVideoId = introInfoBean.videointro;
        videoToken = introInfoBean.token;
       // Glide.with(mActivity).load(introInfoBean.videoURL).into(playBackground);

        ImageLoad.load(mActivity,introInfoBean.videoURL,playBackground);
        if (TextUtils.isEmpty(videoToken)) {
            playIcon.setVisibility(View.GONE);
        } else {
            videoId =  StringUtils.parseLong(backVideoId);
            LogUtils.d(TAG,"videoToken is : "+videoToken);
            initPlayer();
        }
    }

    private void refreshJudgeView() {
        LogUtils.d(TAG,"refreshJudgeView ....");
        tCommned.setText("学员评价");
        setBoldText(tCommned);
        tvJudgeNum.setText("(" + judgeBean.total + ")");
        List<CourseTeacherJudgeItemBean> threeItemList = new ArrayList<>();
        LogUtils.d(TAG,"judgeBean.data.size() is :"+judgeBean.data.size());
        if (judgeBean.data.size() == 0) {
            comment_layout.setVisibility(View.GONE);
            return;
        }
        if(judgeBean.data.size() > 3) {
            for(int i = 0; i < 3; i++) {
                threeItemList.add(judgeBean.data.get(i));
            }
        } else {
            threeItemList.addAll(judgeBean.data);
            commentMore.setVisibility(View.GONE);
        }
        judgeAdapter = new CourseJudgeAdapter();
        lvJudge.setAdapter(judgeAdapter);
        LogUtils.d(TAG,"threeItemList size is : "+threeItemList.size());
        judgeAdapter.setDataAndNotify(threeItemList);
        mActivity.hideProgess();
    }

    public void refreshCourseView() {
        LogUtils.d(TAG,"refreshCourseView...");
        List<CourseTeacherCourseItemBean> threeItemSaleList = new ArrayList<>();
        LogUtils.d(TAG,"sale couse bean size is :"+courseBean.data.size());
        if (courseBean.data.size() == 0) {
             course_layout.setVisibility(View.GONE);
             return;
        }
        if(courseBean.data.size() > 3) {
            for(int i = 0; i < 3; i++) {
                threeItemSaleList.add(courseBean.data.get(i));
            }
        } else {
            threeItemSaleList.addAll(courseBean.data);
            saleMore.setVisibility(View.GONE);
        }
        courseAdapter = new TeacherCourseAdapter(courseType,mActivity.getApplicationContext());
        lvCourse.setAdapter(courseAdapter);
        LogUtils.d(TAG,"threeItemSaleList size is : "+threeItemSaleList.size());
        courseAdapter.setDataAndNotify(threeItemSaleList);
        getJudgeInfo();
    }

    @OnClick(R.id.course_teacher_detail_judge_all_layout)
    void onClickAllJudge() {
        //需要跳新页面
        Intent mCommentIntent = new Intent(mActivity,TecherCommentActivity.class);
        mCommentIntent.putExtra("teachername",teacherName);
        if (!TextUtils.isEmpty(nickName)) {
            mCommentIntent.putExtra("nickname",nickName);
        }
        mCommentIntent.putExtra("teacherid",teacherId);
        mCommentIntent.putExtra("count",judgeBean.total);
        mActivity.startActivity(mCommentIntent);
    }


    @OnClick(R.id.course_teacher_detail_course_all_layout)
    void onClickAllCourse() {
        //跳转到老师在售课程页面
        Intent mCourseIntent = new Intent(mActivity,CourseSaleTeacherActivity.class);
        mCourseIntent.putExtra("teachername",teacherName);
        if (!TextUtils.isEmpty(nickName)) {
            mCourseIntent.putExtra("nickname",nickName);
        }
        mCourseIntent.putExtra("course_type",courseType);
        mActivity.startActivity(mCourseIntent);
    }

    public static void show(Context context, String teacherId,String nickName,String teacherName,int coursetype) {
        Bundle arg = new Bundle();
        arg.putString("teacher_id", teacherId);
        arg.putString("nick_name",nickName);
        arg.putString("teacher_name",teacherName);
        arg.putInt("course_type",coursetype);
        BaseFrgPreContainerActivity.newInstance(context,
                CourseTeacherDetailFragment.class.getName(), arg);
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }
}
