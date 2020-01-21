package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.ADelayStripTwoTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTwoTabsFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\6\29 0029.
 */

public class DownAllLoadingFragment extends ADelayStripTwoTabsFragment<AStripTwoTabsFragment.StripTabItem> {


    private boolean mHasDeleted=false;
    @Override
    public int getContentView() {
        return R.layout.down_finished_ui_twotabs;
    }

    @Override
    protected int delayGenerateTabs() {
        return 100;
    }

    private boolean mForResult=false;
    private boolean mHasTypeInit=false;
    private CompositeSubscription mCompositeSubscription;

    public void setHasDelete(){
        mHasDeleted=true;
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if(getArguments() != null){
            parserParams(getArguments());
        }
    }

    public static void lanuchForResult(Fragment context , int requestCode){

        Bundle tmpArg=new Bundle();
        tmpArg.putBoolean(ArgConstant.FROM_ACTION,true);
        UIJumpHelper.jumpFragment(context,requestCode,DownAllLoadingFragment.class,tmpArg);
    }


    protected void parserParams(Bundle arg) {
        mForResult = arg.getBoolean(ArgConstant.FROM_ACTION);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
      //  EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);

        getTitleBar().setShadowVisibility(View.GONE);
        setTitle("正在下载");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(viewPager!=null) viewPager.setScrollable(false);
    }
    MenuItem mRightMenu;
    @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {
        super.onCreateTitleBarMenu(titleBar, container);
          titleBar.add("编辑", android.R.id.button1);
          mRightMenu=titleBar.findMenuItem(android.R.id.button1);
     }


    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if (menuItem.getId() == android.R.id.button1) {
              if(getCurrentFragment() instanceof OnSwitchListener){

                  int isOldEdit=((OnSwitchListener)getCurrentFragment()).isEditMode();
                  if(isOldEdit==2){return;}
                  menuItem.setText((isOldEdit==0) ? R.string.pickerview_cancel:R.string.edit);
                  ((OnSwitchListener)getCurrentFragment()).switchMode();
              }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
      //  EventBus.getDefault().unregister(this);
    }

    @Override
    protected ArrayList<StripTabItem> generateTabs() {
        ArrayList<StripTabItem> items = new ArrayList<StripTabItem>();
        items.add(new StripTabItem(StringUtils.valueOf(0), "视频"));
        items.add(new StripTabItem(StringUtils.valueOf(1), "讲义"));
        return items;
   }

    @Override
    protected Fragment newFragment(StripTabItem bean) {
         if (bean != null) {
             if(bean.getType().equals("0"))
                 return new DownloadingCourseFragment();
             else
                 return new DownloadingHandoutFragment();
        }
        return null;
    }


    @Override
    public void onPageSelected(int position) {
         super.onPageSelected(position);
         if(getCurrentFragment() instanceof OnSwitchListener){
             int isOldEdit=((OnSwitchListener)getCurrentFragment()).isEditMode();
             mRightMenu.setText((isOldEdit==1) ? R.string.pickerview_cancel:R.string.edit);
            // mRightMenu.setVisibility(position==0?View.VISIBLE:View.GONE);
         }
     }




    @Override
    protected void onGoBack() {
        if(mHasDeleted){
            Intent data = new Intent();
            setResult(Activity.RESULT_OK, data);
        }
        super.onGoBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mHasDeleted){
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
                return super.onKeyDown(keyCode,event);
            }
        }
        return super.onKeyDown(keyCode,event);
    }

}
