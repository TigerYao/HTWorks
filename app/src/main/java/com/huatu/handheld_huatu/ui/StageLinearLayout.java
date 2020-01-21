package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.CenterAlignImageSpan;

/**
 * Created by cjx on 2019\1\8 0008.
 */

public class StageLinearLayout  extends LinearLayout {

    TextView mCatalogstageTxt;
    TextView mCatalogNameTxt;
    TextView mTeacherNameTxt;
    private int mlevel=0;
    public boolean hasLevel(){
        return mlevel>0;
    }

    public StageLinearLayout(Context context) {
        super(context);
    }

    public StageLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mCatalogstageTxt= (TextView)getChildAt(0);
        mCatalogNameTxt=(TextView)getChildAt(1);
        mTeacherNameTxt=(TextView)getChildAt(2);
    }

    public void setStageLevel(int level){
       mlevel=level;
       if(level==1){
           mCatalogNameTxt.setVisibility(INVISIBLE);
           mCatalogNameTxt.setEnabled(false);
           mTeacherNameTxt.setVisibility(INVISIBLE);
           mTeacherNameTxt.setEnabled(false);

           mCatalogstageTxt.setVisibility(VISIBLE);
           mTeacherNameTxt=mCatalogstageTxt;
           mTeacherNameTxt.setTag("2");
           mTeacherNameTxt.setText("全部老师");
       }
       else if(level==2){
            mCatalogstageTxt.setVisibility(GONE);
            LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams)mCatalogNameTxt.getLayoutParams();
            lp.weight=2f;
            mCatalogNameTxt.setLayoutParams(lp);
            setCatalogName(mCatalogNameTxt.getText().toString());

       }else {
           mCatalogstageTxt.setVisibility(VISIBLE);
       }
    }

    public void setStageName(String stagename){
        if(mlevel!=1)
          mCatalogstageTxt.setText(stagename);
    }

    public void setCatalogName(String catalogName){

        if(mlevel==2){

            Rect rect = new Rect();
            mCatalogNameTxt.getPaint().getTextBounds(catalogName,0,catalogName.length(), rect);

            int distance= DensityUtils.dp2px(getContext(),28);
            if((rect.width()+distance)> DisplayUtil.getScreenWidth()*2/3){
               mCatalogNameTxt.setText(catalogName);
                mCatalogNameTxt.setCompoundDrawablesWithIntrinsicBounds(null,null,ResourceUtils.getDrawable(R.drawable.zf_icon_aa),null);

            }else {

                mCatalogNameTxt.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                String tmpStr=catalogName+"    [img]";
                SpannableStringBuilder builder = new SpannableStringBuilder(tmpStr);

                //得到drawable对象，即所要插入的图片
                Drawable d = ResourceUtils.getDrawable(R.drawable.zf_icon_aa);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

                //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                //用这个drawable对象代替字符串easy
                CenterAlignImageSpan span = new CenterAlignImageSpan(d);
                //包括0但是不包括"easy".length()即：4。[0,4)。值得注意的是当我们复制这个图片的时候，实际是复制了"easy"这个字符串。

                int Startindex2=tmpStr.indexOf("[img]");
                builder.setSpan(span, tmpStr.indexOf("[img]"), Startindex2+"[img]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                mCatalogNameTxt.setText(builder);
            }

        }else {

            mCatalogNameTxt.setText(catalogName);
        }

    }

    public void setTeacherName(String teacherName){
        mTeacherNameTxt.setText(teacherName);
    }
}
