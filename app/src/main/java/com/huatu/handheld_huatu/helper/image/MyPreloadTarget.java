package com.huatu.handheld_huatu.helper.image;

import android.support.annotation.Nullable;

import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.io.File;

/**
 * Created by Terry

 */
 public    class MyPreloadTarget<Z> extends SimpleTarget<Z> {

    public String mFileName;
    /**
     * Returns a PreloadTarget.
     *
     * @param width The width in pixels of the desired resource.
     * @param height The height in pixels of the desired resource.
     * @param <Z> The type of the desired resource.
     */
    public static <Z> MyPreloadTarget<Z> obtain(int width, int height) {
        return new MyPreloadTarget<Z>(width, height);
    }

    public static  MyPreloadTarget<File> obtain() {
        return new MyPreloadTarget<File>(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }


    public MyPreloadTarget(String fileUrl) {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE);

        mFileName= IoExUtils.getFileName(fileUrl);
    }

    private MyPreloadTarget(int width, int height) {
        super(width, height);
    }


   // void onResourceReady(@NonNull R resource, @Nullable Transition<? super R> transition);

    @Override
    public void onResourceReady(Z resource, @Nullable Transition<? super Z> transition) {

        LogUtils.e("onResourceReady","onResourceReady");
        if(resource instanceof File){
            try {
                onDownFinished(true,((File)resource).getAbsolutePath());
            }
            catch (Exception e){
                e.printStackTrace();
                onDownFinished(false,e.getMessage());
            }
        }
        final MyPreloadTarget curTarget=this;
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                ImageLoad.clear(curTarget,true);
            }
        });
        //
    }



   /* @Override
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
        Glide.clear(this);
        LogUtils.e("onResourceReady","onResourceReady");
        if(resource instanceof File){
            try {
                  onDownFinished(true,((File)resource).getAbsolutePath());
             }
             catch (Exception e){
                  e.printStackTrace();
                 onDownFinished(false,e.getMessage());
             }
         }
    }*/

    public void onDownFinished(boolean isSuccess,String filePath){


    }
}

/*
https://www.jianshu.com/p/7ce7b02988a4
    public void loadImageTarget(Context context){
        CustomView mCustomView = (CustomView) findViewById(R.id.custom_view);

        ViewTarget viewTarget = new ViewTarget<CustomView,GlideDrawable>( mCustomView ) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                this.view.setImage(resource);
            }
        };

        Glide.with(context)
                .load(mUrl)
                .into(viewTarget);
    }
*/

