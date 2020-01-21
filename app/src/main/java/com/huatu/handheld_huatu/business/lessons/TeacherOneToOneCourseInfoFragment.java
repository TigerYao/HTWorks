package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
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

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class TeacherOneToOneCourseInfoFragment extends BaseFragment {
    @BindView(R.id.one_to_one_title_bar)
    TopActionBar topActionBar;
    //姓名
    @BindView(R.id.one_to_one_name_edit)
    EditText editName;
    //性别
    @BindView(R.id.one_to_one_sex_tv)
    TextView btnSex;
    //学历
    @BindView(R.id.one_to_one_grade_tv)
    TextView tvGrade;
    //专业
    @BindView(R.id.one_to_one_major_edit)
    EditText editMajor;
    //电话
    @BindView(R.id.one_to_one_phone_edit)
    EditText editPhone;
    //课程名称
    @BindView(R.id.one_to_one_course_name_tv)
    TextView tvCourseName;
    //相关考试经历
    @BindView(R.id.one_to_one_experience_edit)
    EditText editExperience;
    //报考学段
    @BindView(R.id.one_to_one_stage_tv)
    TextView tvStage;
    //报考科目
    @BindView(R.id.one_to_one_exam_subject_edit)
    EditText editSubject;
    //报考地区
    @BindView(R.id.one_to_one_exam_location_edit)
    EditText editLocation;
    //可上课时段
    @BindView(R.id.one_to_one_exercise_time_tv)
    TextView tv_exercise_time;
    //提交
    @BindView(R.id.ll_submit)
    LinearLayout ll_submit;
    @BindView(R.id.tv_complete_submit)
    TextView tv_complete_submit;


    private CustomConfirmDialog exitDlg;
    private OptionsPickerView pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private int selectorType = 0;//1:学历 2：性别 3:报考学段 4:可上课时段
    private boolean isEdit;
    private String courseId;
    private int NetClassCategoryId;//课程类型id
    private String courseName;
    private String userName;
    private String orderNumber;
    private int genderFlag = 0;//默认女 0女 1男
    private String mSex[] = new String[]{"男", "女"};//相关考试经历
    private String mEducations[] = new String[]{"研究生以上", "大学本科", "大学专科", "高职以下"};//学历
    private String mStage[] = new String[]{"幼儿", "小学", "初中", "高中"};//报考学段
    private String mClassTime[] = new String[]{"工作日（全天）","工作日（上午）","工作日（下午）","工作日（晚上）","周末（全天）","周末（上午）","周末（下午）","周末（晚上）","任意时段"};//可上课时段
    private int gradeFlag = 2;
    private String phone;
    private OneToOneInfoBean oneToOneInfoBean;
    private int stageFlag=1;
    private int classTimeFlag=-1;
    private String experience;
    private String major;
    private String subject;
    private String location;
    private boolean isAgree=true;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_teacher_one_to_one_info_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        isEdit = args.getBoolean("is_edit", true);
        courseId = args.getString("course_id");
        NetClassCategoryId = args.getInt("NetClassCategoryId");
        courseName = args.getString("course_name");
        tvCourseName.setText(courseName);
        orderNumber = args.getString("order_number");
        initTitleBar();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        oneToOneInfoBean = new OneToOneInfoBean();
        oneToOneInfoBean.OrderNum = orderNumber;
        oneToOneInfoBean.NetClassCategoryId = NetClassCategoryId;
        if(!isEdit) {
            editName.setEnabled(false);
            btnSex.setEnabled(false);
            tvGrade.setEnabled(false);
            editMajor.setEnabled(false);
            editPhone.setEnabled(false);
            tvCourseName.setEnabled(false);
            editExperience.setEnabled(false);
            tvStage.setEnabled(false);
            editSubject.setEnabled(false);
            editLocation.setEnabled(false);
            tv_exercise_time.setEnabled(false);
        }
    }

    private void initTitleBar() {
        if(!isEdit) {
            ll_submit.setVisibility(View.GONE);
        } else {
            ll_submit.setVisibility(View.VISIBLE);
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
        //姓名
        if(!TextUtils.isEmpty(oneToOneInfoBean.UserReName)) {
            editName.setText(oneToOneInfoBean.UserReName);
        }
        //性别
        if(oneToOneInfoBean.Sex==0) {
            btnSex.setText("女");
        }else {
            btnSex.setText("男");
        }
        //学历
        gradeFlag = oneToOneInfoBean.Edu - 1;
        if(gradeFlag >= 0 && gradeFlag < mEducations.length) {
            tvGrade.setText(mEducations[gradeFlag]);
        }

        //专业
        if(!TextUtils.isEmpty(oneToOneInfoBean.major)) {
            editMajor.setText(oneToOneInfoBean.major);
        }
        //电话
        if(!TextUtils.isEmpty(oneToOneInfoBean.Telephone)) {
            editPhone.setText(oneToOneInfoBean.Telephone);
        }
        //课程名称
        tvCourseName.setText(courseName);

        //相关考试经历
        if(!TextUtils.isEmpty(oneToOneInfoBean.ExamExperience)) {
            editExperience.setText(oneToOneInfoBean.ExamExperience);
        }
        //报考学段
        stageFlag = oneToOneInfoBean.stage - 1;
        if(stageFlag >= 0 && stageFlag < mStage.length) {
            tvStage.setText(mStage[stageFlag]);
        }
        //报考科目
        if(!TextUtils.isEmpty(oneToOneInfoBean.subject)) {
            editSubject.setText(oneToOneInfoBean.subject);
        }
        //报考地区
        if(!TextUtils.isEmpty(oneToOneInfoBean.area)) {
            editLocation.setText(oneToOneInfoBean.area);
        }
        //可上课时段
        if(!TextUtils.isEmpty(oneToOneInfoBean.classTime)) {
            tv_exercise_time.setText(oneToOneInfoBean.classTime);
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
                    //学历
                    gradeFlag = options1 + 1;
                    tvGrade.setText(mEducations[options1]);
                } else if(selectorType == 2) {
                    //性别
                    genderFlag = options1;
                    btnSex.setText(mSex[options1]);
                } else if(selectorType == 3) {
                    //报考学段
                    stageFlag = options1 + 1;
                    tvStage.setText(mStage[options1]);
                } else if(selectorType == 4) {
                    //可上课时段
                    classTimeFlag=options1;
                    tv_exercise_time.setText(mClassTime[options1]);
                }
            }
        });
    }

 @OnClick(R.id.tv_complete_submit)
    public void onClickSubmit() {
        onClickConfirm();
    }

    @OnClick(R.id.one_to_one_exercise_time_tv)
    public void onClickTime() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 4;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mClassTime));
        pvOptions.setTitle("可上课时间段");
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }

    @OnClick(R.id.one_to_one_stage_tv)
    public void onClickInterview() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 3;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mStage));
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setTitle("报考学段");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.show();
    }

    @OnClick(R.id.one_to_one_sex_tv)
    public void onClickExperience() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        selectorType = 2;
        initSelector();
        options1Items.clear();
        options1Items.addAll(Arrays.asList(mSex));
        pvOptions.setPicker(options1Items, null, null, true);
        pvOptions.setTitle("性别");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("onActivityResult");
