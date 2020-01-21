package com.huatu.handheld_huatu.business.faceteach;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.faceteach.adapter.AreaListAdapter;
import com.huatu.handheld_huatu.business.faceteach.adapter.ContentListAdapter;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.FaceAreaBean;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * 某类商品  筛选弹窗popupwindow
 * Created by Administrator on 2017/2/16.
 */
public class AreasFilterPopWin implements View.OnClickListener , OnRecItemClickListener {
    private Activity activity;

    private PopupWindow filterPop;


    private String mSelectAreaId,mSelectCateId,mSelectExamType;
    private int mSelectAction=-1;//1区域布局 2,分类布局 3, 面试或笔试  0-笔试 1-面试

    private RecyclerView mAreaListView,mContentListView;


    /**  透明遮罩  */
    private View maskView;

    private View mRootView;
    private WindowManager windowManager;
    AreaListAdapter mAreaListAdapter;
    ContentListAdapter mContentListAdapter;
    GridLayoutManager mContentlayoutManager;
    private List<FaceAreaBean> mCateIdList,mAreaList,mExamTypeList;


    public AreasFilterPopWin(Activity activity) {
        this.activity = activity;
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    }

    public interface OnFilterConfirmClickListener {
        void filterListConfirm(int actionType , FaceAreaBean areaBean);
    }
    private OnFilterConfirmClickListener mOnFilterConfirmClickListener;

    private View.OnClickListener mDismissListener;

    public void setOnFilterConfirmClickListener(OnFilterConfirmClickListener onFilterConfirmClickListener) {
        this.mOnFilterConfirmClickListener = onFilterConfirmClickListener;
    }

    public void setDismissListener(View.OnClickListener dismissListener){
        this.mDismissListener=dismissListener;
    }

    public AreasFilterPopWin build(List<FaceAreaBean> cateIdlist,List<FaceAreaBean> areaList,String selectAreaId,String selectCateId,String topAreaId) {

            mSelectAreaId=selectAreaId;
            mSelectCateId=selectCateId;
            mSelectExamType= PrefStore.getSettingString(UserInfoUtil.userId+"_"+"faceExamType","0");;
            mCateIdList=cateIdlist;
            mAreaList=areaList;
            View view = LayoutInflater.from(activity).inflate(R.layout.course_area_fiter_layout, null);
            filterPop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT ,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //   filterPop.setAnimationStyle(R.style.pop_window_anim_left2right);
            mAreaListView = (RecyclerView) view.findViewById(R.id.area_listView);
            mContentListView = (RecyclerView) view.findViewById(R.id.content_listView);


            mContentlayoutManager = new GridLayoutManager(activity, 3);

            mContentListView.setLayoutManager(mContentlayoutManager);
            mContentListAdapter=new ContentListAdapter(activity);
            mContentListView.setAdapter(mContentListAdapter);
            if((!ArrayUtils.isEmpty(mAreaList))&&(!ArrayUtils.isEmpty(mAreaList.get(0).child))){
                mContentListAdapter.setSeletedID(mAreaList.get(0).child.get(0).areaid);
                mContentListAdapter.refresh(mAreaList.get(0).child);
            }

            mContentListAdapter.setOnViewItemClickListener(new OnRecItemClickListener() {
                @Override
                public void onItemClick(int position, View view, int type) {

                    FaceAreaBean tmpBean=   mContentListAdapter.getItem(position);
                    if(null!=tmpBean){
                        if(tmpBean.areaid.equals(mContentListAdapter.mSeletedId)) return;
                        mContentListAdapter.setSeletedID(tmpBean.areaid);
                        mContentListAdapter.notifyDataSetChanged();

                        if(mSelectAction==1){
                            mSelectAreaId=tmpBean.areaid;
                        }
                        else if(mSelectAction==2){
                            mSelectCateId=tmpBean.areaid;
                        }else {
                            mSelectExamType=tmpBean.areaid;
                        }
                        if(null!=mOnFilterConfirmClickListener){
                            mOnFilterConfirmClickListener.filterListConfirm(mSelectAction ,tmpBean);
                        }
                    }
                    filterPop.dismiss();
                }
            });

            mAreaListView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
            mAreaListAdapter=new AreaListAdapter(activity,areaList);
            mAreaListAdapter.setSeletedID(topAreaId);
            mAreaListView.setAdapter(mAreaListAdapter);

            mAreaListAdapter.setOnViewItemClickListener(this);

            filterPop.setFocusable(true);
            filterPop.setBackgroundDrawable(new ColorDrawable());
            filterPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //removeMask();
                    if(null!=mDismissListener){
                        mDismissListener.onClick(null);
                    }

                 }
            });
         return this;
    }


    public void setCurrentType(int  curAction){
        if(mSelectAction!=curAction){
            mSelectAction=curAction;
            //1区域布局 2,分类布局,3, 面试或笔试
            if(curAction==1)  {
                mAreaListView.setVisibility(View.VISIBLE);
                int selIndex= mAreaListAdapter.getSelectIndex();
                LogUtils.e("setCurrentType",selIndex+",");
                FaceAreaBean curSelBean=mAreaListAdapter.getItem(selIndex);

                mContentlayoutManager.setSpanCount(3);
                mContentListAdapter.setSeletedID(mSelectAreaId);
                if((curSelBean!=null)&&!ArrayUtils.isEmpty(curSelBean.child)){
                    mContentListAdapter.refresh( curSelBean.child);
                    mContentListView.scrollToPosition(0);
                }else {
                    mContentListAdapter.setSeletedID("");
                    mContentListAdapter.clearAndRefresh();
                }

            }else if(curAction==2) {
                mAreaListView.setVisibility(View.GONE);
                mContentlayoutManager.setSpanCount(4);

                mContentListAdapter.setSeletedID(mSelectCateId);
                mContentListAdapter.refresh(mCateIdList);
                mContentListView.scrollToPosition(0);
            }else if(curAction==3) {
                mAreaListView.setVisibility(View.GONE);
                mContentlayoutManager.setSpanCount(4);

                if(mExamTypeList==null){
                    mExamTypeList=new ArrayList<>();
                    FaceAreaBean tmpbean=new FaceAreaBean();
                    tmpbean.areaid="0";
                    tmpbean.areaname="笔试课程";
                    FaceAreaBean tmpbean2=new FaceAreaBean();
                    tmpbean2.areaid="1";
                    tmpbean2.areaname="面试课程";
                    mExamTypeList.add(tmpbean);
                    mExamTypeList.add(tmpbean2);
                }
                mContentListAdapter.setSeletedID(mSelectExamType);
                mContentListAdapter.refresh(mExamTypeList);
                mContentListView.scrollToPosition(0);
            }
        }
    }


    private View view;


    public void showAtLocation(View curAnvor) {
        view = curAnvor;
//        Window win = activity.getWindow();
//        ViewGroup decorViewGroup = (ViewGroup) win.getDecorView();
//        AnimUtils.alphaDark(decorViewGroup);

        // addMask(curAnvor.getWindowToken());
        //filterPop.showAtLocation(curAnvor, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);

        filterPop.showAsDropDown(view, 0, 0);
    }
