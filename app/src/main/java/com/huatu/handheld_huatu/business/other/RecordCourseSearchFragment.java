package com.huatu.handheld_huatu.business.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.RecordingCourseAdapter;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.RecordCourseListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.ClearEditText;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.InputMethodUtils;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\28 0028.
 */

public class RecordCourseSearchFragment extends ABaseListFragment<RecordCourseListResponse> implements OnRecItemClickListener, TextWatcher {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;

    RecordingCourseAdapter mListAdapter;

    // private String orderId = "1";                           // 最终排序Id
    private int mCategoryId= 0;                             // 最终考试Id
    private String mSubjectId="";                           // 最终科目Id


    private CompositeSubscription mCompositeSubscription;

    protected String keyword = "";

    protected String mOrderId = "";
    protected int mPriceId = 1000;
    private ClearEditText mEditText;
    private TextView mRightBtn;
    private  int mType;//0 为录播，1为直播

    public static void lanuch(Context context,int categoryId,String subjectId,String orderId){
        Bundle Args=new Bundle();
        Args.putInt(ArgConstant.KEY_ID,categoryId);
        Args.putString(ArgConstant.TYPE,subjectId);
        Args.putString(ArgConstant.KEY_TITLE,orderId);

        FragmentParameter parameter = new FragmentParameter(RecordCourseSearchFragment.class, Args);
        int[]  mAnimationDefaultRes = new int[]{0, R.anim.slide_still,
                R.anim.slide_still, R.anim.dialog_fade_out};
        parameter.setAnimationRes(mAnimationDefaultRes);
        UIJumpHelper.jumpFragment(context,parameter);
    }

    @Override
    protected void parserParams(Bundle arg){
        mCategoryId=arg.getInt(ArgConstant.KEY_ID);
        mSubjectId=arg.getString(ArgConstant.TYPE);
        mOrderId=arg.getString(ArgConstant.KEY_TITLE);
        mType=arg.getBoolean(ArgConstant.IS_LIVE,false)? 1:0;
    }

    @Override
    protected int getLimit() {  return 20; }

    private Runnable showInputRunable=new Runnable() {
        @Override
        public void run() {
            if(mEditText!=null)
            InputMethodUtils.openKeybord(mEditText, mEditText.getContext());
        }
    };

