package com.huatu.handheld_huatu.business.ztk_vod;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.SimpleResponseModel;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.helper.db.CollectInfoDao;
import com.huatu.handheld_huatu.helper.db.CourseWareCollectInfo;
import com.huatu.handheld_huatu.mvpmodel.CollectionBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.popup.QuickListAction;
import com.huatu.utils.StringUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2019\4\4 0004.
 */
public class PopCollectListAction extends QuickListAction {

    private String mCourseId;
    private CompositeSubscription mCompositeSubscription = null;

    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void dismiss() {
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        super.dismiss();
    }

    public PopCollectListAction(Context context, int LayoutID, int ScrollerID) {
        super(context,LayoutID,ScrollerID);

    }

    TextView mLikeButton;
    private SparseArray<Integer> mLikeList=new SparseArray<>();//减少查询次数

    @Override
    public void setContentView(View root) {
        super.setContentView(root);
        mLikeButton=(TextView)root.findViewById(R.id.likeImage);
        mLikeButton.setTag(R.id.reuse_tag2,"0");
    }

    public void setDisableLike(){
        if(mLikeButton!=null)
            mLikeButton.setVisibility(View.GONE);
    }

    public void setCourseId(String courseId){
        this.mCourseId=courseId;
       // buildCache();
    }

    @Override
    public void show(View anchor) {
        checkCollection(true);
        super.show(anchor);
    }

    private void checkCollection(boolean isFrist){
       /* int cacheChange= PrefStore.getUserSettingInt(String.format("collect_type1_%s",mCourseId),0);
        if(cacheChange==1){
            PrefStore.putUserSettingInt(String.format("collect_type1_%s",mCourseId),0);
            if(!isFrist) buildCache();
        }*/
       if((null!=mLikeButton)&&mLikeButton.getVisibility()==View.VISIBLE){
           checkCollection(mLikeButton);
       }
    }

    @Override
    public void Reshow(View curAnchor){
        super.Reshow(curAnchor);
        checkCollection(false);
    }

    public void perfomClick(){
        if(mLikeButton!=null&&(null!=mLikeButton.getTag(R.id.reuse_tag2))){
            CourseWareInfo curInfo = getActivityPlayCourseWare(mLikeButton);
            if(null==curInfo){
                ToastUtils.showShort("暂无课件，请稍后再试哦~ ");
                return;
            }
            if(null!=curInfo){
                boolean oldFlag="1".equals(mLikeButton.getTag(R.id.reuse_tag2));

                mLikeButton.setTag(R.id.reuse_tag2,oldFlag?"0":"1");
                mLikeButton.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(oldFlag?R.drawable.heart_off:R.drawable.heart_on)
                        ,null,null,null);
                doCollection(curInfo,oldFlag);
            }
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

    private void checkCollection(View v) {
        if (null == v) return;
        CourseWareInfo curInfo = getActivityPlayCourseWare(v);
        if(null==curInfo){
            //ToastUtils.showShort("暂无课件，请稍后再试哦~ ");
            return;
        }
        if (curInfo != null) {
            final int curkeyId = StringUtils.parseInt(curInfo.id);
            final int classId=curInfo.classId;

            CourseWareCollectInfo tmpCollectInfo= CollectInfoDao.getInstance().get(curInfo.id);
       /*     if (null != mLikeList.get(curkeyId)) {
                Integer curVal = mLikeList.get(curkeyId);
                if (null != mLikeButton) {
                    mLikeButton.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(curVal == 1 ? R.drawable.heart_on : R.drawable.heart_off)
                            , null, null, null);
                    mLikeButton.setTag(R.id.reuse_tag2, curVal == 1 ? "1" : "0");
                }
                return;
            }*/

            if (null != tmpCollectInfo) {
                int curVal = tmpCollectInfo.status;
                if (null != mLikeButton) {
                    mLikeButton.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(curVal == 1 ? R.drawable.heart_on : R.drawable.heart_off)
                            , null, null, null);
                    mLikeButton.setTag(R.id.reuse_tag2, curVal == 1 ? "1" : "0");
                }
                return;
            }

            mLikeButton.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable( R.drawable.heart_off), null, null, null);
            mLikeButton.setTag(R.id.reuse_tag2, "0");
            ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().isCollection(curInfo.id)
                    , new NetObjResponse<CollectionBooleanBean>() {
                        @Override
                        public void onSuccess(BaseResponseModel<CollectionBooleanBean> model) {
                            if (null != mLikeButton) {
                                mLikeButton.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(model.data.isCollection ? R.drawable.heart_on : R.drawable.heart_off)
                                        , null, null, null);

                                mLikeButton.setTag(R.id.reuse_tag2, model.data.isCollection ? "1" : "0");
                            }
                           /* if (null != mLikeList) {
                                mLikeList.put(curkeyId, model.data.isCollection ? 1 : 0);
                            }*/
                            CollectInfoDao.getInstance().create(curkeyId,model.data.isCollection?1:0,classId,StringUtils.parseInt(mCourseId));
                            // PrefStore.putUserSettingInt(String.format("collect_type2_%s", mCourseId), 1);
                            // saveCache();
                        }

                        @Override
                        public void onError(String message, int type) {  }
                    });
        }
    }

    private void doCollection(CourseWareInfo curInfo, final boolean hasCollected) {

        if (curInfo != null) {

            final int curkeyId = StringUtils.parseInt(curInfo.id);
            rx.Observable<SimpleResponseModel> curObserable = hasCollected ? CourseApiService.getApi().unCollectCourseWare(curInfo.id)
                    : CourseApiService.getApi().collectCourseWare(curInfo.classId, curInfo.coursewareId, curInfo.id, curInfo.videoType);
            ServiceExProvider.visitSimple(getSubscription(), curObserable
                    , new NetObjResponse<String>() {
                        @Override
                        public void onSuccess(BaseResponseModel<String> model) {
                            ToastUtils.showShort(hasCollected ? "已移除收藏" : "成功收藏");
                            if (null != mLikeList) mLikeList.put(curkeyId, hasCollected ? 0 : 1);
                            //PrefStore.putUserSettingInt(String.format("collect_type2_%s", mCourseId), 1);
                           // CollectInfoDao.getInstance().create(curkeyId,hasCollected?1:0,classId,StringUtils.parseInt(mCourseId));
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


    /*private void buildCache(){
        String cacheCollect= IoExUtils.getJsonString(String.format("collect_%s",mCourseId));
        mLikeList.clear();
        if(!TextUtils.isEmpty(cacheCollect)){

            String[] collectList=cacheCollect.split(",");
            for(int i=0;i<collectList.length;i++){

                int curValue=StringUtils.parseInt(collectList[i]);
                int specMode = View.MeasureSpec.getMode(curValue);
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
}
