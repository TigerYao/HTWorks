package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.bean.OneToOneInfoBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.view.OptionsPickerView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2017/10/1.
 */

public class OneToOnCourseInfoFragment extends BaseFragment {
    @BindView(R.id.one_to_one_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.one_to_one_name_edit)
    EditText editName;
    @BindView(R.id.one_to_one_gender_female)
    TextView btnFemale;
    @BindView(R.id.one_to_one_gender_male)
    TextView btnMale;
    @BindView(R.id.one_to_one_grade_tv)
    TextView tvGrade;
    @BindView(R.id.one_to_one_phone_edit)
    EditText editPhone;
    @BindView(R.id.one_to_one_qq_edit)
    EditText editQQ;
    @BindView(R.id.one_to_one_course_name_tv)
    TextView tvCourseName;
    @BindView(R.id.one_to_one_exam_type_edit)
    EditText editExamType;
    @BindView(R.id.one_to_one_experience_tv)
    TextView tvExperience;
    @BindView(R.id.one_to_one_interview_tv)
    TextView tvInterview;
    @BindView(R.id.one_to_one_exam_position_edit)
    EditText editPosition;
    @BindView(R.id.one_to_one_exam_require_tv)
    TextView tvRequirement;
    @BindView(R.id.one_to_one_age_tv)
    TextView tvAge;
    @BindView(R.id.one_to_one_position_number_edit)
    EditText editPositionNumber;
    @BindView(R.id.one_to_one_position_number_des)
    TextView tvPositionDes;
    @BindView(R.id.one_to_one_time_tv)
    TextView tvTime;
    @BindView(R.id.one_to_one_time_des)
    TextView tvTimeDes;
    @BindView(R.id.ll_one_to_one_position_number_des)
    View layoutPosition;
    @BindView(R.id.one_to_one_interview_ratio_layout)
    View layoutInterviewRatio;
    @BindView(R.id.ll_score)
    View layoutScore;
    @BindView(R.id.one_to_one_interview_ratio_edit)
    EditText editInterviewRatio;
    @BindView(R.id.one_to_one_score_edit)
    EditText editScore;

