package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2018/1/25.
 */

public class CourseCategorySelectFragment extends BaseListFragment {
    @Override
    protected void onInitView() {
        super.onInitView();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void initAdapter() {
        mAdapter= new CommonAdapter<CourseCategoryBean>(
                dataList, R.layout.course_category_select_item_layout) {
            @Override
            public void convert(ViewHolder holder, final CourseCategoryBean item, final int position) {
                holder.setText(R.id.course_category_select_item_name, item.name);
                if(item.checked) {
                    holder.setViewImageResource(R.id.course_category_select_item_iv,
                            R.drawable.icon_slip_on);
                } else {
                    holder.setViewImageResource(R.id.course_category_select_item_iv,
                            R.drawable.icon_slip_off);
                }
                holder.setViewOnClickListener(R.id.course_category_select_item_iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.checked){
                            StudyCourseStatistic.clickStatistic("课程","页面第二模块右侧",item.name);
                        }
                        item.checked = !item.checked;
                        mAdapter.setDataAndNotify(dataList);
                        setConfirmBtnState();
                    }
                });
            }
        };
    }

    private void setConfirmBtnState() {
        boolean isSelect = false;
        for(int i = 0; i < dataList.size(); i++) {
            if(((CourseCategoryBean)dataList.get(i)).checked) {
                isSelect = true;
                break;
            }
        }
        if(isSelect) {
            topActionBar.showButtonText("确定",
                    TopActionBar.RIGHT_AREA, R.color.main_color);
            topActionBar.setButtonEnable(TopActionBar.RIGHT_AREA, true);
        } else {
            topActionBar.showButtonText("确定",
                    TopActionBar.RIGHT_AREA, R.color.gray_999999);
            topActionBar.setButtonEnable(TopActionBar.RIGHT_AREA, false);
        }
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
        topActionBar.setTitle("考试类型设置");
        topActionBar.showButtonText("取消", TopActionBar.LEFT_AREA);
        topActionBar.showButtonText("确定", TopActionBar.RIGHT_AREA, R.color.main_color);
        setConfirmBtnState();
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.onBackPressed();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                onConfirm();
            }
        });
    }

    @Override
    public void showProgressBar() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.showProgress();
            }
        });
    }

    @Override
    public void dismissProgressBar() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.hideProgress();
            }
        });
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onRefresh() {
        onLoadData();
    }

    @Override
    public void onLoadMore() {

    }

    public List<CourseCategoryBean> getCacheList() {
        String value = SpUtils.getLiveCategoryList();
        if (!TextUtils.isEmpty(value)) {
            List<CourseCategoryBean> allType = GsonUtil.jsonToList(value, new GsonUtil.TypeToken<List<CourseCategoryBean>>() {
            }.getType());
            if(!ArrayUtils.isEmpty(allType)){
               return allType;
            }
        }
        return SignUpTypeDataCache.getInstance().getCourseCategoryList();
    }

    @Override
    protected void onLoadData() {
        List<CourseCategoryBean> resultList = getCacheList();
        if(!Method.isListEmpty(resultList)) {
            processDataList(resultList);
        } else {
            if(!NetUtil.isConnected()) {
                CourseCategorySelectFragment.this.onLoadDataFailed();
                return;
            }
            getCategoryListFromNet();
        }
    }

    public void getCategoryListFromNet() {
        showProgressBar();
        ServiceProvider.getCourseCategoryList(compositeSubscription, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                dismissProgressBar();
                CourseCategorySelectFragment.this.onLoadDataFailed();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                dismissProgressBar();
                List<CourseCategoryBean> resultList = model.data;
                SignUpTypeDataCache.getInstance().setCourseCategoryList(resultList);
                processDataList(resultList);
            }
        });
    }

    public void processDataList(List<CourseCategoryBean> tmpList) {
        if(Method.isListEmpty(tmpList)) {
           return;
        }
        CourseCategorySelectFragment.this.onSuccess(tmpList, true);
        listView.setPullRefreshEnable(true);
        setConfirmBtnState();
    }

    private void onConfirm() {
        SpUtils.setSelectedLiveCategory(0);
        if(!NetUtil.isConnected()) {
            ToastUtils.showShort("网络错误，请检查网络设置");
            return;
        }
        String ids = "";
        for(int i = 0; i < dataList.size(); i++) {
            CourseCategoryBean bean = (CourseCategoryBean) dataList.get(i);
            if(bean.checked) {
                ids += bean.cateId + ",";
            }
        }
        if(TextUtils.isEmpty(ids)) {
            ToastUtils.showShort("请选择考试科目");
            return;
        } else {
            ids = ids.substring(0, ids.length() - 1);
        }
        setCourseCategoryList(ids);
    }

    public void setCourseCategoryList(String ids) {
       // 不上报

      /* showProgressBar();
        ServiceProvider.setCourseCategoryList(compositeSubscription, ids, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                dismissProgressBar();
                ToastUtils.showShort("设置考试类型失败");
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                dismissProgressBar();
                SignUpTypeDataCache.getInstance().setCourseCategoryList(dataList);
                List<CourseCategoryBean> tmpList = SignUpTypeDataCache
                        .getInstance().getCourseCategoryList();
                processNetResult(tmpList);
            }

        });*/

        SignUpTypeDataCache.getInstance().setCourseCategoryList(dataList);
        List<CourseCategoryBean> tmpList = SignUpTypeDataCache
                .getInstance().getCourseCategoryList();
        processNetResult(tmpList);
    }

    public void processNetResult(List<CourseCategoryBean> tmpList) {
        ArrayList<CourseCategoryBean> selCategoryList = new ArrayList<>();
        if(!Method.isListEmpty(tmpList)) {
            for(int i = 0; i < tmpList.size(); i++) {
                if(tmpList.get(i).cateId == SpUtils.getSelectedLiveCategory()) {
                    tmpList.get(i).isSelected = true;
                } else {
                    tmpList.get(i).isSelected = false;
                }
                /*if(tmpList.get(i).checked)*/
                {
                    selCategoryList.add(tmpList.get(i));
                }
            }
        }
        if(!Method.isListEmpty(selCategoryList)) {

            String value = "";
            try {
                value = GsonUtil.GsonString(selCategoryList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveCategoryListValue(value);
        }
        setResultForTargetFrg(Activity.RESULT_OK, null);
    }

    public void saveCategoryListValue(String value) {
        if(!TextUtils.isEmpty(value)) {
            SpUtils.setLiveCategoryList(value);
        }
    }

    public static void newInstance(Activity context, int requestCode) {
        Bundle args = new Bundle();
        BaseFrgContainerActivity.newInstance(context,
                CourseCategorySelectFragment.class.getName(), args, requestCode);
    }
}
