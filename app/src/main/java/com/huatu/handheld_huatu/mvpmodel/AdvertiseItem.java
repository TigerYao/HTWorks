package com.huatu.handheld_huatu.mvpmodel;

import android.text.TextUtils;

import com.huatu.handheld_huatu.mvpmodel.arena.ExamPagerItem;
import com.huatu.handheld_huatu.mvpmodel.exercise.SimulationListItem;

import java.io.Serializable;

/**
 * @author zhaodongdong
 */
public class AdvertiseItem implements Serializable {
    public int id;
    public String title;
    public String shortTitle;
    public String image;
    public String url;//指定链接
    public String  padImageUrl;//表示平板对应的广告图
    public long rid;
    public String text;
    public int hide;
    public int subject;
    public int catgory;
    public int type;
    public int paperId;
    public long areaId;
    public int area;
    public ExamPagerItem.ExamPaperResult pastPaper;
    public SimulationListItem estimatePaper;
    public int width;
    public int height;
    public String content;
    public String areaName;

    //public int needLogin;//判断是否要登录


    // 自己添加字段，添加点击来源，用于神策向前来源
    public String from;
    public int cateId;//跳转课程列表定位到具体科目

    public String formatString(){
        return "title="+title+"&url="+url;
    }

    //   intent.putExtra("AdvNetClassId", params.rid + "");
     //           intent.putExtra("course_type", params.type);

    public String formatString(boolean tohome,String pageSource){

            return    "classId="+rid+"&course_type="+type+"&subject="+subject
                       +( TextUtils.isEmpty(shortTitle)?"":"&shortTitle="+shortTitle  )
                       +( TextUtils.isEmpty(title)     ?"":"&title="+title  )
                       +( TextUtils.isEmpty(url)       ?"":"&url="+url.replaceAll("#","{n}")  )
                       +( !tohome                      ?"":"&toHome=1"  )
                       +( TextUtils.isEmpty(pageSource)   ?"":"&from="+pageSource );

    }
}
