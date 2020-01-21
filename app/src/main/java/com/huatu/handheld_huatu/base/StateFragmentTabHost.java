package com.huatu.handheld_huatu.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by saiyuan on 2016/11/22.
 * android.support.v4.app.FragmentTabHost
 */

public class StateFragmentTabHost extends TabHost
        implements TabHost.OnTabChangeListener{
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private FrameLayout mRealTabContent;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mContainerId;
    private OnTabChangeListener mOnTabChangeListener;
    private TabInfo mLastTab;
    private boolean mAttached;

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    static class DummyTabFactory implements TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    static class SavedState extends BaseSavedState {
        String curTab;
        Bundle bundle;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            curTab = in.readString();
            bundle = in.readBundle();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(curTab);
            out.writeBundle(bundle);
        }

        @Override
        public String toString() {
            return "FragmentTabHost.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " curTab=" + curTab + "}" + ", bundle: " + bundle.toString();
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public StateFragmentTabHost(Context context) {
        // Note that we call through to the version that takes an AttributeSet,
        // because the simple Context construct can result in a broken object!
        super(context, null);
        initFragmentTabHost(context, null);
    }

    public StateFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFragmentTabHost(context, attrs);
    }

    private void initFragmentTabHost(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                new int[]{android.R.attr.inflatedId}, 0, 0);
        mContainerId = a.getResourceId(0, 0);
        a.recycle();

        super.setOnTabChangedListener(this);

        // If owner hasn't made its own view hierarchy, then as a convenience
        // we will construct a standard one here.
        if (findViewById(android.R.id.tabs) == null) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            addView(ll, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TabWidget tw = new TabWidget(context);
            tw.setId(android.R.id.tabs);
            tw.setOrientation(TabWidget.HORIZONTAL);
            tw.setDividerDrawable(null);//去掉了底部tab分割线
            ll.addView(tw, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0));

            FrameLayout fl = new FrameLayout(context);
            fl.setId(android.R.id.tabcontent);
            ll.addView(fl, new LinearLayout.LayoutParams(0, 0, 0));

            mRealTabContent = fl = new FrameLayout(context);
            mRealTabContent.setId(mContainerId);
            ll.addView(fl, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }
    }

    /**
     * @deprecated Don't call the original TabHost setup, you must instead
     * call {@link #setup(Context, android.support.v4.app.FragmentManager)} or
     * {@link #setup(Context, android.support.v4.app.FragmentManager, int)}.
     */
    @Override
    @Deprecated
    public void setup() {
        throw new IllegalStateException(
                "Must call setup() that takes a Context and FragmentManager");
    }

    public void setup(Context context, FragmentManager manager) {
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        ensureContent();
    }

    public void setup(Context context, FragmentManager manager, int containerId) {
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;
        ensureContent();
        mRealTabContent.setId(containerId);

        // We must have an ID to be able to save/restore our state. If
        // the owner hasn't set one at this point, we will set it ourself.
        if (getId() == View.NO_ID) {
            setId(android.R.id.tabhost);
        }
    }

    private void ensureContent() {
        if (mRealTabContent == null) {
            mRealTabContent = (FrameLayout) findViewById(mContainerId);
            if (mRealTabContent == null) {
                throw new IllegalStateException(
                        "No tab content FrameLayout found for id " + mContainerId);
            }
        }
    }

    @Override
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    public void addTab(TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mContext));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        if (mAttached) {
            // If we are already attached to the window, then check to make
            // sure this tab's fragment is inactive if it exists. This shouldn't
            // normally happen.
            info.fragment = mFragmentManager.findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }
        }

        mTabs.add(info);
        addTab(tabSpec);
    }

    public Fragment getCurrentFragment() {
        return (Fragment) mFragmentManager.findFragmentByTag(getCurrentTabTag());
    }

    public Fragment getCurrentFragmentByTag(String tag) {
        if (mFragmentManager != null) {
            return (Fragment) mFragmentManager.findFragmentByTag(tag);
        }
        return null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String currentTab = getCurrentTabTag();
//        LogUtils.d("TAB", "currentTab:" + currentTab);

        // Go through all tabs and make sure their fragments match
        // the correct state.
        FragmentTransaction ft = null;
        for (int i = 0; i < mTabs.size(); i++) {
            TabInfo tab = mTabs.get(i);
            tab.fragment = mFragmentManager.findFragmentByTag(tab.tag);
            if (tab.fragment != null && !tab.fragment.isDetached()) {
                if (tab.tag.equals(currentTab)) {
                    // The fragment for this tab is already there and
                    // active, and it is what we really want to have
                    // as the current tab. Nothing to do.
                    mLastTab = tab;
//                    LogUtils.d("TAB", "lastTab:" + mLastTab.tag);
                } else {
                    // This fragment was restored in the active state,
                    // but is not the current tab. Deactivate it.
                    if (ft == null) {
                        ft = mFragmentManager.beginTransaction();
                    }
//                    LogUtils.d("TAB", "detachTab:" + tab.tag);
                    ft.detach(tab.fragment);
                }
            }
        }

        // We are now ready to go. Make sure we are switched to the
        // correct tab.
        mAttached = true;
        ft = doTabChanged(currentTab, ft);
        if (ft != null) {
//            ft.commit();
            ft.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttached = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.curTab = getCurrentTabTag();
        Bundle args = new Bundle();
        for(int i = 0; i < mTabs.size(); i++) {
            args.putBundle("argument" + i, mTabs.get(i).args);
        }
        ss.bundle = args;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if(ss.bundle != null) {
            for(int i = 0; i < mTabs.size(); i++) {
                if(ss.bundle.getBundle("argument" + i) != null) {
                    mTabs.get(i).args.clear();
                    mTabs.get(i).args.putAll(ss.bundle.getBundle("argument" + i));
                }
            }
        }
        setCurrentTabByTag(ss.curTab);
        LogUtils.v(this.getClass().getName() + " onRestoreInstanceState(): ");
    }

    @Override
    public void onTabChanged(String tabId) {
        if (mAttached) {
            FragmentTransaction ft = doTabChanged(tabId, null);
            if (ft != null) {
//                ft.commit();
                ft.commitAllowingStateLoss();
            }
        }
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(tabId);
        }
    }

    private FragmentTransaction doTabChanged(String tabId, FragmentTransaction ft) {
        if(TextUtils.isEmpty(tabId)) {
            return null;
        }
        TabInfo newTab = null;
        int tabIndex = 0;
        for (int i = 0; i < mTabs.size(); i++) {
            TabInfo tab = mTabs.get(i);
            if (tab.tag.equals(tabId)) {
                newTab = tab;
                tabIndex = i;
//                LogUtils.d("TAB", "newTab:" + newTab.tag);
            }
        }

        if (newTab == null) {
            throw new IllegalStateException("No tab known for tag " + tabId);
        }
        if (mLastTab != newTab) {
            if (ft == null) {
                ft = mFragmentManager.beginTransaction();
            }
            if (mLastTab != null) {
//                LogUtils.d("TAB", "lastTab:" + mLastTab.tag);
                if (mLastTab.fragment != null) {
//                    ft.detach(mLastTab.fragment);
                    ft.hide(mLastTab.fragment);
                }
            }
            if (newTab != null) {
//                LogUtils.d("TAB", "newTab:" + newTab.tag);
                if (newTab.fragment == null || newTab.fragment.getView() == null) {
                    newTab.fragment = Fragment.instantiate(mContext,
                            newTab.clss.getName(), newTab.args);
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
//                    LogUtils.d("TAB", "addTab:" + newTab.tag);
                } else {
//                    ft.attach(newTab.fragment);
                    if(!newTab.fragment.isAdded()) {
                        ft.add(newTab.fragment, newTab.tag);
                    }
                    ft.show(newTab.fragment);
//                    LogUtils.d("TAB", "attachTab:" + newTab.tag);
                }
            }
            mLastTab = newTab;
            Log.d("changxin", "tabIndex:" + tabIndex);
        }
        return ft;
    }

    @Override
    public void setCurrentTab(int index){
        if((null!=mOnTabChangeIntercept)&&(mOnTabChangeIntercept.OnBeforeTabIntercept(index)))
            return;
        super.setCurrentTab(index);
    }

    public interface onTabChangeIntercept{
         boolean OnBeforeTabIntercept(int index);
    }

    onTabChangeIntercept mOnTabChangeIntercept;
    public void setOnTabChangeIntercept(onTabChangeIntercept ontabChangeIntercept){
        this.mOnTabChangeIntercept=ontabChangeIntercept;
    }
}
