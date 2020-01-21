package com.huatu.handheld_huatu.helper.image;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;
import android.support.annotation.NonNull;

import com.baijiayun.glide.load.engine.bitmap_recycle.BitmapPool;
import com.baijiayun.glide.load.resource.bitmap.BitmapTransformation;

import com.huatu.handheld_huatu.UniApplicationContext;

import java.security.MessageDigest;


public class BlurTransformation extends BitmapTransformation {

  private static final int VERSION = 1;
  private static final String ID =
          "jp.wasabeef.glide.transformations.BlurTransformation." + VERSION;
  private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

  private static int MAX_RADIUS = 25;
  private static int DEFAULT_DOWN_SAMPLING = 1;

  private int radius;
  private int sampling;

  public BlurTransformation() {
    this(MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
  }

  public BlurTransformation(int radius) {
    this(radius, DEFAULT_DOWN_SAMPLING);
  }

  public BlurTransformation(int radius, int sampling) {
    this.radius = radius;
    this.sampling = sampling;
  }

  @Override
  protected Bitmap transform( @NonNull BitmapPool pool,
                                       @NonNull Bitmap toTransform, int outWidth, int outHeight) {

    int width = toTransform.getWidth();
    int height = toTransform.getHeight();
    int scaledWidth = width / sampling;
    int scaledHeight = height / sampling;

    Bitmap bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(bitmap);
    canvas.scale(1 / (float) sampling, 1 / (float) sampling);
    Paint paint = new Paint();
    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
    canvas.drawBitmap(toTransform, 0, 0, paint);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      try {
        bitmap = RSBlur.blur(UniApplicationContext.getContext(), bitmap, radius);
      } catch (RSRuntimeException e) {
        bitmap = FastBlur.blur(bitmap, radius, true);
      }
    } else {
      bitmap = FastBlur.blur(bitmap, radius, true);
    }

    return bitmap;
  }

  @Override public String toString() {
    return "BlurTransformation(radius=" + radius + ", sampling=" + sampling + ")";
  }

  @Override public boolean equals(Object o) {
    return o instanceof BlurTransformation;
  }

  @Override public int hashCode() {
    return ID.hashCode();
  }

  @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
    messageDigest.update(ID_BYTES);
  }
}
