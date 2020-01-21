package com.huatu.handheld_huatu.business.ztk_vod;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseHandoutAdapter;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.adapter.MyCustomFragmentPagerAdapter;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseHandoutFragment;
import com.huatu.handheld_huatu.mvpmodel.CourseHandoutListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PointExercisesBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.transformer.ZoomOutSlideTransformer;
import com.huatu.utils.ArrayUtils;
import com.huatu.widget.MaterialLoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cjx on 2018\7\24 0024.
 * 随堂练习
 */

public class InclassPracticeFragment extends AbsSettingFragment{

    @BindView(R.id.vp)
    ViewPager mViewPager;

    @BindView(R.id.loadingBar)
    ProgressBar mProgressBar;

    @BindView(R.id.cancel_action_btn)
    TextView mCancleBtn;


    @BindView(R.id.top_tip_txt)
    TextView mTopTipTxt;

    @BindView(R.id.submit_btn)
    TextView mSubmitBtn;


    private long mPointId;
    private String mParcaticeId;
    final ArrayList<Fragment> mFragmentList = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.play_inclass_practice_layout;
    }

    public static InclassPracticeFragment getInstance(long pointId,String practiceId) {
        Bundle args = new Bundle();
        args.putLong(ArgConstant.KEY_ID, pointId);
        args.putString(ArgConstant.FROM_ACTION,practiceId);
        InclassPracticeFragment tmpFragment = new InclassPracticeFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    @Override
    protected void parserParams(Bundle args) {
        mPointId = args.getLong(ArgConstant.KEY_ID);
        mParcaticeId= args.getString(ArgConstant.FROM_ACTION);
    }

    private List<PointExercisesBean> mPointExercises;

    @Override
    public void requestData(){

        if(!TextUtils.isEmpty(mParcaticeId)&&mParcaticeId.contains("_")){
            try{
                String[] tmpStr=mParcaticeId.split("_");
               // mParcaticeId=tmpStr[0];
                if(!TextUtils.isEmpty(tmpStr[1])){
                    mCancleBtn.setText("关闭");
                    mTopTipTxt.setVisibility(View.INVISIBLE);
                }
             }catch (Exception e){
                e.printStackTrace();
            }
        }
        mProgressBar.setVisibility(View.VISIBLE);
        showContent();
        ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getLearnPointInfo(mPointId),new NetListResponse<PointExercisesBean>(){

            @Override
            public void onError(String message, int type){
                if(mProgressBar!=null){
                    mProgressBar.setVisibility(View.GONE);
                }
                ToastUtils.showShort("数据加载出错");
            }

            @Override
            public void onSuccess(BaseListResponseModel<PointExercisesBean> model){
                if(mProgressBar!=null){
                    mProgressBar.setVisibility(View.GONE);
                }
                mPointExercises=model.data;
                if(ArrayUtils.isEmpty(mFragmentList)||ArrayUtils.isEmpty(mPointExercises)) return;
                Fragment tmpFragment=  mFragmentList.get(0) ;

                if(tmpFragment.isAdded()&&(!tmpFragment.isDetached())&&(tmpFragment instanceof InclassSingleFragment)) {
                   ((InclassSingleFragment)tmpFragment).setCurrentExercise(mPointExercises.get(0));
                }
                //showContent();
            }
        });
    }

    private void showContent(){

            List<String> tmpList=new ArrayList<>();
            tmpList.add(String.valueOf(mPointId));
           /* for(PointExercisesBean bean:mPointExercises){
                tmpList.add(String.valueOf(bean.id));
                mFragmentList.add(InclassSingleFragment.getInstance(mPointId+"",mParcaticeId));
                break;//只作添加单选
            }
           */
            mFragmentList.add(InclassSingleFragment.getInstance(mPointId+"",mParcaticeId));
          //  fragmentList.add(CourseCatalogFragment.getInstance(classid));

            mViewPager.setAdapter(new MyCustomFragmentPagerAdapter(getChildFragmentManager(), mFragmentList, tmpList));
            // mScrollLayout.getHelper().setCurrentScrollableContainer((ScrollableHelper.ScrollableContainer) fragmentList.get(0));
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
            // mViewPager.setCurrentItem(0);

    }


    @OnClick({R.id.cancel_action_btn,R.id.submit_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_action_btn:
                if(!Method.isActivityFinished(getActivity())){
                    ((BJRecordPlayActivity)getActivity()).showInclassFragment(false,0,"");
                }

                break;
            case R.id.submit_btn:
                Fragment curFragment=  mFragmentList.get(0);
                if(curFragment.isAdded()&&(curFragment instanceof InclassSingleFragment)){
                     ((InclassSingleFragment)curFragment).showAnswer();
                    AnimUtils.animBottomHide(mSubmitBtn,false);
                }
                break;
        }
    }

    public void hideJump(){
        if(mCancleBtn!=null) mCancleBtn.setVisibility(View.GONE);
    }

    public void showSubmitAction(){
        if(mSubmitBtn.getVisibility()==View.GONE)
            AnimUtils.animBottomShow(mSubmitBtn);
    }
}


