package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ArenaHomefTreeItemAccuracyView extends LinearLayout {

    @BindView(R.id.homef_listitem_accuracy_0_iv)
    ImageView homefListitemAccuracy0Iv;
    @BindView(R.id.homef_listitem_accuracy_1_iv)
    ImageView homefListitemAccuracy1Iv;
    @BindView(R.id.homef_listitem_accuracy_2_iv)
    ImageView homefListitemAccuracy2Iv;
    @BindView(R.id.homef_listitem_accuracy_3_iv)
    ImageView homefListitemAccuracy3Iv;
    @BindView(R.id.homef_listitem_accuracy_4_iv)
    ImageView homefListitemAccuracy4Iv;
    private Context mContext;
    private View rootView;


    private int resId = R.layout.layout_item_partv_homef_listitem_accuracy_view_ll;
    private int requestType;
    private String tagFrom;
    private boolean isAttached = false;
    private boolean canShowTip = true;
    private double mAccuracy = -1;
    private String TAG = "ArenaHomefTreeItemAccuracyView";

    public ArenaHomefTreeItemAccuracyView(Context context) {
        super(context);
        init(context);
    }

    public ArenaHomefTreeItemAccuracyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaHomefTreeItemAccuracyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        initView();
    }

    private void initView() {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    public void updateViews(double accuracy) {
        if (this.mAccuracy == accuracy) {
            return;
        }
        LogUtils.i(TAG, " mAccuracy " + mAccuracy + " accuracy " + accuracy);
        this.mAccuracy = accuracy;
        homefListitemAccuracy0Iv.setImageResource(R.mipmap.homef_item_water_percent_no);
        homefListitemAccuracy1Iv.setImageResource(R.mipmap.homef_item_water_percent_no);
        homefListitemAccuracy2Iv.setImageResource(R.mipmap.homef_item_water_percent_no);
        homefListitemAccuracy3Iv.setImageResource(R.mipmap.homef_item_water_percent_no);
        homefListitemAccuracy4Iv.setImageResource(R.mipmap.homef_item_water_percent_no);
        if (accuracy > 0 && accuracy <= 20) {
            homefListitemAccuracy0Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
        } else if (accuracy > 20 && accuracy <= 40) {
            homefListitemAccuracy0Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy1Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
        } else if (accuracy > 40 && accuracy <= 60) {
            homefListitemAccuracy0Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy1Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy2Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
        } else if (accuracy > 60 && accuracy <= 80) {
            homefListitemAccuracy0Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy1Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy2Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy3Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
        } else if (accuracy > 80 && accuracy <= 100) {
            homefListitemAccuracy0Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy1Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy2Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy3Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
            homefListitemAccuracy4Iv.setImageResource(R.mipmap.homef_item_water_percent_has);
        }

    }


}
