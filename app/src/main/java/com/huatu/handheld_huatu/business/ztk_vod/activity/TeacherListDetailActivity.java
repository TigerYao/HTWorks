package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherJieshaoBean;
import com.huatu.handheld_huatu.business.ztk_vod.view.TeacherEvaluateView;
import com.huatu.handheld_huatu.business.ztk_vod.view.TeacherIntroduceView;
import com.huatu.handheld_huatu.business.ztk_vod.view.TeacherListView;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.CircleTransform;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.ViewAnimHelper;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht-djd on 2017/9/6.
 * 教师详情页
 */

public class TeacherListDetailActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.ib_back)
    ImageButton ib_back;
    @BindView(R.id.iv_teacher)
    ImageView iv_teacher;
    @BindView(R.id.tv_teachername)
    TextView tv_teachername;
    @BindView(R.id.viewpager)
    ViewPager mPager;
    @BindView(R.id.tv_introduce)
    TextView tv_introduce;
    @BindView(R.id.tv_evaluate)
    TextView tv_evaluate;
    @BindView(R.id.tv_list)
    TextView tv_list;
    @BindView(R.id.imageview_tab_line)
    ImageView imageview_tab_line;
    private List<View> mView;
    private String personId;
    private String name;
    private String teacherImage;
    private TeacherListDetailActivity mActivity = this;
    private TeacherIntroduceView introduceView;
    private TeacherEvaluateView evaluateView;
    private TeacherListView listView;
    private int sum = 0;
    private int tabLineWidth;
    private TeacherJieshaoBean.DataBean data;
    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_teacherdetail;
    }

    @Override
    protected void onInitView() {
        ButterKnife.bind(this);
        personId = getIntent().getStringExtra("teacherID");
        name = getIntent().getStringExtra("titleName");
        teacherImage = getIntent().getStringExtra("teacherImage");
        mView = new ArrayList<View>();
        mPager.addOnPageChangeListener(new MyPageChangeListener());
        tv_introduce.setOnClickListener(this);
        tv_evaluate.setOnClickListener(this);
        tv_list.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        loadData(Integer.parseInt(personId));
        initData();
        setTabLineWidth();
    }

    private void loadData(int teacherId) {
        showProgress();
        DataController.getInstance().getTeacherJieshao(teacherId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TeacherJieshaoBean>() {

                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        Log.e("onError", "" + e.getMessage().toString());
                    }

                    @Override
                    public void onNext(TeacherJieshaoBean teacherJieshaoBean) {
                        if (teacherJieshaoBean.code == 1000000) {
                            Log.e("codecode", teacherJieshaoBean.code + "");
                            data = teacherJieshaoBean.data;
                            initNetLoading();
                            hideProgress();
                        }
                    }
                });
    }


    private void initNetLoading() {
        introduceView = new TeacherIntroduceView(mActivity, mView.get(0), personId,data);
        evaluateView = new TeacherEvaluateView(mActivity, mView.get(1), personId);
        listView = new TeacherListView(mActivity, mView.get(2), personId);
        initLoadView(sum);
    }

    private void initData() {
        LayoutInflater mInflater = getLayoutInflater();
        mView.add(mInflater.inflate(R.layout.vp_teacherintroduce, null));
        mView.add(mInflater.inflate(R.layout.vp_teacherevaluate, null));
        mView.add(mInflater.inflate(R.layout.vp_teacherlist, null));
        mPager.setAdapter(new MyPagerAdapter(mView));
        mPager.setOffscreenPageLimit(2);
        mPager.setCurrentItem(0);
        if (!TextUtils.isEmpty(name)) {

            tv_teachername.setText(name);
        }
        if (!TextUtils.isEmpty(teacherImage)) {
        /*    Glide.with(TeacherListDetailActivity.this)
                    .load(teacherImage)
                    .transform(new CircleTransform(TeacherListDetailActivity.this))
                    .placeholder(R.mipmap.image11)
                    .error(R.mipmap.image11)
                    .skipMemoryCache(false)
                    .placeholder(R.mipmap.image11)
                    .crossFade()
                    .error(R.mipmap.image11)
                    .into(iv_teacher);*/

            ImageLoad.displaynoCacheUserAvater(TeacherListDetailActivity.this,teacherImage,iv_teacher,R.mipmap.user_default_avater);
        }

    }

    private void setTabLineWidth() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        tabLineWidth = width / mView.size();
        imageview_tab_line.getLayoutParams().width = tabLineWidth - (int) ((float) (width * 2 * 6.94) / 100);
        imageview_tab_line.requestLayout();
    }

    private boolean initLoadView(int id) {
        mPager.setCurrentItem(id);
        switch (id) {
            case 0:
                tv_introduce.setTextColor(Color.parseColor("#e9304e"));
                tv_evaluate.setTextColor(Color.parseColor("#333333"));
                tv_list.setTextColor(Color.parseColor("#333333"));
                break;
            case 1:
                tv_introduce.setTextColor(Color.parseColor("#333333"));
                tv_evaluate.setTextColor(Color.parseColor("#e9304e"));
                tv_list.setTextColor(Color.parseColor("#333333"));
                break;
            case 2:
                tv_introduce.setTextColor(Color.parseColor("#333333"));
                tv_evaluate.setTextColor(Color.parseColor("#333333"));
                tv_list.setTextColor(Color.parseColor("#e9304e"));
                break;

            default:
                break;
        }
        ViewAnimHelper.setTranslationX(imageview_tab_line, id * tabLineWidth);

        return false;
    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     *
     * @author Administrator
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        public void onPageSelected(int position) {
            sum = position;
            initLoadView(sum);
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            int len = tabLineWidth * arg0 + arg2 / mView.size();
            ViewAnimHelper.setTranslationX(imageview_tab_line, len);
        }
    }

    /**
     * 页面适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));

        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);

            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
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
            case R.id.ib_back:
                finish();
                break;
            case R.id.tv_introduce:
                initLoadView(0);
                break;
            case R.id.tv_evaluate:
                initLoadView(1);
                break;
            case R.id.tv_list:
                initLoadView(2);
                break;

        }
    }

    /**
     * @param activity
     * @param personID 教师id
     * @param name     教师姓名
     * @param image    教师头像
     */
    public static void newIntent(Context activity, String personID, String name, String image) {
        Intent intent = new Intent();
        intent.setClass(activity, TeacherListDetailActivity.class);
        intent.putExtra("teacherID", personID);//id、
        intent.putExtra("titleName", name);//姓名
        intent.putExtra("teacherImage", image);//头像
        activity.startActivity(intent);
    }


}