    @Override
    public boolean attachTitleBar(LayoutInflater inflater, ViewGroup container) {
        final View topView=inflater.inflate(R.layout.topbar_general_type5,container,false);
        container.addView(topView);
        mRightBtn=(TextView)topView.findViewById(R.id.tv_right_topbar);
        mEditText=(ClearEditText)topView.findViewById(R.id.et_search_topbar);
        mEditText.setHint("搜索录播课程或老师名字");
        mEditText.addTextChangedListener(this);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if(!NetUtil.isConnected()){
                        ToastUtils.showShort("当前网络不可用");
                        return true;
                    }
                    String keyword = mEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword)) {
                        doSearch(keyword,true);
                        InputMethodUtils.hideMethod(getContext(), mEditText);
                    }
                    return true;
                }
                return false;
             }
         });
        if(mEditText!=null){
            InputMethodUtils.showMethodDelayed(getContext(),mEditText,showInputRunable,200);
        }
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(v.getTag())){
                    topView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.search_bar_exit));
                    onGoBack();
                }
                else {
                    if(!NetUtil.isConnected()){
                        ToastUtils.showShort("当前网络不可用");
                        return;
                    }
                    InputMethodUtils.hideMethod(getContext(), mEditText);
                    doSearch("",false);
                }
            }
        });
        topView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.search_bar_enter));
        return true;
    }

    @Override
    public int getContentView() {
        return R.layout.comm_ptrlist_layout2;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return myPeopleListView.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(  inflater,   savedInstanceSate);
        mListResponse = new RecordCourseListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new RecordingCourseAdapter(getContext(), mListResponse.mLessionlist);
        mListAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void superOnRefresh(){
        super.onRefresh();
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e("onTextChanged", "onTextChanged");
        if (charSequence.length() > 0) {
            if ("0".equals(mRightBtn.getTag())) {
                mRightBtn.setText(R.string.search);
                mRightBtn.setTag("1");
            }
            keyword=String.valueOf(charSequence);
             Log.e("keyword", charSequence+"");
        } else {
            keyword="";
            if ("1".equals(mRightBtn.getTag())) {
                mRightBtn.setText(R.string.netschool_dialog_cancel);
                mRightBtn.setTag("0");
            }
        }
    }

    @Override
    public void setListener() {
        myPeopleListView.setCanPull(false);
        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });

    }

    private void doSearch(String keyword,boolean needSet) {
        if(needSet){
            mEditText.setText(keyword);
            mEditText.setSelection(keyword.length());
        }

        mRightBtn.setText(R.string.netschool_dialog_cancel);
        mRightBtn.setTag("0");
        myPeopleListView.setCanPull(true);
        showFirstLoading();
        superOnRefresh();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setVisibility(View.GONE);
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        myPeopleListView.getRefreshableView().setPagesize(getLimit());
        myPeopleListView.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);

        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));
    }

    @Override
    public void requestData() {
        super.requestData();
        //if(isFirstLoad())   onFirstLoad();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getVodListCourse(offset, mCategoryId, mSubjectId, mOrderId, keyword).enqueue(getCallback());
    }


    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if(mListAdapter.getItemCount()<=0){
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        if(null!=mListAdapter)
            mListAdapter.clearCountDownTask();
        if(mEditText!=null){
            mEditText.removeCallbacks(showInputRunable);
            mEditText.removeTextChangedListener(this);
            mEditText.setOnEditorActionListener(null);
        }
    }

    @Override
    protected void onGoBack(){
        super.onGoBack();
        InputMethodUtils.hideMethod(getContext(), getContainerView());
    }

    @Override
    public void onSuccess(RecordCourseListResponse response)   {
        if(!ArrayUtils.isEmpty(response.getListResponse())){
            long beginTime= CountDownTask.elapsedRealtime();//毫秒级
            for(Lessons curLession :response.getListResponse()) {
                curLession.lSaleStart = Method.parseInt(curLession.saleStart);
                curLession.lSaleEnd = Method.parseInt(curLession.saleEnd);
                if(curLession.lSaleStart>0){
                    curLession.lSaleStart=beginTime+curLession.lSaleStart*1000;
                }
                if(curLession.lSaleEnd>0){
                    curLession.lSaleEnd=beginTime+curLession.lSaleEnd*1000;
                }
            }
        }
        super.onSuccess(response);
    }
    @Override
    protected void onRefreshCompleted(){
        if(null!=myPeopleListView) myPeopleListView.onRefreshComplete();
   }

    @Override
    public  void onItemClick(int position,View view,int type){
        switch (type) {
            case EventConstant.EVENT_ALL:
                Lessons lesson = mListAdapter.getCurrentItem(position);
                if(lesson==null) {
                    return;
                }
                Intent intent;
                if (NetUtil.isConnected()) {
                    if (lesson.isCollect == 1) {
                        CourseCollectSubsetFragment.show(getActivity(),
                                lesson.collectId, lesson.ShortTitle, lesson.title,0);
                    } else if (lesson.isSeckill == 1) {
                        BaseFrgContainerActivity.newInstance(getActivity(),
                                SecKillFragment.class.getName(),
                                SecKillFragment.getArgs(lesson.rid, lesson.title,false));
                    } else {
//                        intent = new Intent(getActivity(), BuyDetailsActivity.class);
                        intent = new Intent(getActivity(), BaseIntroActivity.class);
                        intent.putExtra("rid", lesson.rid);
                        intent.putExtra("NetClassId", lesson.NetClassId);//lesson.NetClassId
                        intent.putExtra("course_type",lesson.iszhibo);
                        intent.putExtra("price",lesson.ActualPrice);
                        intent.putExtra("originalprice",lesson.Price);
                        intent.putExtra("saleout",lesson.isSaleOut);
                        intent.putExtra("rushout",lesson.isRushOut);
                        intent.putExtra("daishou",lesson.isTermined);
                        intent.putExtra("collageActiveId",lesson.collageActiveId);
                        startActivityForResult(intent, 10001);
                    }
                } else {
                    ToastUtils.showShort("网络错误，请检查您的网络");
                }
                break;
        }
    }
}
