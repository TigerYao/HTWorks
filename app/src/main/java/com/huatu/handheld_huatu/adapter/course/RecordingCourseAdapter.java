package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.countdown.CountDownTimers;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cjx on 17/8/17.
 */

public class RecordingCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_ONE = 0;
    private final int TYPE_TWO = 1;

    private Context mContext;
    private List<Lessons> mLessons;
    private CountDownTask mCountDownTask;
    private final int  COUNTDOWNINTERVAL=1000;

    private  final int COUNT_NONE=0;
    private  final int COUNT_ING_STATUS=1;
    private  final int COUNT_PAUSE_STATUS=2;

    private int  isCountStatus=COUNT_NONE;

    public void pauseTicks(){
         if(isCountStatus==COUNT_ING_STATUS){
            isCountStatus=COUNT_PAUSE_STATUS;
            LogUtils.e("onliveCourseFragment","pauseTicks");
            mCountDownTask.cancel();
         }
    }

    public void checkPauseStatus() {
        if (isCountStatus == COUNT_PAUSE_STATUS) {
            LogUtils.e("onliveCourseFragment","checkPauseStatus");
            this.notifyDataSetChanged();
        }
     }

    private final int HightLightColor=ResourceUtils.getColor(R.color.indicator_color);
    private OnRecItemClickListener onRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public RecordingCourseAdapter(Context context, List<Lessons> lessons) {
        this.mContext = context;
        this.mLessons=lessons;
        mCountDownTask = CountDownTask.create();
    }

    public void clearCountDownTask(){
        isCountStatus=COUNT_NONE;
        if(null!=mCountDownTask)   mCountDownTask.cancel();
    }

    public void clear(){
        if(mLessons!=null) mLessons.clear();
    }

    public Lessons getCurrentItem(int position){
        if(position>=0&&position<ArrayUtils.size(mLessons))
            return mLessons.get(position);
        return null;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          if(viewType==TYPE_ONE){
              View lessonView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_shopping_rushlessons, parent, false);
              return new ViewHolderOne(lessonView);

          }else {
              View collectionView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_shopping_collections, parent, false);
              return new ViewHolderTwo(collectionView);
          }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         switch (getItemViewType(position)) {
            case 0:
                final ViewHolderOne holdermult = (ViewHolderOne) holder;
                holdermult.bindUI(mLessons.get(position),position);
                break;
            case 1:
                ViewHolderTwo holderfour = (ViewHolderTwo) holder;
                holderfour.bindUI(mLessons.get(position),position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return ArrayUtils.size(mLessons);
    }


    private void startCountDown(final int position,final long remainTime,  View convertView,boolean isStarted) {
        //大于一天时间直接返回，不定时
        long diffTime= remainTime-CountDownTask.elapsedRealtime();//毫秒级
        if(diffTime>86400000) {
            TextView textView1 = (TextView) convertView.findViewById(R.id.item_shop_tv_end_time);
            String as_detail =(!isStarted? "距开抢&#160;":"距停售&#160;")+ StringUtils.fontColor("#FF3F47", DateUtils.formatStrTime(diffTime/1000)) ;
            textView1.setText(StringUtils.forHtml(as_detail));
            cancelCountDown(position,0,convertView);
            return;
        }
        if (mCountDownTask != null) {
            isCountStatus=COUNT_ING_STATUS;
            mCountDownTask.until(convertView, remainTime,  COUNTDOWNINTERVAL, new CountDownTimers.OnCountDownListener() {
                @Override
                public void onTick(View view, long millisUntilFinished) {
                    LogUtils.e("onliveCourseFragment",millisUntilFinished+"");
                    doOnTick(position, view, millisUntilFinished);
                }

                @Override
                public void onFinish(View view) {
                    doOnFinish(position, view);
                }
            });
        }
    }

    private void doOnTick(int position, View view, long millisUntilFinished) {
        TextView textView1 = (TextView) view.findViewById(R.id.item_shop_tv_end_time);

        boolean keepStarted="1".equals(textView1.getTag(R.id.reuse_tag2));
        String as_detail = (keepStarted ? "距开抢&#160;" : "距停售&#160;")
                + StringUtils.fontColor("#FF3F47", DateUtils.formatStrTime(millisUntilFinished / 1000));
        textView1.setText(StringUtils.forHtml(as_detail));

    }

    private void doOnFinish(int position, View view) {
        TextView textView1 = (TextView) view.findViewById(R.id.item_shop_tv_end_time);
        boolean keepStarted="1".equals(textView1.getTag(R.id.reuse_tag2));
        String as_detail = (keepStarted ? "距开抢&#160;" : "距停售&#160;")
                + StringUtils.fontColor("#FF3F47", "00:00:00");
        textView1.setText(StringUtils.forHtml(as_detail));

    }

    private void cancelCountDown(int position,long millisUntilFinished,  View view) {
        if (mCountDownTask != null&&(isCountStatus==COUNT_ING_STATUS)) {
            mCountDownTask.cancel(view);
        }
    }


    protected class baseHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.group_tag_txt)
        TextView mGroupTag;

        @BindView(R.id.red_tag_txt)
        TextView mRedTag;

        @BindView(R.id.item_shop_mark_today)
        ImageView mIconToday;

        baseHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                }
            });
        }

        public void bindUI(Lessons lessons,int position){

            if (lessons.iszhibo == 1) {
                this.mIconToday.setVisibility(View.VISIBLE);
            } else {
                this.mIconToday.setVisibility(View.GONE);
            }
            int curTag=(lessons.collageActiveId>0? 1:0)*10+(lessons.redEnvelopeId>0?1:0);
            if(curTag==11){
                mGroupTag.setVisibility(View.VISIBLE);
                mGroupTag.setText("拼团");
                mRedTag.setVisibility(View.VISIBLE);
                mRedTag.setText("红包");
            }else if(curTag==10){
                mGroupTag.setVisibility(View.VISIBLE);
                mGroupTag.setText("拼团");
                mRedTag.setVisibility(View.GONE);
            }else if(curTag==1){
                mGroupTag.setVisibility(View.VISIBLE);
                mGroupTag.setText("红包");
                mRedTag.setVisibility(View.GONE);
            }else {
                mGroupTag.setVisibility(View.GONE);
                mRedTag.setVisibility(View.GONE);
            }
         }
     }

    protected class ViewHolderOne extends baseHolder {
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


        ViewHolderOne(View itemView) {
            super(itemView);
        }

        @Override
        public void bindUI(Lessons lessons,int position){
             super.bindUI(lessons,position);
             if(lessons.isTermined == 1) {
                if(!TextUtils.isEmpty(lessons.terminedDesc)) {
                    this.mDownTime.setText(lessons.terminedDesc);
                } else {
                    this.mDownTime.setText("待售");
                }
                this.mDownTime.setTextColor(HightLightColor);
                this.mDownTime.setVisibility(View.VISIBLE);
            } else if (lessons.isRushClass == 1
                    && (lessons.lSaleStart > 0 ||lessons.lSaleEnd > 0)) {
                this.mDownTime.setVisibility(View.VISIBLE);
                if (lessons.lSaleStart > 0) {
                    this.mDownTime.setTag(R.id.reuse_tag2,"1");
                    startCountDown(position, lessons.lSaleStart,this.itemView,false);
                } else if (lessons.lSaleEnd > 0) {
                    this.mDownTime.setTag(R.id.reuse_tag2,"2");
                    startCountDown(position, lessons.lSaleEnd,this.itemView,true);
                }
            } else {
                cancelCountDown(position,0,this.itemView);
                this.mDownTime.setVisibility(View.GONE);
            }
           // ImageLoad.load(mContext, lessons.scaleimg, holderOne.mShopIcon);
            ImageLoad.displaynoCacheImage(this.mShopIcon.getContext(),R.mipmap.load_default,lessons.scaleimg,this.mShopIcon);
            if (lessons.isSeckill == 1) {
                String title = " 秒杀 " + " "+lessons.title;
                SpannableString style=new SpannableString (title);
                style.setSpan(new BackgroundColorSpan(HightLightColor),0,4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(Color.WHITE),0,4,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                style.setSpan(new RelativeSizeSpan(0.8f), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                this.mTitle.setText(style);
            } else {
                this.mTitle.setText(lessons.title);
            }
            this.mTeacher.setText(lessons.TeacherDesc);

            if(!TextUtils.isEmpty(lessons.CourseLength)){
                if(lessons.CourseLength.contains("课时")) {
                    this.mClass.setText(lessons.CourseLength);
                }else {
                    this.mClass.setText(lessons.CourseLength+ "课时");
                }
            }else {
                this.mClass.setText(lessons.CourseLength+ "课时");
            }

            SpannableStringBuilder builder = new SpannableStringBuilder("");

            int lessionBuyNum= Method.parseInt(lessons.buyNum);
            if(lessons.collageActiveId>0&&lessionBuyNum>0){
                String tmpStr = "人已拼";
                builder.append(String.valueOf(lessionBuyNum));
                builder.setSpan(new ForegroundColorSpan(HightLightColor), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.append(tmpStr);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, lessons.buyNum.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(Color.BLACK), builder.length() - tmpStr.length(), builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
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
                    String tmpStr="人已拼";
                    builder.append(lessons.collageActiveId>0?tmpStr:ResourceUtils.getString(R.string.lesson_tv_buy_grab));
                    if(lessons.collageActiveId>0){
                        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, lessons.buyNum.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        builder.setSpan(new ForegroundColorSpan(Color.BLACK), builder.length()-tmpStr.length(), builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
            } else if (lessionBuyNum > 0){
                builder.append(String.valueOf(lessons.buyNum));
                builder.setSpan(new ForegroundColorSpan(HightLightColor), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.append(ResourceUtils.getString(R.string.lesson_tv_buy_grab));
           }
            this.mShopBuyNum.setText(builder);
            this.mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if(lessons.isSaleOut != 1 && lessons.isRushOut != 1
                    && !TextUtils.isEmpty(lessons.Price) &&
                    (Method.parseFloat(lessons.Price) > Method.parseFloat(lessons.ActualPrice))) {
                this.mOriginPrice.setVisibility(View.VISIBLE);
                this.mOriginPrice.setText("¥" + lessons.Price);
            } else {
                this.mOriginPrice.setVisibility(View.INVISIBLE);
            }
            if (CommonUtils.isEmpty(lessons.ActualPrice)) {
                this.mPrice.setVisibility(View.VISIBLE);
                this.mPrice.setText(ResourceUtils.getString(R.string.lesson_tv_free));
                this.mPrice.setTextSize(14);
                this.mPrice.setTextColor(Color.parseColor("#46bb8c"));
            } else {
                this.mPrice.setVisibility(View.VISIBLE);
                this.mPrice.setTextSize(16);
                this.mPrice.setTextColor(HightLightColor);
                this.mPrice.setText("¥" + lessons.ActualPrice);
            }
            if(lessons.isTermined == 1) {
                this.tvSaleState.setVisibility(View.GONE);
            } else if (lessons.isSaleOut == 1) {
                this.tvSaleState.setText("售罄");
                this.tvSaleState.setVisibility(View.VISIBLE);
                this.mOriginPrice.setVisibility(View.INVISIBLE);
//                this.mPrice.setTextColor(Color.parseColor("#999999"));
                this.mPrice.setVisibility(View.INVISIBLE);
            } else if (lessons.isRushOut == 1) {
                this.tvSaleState.setText("停售");
                this.tvSaleState.setVisibility(View.VISIBLE);
                this.mOriginPrice.setVisibility(View.INVISIBLE);
//                this.mPrice.setTextColor(Color.parseColor("#999999"));
                this.mPrice.setVisibility(View.INVISIBLE);
            } else {
                this.tvSaleState.setVisibility(View.GONE);
            }
        }

    }

    protected class ViewHolderTwo extends baseHolder {

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

        ViewHolderTwo(View itemView) {
            super(itemView);
        }

       @Override
       public void bindUI(Lessons lessons,int position){
            super.bindUI(lessons, position);

           ImageLoad.displaynoCacheImage(this.mShopIcon.getContext(),R.mipmap.load_default,lessons.scaleimg,this.mShopIcon);
           this.mTitle.setText(lessons.title);
           this.mShortTitle.setText("  " + lessons.ShortTitle);
           this.mTeacher.setText(lessons.TeacherDesc);
           this.mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
           if(lessons.isCollect == 1 && !TextUtils.isEmpty(lessons.Price)
                   && lessons.Price.contains("-")) {
               this.mOriginPrice.setVisibility(View.GONE);
           } else if(lessons.isSaleOut != 1 && lessons.isRushOut != 1
                   && !TextUtils.isEmpty(lessons.Price)
                   && !Method.isEqualString(lessons.Price, lessons.ActualPrice)) {
               this.mOriginPrice.setVisibility(View.VISIBLE);
               this.mOriginPrice.setText("¥" + lessons.Price);
           } else {
               this.mOriginPrice.setVisibility(View.GONE);
           }
           if (CommonUtils.isEmpty(lessons.ActualPrice)) {
               this.mPrice.setText(ResourceUtils.getString(R.string.lesson_tv_free));
               this.mPrice.setTextSize(14);
               this.mPrice.setTextColor(Color.parseColor("#46bb8c"));
           } else {
               this.mPrice.setTextSize(16);
               this.mPrice.setTextColor(HightLightColor);
               this.mPrice.setText("¥" + lessons.ActualPrice);
           }
           if(lessons.isTermined == 1) {
               if(!TextUtils.isEmpty(lessons.terminedDesc)) {
                   this.mDownTime.setText(lessons.terminedDesc);
               } else {
                   this.mDownTime.setText("待售");
               }
               this.mDownTime.setTextColor(HightLightColor);
               this.mDownTime.setVisibility(View.VISIBLE);
           } else if (lessons.lSaleStart > 0 ||lessons.lSaleEnd > 0) {
               this.mDownTime.setVisibility(View.VISIBLE);
               if (lessons.lSaleStart > 0) {
                   this.mDownTime.setTag(R.id.reuse_tag2,"1");
                   startCountDown(position, lessons.lSaleStart,this.itemView,false);
               } else if (lessons.lSaleEnd > 0) {
                   this.mDownTime.setTag(R.id.reuse_tag2,"2");
                   startCountDown(position, lessons.lSaleEnd,this.itemView,true);
               }
           } else {
               cancelCountDown(position,0,this.itemView);
               this.mDownTime.setVisibility(View.GONE);
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
           //public int purchasType; //限报形式 0默认1限招2仅剩
           else if(lessons.purchasType == 2) {//2仅剩
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
           } else if(lessons.purchasType == 1) {//1限招
               if(lessons.isSaleOut == 1) {
                   builderTwo.append("限招");
                   builderTwo.append(lessons.collectLimitUserCount  + "/" + lessons.collectLimitUserCount );
                   builderTwo.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(
                           R.color.indicator_color)), 2, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
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
               if(lessons.collectShowNum>0){
                   builderTwo.append(String.valueOf(lessons.collectShowNum));
                   builderTwo.setSpan(new ForegroundColorSpan(HightLightColor), 0, builderTwo.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                   builderTwo.append(ResourceUtils.getString(R.string.lesson_tv_buy_grab));
               }

           }
           this.mShopBuyNum.setText(builderTwo);
       }
    }



}
