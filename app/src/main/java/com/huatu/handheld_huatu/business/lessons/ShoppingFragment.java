package com.huatu.handheld_huatu.business.lessons;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @author zhaodongdong.
 */
@Deprecated
public class ShoppingFragment extends BaseCourseListFragment
        implements View.OnClickListener {
    private TopActionBar topActionBar;
    private TextView btnSequenceComprehensive;
    private TextView btnSequenceLatest;
    private TextView btnSequencePrice;
    private ImageView ivSequencePrice;
    private TextView btnSelector;
    private PopupWindow mPopWindow;
    private TextView btnPriceAll;
    private TextView btnPriceFree;
    private TextView btnPriceFee;

    public static ShoppingFragment newInstance() {
        return new ShoppingFragment();
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_shopping;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        topActionBar = (TopActionBar) rootView.findViewById(R.id.fragment_shopping_title_bar);
        initTitleBar();
        btnSequenceComprehensive = (TextView) rootView.findViewById(R.id.shopping_sequence_comprehensive_tv);
        btnSequenceLatest = (TextView) rootView.findViewById(R.id.shopping_sequence_latest_tv);
        btnSequencePrice = (TextView) rootView.findViewById(R.id.shopping_sequence_price_tv);
        ivSequencePrice = (ImageView) rootView.findViewById(R.id.shopping_sequence_price_iv);
        btnSelector = (TextView) rootView.findViewById(R.id.shopping_selector_tv);

        ivSequencePrice.setOnClickListener(this);
        btnSequenceComprehensive.setOnClickListener(this);
        btnSequenceLatest.setOnClickListener(this);
        btnSequencePrice.setOnClickListener(this);
        btnSelector.setOnClickListener(this);
        btnSequenceComprehensive.setTextColor(Color.parseColor("#e9304e"));
    }

    private void initTitleBar() {
        topActionBar.setTitle("直播");
        topActionBar.showButtonImage(R.drawable.icon_tiku_searh, TopActionBar.LEFT_AREA);
        topActionBar.showButtonText("我的课程", TopActionBar.RIGHT_AREA, R.color.text_color_light);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("order_id", mOrderId);
                bundle.putInt("price_id", mPriceId);
                BaseFrgContainerActivity.newInstance(mActivity,
                        CourseSearchLiveFragment.class.getName(), bundle);
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
              //  UIJumpHelper.jumpFragment(view.getContext(),MySingleTypeCourseFragment.class);
                //MyPurchasedFragment.newInstance(1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_selector_tv:
                initPopWindow();
                mPopWindow.showAsDropDown(btnSelector, -20, 0);
                break;
            case R.id.shopping_sequence_comprehensive_tv:
                if (mOrderId != 0) {
                    mOrderId = 0;
                    btnSequenceComprehensive.setTextColor(Color.parseColor("#e9304e"));
                    btnSequenceLatest.setTextColor(Color.parseColor("#666666"));
                    btnSequencePrice.setTextColor(Color.parseColor("#666666"));
                    ivSequencePrice.setImageResource(R.drawable.shopping_price_normal);
                    onLoadData();
                    mListView.setSelection(0);
                    MobclickAgent.onEvent(mActivity, "AL_Comprehensive");
                }
                break;
            case R.id.shopping_sequence_latest_tv:
                if (mOrderId != 3) {
                    mOrderId = 3;
                    btnSequenceLatest.setTextColor(Color.parseColor("#e9304e"));
                    btnSequenceComprehensive.setTextColor(Color.parseColor("#666666"));
                    btnSequencePrice.setTextColor(Color.parseColor("#666666"));
                    ivSequencePrice.setImageResource(R.drawable.shopping_price_normal);
                    onLoadData();
                    mListView.setSelection(0);
                    MobclickAgent.onEvent(mActivity, "AL_OnLineTime");
                }
                break;
            case R.id.shopping_sequence_price_tv:
                if (mOrderId == 2) {
                    //升序
                    mOrderId = 1;
                    btnSequencePrice.setTextColor(Color.parseColor("#e9304e"));
                    btnSequenceComprehensive.setTextColor(Color.parseColor("#666666"));
                    btnSequenceLatest.setTextColor(Color.parseColor("#666666"));
                    ivSequencePrice.setImageResource(R.drawable.shopping_price_up);
                    onLoadData();
                    mListView.setSelection(0);
                    MobclickAgent.onEvent(mActivity, "AL_PriceRangeFromLowToHigh");
                } else {
                    mOrderId = 2;
                    btnSequencePrice.setTextColor(Color.parseColor("#e9304e"));
                    btnSequenceComprehensive.setTextColor(Color.parseColor("#666666"));
                    btnSequenceLatest.setTextColor(Color.parseColor("#666666"));
                    ivSequencePrice.setImageResource(R.drawable.shopping_price_down);
                    onLoadData();
                    mListView.setSelection(0);
                    MobclickAgent.onEvent(mActivity, "AL_PriceRangeFromHighToLow");
                }
                break;
        }
    }

    public void initPopWindow() {
        if (mPopWindow == null) {
            LinearLayout mPopLayout = (LinearLayout) (mLayoutInflater.inflate(R.layout.shopping_popwindow_layout, null));
            mPopWindow = new PopupWindow(mPopLayout, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopWindow.setFocusable(true);
            mPopWindow.setOutsideTouchable(true);
            btnPriceAll = (TextView) mPopLayout.findViewById(R.id.shopping_type_all);
            btnPriceFree = (TextView) mPopLayout.findViewById(R.id.shopping_type_free);
            btnPriceFee = (TextView) mPopLayout.findViewById(R.id.shopping_type_fee);
            btnPriceAll.setBackgroundColor(Color.parseColor("#3c464f"));
            mPopLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mPopWindow.dismiss();
                    return true;
                }
            });
            btnPriceAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPriceId != 1000) {
                        mPriceId = 1000;
                        onLoadData();
                        mListView.setSelection(0);
                        mPopWindow.dismiss();
                        btnSelector.setText("全部");
                        btnPriceAll.setBackgroundColor(Color.parseColor("#3c464f"));
                        btnPriceFree.setBackgroundColor(Color.parseColor("#576069"));
                        btnPriceFee.setBackgroundColor(Color.parseColor("#576069"));
                        MobclickAgent.onEvent(mActivity, "AL_AllPrice");
                    } else {
                        mPopWindow.dismiss();
                    }
                }
            });
            btnPriceFree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPriceId != 1) {
                        mPriceId = 1;
                        onLoadData();
                        mListView.setSelection(0);
                        mPopWindow.dismiss();
                        btnSelector.setText("免费");
                        btnPriceFree.setBackgroundColor(Color.parseColor("#3c464f"));
                        btnPriceAll.setBackgroundColor(Color.parseColor("#576069"));
                        btnPriceFee.setBackgroundColor(Color.parseColor("#576069"));
                        MobclickAgent.onEvent(mActivity, "AL_FreeOfCharge");
                    } else {
                        mPopWindow.dismiss();
                    }
                }
            });
            btnPriceFee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPriceId != 2) {
                        mPriceId = 2;
                        onLoadData();
                        mListView.setSelection(0);
                        mPopWindow.dismiss();
                        btnSelector.setText("付费");
                        btnPriceFee.setBackgroundColor(Color.parseColor("#3c464f"));
                        btnPriceAll.setBackgroundColor(Color.parseColor("#576069"));
                        btnPriceFree.setBackgroundColor(Color.parseColor("#576069"));
                        MobclickAgent.onEvent(mActivity, "AL_price1_9");
                    } else {
                        mPopWindow.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onLoadData() {
        startCountDownTask();
        mActivity.showProgress();
        onRefresh();
    }
}
