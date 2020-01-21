package com.huatu.handheld_huatu.business.essay.camera.cropper.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by zhouyongwei on 2018/1/3.
 */

public class CamParaUtil {
    private static final String TAG = "zyw";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private Camera.Parameters mParams;
    private int preHeight = 1080,preWidth = 1920;
    private int picHeight = 1080,picWidth = 1920;
    public CamParaUtil(Camera.Parameters mParams){
        this.mParams = mParams;
    }

    public Size getPropPreviewSize(){
        List<Size> list = mParams.getSupportedPreviewSizes();
        Collections.sort(list, sizeComparator);

        int i = 0;
        if (android.os.Build.MODEL.equals("EML-AL00")){
            for(Size s:list){

                if (s.width==1920){
                    LogUtils.d(TAG, "chq find PreviewSize : w = " + s.width + "h = " + s.height);
                    break;
                }
                i++;
            }
            return list.get(i);
        }
            for(Size s:list){
                if(s.height >= preHeight){
                LogUtils.d(TAG, "find PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;//如果没找到，取差距不大的
            for(Size s:list){
                if(Math.abs(s.height-preHeight) <= 200){
                    LogUtils.d(TAG, "match PreviewSize:w = " + s.width + "h = " + s.height);
                    break;
                }
                i++;
            }
        }
        if(i == list.size()){
            i = list.size()/2+2;
        }

        return list.get(i);
    }
    public Size getPropPictureSize(){
        List<Size> list = mParams.getSupportedPictureSizes();
        Collections.sort(list, sizeComparator);
        int i = 0;
            if (android.os.Build.MODEL.equals("EML-AL00")){
                for(Size s:list){
                    if (s.height==1080){
                        LogUtils.d(TAG, "chq find PictureSize : w = " + s.width + "h = " + s.height);
                        break;
                }
                i++;
            }
                return list.get(i);
        }
            for(Size s:list){
                if(s.height >= picHeight){
                LogUtils.d(TAG, "find PictureSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
            for(Size s:list){
                if(Math.abs(s.height - picHeight) <= 200){
                    LogUtils.d(TAG, "match PictureSize : w = " + s.width + "h = " + s.height);
                    break;
                }
                i++;
            }
        }
        if(i == list.size()){
            i = list.size()/2;
        }
        return list.get(i);
    }


    public  class CameraSizeComparator implements Comparator<Size>{
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }

    /**打印支持的previewSizes
     * @param params
     */
    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        Collections.sort(previewSizes, sizeComparator);

        for(int i=0; i< previewSizes.size(); i++){
            Size size = previewSizes.get(i);
            LogUtils.d(TAG, "Support previewSizes:width = "+size.width+" height = "+size.height);
        }

    }

    /**打印支持的pictureSizes
     * @param params
     */
    public  void printSupportPictureSize(Camera.Parameters params){
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        Collections.sort(pictureSizes, sizeComparator);
        for(int i=0; i< pictureSizes.size(); i++){
            Size size = pictureSizes.get(i);
            LogUtils.d(TAG, "Support pictureSizes:width = "+ size.width
                    +" height = " + size.height);
        }
    }
    /**打印支持的聚焦模式
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            LogUtils.d(TAG, "focusModes--" + mode);
        }
    }
}
