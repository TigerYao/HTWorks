package com.huatu.handheld_huatu.helper.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.baijiayun.glide.load.engine.bitmap_recycle.BitmapPool;
import com.baijiayun.glide.load.resource.bitmap.BitmapTransformation;
import com.baijiayun.glide.load.resource.bitmap.TransformationUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.security.MessageDigest;

//com.baijiayun.glide.load.resource.bitmap.CenterCrop
//处理过宽的图片
//https://blog.csdn.net/libra_louis/article/details/58604149


public class LimitScaleTransformation extends BitmapTransformation {
  private float sampling;
  private static final int VERSION = 1;
  private static final String ID =
          "jp.wasabeef.glide.transformations.ScaleTransformation." + VERSION;
  private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

  public LimitScaleTransformation(float sampling) {

    this.sampling = sampling;
  }

  @Override
  protected Bitmap transform( @NonNull BitmapPool pool,
                              @NonNull Bitmap toTransform, int outWidth, int outHeight) {


    int realWidth=toTransform.getWidth();
    int realHeight=toTransform.getHeight();
    if(realWidth>DisplayUtil.getScreenWidth()*sampling){
      float outCusHeight=(realHeight*1f/realWidth)*DisplayUtil.getScreenWidth();
      return TransformationUtils.centerCrop(pool, toTransform, DisplayUtil.getScreenWidth(), (int)outCusHeight);
    }
    return toTransform;
  }


  @Override public boolean equals(Object o) {
    boolean isSameClass=o instanceof LimitScaleTransformation;
    if(!isSameClass) return false;
    else {
       return sampling==((LimitScaleTransformation)o).sampling;
     }
  }

  @Override public int hashCode() {
    return ID.hashCode();
  }

  @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
    messageDigest.update(ID_BYTES);
  }
}

