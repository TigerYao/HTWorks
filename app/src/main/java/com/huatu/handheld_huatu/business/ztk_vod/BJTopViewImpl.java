package com.huatu.handheld_huatu.business.ztk_vod;

import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.playerview.IPlayerTopContact;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.DanmuBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2018\7\18 0018.
 */

public class BJTopViewImpl  implements IPlayerTopContact.TopView {
    private IPlayerTopContact.IPlayer mPlayer;
    private final ImageView rl_back;
    private final TextView textView_titleBar_info;

    private final ImageView iv_titlebt;
    PopRightActionDialog mActionDialog;
    View.OnClickListener mPortraitPopListener;
    private String mCourseId;
    private String mCourseImg;
    private boolean mFromLocal;
   // private int mClassId, mCourseWareId,mSyllabusId;

    DanmakuPresenter  mDanmakuViewPresenter;
    private int mOrientation= BJPlayerView.VIDEO_ORIENTATION_PORTRAIT;

    public void setPortraitPopListener(View.OnClickListener viewClickListener){
      this.mPortraitPopListener=viewClickListener;
    }

    private void showRightAction(View v){
        if (mActionDialog == null) {
            mActionDialog = new PopRightActionDialog(v.getContext(), mCourseId,mCourseImg);
            if(mFromLocal){
                mActionDialog.setDisableLike();
            }
            //addressDialog.setOnSubItemClickListener(this);
        }
        mActionDialog.show();
    }
   //@Query("classId") int courseType, @Query("lessonId") int lessonId,@Query("syllabusId") String syllabusId,@Query("videoType") int videoType
    public void setCourseId(String courseId,String courseImg,boolean isFromLocal){
        this.mCourseId=courseId;
        this.mCourseImg=courseImg;
        this.mFromLocal=isFromLocal;
    }


    View mDanmuBtn;
    public void hideDanmu(){
        if(null!=mDanmuBtn){
            mDanmuBtn.setVisibility(View.GONE);
        }
    }

    public BJTopViewImpl(View topView, FrameLayout danmuContainer,boolean canDanmu) {
         if(canDanmu){
            mDanmakuViewPresenter=new DanmakuPresenter(danmuContainer);
             mDanmuBtn= topView.findViewById(R.id.danmu_switch_btn);
             mDanmuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean oldhasSHow="1".equals(v.getTag().toString());

                    LogUtils.e("onClick",oldhasSHow+"");
                    if(mDanmakuViewPresenter!=null){
                        mDanmakuViewPresenter.isShow(!oldhasSHow);
                    }
                    v.setTag(oldhasSHow?"0":"1");
                    v.setSelected(oldhasSHow?true:false);

                }
            });
         }else {
            topView.findViewById(R.id.danmu_switch_btn).setVisibility(View.GONE);
        }

        rl_back = (ImageView) topView.findViewById(R.id.rl_back);
        textView_titleBar_info = (TextView) topView.findViewById(R.id.textView_TitleBar_Info);
        iv_titlebt = (ImageView) topView.findViewById(R.id.iv_titlebt);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
                if (mPlayer != null) {
                    mPlayer.onBackPressed();
                }
            }
        });
        iv_titlebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if((mOrientation==BJPlayerView.VIDEO_ORIENTATION_PORTRAIT)){
                    if(mPortraitPopListener!=null)
                        mPortraitPopListener.onClick(view);
               }
                else
                   showRightAction(view);
             }
        });

    }

    @Override
    public void onBind(IPlayerTopContact.IPlayer iPlayer) {
        mPlayer = iPlayer;
        setOrientation(mPlayer.getOrientation());

    }

    @Override
    public void setTitle(String title) {
        textView_titleBar_info.setText(title);
       /* if (isPlay) {
            if (!TextUtils.isEmpty(bjyTitle))
                textView_titleBar_info.setText(bjyTitle);
        } else {
            if (!Method.isListEmpty(livelession) && !TextUtils.isEmpty(livelession.get(currenttTitlePosition).getTitle())) {
                textView_titleBar_info.setText(livelession.get(currenttTitlePosition).getTitle());
            }
        }
        Log.e("shunxu", bjyTitle + "```" + title);*/
    }

    @Override
    public void setOrientation(int orientation) {

        int oldOrientation=mOrientation;
        mOrientation=orientation;
        if (orientation == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE) {
            //iv_titlebt.setVisibility(View.GONE);
             textView_titleBar_info.setVisibility(View.VISIBLE);
//             iv_titlebt.setRotation(0);
        } else {
            //iv_titlebt.setVisibility(View.VISIBLE);
           // listview_ml.setVisibility(View.GONE);
//            iv_titlebt.setRotation(90);
            textView_titleBar_info.setVisibility(View.INVISIBLE);
        }
      /*  if(oldOrientation!=orientation){
            if((mDanmakuViewPresenter!=null)&&(mDanmakuViewPresenter.mDanmakuView!=null)){
                LogUtils.e("setOrientation",orientation+"");
                mDanmakuViewPresenter.mDanmakuView.getConfig()
                                     .setDanmakuMargin(orientation== BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE?5:10);
             }
        } */
    }

    @Override
    public void setOnBackClickListener(final View.OnClickListener listener) {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!mFromLocal)&&(!CommonUtils.isPad(v.getContext()))&&(mPlayer != null)) {
                    if (!mPlayer.onBackPressed()) {
                        listener.onClick(v);
                    }
                } else {
                    listener.onClick(v);
                }
            }
        });
    }

    public void sendDanma(int textColor,String message){
        if(mDanmakuViewPresenter!=null)
            mDanmakuViewPresenter.addDanmaku(message,false,textColor);
    }

    public void start(long ms,boolean isSeek){
        if(mDanmakuViewPresenter!=null)
            mDanmakuViewPresenter.start(ms,isSeek);


    }

    public void resume(){
        if (mDanmakuViewPresenter != null ) {
            mDanmakuViewPresenter.resume();
        }
    }

    public void pause(){
        if (mDanmakuViewPresenter != null ) {
            mDanmakuViewPresenter.pause();
        }
    }

    public void destory(){
        if (mDanmakuViewPresenter != null ) {
            mDanmakuViewPresenter.destory();
            mDanmakuViewPresenter=null;
        }
    }

    public void loadInitDanMu(String courseId, CourseWareInfo selCourseWare, CompositeSubscription comSubScrip){

        if(selCourseWare==null){
            if(mDanmakuViewPresenter!=null) mDanmakuViewPresenter.startDanmaku("");
            return;
        }
        ServiceExProvider.visit(comSubScrip, CourseApiService.getApi().getDanmuList(selCourseWare.classId, selCourseWare.coursewareId, 1, 500,0),
                new NetObjResponse<DanmuBean>() {

                    @Override
                    public void onError(String message, int type) {
                      //  ToastUtils.showShort("fail");
                        if(mDanmakuViewPresenter!=null) mDanmakuViewPresenter.startDanmaku("");
                    }

                    @Override
                    public void onSuccess(BaseResponseModel<DanmuBean> model) {

                        //ToastUtils.showShort("ok");
                        //[{"c":"19.408,16777215,1,25,196050,1364468342","m":"。。。。。。。。。"}

                        if(mDanmakuViewPresenter!=null) mDanmakuViewPresenter.startDanmaku(GsonUtil.toJsonStr(model.data.data));

                    }
                });




       // ServiceExProvider.visit(comSubScrip,);

    }
}