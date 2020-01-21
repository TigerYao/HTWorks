package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDetailListBean;

import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht-djd on 2017/9/15.
 *
 */

public class TeacherDetailListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TeacherDetailListBean.ResultBean> mVodCourseBean;
    private LayoutInflater inflater;

    public TeacherDetailListAdapter(Context context, ArrayList<TeacherDetailListBean.ResultBean> mTeacherDetailList) {
        this.mContext = context;
        this.mVodCourseBean = mTeacherDetailList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mVodCourseBean.size();
    }

    @Override
    public Object getItem(int position) {
        return mVodCourseBean.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TeacherDetailListBean.ResultBean resultBean = mVodCourseBean.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_fragment_vodcourse, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mDownTime.setVisibility(View.GONE);
        holder.mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
   /*     Glide.with(mContext).load(resultBean.scaleimg).diskCacheStrategy(DiskCacheStrategy.RESULT).
                placeholder(R.mipmap.load_default).into(holder.mShopIcon);*/

        ImageLoad.displaynoCacheImage(mContext,R.mipmap.load_default,resultBean.scaleimg,holder.mShopIcon);
            holder.mTitle.setText(resultBean.Title);
            holder.mTeacher.setText(resultBean.TeacherDesc);
        holder.mClass.setText(resultBean.TimeLength + "课时");
        SpannableStringBuilder styles = new SpannableStringBuilder(resultBean.buyNum + "人已抢");
        if (!TextUtils.isEmpty(resultBean.buyNum)){
            styles.setSpan(new ForegroundColorSpan(Color.parseColor("#e9304e")), 0, resultBean.buyNum.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        holder.mShopBuyNum.setText(styles);
        if (CommonUtils.isEmpty(resultBean.ActualPrice)) {
            holder.mPrice.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            holder.mPrice.setText(mContext.getString(R.string.lesson_tv_free));
            holder.mPrice.setTextSize(14);
            holder.mPrice.setTextColor(ContextCompat.getColor(mContext, R.color.text_bg_un_click));
        } else {
            holder.mPrice.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.mipmap.icon_price), null, null, null);
            holder.mPrice.setTextSize(16);
            holder.mPrice.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            holder.mPrice.setText(" "+resultBean.ActualPrice);
        }
        if( !TextUtils.isEmpty(resultBean.Price) &&
                (Method.parseFloat(resultBean.Price) > Method.parseFloat(resultBean.ActualPrice))) {
            holder.mOriginPriceLayout.setVisibility(View.VISIBLE);
            holder.mOriginPrice.setText("￥" + resultBean.Price);
        } else {
            holder.mOriginPriceLayout.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BuyDetailsActivity.newIntent(mContext, resultBean.rid);
                BaseIntroActivity.newIntent(mContext, resultBean.rid);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.item_shop_tv_end_time)
        TextView mDownTime;
        @BindView(R.id.item_shop_tv_title)
        TextView mTitle;
        @BindView(R.id.item_shop_tv_teacher)
        TextView mTeacher;
        @BindView(R.id.item_shop_tv_class)
        TextView mClass;
        @BindView(R.id.item_shop_buy_num)
        TextView mShopBuyNum;
        @BindView(R.id.item_shop_tv_price)
        TextView mPrice;
        @BindView(R.id.item_shop_img)
        ImageView mShopIcon;
        @BindView(R.id.item_shop_mark_today)
        ImageView mIconToday;
        @BindView(R.id.item_shop_sale_status)
        ImageView mSaleStatus;
        @BindView(R.id.item_shop_tv_total_price)
        TextView item_shop_tv_total_price;
        @BindView(R.id.item_shop_tv_origin_price_layout)
        LinearLayout mOriginPriceLayout;
        @BindView(R.id.item_shop_tv_origin_price)
        TextView mOriginPrice;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }

}