//  @OnClick({R.id.tv_collection,R.id.rl_scan_btn




    @Override
    public void onItemClick(int position,View view,int type){

        FaceAreaBean tmpBean= mAreaListAdapter.getItem(position);
        if(null!=tmpBean){
            String  curSelectID=tmpBean.areaid;
            if(curSelectID.equals(mAreaListAdapter.getSeletedID())) return;
            PrefStore.putSettingString(UserInfoUtil.userId+"_"+"provinceAreaId",curSelectID);

            mAreaListAdapter.setSeletedID(curSelectID);
            mAreaListAdapter.notifyDataSetChanged();
            if(!ArrayUtils.isEmpty(tmpBean.child))
                mContentListAdapter.refresh(tmpBean.child);
            else {
                mContentListAdapter.setSeletedID("");
                mContentListAdapter.clearAndRefresh();
            }
        }
     }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.btn_find_all:
              //  new AllBrandPopWin(activity).build(300, selectedBrandIds).setOnBrandSelectedChangedListener(this).showAtLocation(view);
                break;

            case R.id.reset:
                selectedBrandIds.clear();
                selectedBrandAdapter.setDatas(selectedBrandIds);
                tvSelected.setVisibility(View.GONE);
                container.setVisibility(View.GONE);

                selectedPriceId = -1;
                priceFilter.clearSelectedStatus();
                break;

            case R.id.confirm:
                onFilterConfirmClickListener.filterListConfirm(selectedPriceId, getBrandFilterIds(selectedBrandIds));
                filterPop.dismiss();
                break;
        }*/
    }




    private void addMask(IBinder token) {
        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;
        wl.format = PixelFormat.TRANSLUCENT;//不设置这个弹出框的透明遮罩显示为黑色
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;//该Type描述的是形成的窗口的层级关系
        wl.token = token;//获取当前Activity中的View中的token,来依附Activity
        maskView = new View(activity);
        maskView.setBackgroundColor(Color.WHITE);
        maskView.setFitsSystemWindows(false);
        maskView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    removeMask();
                    return true;
                }
                return false;
            }
        });
        /**
         * 通过WindowManager的addView方法创建View，产生出来的View根据WindowManager.LayoutParams属性不同，效果也就不同了。
         * 比如创建系统顶级窗口，实现悬浮窗口效果！
         */
        windowManager.addView(maskView, wl);
    }

    private void removeMask() {
        if (null != maskView) {
            windowManager.removeViewImmediate(maskView);
            maskView = null;
        }
    }
}
