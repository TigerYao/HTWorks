package com.huatu.handheld_huatu.business.essay.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.essay.camera.cropper.CropImageView;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class CropImgActivity extends BaseActivity implements View.OnClickListener {

    private CropImageView cropImageView;
    private ImageView ivCancel;
    private ImageView ivConfirm;

    private Bundle extraArgs;

    private String originPath;
    private String targetPath;

    @Override
    protected int onSetRootViewId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_crop_img;
    }

    @Override
    protected void onInitView() {
        cropImageView = findViewById(R.id.crop_view);
        ivCancel = findViewById(R.id.iv_cancel);
        ivConfirm = findViewById(R.id.iv_confirm);

        ivCancel.setOnClickListener(this);
        ivConfirm.setOnClickListener(this);

        cropImageView.setGuidelines(2);

        extraArgs = originIntent.getBundleExtra("extra_args");

        if (extraArgs == null) {
            extraArgs = new Bundle();
        }

        originPath = extraArgs.getString("originPath");
        targetPath = extraArgs.getString("targetPath");

        if (StringUtils.isEmpty(originPath)) {
            ToastUtils.showEssayToast("图片路径无效");
            finish();
        } else if (StringUtils.isEmpty(targetPath)) {
            ToastUtils.showEssayToast("缺少目标路径");
            finish();
        }
    }

    @Override
    protected void onLoadData() {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .format(DecodeFormat.PREFER_RGB_565)
                .skipMemoryCache(true)
                .optionalFitCenter()
                .override(DisplayUtil.getScreenWidth(), DisplayUtil.getScreenHeight() - DisplayUtil.dp2px(82));

        Glide.with(this)
                .asBitmap()
                .load(originPath)
                .apply(options)
                .into(new SimpleViewTargetV2<Bitmap>(cropImageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        cropImageView.setImageBitmap(resource);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.iv_confirm:
                if (cropImageView.hasBitmap()) {
                    startCropper();
                }
                break;
        }
    }


    /**
     * 开始截图，并保存图片
     */
    public void startCropper() {
        // 获取截图
        CropperImage cropperImage = cropImageView.getCroppedImage();
        final Bitmap photo = cropperImage.getBitmap();
        saveImage(targetPath, photo);
        Intent intent = new Intent();
        intent.putExtra("targetPath", targetPath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 存储图像并将信息添加入媒体数据库
     */
    private void saveImage(String filePath, Bitmap source) {
        OutputStream outputStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            if (file.exists()) {
                outputStream = new FileOutputStream(file);
                if (source != null) {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showEssayToast("裁剪失败");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
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
}
