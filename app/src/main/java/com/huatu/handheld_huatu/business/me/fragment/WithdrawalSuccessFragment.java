package com.huatu.handheld_huatu.business.me.fragment;

import android.content.Intent;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;

import butterknife.BindView;

/**
 * Created by cjx on 2018\9\29 0029.
 */

public class WithdrawalSuccessFragment  extends MySupportFragment {

    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;


    @Override
    public int getContentView() {
        return R.layout.red_envelope_withsuccess_layout;
    }

    @Override
    protected void setListener() {
        mTopTitleBar.setTitle("红包提现");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if(menuItem.getId() == R.id.xi_title_bar_home){
                    Intent intent = new Intent( getActivity(), MainTabActivity.class);
                    intent.putExtra("require_index", 3);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
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
