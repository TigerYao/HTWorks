package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.ImageLoad;

/**
 * desc:
 *
 * @author zhaodongdong
 * QQ: 676362303
 * email: androidmdeveloper@163.com
 */
public class MeAdvertiseHolder implements Holder<String> {

    private ImageView mImageView;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.me_adv_layout, null);
        mImageView = view.findViewById(R.id.iv);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        ImageLoad.load(context, data, mImageView, R.mipmap.icon_default_home_looper_view);
    }
}
