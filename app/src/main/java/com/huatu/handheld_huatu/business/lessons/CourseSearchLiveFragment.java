package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.helper.KeyboardStatusDetector;
import com.huatu.handheld_huatu.listener.SimpleTextWatcher;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.ClearEditText;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.FlowLayout;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.utils.InputMethodUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2017/10/9.
 */
@Deprecated
public class CourseSearchLiveFragment extends BaseCourseListFragment {
    @BindView(R.id.search_live_course_cancel_btn)
    TextView btnSearch;
    @BindView(R.id.search_live_course_edit)
    ClearEditText editText;
   /* @BindView(R.id.search_purchased_course_tips_tv)
    TextView tvTips;*/
    @BindView(R.id.search_live_history_layout)
    View layoutHistory;
    @BindView(R.id.search_live_hot_grid)
    FlowLayout gridView;
//    CommonAdapter<String> mAdapterHotWords;
    @BindView(R.id.search_live_history_list)
    ListViewForScroll listViewHistory;
    CommonAdapter<String> mAdapterHistory;
    @BindView(R.id.search_live_history_tips)
    View tvHistoryTips;
    View footerView;


    private boolean isChanged = false;
    private List<String> historyList = new ArrayList<>();
    private List<String> hotwordsList = new ArrayList<>();
    boolean isOpen;
    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_search_live_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        mListView.setHeaderDividersEnabled(false);
        mOrderId = args.getInt("order_id", 0);
        mPriceId = args.getInt("price_id", 1000);
        initAdapter();
        layoutErrorView.setErrorText("暂无相关课程");
        footerView = mLayoutInflater.inflate(
                R.layout.search_live_history_footer_layout, null, false);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
           @Override
           public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
              if(arg1 == EditorInfo.IME_ACTION_SEARCH)  {
                  if("取消".equals(btnSearch.getText())){

                      String tmpStr= editText.getText().toString().trim();
                      if(TextUtils.isEmpty(tmpStr)){
                          ToastUtils.showShort("请输入关键字");
                          return true;
                      }else {
                          ToastUtils.showShort("换个关键字吧");
                          return true;
                      }
                  }
                  onClickSearch();
                  return true;
               }
               return false;
             }
         });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(!TextUtils.isEmpty(editText.getText().toString().trim())) {
                        btnSearch.setText("搜索");
                    }
                    onClickSearch();
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btnSearch.setText("搜索");
                    keyword = editText.getText().toString().trim();
                } else {
                    btnSearch.setText("取消");
                }
            }
        });
        InputMethodUtils.showMethodDelayed(getContext(),editText,1000);
        layoutHistory.setVisibility(View.VISIBLE);
        listViewHistory.setAdapter(mAdapterHistory);
        listViewHistory.addFooterView(footerView);
        tvHistoryTips.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.touch_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isOpen) {
                    InputMethodUtils.closeKeybord(editText, getContext());
                    return true;
                }
                return false;
            }
        });
        KeyboardStatusDetector detector = new KeyboardStatusDetector();
        detector.registerFragment(this);
        detector.setVisibilityListener(new KeyboardStatusDetector.KeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean keyboardVisible) {
                isOpen = keyboardVisible;
            }
        });
//        gridView.setAdapter(mAdapterHotWords);
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        mActivity.showProgress();
        // ServiceProvider.getSearchLiveKeywords(compositeSubscription,
        ServiceExProvider.visit(compositeSubscription, RetrofitManager.getInstance().getService().getSearchLiveKeywords(),
                new NetObjResponse<LiveSearchKeyword>() {
                    @Override
                    public void onSuccess(BaseResponseModel<LiveSearchKeyword> model) {
                        mActivity.hideProgress();
                        LiveSearchKeyword data = (LiveSearchKeyword) model.data;
                        List<String> hotwords = data.hotwords;
                        List<String> mywords = data.mywords;
                        historyList.clear();
                        if (mywords != null) {
                            historyList.addAll(mywords);
                        }
                        if (Method.isListEmpty(historyList)) {
                            listViewHistory.setVisibility(View.GONE);
                            tvHistoryTips.setVisibility(View.GONE);
                        } else {
                            listViewHistory.setVisibility(View.VISIBLE);
                            tvHistoryTips.setVisibility(View.VISIBLE);
                        }
                        hotwordsList.clear();
                        if (hotwords != null) {
                            hotwordsList.addAll(hotwords);
                        }
                        mAdapterHistory.setDataAndNotify(historyList);
                        gridView.removeAllViews();
                        for (int i = 0; i < hotwordsList.size(); i++) {
                            View view = mLayoutInflater.inflate(R.layout.gridview_item_hotwords, null);
                            TextView tvWord = (TextView) view.findViewById(R.id.bt_grid);
                            final String strWord = hotwordsList.get(i);
                            tvWord.setText(strWord);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    editText.setText(strWord);
                                    editText.setSelection(strWord.length());
                                    btnSearch.setText("搜索");
                                    onClickSearch();
                                }
                            });
                            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.rightMargin = DisplayUtil.dp2px(15);
                            lp.bottomMargin = DisplayUtil.dp2px(15);
                            gridView.addView(view, lp);
//                    mAdapterHotWords = new CommonAdapter<String>(hotwordsList, R.layout.gridview_item_hotwords) {
//                        @Override
//                        public void convert(ViewHolder holder, final String item, int position) {
//                            holder.setText(R.id.bt_grid, item);
//                            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    editText.setText(item);
//                                    btnSearch.setText("搜索");
//                                    onClickSearch();
//                                }
//                            });
//                        }
//                    };
                        }
