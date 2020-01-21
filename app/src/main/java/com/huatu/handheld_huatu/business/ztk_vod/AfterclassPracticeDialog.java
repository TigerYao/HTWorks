package com.huatu.handheld_huatu.business.ztk_vod;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

/**
 * Created by cjx on 2018\8\10 0010.
 */

public class AfterclassPracticeDialog implements View.OnClickListener {


    public interface onAfterPracticeListener{
         void onAfterViewClick(boolean isStart);//
    }

    onAfterPracticeListener mOnAfterPracticeListener;
    TextView mTxtColorView;
    private int mDirection= Configuration.ORIENTATION_PORTRAIT;

    private Context mContext;
    private long mClassId,mlessionId;
    private int mExamNum=0;
    private TextView mTipView;

    protected QMUIDialog mDialog;
    private FrameLayout mContentLayout;

    public AfterclassPracticeDialog(Context context,onAfterPracticeListener afterPracticeListener) {

        this.mContext = context;
        mOnAfterPracticeListener=afterPracticeListener;
    }
    private FrameLayout mRootView;

    public QMUIDialog create(@StyleRes int style,int direction,int examNUm) {
        mDirection=direction;
        mExamNum=examNUm;
        mDialog = new QMUIDialog(mContext, style,true);
        if(mDirection==Configuration.ORIENTATION_PORTRAIT)
            mDialog.setCanFullScreen(false);

        Context dialogContext = mDialog.getContext();

        mRootView = (FrameLayout) LayoutInflater.from(dialogContext).inflate(
                R.layout.play_afterclass_hortip_layout, null);

        mTipView=(TextView) mRootView.findViewById(R.id.tip_txt);
        mRootView.findViewById(R.id.close_btn).setOnClickListener(this);
        mRootView.findViewById(R.id.show_next_btn).setOnClickListener(this);
        mRootView.findViewById(R.id.start_btn).setOnClickListener(this);
        mContentLayout=(FrameLayout)mRootView.findViewById(R.id.content_layout);



        mDialog.addContentView(mRootView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
      //  onAfter(mDialog, mRootView, dialogContext);
        String as_detail ="接下来做"+ StringUtils.fontColor("#EC74A0", mExamNum)+"道练习题<br/> 巩固下课堂学习的知识" ;
         mTipView.setText(StringUtils.forHtml(as_detail));
        if(mDirection==Configuration.ORIENTATION_PORTRAIT){
            FrameLayout.LayoutParams tmpParams=(FrameLayout.LayoutParams) mContentLayout.getLayoutParams();
            tmpParams.width=DensityUtils.dp2px(mContext,280);
            mContentLayout.setLayoutParams(tmpParams);
        }
        return mDialog;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.close_btn:
                if(mDialog!=null) mDialog.dismiss();
                break;
            case  R.id.start_btn:
                if(mDialog!=null) mDialog.dismiss();
                if(mOnAfterPracticeListener!=null)
                    mOnAfterPracticeListener.onAfterViewClick(true);
                 break;
            case R.id.show_next_btn:
                if(mDialog!=null) mDialog.dismiss();
                if(mOnAfterPracticeListener!=null)
                    mOnAfterPracticeListener.onAfterViewClick(false);
                 break;
         }
    }

    public void destory(){
        if(mDialog!=null&&mDialog.isShowing())
            mDialog.dismiss();
        this.mOnAfterPracticeListener=null;
        this.mContext=null;

    }


    private void setTranslucentStatus() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = mDialog.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//4.4 全透明状态栏
            mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    public void show(int direction,int examNum){
        if(mDialog!=null) {
            mExamNum=examNum;
            String as_detail ="接下来做"+ StringUtils.fontColor("#EC74A0", mExamNum)+"道练习题<br/> 巩固下课堂学习的知识" ;
            mTipView.setText(StringUtils.forHtml(as_detail));
            if(direction!=mDirection){
                mDirection= direction;
                if(mDirection==Configuration.ORIENTATION_PORTRAIT){
                    FrameLayout.LayoutParams tmpParams=(FrameLayout.LayoutParams) mContentLayout.getLayoutParams();
                    tmpParams.width=DensityUtils.dp2px(mContext,280);
                    mContentLayout.setLayoutParams(tmpParams);
                    mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setTranslucentStatus();
                  }else {
                    FrameLayout.LayoutParams tmpParams=(FrameLayout.LayoutParams) mContentLayout.getLayoutParams();
                    tmpParams.width=DensityUtils.dp2px(mContext,398);
                    mContentLayout.setLayoutParams(tmpParams);

                    mDialog.getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                  }
            }
            mDialog.show();
         }
    }

}
