package com.huatu.handheld_huatu.tinker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.mvpmodel.UpdateInfoBean;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.StorageUtils;

import java.io.File;
import java.io.FileInputStream;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by Administrator on 2019\5\13 0013.
 */

public class PatchUtils {


    public static boolean hasLocalPatchConfig(Context context) {
        File patchFile = new File(Environment.getExternalStorageDirectory(), "huatu_toast.txt");
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && (perm == PackageManager.PERMISSION_GRANTED)
                 && patchFile.exists();

    }

    public static UpdateInfoBean getLocalPatchConfig(Context context){
        if(hasLocalPatchConfig(context)){

            File mSaveFile = new File(Environment.getExternalStorageDirectory(), "huatu_toast.txt");
            if(mSaveFile.exists()) {
                String res = "";
                try {
                    FileInputStream fin = new FileInputStream(mSaveFile);
                    int length = fin.available();
                    byte[] buffer = new byte[length];

                    fin.read(buffer);
                    res =new String(buffer,"UTF-8");//
                    fin.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return GsonUtil.GsonToBean(res,UpdateInfoBean.class);
            }
            return null;
        }
        return null;
    }
 }
