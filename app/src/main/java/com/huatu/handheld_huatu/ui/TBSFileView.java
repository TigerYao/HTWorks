package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.baijiayun.hubble.sdk.utils.Logger;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * Created by cjx on 2019\2\18 0018.
 */

public class TBSFileView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;

    private String TAG = "TBSFileView";

    public TBSFileView(@NonNull Context context) {
        super(context);
        mTbsReaderView = new TbsReaderView(context, this);
    }

    public TBSFileView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new FrameLayout.LayoutParams(-1, -1));
    }

    public TBSFileView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new FrameLayout.LayoutParams(-1, -1));
    }


    public void displayFile(File mFile) {
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String bsReaderTemp = "/storage/emulated/0/TbsReaderTemp";
            File bsReaderTempFile = new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                Logger.d(TAG, "准备创建/storage/emulated/0/TbsReaderTemp！！");
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                    Logger.d(TAG, "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
                }
            }

            //加载文件
            Bundle localBundle = new Bundle();
            localBundle.putString("filePath", mFile.toString());
            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            }
        } else {
            //MyToast.showAtCenter(getContext(), "文件路径无效！");

            ToastUtils.showShort("文件路径无效！");
        }

    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Logger.d(TAG, "paramString---->null");
            return str;
        }
        Logger.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Logger.d(TAG, "i <= -1");
            return str;
        }
        str = paramString.substring(i + 1);
        Logger.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}