package com.huatu.handheld_huatu.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.ReuseActivityHelper;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xing on 2018/3/30.
 */

public class UIJumpHelper {


    /**
     * 跳转三星应用商店
     * @param context {@link Context}
     * @param packageName 包名
     * @return {@code true} 跳转成功 <br> {@code false} 跳转失败
     */
    public static boolean goToSamsungMarket(Context context, String packageName) {
        Uri uri = Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=" + packageName);
//        Uri uri = Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.sec.android.app.samsungapps");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
           // ToastUtils.showEssayToast("您的系统暂时不能打开此地址");
            return false;
        }
    }

    public static void goToMarket(Context context, String packageName) {

        String  manufacturer = Build.MANUFACTURER + android.os.Build.MODEL;
        String model = Build.MODEL;
        /*    && (model == null || (!model.trim().toLowerCase()
                .contains("google") && !model.trim().toLowerCase()
                .contains("nexus")))) {*/
        if (manufacturer != null && (manufacturer.trim().toLowerCase().contains("samsung")||manufacturer.trim().toLowerCase().contains("三星"))){
            boolean flag= goToSamsungMarket(context,packageName);
            if(flag) {
               return;
            }
        }

        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showEssayToast("您的系统暂时不能打开此地址");
        }
    }


    public static void openActionView(Context context,Intent intent ){
        PackageManager pm = context.getPackageManager();
        ComponentName cn = intent.resolveActivity(pm);
        if (cn == null) {
         /*   // If there is no Activity available to perform the action
            // Check to see if the Google Play Store is available.
            Uri marketUri = Uri.parse("market://search?q=pname:com.myapp.packagename");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
            // If the Google Play Store is available, use it to download an application
            // capable of performing the required action. Otherwise log an error.
            if (marketIntent.resolveActivity(pm) != null) {
                context.startActivity(marketIntent);
            } else {
                Log.d(TAG, "Market client not available.");
            }*/
            ToastUtils.showEssayToast("您的系统暂时不能打开此地址");
        } else{
            context.startActivity(intent);
        }
    }


    public static void showWebCourse(Context context,String courseId){

        String webUrl = RetrofitManager.getInstance().getBaseUrl()
                + "c/v3/courses/" + courseId;
        Bundle bundle = new Bundle();
        bundle.putString(BaseWebViewFragment.ARGS_STRING_URL, webUrl);
        bundle.putString(BaseWebViewFragment.ARGS_STRING_TITLE, "课程详情");
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isSupportBack", true);
        BaseFrgContainerActivity.newInstance(context,
                BaseWebViewFragment.class.getName(), bundle);

    }

    public static boolean dealOverrideUrl(Activity activity, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if(url.contains("http://v.huatu.com/h5/detail.php?rid=")) {
            String rid = url.substring("http://v.huatu.com/h5/detail.php?rid=".length());
//            BuyDetailsActivity.newIntent(activity, rid);
            BaseIntroActivity.newIntent(activity, rid);
            return true;
        }
        return false;

    }

    public static void startStudyPage(Context context) {
        Intent intent = new Intent(context, MainTabActivity.class);
        intent.putExtra("require_index",2);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public static void startActivity(Context mContext, Intent intent) {
        mContext.startActivity(intent);
    }

    public static void jumpFragment(Context mContext, FragmentParameter parameter) {
        Intent build = ReuseActivityHelper.builder(mContext).setFragmentParameter(parameter).build();
        startActivity(mContext, build);
    }

    public static void jumpFragment(Context mContext, Class<? extends AbsFragment> fragmentClass) {
        Intent build = ReuseActivityHelper.builder(mContext).setFragmentParameter(new FragmentParameter(fragmentClass)).build();
        startActivity(mContext, build);
    }

    public static void jumpFragment(Context mContext, Class<? extends AbsFragment> fragmentClass, Bundle args) {
        Intent build = ReuseActivityHelper.builder(mContext).setFragmentParameter(new FragmentParameter(fragmentClass, args)).build();
        startActivity(mContext, build);
    }

    public static void jumpSupportFragment(Context mContext, Class<? extends MySupportFragment> fragmentClass, Bundle args) {
        Intent build = ReuseActivityHelper.builderCustomEx(mContext).setFragmentParameter(new FragmentParameter(fragmentClass, args)).build();
        startActivity(mContext, build);
    }
    public static void jumpSupportFragment(Context mContext,  FragmentParameter parameter,boolean isTranslucent) {
        Intent build = ReuseActivityHelper.builderCustomEx(mContext).setFragmentParameter(parameter,isTranslucent?1:0).build();
        startActivity(mContext, build);
    }

    public static void jumpSupportFragment(Context mContext,  FragmentParameter parameter,int blackTranslucent) {
        Intent build = ReuseActivityHelper.builderCustomEx(mContext).setFragmentParameter(parameter,blackTranslucent==1?3:0).build();
        startActivity(mContext, build);
    }
    /**
     * 页面跳转
     *
     * @param parameter 参数实体
     */

    public static void jumpFragment(Fragment mContext, FragmentParameter parameter) {
        Intent intent = ReuseActivityHelper.builder(mContext.getActivity()).setFragmentParameter(parameter).build();
        if (mContext.getActivity() == null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (parameter.getRequestCode() != FragmentParameter.NO_RESULT_CODE) {
            mContext.startActivityForResult(intent, parameter.getRequestCode());
        } else {
            mContext.startActivity(intent);
        }
    }

    public static void jumpFragment(Activity mContext, FragmentParameter parameter) {
        Intent intent = ReuseActivityHelper.builder(mContext).setFragmentParameter(parameter).build();
        if (mContext == null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (parameter.getRequestCode() != FragmentParameter.NO_RESULT_CODE) {
            mContext.startActivityForResult(intent, parameter.getRequestCode());
        } else {
            mContext.startActivity(intent);
        }
    }

    /**
     * 页面跳转
     *
     * @param fragmentClass 目标Fragment
     */

    public static void jumpFragment(Fragment mContext, Class<? extends AbsFragment> fragmentClass) {
        jumpFragment(mContext, fragmentClass, null);
    }

    /**
     * 页面跳转
     *
     * @param requestCode   请求码
     * @param fragmentClass 目标Fragment
     */
    public static void jumpFragment(Fragment mContext, int requestCode, Class<? extends AbsFragment> fragmentClass) {
        jumpFragment(mContext, requestCode, fragmentClass, null);
    }

    /**
     * 页面跳转
     *
     * @param fragmentClass 目标Fragment
     * @param args          参数
     */

    public static void jumpFragment(Fragment mContext, Class<? extends AbsFragment> fragmentClass, Bundle args) {
        jumpFragment(mContext, new FragmentParameter(fragmentClass, args));
    }

    /**
     * 页面跳转
     *
     * @param requestCode   请求码
     * @param fragmentClass 目标Fragment
     * @param args          参数
     */
    public static void jumpFragment(Fragment mContext, int requestCode, Class<? extends AbsFragment> fragmentClass, Bundle args) {
        FragmentParameter parameter = new FragmentParameter(fragmentClass, args);
        parameter.setRequestCode(requestCode);
        jumpFragment(mContext, parameter);
    }

    public static void jumpFragment(Activity mContext, int requestCode, Class<? extends AbsFragment> fragmentClass, Bundle args) {
        FragmentParameter parameter = new FragmentParameter(fragmentClass, args);
        parameter.setRequestCode(requestCode);
        jumpFragment(mContext, parameter);
    }


    private static String TruncateUrlPage(String strURL) {

        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) strAllParam = arrSplit[1];
            }
        }
        return strAllParam;
    }

    public static Map<String, String> URLRequest(String URL) {

        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null)
            return mapRequest;

        //每个键值为一组 www.2cto.com
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {

            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

}
