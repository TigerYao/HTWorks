package com.huatu.handheld_huatu.business.ztk_zhibo.play.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.EditText;
/*
import com.gensee.chat.gif.GifDrawalbe;
import com.gensee.chat.gif.SpanResource;*/
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.LiveChatExpressBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.GridViewAvatarAdapter;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.custom.GlideImageV4Getter;
import com.huatu.handheld_huatu.view.custom.ImgTagHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by saiyuan on 2017/12/14.
 */

public class ChatMsgTextView extends EditText  {
    private final Vector<Drawable> drawables = new Vector<>();
    private final Map<Integer, Drawable> cache = new HashMap();
    private int playerType;

    public ChatMsgTextView(Context context) {
        this(context, null);
    }

    public ChatMsgTextView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public ChatMsgTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayerType(int type) {
        playerType = type;
    }

    public void setSuperText(CharSequence text){
        super.setText(text);
    }

    //<font color="#e9304e">【助教】小可爱：</font>[keai]
    public void setText(String richText) {
        LogUtils.i("richText " + richText);
        if(TextUtils.isEmpty(richText)) {
            super.setText("");
            return;
        }
        String strPre = "";
        String strTail = "";
        if(playerType == 1) {
            strPre = "[";
            strTail = "]";
        }
        String htmlText = "";
        int imgHeight = DisplayUtil.px2dp((int) getPaint().getTextSize() * 13 / 10);
        if(richText.contains(strPre) && richText.contains(strTail)) {
            List<LiveChatExpressBean> expressList = GridViewAvatarAdapter.expressMap
                    .get(String.valueOf(playerType));

           // LogUtils.e("setText"+ GsonUtil.GsonString(expressList));
            //用户1519：</font>[img:https://img.baijiayun.com/baijiacloud/46727567_fqnmuse4.png]
            //https://img.baijiayun.com/0baijiacloud/emotion/baijiacloud/v2/3.pn
            if(!Method.isListEmpty(expressList)) {
                String text = richText;
                while (text.contains(strPre) && text.contains(strTail)) {
                    int indexPre = text.indexOf(strPre);
                    int indexTail = text.indexOf(strTail);
                    htmlText += text.substring(0, indexPre);
                    String emo = text.substring(indexPre + 1, indexTail);
                    if(!TextUtils.isEmpty(emo) && emo.startsWith("img:")) {
                        emo = emo.substring("img:".length());
                        htmlText += "<img src=\"" + emo
                                + "\" width=\"" +  imgHeight * 6
                                + "\" height=\"" + imgHeight * 6 +  "/>";
                        text = text.substring(indexTail + 1);
                    } else {
                        for (LiveChatExpressBean bean : expressList) {

                            if (Method.isEqualString(bean.key, emo)) {
                                htmlText += "<img src=\"" + bean.imgUrl
                                        + "\" width=\"" + imgHeight
                                        + "\" height=\"" + imgHeight + "/>";
                                text = text.substring(indexTail + 1);
                                break;
                            }
                        }
                    }
                }
            }
            else htmlText= richText;
        } else {
            htmlText = richText;
        }
       //LogUtils.e("setText2"+ htmlText);
        Spanned spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT,
                    new GlideImageV4Getter(htmlText, ChatMsgTextView.this,this.getContext()), new ImgTagHandler(true));
        } else {
            spanned = Html.fromHtml(htmlText,
                    new GlideImageV4Getter(htmlText, ChatMsgTextView.this,this.getContext()),
                    new ImgTagHandler(true));
        }
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(spanned);
    }

    public void setRichText(String richText) {
        if(playerType == 1) {

        } else {
           // insertGif(richText);
        }
    }

   /* private void insertGif(String str) {
        Spanned spannableString;
        try {
            spannableString = SpanResource.convetRichToExpression(
                    UniApplicationContext.getContext(), str, 0, cache, this.drawables);
        } catch (NullPointerException e) {
            e.printStackTrace();
            spannableString = new SpannableStringBuilder(str);
        }
        this.setText(spannableString);
        Iterator var4 = this.drawables.iterator();
        while(var4.hasNext()) {
            Drawable gifDrawable = (Drawable)var4.next();
            ((GifDrawalbe)gifDrawable).addListen(this);
            ((GifDrawalbe)gifDrawable).readFrames(true);
        }

    }*/

    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == GONE  && this.drawables != null) {
           /* Iterator var3 = this.drawables.iterator();
            while(var3.hasNext()) {
                Drawable gifDrawable = (Drawable)var3.next();
                ((GifDrawalbe)gifDrawable).removeListen(this);
            }*/

            this.destroy();
        }

    }

    private void destroy() {
        drawables.clear();
        cache.clear();
    }
    public void updateUI() {
        this.invalidate();
    }
}
