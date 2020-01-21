package com.huatu.handheld_huatu.helper.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ImageView;


import com.huatu.handheld_huatu.UniApplicationContext;


import java.util.ArrayList;

/**
 * 图库选择帮助类
 * 1:execute启动图库
 * 2：onActivityResult接收回调

 */
public class ImagePickerHelper {

    public static final int REQUEST_CODE = 23;
    private static ImagePickerHelper mInstance;

    /**
     * 裁剪类型
     */
    public enum PickerType {
        Crop(0),//默认模式,裁剪图片
        Normal(1);//不裁剪图片
        public int type;

        PickerType(int type) {
            this.type = type;
        }

        public static PickerType valueOf(int type) {
            switch (type) {
                case 0:
                    return Crop;
                default:
                    return Normal;
            }
        }
    }

    //private Activity mActivity;
    private OnPictureChooseCallback mOnPictureChooseCallback;

    private ImagePickerHelper() {

    }

    public static ImagePickerHelper getInstance() {
        if (mInstance == null) {
            synchronized (ImagePickerHelper.class) {
                if (mInstance == null) {
                    mInstance = new ImagePickerHelper();
                }
            }
        }
        return mInstance;
    }

    //由fragment进入startActivityForResult

    /**
     * 进入图库选择
     *
     * @param num 最多可以选择多少张图片
     */
    public void execute(Fragment curFragment, int num, OnPictureChooseCallback mOnPictureChooseCallback) {
        //this.mActivity = curFragment.getActivity();
        this.mOnPictureChooseCallback = mOnPictureChooseCallback;
        //设置缓存框架
  /*      ImagePicker.getInstance().setImageLoader(new GlideImageLoader());
        //设置裁剪宽高
        ImagePicker.getInstance().setFocusWidth(AppStructure.getInstance().getScreenWidth() - 40);
        ImagePicker.getInstance().setFocusHeight(AppStructure.getInstance().getScreenWidth() - 40);
        //设置选择的最大数量
        ImagePicker.getInstance().setSelectLimit(num);
        //设置裁剪
        ImagePicker.getInstance().setCrop(false);
        ImagePicker.getInstance().setShowCamera(true);

        Intent intent = new Intent(curFragment.getContext(), ImageGridActivity.class);*/
       // curFragment.startActivityForResult(intent, REQUEST_CODE);
    }
    //end

    /**
     * 进入图库选择
     *
     * @param num 最多可以选择多少张图片
     */
    public void execute(Activity activity, int num, OnPictureChooseCallback mOnPictureChooseCallback) {
       // this.mActivity = activity;
        this.mOnPictureChooseCallback = mOnPictureChooseCallback;
        //设置缓存框架
   /*     ImagePicker.getInstance().setImageLoader(new GlideImageLoader());
        //设置裁剪宽高
        ImagePicker.getInstance().setFocusWidth(AppStructure.getInstance().getScreenWidth() - 40);
        ImagePicker.getInstance().setFocusHeight(AppStructure.getInstance().getScreenWidth() - 40);
        //设置选择的最大数量
        ImagePicker.getInstance().setSelectLimit(num);
        //设置裁剪
        ImagePicker.getInstance().setCrop(true);
        ImagePicker.getInstance().setShowCamera(true);
        ImagePicker.getInstance().setMultiMode(true);

        Intent intent = new Intent(activity, ImageGridActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);*/
    }

    /**
     * 进入图库选择
     *
     * @param num  最多可以选择多少张图片
     * @param type 模式
     */
    public void execute(Activity activity, int num, PickerType type, OnPictureChooseCallback mOnPictureChooseCallback) {
        execute(activity, num, mOnPictureChooseCallback);
      /*  switch (PickerType.valueOf(type.type)) {
            case Crop:
                ImagePicker.getInstance().setCrop(true);
                break;
            case Normal:
                ImagePicker.getInstance().setCrop(false);
                break;
        }*/
    }

    /**
     * 进入图库选择
     *
     * @param activity
     * @param num                      最多可以选择多少张图片
     * @param ratio                    裁剪的比例 高/宽
     * @param mOnPictureChooseCallback
     */
    public void execute(Activity activity, int num, float ratio, OnPictureChooseCallback mOnPictureChooseCallback) {
        execute(activity, num, mOnPictureChooseCallback);
    /*    int width = AppStructure.getInstance().getScreenWidth() - 40;
        int height = (int) (width * ratio);
        //设置裁剪宽高
        ImagePicker.getInstance().setFocusWidth(width);
        ImagePicker.getInstance().setFocusHeight(height);
        ImagePicker.getInstance().setOutPutX(width);
        ImagePicker.getInstance().setOutPutY(height);*/
    }

