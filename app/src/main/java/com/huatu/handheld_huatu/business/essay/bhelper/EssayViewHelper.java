package com.huatu.handheld_huatu.business.essay.bhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.essay.camera.TakePhotoActivity;
import com.huatu.handheld_huatu.business.essay.checkfragment.CheckOrderFragment;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import rx.Subscriber;

/**
 */
public class EssayViewHelper {

    private static String TAG = "EssayViewHelper";


    public interface OnHelperCallBack {
        boolean doSomething(Object isOpen);
    }

    public void showDialog_m1(Activity mActivity, String photoMsg, final OnHelperCallBack varCallback) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        if (TextUtils.isEmpty(photoMsg)) {
            return;
        }
        dialog = DialogUtils.createDialog(mActivity, null, photoMsg);
        dialog.setNegativeButton("不再提示", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (varCallback != null) {
                    varCallback.doSomething(1);
                }
                dialog.dismiss();
                SpUtils.setEssayMaterialShow(true);
            }
        });
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /****/

    public void vCameraPer(final Activity mActivity) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        AppPermissions rxPermissions = new AppPermissions(mActivity);
        // rxPermissions.setLogging(true);
        rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }

                        }, null, "请在“设置-应用管理-华图在线-权限”中允许访问相机", null, "确认");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            mActivity.startActivityForResult(new Intent(mActivity, TakePhotoActivity.class), 10010);
                        } else {
                            DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }

                            }, null, "请在“设置-应用管理-华图在线-权限”中允许访问相机", null, "确认");

                        }
                    }
                });
    }


    public void vAudioPer(final Activity mActivity, final OnHelperCallBack varCallback) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        AppPermissions rxPermissions = new AppPermissions(mActivity);
        //rxPermissions.setLogging(true);
        rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }

                        }, null, "请在“设置-应用管理-华图在线-权限”中允许访问麦克风", null, "确认");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            if (varCallback != null) {
                                varCallback.doSomething(1);
                            }
                        } else {
                            DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }

                            }, null, "请在“设置-应用管理-华图在线-权限”中允许访问麦克风", null, "确认");

                        }
                    }
                });
    }


    public void showBuyDialog(final Activity mActivity, final OnHelperCallBack varCallBack) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        final CustomConfirmDialog mDialog;
        mDialog = DialogUtils.createDialog(mActivity, null, "批改次数不足");
        mDialog.setNegativeButton("先保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (varCallBack != null) {
                    varCallBack.doSomething(1);
                }
                mDialog.dismiss();
            }
        });
        mDialog.setPositiveButton("去购买", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 去购买页面
                mDialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("page_source","申论交卷页");
                BaseFrgContainerActivity.newInstance(mActivity,
                        CheckOrderFragment.class.getName(),
                        bundle);
            }
        });
        mDialog.show();
    }

    private CustomConfirmDialog dialog;

    public void showDialog(boolean single, Activity mActivity, int maxCorrectTimes, final OnHelperCallBack
            varCallback) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        if (single) {
            dialog = DialogUtils.createDialog(mActivity, null, "本试题已批改" + maxCorrectTimes + "次，可以继续练习，但无法交卷批改");
        } else {
            dialog = DialogUtils.createDialog(mActivity, null, "本试卷已批改" + maxCorrectTimes + "次，可以继续练习，但无法交卷批改");
        }
        dialog.setNegativeButton("不再提示", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (varCallback != null) {
                    varCallback.doSomething(1);
                }
                SpUtils.setDialogShow(true);
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
