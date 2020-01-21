package com.huatu.handheld_huatu.business.ztk_vod;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.SimpleResponseModel;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.helper.db.CollectInfoDao;
import com.huatu.handheld_huatu.helper.db.CourseWareCollectInfo;
import com.huatu.handheld_huatu.mvpmodel.CollectionBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.like.LikeButton;
import com.huatu.widget.like.OnLikeListener;

import rx.subscriptions.CompositeSubscription;

/**https://www.jianshu.com/p/3ecad4bfc55e  全屏Dialog
 * Created by cjx on 2018\7\19 0019.
 */

public class PopRightActionDialog extends Dialog implements View.OnClickListener {


    private Context mContext;
    private String mCourseId;
    private String mCourseImg;
    //private boolean mIsFirst=true;
    //private SparseArray<Integer> mLikeList=new SparseArray<>();//减少查询次数

    LikeButton mLikeButton;

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    public interface OnSubItemClickListener {
        void onShareBtnClick();
    }

    private OnSubItemClickListener onSubItemClickListener;

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    public PopRightActionDialog(Context context, String rid,String coverUrl) {
        super(context, R.style.ActionhorizontalDialogStyle);
        this.mContext = context;
        this.mCourseId = rid;
        this.mCourseImg=coverUrl;
    }

    boolean mCanLike=true;
    public void setDisableLike(){
        mCanLike=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.play_pop_action_layout, null);
        rootView.setMinimumHeight(DisplayUtil.getScreenWidth()+DensityUtils.getStatusHeight(mContext));

        setContentView(rootView);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        // dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =0;//DensityUtils.getStatusHeight(mContext);// 0;
        dialogWindow.setAttributes(lp);

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
        this.findViewById(R.id.share_action_txt).setOnClickListener(this);
        this.findViewById(R.id.suggest_action_txt).setOnClickListener(this);


        TextView  mTextLikeView=(TextView)this.findViewById(R.id.like_txt);
        mLikeButton=(LikeButton)this.findViewById(R.id.likeImage);

        if(!mCanLike){
            mLikeButton.setVisibility(View.GONE);
            mTextLikeView.setVisibility(View.GONE);
        }

        mLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                doCollection(likeButton,false);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                doCollection(likeButton,true);
            }
        });

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
               /* int cacheChange= PrefStore.getUserSettingInt(String.format("collect_type2_%s",mCourseId),0);
                if(cacheChange==1){
                    PrefStore.putUserSettingInt(String.format("collect_type2_%s",mCourseId),0);
                    if(!mIsFirst){//重建缓存
                        buildCache();
                    }else
                        mIsFirst=false;
                }*/
                 checkCollection(mLikeButton);
            }
        });
        //buildCache();
     }

    /* private void buildCache(){
        String cacheCollect= IoExUtils.getJsonString(String.format("collect_%s",mCourseId));
        mLikeList.clear();
        if(!TextUtils.isEmpty(cacheCollect)){

            String[] collectList=cacheCollect.split(",");
            for(int i=0;i<collectList.length;i++){

                int curValue=StringUtils.parseInt(collectList[i]);
                int specMode = View.MeasureSpec.getMode(curValue);//复用int高位状态
                int specSize = View.MeasureSpec.getSize(curValue);

                if(specMode==View.MeasureSpec.EXACTLY)
                      mLikeList.put(specSize,1);
                else
                      mLikeList.put(specSize,0);
            }
        }
    }

    private void saveCache() {
        StringBuffer sb=new StringBuffer();
        if (mLikeList.size() > 0) {
            for (int i = 0; i < mLikeList.size(); i++) {
                Integer valueAt = mLikeList.valueAt(i);
                int key = mLikeList.keyAt(i);
                int MakeSpec=View.MeasureSpec.makeMeasureSpec(key,valueAt.intValue()==1?View.MeasureSpec.EXACTLY:View.MeasureSpec.UNSPECIFIED);
                sb.append(String.valueOf(MakeSpec));
                if(i!=mLikeList.size()-1)
                    sb.append(",");
            }
        }
        IoExUtils.saveJsonFile(sb.toString(),String.format("collect_%s",mCourseId));
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_action_txt:
                this.dismiss();
                showShareDialog(v);
                break;
            case R.id.suggest_action_txt:
                FeedbackActivity.newInstance(mContext);
                this.dismiss();
                break;

        }
    }
    private void showShareDialog(View v){
         Activity contenxt= CommonUtils.getActivityFromView(v);
         if(contenxt!=null&&contenxt instanceof FragmentActivity){

            if(contenxt instanceof PlayStatistPresenter.View){
                 CourseWareInfo playingCourseWare= ((PlayStatistPresenter.View)contenxt).getPlayingCourseWare();
                if(null!=playingCourseWare){
                    ShareDialogFragment fragment=  ShareDialogFragment.getInstance(mCourseId,
                            playingCourseWare == null ? 0 : playingCourseWare.coursewareId ,
                            mCourseImg,
                            playingCourseWare == null ? "" : playingCourseWare.id,
                            playingCourseWare != null ? playingCourseWare.videoType : -1);

                    fragment.show(((FragmentActivity)contenxt).getSupportFragmentManager(), "share");
                    return;
                }
            }
            ShareDialogFragment fragment=  ShareDialogFragment.getInstance(mCourseId,mCourseImg);
            // fragment.setTargetFragment(MainFragment.this, REQUEST_CODE);
            fragment.show(((FragmentActivity)contenxt).getSupportFragmentManager(), "share");
        }
    }

    private void checkCollection(View v){
        if((null==v)&&v.getVisibility()==View.GONE){
             return;
        }

        CourseWareInfo curInfo = getActivityPlayCourseWare(v);
        if(null==curInfo){
            //ToastUtils.showShort("暂无课件，请稍后再试哦~ ");
            return;
        }
            if(curInfo!=null){

                final int curkeyId=  StringUtils.parseInt(curInfo.id);
                final int classId=curInfo.classId;
                CourseWareCollectInfo tmpCollectInfo= CollectInfoDao.getInstance().get(curInfo.id);
                if(null!=tmpCollectInfo){
                    int curVal = tmpCollectInfo.status;
                    if(null!=mLikeButton) mLikeButton.setLiked(curVal==1?true:false);
                    return;
                }

                ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().isCollection(curInfo.id)
                        , new NetObjResponse<CollectionBooleanBean>() {
                            @Override
                            public void onSuccess(BaseResponseModel<CollectionBooleanBean> model) {
                                 if(null!=mLikeButton) mLikeButton.setLiked(model.data.isCollection);
                                 CollectInfoDao.getInstance().create(curkeyId,model.data.isCollection?1:0,classId,StringUtils.parseInt(mCourseId));
                           }

                            @Override
                            public void onError(String message, int type) {    }
                        });
        }
     }

    private final CourseWareInfo getActivityPlayCourseWare(View v){

        Activity contenxt= CommonUtils.getActivityFromView(v);
        if(null!=contenxt&&(contenxt instanceof BJRecordPlayActivity)) {
            CourseWareInfo curInfo = ((BJRecordPlayActivity) contenxt).getPlayingCourseWare();
            if (curInfo != null) return curInfo;
        }
        if(null!=contenxt&&(contenxt instanceof LiveVideoForLiveActivity)) {
            CourseWareInfo curInfo = ((LiveVideoForLiveActivity) contenxt).getPlayingCourseWare();
            if (curInfo != null) return curInfo;
        }
        return null;
    }

    private void doCollection(View v,final boolean hasCollected){
        CourseWareInfo curInfo = getActivityPlayCourseWare(v);
        if(null==curInfo){
            ToastUtils.showShort("暂无课件，请稍后再试哦~ ");
            return;
        }
        if (curInfo != null) {

            final int curkeyId = StringUtils.parseInt(curInfo.id);
            rx.Observable<SimpleResponseModel> curObserable = hasCollected ? CourseApiService.getApi().unCollectCourseWare(curInfo.id)
                    : CourseApiService.getApi().collectCourseWare(curInfo.classId, curInfo.coursewareId, curInfo.id, curInfo.videoType);
            ServiceExProvider.visitSimple(getSubscription(), curObserable
                    , new NetObjResponse<String>() {
                        @Override
                        public void onSuccess(BaseResponseModel<String> model) {
                            ToastUtils.showShort(hasCollected ? "已移除收藏" : "成功收藏");

                            CourseWareCollectInfo tmpCollectInfo=new CourseWareCollectInfo();
                            tmpCollectInfo.id=-1;
                            tmpCollectInfo.status=hasCollected?0:1;
                            CollectInfoDao.getInstance().update(tmpCollectInfo,curkeyId);
                            dismiss();
                        }

                        @Override
                        public void onError(String message, int type) {
                            ToastUtils.showShort(hasCollected ? "取消收藏失败" : "收藏失败");
                            dismiss();
                        }
                    });
        }
    }

    @Override
    public void  dismiss(){
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        super.dismiss();
    }


}