package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseCalenderFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.DissLessionActivity;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.view.TopActionBar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.huatu.handheld_huatu.R.id.purchased_course_calender_tv;

/**
 * Created by saiyuan on 2017/8/29.
 */
@Deprecated
public class MyPurchasedFragment extends BasePurchasedFragment {
    private PopupWindow mPopupWindowMore;
    private TextView btnDismissCourse;
    private TextView btnSearch;
    @BindView(R.id.purchased_course_selector_tv)
    public TextView btnSelector;
    @BindView(purchased_course_calender_tv)
    public TextView btnCalender;
    private PopupWindow mPopupWindowSelector;
    private TextView btnSelectAll;
    private TextView btnSelectLive;
    private TextView btnSelectVod;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_my_purchased_course_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        setSelectorState();
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        mActivity.showProgress();
        onRefresh();
    }

    @Override
    public void initToolBar() {
        topActionBar.setTitle("我的课程");
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.showButtonImage(R.drawable.icon_more, TopActionBar.RIGHT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                onBackPressed();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                initPopWindowMore();
                mPopupWindowMore.showAsDropDown(topActionBar.getArea(TopActionBar.RIGHT_AREA), -20, 0);
            }
        });
    }

    private void initPopWindowMore() {
        if (mPopupWindowMore == null) {
            LinearLayout mPopLayout = (LinearLayout) (mLayoutInflater.inflate(
                    R.layout.purchased_course_popwindow_layout, null));
            mPopupWindowMore = new PopupWindow(mPopLayout, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindowMore.setFocusable(true);
            mPopupWindowMore.setOutsideTouchable(true);
            btnDismissCourse = (TextView) mPopLayout.findViewById(R.id.shopping_type_all);
            btnSearch = (TextView) mPopLayout.findViewById(R.id.shopping_type_fee);
            mPopLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mPopupWindowMore.dismiss();
                    return true;
                }
            });
            btnDismissCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindowMore.dismiss();
                    Intent intent = new Intent(getActivity(), DissLessionActivity.class);
                    intent.putExtra("course_type", courseType);
                    startActivityForResult(intent, 1001);
                }
            });
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindowMore.dismiss();
                    onClickSearch();
                }
            });
        }
    }

    private void onClickSearch() {
        CourseSearchMineFragment fragment = new CourseSearchMineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("course_type", courseType);
        fragment.setArguments(bundle);
        startFragmentForResult(fragment, 1002);
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if(mPopupWindowMore != null && mPopupWindowMore.isShowing()) {
            mPopupWindowMore.dismiss();
        } else if(mPopupWindowSelector != null && mPopupWindowSelector.isShowing()) {
            mPopupWindowMore.dismiss();
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
        }
        return true;
    }

    @OnClick(R.id.purchased_course_selector_tv)
    public void onClickSelector() {
        initPopWindowSelector();
        mPopupWindowSelector.showAsDropDown(btnSelector, -20, 0);
    }

    @OnClick(purchased_course_calender_tv)
    public void onClickCalender() {
        CourseCalenderFragment calenderFragment = new CourseCalenderFragment();
        startFragmentForResult(calenderFragment);
    }

    private void setSelectorState() {
        if (mPopupWindowSelector == null) {
            initPopWindowSelector();
        }
        if (courseType == 0) {//ALL
            btnSelector.setText("全部");
            btnSelectAll.setBackgroundColor(Color.parseColor("#3c464f"));
            btnSelectLive.setBackgroundColor(Color.parseColor("#00000000"));
            btnSelectVod.setBackgroundColor(Color.parseColor("#00000000"));
            btnCalender.setVisibility(View.VISIBLE);
        } else if (courseType == 1) {
            btnSelector.setText("直播");
            btnSelectLive.setBackgroundColor(Color.parseColor("#3c464f"));
            btnSelectAll.setBackgroundColor(Color.parseColor("#00000000"));
            btnSelectVod.setBackgroundColor(Color.parseColor("#00000000"));
            btnCalender.setVisibility(View.VISIBLE);
        } else if (courseType == 2) {
            btnSelector.setText("录播");
            btnSelectVod.setBackgroundColor(Color.parseColor("#3c464f"));
            btnSelectAll.setBackgroundColor(Color.parseColor("#00000000"));
            btnSelectLive.setBackgroundColor(Color.parseColor("#00000000"));
            btnCalender.setVisibility(View.GONE);
        }
    }

    public void initPopWindowSelector() {
        if (mPopupWindowSelector == null) {
            LinearLayout mPopLayout = (LinearLayout) (mLayoutInflater.inflate(
                    R.layout.shopping_popwindow_layout, null));
            mPopLayout.setBackgroundResource(R.drawable.my_course_selection_pop_bg);
            mPopupWindowSelector = new PopupWindow(mPopLayout, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindowSelector.setFocusable(true);
            mPopupWindowSelector.setOutsideTouchable(true);
            btnSelectAll = (TextView) mPopLayout.findViewById(R.id.shopping_type_all);
            btnSelectAll.setText("全部");
            btnSelectLive = (TextView) mPopLayout.findViewById(R.id.shopping_type_free);
            btnSelectLive.setText("直播");
            btnSelectVod = (TextView) mPopLayout.findViewById(R.id.shopping_type_fee);
            btnSelectVod.setText("录播");
            setSelectorState();
            mPopLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mPopupWindowSelector.dismiss();
                    return true;
                }
            });
            btnSelectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 0) {
                        courseType = 0;
                        setSelectorState();
                        onRefresh();
                        listView.setSelection(0);
                    }
                    mPopupWindowSelector.dismiss();
                }
            });
            btnSelectLive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 1) {
                        courseType = 1;
                        setSelectorState();
                        onRefresh();
                        listView.setSelection(0);
                    }
                    mPopupWindowSelector.dismiss();
                }
            });
            btnSelectVod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 2) {
                        courseType = 2;
                        setSelectorState();
                        onRefresh();
                        listView.setSelection(0);
                    }
                    mPopupWindowSelector.dismiss();
                }
            });
        }
    }

    @Override
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            curPage = 1;
        } else {
            curPage++;
        }
        ServiceProvider.getMyPurchasedCourse(compositeSubscription, courseType, curPage, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                CourseMineBean dataBean = (CourseMineBean) model.data;
                MyPurchasedFragment.this.onSuccess(dataBean.result, isRefresh);
                if(dataBean.next == 1) {
                    listView.setPullLoadEnable(true);
                } else {
                    listView.setPullLoadEnable(false);
                }
                layoutErrorView.setErrorText("暂无相关课程");
            }

            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                MyPurchasedFragment.this.onLoadDataFailed();
            }
        });
    }

    public static void newInstance(int type) {
        Bundle arg = new Bundle();
        arg.putInt("course_type", type);
        BaseFrgContainerActivity.newInstance(UniApplicationContext.getContext(),
                MyPurchasedFragment.class.getName(), arg);
    }
}
