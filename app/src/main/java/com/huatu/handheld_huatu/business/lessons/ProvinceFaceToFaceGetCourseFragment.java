package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.bean.FaceToFaceCourseBean;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

/**
 * Created by saiyuan on 2018/3/28.
 */

public class ProvinceFaceToFaceGetCourseFragment extends BaseListFragment {
    private View footerView;
    private TextView btnCourse;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_face_to_face_get_course_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        layoutErrorView.setErrorText("无课程");
        listView.setPullRefreshEnable(false);
        listView.setSlideEnable(false);
        footerView = mLayoutInflater.inflate(R.layout.face_to_face_get_free_course_btn_layout, null);
        btnCourse = (TextView) footerView.findViewById(R.id.face_to_face_free_course_get_btn);
        btnCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConfirm();
            }
        });
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<FaceToFaceCourseBean>(dataList, R.layout.face_to_face_free_course_item) {
            @Override
            public void convert(ViewHolder holder, FaceToFaceCourseBean item, int position) {
                holder.setText(R.id.face_to_face_course_title_tv, item.Title);
                holder.setText(R.id.face_to_face_course_price_tv, "0元");
                TextView tvPrice = holder.getView(R.id.face_to_face_course_origin_price_tv);
                tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                tvPrice.setText("¥" + item.Price);
                ImageView imgView = holder.getView(R.id.face_to_face_course_img);
               /* Glide.with(mContext).load(item.scaleimg).diskCacheStrategy(DiskCacheStrategy.RESULT).
                        placeholder(R.mipmap.load_default).into(imgView);*/

                ImageLoad.displaynoCacheImage(mContext,R.mipmap.load_default,item.scaleimg,imgView);
            }
        };
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_CANCELED);
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_OK);
            }
        });
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        mActivity.showProgress();
        ServiceProvider.getFaceToFaceFreeCourseList(compositeSubscription, new NetResponse(){
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mActivity.hideProgress();
                showCourseBtn(!Method.isListEmpty(model.data));
                ProvinceFaceToFaceGetCourseFragment.this.onSuccess(model.data, true);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                ProvinceFaceToFaceGetCourseFragment.this.onLoadDataFailed();
                showCourseBtn(false);
            }
        });
    }

    private void showCourseBtn(boolean isShow) {
        if(isShow) {
            listView.addFooterView(footerView);
        } else {
            listView.removeFooterView(footerView);
        }
    }

    private void onClickConfirm() {
        mActivity.showProgress();
        ServiceProvider.getFaceToFaceFreeCourse(compositeSubscription, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                ToastUtils.showShort("领取成功");
               // MyPurchasedFragment.newInstance(0);
               // UIJumpHelper.jumpFragment(getContext(),MySingleTypeCourseFragment.class);
                UIJumpHelper.startStudyPage(getContext());
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                ToastUtils.showShort("领取失败");
            }
        });
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void dismissProgressBar() {

    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
