package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamReportActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamReportExActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.me.bean.RecordTypeData;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.mvpmodel.exercise.PaperBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.mvpmodel.me.RecordBean;
import com.huatu.handheld_huatu.mvpmodel.me.RecordDataBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.kankan.wheel.widget.OnWheelChangedListener;
import com.kankan.wheel.widget.WheelView;
import com.kankan.wheel.widget.adapters.ArrayWheelAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 答题记录
 */
public class RecordActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_prompt;
    private ImageView image_empty;
    private TextView text_faile;
    private ProgressBar progress_bar;
    //    private RelativeLayout rl_left_topbar;
    private ImageView ivBack;
    private LinearLayout ll_choose;
    private LinearLayout ll_choose_type;
    private TextView text_type_name;
    private ImageView image_type_select;
    private LinearLayout ll_choose_time;
    private TextView text_time;
    private ImageView image_time_select;
    private SwipeRefreshLayout refreshlayout;
    private ListView listview;
    private View view_line2;
    private PopupWindow typePw;
    private View view_blank;
    private WheelView mViewYear;
    private WheelView mViewMonth;
    private PopupWindow timeSel;
    private TextView text_delete;
    private TextView text_ok;
    private TextView tv_title_titlebar;
    private RecordAdapter recordAdapter;
    private TypeAdapter typeAdapter;
    private View loadView;
    private CustomDialog deleteDialog;
    private CustomDialog netDialog;
    private String mYearDatas[] = new String[3];
    private String mMonthDatas[] = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private Calendar calendar;
    private int currentYear, current_mon;
    private int yearItem;
    private boolean isLoadEnd = true;

    private int requestType;
    private Bundle extraArgs = null;
    private int cardType = 0;
    private String cardTime = "";
    private long cursor = 0;
    private int total;

    private List<RecordTypeData> mData;

    private int selectPosition = 0;

    private boolean typeFlag = true;
    private boolean timeFlag = true;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_record;
    }

    @Override
    protected void onInitView() {
        if (originIntent != null) {
            requestType = originIntent.getIntExtra("request_type", 0);
            extraArgs = originIntent.getBundleExtra("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
        }
        LogUtils.d(TAG, "requestType:" + requestType);
        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        current_mon = calendar.get(Calendar.MONTH) + 1;
        for (int i = 0; i < 3; i++) {
            mYearDatas[i] = String.valueOf(currentYear - 3 + i + 1) + "年";
        }
        yearItem = mYearDatas.length - 1;

        tv_title_titlebar = (TextView) findViewById(R.id.tv_title_titlebar);
        ll_prompt = (LinearLayout) findViewById(R.id.ll_prompt);
        image_empty = (ImageView) findViewById(R.id.image_empty);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        text_faile = (TextView) findViewById(R.id.text_faile);

        view_line2 = findViewById(R.id.view_line2);

//        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        ivBack = (ImageView) findViewById(R.id.iv_back);

        ll_choose = (LinearLayout) findViewById(R.id.ll_choose);
        ll_choose_type = (LinearLayout) findViewById(R.id.ll_choose_type);
        text_type_name = (TextView) findViewById(R.id.text_type_name);
        image_type_select = (ImageView) findViewById(R.id.image_type_select);

        ll_choose_time = (LinearLayout) findViewById(R.id.ll_choose_time);
        text_time = (TextView) findViewById(R.id.text_time);
        image_time_select = (ImageView) findViewById(R.id.image_time_select);

        loadView = View.inflate(RecordActivity.this, R.layout.load_record_view, null);
        refreshlayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);
        listview = (ListView) findViewById(R.id.listview);
        listview.addFooterView(loadView);
        recordAdapter = new RecordAdapter();
        typeAdapter = new TypeAdapter();
        listview.setAdapter(recordAdapter);
        listview.removeFooterView(loadView);

        initDataSetView();

        show_loadData();
        loadRecordType();
        loadData(true, "22");
        setListener();
    }

    private void loadRecordType() {
        ServiceProvider.getRecordTypeData(compositeSubscription, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                if (model != null) {
                    mData = model.data;
                }
                if (mData != null) {
                    mTypeData.addAll(mData);
                    typeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("网络错误，请稍后重试");
            }
        });
    }

    private void initDataSetView() {

        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN) {
            cardType = ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN;
            ll_choose.setVisibility(View.GONE);
            tv_title_titlebar.setText("模考历史");
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN) {
            cardType = ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN;
            ll_choose.setVisibility(View.GONE);
            tv_title_titlebar.setText("估分历史");
        } else {
            cardType = 0;
            ll_choose.setVisibility(View.VISIBLE);
            tv_title_titlebar.setText("答题记录");
//            if(SignUpTypeDataCache.getInstance().getSignUpType() == Type.SignUpType.CIVIL_SERVANT){
//                typeName = typeNameGwy;
//                typeId=typeIdGwy;
//            }else if(SignUpTypeDataCache.getInstance().getSignUpType() == Type.SignUpType.PUBLIC_INSTITUTION){
//                typeName = typeNameSydw;
//                typeId=typeIdSydw;
//            }else if(SignUpTypeDataCache.getInstance().getSignUpType() == Type.SignUpType.PUBLIC_SECURITY){
//                typeName = typeNameGAZJ;
//                typeId=typeIdGAZJ;
//            }else {

//    }
        }
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    private void setListener() {
        ll_choose_type.setOnClickListener(this);
        ll_choose_time.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordActivity.this.finish();
            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            isLoadEnd = false;
                            listview.addFooterView(loadView);
                            loadData(false, "22");
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                RealExamBeans.RealExamBean recordResultBean = (RealExamBeans.RealExamBean) recordAdapter.getItem(position);
                showDialog(recordResultBean, position);
                return true;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                RealExamBeans.RealExamBean recordResultBean = (RealExamBeans.RealExamBean) recordAdapter.getItem(position);

                long id = recordResultBean.id;
                int type = recordResultBean.type;
                int status = recordResultBean.status;

                Bundle bundle = new Bundle();

                bundle.putLong("practice_id", id);
                if (recordResultBean.paper != null && recordResultBean.paper.modules != null
                        && recordResultBean.paper.modules.size() > 0) {
                    int category = recordResultBean.paper.modules.get(0).category;
                    bundle.putLong("point_ids", category);
                }
                //试卷状态 1:新建,2:未做完,3:已做完,4:删除
                if (status == 1 || status == 2) {       // 未完成
                    bundle.putBoolean("continue_answer", true);
                    ArenaExamActivityNew.show(RecordActivity.this, type, bundle);
                } else {                                // 已完成 查看报告，背题-去查看全部解析
                    if (type == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                            || type == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {    // 专项练习 错题练习 背题模式获取全部题目，然后去查看 相当于直接去全部解析，试题在解析页面获取
                        ArenaDataCache.getInstance().realExamBean = recordResultBean;
                        bundle.putInt("showIndex", 0);
                        bundle.putInt("fromType", type);
                        ArenaExamActivityNew.show(RecordActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, bundle);
                    } else if (type == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI
                            || type == ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN
                            || type == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE
                            || type == ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI
                            || type == ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT) {
                        ArenaExamReportExActivity.show(RecordActivity.this, type, bundle);
                    } else {
                        ArenaExamReportActivity.show(RecordActivity.this, type, bundle);
                    }
                }
            }
        });

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                show_loadSuccess();
                refreshlayout.setRefreshing(true);
                resultList.clear();
                recordAdapter.notifyDataSetChanged();
                loadData(true, "22");
            }
        });
    }

    private void showDialog(final RealExamBeans.RealExamBean recordResultBean, final int position) {
        deleteDialog = new CustomDialog(RecordActivity.this, R.layout.dialog_delete_record);
        TextView text_ok = (TextView) deleteDialog.mContentView.findViewById(R.id.text_ok);
        TextView text_cancel = (TextView) deleteDialog.mContentView.findViewById(R.id.text_cancel);

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long id = recordResultBean.id;
                deleteItem(id, position);
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void deleteItem(long id, final int position) {
        deleteDialog.dismiss();
        netDialog = new CustomDialog(RecordActivity.this, R.layout.dialog_feedback_commit);
        netDialog.show();

        Subscription deleteSubscription = RetrofitManager.getInstance().getService().deleteRecordItem(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DeleteResponseBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        netDialog.dismiss();
                        CommonUtils.showToast("删除答题记录失败");
                    }

                    @Override
                    public void onNext(DeleteResponseBean responseBean) {
                        netDialog.dismiss();
                        long code = responseBean.code;
                        if (code == 1000000) {
                            changeShowItemLists(position);
                            CommonUtils.showToast("删除答题记录成功");
                        } else {
                            CommonUtils.showToast("删除答题记录失败");
                        }
                    }
                });

        compositeSubscription.add(deleteSubscription);
    }

    private void changeShowItemLists(int position) {
        if (resultList != null && resultList.size() > 0) {
            resultList.remove(position);
            recordAdapter.notifyDataSetChanged();
            if (resultList != null && resultList.size() == 0) {
                show_loadFaile("没有答题记录数据了");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cursor = 0;
        loadData(false, "11");
    }

    private void loadData(final boolean flag, final String str) {
        if (!NetUtil.isConnected()) {
            refreshlayout.setRefreshing(false);
            isLoadEnd = true;
            if (flag) {
                show_loadFaile("不好了，网络被外星人绑架了");
            } else {
                CommonUtils.showToast("无网络，请检查网络连接");
            }
            return;
        }

        if (flag) {
            cursor = 0;
        }

        Subscription subscription = RetrofitManager.getInstance().getService().getRecordList(cardType, cardTime, cursor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RecordBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshlayout.setRefreshing(false);
                        isLoadEnd = true;
                        listview.removeFooterView(loadView);

                        if (flag) {
                            show_loadFaile("获取答题记录失败");
                        } else {
                            CommonUtils.showToast("获取答题记录失败");
                        }
                    }

                    @Override
                    public void onNext(RecordBean recordBean) {
                        show_loadSuccess();
                        refreshlayout.setRefreshing(false);
                        isLoadEnd = true;
                        listview.removeFooterView(loadView);

                        int code = recordBean.getCode();
                        RecordDataBean data = recordBean.getData();

                        if (code == 1000000) {
                            dealwithData(data, flag, str);
                        } else if (code == 1110002) {
                            if (flag) {
                                show_loadFaile("用户会话过期");
                            } else {
                                CommonUtils.showToast("用户会话过期");
                            }
                        } else {
                            if (flag) {
                                show_loadFaile("获取答题记录失败");
                            } else {
                                CommonUtils.showToast("获取答题记录失败");
                            }
                        }
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void dealwithData(RecordDataBean data, boolean flag, String str) {
        if (data != null) {
            List<RealExamBeans.RealExamBean> result = data.getResult();
            cursor = data.getCursor();
            if (result != null && result.size() > 0) {
                if (flag || "11".equals(str)) {
                    resultList.clear();
                }
                resultList.addAll(result);
                recordAdapter.notifyDataSetChanged();
            } else {
                if (flag) {
                    show_loadFaile("没有满足条件的记录~");
                } else {
                    CommonUtils.showToast("没有更多数据了");
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_choose_type:
                if (typeFlag) {
                    typeSelect();
                    timeNormal();
                    hideTimePw();
                    showTypePw();
                } else {
                    typeNormal();
                    hideTypePw();
                }
                break;
            case R.id.ll_choose_time:
                if (timeFlag) {
                    timeSelect();
                    typeNormal();
                    hideTypePw();
                    showTimePw();
                } else {
                    timeNormal();
                    hideTimePw();
                }
                break;
            case R.id.text_delete:
                timeNormal();
                hideTimePw();

                currentYear = calendar.get(Calendar.YEAR);
                current_mon = calendar.get(Calendar.MONTH);
                text_time.setText("时间不限");
                cardTime = "";

                resultList.clear();
                recordAdapter.notifyDataSetChanged();
                show_loadData();
                loadData(true, "22");
                break;
            case R.id.text_ok:
                timeNormal();
                hideTimePw();

                cardTime = currentYear + "-" + current_mon;
                text_time.setText(currentYear + "年" + current_mon + "月");

                resultList.clear();
                recordAdapter.notifyDataSetChanged();
                show_loadData();
                loadData(true, "22");
                break;
        }
    }

    private void initTime() {
        currentYear = calendar.get(Calendar.YEAR);
        current_mon = calendar.get(Calendar.MONTH);
    }

    private void hideTypePw() {
        if (typePw != null && typePw.isShowing()) {
            typePw.dismiss();
        }
    }

    private void hideTimePw() {
        if (timeSel != null && timeSel.isShowing()) {
            timeSel.dismiss();
        }
    }

    private void typeSelect() {
        typeFlag = false;
        text_type_name.setTextColor(getResources().getColor(R.color.common_style_text_color));
        image_type_select.setBackgroundResource(R.mipmap.record_selected);
    }

    private void typeNormal() {
        typeFlag = true;
        text_type_name.setTextColor(getResources().getColor(R.color.gray010));
        image_type_select.setBackgroundResource(R.mipmap.record_normal);
    }

    private void timeSelect() {
        timeFlag = false;
        text_time.setTextColor(getResources().getColor(R.color.common_style_text_color));
        image_time_select.setBackgroundResource(R.mipmap.record_selected);
    }

    private void timeNormal() {
        timeFlag = true;
        text_time.setTextColor(getResources().getColor(R.color.gray010));
        image_time_select.setBackgroundResource(R.mipmap.record_normal);
    }

    private void showTimePw() {
        View view = View.inflate(RecordActivity.this, R.layout.popupwindow_record_time, null);

        view_blank = view.findViewById(R.id.view_blank);
        mViewYear = (WheelView) view.findViewById(R.id.id_year);
        mViewMonth = (WheelView) view.findViewById(R.id.id_month);
        text_delete = (TextView) view.findViewById(R.id.text_delete);
        text_ok = (TextView) view.findViewById(R.id.text_ok);

        mViewYear.setViewAdapter(new ArrayWheelAdapter<String>(RecordActivity.this, mYearDatas));
        mViewYear.setVisibleItems(7);
        mViewMonth.setVisibleItems(7);
        updateMonth();
        mViewYear.setCurrentItem(yearItem);
        mViewMonth.setCurrentItem(current_mon - 1);

        text_delete.setOnClickListener(this);
        text_ok.setOnClickListener(this);
        view_blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTimePw();
                timeNormal();
            }
        });

        mViewYear.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                yearItem = mViewYear.getCurrentItem();
                String mYearData = mYearDatas[yearItem];
                String yearNum = mYearData.replace("年", "");
                currentYear = Integer.valueOf(yearNum);
            }
        });

        mViewMonth.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int currentItem = mViewMonth.getCurrentItem();
                String mMonthData = mMonthDatas[currentItem];
                String monthNum = mMonthData.replace("月", "");
                current_mon = Integer.valueOf(monthNum);
            }
        });

        timeSel = new PopupWindow(view);
        timeSel.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        timeSel.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        timeSel.setClippingEnabled(false);
