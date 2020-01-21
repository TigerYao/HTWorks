package com.huatu.handheld_huatu.business.lessons.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CHQ on 2017/4/7.
 */

public class NewShoppingAdapter extends BaseAdapter {
    private static final String TAG = "NewShoppingAdapter";
    private Context mContext;
    private List<Lessons> mLessons;
    final int TYPE_COUNT = 2;
    private final int TYPE_ONE = 0;

    private final int TYPE_TWO = 1;

    private LayoutInflater inflater;
    private final int HightLightColor=ResourceUtils.getColor(R.color.indicator_color);
    public NewShoppingAdapter(Context context) {
        this.mContext = context;
        mLessons = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mLessons.size();
    }

    @Override
    public Object getItem(int position) {
        return mLessons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lessons lessons = mLessons.get(position);
        int type = getItemViewType(position);
        ViewHolderOne holderOne;
        ViewHolderTwo holderTwo;
        switch (type) {
            case TYPE_ONE:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_fragment_shopping_rushlessons, parent, false);
                    holderOne = new ViewHolderOne(convertView);
                } else {
                    holderOne = (ViewHolderOne) convertView.getTag();
                }
            /*    if(lessons.collageActiveId>0){
                    holderOne.mGroupTag.setVisibility(View.VISIBLE);
                    holderOne.mGroupTag.setText("拼团");
                    // holderTwo.mGroupTag.setEnabled(lessons.collageIsBuy==0);
                }else {
                    holderOne.mGroupTag.setVisibility(View.GONE);
                }*/

                int curTag=(lessons.collageActiveId>0? 1:0)*10+(lessons.redEnvelopeId>0?1:0);
                if(curTag==11){
                    holderOne.mGroupTag.setVisibility(View.VISIBLE);
                    holderOne.mGroupTag.setText("拼团");
                    holderOne.mRedTag.setVisibility(View.VISIBLE);
                    holderOne.mRedTag.setText("红包");
                }else if(curTag==10){
                    holderOne.mGroupTag.setVisibility(View.VISIBLE);
                    holderOne.mGroupTag.setText("拼团");
                    holderOne.mRedTag.setVisibility(View.GONE);
                }else if(curTag==1){
                    holderOne.mGroupTag.setVisibility(View.VISIBLE);
                    holderOne.mGroupTag.setText("红包");
                    holderOne.mRedTag.setVisibility(View.GONE);
                }else {
                    holderOne.mGroupTag.setVisibility(View.GONE);
                    holderOne.mRedTag.setVisibility(View.GONE);
                }

