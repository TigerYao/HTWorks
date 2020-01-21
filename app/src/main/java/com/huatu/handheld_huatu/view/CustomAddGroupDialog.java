package com.huatu.handheld_huatu.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijiayun.glide.load.DataSource;
import com.baijiayun.glide.load.engine.GlideException;
import com.baijiayun.glide.request.RequestListener;
import com.baijiayun.glide.request.target.Target;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.ImageUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/10/25.
 */
public class CustomAddGroupDialog extends Activity implements View.OnClickListener {
    @BindView(R.id.imv_dialog_erweima)
    ImageView mErWM;
    @BindView(R.id.group_id_tv)
    TextView mGroupCode;
    @BindView(R.id.course_title_tv)
    TextView mCourseTitle;
    @BindView(R.id.tv_describe)
    TextView mAddGroupDes;
    @BindView(R.id.add_group_save_img)
    View mSaveErwm;
    @BindView(R.id.add_group_jump_scan)
    Button mJumpScan;

    Drawable mQrImg;

    private PayInfo.AddGroupInfo addGroupInfo;

    public static void showCustomDialog(PayInfo.AddGroupInfo info) {
        Context ctx = UniApplicationContext.getContext();
        Intent intent = new Intent(ctx, CustomAddGroupDialog.class);
        intent.putExtra(ArgConstant.BEAN, info);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_group_layout);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSaveErwm.setOnClickListener(this);
        mJumpScan.setOnClickListener(this);
        addGroupInfo = (PayInfo.AddGroupInfo) getIntent().getSerializableExtra(ArgConstant.BEAN);
        if (addGroupInfo != null) {
            mCourseTitle.setText(addGroupInfo.title);
            boolean isQQ = addGroupInfo.service == 1;
            String addQQCode = (isQQ ? "QQ群号：" : "微信号：") + addGroupInfo.number;
            mGroupCode.setText(addQQCode);
            String descri = isQQ ? "加群可获得专属班级服务，及时答疑解惑，学习资料定期发送，更多福利加群了解~" : "点击“保存二维码”可下载二维码至相册，如已保存可点击“扫码加群”打开微信，识别相册中的二维码添加好友，以便邀您入群";//String.format("点击“保存二维码”可下载二维码至相册，如已保存可点击“扫码加群”打开%s，识别相册中的二维码%s", isQQ ? "QQ" : "微信", isQQ ? "加入课程班级群" : "添加好友，以便邀您入群");
            SpannableString spannableString = new SpannableString(descri);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#4A4A4A"));
            String colorStri = isQQ ? "专属班级服务" : "“保存二维码”";
            int startIndex = descri.indexOf(colorStri);
            int endIndex = startIndex + colorStri.length();
            spannableString.setSpan(CharacterStyle.wrap(colorSpan), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            if (!isQQ) {
                colorSpan = new ForegroundColorSpan(Color.parseColor("#4A4A4A"));
                startIndex = descri.indexOf("“扫码加群”");
                endIndex = startIndex + "“扫码加群”".length();
            }
            spannableString.setSpan(CharacterStyle.wrap(colorSpan), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            mAddGroupDes.setText(spannableString);
            if (isQQ) {
                mSaveErwm.setVisibility(View.GONE);
                mJumpScan.setText("一键加群");
            } else if (!TextUtils.isEmpty(addGroupInfo.qrCode)) {
                mErWM.setVisibility(View.VISIBLE);
                ImageLoad.displayWorkImageListener(this, addGroupInfo.qrCode, mErWM, R.mipmap.err_no_data, new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        mQrImg = drawable;
                        return false;
                    }
                });
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_group_save_img:
                if (addGroupInfo != null && mQrImg != null && checkWriteStorePermission()) {
                    saveBitmap();
                }
                break;
            case R.id.add_group_jump_scan:
                if (addGroupInfo != null && addGroupInfo.service == 1) {
                    Method.joinQQGroup(addGroupInfo.function);
                } else
                    Method.jumpWXScanView();
                break;
            case R.id.iv_dialog_title:
                finish();
                break;
        }
    }

    private void saveBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) mQrImg;
        String path = ImageUtil.savePhotoToSDCard(bitmapDrawable.getBitmap(), addGroupInfo.number + "wx");
        if (!Utils.isEmptyOrNull(path)) {
            MediaScannerConnection.scanFile(UniApplicationContext.getContext(),
                    new String[]{path}, new String[]{"image/jpeg"}, null);
            ToastUtils.showEssayToast("保存成功");
        } else
            ToastUtils.showEssayToast("保存失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkWriteStorePermission() {
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return true;
        }

        // 如果有授权，走正常插入日历逻辑
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x2101);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x2101 && grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            saveBitmap();
    }
}