//        timeSel.showAsDropDown(rl_left_topbar, 0,0);
        timeSel.showAtLocation(ivBack, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        timeSel.update();
    }

    private int selectYear;
    private int selecetMonth;

    private void updateMonth() {
        mViewMonth.setViewAdapter(new ArrayWheelAdapter<String>(this, mMonthDatas));
        mViewMonth.setCurrentItem(0);
    }

    private void showTypePw() {
        View view = View.inflate(RecordActivity.this, R.layout.popupwindow_record_type, null);
        ListView pwlistview = (ListView) view.findViewById(R.id.listview);
        View view_pw = view.findViewById(R.id.view_pw);
        view_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideTypePw();
                typeNormal();
            }
        });
        TypeAdapter typeAdapter = new TypeAdapter();
        pwlistview.setAdapter(typeAdapter);

        typePw = new PopupWindow(view);
        typePw.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= 24) {
            LogUtils.d("RecordActivity", "Build.VERSION.SDK_INT >= 24");
            Rect rect = new Rect();
            view_line2.getGlobalVisibleRect(rect);
            int h = view_line2.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            typePw.setHeight(h);
        } else {
            typePw.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        }
        typePw.showAsDropDown(view_line2, 0, 0);
        typePw.update();
    }


    private List<RecordTypeData> mTypeData = new ArrayList<>();

    private class TypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTypeData.size();
        }

        @Override
        public Object getItem(int position) {
            return mTypeData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TypeHolder holder;
            if (convertView == null) {
                holder = new TypeHolder();
                convertView = View.inflate(RecordActivity.this, R.layout.item_record_type, null);

                holder.text_city_name = (TextView) convertView.findViewById(R.id.text_city_name);
                holder.image_select = (ImageView) convertView.findViewById(R.id.image_select);

                convertView.setTag(holder);
            } else {
                holder = (TypeHolder) convertView.getTag();
            }
            final RecordTypeData datas = mTypeData.get(position);
//            final String name = typeName[position];

            holder.text_city_name.setText(datas.typeName);

            if (selectPosition == position) {
                holder.image_select.setVisibility(View.VISIBLE);
                holder.text_city_name.setTextColor(getResources().getColor(R.color.common_style_text_color));
            } else {
                holder.image_select.setVisibility(View.INVISIBLE);
                holder.text_city_name.setTextColor(getResources().getColor(R.color.black001));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition = position;
                    notifyDataSetChanged();
                    cardType = datas.typeId;
                    text_type_name.setText(datas.typeName);

                    hideTypePw();
                    typeNormal();
                    resultList.clear();
                    recordAdapter.notifyDataSetChanged();
                    show_loadData();
                    loadData(true, "22");
                }
            });
            return convertView;
        }
    }

    class TypeHolder {
        TextView text_city_name;
        ImageView image_select;
    }

    private List<RealExamBeans.RealExamBean> resultList = new ArrayList<>();

    private class RecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (resultList != null && resultList.size() > 0) {
                return resultList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecordHolder holder;
            if (convertView == null) {
                holder = new RecordHolder();

                convertView = View.inflate(RecordActivity.this, R.layout.item_record_list, null);

                holder.view = convertView.findViewById(R.id.view);
                holder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                holder.text_time = (TextView) convertView.findViewById(R.id.text_time);
                holder.text_status = (TextView) convertView.findViewById(R.id.text_status);
                holder.text_num = (TextView) convertView.findViewById(R.id.text_num);
                holder.text_total = (TextView) convertView.findViewById(R.id.text_total);

                convertView.setTag(holder);
            } else {
                holder = (RecordHolder) convertView.getTag();
            }

            RealExamBeans.RealExamBean recordResultBean = resultList.get(position);
            String name = "";
            if (!TextUtils.isEmpty(recordResultBean.name)) {
                name = recordResultBean.name.trim();
            }
            int ucount = recordResultBean.ucount;
            int rcount = recordResultBean.rcount;
            if (TextUtils.isEmpty(name))
                holder.text_name.setText("");
            else
                holder.text_name.setText(name);

            int status = recordResultBean.status;
            int type = recordResultBean.type;
            if (type == 5) {
                holder.text_status.setText("");
            } else {
                if (status == 2 || status == 1) {
                    holder.text_status.setText("未完成");
                    holder.text_status.setTextColor(getResources().getColor(R.color.text_day_002));
                    holder.text_total.setVisibility(View.GONE);
                    holder.text_num.setVisibility(View.GONE);
                } else {
                    holder.text_status.setText("");
                    holder.text_total.setVisibility(View.VISIBLE);
                    holder.text_num.setVisibility(View.VISIBLE);
                }
            }

            long time = recordResultBean.createTime;
            String timeStr = DateUtils.formatAMs(time);
            holder.text_time.setText(timeStr);

            PaperBean p = recordResultBean.paper;
            if (p != null)
                holder.text_total.setText("/" + p.qcount);
            else
                holder.text_total.setText("");

            holder.text_num.setText(rcount + "");

            if (position == 0)
                holder.view.setVisibility(View.VISIBLE);
            else
                holder.view.setVisibility(View.GONE);

            return convertView;
        }
    }

    class RecordHolder {
        View view;
        TextView text_name, text_time, text_num, text_total, text_status;
    }

    public static void newInstance(Context context, int from) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra("request_type", from);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    private void show_loadData() {
        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText("获取数据中...");
    }

    private void show_loadFaile(String msg) {
        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText(msg);
    }

    private void show_loadSuccess() {
        ll_prompt.setVisibility(View.GONE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.GONE);
        text_faile.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (typePw != null && typePw.isShowing()) {
                typePw.dismiss();
                typeNormal();
                return true;
            }

            if (timeSel != null && timeSel.isShowing()) {
                timeSel.dismiss();
                timeNormal();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
