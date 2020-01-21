package com.huatu.handheld_huatu.business.lessons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.MyBuyCourselistAdapter;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.highmianshou.HuaTuXieYiActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.CourseTypeEnum;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.ClearEditText;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.InputMethodUtils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\28 0028.
 */

public class MyCourseSearchFragment extends ABaseListFragment<BuyCourseListResponse> implements OnRecItemClickListener, TextWatcher {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;

    MyBuyCourselistAdapter mListAdapter;

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

        FragmentParameter parameter = new FragmentParameter(MyCourseSearchFragment.class, Args);
        int[]  mAnimationDefaultRes = new int[]{0, R.anim.slide_still,
                R.anim.slide_still, android.R.anim.fade_out};
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
         View topView=inflater.inflate(R.layout.topbar_general_type5,container,false);
        container.addView(topView);
        mRightBtn=(TextView)topView.findViewById(R.id.tv_right_topbar);
        mEditText=(ClearEditText)topView.findViewById(R.id.et_search_topbar);
        mEditText.setHint("搜索我的课程或老师名字");
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
                 if("0".equals(v.getTag()))
                    onGoBack();
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
        mListResponse = new BuyCourseListResponse();
        mListResponse.mCourselist = new ArrayList<>();
        mListAdapter = new MyBuyCourselistAdapter(getContext(), mListResponse.mCourselist,true);
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
       // CourseApiService.getApi().searchMyCourseList(keyword, 0,offset).enqueue(getCallback());

        CourseApiService.getApi().getMyRecyleCourses(keyword,0,"","",1,"","",offset,limit).enqueue(getCallback());
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
            mListAdapter.clearAndRefresh();
          //  mListAdapter.notifyDataSetChanged();
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

  /*  @Override
    public void onSuccess(MyCourseListResponse response)   {
        if(!ArrayUtils.isEmpty(response.getListResponse())){
            for(Lessons curLession :response.getListResponse()) {
                curLession.lSaleStart = Method.parseInt(curLession.saleStart);
                curLession.lSaleEnd = Method.parseInt(curLession.saleEnd);
            }
        }
        super.onSuccess(response);
    }*/
    @Override
    protected void onRefreshCompleted(){
        if(null!=myPeopleListView) myPeopleListView.onRefreshComplete();
   }

    @Override
    public  void onItemClick(int position,View view,int type){
        BuyCourseBean.Data lesson = mListAdapter.getCurrentItem(position);
        if(lesson==null) {
            return;
        }
        if (!NetUtil.isConnected()) {
             ToastUtils.showShort("网络错误，请检查您的网络");
             return;
        }
        switch (type) {
            case EventConstant.EVENT_MORE:
                if (lesson.oneToOne == 1) {
                    showOneToOneDlg(lesson);
                    return;
                }else if (lesson.oneToOne == 2) {
                    startOneToOne(lesson, false);
                    return;
                }
                break;

            case EventConstant.EVENT_ALL:
                if (!TextUtils.isEmpty(lesson.protocolUrl)) {
                    HuaTuXieYiActivity.newIntent(getActivity().getApplicationContext()
                            , lesson.protocolUrl);
                    return;
                }
                if (lesson.oneToOne == 1) {
                    showOneToOneDlg(lesson);
                    return;
                }/* else if (mineItem.oneToOne == 2) {
                    startOneToOne(mineItem, false);
                    return;
                }*/
                if(lesson.isExpired){
                    alertOutDateDialog();
                    return;
                }
                if(lesson.classType == CourseTypeEnum.RECORDING.getValue()){
                    Intent intent2 = new Intent(getActivity(), BJRecordPlayActivity.class);
                    intent2.putExtra("current", -1);
                    intent2.putExtra("classid", String.valueOf(lesson.rid));
                    intent2.putExtra(ArgConstant.FOR_RESUTL, true);

                    startActivityForResult(intent2,11200);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("classid", String.valueOf(lesson.rid));
                    intent.setClass(getActivity(), BJRecordPlayActivity.class);
                    intent.putExtra(ArgConstant.TYPE, 1);
                    intent.putExtra(ArgConstant.FOR_RESUTL, true);
                    startActivityForResult(intent,11201);
                }
                break;
        }
    }

    protected void alertOutDateDialog() {
    /*    final QMUITipDialog  tipDialog = new QMUITipDialog.CustomBuilder(getContext())
                .setContent( R.layout.course_outdate_popwin)
                .create(); */
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                .setTipWord("课程已过期，学习一下其他课程吧")
                .create();
        tipDialog.show();
        myPeopleListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 3000);
    }

    CustomConfirmDialog confirmDialog;
    private void showOneToOneDlg(final BuyCourseBean.Data item) {
        if (item == null) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = DialogUtils.createExitConfirmDialog(getActivity(), null,
                    "此课程包含1对1内容，请尽快填写学员信息。学员可通过填写信息卡预约上课时间，" +
                            "上课老师等。若90天未填写学员信息卡，课程将失效且无法退款。", "取消", "去填写");
            confirmDialog.setContentGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOneToOne(item, true);
            }
        });
        if(confirmDialog != null) {
            confirmDialog.show();
        }
    }

    private void startOneToOne(final BuyCourseBean.Data item, boolean isEdit) {
        //OneToOnCourseInfoFragment fragment = new OneToOnCourseInfoFragment();
        Bundle arg = new Bundle();
        arg.putBoolean("is_edit", isEdit);
        arg.putString("course_id", item.classId);
        arg.putString("course_name", item.title);
        arg.putString("order_number", item.orderNum);
        // fragment.setArguments(bundle);
        // startFragmentForResult(fragment);

        BaseFrgContainerActivity.newInstance(UniApplicationContext.getContext(),
                OneToOnCourseInfoFragment.class.getName(), arg);
    }
}