    private CustomConfirmDialog exitDlg;
    private OptionsPickerView pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private int selectorType = 0;//1:学历 2：考试经历 3:笔试面试 4:笔试时间
    private boolean isEdit;
    private String courseId;
    private String courseName;
    private int NetClassCategoryId;//课程类型id
    private String userName;
    private String orderNumber;
    private int genderFlag = 0;//默认女 0女 1男
    private String mEducations[] = new String[]{"研究生以上", "大学本科", "大学专科", "高职以下"};//学历
    private int gradeFlag = 0;
    private String phone;
    private String qq;
    private String examType;
    private String mExamExperiences[] = new String[]{"有", "无"};//相关考试经历
    private int experinceFlag;
    private String mWriteExams[] = new String[]{"笔试", "面试"};//面试笔试
    private int interviewFlag;
    private int ageFlag = 0;
    String positionName;
    String requirement;
    private String mYearDatas[] = new String[]{"2017年", "2018年"};
    private String mMonthDatas[] = new String[]{"1月", "2月", "3月", "4月",
            "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private String mDayDatas[] = new String[]{"1日", "2日", "3日", "4日",
            "5日", "6日", "7日", "8日", "9日", "10日", "11日", "12日", "13日", "14日",
            "15日", "16日", "17日", "18日", "19日", "20日", "21日", "22日", "23日", "24日",
            "25日", "26日", "27日", "28日", "29日", "30日", "31日"};
    private String ageData[] = new String[]{"18", "19", "20", "21", "22", "23", "24", "25",
            "26", "27", "28", "29", "30", "31", "32", "33", "34", "35"};
    String time;
    private OneToOneInfoBean oneToOneInfoBean;
    private String userReqireEditText;
    private int userRequireTimeIndex = -1;
    private int userRequireTypeIndex = -1;
    private int userRequireLengthIndex = -1;
    private String score;
    private int year;
    public static void lanuch(Context context,String courseId, String courseName, String orderNumber){

        Bundle arg = new Bundle();
        arg.putBoolean("is_edit", false);
        arg.putString("course_id", courseId);
        arg.putString("course_name", courseName);
        arg.putString("order_number",orderNumber);

        // fragment.setArguments(bundle);
        // startFragmentForResult(fragment);

        BaseFrgContainerActivity.newInstance(context,
                OneToOnCourseInfoFragment.class.getName(), arg);
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_one_to_one_info_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();

        if(!ArrayUtils.isEmpty(mYearDatas)){
            Calendar calendar = Calendar.getInstance();
             year=calendar.get(Calendar.YEAR);
            mYearDatas[0]=(year)+"年";
            mYearDatas[1]=(year+1)+"年";
        }

        isEdit = args.getBoolean("is_edit", true);
        courseId = args.getString("course_id");
        courseName = args.getString("course_name");
        NetClassCategoryId = args.getInt("NetClassCategoryId");
        tvCourseName.setText(courseName);
        orderNumber = args.getString("order_number");
        initTitleBar();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        oneToOneInfoBean = new OneToOneInfoBean();
        oneToOneInfoBean.OrderNum = orderNumber;
        oneToOneInfoBean.NetClassCategoryId = NetClassCategoryId;
        if(!isEdit) {
            editName.setEnabled(false);
            btnFemale.setEnabled(false);
            btnMale.setEnabled(false);
            tvGrade.setEnabled(false);
            editPhone.setEnabled(false);
            editQQ.setEnabled(false);
            tvCourseName.setEnabled(false);
            editExamType.setEnabled(false);
            tvExperience.setEnabled(false);
            tvInterview.setEnabled(false);
            editPosition.setEnabled(false);
            tvRequirement.setEnabled(false);
            tvAge.setEnabled(false);
            editPositionNumber.setEnabled(false);
            tvTime.setEnabled(false);
            editInterviewRatio.setEnabled(false);
            editScore.setEnabled(false);
        }
    }

