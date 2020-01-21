package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.lessons.view.HorizontalListView;
import com.huatu.handheld_huatu.ui.LineTextView;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;

import java.util.ArrayList;

/**
 * Created by saiyuan on 2018/1/25.
 */
@Deprecated
public class ShoppingFragmentV2 extends BaseCourseListFragment implements View.OnClickListener {
   // private TopActionBar topActionBar;
    private HorizontalListView hListView;
    private CommonAdapter<CourseCategoryBean> listAdapter;
    private ImageView btnSetting;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_shopping_v3_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
    /*    topActionBar = (TopActionBar) rootView.findViewById(
                R.id.fragment_shopping_title_bar);*/
        hListView = (HorizontalListView) rootView.findViewById(
                R.id.shopping_category_list_view);
        btnSetting = (ImageView) rootView.findViewById(
                R.id.shopping_category_setting_btn);
        initAdapter();
        hListView.setAdapter(listAdapter);
        initTitleBar();
    }

    private void initAdapter() {
        listAdapter = new CommonAdapter<CourseCategoryBean> (
                selCategoryList, R.layout.item_shopping_category_layout) {
            @Override
            public void convert(ViewHolder holder, final CourseCategoryBean item, final int position) {
                holder.setText(R.id.item_shopping_category_name, item.name);

                LineTextView mTextView=  holder.getView(R.id.item_shopping_category_name);
                mTextView.setText(item.name);

                if(item.isSelected) {
                    holder.setTextColorRes(R.id.item_shopping_category_name, R.color.black);
                    mTextView.setSelected(true);

                } else {
                    holder.setTextColorRes(R.id.item_shopping_category_name, R.color.presale_tab_unselect);
                    mTextView.setSelected(false);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSelectCategory(position);
                        ShoppingFragmentV2.this.getData(true);
                    }
                });
            }
        };
    }

    @Override
    protected void onSelectCategory(int position) {
        super.onSelectCategory(position);
        listAdapter.setDataAndNotify(selCategoryList);
    }

    @Override
    protected void onInitListener() {
        btnSetting.setOnClickListener(this);
    }

    private void initTitleBar() {
       rootView.findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // MyPurchasedFragment.newInstance(1);

               // UIJumpHelper.jumpFragment(getContext(), MySingleTypeCourseFragment.class);
            }
        });
        rootView.findViewById(R.id.search_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("order_id", mOrderId);
                bundle.putInt("price_id", mPriceId);
                BaseFrgContainerActivity.newInstance(mActivity,
                        CourseSearchLiveFragment.class.getName(), bundle);

            }
        });
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        setCategoryListData();
        getData(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_category_setting_btn:
                CourseCategorySelectFragment.newInstance(mActivity, 10034);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!Method.isListEmpty(selCategoryList)) {
            setCategoryListData();
        } else {
            String value = SpUtils.getLiveCategoryList();
            if(!TextUtils.isEmpty(value)) {
                Gson gson = new Gson();
                try {
                    selCategoryList = gson.fromJson(value,
                            new TypeToken<ArrayList<CourseCategoryBean>>(){}.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ensureSelectedItem();
            setCategoryListData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10034 && resultCode == Activity.RESULT_OK) {
            onSetCategoryDataList();
            getData(true);
        }
    }

    @Override
    protected void onSetCategoryDataList() {
        super.onSetCategoryDataList();
        setCategoryListData();
    }

    private void setCategoryListData() {
        listAdapter.setDataAndNotify(selCategoryList);
    }

    public static ShoppingFragmentV2 newInstance() {
        return new ShoppingFragmentV2();
    }
}