//        if(resultCode == Activity.RESULT_OK && data != null) {
//            requirement = data.getStringExtra("require_content");
//            userReqireEditText = data.getStringExtra("require_user_edit");
//            userRequireTimeIndex = data.getIntExtra("require_time_index", -1);
//            userRequireTypeIndex = data.getIntExtra("require_type_index", -1);
//            userRequireLengthIndex = data.getIntExtra("require_length_index", -1);
//            if(TextUtils.isEmpty(requirement)) {
//                requirement = "";
//            }
//            tvRequirement.setText(requirement);
//        }
    }

    public void onClickConfirm() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        userName = editName.getText().toString().trim();
        if(TextUtils.isEmpty(userName)) {
            ToastUtils.showShort("请填写你的真实姓名哦~");
            return;
        }
        if(gradeFlag == 0) {
            ToastUtils.showShort("请选择学历");
            return;
        }
        major=editMajor.getText().toString().trim();
        if (TextUtils.isEmpty(major)){
            ToastUtils.showShort("请填写你的专业");
            return;
        }
        phone = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort("请填写手机号码");
            return;
        }
        if(!Method.isPhoneValid(phone)) {
            ToastUtils.showShort("手机号格式不正确哦");
            return;
        }

        experience=editExperience.getText().toString().trim();

        if(stageFlag == 0) {
            ToastUtils.showShort("请选择报考学段");
            return;
        }
        subject=editSubject.getText().toString().trim();
        if (TextUtils.isEmpty(subject)){
            ToastUtils.showShort("请填写报考科目");
            return;
        }
        location=editLocation.getText().toString().trim();
        if (TextUtils.isEmpty(location)){
            ToastUtils.showShort("请填写报考地区，精确到区县哦~");
            return;
        }
        if (classTimeFlag==-1||classTimeFlag>=mClassTime.length){
            ToastUtils.showShort("请选择你可上课的时间段");
            return;
        }
        mActivity.showProgress();
        oneToOneInfoBean.UserReName = userName;
        oneToOneInfoBean.Sex = genderFlag;
        oneToOneInfoBean.Edu = gradeFlag;
        oneToOneInfoBean.major = major;
        oneToOneInfoBean.Telephone = phone;
        oneToOneInfoBean.NetClassName = courseName;
        oneToOneInfoBean.ExamExperience = experience;
        oneToOneInfoBean.stage=stageFlag;
        oneToOneInfoBean.subject = subject;
        oneToOneInfoBean.area = location;
        oneToOneInfoBean.classTime = mClassTime[classTimeFlag];

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

        ServiceExProvider.visitSimple(compositeSubscription, RetrofitManager.getInstance().getService()
                        .setOneToOneInfo(courseId,oneToOneInfoBean),
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