    /**
     * 进入图库选择
     *
     * @param activity
     * @param width                     裁剪宽
     * @param ratio                    裁剪的比例 高/宽
     * @param mOnPictureChooseCallback
     */
    public void executeInFaceCatch(Activity activity, int width, float ratio, OnPictureChooseCallback mOnPictureChooseCallback) {
        execute(activity, 1, mOnPictureChooseCallback);
//        int width = AppStructure.getInstance().getScreenWidth() - 40;
        int height = (int) (width * ratio);
        //设置裁剪宽高/*  ImagePicker.getInstance().setFocusWidth(AppStructure.getInstance().getScreenWidth());
        /*ImagePicker.getInstance().setFocusHeight(AppStructure.getInstance().getScreenWidth());
        ImagePicker.getInstance().setOutPutX(width);
        ImagePicker.getInstance().setOutPutY(height);
        //设置裁剪
        ImagePicker.getInstance().setCrop(true);
        ImagePicker.getInstance().setShowCamera(false);
        //设置选择的最大数量
        ImagePicker.getInstance().setSelectLimit(1);
        ImagePicker.getInstance().setMultiMode(false);*/

    }

    /**
     *  进入图库选择照片
     * @param activity
     * @param num           数量
     * @param isShowCamera  是否显示拍照
     * @param isCrop        是否裁剪
     * @param mOnPictureChooseCallback
     */
    public void execute(Activity activity, int num, boolean isShowCamera, boolean isCrop, OnPictureChooseCallback mOnPictureChooseCallback) {
        this.mOnPictureChooseCallback = mOnPictureChooseCallback;
        //设置缓存框架
      /*  ImagePicker.getInstance().setImageLoader(new GlideImageLoader());
        //设置裁剪宽高
        ImagePicker.getInstance().setFocusWidth(AppStructure.getInstance().getScreenWidth());
        ImagePicker.getInstance().setFocusHeight(AppStructure.getInstance().getScreenWidth());
        //设置选择的最大数量
        ImagePicker.getInstance().setSelectLimit(num);
        ImagePicker.getInstance().setMultiMode(num > 1);
        //设置裁剪
        ImagePicker.getInstance().setCrop(isCrop);
        ImagePicker.getInstance().setShowCamera(isShowCamera);

        Intent intent = new Intent(activity, ImageGridActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);*/
    }

    /**
     * 接收Activity结果
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                onSuccess(images);
            } else {
                MaterialToast.makeText(getContext(), "没有数据").show();
            }
        }*/
    }

    public void clearCallBack(){
         mOnPictureChooseCallback=null;
    }

   /* public void onSuccess(ArrayList<ImageItem> filePath) {
        if (ArrayUtils.isEmpty(filePath)) {
            onError(new Exception("The filePath is NULL!"));
        } else {
            try {
                for (ImageItem item : filePath) {
                    item.path = ImageUrlUtils.FILE_HEAD + item.path;
                }
                if (mOnPictureChooseCallback != null) {
                    mOnPictureChooseCallback.onSuccess(filePath);
                }
            } catch (Exception e) {
                onError(e);
            }
        }
    }*/

    public void onError(Exception e) {
        if (mOnPictureChooseCallback != null) {
            mOnPictureChooseCallback.onError(e);
        }
    }

    public OnPictureChooseCallback getmOnPictureChooseCallback() {
        return mOnPictureChooseCallback;
    }

    public void setmOnPictureChooseCallback(OnPictureChooseCallback mOnPictureChooseCallback) {
        this.mOnPictureChooseCallback = mOnPictureChooseCallback;
    }

    public Context getContext() {
         return UniApplicationContext.getContext();
    }

    /**
     * 选择回调
     */
    public interface OnPictureChooseCallback {

        /**
         * 成功
         */
       // public void onSuccess(ArrayList<ImageItem> filePath) throws Exception;

        /**
         * 失败
         */
        public void onError(Exception e);
    }

    /**
     * 图库的加载框架实现类
     */
   /* public static class GlideImageLoader implements ImageLoader {

        @Override
        public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
            *//*ImageLoadFactory.getInstance().getGlideLoadHandler().display(activity, path, imageView,
                    com.lzy.imagepicker.R.mipmap.default_image, com.lzy.imagepicker.R.mipmap.default_image);*//*

            if(TextUtils.isEmpty(path)) return;
            if(!path.equals(imageView.getTag(R.id.reuse_cachetag))){
                imageView.setTag(R.id.reuse_cachetag, path);
               // noMemoryCacheWorkdisplay(context, BuoumallUtil.getFinalAddr(url, DensityUtils.dp2px(context, width)), imageView, shouldDrawable, shouldDrawable);
                UniversalGlideHandler.displayAlbumLocal(activity, path, imageView, com.lzy.imagepicker.R.mipmap.default_image, com.lzy.imagepicker.R.mipmap.default_image);
            }


        }

        @Override
        public void clearMemoryCache() {
         *//*     AppStructure.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.get(AppStructure.getInstance().getContext()).clearMemory();
                }
             }, 400); *//*
        }
    }*/
}
