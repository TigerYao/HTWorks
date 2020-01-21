package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2018/3/27.
 */

public class ProvinceFaceToFaceFragment extends BaseFragment {
    @BindView(R.id.face_to_face_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.face_to_face_apply_tv)
    TextView tvApply;
    @BindView(R.id.face_to_face_apply_line_1)
    View viewApplyLine1;
    @BindView(R.id.face_to_face_apply_view)
    View viewApplyCircle;
    @BindView(R.id.face_to_face_apply_line_2)
    View viewApplyLine2;
    @BindView(R.id.face_to_face_verify_tv)
    TextView tvVerify;
    @BindView(R.id.face_to_face_verify_line_1)
    View viewVerifyLine1;
    @BindView(R.id.face_to_face_verify_view)
    View viewVerifyCircle;
    @BindView(R.id.face_to_face_verify_line_2)
    View viewVerifyLine2;
    @BindView(R.id.face_to_face_pass_tv)
    TextView tvPass;
    @BindView(R.id.face_to_face_name_edit)
    EditText editName;
    @BindView(R.id.face_to_face_id_edit)
    EditText editId;
    @BindView(R.id.face_to_face_score_edit)
    EditText editScore;
    @BindView(R.id.face_to_face_rank_edit)
    EditText editRank;
    @BindView(R.id.face_to_face_confirm_btn)
    TextView btnConfirm;
    private CustomConfirmDialog exitDlg;
    private String provinceId;
    private String strName;
    private String strId;
    private String strScore;
    private String strRank;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_province_face_to_face_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        provinceId = args.getString("province_id");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
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


    @OnClick(R.id.face_to_face_confirm_btn)
    public void onClickConfirm() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
        strName = editName.getText().toString().trim();
        if(TextUtils.isEmpty(strName)) {
            ToastUtils.showShort("请填写姓名");
            return;
        }
        strId = editId.getText().toString().trim();
        if(TextUtils.isEmpty(strId)) {
            ToastUtils.showShort("请填写准考证号");
            return;
        }
        strScore = editScore.getText().toString().trim();
        if(TextUtils.isEmpty(strScore)) {
            ToastUtils.showShort("请填写笔试总分");
            return;
        }
        strRank = editRank.getText().toString().trim();
//        if(TextUtils.isEmpty(strRank)) {
//            ToastUtils.showShort("请填写笔试排名");
//            return;
//        }
        mActivity.showProgress();
        ServiceProvider.applyFaceToFace(compositeSubscription, provinceId, strName, strId, strScore, strRank, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                Bundle arg = new Bundle(args);
                arg.putBoolean("is_verify_passed", false);
                ProvinceFaceToFaceVerifyFragment fragment = new ProvinceFaceToFaceVerifyFragment();
                fragment.setArguments(arg);
                startFragmentForResult(fragment);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                Bundle arg = new Bundle(args);
                arg.putBoolean("is_verify_passed", true);
                arg.putInt("sid", (int) model.data);
                ProvinceFaceToFaceVerifyFragment fragment = new ProvinceFaceToFaceVerifyFragment();
                fragment.setArguments(arg);
                startFragmentForResult(fragment);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            setResultForTargetFrg(Activity.RESULT_OK);
        }
    }

    @Override
    public boolean onBackPressed() {
        Method.hideKeyboard(mActivity.getCurrentFocus());
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
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(exitDlg != null) {
            exitDlg.dismiss();
            exitDlg = null;
        }
    }

    public static void show(Context context, Bundle arg) {
        BaseFrgContainerActivity.newInstance(context, ProvinceFaceToFaceFragment.class.getName(), arg);
    }
}
