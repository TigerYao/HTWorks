package com.huatu.handheld_huatu.business.me.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.bean.ScanCourseData;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;


/**
 * Created by saiyuan on 2019/1/17.
 */

public class LoadScanCourseFragment extends BaseWebViewFragment{
    private static String mShareTitle;
    private static String mShareDesc;
    private String lessonId;
    private String url;
    @Override
    protected void onInitView() {
        super.onInitView();
        lessonId=args.getString("lesson_Id");
        mShareTitle=args.getString("mShareTitle");
        mShareDesc=args.getString("mShareDesc");
        url=args.getString(ARGS_STRING_URL);

    }

    @Override
    public void onRightClickBtn() {
        super.onRightClickBtn();
        ShareUtil.test(mActivity,"0",mShareDesc,mShareTitle,url);
    }

    @Override
    protected boolean dealOverrideUrl(String dealUrl) {
        Uri uri=Uri.parse(dealUrl);
        String plateForm=uri.getQueryParameter("plateform");
        if (plateForm!=null&&plateForm.equals("app")){
            //跳课程
            String rid=uri.getQueryParameter("rid");
            String isLive=uri.getQueryParameter("isLive");
            String collageActiveId=uri.getQueryParameter("collageActiveId");
            Intent  intent = new Intent(mActivity, BaseIntroActivity.class);
            intent.putExtra("rid", rid );
            intent.putExtra("isLive", isLive);
            intent.putExtra("collageActiveId", collageActiveId);
            mActivity.startActivity(intent);
            return true;
        }
        return super.dealOverrideUrl(dealUrl);

    }

    public static Bundle getArgs(String result, String lessonId, String title, String mShareDesc, String mShareTitle) {
        LogUtils.i("webUrl: " + result);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, result);
        bundle.putString(ARGS_STRING_TITLE,title);
        bundle.putString("mShareDesc",mShareDesc);
        bundle.putString("mShareTitle",mShareTitle);
        bundle.putString("lesson_Id",lessonId);
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putBoolean("showRightShare", true);
        return bundle;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