//                mAdapterHotWords.setDataAndNotify(hotwordsList);
                    }

                    @Override
                    public void onError(String message, int type) {
                        ToastUtils.showMessage(message);
                        mActivity.hideProgress();
                    }

                });
    }

    private void initAdapter() {
        mAdapterHistory = new CommonAdapter<String>(historyList, R.layout.course_search_lishi) {
            @Override
            public void convert(ViewHolder holder, final String item, int position) {
                holder.setText(R.id.course_search_history_tv, item);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editText.setText(item);
                        editText.setSelection(item.length());
                        onClickSearch();
                    }
                });
                holder.setViewOnClickListener(R.id.course_search_history_delete_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteHistory(item);
                    }
                });
            }
        };
//        mAdapterHotWords = new CommonAdapter<String>(hotwordsList, R.layout.gridview_item_hotwords) {
//            @Override
//            public void convert(ViewHolder holder, final String item, int position) {
//                holder.setText(R.id.bt_grid, item);
//                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        editText.setText(item);
//                        btnSearch.setText("搜索");
//                        onClickSearch();
//                    }
//                });
//            }
//        };
    }

    private void clearHistory() {
        mActivity.showProgress();
        ServiceProvider.clearLiveSearchHistory(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                ToastUtils.showShort("清除搜索历史成功");
                historyList.clear();
                mAdapterHistory.setDataAndNotify(historyList);
                listViewHistory.setVisibility(View.GONE);
                tvHistoryTips.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }
        });
    }

    private void deleteHistory(final String word) {
        mActivity.showProgress();
        ServiceProvider.deleteLiveSearchKeyword(compositeSubscription, word, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                ToastUtils.showShort("删除搜索历史成功");
                for(int i = 0; i < historyList.size(); i++) {
                    if(Method.isEqualString(word, historyList.get(i))) {
                        historyList.remove(i);
                        break;
                    }
                }
                mAdapterHistory.setDataAndNotify(historyList);
                if(Method.isListEmpty(historyList)) {
                    listViewHistory.setVisibility(View.GONE);
                    tvHistoryTips.setVisibility(View.GONE);
                } else {
                    listViewHistory.setVisibility(View.VISIBLE);
                    tvHistoryTips.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                ToastUtils.showShort("删除搜索历史失败");
            }
        });
    }

    @OnClick(R.id.search_live_course_cancel_btn)
    public void onClickSearch() {

        if("取消".equals(btnSearch.getText())) {
            finishFrg();
            return;
        }
        InputMethodUtils.hideMethod(getContext(),editText);
        layoutHistory.setVisibility(View.GONE);


       // tvTips.setVisibility(View.VISIBLE);
        String strKeyWord = new String(keyword);
        strKeyWord.replaceAll ("\r", " ");
        strKeyWord.replaceAll ("\n", " ");
        strKeyWord.replaceAll ("\t", " ");
       // tvTips.setText("在直播中搜索“" + strKeyWord + "”的结果为:");
        btnSearch.setText("取消");
        onRefresh();
        saveKeywords();
    }

    private void saveKeywords() {
       if(SpUtils.getLoginState()){
            ServiceProvider.saveSearchLiveKeywords(compositeSubscription, keyword, new NetResponse(){});
       }

    }

    private void finishFrg() {
        if(isChanged) {
            setResultForTargetFrg(Activity.RESULT_OK);
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
        }
        InputMethodUtils.hideMethod(getContext(),editText);
     }

    @Override
    public boolean onBackPressed() {
        finishFrg();
        return true;
    }

    @Override
    public void showEmptyView() {
        super.showEmptyView();
        layoutErrorView.setErrorText("没有找到相关课程");
    }
}
