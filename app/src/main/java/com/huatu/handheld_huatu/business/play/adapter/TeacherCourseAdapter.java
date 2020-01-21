package com.huatu.handheld_huatu.business.play.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseItemBean;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.view.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2018/7/10.
 */

public class TeacherCourseAdapter extends CommonAdapter<CourseTeacherCourseItemBean> {
    private final List<CourseTeacherCourseItemBean> courseList = new ArrayList<>();
    private int courseType;
    private Context mContext;
    public TeacherCourseAdapter(int courstype,Context context) {
        super();
        courseType = courstype;
        mData = courseList;
        mContext = context;
        layoutId = R.layout.course_teacher_course_item_layout;
    }

    public TeacherCourseAdapter(List<CourseTeacherCourseItemBean> data, int layoutId) {
        super(data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, CourseTeacherCourseItemBean bean, int position) {
        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "font/851-CAI978.ttf");
        final CourseTeacherCourseItemBean item = bean;
        ImageView imageView = holder.getView(R.id.course_teacher_course_item_img);
        ImageLoad.load(UniApplicationContext.getContext(), item.scaleimg, imageView);
        ImageView group = holder.getView(R.id.show_group);
        ImageView redenvelpe = holder.getView(R.id.show_red);
        if (bean.collageActiveId > 0) {
            group.setVisibility(View.VISIBLE);
        } else {
            group.setVisibility(View.GONE);
        }
        if (bean.redEnvelopeId > 0) {
            redenvelpe.setVisibility(View.VISIBLE);
        } else {
            redenvelpe.setVisibility(View.GONE);
        }
        holder.setText(R.id.course_teacher_course_item_title, item.Title);
        holder.setText(R.id.course_teacher_course_item_teacher, item.TeacherDesc);
        holder.setText(R.id.course_teacher_course_item_lesson_num, item.TimeLength + "课时");

        //多少人已购显示，0人不显示，非0人显示
        TextView buyNumber = holder.getView(R.id.course_teacher_course_item_buy_num);
        TextView buyShow = holder.getView(R.id.course_teacher_course_item_buy_show);
        if (item.showNum == 0){
            buyNumber.setVisibility(View.GONE);
            buyShow.setVisibility(View.GONE);
        } else {
            buyNumber.setText(""+item.showNum);
            buyNumber.setTypeface(type);
            buyNumber.setVisibility(View.VISIBLE);
            buyShow.setVisibility(View.VISIBLE);
            if (bean.collageActiveId > 0) {
                buyShow.setText("人已拼");
            }else
                buyShow.setText("人已抢");
        }
        //原价显示，原价和实价相同不显示，不相同显示
//        int originalPrice = 0;
//        try {
//            originalPrice = Integer.parseInt(item.Price);
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
        TextView tvOriginPrice = holder.getView(R.id.course_original_price);
        if (item.ActualPrice.equals(item.Price)) {
            tvOriginPrice.setVisibility(View.GONE);
        } else {
            tvOriginPrice.setVisibility(View.VISIBLE);
            tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvOriginPrice.setText("¥ " + item.Price);
        }
        //实际价格设置显示，非零显示，零显示免费
        TextView realPrice = holder.getView(R.id.course_teacher_course_item_price);
        if (item.ActualPrice.equals("0")) {
            realPrice.setTextColor(Color.parseColor("#46bb8c"));
            realPrice.setText("免费");
        } else {
            realPrice.setTextColor(ContextCompat.getColor(mContext, R.color.red250));
            realPrice.setText("  ¥ "+item.ActualPrice);
            setBoldText(realPrice);
        }
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, BaseIntroActivity.class);
                intent.putExtra("rid", item.rid);
                intent.putExtra("course_type",Integer.parseInt(item.videoType));
                intent.putExtra("price",String.valueOf(item.ActualPrice));
                intent.putExtra("saleout",0);  //未售罄
                intent.putExtra("rushout",0);  //未停售
                intent.putExtra("collageActiveId", item.collageActiveId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }
}
