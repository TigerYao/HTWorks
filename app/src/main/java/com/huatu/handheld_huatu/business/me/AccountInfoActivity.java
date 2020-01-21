package com.huatu.handheld_huatu.business.me;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.address.AddressManageFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadAssist;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.mvpmodel.me.LogoutBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
/*import com.huatu.handheld_huatu.network.tcp.ConnectionKeeper;
import com.huatu.handheld_huatu.network.tcp.NettyClient;*/
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.ImageUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UriUtil;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomDialog;

import java.io.File;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 账号信息
 */
public class AccountInfoActivity extends BaseActivity {
    private RelativeLayout rl_left_topbar;
    private RelativeLayout rl_change_account;
    private RelativeLayout rl_change_password;
    private RelativeLayout rl_change_phone;
    private RelativeLayout rl_image_info;
    private TextView text_exit;
    private ImageView image_info;
    private TextView text111;
    private PopupWindow popupWindow;
    private RelativeLayout rl_top_titlebar;
    private CustomDialog dialog1;
    private PopupWindow popupWindow11;
    private RelativeLayout rl_address;
    private AppPermissions rxPermissions;
    private String avatorUrl;
    private String avatorName;
    private String tackPicName;
    private String phone;
    private TextView text_info_phonenum;


    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_account_info;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new AppPermissions(this);
    }

    @Override
    protected void onInitView() {
        phone = SpUtils.getMobile();
        rl_address = (RelativeLayout) findViewById(R.id.rl_address);
        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        rl_top_titlebar = (RelativeLayout) findViewById(R.id.rl_top_titlebar);
        rl_change_account = (RelativeLayout) findViewById(R.id.rl_change_account);
        rl_change_password = (RelativeLayout) findViewById(R.id.rl_change_password);
        rl_change_phone = (RelativeLayout) findViewById(R.id.rl_change_phone);
        text_info_phonenum = (TextView) findViewById(R.id.text_info_phonenum);
        rl_image_info = (RelativeLayout) findViewById(R.id.rl_image_info);
        image_info = (ImageView) findViewById(R.id.image_info);
        text111 = (TextView) findViewById(R.id.text111);
        showImage(image_info, 0);
        if (!TextUtils.isEmpty(phone)) {
            text_info_phonenum.setText(phone);
        }
        if (!TextUtils.isEmpty(SpUtils.getUname())) {
            text111.setText(SpUtils.getUname());
        }
        avatorUrl = FileUtil.getCacheDir() + File.separator + "avator";
        avatorName = SpUtils.getUid() + ".jpg";
        text_exit = (TextView) findViewById(R.id.text_exit);
        setListener();
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
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
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
    private String getMobile(){
        String mobile = SpUtils.getMobile();
        if (!TextUtils.isEmpty(mobile)) {
            return mobile;
        }
        return "";
    }
    private String getStr() {
        String mobile = SpUtils.getMobile();
        String uname = SpUtils.getUname();
        String email = SpUtils.getEmail();

        if (!TextUtils.isEmpty(mobile)) {
            return mobile;
        }

        if (!TextUtils.isEmpty(email)) {
            return email;
        }

        if (!TextUtils.isEmpty(uname)) {
            return uname;
        }
        return "";
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfoActivity.this.finish();
            }
        });

        rl_change_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeNicknameActivity.newInstance(AccountInfoActivity.this);
            }
        });

        rl_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordActivity.newInstance(AccountInfoActivity.this);
            }
        });

        rl_change_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phone)) {
                    Intent intent = new Intent(AccountInfoActivity.this, BindPhoneActivity.class);
                    startActivityForResult(intent, 10086);
                } else {
                    Intent intent = new Intent(AccountInfoActivity.this, ChangePhoneActivity.class);
                    startActivityForResult(intent, 10010);
                }
            }
        });

        rl_image_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPression();
            }
        });

        image_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show3();
            }
        });
        rl_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressManageFragment.newInstance(AccountInfoActivity.this);
            }
        });
        text_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.onShowConfirmDialog(AccountInfoActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userExitAccount();
                    }
                }, null, "确定退出当前账号", "取消", "确定");
            }
        });

    }

    private void showPression() {
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("没有读取SD卡权限");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            showSelectPop();
                        } else {
                            CommonUtils.showToast("没有读取SD卡权限");
                        }
                    }
                });
    }

    private void show3() {
        View inflate = LayoutInflater.from(AccountInfoActivity.this).inflate(R.layout.pop_window4, null);
        ImageView image = (ImageView) inflate.findViewById(R.id.image);
        View text_p = inflate.findViewById(R.id.text_p);

        showImage(image, 1);
        text_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide11();
            }
        });

        popupWindow11 = new PopupWindow(inflate);
        popupWindow11.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow11.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow11.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow11.showAtLocation(rl_top_titlebar, Gravity.CENTER, 0, 0);
        popupWindow11.update();
    }

    private void hide11() {
        if (popupWindow11 != null && popupWindow11.isShowing()) {
            popupWindow11.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return true;
                }
                if (popupWindow11 != null && popupWindow11.isShowing()) {
                    popupWindow11.dismiss();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSelectPop() {
        String externalStorageState = Environment.getExternalStorageState();

        if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            CommonUtils.showToast("SD卡不可用");
            return;
        }

        if (!FileUtil.isFileExist(avatorUrl)) {
            new File(avatorUrl).mkdirs();
        }
        View inflate = View.inflate(AccountInfoActivity.this, R.layout.pop_window3, null);

        TextView text_p1 = (TextView) inflate.findViewById(R.id.text_p1);
        TextView text_p2 = (TextView) inflate.findViewById(R.id.text_p2);
        TextView text_p3 = (TextView) inflate.findViewById(R.id.text_p3);
        View text_p4 = inflate.findViewById(R.id.text_p4);

        text_p4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

        text_p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        text_p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
                ImageUtil.deleteAllPhoto(avatorUrl);
                selectPhotoAlbum();
            }
        });
        text_p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
                ImageUtil.deleteAllPhoto(avatorUrl);
                showCameraPermission();
            }
        });

        popupWindow = new PopupWindow(inflate);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAsDropDown(rl_top_titlebar, 0, 0);
        popupWindow.update();
    }

    private void showCameraPermission() {
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("没有调用摄像头权限");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            takePicture();
                        } else {
                            CommonUtils.showToast("没有调用摄像头权限");
                        }
                    }
                });
    }

    private final int PHOTO_ALBUM = 1;
    private final int TAKE_PIC = 2;
    private final int CROP_IMAGE = 3;

    private void takePicture() {
        tackPicName = System.currentTimeMillis() + ".jpg";
        Uri imageUri = UriUtil.getUriFromFile(new File(avatorUrl, tackPicName));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PIC);
    }

    private void selectPhotoAlbum() {
//        Uri imageUri = Uri.fromFile(new File(avatorUrl, avatorName));
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 500);
//        intent.putExtra("outputY", 500);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, PHOTO_ALBUM);
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_ALBUM);
        }
    }

    public void cropImage(Uri uri) {
        if (uri == null) {
            return;
        }
        Uri imageUri = Uri.fromFile(new File(avatorUrl, avatorName));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (true) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
        startActivityForResult(intent, CROP_IMAGE);
    }

    private void showImage(ImageView imageView, int type) {
        String avatar;
        if (type == 0) {
            avatar = SpUtils.getAvatar();
        } else {
            avatar = SpUtils.getAvatar() + "?w=" + DisplayUtil.getScreenWidth();
        }

        ImageLoad.displayUserAvater(AccountInfoActivity.this,avatar,imageView,R.mipmap.user_default_avater);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CROP_IMAGE:
                /*    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.get(AccountInfoActivity.this).clearDiskCache();
                        }
                    }).start();

                    Glide.get(this).clearMemory();*/
                    show2();
                    break;
                case PHOTO_ALBUM:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        cropImage(selectedImage);
                    } else {
                        ToastUtils.showShort("照片选取失败");
                    }
                    break;
                case TAKE_PIC:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            Uri uriVar = UriUtil.getUriFromPath(AccountInfoActivity.this, UriUtil.getPath(AccountInfoActivity.this, uri));
                            if (uriVar != null) {
                                uri = uriVar;
                            }
                        }
                    } else {
                        uri = UriUtil.getUriFromFile(new File(avatorUrl, tackPicName));
                    }
                    cropImage(uri);
                    break;
                case 10010:
                    text_info_phonenum.setText(getMobile());
                    break;
                case 10086:
                    text_info_phonenum.setText(getMobile());
                    break;
            }
        }

    }

    private void show2() {
        File file = null;
        try {
            file = new File(avatorUrl, SpUtils.getUid() + ".jpg");
            if (!FileUtil.isFileExist(avatorUrl + File.separator + avatorName)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        dialog1 = new CustomDialog(AccountInfoActivity.this, R.layout.dialog_feedback_commit);
        dialog1.show();

        Subscription imageSubscribe = RetrofitManager.getInstance().getService().sendImage(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DeleteResponseBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog1.dismiss();
                        String string = e.toString();
                        if (!TextUtils.isEmpty(string) && string.contains("SocketTimeoutException")) {
                            CommonUtils.showToast("连接超时");
                        } else {
                            CommonUtils.showToast("上传图像失败");
                        }
                    }

                    @Override
                    public void onNext(DeleteResponseBean responseBean) {
                        dialog1.dismiss();
                        long code = responseBean.code;
                        if (code == 1000000) {
                            CommonUtils.showToast("上传图像成功");
                            DeleteResponseBean.Data data = responseBean.data;
                            if (null != data) {
                                String url = data.url;
                                SpUtils.setAvatar(url);
                                showImage(image_info, 0);
                            } else {
                                CommonUtils.showToast("上传图像失败");
                            }
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("上传图像失败");
                        }
                    }
                });

        compositeSubscription.add(imageSubscribe);
    }

    private void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    //用户注销登录
    private void userExitAccount() {
        dialog1 = new CustomDialog(AccountInfoActivity.this, R.layout.dialog_feedback_commit);
        TextView tv_notify_message = (TextView) dialog1.mContentView.findViewById(R.id.tv_notify_message);
        tv_notify_message.setText("账户退出中...");
        dialog1.show();
        if (!NetUtil.isConnected()) {
            dialog1.dismiss();
            CommonUtils.showToast("无网络，请检查网络连接");
            return;
        }
       // ConnectionKeeper.clear();
       // NettyClient.getInstance().close();
        Subscription exitSubscription = RetrofitManager.getInstance().getService().userExitAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LogoutBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog1.dismiss();
                        CommonUtils.showToast("账户退出失败");
                    }

                    @Override
                    public void onNext(LogoutBean logoutBean) {
                        dialog1.dismiss();
                        int code = logoutBean.getCode();
                        if (code == 1000000) {
                            SignUpTypeDataCache.getInstance().setCourseCategoryList(null);
                           // SignUpTypeDataCache.getInstance().setVodCourseCategoryList(null);
//                            if (Ntalker.getInstance() != null) {
//                                int logOut = 0;
//                                try {
//                                    logOut = Ntalker.getInstance().logout();
//                                } catch (NullPointerException e) {
//                                    e.printStackTrace();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                if (0 == logOut) {
//                                    // Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    //Toast.makeText(this, "注销失败，错误码:" + logOut, Toast.LENGTH_SHORT).show();
//                                }
//                            }
                            userExitSuccess();
                            CommonUtils.showToast("账户退出成功");
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("账户退出失败");
                        }
                    }
                });

        compositeSubscription.add(exitSubscription);
    }

    //    用户注销成功
    private void userExitSuccess() {
        UserInfoUtil.clearLiverUserInfo();
        UserInfoUtil.clearUserInfo();
        DownLoadAssist.getInstance().stopAll(false);

        AppContextProvider.addFlags(AppContextProvider.GUESTSPLASH);
        LoginByPasswordActivity.newIntent(AccountInfoActivity.this);
        AccountInfoActivity.this.finish();
        ActivityStack.getInstance().finishAllActivity();
    }

}