    private void initTitleBar() {
        if(!isEdit) {
            topActionBar.showRightButton(false);
        } else {
            topActionBar.showRightButton(true);
        }
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
                onClickConfirm();
            }
        });
    }

    @Override
    protected void onLoadData() {
        if(isEdit) {
            return;
        }
        mActivity.showProgress();
        ServiceProvider.getOneToOneInfo(compositeSubscription, courseId, orderNumber, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                oneToOneInfoBean = (OneToOneInfoBean) model.data;
                refreshUI();
                mActivity.hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
            }
        });
    }

    private void refreshUI() {
        if(oneToOneInfoBean == null) {
            return;
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.UserReName)) {
            editName.setText(oneToOneInfoBean.UserReName);
        }
        genderFlag = oneToOneInfoBean.Sex;
        setGenderState();
        gradeFlag = oneToOneInfoBean.Edu - 1;
        if(gradeFlag >= 0 && gradeFlag < mEducations.length) {
            tvGrade.setText(mEducations[gradeFlag]);
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.Telephone)) {
            editPhone.setText(oneToOneInfoBean.Telephone);
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.QQ)) {
            editQQ.setText(oneToOneInfoBean.QQ);
        }
        tvCourseName.setText(courseName);
        if(!TextUtils.isEmpty(oneToOneInfoBean.NetClassCategory)) {
            editExamType.setText(oneToOneInfoBean.NetClassCategory);
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.ExamExperience)) {
            tvExperience.setText(oneToOneInfoBean.ExamExperience);
        }
        interviewFlag = oneToOneInfoBean.NetClassType - 1;
        if(interviewFlag >= 0 && interviewFlag < mWriteExams.length) {
            tvInterview.setText(mWriteExams[interviewFlag]);
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.ApplyJobs)) {
            editPosition.setText(oneToOneInfoBean.ApplyJobs);
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.UserBz)) {
            tvRequirement.setText(oneToOneInfoBean.UserBz);
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.Age) && Method.parseInt(oneToOneInfoBean.Age) > 0) {
            tvAge.setText(String.valueOf(oneToOneInfoBean.Age));
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.ApplyNum)) {
            editPositionNumber.setText(String.valueOf(oneToOneInfoBean.ApplyNum));
        }
        if(!TextUtils.isEmpty(oneToOneInfoBean.Examtime)) {
            tvTime.setText(oneToOneInfoBean.Examtime);
        }
        if (oneToOneInfoBean.NetClassType == 2) { //面试课下面显示3个
            tvTimeDes.setText("面试时间");
            layoutPosition.setVisibility(View.GONE);
            layoutInterviewRatio.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(oneToOneInfoBean.ViewRatio)){
                editInterviewRatio.setText(oneToOneInfoBean.ViewRatio);
            }
            layoutScore.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(oneToOneInfoBean.score)){
                editScore.setText(oneToOneInfoBean.score);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        if(isEdit) {
            showExitDlg();
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
        }
        return true;
    }

    private void showExitDlg() {
        if(exitDlg == null) {
            exitDlg = DialogUtils.createExitConfirmDialog(mActivity, null,
                    "关闭该页面将不保存您填写的信息，确认关闭吗？", "取消", "确认");
        }
        exitDlg.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultForTargetFrg(Activity.RESULT_CANCELED);
            }
        });
        exitDlg.show();
    }

    private void initSelector() {
        if(pvOptions == null) {
            pvOptions = new OptionsPickerView(mActivity);
        }
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                if(selectorType == 1) {
                    gradeFlag = options1 + 1;
                    tvGrade.setText(mEducations[options1]);
                } else if(selectorType == 2) {
                    experinceFlag = options1;
                    tvExperience.setText(mExamExperiences[options1]);
                } else if(selectorType == 3) {
                    interviewFlag = options1 + 1;
                    tvInterview.setText(mWriteExams[options1]);
                    if(interviewFlag == 1) {
                        tvPositionDes.setText("职位招聘人数");
                        tvTimeDes.setText("笔试时间");
                        layoutInterviewRatio.setVisibility(View.GONE);
                        layoutScore.setVisibility(View.GONE);
                        layoutPosition.setVisibility(View.VISIBLE);
                    } else if(interviewFlag == 2) {
                        tvTimeDes.setText("面试时间");
                        layoutInterviewRatio.setVisibility(View.VISIBLE);
                        layoutScore.setVisibility(View.VISIBLE);
                        layoutPosition.setVisibility(View.GONE);
                    }
                } else if(selectorType == 4) {
                    time = String.valueOf(year + options1) + "-";
                    if(options2 < 9) {
                        time = time + "0" + String.valueOf(options2 + 1);
                    } else {
                        time = time + String.valueOf(options2 + 1);
                    }
                    time = time + "-";
                    if(options3 < 9) {
                        time = time + "0" + String.valueOf(options3 + 1);
                    } else {
                        time = time + String.valueOf(options3 + 1);
                    }
                    time = time + " 00:00:00";
                    LogUtils.i("select time is " + time);
                    tvTime.setText(mYearDatas[options1] + mMonthDatas[options2] + mDayDatas[options3]);
                } else if(selectorType == 5) {
                    ageFlag = options1;
                    if(ageFlag >= 0 && ageFlag < ageData.length) {
                        tvAge.setText(ageData[ageFlag]);
                    }
                }
            }
        });
    }

    @OnClick(R.id.one_to_one_time_tv)
    public void onClickTime() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 4;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mYearDatas));
        options2Items.clear();
        for(int i = 0; i < mYearDatas.length; i++) {
            ArrayList<String> monthList = new ArrayList<>();
            monthList.addAll(Arrays.asList(mMonthDatas));
            options2Items.add(monthList);
            ArrayList<ArrayList<String>> options3Item2 = new ArrayList<>();
            for (int j = 0; j < mMonthDatas.length; j++) {
                ArrayList<String> options3Item_3 = new ArrayList<>();
                int dayNumber = getDayNumber(2017 + i, j + 1);
                for(int k = 0; k < dayNumber; k++) {
                    options3Item_3.add(mDayDatas[k]);
                }
                options3Item2.add(options3Item_3);
            }
            options3Items.add(options3Item2);
        }
        pvOptions.setPicker(options1Items, options2Items, options3Items, true);
        if(TextUtils.isEmpty(tvInterview.getText())) {
            pvOptions.setTitle("考试时间");
        } else {
            pvOptions.setTitle(tvInterview.getText() + "时间");
        }
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }

    private int getDayNumber(int year, int month) {
        int number;
        boolean isRun = false;
        if((year % 4 == 0) && (year % 100!=0) || (year % 400 == 0)){
            isRun = true;
        }
        if(month == 2 && isRun) {
            number = 29;
        } else if(month == 2 && !isRun) {
            number = 28;
        } else if(month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            number = 31;
        } else {
            number = 30;
        }
        return number;
    }

    @OnClick(R.id.one_to_one_interview_tv)
    public void onClickInterview() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 3;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mWriteExams));
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setTitle("笔试/面试");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }

    @OnClick(R.id.one_to_one_experience_tv)
    public void onClickExperience() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 2;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mExamExperiences));
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setTitle("考试经历");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }

    @OnClick(R.id.one_to_one_grade_tv)
    public void onClickGrade() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 1;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mEducations));
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setTitle("学历");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }

    @OnClick(R.id.one_to_one_age_tv)
    public void onClickAge() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 5;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(ageData));
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setTitle("年龄");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(ageFlag, 0, 0);
        pvOptions.show();
    }

    @OnClick(R.id.one_to_one_gender_male)
    public void onClickGenderMale() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        if(genderFlag != 1) {
            genderFlag = 1;
            setGenderState();
        }
    }

    @OnClick(R.id.one_to_one_gender_female)
    public void onClickGenderFemale() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        if(genderFlag != 0) {
            genderFlag = 0;
            setGenderState();
        }
    }

    private void setGenderState() {
        Drawable drawableChecked = getResources().getDrawable(R.drawable.icon_checked);
        Drawable drawableUnChecked = getResources().getDrawable(R.drawable.icon_check_normal);
        drawableChecked.setBounds(0, 0, drawableChecked.getMinimumWidth(),
                drawableChecked.getMinimumHeight());
        drawableUnChecked.setBounds(0, 0, drawableUnChecked.getMinimumWidth(),
                drawableUnChecked.getMinimumHeight());
        if(genderFlag == 1){
            btnMale.setCompoundDrawables(drawableChecked, null, null, null);
            btnFemale.setCompoundDrawables(drawableUnChecked, null, null, null);
        } else {
            btnFemale.setCompoundDrawables(drawableChecked, null, null, null);
            btnMale.setCompoundDrawables(drawableUnChecked, null, null, null);
        }
    }

    @OnClick(R.id.one_to_one_exam_require_tv)
    public void onClickRequire() {
        View view = mActivity.getCurrentFocus();
        Method.hideKeyboard(view);
        if(view != null) {
            view.clearFocus();
        }
        OneToOnCourseInfoRequireFragment fragment = new OneToOnCourseInfoRequireFragment();
        Bundle bundle = new Bundle();
        bundle.putString("require_user_edit", userReqireEditText);
        bundle.putInt("require_time_index", userRequireTimeIndex);
        bundle.putInt("require_type_index", userRequireTypeIndex);
        bundle.putInt("require_length_index", userRequireLengthIndex);
        fragment.setArguments(bundle);
        startFragmentForResult(fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("onActivityResult");
        if(resultCode == Activity.RESULT_OK && data != null) {
            requirement = data.getStringExtra("require_content");
            userReqireEditText = data.getStringExtra("require_user_edit");
            userRequireTimeIndex = data.getIntExtra("require_time_index", -1);
            userRequireTypeIndex = data.getIntExtra("require_type_index", -1);
            userRequireLengthIndex = data.getIntExtra("require_length_index", -1);
            if(TextUtils.isEmpty(requirement)) {
                requirement = "";
            }
            tvRequirement.setText(requirement);
        }
    }

    public void onClickConfirm() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        userName = editName.getText().toString().trim();
        if(TextUtils.isEmpty(userName)) {
            ToastUtils.showShort("请填写姓名");
            return;
        }
        if(gradeFlag == 0) {
            ToastUtils.showShort("请选择学历");
            return;
        }
        phone = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort("请填写手机号码");
            return;
        }
        if(!Method.isPhoneValid(phone)) {
            ToastUtils.showShort("手机号码无效");
            return;
        }
        if(!checkQQNumber()) {
            return;
        }
        examType = editExamType.getText().toString().trim();
        if(TextUtils.isEmpty(examType)) {
            ToastUtils.showShort("请填写考试类别");
            return;
        }
        if(TextUtils.isEmpty(tvExperience.getText())) {
            ToastUtils.showShort("请选择考试经历");
            return;
        }
        if(interviewFlag == 0) {
            ToastUtils.showShort("请选择笔试或面试");
            return;
        }
        score=editScore.getText().toString().trim();
        if (interviewFlag==2){
            if (TextUtils.isEmpty(score)){
                ToastUtils.showShort("请填写分数和名次");

            }
        }
        positionName = editPosition.getText().toString().trim();
        if(TextUtils.isEmpty(positionName)) {
            ToastUtils.showShort("请填写报考职位");
            return;
        }
        requirement = tvRequirement.getText().toString().trim();
        if(TextUtils.isEmpty(requirement)) {
            ToastUtils.showShort("请填写额外要求");
            return;
        }
        mActivity.showProgress();
        oneToOneInfoBean.ApplyJobs = positionName;
        oneToOneInfoBean.Edu = gradeFlag;
        oneToOneInfoBean.NetClassType = interviewFlag;

        oneToOneInfoBean.QQ = qq;
        oneToOneInfoBean.Sex = genderFlag;
        oneToOneInfoBean.Telephone = phone;
        oneToOneInfoBean.UserBz = requirement;
        oneToOneInfoBean.UserReName = userName;
        if(ageFlag >= 0 && ageFlag < ageData.length) {
            oneToOneInfoBean.Age = ageData[ageFlag];
        }
        oneToOneInfoBean.UserReName = userName;
        oneToOneInfoBean.ExamExperience = mExamExperiences[experinceFlag];
        oneToOneInfoBean.Examtime = time;
        oneToOneInfoBean.NetClassCategory = examType;
        oneToOneInfoBean.NetClassName = courseName;
        if(interviewFlag == 1) {
            oneToOneInfoBean.ApplyNum = editPositionNumber.getText().toString().trim();
        } else {
            oneToOneInfoBean.score = score;
            oneToOneInfoBean.ViewRatio = editInterviewRatio.getText().toString().trim();
        }
   /*     ServiceProvider.setOneToOneInfo(compositeSubscription, courseId, oneToOneInfoBean, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                setResultForTargetFrg(Activity.RESULT_OK);
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }
        });*/

        ServiceExProvider.visitSimple(compositeSubscription, RetrofitManager.getInstance().getService().setOneToOneInfo(courseId, oneToOneInfoBean),
                new NetObjResponse<String>() {
                    @Override
                    public void onSuccess(BaseResponseModel<String> model) {
                        ToastUtils.showShort("保存成功~");
                        mActivity.hideProgress();
                        setResultForTargetFrg(Activity.RESULT_OK);
                    }

                    @Override
                    public void onError(String message, int type) {
                        mActivity.hideProgress();
                        ToastUtils.showShort(message);
                    }
                });
    }

    private boolean checkQQNumber() {
        qq = editQQ.getText().toString().trim();
        if(TextUtils.isEmpty(qq)) {
            ToastUtils.showShort("请输入QQ号");
            return false;
        }
        if(qq.length() < 4 || qq.length() > 12) {
            ToastUtils.showShort("QQ位数需大于4位小于13位");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(pvOptions != null) {
            pvOptions.dismiss();
            pvOptions = null;
        }
        if(exitDlg != null) {
            exitDlg.dismiss();
            exitDlg = null;
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
