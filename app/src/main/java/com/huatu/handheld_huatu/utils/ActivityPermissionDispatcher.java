package com.huatu.handheld_huatu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * @author zhaodongdong
 */
public class ActivityPermissionDispatcher {
    private static ActivityPermissionDispatcher mDispatcher;

    public static ActivityPermissionDispatcher getInstance() {
        if (mDispatcher == null) {
            synchronized (ActivityPermissionDispatcher.class) {
                if (mDispatcher == null) {
                    mDispatcher = new ActivityPermissionDispatcher();
                }
            }
        }
        return mDispatcher;
    }

    private PermissionCallback mCallback;

    public interface PermissionCallback {
        void onPermissionNeverAskAgain(int request);

        void onPermissionDenied(int request);

        void onPermissionSuccess(int request);

        void onPermissionExplain(int request);
    }

    /**
     * 设置权限回调
     *
     * @param callback 权限回调
     */
    public void setPermissionCallback(PermissionCallback callback) {
        mCallback = callback;
    }

    public ActivityPermissionDispatcher() {
    }

    /**
     * SD卡读写权限
     *
     * @param activity activity
     */
    public void checkedWithStorage(Activity activity) {
        checkedWithPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Constant.PERMISSION_STORAGE_REQUEST_CODE);
    }

    /**
     * camera 权限
     *
     * @param activity activity
     */
    public void checkedWithCamera(Activity activity) {
        checkedWithPermission(activity, Manifest.permission.CAMERA, Constant.PERMISSION_CAMERA_REQUEST_CODE);
    }

    /**
     * 短信操作权限
     *
     * @param activity activity
     */
    public void checkedWithSMS(Activity activity) {
        checkedWithPermission(activity, Manifest.permission.READ_SMS, Constant.PERMISSION_SMS_REQUEST_CODE);
    }

    /**
     * 电话权限
     *
     * @param activity activity
     */
    public void checkedWithPhone(Activity activity) {
        checkedWithPermission(activity, Manifest.permission.READ_PHONE_STATE, Constant.PERMISSION_PHONE_REQUEST_CODE);
    }

    /**
     * 联系人权限
     *
     * @param activity activity
     */
    public void checkedWithContacts(Activity activity) {
        checkedWithPermission(activity, Manifest.permission.READ_CONTACTS, Constant.PERMISSION_CONTACTS_REQUEST_CODE);
    }

    /**
     * 位置权限
     *
     * @param activity activity
     */
    public void checkedWithLocation(Activity activity) {
        checkedWithPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, Constant.PERMISSION_LOCATION_REQUEST_CODE);
    }

    /**
     * 权限处理
     *
     * @param activity   activity
     * @param permission Manifest.permission.
     * @param request    权限请求码
     */
    public void checkedWithPermission(Activity activity, String permission, int request) {
        int hasPermissionStorage = ContextCompat.checkSelfPermission(activity, permission);
        if (hasPermissionStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                mCallback.onPermissionExplain(request);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission},
                        request);
            }
        } else {
            mCallback.onPermissionSuccess(request);
        }
    }

    /**
     * 权限请求结果处理
     *
     * @param activity     activity
     * @param requestCode  请求码
     * @param grantResults result
     */
    public void onRequestPermissionResult(Activity activity, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSION_STORAGE_REQUEST_CODE:
                if (verifyPermission(grantResults)) {
                    mCallback.onPermissionSuccess(requestCode);
                } else {
                    //denied
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mCallback.onPermissionNeverAskAgain(requestCode);
                    } else {
                        mCallback.onPermissionDenied(requestCode);
                    }

                }
                break;
        }
    }

    /**
     * 验证请求结果
     *
     * @param grantResults result
     * @return true为通过
     */
    private boolean verifyPermission(int... grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 销毁持有对象,防止内存泄露
     */
    public void clear() {
        mCallback = null;
    }
}
