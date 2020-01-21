package com.huatu.handheld_huatu.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.common.permission.AppPermissions;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.mvpmodel.me.UploadImgBean;
import com.huatu.handheld_huatu.network.RetrofitManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UploadImgUtil {

    private AppPermissions rxPermissions;
    private PopupWindow popupWindow;
    protected CompositeSubscription compositeSubscription = null;
    private BaseActivity mContext;
    private String basePath;

    private final int PHOTO_ALBUM = 1;
    private final int TAKE_PIC = 2;
    private final int CROP_IMAGE = 3;

    private String imageName;
    public String success;
    private Call call;
    private View anchor;

    public interface Call{
        void pathBack(String path,String url);
        void failBack(String path);
    }

    public UploadImgUtil(BaseActivity mContext) {
        this.mContext = mContext;
        basePath = FileUtil.getCacheDir() + File.separator + "UploadImgUtil";
        rxPermissions = new AppPermissions(mContext);
       // rxPermissions.setLogging(true);
        if (!FileUtil.isFileExist(basePath)) {
            new File(basePath).mkdirs();
        }
    }

    public void startUpload(View anchor,CompositeSubscription compositeSubscription,String path,Call call){
        this.compositeSubscription=compositeSubscription;
        this.call=call;
        this.anchor=anchor;
        if (compositeSubscription != null) {
        }else {
            LogUtils.e("uploadImg", "uploadImg: compositeSubscription != null" );
            return;
        }
        if(path==null){
            showPression();
        }else {
            uploadImg(path);
        }
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

    private void showSelectPop() {
        String externalStorageState = Environment.getExternalStorageState();
       if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            CommonUtils.showToast("SD卡不可用");
            return;
        }

        if (mContext == null) {
            return;
        }
        View inflate = View.inflate(mContext, R.layout.pop_window3, null);

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
//                ImageUtil.deleteAllPhoto(basePath);
                selectPhotoAlbum();
            }
        });
        text_p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
//                ImageUtil.deleteAllPhoto(basePath);
                showCameraPermission();
            }
        });

        popupWindow = new PopupWindow(inflate);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (anchor != null) {
            popupWindow.showAsDropDown(anchor, 0, 0);
        }
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

    private void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void takePicture() {
        imageName = System.currentTimeMillis() + ".jpg";
        Uri imageUri = UriUtil.getUriFromFile(new File(basePath, imageName));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (mContext != null) {
            mContext.startActivityForResult(intent, TAKE_PIC);
        }
    }

    private void selectPhotoAlbum() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setType("image/*");
            if (mContext != null) {
                mContext.startActivityForResult(intent, PHOTO_ALBUM);
            }
        }
    }

    private void compressImage(String filePath){
        File uploadCompressFile= FileUtil.getUploadFile(new File(filePath ));
        uploadImg(uploadCompressFile.getAbsolutePath());
    }

    private void compressImage(Uri uri) {
        if (uri == null) {
            return;
        }
        String filePath=UriUtil.getPath(mContext,uri);
        LogUtils.e("cropImage1", uri.getPath());
        LogUtils.e("cropImage2", filePath);
        compressImage(filePath);
    }

    public void cropImage(Uri uri) {
        if (uri == null) {
            return;
        }
        imageName = System.currentTimeMillis() + ".jpg";
        Uri imageUri = Uri.fromFile(new File(basePath, imageName));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 500);
//        intent.putExtra("outputY", 500);
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
        if (mContext != null) {
            mContext.startActivityForResult(intent, CROP_IMAGE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mContext == null) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CROP_IMAGE:
              /*      new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (mContext != null) {
                                Glide.get(mContext).clearDiskCache();
                            }
                        }
                    }).start();
                    Glide.get(mContext).clearMemory();*/
                    uploadImg(basePath + File.separator + imageName);
                    break;
                case PHOTO_ALBUM:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        compressImage(selectedImage);
                    } else {
                        ToastUtils.showShort("照片选取失败");
                    }
                    break;
                case TAKE_PIC:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            Uri uriVar = UriUtil.getUriFromPath(mContext, UriUtil.getPath
                                    (mContext, uri));
                            if (uriVar != null) {
                                uri = uriVar;
                            }
                        }
                    } else {
                        File curFile= new File(basePath, imageName);
                        if(curFile.exists()){
                              compressImage(curFile.getAbsolutePath());
                              return;
                        }else{
                            uri = UriUtil.getUriFromFile(new File(basePath, imageName));
                        }
                    }
                    cropImage(uri);
                    break;
            }
        }

    }

    String url;
    int imgsize = 250 ;

    private void uploadImg(final String filePath) {
        File file = null;
        url = null;
        try {
            file = new File(filePath);
            if (!FileUtil.isFileExist(filePath)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!Method.isActivityFinished(mContext)) {
            mContext.showProgress();
        }

        if (file != null) {
            LogUtils.d("UploadImgUtil", "file.length():" + file.length());
            if (file.length()/1024 > imgsize) {
                Subscription compressBitmap = Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        imageName = System.currentTimeMillis() + ".jpg";
                        String targetFilePath = BitmapUtil.compressBitmap(filePath, basePath + File.separator +
                                imageName, imgsize);
                        subscriber.onNext(targetFilePath);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                            @Override
                            public void onNext(String filePath) {
                                uploadImgNet(filePath, new File(filePath));
                            }

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (!Method.isActivityFinished(mContext)) {
                                    mContext.hideProgress();
                                }
                                Toast.makeText(mContext, "图片压缩失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                if (compositeSubscription != null) {
                    compositeSubscription.add(compressBitmap);
                } else {
                    LogUtils.e("uploadImg", "uploadImg: compositeSubscription != null");
                }
            } else {
                uploadImgNet(filePath, file);
            }
        } else {
            LogUtils.e("uploadImg", "file!=null");
        }
    }

    private void uploadImgNet(final String filePath,File file){
        if(file==null || filePath==null){
            LogUtils.d("UploadImgUtil", "file==null || filePath==null");
            return;
        }
        LogUtils.d("UploadImgUtil", "file.length():" + file.length());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        Subscription imageSubscribe = RetrofitManager.getInstance().getService().sendFeedBackImage(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadImgBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!Method.isActivityFinished(mContext)) {
                            mContext.hideProgress();
                        }
                        String string = e.toString();
                        if (!TextUtils.isEmpty(string) && string.contains("SocketTimeoutException")) {
                            CommonUtils.showToast("连接超时");
                        } else {
                            if(NetUtil.isConnected()) {
                                CommonUtils.showToast("上传图像失败");
                            }else {
                                CommonUtils.showToast("网络未链接，请检查你的网络");
                            }
                        }
                        if (call != null) {
                            call.failBack(filePath);
                        }
                    }

                    @Override
                    public void onNext(UploadImgBean responseBean) {
                        if (!Method.isActivityFinished(mContext)) {
                            mContext.hideProgress();
                        }
                        long code = responseBean.code;
                        if (code == 1000000) {
                            CommonUtils.showToast("上传图像成功");
                            String data = (String)responseBean.data;
                            if (null != data) {
                                url= data;
                                if (call != null) {
                                    call.pathBack(filePath,url);
                                }
                                return;
                            } else {
                                CommonUtils.showToast("上传图像失败");
                            }
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("上传图像失败");
                        }
                        if (call != null) {
                            call.failBack(filePath);
                        }

                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(imageSubscribe);
        }else {
            LogUtils.e("uploadImg", "uploadImg: compositeSubscription != null" );
        }
    }

    protected void onDestroy() {
        LogUtils.v(this.getClass().getName() + " onDestroy()");
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

}
