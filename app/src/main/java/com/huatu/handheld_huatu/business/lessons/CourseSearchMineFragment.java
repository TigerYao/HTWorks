package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.ClearEditText;
import com.huatu.utils.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2017/10/9.
 */
@Deprecated
public class CourseSearchMineFragment extends BasePurchasedFragment {
    @BindView(R.id.search_purchased_course_cancel_btn)
    TextView btnSearch;
    @BindView(R.id.search_purchased_course_edit)
    ClearEditText editText;
/*    @BindView(R.id.search_purchased_course_tips_tv)
    TextView tvTips;*/

    private InputMethodManager imm;
    private String keyword;
    private boolean isChanged = false;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_search_purchased_course_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterViewVisible(false);
        imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (courseType == 0){
            editText.setHint("搜索我的课程或老师名字");
        }else if (courseType == 1){
            editText.setHint("搜索直播课程或老师名字");
        }else{
            editText.setHint("搜索录播课程或老师名字");
        }
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClickSearch();
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btnSearch.setText("搜索");
                    keyword = editText.getText().toString().trim();
                } else {
                    btnSearch.setText("取消");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(imm != null) {
                    editText.requestFocus();
                    imm.showSoftInput(editText, 0);
                }
            }
        }, 300);
    }

    @Override
    public void getData(final boolean isRefresh) {
        if(TextUtils.isEmpty(keyword)) {
            listView.stopRefresh();
            return;
        }
        if(isRefresh) {
            curPage = 1;
        } else {
            curPage++;
        }
        ServiceProvider.searchMyCourseList(compositeSubscription, keyword, courseType,
                curPage, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                CourseMineBean dataBean = (CourseMineBean) model.data;
                CourseSearchMineFragment.this.onSuccess(dataBean.result, isRefresh);
                if(dataBean.next == 1) {
                    listView.setPullLoadEnable(true);
                } else {
                    listView.setPullLoadEnable(false);
                }
                layoutErrorView.setErrorImageVisible(true);
                layoutErrorView.setErrorImage(R.drawable.no_data_bg);
                String as_detail = StringUtils.fontColor("#9B9B9B", "暂无相关课程") ;
                layoutErrorView.setErrorText(StringUtils.forHtml(as_detail));
            }

            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                if(isRefresh) {
                    dataList.clear();
                }
                CourseSearchMineFragment.this.onLoadDataFailed();
                layoutErrorView.setErrorImageMargin(120);
                layoutErrorView.setErrorImageVisible(true);
                layoutErrorView.setErrorImage(R.drawable.icon_common_net_unconnected);
                String as_detail = StringUtils.fontColor("#9B9B9B", "网络加载出错") ;
                layoutErrorView.setErrorText(StringUtils.forHtml(as_detail));
            }
        });
    }

    @OnClick(R.id.search_purchased_course_cancel_btn)
    public void onClickSearch() {
        if("取消".equals(btnSearch.getText())) {
            finishFrg();
            return;
        }
        keyword = editText.getText().toString().trim();
     /*   tvTips.setVisibility(View.VISIBLE);
        if(courseType == 1) {
            tvTips.setText("在我的课程-直播中搜索“" + keyword + "”的结果为:");
        } else if(courseType == 2) {
            tvTips.setText("在我的课程-录播中搜索“" + keyword + "”的结果为:");
        } else {
            tvTips.setText("在我的课程中搜索“" + keyword + "”的结果为:");
        }*/
        btnSearch.setText("取消");
        mActivity.showProgress();
        getData(true);
    }

    private void finishFrg() {
        if(isChanged) {
            setResultForTargetFrg(Activity.RESULT_OK);
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
        }
        Method.hideKeyboard(mActivity.getCurrentFocus());
    }

    @Override
    public boolean onBackPressed() {
        finishFrg();
        return true;
    }
}
