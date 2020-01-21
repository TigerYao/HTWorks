package com.huatu.handheld_huatu.adapter.course;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.view.CommonAdapter;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2018\7\10 0010.
 */

public class DownAddAdapter extends CommonAdapter<DownLoadLesson> {

    public DownAddAdapter(List<DownLoadLesson> data, int layoutId){
        super(data,layoutId);
    }

    public static boolean isCoursewareDownloaded(DownLoadLesson courseware) {
        if(courseware.getDownStatus() == DownLoadStatusEnum.FINISHED.getValue()) {//2
            return true;
        }
        return false;
    }

    public static boolean isCoursewareDownloading(DownLoadLesson courseware) {
        if(courseware.getDownStatus() ==DownLoadStatusEnum.INIT.getValue()               // -2
                || courseware.getDownStatus() == DownLoadStatusEnum.PREPARE.getValue()   //-1
                || courseware.getDownStatus() == DownLoadStatusEnum.START.getValue()     // 1
                || courseware.getDownStatus() == DownLoadStatusEnum.STOP.getValue()      //4
                || courseware.getDownStatus() == DownLoadStatusEnum.ERROR.getValue()) {  //3
            return true;
        }
        return false;
    }

    public static boolean isDownedOrLoading(DownLoadLesson courseware){
        return isCoursewareDownloaded(courseware) || isCoursewareDownloading(courseware);
    }
    DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分
    private String formatFileSize(long space){
        float total =  ((float) space / 1024 / 1024);
        String spaceStr=total<=0.1 ? (int)(space / 1024)+"K":
                total>1024 ? df.format(total/1024) +"G":df.format(total)+"M";
        return spaceStr;
    }

    @Override
    public void convert(ViewHolder holder, final DownLoadLesson item, int position) {

        int curPostion=item.getLesson();
        Spannable span = new SpannableString(curPostion+"  "+item.getSubjectName());
        span.setSpan(new RelativeSizeSpan(1.2f), 0, curPostion<10 ?1:2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView nameTxt= holder.getView(R.id.tv_record_lesson_name);
        if(nameTxt!=null) nameTxt.setText(span);

        if (isCoursewareDownloaded(item)) {
            holder.setViewVisibility(R.id.iv_record_status, View.INVISIBLE);
            holder.setViewVisibility(R.id.status_txt,View.VISIBLE);
            holder.setText(R.id.status_txt,"已下载");
            //holder.setImageResource(R.id.iv_record_status, R.drawable.record_down_play);

        } else if (isCoursewareDownloading(item)) {
            holder.setViewVisibility(R.id.iv_record_status,View.INVISIBLE);
            holder.setViewVisibility(R.id.status_txt,View.VISIBLE);
            holder.setText(R.id.status_txt,"");
            //holder.setImageResource(R.id.iv_record_status, R.drawable.record_down_unplay);

        } else if(item.isSelect()) {
            holder.setViewVisibility(R.id.iv_record_status,View.VISIBLE);
            holder.setImageResource(R.id.iv_record_status, R.mipmap.down_check_icon);
            holder.setViewVisibility(R.id.status_txt,View.VISIBLE);
            holder.setText(R.id.status_txt,formatFileSize(item.getSpace()));

        } else {
            holder.setViewVisibility(R.id.iv_record_status,View.VISIBLE);
            holder.setImageResource(R.id.iv_record_status, R.mipmap.down_uncheck_icon);
            holder.setViewVisibility(R.id.status_txt,View.VISIBLE);
            holder.setText(R.id.status_txt,formatFileSize(item.getSpace()));

        }
    }

}
