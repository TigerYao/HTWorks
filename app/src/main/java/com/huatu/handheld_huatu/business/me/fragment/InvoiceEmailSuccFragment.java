package com.huatu.handheld_huatu.business.me.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.ui.CustomShapeDrawable;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.utils.DensityUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019\5\17 0017.
 */

public class InvoiceEmailSuccFragment  extends MySupportFragment {

    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;



    public static void lanuch(Context context){
        Bundle tmpArg=new Bundle();

        FragmentParameter tmpPar=new FragmentParameter(InvoiceEmailSuccFragment.class,tmpArg);
        UIJumpHelper.jumpSupportFragment(context,tmpPar,1);
    }

    @Override
    public int getContentView() {
        return R.layout.invoice_emailsuccess_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShapeDrawable tmpDrawable=CustomShapeDrawable.build(22,0XFFF3F3F3,0XFFE1E1E1,1f);

        int padding=  DensityUtils.dp2px(getContext(),1f);
        tmpDrawable.setPadding(padding,padding,padding,padding);
        view.findViewById(R.id.show_detail_btn).setBackground(tmpDrawable);
    }

    @Override
    protected void setListener() {
        mTopTitleBar.setTitle("邮箱地址");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if(menuItem.getId() == R.id.xi_title_bar_home){

                    getActivity().finish();
                }
            }
        });
       // mNextBtn.setBackground();
    }

    @OnClick({R.id.show_detail_btn})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.show_detail_btn:
               // BaseWebViewFragment.lanuch(getActivity(),"http://m.v.huatu.com/customer/explain.html", "开票详情");
                pop();
                break;

        }
    }


    @Override
    public boolean onBackPressedSupport() {
        Intent intent = new Intent( getActivity(), MainTabActivity.class);
        intent.putExtra("require_index", 3);
        startActivity(intent);
        getActivity().finish();
        return true;
    }
}
