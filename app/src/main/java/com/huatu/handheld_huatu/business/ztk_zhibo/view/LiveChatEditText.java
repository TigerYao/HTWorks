package com.huatu.handheld_huatu.business.ztk_zhibo.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;
/*
import com.gensee.chat.gif.SpanResource;*/
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.StringUtils;

/**
 * Created by saiyuan on 2017/11/24.
 */

public class LiveChatEditText extends EditText {
    private int playerType = 0;
    private int mMax = 25;

    public LiveChatEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public LiveChatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public LiveChatEditText(Context context) {
        super(context);
        this.init();
    }

    public void setPlayerType(int type) {
        playerType = type;
    }

  /*  public void insertAvatar(String avatar) {
        this.getText().insert(this.getSelectionStart(),
                SpanResource.convetToSpan(avatar.toString(), UniApplicationContext.getContext()));
    }*/

    private void init() {

        this.setFilters(new InputFilter[]{new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //添加百家云的判断   参考 new InputFilter.LengthFilter(400)
                if (playerType == PlayerTypeEnum.BaiJia.getValue()) {
                    int keep = mMax - (dest.length() - (dend - dstart));
                    if (keep <= 0) {
                        return "";
                    } else if (keep >= end - start) {
                        return null; // keep original
                    } else {
                        keep += start;
                        if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                            --keep;
                            if (keep == start) {
                                return "";
                            }
                        }
                        return source.subSequence(start, keep);
                    }

                }
                return "";
              /*  int[] nAvatarCountOld = SpanResource.getAvatarCount(dest.toString());
                int totalCountOld = dest.toString().length() - nAvatarCountOld[1] + nAvatarCountOld[0];
                int[] nAvatarCountNew = SpanResource.getAvatarCount(source.toString());
                int totalCountNew = source.toString().length() - nAvatarCountNew[1] + nAvatarCountNew[0];
                int totalAvatarCount = nAvatarCountOld[0] + nAvatarCountNew[0];
                int totalCount = totalCountOld + totalCountNew;
                return (totalAvatarCount <= 20 && totalCount <= 512 ? source : "");*/
            }
        }});
    }

    public String getChatText() {

        String text = this.getText().toString();
        if (playerType == PlayerTypeEnum.BaiJia.getValue()) return StringUtils.Trim(text);
        return StringUtils.Trim(text);
       // return StringUtils.isEmpty(text) ? "" : SpanResource.convertToSendText(text);
    }

    public String getRichText() {
        String text = this.getText().toString();
        if (playerType == PlayerTypeEnum.BaiJia.getValue()) return StringUtils.Trim(text);
        return StringUtils.Trim(text);
        //return StringUtils.isEmpty(text) ? "" : SpanResource.convertToSendRichText(text);
    }
}
