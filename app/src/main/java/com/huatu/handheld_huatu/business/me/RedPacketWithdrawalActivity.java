package com.huatu.handheld_huatu.business.me;

import android.content.Intent;
import android.os.Bundle;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.MySupportActivity;
import com.huatu.handheld_huatu.business.me.fragment.ChoosePayWithdrawalFragment;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by cjx on 2018\9\29 0029.
 */

public class RedPacketWithdrawalActivity extends MySupportActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_root_layout);

        ChoosePayWithdrawalFragment fragment = findFragment(ChoosePayWithdrawalFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.fl_container, ChoosePayWithdrawalFragment.newInstance());
        }
       // initView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
