package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.TestAdapter;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ht on 2017/5/11.
 */

public class TestActivity extends BaseActivity {
    private TextView tv_subset_top_title;
    private ImageButton ib_titleBar_back;
    private RelativeLayout rl_titleBar;
    private ListView lv_test;
    private TestAdapter mAdapter;
    private List<String> listUrl=new ArrayList<String>();

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_test;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int i) {
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
    public void onFragmentClickEvent(int i, Bundle bundle) {

    }

    @Override
    protected void onInitView() {

        this.findViewById(R.id.test_patch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPatch();
            }
        });
        ib_titleBar_back = (ImageButton) findViewById(R.id.ib_titleBar_back);
        ib_titleBar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestActivity.this.finish();
            }
        });
        listUrl.add(RetrofitManager.BASE_URL);
        listUrl.add(RetrofitManager.BASE_MONK_URL);
        listUrl.add(RetrofitManager.BASE_TEST_URL);
        listUrl.add(RetrofitManager.BASE_GRAY_URL);
        listUrl.add(RetrofitManager.BASE_TEST_URL_2280);
        lv_test= (ListView) findViewById(R.id.lv_test);
        mAdapter=new TestAdapter(listUrl,TestActivity.this);
        lv_test.setAdapter(mAdapter);
        for(int i = 0; i < listUrl.size(); i++) {
            if(Method.isEqualString(listUrl.get(i), RetrofitManager.getInstance().getBaseUrl())) {
                mAdapter.setSelectPosition(i);
                break;
            }
        }
        lv_test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String testUrl=listUrl.get(position);
                RetrofitManager.getInstance().setBaseUrl(testUrl);
                SpUtils.setTestUrlPosition(position);
//                String text=RetrofitManager.getInstance().getBaseUrl();
                mAdapter.setSelectPosition(position);
                ToastUtils.showShort(TestActivity.this,"设置成功，杀掉应用后重启生效");
//                AppUtils.killProgress();
            }
        });

    }

    //https://www.jianshu.com/p/d50817b6d622
   //adb push app/build/outputs/apk/app-beijing-release-unsigned.apk /sdcard/Test.apk
    private void testPatch(){
         File patchFile = new File(Environment.getExternalStorageDirectory(), "Test3.apk");
        LogUtils.e("patchFile1",patchFile.exists()?"true":"false");
        if(patchFile.exists()){
            TinkerInstaller.onReceiveUpgradePatch(TestActivity.this, patchFile.getAbsolutePath());
            return;
        }
    }


    public static void newInstance(Context context) {
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }
}
