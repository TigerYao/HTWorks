package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.huatu.Indicator.AVLoadingIndicatorView;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.VideoPlayBugDialog;
import com.huatu.utils.StringUtils;


/**
 * Created by xing
 * Date : 2015/12/28 0028.
 */
public class CommloadingView extends LinearLayout implements View.OnClickListener {


    private AnimationDrawable animationDrawable;
    private ImageView mimgHite;
    private TextView  mtextHite;
    private TextView  mTextTips;

    private View mImgLoading;

    private  OnClickListener mClickListener;
    private SpannableString mEmptyTipSpanString;

    private int normalRid= R.string.xs_none_related_organize;
    private int emptyRid= R.string.xs_none_related_organize;
    private int emptyImgRid=-1;
    private int emptyTipsRid=-1;

    //private int curStatus=0;//0 loading,1 network,2 empty
    public enum StatusMode {
        loading,
        network,
        serverError,
        empty,
        None
    }
    StatusMode curMode= StatusMode.None;


    public void setStatusStringId(@StringRes int nRid, @StringRes int eRid){
        normalRid=nRid;
        emptyRid=eRid;
        if(mtextHite!=null) mtextHite.setText(normalRid);
     }



    public void setEmptyImg(@DrawableRes int eRid){
        emptyImgRid=eRid;
    }

    public void setTipText(@StringRes int eRid){
        emptyTipsRid=eRid;
    }

    public void setTipText(SpannableString emptyTipSpan){
        mEmptyTipSpanString=emptyTipSpan;
    }


    public CommloadingView(Context context) {
        super(context);
        super.setOnClickListener(this);

    }

    public CommloadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
         if(curMode==StatusMode.network||curMode==StatusMode.serverError){
             if(mClickListener!=null){
                 showLoadingStatus();
                 mClickListener.onClick(v);
             }
          }
    }

    public  void  setOnRtyClickListener(OnClickListener onClickListener){
        mClickListener=onClickListener;
       // mTextTips.setOnClickListener(mClickListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImgLoading=   this.findViewById(R.id.xi_loading_img);
        mimgHite= (ImageView) this.findViewById(R.id.xi_hite_img);
        mtextHite = (TextView) this.findViewById(R.id.xi_hite_txt);

        mTextTips= (TextView) this.findViewById(R.id.xi_tv_tips);
        //curMode= StatusMode.loading;
         //showLoadingStatus();
    }
    public void start() {
        //animationDrawable = (AnimationDrawable) mimgLoading.getDrawable();
       // animationDrawable.start();
        if(mImgLoading instanceof AVLoadingIndicatorView) {
            ((AVLoadingIndicatorView)mImgLoading).start();
        }
    }

    public void stop() {
        if(mImgLoading instanceof AVLoadingIndicatorView) {
            ((AVLoadingIndicatorView)mImgLoading).stop();
        }

       // animationDrawable = (AnimationDrawable) mimgLoading.getDrawable();
       // animationDrawable.stop();
    }

    public void disableDetachedFromWindow(){
        if(mImgLoading instanceof AVLoadingIndicatorView) {
            ((AVLoadingIndicatorView)mImgLoading).setCanDetachedFromWindow(false);
        }
    }

    public void showLoadingStatus() {

        curMode= StatusMode.loading;
        show();
        mtextHite.setText(R.string.xs_loading_text);
        mTextTips.setText("");
        mimgHite.setVisibility(GONE);
        mImgLoading.setVisibility(VISIBLE);
        start();
    }

    public void showEmptyStatus(){
        curMode= StatusMode.empty;
        show();
        mtextHite.setText(emptyRid);
        if(null==mEmptyTipSpanString){
            mTextTips.setText(emptyTipsRid == -1 ? emptyRid : emptyTipsRid);
        }else {
            mTextTips.setText(mEmptyTipSpanString);
        }
        if(mTextTips.getVisibility()==View.GONE)
            mTextTips.setVisibility(VISIBLE);
        mimgHite.setVisibility(VISIBLE);
        mimgHite.setImageResource(emptyImgRid==-1 ?R.drawable.down_no_num:emptyImgRid);
        mImgLoading.setVisibility(GONE);
        stop();
    }

    public void showServerError(){

        curMode= StatusMode.network;
        show();
        mtextHite.setText(R.string.xs_networkdata_failed1);
        mTextTips.setText(R.string.xs_my_empty);
        mimgHite.setVisibility(VISIBLE);

        mimgHite.setImageResource(R.drawable.img_network_error);
//        mimgHite.setImageResource(R.drawable.wsj_7_kong);
        mImgLoading.setVisibility(GONE);
        stop();
    }

    public void showNetworkTip(){

        curMode= StatusMode.network;
        show();
        mtextHite.setText(R.string.xs_no_networkdata);
        mTextTips.setText(R.string.xs_my_empty);
        mimgHite.setVisibility(VISIBLE);

        mimgHite.setImageResource(R.drawable.icon_common_net_unconnected);
//        mimgHite.setImageResource(R.drawable.wsj_7_kong);
        mImgLoading.setVisibility(GONE);
        stop();
    }

    public boolean isShownStatus(StatusMode mode){

        return   this.isShown()&&mode==curMode;
    }

    public  void hide(){
        if(this.getVisibility()!=View.GONE)
            this.setVisibility(View.GONE);
    }

    public void removeFromParent(){
        if(null!=this.getParent()){
            mClickListener=null;
            ((ViewGroup) this.getParent()).removeView(this);
        }
    }

    public  void show(){
        if(this.getVisibility()==View.GONE)  this.setVisibility(View.VISIBLE);
     }


}
