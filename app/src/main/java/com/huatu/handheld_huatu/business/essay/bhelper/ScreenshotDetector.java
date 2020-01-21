
package com.huatu.handheld_huatu.business.essay.bhelper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.baijiahulian.common.permission.AppPermissions;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.ReuseActivity;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.me.account.BalanceDetailActivity;
import com.huatu.handheld_huatu.business.me.account.MyAccountActivity;
import com.huatu.handheld_huatu.business.me.fragment.ServiceCenterFragment;
import com.huatu.handheld_huatu.business.me.order.OrderActivity;
import com.huatu.handheld_huatu.business.me.order.OrderDetailActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmOrderFragment;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.TimeUtils;

import java.io.File;

import rx.Subscriber;


public final class ScreenshotDetector {

    private static final String TAG = "ScreenshotDetector";
    private static final String EXTERNAL_CONTENT_URI_MATCHER =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 100;

    private BaseActivity mMainActivity;
    private Call call;
    private ContentResolver contentResolver;
    private ContentObserver contentObserver;

    private String tempPath = null;

    public interface Call {
        void callPath(String path);
    }

/*    public ScreenshotDetector(final BaseActivity activity) {
        mActivity = activity;
    }*/

    private static boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshot") || path.contains("截屏") ||
                path.contains("截图");
    }

    private static boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    public void start(BaseActivity activity, Call v) {
        call = v;
        mMainActivity = activity;
        AppPermissions rxPermissions = new AppPermissions(mMainActivity);
        // rxPermissions.setLogging(true);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtils.onShowConfirmDialog(mMainActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }, null, "请在“设置-应用管理-华图在线-权限”中允许访问手机存储", null, "确认");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            startAfterPermissionGranted(mMainActivity);
                        } else {
                            DialogUtils.onShowConfirmDialog(mMainActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, null, "请在“设置-应用管理-华图在线-权限”中允许访问手机存储", null, "确认");
                        }
                    }
                });
    }


    private void startAfterPermissionGranted(final Context context) {
        contentResolver = context.getContentResolver();
        //&& ((Activity) context).hasWindowFocus()
        contentObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (context instanceof Activity && !Method.isActivityFinished((Activity) context)) {
                    Log.d(TAG, "onChange: " + selfChange + ", " + uri.toString());
                    if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
                        Cursor cursor = null;
                        try {
                            cursor = contentResolver.query(uri, PROJECTION, null, null,
                                    SORT_ORDER);
                            if (cursor != null && cursor.moveToFirst()) {
                                final String path = cursor.getString(
                                        cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                long dateAdded = cursor.getLong(cursor.getColumnIndex(
                                        MediaStore.Images.Media.DATE_ADDED));
                                long currentTime = System.currentTimeMillis() / 1000;
                                Log.d(TAG, "path: " + path + ", dateAdded: " + dateAdded +
                                        ", currentTime: " + currentTime);
                                if (matchPath(path) && matchTime(currentTime, dateAdded)) {
                                    if (call != null) {
                                        if (mMainActivity != null && !Method.isActivityFinished(mMainActivity)) {
                                            mMainActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    call.callPath(path);
                                                    if (tempPath == null) {
                                                        tempPath = path;
                                                        Log.d(TAG, "tempPath: " + tempPath);
                                                        Activity topActivity = ActivityStack.getInstance().getTopActivity();
                                                        showPopWindow(topActivity, path);

                                                    } else {
                                                        if (!tempPath.equals(path)) {
                                                            tempPath = path;
                                                            Log.d(TAG, "tempPath: " + tempPath);
                                                            Activity topActivity = ActivityStack.getInstance().getTopActivity();
                                                            showPopWindow(topActivity, path);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "open cursor fail");
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                    super.onChange(selfChange, uri);
                }
            }
        };
        contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    public void unregisterContentObserver() {
        dismiss();
        if (runnableTask != null) {
            TimeUtils.mHandler.removeCallbacks(runnableTask);
        }
        if (contentResolver != null && contentObserver != null) {
            contentResolver.unregisterContentObserver(contentObserver);
        }
    }

    PopupWindow popWin;
    Runnable runnableTask;

    private boolean checkIsLegalActivty(Activity topActivity){

         if(topActivity instanceof MyAccountActivity) return true;      //我的图币
         if(topActivity instanceof BalanceDetailActivity) return true;  //账户明细
         if(topActivity instanceof OrderActivity) return true;          //我的订单界面
         if(topActivity instanceof OrderDetailActivity)return true;     //我的订单详情

         if(topActivity instanceof ReuseActivity){
             Fragment curFragment=((ReuseActivity)topActivity).getCurrentFragment();
             if((null!=curFragment)&&(curFragment instanceof ServiceCenterFragment))
                 return true;
         }
         if(topActivity instanceof BaseFrgContainerActivity){
            String curFragmentName=((BaseFrgContainerActivity)topActivity).frgClassName;
            if(ConfirmOrderFragment.class.getName().equals(curFragmentName))
                return true;
         }
         return false;
    }

    private void showPopWindow(final Activity topActivity, final String path) {

        if (topActivity != null && !Method.isActivityFinished(topActivity) && topActivity.hasWindowFocus()) {
            /* if (topActivity instanceof FeedbackActivity) {
                return;
            }*/
            if(!checkIsLegalActivty(topActivity)) return;
            // if(topActivity!=null) LogUtils.e("showPopWindow",topActivity.getClass().getName()+",");

            dismiss();
            if (runnableTask != null) {
                TimeUtils.mHandler.removeCallbacks(runnableTask);
            }

            PopWindowUtil.showPopWindow_showAtLocation(topActivity, topActivity.getWindow().getDecorView(), 120,
                    -20, R.layout.layout_essay_feedback_popv,
                    90, 159, new PopWindowUtil.PopViewCall() {
                        @Override
                        public void popViewCall(View contentView, final PopupWindow popWindow) {
                            View essay_feedback_popv = contentView.findViewById(R.id
                                    .essay_feedback_popv);
                            popWindow.setOutsideTouchable(false);
                            popWindow.setFocusable(false);
                            popWin = popWindow;
                            essay_feedback_popv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FeedbackActivity.newInstance(topActivity, path);
                                    if (topActivity != null && !Method.isActivityFinished(topActivity)) {
                                        if (popWindow != null && popWindow.isShowing()) {
                                            popWindow.dismiss();
                                        }
                                    }
                                }
                            });

                            ImageView essay_feedback_pop_iv = (ImageView) contentView.findViewById(R.id
                                    .essay_feedback_pop_iv);
                            if (essay_feedback_pop_iv != null) {
                                try {
                                    ImageLoad.load(topActivity,new File(path),essay_feedback_pop_iv);
                                   // Glide.with(topActivity).load(path).asBitmap().into(essay_feedback_pop_iv);
                                } catch (Exception e) {
                                    LogUtils.e(e);
                                }
                            }
                        }

                        @Override
                        public void popViewDismiss() {

                        }
                    });

            runnableTask = new Runnable() {
                @Override
                public void run() {
                    if (topActivity != null) LogUtils.e("showPopWindow", "dismiss");
                    dismiss();
                }
            };

            TimeUtils.delayTask(runnableTask, 5000);
        }
    }

    //https://blog.csdn.net/sd19871122/article/details/51337450
    private void dismiss() {
        try {
            if (popWin != null && popWin.isShowing()) {
                popWin.dismiss();
            }

        } catch (Exception e) {
        }
    }

}
