package com.huatu.handheld_huatu.business.lessons;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.ADelayStripTwoTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTwoTabsFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseCalenderFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2018\6\29 0029.
 */
@Deprecated
public class MyAllCourseFragment  extends ADelayStripTwoTabsFragment<AStripTwoTabsFragment.StripTabItem> {

    @Override
    public int getContentView() {
        return R.layout.comm_ui_twotabs;
    }

    @Override
    protected int delayGenerateTabs() {
        return 100;
    }

    protected boolean isRecylerView(){
        return false;
    }

    private boolean mHasTypeInit=false;
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

 /*  public static void lanuch(Context mContext, int pos) {
    *//*    Bundle args = new Bundle();
        args.putInt(SET_INDEX, pos);
        //XLog.e("lanuch", "lanuch" + pos);
        FragmentParameter tmpPar = new FragmentParameter(MyAllCourseFragment.class, args);
        UIJumpHelper.jumpFragment(mContext, tmpPar);*//*
        UIJumpHelper.jumpFragment(mContext, MySingleTypeCourseFragment.class);
    }*/

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
      //  EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);

        getTitleBar().setShadowVisibility(View.GONE);
        setTitle("我的课程");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(viewPager!=null) viewPager.setScrollable(false);
    }

    @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {
        super.onCreateTitleBarMenu(titleBar, container);
        if(!isRecylerView()){
            titleBar.addIcon(R.mipmap.course_search_icon, android.R.id.button1);
            titleBar.addIcon(R.mipmap.course_recyle_icon, android.R.id.button2);
            titleBar.addIcon(R.mipmap.course_calendar_icon, android.R.id.button3);
        }
    }

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if(isRecylerView()) return;
        if (menuItem.getId() == android.R.id.button1) {
            onClickSearch();
        }
        else if (menuItem.getId() == android.R.id.button2) {

            MyAllRecyleCourseFragment.lanuch(getContext(),viewPager.getCurrentItem());
          //  UIJumpHelper.jumpFragment(getContext(),MyRecylerCourseFragment.class);
        }
       else if (menuItem.getId() == android.R.id.button3) {
          //  CourseCalenderFragment calenderFragment = new CourseCalenderFragment();
          //  startFragmentForResult(calenderFragment);
            Bundle bundle = new Bundle();
            BaseFrgContainerActivity.newInstance(UniApplicationContext.getContext(),
                    CourseCalenderFragment.class.getName(), bundle);
        }
    }

    private void onClickSearch() {
       // CourseSearchMineFragment fragment = new CourseSearchMineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("course_type", 0);
        //fragment.setArguments(bundle);

        BaseFrgContainerActivity.newInstance(UniApplicationContext.getContext(),
                CourseSearchMineFragment.class.getName(), bundle);
        //startFragmentForResult(fragment, 1002);
    }


   /* @Subscribe
    public void onEventMainThread(CloseOrderEvent event) {
        if (this.getActivity() != null && event.type == 2)
            this.getActivity().finish();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
      //  EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!mHasTypeInit){
            loadType(savedInstanceState);
        }
    }

    private void loadType(final Bundle savedInstanceState){
        mCompositeSubscription= RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ServiceProvider.getVodCourseCategoryList(mCompositeSubscription, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort("分类加载出错");
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                List<CourseCategoryBean> allType = model.data;
                if(!ArrayUtils.isEmpty(allType)){
                    IoExUtils.saveJsonFile(GsonUtil.toJsonStr(allType), Constant.ALL_COUSRE_TYPE);

                    ArrayList<StripTabItem> items = new ArrayList<StripTabItem>();
                    for (CourseCategoryBean bean : allType) {
                        items.add(new StripTabItem(StringUtils.valueOf(bean.cateId), bean.name));
                    }
                    mHasTypeInit=true;
                    setTab(savedInstanceState,items);
                }else
                    ToastUtils.showShort("分类加载出错~");

            }
        });
    }

    @Override
    protected ArrayList<StripTabItem> generateTabs() {
        String jsonStr = IoExUtils.getJsonString(Constant.ALL_COUSRE_TYPE);
        if (!TextUtils.isEmpty(jsonStr)) {

            List<CourseCategoryBean> allType=GsonUtil.jsonToList(jsonStr,new GsonUtil.TypeToken<List<CourseCategoryBean>>(){}.getType());
            if (!ArrayUtils.isEmpty(allType)) {
                ArrayList<StripTabItem> items = new ArrayList<StripTabItem>();
                for (CourseCategoryBean bean : allType) {
                    items.add(new StripTabItem(StringUtils.valueOf(bean.cateId), bean.name));
                }
                mHasTypeInit = true;
                return items;
            }
            return null;
        }
        return null;
    }

    @Override
    protected void setTab(final Bundle savedInstanceSate, ArrayList<AStripTwoTabsFragment.StripTabItem> mTypes) {
        if(!mHasTypeInit) return;
        super.setTab(savedInstanceSate,mTypes);
    }

    @Override
    protected Fragment newFragment(StripTabItem bean) {
         if (bean != null) {
//            if (StringUtils.parseInt(bean.getType()) == PayStatusEnums.DEFAULT.getAction())
//                return OrderToBePaidFragment.getInstance(StringUtils.parseInt(bean.getType()));
            return MySingleTypeCourseFragment.getInstance(StringUtils.parseInt(bean.getType()));
//            return UserAllOrderSubFragment.getInstance(StringUtils.parseInt(bean.getType()));
        }
        return null;
    }

    private void showGoMarket(){
        int buyCount= PrefStore.getSettingInt("BuyDetailsActivity.TAG",0);
        int hasShow=  PrefStore.getSettingInt("market_has_show",0);

        LogUtils.e("showGoMarket",buyCount+","+hasShow);
        if(buyCount==2&&(hasShow==0)){
            PrefStore.putSettingInt("BuyDetailsActivity.TAG",(buyCount+1));
            PrefStore.putSettingInt("market_has_show",1);
            DialogUtils.onShowMarketDialog(getActivity());
        }
    }
}
