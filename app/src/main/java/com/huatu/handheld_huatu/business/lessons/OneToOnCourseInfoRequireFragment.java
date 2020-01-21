package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2017/10/1.
 */

public class OneToOnCourseInfoRequireFragment extends BaseFragment {
    @BindView(R.id.one_to_one_require_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.one_to_one_require_edit)
    EditText editRequire;
    @BindView(R.id.one_to_one_require_time_nine)
    TextView btnTimeNine;
    @BindView(R.id.one_to_one_require_time_half_one)
    TextView btnTimeHalfOne;
    @BindView(R.id.one_to_one_require_time_nineteen)
    TextView btnTimeNineteen;
    @BindView(R.id.one_to_one_require_content_shen)
    TextView btnShenLun;
    @BindView(R.id.one_to_one_require_content_xing)
    TextView btnXingCe;
    @BindView(R.id.one_to_one_require_content_interview)
    TextView btnInterview;
    @BindView(R.id.one_to_one_require_time_length_half)
    TextView btnLengthHalf;
    @BindView(R.id.one_to_one_require_time_length_one)
    TextView btnLengthOne;
    @BindView(R.id.one_to_one_require_time_length_two)
    TextView btnLengthTwo;

    private String content;
    private int colorWhite;
    private int colorMain;
    private int timeIndex = -1;
    private String strTime;
    private int typeIndex = -1;
    private String strContent;
    private int lengthIndex = -1;
    private String strLength;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_one_to_one_info_require_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        colorWhite = getContext().getResources().getColor(R.color.white);
        colorMain = getContext().getResources().getColor(R.color.main_color);
        initTitleBar();
        content = args.getString("require_user_edit");
        if(!TextUtils.isEmpty(content)) {
            editRequire.setText(content);
        }
        timeIndex = args.getInt("require_time_index", -1);
        if(timeIndex == 0) {
            onClickTimeNine();
        } else if(timeIndex == 1) {
            onClickTimeHalfOne();
        } else if(timeIndex == 2) {
            onClickTimeNineteen();
        }
        typeIndex = args.getInt("require_type_index", -1);
        if(typeIndex == 0) {
            onClickShenLun();
        } else if(typeIndex == 1) {
            onClickXingCe();
        } else if(typeIndex == 2) {
            onClickInterview();
        }
        lengthIndex = args.getInt("require_length_index", -1);
        if(lengthIndex == 0) {
            onClickLengthHalf();
        } else if(lengthIndex == 1) {
            onClickLengthOne();
        } else if(lengthIndex == 2) {
            onClickLengthTwo();
        }
    }

    private void initTitleBar() {
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_CANCELED);
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

    @OnClick(R.id.one_to_one_require_time_nine)
    public void onClickTimeNine() {
        btnTimeNine.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnTimeNine.setTextColor(colorWhite);
        btnTimeHalfOne.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnTimeHalfOne.setTextColor(colorMain);
        btnTimeNineteen.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnTimeNineteen.setTextColor(colorMain);
        strTime = btnTimeNine.getText().toString();
        timeIndex = 0;
    }

    @OnClick(R.id.one_to_one_require_time_half_one)
    public void onClickTimeHalfOne() {
        btnTimeHalfOne.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnTimeHalfOne.setTextColor(colorWhite);
        btnTimeNine.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnTimeNine.setTextColor(colorMain);
        btnTimeNineteen.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnTimeNineteen.setTextColor(colorMain);
        strTime = btnTimeHalfOne.getText().toString();
        timeIndex = 1;
    }

    @OnClick(R.id.one_to_one_require_time_nineteen)
    public void onClickTimeNineteen() {
        btnTimeNineteen.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnTimeNineteen.setTextColor(colorWhite);
        strTime = btnTimeNineteen.getText().toString();
        btnTimeNine.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnTimeNine.setTextColor(colorMain);
        btnTimeHalfOne.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnTimeHalfOne.setTextColor(colorMain);
        timeIndex = 2;
    }

    @OnClick(R.id.one_to_one_require_content_shen)
    public void onClickShenLun() {
        btnShenLun.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnShenLun.setTextColor(colorWhite);
        strContent = btnShenLun.getText().toString();
        btnXingCe.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnXingCe.setTextColor(colorMain);
        btnInterview.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnInterview.setTextColor(colorMain);
        typeIndex = 0;
    }

    @OnClick(R.id.one_to_one_require_content_xing)
    public void onClickXingCe() {
        btnXingCe.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnXingCe.setTextColor(colorWhite);
        strContent = btnXingCe.getText().toString();
        btnShenLun.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnShenLun.setTextColor(colorMain);
        btnInterview.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnInterview.setTextColor(colorMain);
        typeIndex = 1;
    }

    @OnClick(R.id.one_to_one_require_content_interview)
    public void onClickInterview() {
        btnInterview.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnInterview.setTextColor(colorWhite);
        strContent = btnInterview.getText().toString();
        btnShenLun.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnShenLun.setTextColor(colorMain);
        btnXingCe.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnXingCe.setTextColor(colorMain);
        typeIndex = 2;
    }

    @OnClick(R.id.one_to_one_require_time_length_half)
    public void onClickLengthHalf() {
        btnLengthHalf.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnLengthHalf.setTextColor(colorWhite);
        strLength = btnLengthHalf.getText().toString();
        btnLengthOne.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnLengthOne.setTextColor(colorMain);
        btnLengthTwo.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnLengthTwo.setTextColor(colorMain);
        lengthIndex = 0;
    }

    @OnClick(R.id.one_to_one_require_time_length_one)
    public void onClickLengthOne() {
        btnLengthOne.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnLengthOne.setTextColor(colorWhite);
        strLength = btnLengthOne.getText().toString();
        btnLengthHalf.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnLengthHalf.setTextColor(colorMain);
        btnLengthTwo.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnLengthTwo.setTextColor(colorMain);
        lengthIndex = 1;
    }

    @OnClick(R.id.one_to_one_require_time_length_two)
    public void onClickLengthTwo() {
        btnLengthTwo.setBackgroundResource(R.drawable.bg_rectangle_e9034e);
        btnLengthTwo.setTextColor(colorWhite);
        strLength = btnLengthTwo.getText().toString();
        btnLengthHalf.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnLengthHalf.setTextColor(colorMain);
        btnLengthOne.setBackgroundResource(R.drawable.bg_rectangle_border_e9034e);
        btnLengthOne.setTextColor(colorMain);
        lengthIndex = 2;
    }

    private void onClickConfirm() {
        Intent intent = new Intent();
        content = "";
        content += editRequire.getText().toString().trim();
        intent.putExtra("require_user_edit", content);
        if(!TextUtils.isEmpty(strTime)) {
            content += " " + strTime;
        }
        if(!TextUtils.isEmpty(strContent)) {
            content += " " + strContent;
        }
        if(!TextUtils.isEmpty(strLength)) {
            content += " " + strLength;
        }
        content = content.trim();
        if(TextUtils.isEmpty(content)) {
            ToastUtils.showShort("写点什么吧");
            return;
        }
        intent.putExtra("require_content", content);
        intent.putExtra("require_time_index", timeIndex);
        intent.putExtra("require_type_index", typeIndex);
        intent.putExtra("require_length_index", lengthIndex);
        setResultForTargetFrg(Activity.RESULT_OK, intent);
    }
}
