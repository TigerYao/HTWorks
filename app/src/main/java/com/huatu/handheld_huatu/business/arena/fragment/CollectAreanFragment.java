package com.huatu.handheld_huatu.business.arena.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.popup.QuickListAction;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import butterknife.BindView;

/**
 * Created by cjx on 2020\1\15 0015.
 * 收藏夹
 */

public class CollectAreanFragment extends MySupportFragment {

    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;

    @BindView(R.id.pop_title_layout)
    LinearLayout mPopBar;

    public static void launch(Context context){
        UIJumpHelper.jumpSupportFragment(context,new FragmentParameter(CollectAreanFragment.class, new Bundle()),1);
    }

    @Override
    public int getContentView() {
        return R.layout.arena_collect_layout;
    }

    @Override
    protected void setListener() {
        mTopTitleBar.setTitle("收藏");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if(menuItem.getId() == R.id.xi_title_bar_home){
                    getActivity().finish();
                }
                else if(menuItem.getId() == android.R.id.button1){
                    getActivity().finish();
                }
            }
        });

        mTopTitleBar.add("导出",0xff5D9AFF,android.R.id.button1);
        mTopTitleBar.add(ResourceUtils.getDrawable(R.mipmap.arean_collect_question),android.R.id.button2);
        mPopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    int[] location = new int[2];
                v.getLocationOnScreen(location);
                ToastUtils.showCenter(getActivity(),location[0]+","+location[1]+","+v.getWidth()+","+ DisplayUtil.getScreenWidth());
                showFilterTypeWindow(mTopTitleBar);*/

         /*       initNormalPopupIfNeed();
                mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mNormalPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);*/
                showFilterWindow(mTopTitleBar);
            }
        });
    }


    QuickListAction shareActons;
    private void showFilterTypeWindow(View anchor) {

        if (shareActons == null) {
            shareActons = new QuickListAction(getContext(), R.layout.arena_collect_poplayout, R.id.root);
            //shareActons.setForceOnBottom();
           // shareActons.getRootView().findViewById(R.id.pop_menu_upreport).setSelected(true);
             shareActons.setDistance(0);
            shareActons.setAnimStyle(R.style.Animations_PopDownMenu_Center);
            shareActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    if(!view.isSelected()){//  没有切换
                        shareActons.dismiss();
                        return;
                    }else {
/*
                        ViewGroup containerView= shareActons.getRootView().findViewById(R.id.root);
                        int resetIndex=position==0? 1:0;
                        view.setSelected(false);
                        containerView.getChildAt(resetIndex).setSelected(true);

                        shareActons.dismiss();
                        mListAdapter.refreshOrderType(position);

                        if(null!=myPeopleListView){
                            mRecentStatus=position;
                            mListResponse.mRecentStatus=mRecentStatus;
                            myPeopleListView.getRefreshableView().scrollToPosition(0);
                            myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                        }*/
                    }
                }
            });
            shareActons.show(anchor);
        } else
            shareActons.Reshow(anchor);
    }

    QMUIPopup mNormalPopup;
    int mLastSelected=0;
    private void showFilterWindow(View anchor) {
        if (mNormalPopup == null) {
            mNormalPopup = new QMUIPopup(getContext(), QMUIPopup.DIRECTION_BOTTOM);

            mNormalPopup.setContentView(R.layout.arena_collect_poplayout);
            mLastSelected=0;
            mNormalPopup.getRootView().findViewById(R.id.pop_menu_upreport).setSelected(true);
            mNormalPopup.setOnViewItemClickListener(new QMUIPopup.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    if(view.isSelected()) return;
                    view.setSelected(true);
                    ViewGroup containerView= mNormalPopup.getRootView().findViewById(R.id.root);
                    containerView.getChildAt(mLastSelected).setSelected(false);
                    mLastSelected=position;
                    mNormalPopup.dismiss();
                }
            });
         }
        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        mNormalPopup.show(anchor);
    }
}
