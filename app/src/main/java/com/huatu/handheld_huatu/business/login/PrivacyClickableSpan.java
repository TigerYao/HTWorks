package com.huatu.handheld_huatu.business.login;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

/**
 * Created by Administrator on 2019\12\26 0026.
 */

public class PrivacyClickableSpan  extends ClickableSpan {

        int mType=0;
        public PrivacyClickableSpan(int type){
            mType=type;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (!NetUtil.isConnected()) {
                ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                return;
            }
            if(mType==0){
                BaseFrgContainerActivity.newInstance(widget.getContext(),
                        ContractFragment.class.getName(),
                        ContractFragment.getServiceAgreementArgs());
            }else {
                BaseFrgContainerActivity.newInstance(widget.getContext(),
                        ContractFragment.class.getName(),
                        ContractFragment.getPrivacyPolicyArgs());
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.parseColor("#37ADFF"));
            ds.setUnderlineText(false); // 去掉下划线
        }
}
