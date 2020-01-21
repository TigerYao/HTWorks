package com.huatu.handheld_huatu.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.FloatRange;
import android.support.annotation.MainThread;
/*import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;*/
import android.support.annotation.RequiresApi;
import android.view.View;

/**
 * Created by kyle on 2017/3/14.
 */

public class BlurUtils {
    @MainThread
    @RequiresApi(18)
    public static Bitmap blur(Context context, View view, @FloatRange(from = 0, to = 25) float radius) {
        Bitmap sourceBitmap = getScreenshot(view);
        return blur(context, sourceBitmap, radius);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Bitmap blurV2(Context context, Bitmap bitmap,  @FloatRange(from = 0, to = 25) float radius)  {//throws RSRuntimeException
        if (Build.VERSION.SDK_INT <= 17) {
            return null;
        }
        RenderScript rs = null;
        try {
            rs = RenderScript.create(context);
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input =
                    Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                            Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            blur.setInput(input);
            blur.setRadius(radius);
            blur.forEach(output);
            output.copyTo(bitmap);
        }catch(Exception e){
            return null;
        }
        finally {
            if (rs != null) {
                rs.destroy();
            }
        }
        return bitmap;
    }


    @RequiresApi(18)
    public static Bitmap blur(Context context, Bitmap origin, @FloatRange(from = 0, to = 25) float radius) {

        if (Build.VERSION.SDK_INT < 17) {
            return null;
        }
        Bitmap scaled = Bitmap.createScaledBitmap(origin, origin.getWidth(), origin.getHeight(), false);
        Bitmap output = Bitmap.createBitmap(scaled);

        RenderScript rs = RenderScript.create(context);
        Allocation allIn = Allocation.createFromBitmap(rs, scaled);
        Allocation allOut = Allocation.createFromBitmap(rs, output);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allIn.getElement());

        blur.setRadius(radius);
        blur.setInput(allIn);
        blur.forEach(allOut);
        allOut.copyTo(output);
        return output;
    }

    @MainThread
    private static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}
