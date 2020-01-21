package com.huatu.handheld_huatu.business.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.InputMethodUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\28 0028.
 */

public   class OnLiveSearchActivity extends SimpleBaseActivity implements  TextWatcher {

    @BindView(R.id.et_search_topbar)
    EditText etSearchContent;

    @BindView(R.id.tv_right_topbar)
     TextView mRightBtn;
    //    private MainSearchFragment result;
    //    private MainSearchFragment search;
    private OnLiveHotTagFragment search;
    private OnLiveCourseSearchFragment result;

    private int mCategoryId ;
    private String mSubjectId ;
    private String orderId ;
    private ArrayList<CourseCategoryBean> categoryList=new ArrayList<>();

    public static void newIntent(Context context, int categoryId, String subjectId, String orderId, ArrayList<CourseCategoryBean> categoryList) {
        Bundle bundle=new Bundle();
        bundle.putSerializable("categoryList",categoryList);
        Intent intent = new Intent(context, OnLiveSearchActivity.class);
        intent.putExtra("categoryId",categoryId);
        intent.putExtra("subjectId",subjectId);
        intent.putExtra("orderId",orderId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }




    @Override
    protected int onSetRootViewId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         return R.layout.online_search_layout;
    }
    private Runnable showInputRunable=new Runnable() {
        @Override
        public void run() {
            if(etSearchContent!=null)
                InputMethodUtils.openKeybord(etSearchContent, etSearchContent.getContext());
        }
    };

    @Override
    protected void onInitView() {
        super.onInitView();
        mCategoryId = getIntent().getIntExtra("categoryId", 0);
        mSubjectId = getIntent().getStringExtra("subjectId");
        orderId = getIntent().getStringExtra("orderId");
        Bundle bundle = getIntent().getExtras();
        categoryList = (ArrayList<CourseCategoryBean>) bundle.getSerializable("categoryList");
        etSearchContent.setHint("搜索课程或老师名字");
        search = (OnLiveHotTagFragment) this.getSupportFragmentManager().findFragmentById(R.id.initSearch);
        etSearchContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                 if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                     if(!NetUtil.isConnected()){
                         ToastUtils.showShort("当前网络不可用");
                         return true;
                     }
                    String keyword = etSearchContent.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword)) {
                        doSearch(keyword,-1,-1);
                    }
                    return true;
                }
                return false;
             }
         });
        etSearchContent.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e("onTextChanged", "onTextChanged");
        if (charSequence.length() > 0) {
            if ("0".equals(mRightBtn.getTag())) {
                mRightBtn.setText(R.string.search);
                mRightBtn.setTag("1");
            }
           // keyword=String.valueOf(charSequence);
            Log.e("keyword", charSequence+"");
        } else {
          //  keyword="";
            if ("1".equals(mRightBtn.getTag())) {
                mRightBtn.setText(R.string.netschool_dialog_cancel);
                mRightBtn.setTag("0");
            }
        }
    }

    @OnClick({R.id.tv_right_topbar})
//    @OnClick({R.id.back, R.id.search, R.id.clean_search_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right_topbar:
                if("0".equals(view.getTag()))
                    backToSearch();
                else {

                    if(!NetUtil.isConnected()){
                        ToastUtils.showShort("当前网络不可用");
                        return;
                    }
                    String keyword = etSearchContent.getText().toString().trim();
                    doSearch(keyword,-1,-1);
                }
                break;
/*
            case R.id.search:
                String keyword = etSearchContent.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    doSearch(keyword);
                    InputMethodUtils.hideMethod(this, view);
                }
                break;*/

//            case R.id.clean_search_iv:
//                keyword = etSearchContent.getText().toString().trim();
//                if (!TextUtils.isEmpty(keyword)) {
//                    etSearchContent.setText("");
//
//                    backToSearch(false);
//                }
//                break;
        }
    }


    public void doSearch(String keyword, int isRecommend,int isHistory) {
        etSearchContent.setText(keyword);
        etSearchContent.setSelection(keyword.length());
        mRightBtn.setText(R.string.netschool_dialog_cancel);
        mRightBtn.setTag("0");
        InputMethodUtils.hideMethod(this, etSearchContent);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if (result == null) {
            result = new OnLiveCourseSearchFragment();
//        result = new MainSearchFragment();

            Bundle argument = new Bundle();
            argument.putString(ArgConstant.KEY_TITLE, keyword);
            argument.putInt(ArgConstant.KEY_ID,mCategoryId);
            argument.putInt(ArgConstant.IS_RECOMMEND,isRecommend);
            argument.putInt(ArgConstant.IS_HISTORY,isHistory);
            argument.putSerializable(ArgConstant.CATEGORY_LIST,categoryList);
            result.setArguments(argument);

            if (!result.isAdded()) {
                // 隐藏当前的fragment，显示下一个
//                if (result != null) {
//                    transaction.hide(search).show(result).commitAllowingStateLoss();
//                }
                transaction.hide(search).add(R.id.frame, result).commitAllowingStateLoss();
            }

        } else {
            //if (!result.isAdded())
            {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(search).show(result).commitAllowingStateLoss();
            }
            result.refresh(keyword,isRecommend,isHistory);
        }

        if(SpUtils.getLoginState()) {
            ServiceProvider.saveSearchLiveKeywords(getSubscription(), keyword, new NetResponse() {
            });
        }
    }

    private void backToSearch() {
        backToSearch(true);
    }

    private void backToSearch(boolean isBack) {
        if (result != null && search.isHidden()) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.hide(result).show(search).commitAllowingStateLoss();
        } else {
            if (isBack) finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToSearch();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(etSearchContent!=null){
            etSearchContent.removeCallbacks(showInputRunable);
            etSearchContent.removeTextChangedListener(this);
            etSearchContent.setOnEditorActionListener(null);
        }
    }

}