                if(lessons.isTermined == 1) {
                    if(!TextUtils.isEmpty(lessons.terminedDesc)) {
                        holderOne.mDownTime.setText(lessons.terminedDesc);
                    } else {
                        holderOne.mDownTime.setText("待售");
                    }
                    holderOne.mDownTime.setTextColor(HightLightColor);
                    holderOne.mDownTime.setVisibility(View.VISIBLE);
                } else if (lessons.isRushClass == 1
                        && (lessons.lSaleStart > 0 ||lessons.lSaleEnd > 0)) {
                    holderOne.mDownTime.setVisibility(View.VISIBLE);
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    if (lessons.lSaleStart > 0) {
                        builder.append(mContext.getString(R.string.lesson_tv_time_start));
                        builder.append(DateUtils.formatTime(lessons.lSaleStart));
                        holderOne.mDownTime.setText(builder);
                    } else if (lessons.lSaleEnd > 0) {
                        builder.append(mContext.getString(R.string.lesson_tv_time_end));
                        builder.append(DateUtils.formatTime(lessons.lSaleEnd));
                        holderOne.mDownTime.setText(builder);
                    }
                } else {
                    holderOne.mDownTime.setVisibility(View.GONE);
                }
                ImageLoad.load(mContext, lessons.scaleimg, holderOne.mShopIcon);
                if (lessons.isSeckill == 1) {
                    String title = " 秒杀 " + " "+lessons.title;
                    SpannableString style=new SpannableString (title);
                    style.setSpan(new BackgroundColorSpan(Color.parseColor("#e9304e")),0,4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(new ForegroundColorSpan(Color.WHITE),0,4,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    style.setSpan(new RelativeSizeSpan(0.8f), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holderOne.mTitle.setText(style);
                } else {
                    holderOne.mTitle.setText(lessons.title);
                }
                holderOne.mTeacher.setText(lessons.TeacherDesc);
                holderOne.mClass.setText(lessons.CourseLength);
                if (lessons.iszhibo == 1) {
                    holderOne.mIconToday.setVisibility(View.VISIBLE);
                   // holderOne.mIconToday.setImageResource(R.drawable.mark_today);
                } else {
                    holderOne.mIconToday.setVisibility(View.GONE);
                }
                SpannableStringBuilder builder = new SpannableStringBuilder("");
                int lessionBuyNum= Method.parseInt(lessons.buyNum);
                if(lessons.collageActiveId>0&&lessionBuyNum>0){
                    String tmpStr="人已拼";
                    builder.append(String.valueOf(lessionBuyNum));
                    builder.setSpan(new ForegroundColorSpan(HightLightColor), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.append(tmpStr);

                    builder.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf(lessionBuyNum).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.BLACK), builder.length()-tmpStr.length(), builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
               else if(lessons.limitUserCount > 0) {
                    if(lessons.purchasType == 2) {
                        builder.append("仅剩");
                        if (lessons.isSaleOut != 1) {
                            builder.append(String.valueOf(lessons.limitUserCount - Method.parseInt(lessons.buyNum)));
                            builder.setSpan(new ForegroundColorSpan(HightLightColor), 2, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            builder.append("名额");
                        } else {
                            builder = new SpannableStringBuilder("");
                        }
                    } else if(lessons.purchasType == 1) {
                        builder.append("限招");
                        if(lessons.isSaleOut == 1) {
                            builder.append(lessons.limitUserCount + "/" + lessons.limitUserCount);
                        } else if(Method.parseInt(lessons.buyNum) == 0) {
                            builder.append(String.valueOf(lessons.limitUserCount));
                        } else {
                            builder.append(lessons.buyNum + "/" + lessons.limitUserCount);
                        }
                        builder.setSpan(new ForegroundColorSpan(HightLightColor), 2, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builder.append("名额");
                    } else if (Method.parseInt(lessons.buyNum) > 0) {
                        builder.append(String.valueOf(lessons.buyNum));
                        builder.setSpan(new ForegroundColorSpan(HightLightColor), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builder.append(mContext.getString(R.string.lesson_tv_buy_grab));
                    }
                } else if (Method.parseInt(lessons.buyNum) > 0){
                    builder.append(String.valueOf(lessons.buyNum));
                    builder.setSpan(new ForegroundColorSpan(HightLightColor), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.append(ResourceUtils.getString(R.string.lesson_tv_buy_grab));
                }
                holderOne.mShopBuyNum.setText(builder);
                holderOne.mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                if(lessons.isSaleOut != 1 && lessons.isRushOut != 1
                        && !TextUtils.isEmpty(lessons.Price) &&
                        (Method.parseFloat(lessons.Price) > Method.parseFloat(lessons.ActualPrice))) {
                    holderOne.mOriginPrice.setVisibility(View.VISIBLE);
                    holderOne.mOriginPrice.setText("¥" + lessons.Price);
                } else {
                    holderOne.mOriginPrice.setVisibility(View.INVISIBLE);
                }
                if (CommonUtils.isEmpty(lessons.ActualPrice)) {
                    holderOne.mPrice.setText(mContext.getString(R.string.lesson_tv_free));
                    holderOne.mPrice.setTextSize(14);
                    holderOne.mPrice.setTextColor(Color.parseColor("#46bb8c"));
                } else {
                    holderOne.mPrice.setTextSize(16);
                    holderOne.mPrice.setTextColor(HightLightColor);
                    holderOne.mPrice.setText("¥" + lessons.ActualPrice);
                }
                if(lessons.isTermined == 1) {
                    holderOne.tvSaleState.setVisibility(View.GONE);
                } else if (lessons.isSaleOut == 1) {
                    holderOne.tvSaleState.setText("售罄");
                    holderOne.tvSaleState.setVisibility(View.VISIBLE);
                    holderOne.mOriginPrice.setVisibility(View.INVISIBLE);
                    holderOne.mPrice.setTextColor(Color.parseColor("#999999"));
                } else if (lessons.isRushOut == 1) {
                    holderOne.tvSaleState.setText("停售");
                    holderOne.tvSaleState.setVisibility(View.VISIBLE);
                    holderOne.mOriginPrice.setVisibility(View.INVISIBLE);
                    holderOne.mPrice.setTextColor(Color.parseColor("#999999"));
                } else {
                    holderOne.tvSaleState.setVisibility(View.GONE);
                }
                break;
            case TYPE_TWO:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_fragment_shopping_collections, parent, false);
                    holderTwo = new ViewHolderTwo(convertView);
                } else {
                    holderTwo = (ViewHolderTwo) convertView.getTag();
                }


                int curTag2=(lessons.collageActiveId>0? 1:0)*10+(lessons.redEnvelopeId>0?1:0);
                if(curTag2==11){
                    holderTwo.mGroupTag.setVisibility(View.VISIBLE);
                    holderTwo.mGroupTag.setText("拼团");
                    holderTwo.mRedTag.setVisibility(View.VISIBLE);
                    holderTwo.mRedTag.setText("红包");
                }else if(curTag2==10){
                    holderTwo.mGroupTag.setVisibility(View.VISIBLE);
                    holderTwo.mGroupTag.setText("拼团");
                    holderTwo.mRedTag.setVisibility(View.GONE);
                }else if(curTag2==1){
                    holderTwo.mGroupTag.setVisibility(View.VISIBLE);
                    holderTwo.mGroupTag.setText("红包");
                    holderTwo.mRedTag.setVisibility(View.GONE);
                }else {
                    holderTwo.mGroupTag.setVisibility(View.GONE);
                    holderTwo.mRedTag.setVisibility(View.GONE);
                }
                GlideApp.with(mContext).load(lessons.scaleimg).
                        placeholder(R.mipmap.load_default).into(holderTwo.mShopIcon);
                holderTwo.mTitle.setText(lessons.title);
                holderTwo.mShortTitle.setText("  " + lessons.ShortTitle);
                holderTwo.mTeacher.setText(lessons.TeacherDesc);
                holderTwo.mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                if(lessons.isCollect == 1 && !TextUtils.isEmpty(lessons.Price)
                        && lessons.Price.contains("-")) {
                    holderTwo.mOriginPrice.setVisibility(View.GONE);
                } else if(lessons.isSaleOut != 1 && lessons.isRushOut != 1
                        && !TextUtils.isEmpty(lessons.Price)
                        && !Method.isEqualString(lessons.Price, lessons.ActualPrice)) {
                    holderTwo.mOriginPrice.setVisibility(View.VISIBLE);
                    holderTwo.mOriginPrice.setText("¥" + lessons.Price);
                } else {
                    holderTwo.mOriginPrice.setVisibility(View.GONE);
                }
                if (CommonUtils.isEmpty(lessons.ActualPrice)) {
                    holderTwo.mPrice.setText(mContext.getString(R.string.lesson_tv_free));
                    holderTwo.mPrice.setTextSize(14);
                    holderTwo.mPrice.setTextColor(Color.parseColor("#46bb8c"));
                } else {
                    holderTwo.mPrice.setTextSize(16);
                    holderTwo.mPrice.setTextColor(HightLightColor);
                    holderTwo.mPrice.setText("¥" + lessons.ActualPrice);
                }
                if(lessons.isTermined == 1) {
                    if(!TextUtils.isEmpty(lessons.terminedDesc)) {
                        holderTwo.mDownTime.setText(lessons.terminedDesc);
                    } else {
                        holderTwo.mDownTime.setText("待售");
                    }
                    holderTwo.mDownTime.setTextColor(HightLightColor);
                    holderTwo.mDownTime.setVisibility(View.VISIBLE);
                } else if (lessons.lSaleStart > 0 ||lessons.lSaleEnd > 0) {
                    holderTwo.mDownTime.setVisibility(View.VISIBLE);
                    SpannableStringBuilder builder2 = new SpannableStringBuilder();
                    if (lessons.lSaleStart > 0) {
                        builder2.append(mContext.getString(R.string.lesson_tv_time_start));
                        builder2.append(DateUtils.formatTime(lessons.lSaleStart));
                    } else if (lessons.lSaleEnd > 0) {
                        builder2.append(mContext.getString(R.string.lesson_tv_time_end));
                        builder2.append(DateUtils.formatTime(lessons.lSaleEnd));
                    }
                    holderTwo.mDownTime.setText(builder2);
                } else {
                    holderTwo.mDownTime.setVisibility(View.GONE);
                }
                SpannableStringBuilder builderTwo = new SpannableStringBuilder("");


                if(lessons.collageActiveId>0&&lessons.collectShowNum>0){
                    String tmpStr="人已拼";
                    builderTwo.append(String.valueOf(lessons.collectShowNum));
                    builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 0, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builderTwo.append(tmpStr);

                    builderTwo.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf(lessons.collectShowNum).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    builderTwo.setSpan(new ForegroundColorSpan(Color.BLACK), builderTwo.length()-tmpStr.length(), builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                else if(lessons.purchasType == 2) {
                    if (lessons.isSaleOut != 1) {
                        builderTwo.append("仅剩");
                        if(lessons.collectLimitUserCount >= lessons.collectShowNum) {
                            builderTwo.append(String.valueOf(lessons.collectLimitUserCount  - lessons.collectShowNum));
                            builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 2, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            builderTwo.append("名额");
                            if (lessons.collectLimitUserCount - lessons.collectShowNum == 0) {
                                builderTwo = new SpannableStringBuilder("");
                            }
                        } else {
                            builderTwo = new SpannableStringBuilder("");
                        }
                    } else {
                        builderTwo = new SpannableStringBuilder("");
                    }
                } else if(lessons.purchasType == 1) {
                    if(lessons.isSaleOut == 1) {
                        builderTwo.append("限招");
                        builderTwo.append(lessons.collectLimitUserCount  + "/" + lessons.collectLimitUserCount );
                        builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 2, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builderTwo.append("名额");
                    } else if(lessons.collectLimitUserCount > 0) {
                        builderTwo.append("限招");
                        if(lessons.collectLimitUserCount >= lessons.collectShowNum) {
                            builderTwo.append(lessons.collectShowNum + "/" + lessons.collectLimitUserCount);
                        } else {
                            builderTwo.append(lessons.collectLimitUserCount  + "/" + lessons.collectLimitUserCount );
                        }
                        builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 2, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builderTwo.append("名额");
                    } else if(lessons.collectShowNum > 0) {
                        builderTwo.append(String.valueOf(lessons.collectShowNum));
                        builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 0, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builderTwo.append("已抢");
                    } else {
                        builderTwo = new SpannableStringBuilder("");
                    }
                } else {

                    builderTwo.append(String.valueOf(lessons.collectShowNum));
                    builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 0, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builderTwo.append( ResourceUtils.getString(R.string.lesson_tv_buy_grab));

                }
                holderTwo.mShopBuyNum.setText(builderTwo);
                break;
            default:
                break;
        }


        return convertView;
    }

    static class ViewHolderOne {
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
        @BindView(R.id.item_shop_tv_origin_price)
        TextView mOriginPrice;
        @BindView(R.id.item_shop_tv_state)
        TextView tvSaleState;
        @BindView(R.id.item_shop_img)
        ImageView mShopIcon;
        @BindView(R.id.item_shop_mark_today)
        ImageView mIconToday;

        @BindView(R.id.group_tag_txt)
        TextView mGroupTag;

        @BindView(R.id.red_tag_txt)
        TextView mRedTag;

        ViewHolderOne(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
    static class ViewHolderTwo {

        @BindView(R.id.item_shop_tv_title)
        TextView mTitle;
        @BindView(R.id.item_shop_tv_teacher)
        TextView mTeacher;
        @BindView(R.id.item_shop_tv_price)
        TextView mPrice;
        @BindView(R.id.item_shop_tv_origin_price)
        TextView mOriginPrice;
        @BindView(R.id.item_shop_img)
        ImageView mShopIcon;
        @BindView(R.id.item_shop_tv_shortTitle)
        TextView mShortTitle;
        @BindView(R.id.item_shop_tv_end_time)
        TextView mDownTime;
        @BindView(R.id.item_shop_buy_num)
        TextView mShopBuyNum;

        @BindView(R.id.group_tag_txt)
        TextView mGroupTag;

        @BindView(R.id.red_tag_txt)
        TextView mRedTag;

        ViewHolderTwo(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }


    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Lessons lessons = mLessons.get(position);
        if (lessons.isCollect == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }

    public void setData(List<Lessons> lessons) {
        mLessons = lessons;
        notifyDataSetChanged();
    }
}