package com.huatu.handheld_huatu.business.essay.examfragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsDialogFragment;
import com.huatu.handheld_huatu.base.adapter.PageRecycleAdapter;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.OnSelectListener;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableTextHelper;
import com.huatu.handheld_huatu.business.essay.cusview.drawimpl.CusAlignText;
import com.huatu.handheld_huatu.business.essay.cusview.drawimpl.CusDrawUnderLineEx;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.utils.ArrayUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019\6\24 0024.
 */

public class MaterialsCardDialogFragment extends AbsDialogFragment {

    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout llProvince;                                // 选择省布局
    private TextView tvProvince;                                    // 地区TextView
    private ImageView ivProvince;                                   // 选择省下箭头
    private TextView tvNoData;                                      // 无数据提示

    private String areaName = "";

    private List<ExamMaterialListBean> mCacheExamMaterialListBean;   // 传递进来的数据

    public void setCacheExamMaterialList(List<ExamMaterialListBean> listbean) {
        this.mCacheExamMaterialListBean = listbean;
    }

    private PagePreviewAdapter adapter;
    private int mCurrentPage = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_essay_dialogmaterials, null);
        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        mRootView = view;
        mRootView.findViewById(R.id.iv_hide_material).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //dialog.setOnShowListener(this);

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/

        onInitView();
        return dialog;
    }


    /**
     * 传递进来数据
     */
    public void setData(List<ExamMaterialListBean> cacheExamMaterialListBean) {
        this.mCacheExamMaterialListBean = cacheExamMaterialListBean;
    }

    protected void onInitView() {
        tabLayout = mRootView.findViewById(R.id.ex_materials_viewpager_tab);
        llProvince = mRootView.findViewById(R.id.ll_province);
        tvProvince = mRootView.findViewById(R.id.show_province_tv);
        ivProvince = mRootView.findViewById(R.id.show_province_iv);
        viewPager = mRootView.findViewById(R.id.ex_materials_viewpager);
        tvNoData = mRootView.findViewById(R.id.tv_no_data);
        tabLayout.setDividerColors(android.R.color.transparent);

        reFreshView();

        // refreshSingleAreaSelView();
        //tvProvince.setText(areaName);
    }

    private void reFreshView() {
        if (mCacheExamMaterialListBean == null || mCacheExamMaterialListBean.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }

        adapter = new PagePreviewAdapter(mCacheExamMaterialListBean);
        //  viewPager.setOffscreenPageLimit(ArrayUtils.size(mCacheExamMaterialListBean));
        // viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                if (offset == 0 && offsetPixels == 0) {
                    if (mCurrentPage != position) {
                        mCurrentPage = position;
                        if (null != adapter)
                            adapter.clearSelectView();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.setViewPager(viewPager);
    }

    public class PagePreviewAdapter extends PageRecycleAdapter {
        private List<ExamMaterialListBean> imageInfo;
       // private Context context;
        private View currentView;
        private SelectableTextHelper mLastSelectTextView;


        public void clearSelectView() {
            if (null != mLastSelectTextView) {
                mLastSelectTextView.clearView();
                mLastSelectTextView = null;
            }
        }

        public void removeAndRefresh(int pos) {
            imageInfo.remove(pos);
            this.notifyDataSetChanged();
        }

        @Nullable
        public CharSequence getPageTitle(int position) {
            return "资料" + (position + 1);
        }

        public PagePreviewAdapter( @NonNull List<ExamMaterialListBean> imageInfo) {
            super();
            this.imageInfo = imageInfo;
           // this.context = context;
        }

        @Override
        public int getCount() {
            return ArrayUtils.size(imageInfo);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentView = (View) object;
        }

        public View getPrimaryItem() {
            return currentView;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getRecycView(container, R.layout.ess_ex_materials_content_layout);
            // UniversalGlideHandler.display();
            ExerciseTextView essExMaterialsContent = view.findViewById(R.id.ess_ex_materials_content);
            String content = imageInfo.get(position).content;
            final SelectableTextHelper mSelectableTextHelper;
            CusAlignText cusAlignText;
            if (content != null) {
                ArrayList<String> uContentls = new ArrayList<>();           // 原本自带下划线的内容匹配到这里
                essExMaterialsContent.setType(1);
                EssayHelper.getString(content, uContentls);                                                     // 把 "<u>.+?</u>" 中的内容添加进 ArrayList 中
                essExMaterialsContent.setTextImgTagEssay(content);                                              // 是否包含图片
                essExMaterialsContent.setHtmlSource(DisplayUtil.dp2px(360 - 20), content);              // 如果包含图片，限制图片宽度
                {
                    mSelectableTextHelper = new SelectableTextHelper.Builder(essExMaterialsContent)             // 打开复制
                            .setSelectedColor(ContextCompat.getColor(getActivity(), R.color.essay_sel_color))
                            .setCursorHandleSizeInDp(10)
                            .setCursorHandleColor(ContextCompat.getColor(getActivity(), R.color.main_color))
                            .build();
                    mSelectableTextHelper.setSel(String.valueOf(imageInfo.get(position).id));
                }
                // 实现了 ExerciseTextView.CusDraw 接口，所以 ExerciseTextView 的绘制全交给这里
                cusAlignText = new CusAlignText(essExMaterialsContent);
                // 实现了 CusAlignText.DrawLine 划线接口，所以 CusAlignText 的下划线绘制会交给这里
                final CusDrawUnderLineEx cusDrawUnderLineEx = new CusDrawUnderLineEx(cusAlignText);

                cusAlignText.setFront(true);

                essExMaterialsContent.setmCusDraw(cusAlignText);

                mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                    @Override
                    public void onTextSelected(CharSequence content) {
                        if (null != mLastSelectTextView) {
                            if (mLastSelectTextView != mSelectableTextHelper) {
                                mLastSelectTextView.clearView();
                                mLastSelectTextView = null;
                                mLastSelectTextView = mSelectableTextHelper;
                            }
                        } else {
                            mLastSelectTextView = mSelectableTextHelper;
                        }
                        // 问题内容取消选择
                        //EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
                    }

                    @Override
                    public void updateView(int type) {
                        //Log.d(TAG, "updateView ==  " + type);
                      /*  if (type == 0) {
                            cusAlignText.setSelectableTextHelper(null);
                        } else if (type == 1) {
                            cusAlignText.setSelectableTextHelper(null);
                        } else {
                            cusAlignText.setSelectableTextHelper(mSelectableTextHelper.mSelectionInfo);
                        }
                        // mSelectableTextHelper.indexList是已划线的区域
                        // uContentls是<u> 标签，下划线区域
                        cusDrawUnderLineEx.setUnderLine(mSelectableTextHelper.indexList, uContentls);*/
                    }
                });
                // 把长按选择的 对象给 CusAlignText 重绘的时候，每次判断是否有选择然后绘制背景
                cusAlignText.setSelectableTextHelper(mSelectableTextHelper.mSelectionInfo);
                // 把下划线位置列表 indexList 和本身的下划线 uContentls 给CusDrawUnderLineEx 每次绘制的时候，进行绘制
                cusDrawUnderLineEx.setUnderLine(mSelectableTextHelper.indexList, uContentls);
            }

            //setTxtSize();

            container.addView(view);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    /**
     * 刷新显示的区域
     */
    private void refreshSingleAreaSelView() {
      /*  if (provinceChoiceListener == null) {
            return;
        }*/
      /*  llProvince.setOnClickListener(null);
        if (isSingle) {
            if (!isStartToCheckDetail) {
                if (isFromCollection) {
                    llProvince.setVisibility(View.VISIBLE);
                    tvProvince.setVisibility(View.VISIBLE);
                    tvProvince.setText(areaName);
                    ivProvince.setVisibility(View.GONE);
                } else {
                    llProvince.setVisibility(View.VISIBLE);
                    tvProvince.setVisibility(View.VISIBLE);
                    ivProvince.setVisibility(View.VISIBLE);
                    llProvince.setOnClickListener(provinceChoiceListener);
                }
            } else {
                llProvince.setVisibility(View.VISIBLE);
                tvProvince.setVisibility(View.VISIBLE);
                tvProvince.setText(areaName);

                ivProvince.setVisibility(View.GONE);
            }
        } else {
            llProvince.setVisibility(View.GONE);
            tvProvince.setVisibility(View.GONE);
            ivProvince.setVisibility(View.GONE);

        }*/
    }


}
