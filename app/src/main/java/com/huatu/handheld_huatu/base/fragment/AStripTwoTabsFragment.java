package com.huatu.handheld_huatu.base.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.SelectedLoadViewPager;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.utils.ArrayUtils;
import com.huatu.viewpagerindicator.PagerSlidingTabStrip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;



/**
 * Created by xing on 15-1-20.
 */
public abstract class AStripTwoTabsFragment<T extends AStripTwoTabsFragment.StripTabItem> extends AbsFragment
        implements SelectedLoadViewPager.OnPageSelectedFlushListener {

    static final String TAG = AStripTwoTabsFragment.class.getSimpleName();

    public static final String SET_INDEX = "org.xdw.android.ui.SET_INDEX";// 默认选择第几个


    @BindView(R.id.xi_activity_type_tab_strip)
    PagerSlidingTabStrip slidingTabs;

    @BindView(R.id.xi_activity_type_view_pager)
   protected SelectedLoadViewPager viewPager;

    MyViewPagerAdapter mViewPagerAdapter;

    ArrayList<T> mItems;
     Map<String, Fragment> fragments;
    int mCurrentPosition = 0;

    protected void setCurrentPosition(int positon){
        mCurrentPosition=positon;
    }

     public PagerSlidingTabStrip getslidingTabs(){return slidingTabs;}
    @Override
    public int getContentView() {
        return R.layout.comm_ui_tabs;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCurrentPosition = viewPager.getCurrentItem();
        outState.putSerializable("items", mItems);
        outState.putInt("current", mCurrentPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = savedInstanceState == null ? generateTabs()
                : (ArrayList<T>) savedInstanceState.getSerializable("items");
        mCurrentPosition = savedInstanceState == null ? 0
                : savedInstanceState.getInt("current");
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, final Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        // setHasOptionsMenu(true);

        if (delayGenerateTabs() == 0) {
            setTab(savedInstanceSate,null);
        } else if (delayGenerateTabs() == -1) {

        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    setTab(savedInstanceSate,null);
                }

            }, delayGenerateTabs());
        }
    }

    protected void setTab(final Bundle savedInstanceSate, ArrayList<T> mTypes) {
        if (getActivity() == null)
            return;
        if(null!=mTypes&&(!ArrayUtils.isEmpty(mTypes))) mItems=mTypes;
        if (savedInstanceSate == null) {
            if (getArguments() != null && getArguments().containsKey(SET_INDEX)) {
                mCurrentPosition = getArguments().getInt(SET_INDEX,0);
            } else {
                if (configLastPositionKey() != null) {
                    // 记录了最后阅读的标签
                    String type = PrefStore.getSettingString("PagerLastPosition" + configLastPositionKey(), "");
                    if (!TextUtils.isEmpty(type)) {
                        for (int i = 0; i < mItems.size(); i++) {
                            StripTabItem item = mItems.get(i);
                            if (item.getType().equals(type)) {
                                mCurrentPosition = i;
                                break;
                            }
                        }
                    }
                }
            }
        }

        //Logger.w("strip-current-" + mCurrentPosition);

        fragments = new HashMap<String, Fragment>();

        if (mItems == null)
            return;

        for (int i = 0; i < mItems.size(); i++) {//.getFragmentManager()  getActivity().getSupportFragmentManager()


        /*    Fragment fragment = this.getActivity().getSupportFragmentManager().findFragmentByTag(makeFragmentName(i));//getActivity().getFragmentManager().findFragmentByTag(makeFragmentName(i));
            if (fragment != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment).commitAllowingStateLoss();
            }
*/
            Fragment fragment = this.getChildFragmentManager().findFragmentByTag(makeFragmentName(i));//getActivity().getFragmentManager().findFragmentByTag(makeFragmentName(i));
            if (fragment != null) {
                getChildFragmentManager().beginTransaction()
                        .remove(fragment).commitAllowingStateLoss();
            }

//         fragments.put(makeFragmentName(i), fragment);
        }

        mViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager());//getChildFragmentManager() getFragmentManager()
