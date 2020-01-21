package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.common.LargeImageActivity;
import com.huatu.handheld_huatu.utils.LogUtils;

import org.xml.sax.XMLReader;

/**
 * Created by saiyuan on 2017/12/14.
 */

public class ImgTagHandler implements Html.TagHandler {
    private int underLineStartIndex;
    private boolean isClickable = true;

    public ImgTagHandler(boolean clickable) {
        isClickable = clickable;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        // 处理标签<img>
        if (tag.toLowerCase().equals("img")) {
            // 获取长度
            int len = output.length();
            // 获取图片地址
            ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
            String imgURL = images[0].getSource();

            // 让图片和文字居中。问题：只是把图片向下移动，没有改变TextView的属性，所以最后一行的图片，下半部分会被遮挡。
//            ImageSpan image = images[0];

//            CenterAlignImageSpan centerAlignImageSpan = new CenterAlignImageSpan(image.getDrawable());

//            output.setSpan(centerAlignImageSpan, output.length() - 1, output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //LogUtils.d("handleTag",imgURL);

            if (!TextUtils.isEmpty(imgURL) && (!imgURL.contains("/emotion/")))
                // 使图片可点击并监听点击事件
                output.setSpan(new ImageClick(UniApplicationContext.getContext(), imgURL),
                        len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (tag.toLowerCase().equals("underline")) {
            if (opening) {
                underLineStartIndex = output.length();
            } else {
                int underLineEndIndex = output.length();
                output.setSpan(new UnderlineSpan(), underLineStartIndex,
                        underLineEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static class ImageClick extends ClickableSpan {

        private String url;
        private Context context;

        public ImageClick(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View view) {
            LargeImageActivity.newIntent(context, url);
        }
    }

    // 把图片上下居中绘制，和文字对齐
    public static class CenterAlignImageSpan extends ImageSpan {

        public CenterAlignImageSpan(@NonNull Drawable d) {
            super(d);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                         @NonNull Paint paint) {

            Drawable b = getDrawable();
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;//计算y方向的位移
            canvas.save();
            canvas.translate(x, transY);//绘制图片位移一段距离
            b.draw(canvas);
            canvas.restore();
        }
    }

}
