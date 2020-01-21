package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.me.bean.ExchangeVoucherBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.GetEvaluateBean;
import com.huatu.handheld_huatu.network.DataController;

import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht-djd on 2017/9/16.
 * 课程评价提交页面
 */

public class LessionEvaluateActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.tv_quxiao)
    TextView tv_quxiao;
    @BindView(R.id.tv_queding)
    TextView tv_queding;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_teachername)
    TextView tv_teachername;
    @BindView(R.id.iv_teacher)
    ImageView iv_teacher;
    @BindView(R.id.iv_star1)
    ImageView iv_star1;
    @BindView(R.id.iv_star2)
    ImageView iv_star2;
    @BindView(R.id.iv_star3)
    ImageView iv_star3;
    @BindView(R.id.iv_star4)
    ImageView iv_star4;
    @BindView(R.id.iv_star5)
    ImageView iv_star5;
    @BindView(R.id.rl_star1)
    RelativeLayout rl_star1;
    @BindView(R.id.rl_star2)
    RelativeLayout rl_star2;
    @BindView(R.id.rl_star3)
    RelativeLayout rl_star3;
    @BindView(R.id.rl_star4)
    RelativeLayout rl_star4;
    @BindView(R.id.rl_star5)
    RelativeLayout rl_star5;
    @BindView(R.id.et_comment_content)
    EditText et_comment_content;
    @BindView(R.id.tv_num)
    TextView tv_num;
    @BindView(R.id.tv_remain_count)
    TextView tv_remain_count;
    private String classid;
    private String lessionid;
    private List<ImageView> starList = new ArrayList<>();
    private int score = 5;
    private CharSequence temp;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_lessionevalueate;
    }

    @Override
    protected void onInitView() {
        ButterKnife.bind(this);
        classid = getIntent().getStringExtra("classid");
        lessionid = getIntent().getStringExtra("lessionid");
        tv_quxiao.setOnClickListener(this);
        tv_queding.setOnClickListener(this);
        rl_star1.setOnClickListener(this);
        rl_star2.setOnClickListener(this);
        rl_star3.setOnClickListener(this);
        rl_star4.setOnClickListener(this);
        rl_star5.setOnClickListener(this);

        et_comment_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() >= 200) {
                    CommonUtils.showToast("您输入的字符超出了限制!");
                    tv_num.setVisibility(View.GONE);
                    tv_remain_count.setVisibility(View.GONE);
                } else {
                    tv_num.setVisibility(View.VISIBLE);
                    tv_remain_count.setVisibility(View.VISIBLE);
                    tv_num.setText(temp.length() + "");
                }
            }
        });
    }

    @Override
    protected void onLoadData() {
        showProgress();
        getEvaluate(Integer.parseInt(classid), Integer.parseInt(lessionid));

    }

    private void getEvaluate(int classid, int lessionid) {
        DataController.getInstance().getEvaluateContent(classid, lessionid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetEvaluateBean>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                    }

                    @Override
                    public void onNext(GetEvaluateBean getEvaluateBean) {
                        //Log.d("getCode", getEvaluateBean.getCode());
                        if (getEvaluateBean.getCode() == 1000000) {
                            String teachername = getEvaluateBean.getData().getTeachername();
                            String courseRemark = getEvaluateBean.getData().getCourseRemark();
                            String photo_url = getEvaluateBean.getData().getPhoto_url();
                            int star = getEvaluateBean.getData().getCoursescore();
                            String lessiontitle = getEvaluateBean.getData().getLessiontitle();
                            initView(teachername, courseRemark, star, photo_url, lessiontitle);
                            hideProgress();
                        }
                    }


                });

    }

    private void initView(String teachername, String courseRemark, int star, String photo_url, String lessiontitle) {
        tv_title.setText(lessiontitle);
        tv_teachername.setText(teachername);
        if (!TextUtils.isEmpty(courseRemark)) {
            et_comment_content.setHint(courseRemark);
        } else {
            et_comment_content.setHint("课程内容如何?老师讲的怎么样？快来说说感受吧~");
        }
        starList.add(iv_star1);
        starList.add(iv_star2);
        starList.add(iv_star3);
        starList.add(iv_star4);
        starList.add(iv_star5);
        if (String.valueOf(star).equals("1")) {
            starList.get(0).setBackgroundResource(R.drawable.teacher_kxing);
        } else if (String.valueOf(star).equals("0")) {
            starList.get(0).setBackgroundResource(R.drawable.teacher_kxing);
        } else {
            for (int i = 1; i <= star / 2; i++) {
                if (star % 2 == 1) {
                    starList.get(i - 1).setBackgroundResource(R.drawable.teacher_xing);
                    starList.get(i).setBackgroundResource(R.drawable.teacher_bbxing);

                } else if (star % 2 == 0) {
                    starList.get(i - 1).setBackgroundResource(R.drawable.teacher_xing);
                }
            }
        }
/*        Glide.with(LessionEvaluateActivity.this)
                .load(photo_url)
                .transform(new CircleTransform(LessionEvaluateActivity.this))
                .placeholder(R.mipmap.image11)
                .error(R.mipmap.image11)
                .skipMemoryCache(false)
                .placeholder(R.mipmap.image11)
                .crossFade()
                .error(R.mipmap.image11)
                .into(iv_teacher);*/
        ImageLoad.displaynoCacheImage(LessionEvaluateActivity.this,R.mipmap.user_default_avater,photo_url,iv_teacher);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_quxiao:
                CourseEvaluateActivity.newIntent(LessionEvaluateActivity.this, classid, lessionid);
                overridePendingTransition(R.anim.fade_in_left,
                        R.anim.fade_out_right);
                finish();
                break;
            case R.id.rl_star1:
                score = 1;
                setStar(1);
                break;
            case R.id.rl_star2:
                score = 2;
                setStar(2);
                break;
            case R.id.rl_star3:
                score = 3;
                setStar(3);
                break;
            case R.id.rl_star4:
                score = 4;
                setStar(4);
                break;
            case R.id.rl_star5:
                score = 5;
                setStar(5);
                break;
            case R.id.tv_queding:
                /*if (et_comment_content.getText() == null || TextUtils.isEmpty(et_comment_content.getText().toString())) {
                    CommonUtils.showToast("总要写点什么的吧~~");
                    return;
                }*/
                commitEvaluate(Integer.parseInt(classid), Integer.parseInt(lessionid), et_comment_content.getText().toString(), score, 0);
                break;
        }
    }

    private void commitEvaluate(final int classid, final int lessionid, String et_comment_content, int score, int type) {

        LogUtils.e("commitEvaluate",classid+","+lessionid+""+et_comment_content);
        DataController.getInstance().commitEvaluate(classid, lessionid, et_comment_content, score, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExchangeVoucherBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExchangeVoucherBean exchangeVoucherBean) {
                        if (exchangeVoucherBean.code == 1000000) {
                            SharedPreferences sharedPreferences = getSharedPreferences(
                                    UserInfoUtil.userName, MODE_PRIVATE);
                            sharedPreferences.edit().putBoolean(lessionid + "judge", true).commit();
                            onBack();
                        } else {
                            CommonUtils.showToast(exchangeVoucherBean.message);
                        }
                    }

                });
    }

    private void onBack() {
        CommonUtils.showToast("提交成功");
        CourseEvaluateActivity.newIntent(LessionEvaluateActivity.this, classid, lessionid);
        overridePendingTransition(R.anim.fade_in_left,
                R.anim.fade_out_right);
        if (SpUtils.getCourseJudgeFlag()) {
            ToastUtils.showRewardToast("EVALUATE");
            SpUtils.setCourseJudgeFlag(false);
        }
        finish();
    }

    private void setStar(int index) {
        switch (index) {
            case 0:
                score = 0;
                iv_star1.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star2.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star3.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star4.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star5.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                break;
            case 1:
                score = 1;
                iv_star1.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star2.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star3.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star4.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star5.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                break;
            case 2:
                score = 2;
                iv_star1.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star2.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star3.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star4.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star5.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                break;
            case 3:
                score = 3;
                iv_star1.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star2.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star3.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star4.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                iv_star5.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                break;
            case 4:
                score = 4;
                iv_star1.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star2.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star3.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star4.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star5.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_kxing));
                break;
            case 5:
                score = 5;
                iv_star1.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star2.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star3.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star4.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                iv_star5.setBackgroundDrawable(getResources().getDrawable(R.drawable.teacher_xing));
                break;
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
    public void onBackPressed() {
        super.onBackPressed();
        CourseEvaluateActivity.newIntent(LessionEvaluateActivity.this, classid, lessionid);
        overridePendingTransition(R.anim.fade_in_left,
                R.anim.fade_out_right);
        finish();
    }

    /**
     * @param context
     * @param classid   课程id
     * @param lessionid 课件id
     */
    public static void newIntent(Context context, String classid, String lessionid) {
        Intent intent = new Intent(context, LessionEvaluateActivity.class);
        intent.putExtra("classid", classid);
        intent.putExtra("lessionid", lessionid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }


}