//					viewPager.setOffscreenPageLimit(mViewPagerAdapter.getCount());
        if(mViewPagerAdapter.getCount()<=0) return;
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(mViewPagerAdapter);
        if (mCurrentPosition >= mViewPagerAdapter.getCount())
            mCurrentPosition = 0;
        viewPager.setCurrentItem(mCurrentPosition);



        slidingTabs.setViewPager(viewPager);
       // slidingTabs.setOnPageChangeListener(this);
        viewPager.setOnPageSelectedFlushListener(this);

        onPageSelected(mCurrentPosition);
    }


    protected void destoryFragments() {
       /* if (getActivity() != null ) {
            if (getActivity() instanceof ReuseActivity) {
                ReuseActivity mainActivity = (ReuseActivity) getActivity();
                if (mainActivity.mIsDestoryed())
                    return;
            }

            try {
                FragmentTransaction trs = getChildFragmentManager().beginTransaction();//getFragmentManager()
                Set<String> keySet = fragments.keySet();
                for (String key : keySet) {
                    if (fragments.get(key) != null) {
                        trs.remove(fragments.get(key));

                        //   Logger.e("remove fragment , key = " + key);
                    }
                }
                trs.commitAllowingStateLoss();//commit();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }*/
    }

 /*   @Override
    public void onPageScrolled(int i, float v, int i2) {

    }*/

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;

        if (configLastPositionKey() != null) {
            PrefStore.putSettingString("PagerLastPosition" + configLastPositionKey(), mItems.get(position).getType());
        }

        LogUtils.e("onPageSelected___?_", position + "");
        // 查看是否需要拉取数据
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof IStripTabInitData) {
            ((IStripTabInitData) fragment).onStripTabRequestData();
        }
    }

/*    @Override
    public void onPageScrollStateChanged(int i) {

    }*/

    protected String makeFragmentName(int position) {
        return mItems.get(position).getTitle();
    }

    // 是否保留最后阅读的标签
    protected String configLastPositionKey() {
        return null;
    }

    abstract protected ArrayList<T> generateTabs();

    abstract protected Fragment newFragment(T bean);

    // 延迟一点初始化tabs，用于在首页切换菜单的时候，太多的tab页导致有点点卡顿
    protected int delayGenerateTabs() {
        return 0;
    }

    @Override
    public void onDestroy() {
        try {
               destoryFragments();

            // viewPager.setAdapter(null);
            mViewPagerAdapter = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public void setCurrentFragment(int position) {
        mCurrentPosition = position;
        //viewPager.setCurrentItem(position);
    }

    public Fragment getCurrentFragment() {
        if (mViewPagerAdapter == null || mViewPagerAdapter.getCount() < mCurrentPosition)
            return null;

        //return  mViewPagerAdapter.getCurrentFragment();
        return fragments.get(makeFragmentName(mCurrentPosition));
    }

 /*   public Fragment getFragment(String tabTitle) {
        if (fragments == null || TextUtils.isEmpty(tabTitle))
            return null;

        for (int i = 0; i < mItems.size(); i++) {
            if (tabTitle.equals(mItems.get(i).getTitle())) {
                return fragments.get(makeFragmentName(i));
            }
        }

        return null;
    }*/

   /* public Map<String, Fragment> getFragments() {
        return fragments;
    }*/

    public ViewPager getViewPager() {
        return viewPager;
    }

    public PagerSlidingTabStrip getSlidingTabLayout() {
        return slidingTabs;
    }

    class MyViewPagerAdapter extends  android.support.v4.app.FragmentStatePagerAdapter{
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            Fragment curFrag=newFragment(mItems.get(position));
            fragments.put(makeFragmentName(position),curFrag);
            return curFrag;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container,position,object);
            fragments.remove(makeFragmentName(position));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mItems.get(position).getTitle();
        }


    }

   /* class MyViewPagerAdapter extends SysFragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
          *//*  Fragment fragment = fragments.get(makeFragmentName(position));
            if (fragment == null) {
                fragment = newFragment(mItems.get(position));

                fragments.put(makeFragmentName(position), fragment);
            }
*//*

            return newFragment(mItems.get(position));
           // return fragment;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mItems.get(position).getTitle();
        }

        @Override
        protected String makeFragmentName(int position) {
            return AStripTwoTabsFragment.this.makeFragmentName(position);
        }

       *//* @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
             super.destroyItem(container,position,object);
            fragments.remove(makeFragmentName(position));
        }*//*

    }
*/
    public static class StripTabItem implements Serializable {

        private static final long serialVersionUID = 3680682035685685311L;

        private String type;

        private String title;

        private Serializable tag;

        public StripTabItem() {

        }

        public StripTabItem(String type, String title) {
            this.type = type;
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Serializable getTag() {
            return tag;
        }

        public void setTag(Serializable tag) {
            this.tag = tag;
        }
    }

    // 这个接口用于多页面时，只有当前的页面才加载数据，其他不显示的页面暂缓加载
    // 当每次onPagerSelected的时候，再调用这个接口初始化数据
    public interface IStripTabInitData {

        public void onStripTabRequestData();

    }


    /**
     * 设置返回按钮是否可用
     *
     * @param enabled 可用状态
     */
    protected void setHomeAsUpEnabled(boolean enabled) {
        if (getTitleBar() != null) {
            getTitleBar().setDisplayHomeAsUpEnabled(enabled);
        }
    }

    /**
     * 返回按钮的点击
     */
    protected void onGoBack() {
        if (!hasResult()) {
            setResult(RESULT_CANCEL);
        }
        finish();
    }

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        if (menuItem.getId() == R.id.xi_title_bar_home) {
            onGoBack();
        }
    }
}
