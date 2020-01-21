package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.CourseCategorySelectFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht-ldc on 2018/1/30.
 */
@Deprecated
public   class VodCourseCategorySelectFragment extends CourseCategorySelectFragment {
   /* @Override
    public List<CourseCategoryBean> getCacheList() {
        return SignUpTypeDataCache.getInstance().getVodCourseCategoryList();
    }

    @Override
    public void getCategoryListFromNet() {
        showProgressBar();
        ServiceProvider.getVodCourseCategoryList(compositeSubscription, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                dismissProgressBar();
                VodCourseCategorySelectFragment.this.onLoadDataFailed();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                dismissProgressBar();
                List<CourseCategoryBean> resultList = model.data;
                SignUpTypeDataCache.getInstance().setVodCourseCategoryList(resultList);
                processDataList(resultList);
            }
        });
    }

    @Override
    public void setCourseCategoryList(String ids) {
        showProgressBar();
        ServiceProvider.setVodCourseCategoryList(compositeSubscription, ids, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                dismissProgressBar();
                ToastUtils.showShort("设置考试类型失败");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                dismissProgressBar();
                SignUpTypeDataCache.getInstance().setVodCourseCategoryList(dataList);
                List<CourseCategoryBean> tmpList = SignUpTypeDataCache
                        .getInstance().getVodCourseCategoryList();
                processNetResult(tmpList);
            }
        });
    }

    @Override
    public void processNetResult(List<CourseCategoryBean> tmpList) {
        ArrayList<CourseCategoryBean> selCategoryList = new ArrayList<>();
        if(!Method.isListEmpty(tmpList)) {
            for(int i = 0; i < tmpList.size(); i++) {
                if(tmpList.get(i).cateId == SpUtils.getVodCourseExam()) {
                    tmpList.get(i).isSelected = true;
                } else {
                    tmpList.get(i).isSelected = false;
                }
                if(tmpList.get(i).checked) {
                    selCategoryList.add(tmpList.get(i));
                }
            }
        }
        if(!Method.isListEmpty(selCategoryList)) {
            Gson gson = new Gson();
            String value = "";
            try {
                value = gson.toJson(selCategoryList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveCategoryListValue(value);
        }
        setResultForTargetFrg(Activity.RESULT_OK, null);
    }

    @Override
    public void saveCategoryListValue(String value) {
        if(!TextUtils.isEmpty(value)) {
            SpUtils.setVodCourseCategoryList(value);

        }
    }

    public static void newInstance(Activity context, int requestCode) {
        Bundle args = new Bundle();
        BaseFrgContainerActivity.newInstance(context,
                VodCourseCategorySelectFragment.class.getName(), args, requestCode);
    }*/
}
