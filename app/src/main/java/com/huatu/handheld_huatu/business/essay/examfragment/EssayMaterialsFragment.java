package com.huatu.handheld_huatu.business.essay.examfragment;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.utils.StringUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 材料Fragment，为了可以复用Fragment；
 */
public class EssayMaterialsFragment extends BaseFragment {

    private static int currentCommentPosition = 0;

    private long showMaterialId;                                     // 搜索跳过来需要默认显示第几个材料

    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout llProvince;                                // 选择省布局
    private TextView tvProvince;                                    // 地区TextView
    private ImageView ivProvince;                                   // 选择省下箭头
    private TextView tvNoData;                                      // 无数据提示

    private String areaName = "";
    private boolean isSingle;
    private boolean isStartToCheckDetail;
    private boolean isFromCollection;

    private boolean isSetData = false;                              // 是否传进来数据了

    private List<ExamMaterialListBean> cacheExamMaterialListBean;   // 传递进来的数据

    private ProvinceChoiceListener provinceChoiceListener;            // 改变区域的监听回调

    private int currentPosition = 0;
    private FragmentPagerItemAdapter adapter;
    private int requestType;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_essay_materials;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_change_material) {
            if (currentPosition != currentCommentPosition) {
                currentPosition = currentCommentPosition;
                if (adapter != null && adapter.getCount() > currentPosition) {
                    viewPager.setCurrentItem(currentPosition);
                }
            }
        }
        return true;
    }

    @Override
    protected void onInitView() {

        requestType = args.getInt("request_type");

        tabLayout = rootView.findViewById(R.id.ex_materials_viewpager_tab);
        llProvince = rootView.findViewById(R.id.ll_province);
        tvProvince = rootView.findViewById(R.id.show_province_tv);
        ivProvince = rootView.findViewById(R.id.show_province_iv);
        viewPager = rootView.findViewById(R.id.ex_materials_viewpager);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        tabLayout.setDividerColors(android.R.color.white);

        if (isSetData) {
            reFreshView();
        }
        refreshSingleAreaSelView();
        tvProvince.setText(areaName);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentCommentPosition = position;
                currentPosition = position;
                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_change_material));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 传递进来数据
     */
    public void setData(List<ExamMaterialListBean> cacheExamMaterialListBean) {
        this.cacheExamMaterialListBean = cacheExamMaterialListBean;
        this.isSetData = true;
        if (isViewCreated) {
            reFreshView();
        }
    }

    /**
     * 传递进来数据
     */
    public void setData(List<ExamMaterialListBean> cacheExamMaterialListBean, long showMaterialId) {
        this.showMaterialId = showMaterialId;
        setData(cacheExamMaterialListBean);
    }

    // 选择省的点击监听
    public void setProvinceChoiceListener(ProvinceChoiceListener provinceChoiceListener) {
        this.provinceChoiceListener = provinceChoiceListener;
    }

    // 地区信息
    public void setSingleArea(String areaName, boolean isSingle, boolean isStartToCheckDetail, boolean isFromCollection) {
        this.areaName = areaName;
        this.isSingle = isSingle;
        this.isStartToCheckDetail = isStartToCheckDetail;
        this.isFromCollection = isFromCollection;
        if (isViewCreated) {
            refreshSingleAreaSelView();
        }
    }

    /**
     * 显示当前区域
     */
    public void setAreaTitle(String areaName) {
        this.areaName = areaName;
        if (isViewCreated) {
            tvProvince.setText(areaName);
        }
    }

    private void reFreshView() {
        if (cacheExamMaterialListBean == null || cacheExamMaterialListBean.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
            return;
        } else {
            tvNoData.setVisibility(View.GONE);
        }

        FragmentPagerItems pages = new FragmentPagerItems(mActivity);

        int index = 0;
        for (ExamMaterialListBean var : cacheExamMaterialListBean) {
            index++;
            Bundle arg = new Bundle();
            arg.putString("content", var.content);
            arg.putString("id", var.id + "");
            arg.putInt("request_type", 1);
            pages.add(FragmentPagerItem.of("资料" + var.sort, 1.f, EssExMaterialsContent.class, arg));
        }

        adapter = new FragmentPagerItemAdapter(getChildFragmentManager(), pages);
        if (index < 15) {
            viewPager.setOffscreenPageLimit(index);
        }
        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);

        if (currentCommentPosition != 0 && adapter != null && adapter.getCount() > currentCommentPosition) {
            viewPager.setCurrentItem(currentCommentPosition);
        }

        if (showMaterialId != 0) {
            int di = 0;
            for (int i = 0; i < cacheExamMaterialListBean.size(); i++) {
                ExamMaterialListBean examMaterialListBean = cacheExamMaterialListBean.get(i);
                if (examMaterialListBean.id == showMaterialId) {
                    di = i;
                    break;
                }
            }
            if (di != 0) {
                viewPager.setCurrentItem(di);
            }
        }
    }

    /**
     * 刷新显示的区域
     */
    private void refreshSingleAreaSelView() {
        if (provinceChoiceListener == null) {
            return;
        }
        if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业，不显示可选地区按钮
            if (StringUtils.isEmpty(areaName) || !isSingle) {
                llProvince.setVisibility(View.GONE);
                llProvince.setOnClickListener(null);
            } else {
                llProvince.setVisibility(View.VISIBLE);
                tvProvince.setVisibility(View.VISIBLE);
                tvProvince.setText(areaName);
                ivProvince.setVisibility(View.GONE);
                llProvince.setOnClickListener(null);
            }
            return;
        }
        llProvince.setOnClickListener(null);
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
                    llProvince.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            provinceChoiceListener.onClickProvince();
                        }
                    });
                }
            } else {
                llProvince.setVisibility(View.VISIBLE);
                tvProvince.setVisibility(View.VISIBLE);
                tvProvince.setText(areaName);
                if (provinceChoiceListener != null) {
                    provinceChoiceListener.setProvinceVisibility(View.GONE);
                }
                ivProvince.setVisibility(View.GONE);
            }
        } else {
            llProvince.setVisibility(View.GONE);
            tvProvince.setVisibility(View.GONE);
            ivProvince.setVisibility(View.GONE);
            if (provinceChoiceListener != null) {
                provinceChoiceListener.setProvinceVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentCommentPosition = 0;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public interface ProvinceChoiceListener {
        void onClickProvince();

        void setProvinceVisibility(int visibility);
    }
}
