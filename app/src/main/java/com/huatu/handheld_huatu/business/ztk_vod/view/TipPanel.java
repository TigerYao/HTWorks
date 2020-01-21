package com.huatu.handheld_huatu.business.ztk_vod.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.utils.DensityUtils;

/**
 * Created by cjx on 2018\7\31 0031.
 */

public class TipPanel extends FrameLayout {

    ImageView mTxtTip;
    ImageView mImgTip;

    public TipPanel(Context context) {
        super(context);
    }

    public TipPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();

        mTxtTip=(ImageView)this.getChildAt(0);
        mImgTip=(ImageView)this.getChildAt(1);
     }

     public void showSuccess(){
         mTxtTip.setImageResource(R.mipmap.play_inclass_oktip);
         mImgTip.setImageResource(R.mipmap.play_inclass_ok);
         mTxtTip.postDelayed(new Runnable() {
             @Override
             public void run() {
                 AnimUtils.animationFromTo(mTxtTip,0, DensityUtils.dp2floatpx(getContext(),-30));
             }
         },400);
          AnimUtils.animHorShow(this,true);
     }

    public void showError(){
        mTxtTip.setImageResource(R.mipmap.play_inclass_errtip);
        mImgTip.setImageResource(R.mipmap.play_incalss_error);
        AnimUtils.animHorShow(this,true);
    }
}
