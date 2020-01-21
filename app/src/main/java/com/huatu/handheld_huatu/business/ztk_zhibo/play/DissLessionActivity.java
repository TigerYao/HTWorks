package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseDiss;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DissLessionActivity extends BaseActivity implements View.OnClickListener {
    private ListView rListView;
    private RelativeLayout layout_TitleBar;
    private RelativeLayout button_TitleBar_Back;
    private TextView textView_TitleBar_Info;
    @BindView(R.id.hide_course_selector_tv)
    public TextView btnSelector;
    private RelativeLayout button_TitleBar_Operation;
    private String username;
    private ArrayList<CourseMineBean.ResultBean> mCourseMineList = new ArrayList<>();
    private CourseMineBean.ResultBean mCourseMineItem;
    private boolean isEdit = false;
    private boolean isALL = true;
    private ImageView img_select_all;
    private RelativeLayout rl_dibu;
    private TextView tv_quanxuan;
    private TextView tv_quxiao;
    private LinearLayout ll_down_no;
    private String netClassId;
    private String orderId;
    private FrameLayout fl_edit;
    private TextView button_titleBar_operations;
    private RelativeLayout tv_quanxuans;
    private CourseDissAdapter mAdapter;
    private PopupWindow mPopupWindowSelector;
    private TextView btnSelectAll;
    private TextView btnSelectLive;
    private TextView btnSelectVod;
    private CustomConfirmDialog confirmDialog;

    private int page = 1;
    private boolean isChanged = false;
    private int courseType;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_diss_lession;
    }

    @Override
    protected void onInitView() {
        courseType = originIntent.getIntExtra("course_type", 0);
        rListView = (ListView) findViewById(R.id.refreshlistview);
        layout_TitleBar = (RelativeLayout) findViewById(R.id.layout_TitleBar);
        button_TitleBar_Back = (RelativeLayout) findViewById(R.id.button_TitleBar_Back);
        textView_TitleBar_Info = (TextView) findViewById(R.id.textView_TitleBar_Info);
        button_TitleBar_Operation = (RelativeLayout) findViewById(R.id.button_TitleBar_Operation);
        button_titleBar_operations = (TextView) findViewById(R.id.button_TitleBar_Operations);
        username = SpUtils.getUname();
        img_select_all = (ImageView) findViewById(R.id.img_select_all);
        rl_dibu = (RelativeLayout) findViewById(R.id.rl_dibu);
        tv_quanxuan = (TextView) findViewById(R.id.tv_quanxuan);
        tv_quanxuans = (RelativeLayout) findViewById(R.id.tv_quanxuans);
        tv_quxiao = (TextView) findViewById(R.id.tv_quxiao);
        ll_down_no = (LinearLayout) findViewById(R.id.ll_down_no);
        fl_edit = (FrameLayout) findViewById(R.id.fl_edit);
        //事件监听
        setOnClickListener();
        mAdapter = new CourseDissAdapter(DissLessionActivity.this, mCourseMineList);
        rListView.setAdapter(mAdapter);
        //点击事件
        onitem();
        setSelectorState();
    }

    private void onitem() {
        rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                if (isEdit) {
                    int selectNum = 0;
                    mCourseMineList.get(position).setSelect(
                            !mCourseMineList.get(position).isSelect());
                    for (int i = 0; i < mCourseMineList.size(); i++) {
                        if (mCourseMineList.get(i).isSelect()) {
                            selectNum++;
                        }
                    }
                    if (mCourseMineList.size() == selectNum) {
                        img_select_all.setImageResource(R.drawable.img_down_select);
                        tv_quanxuan.setText("取消全选");
                        tv_quxiao.setTextColor(Color.parseColor("#e9304e"));
                        tv_quxiao.setClickable(true);
                        isALL = false;
                    }
                    if (selectNum > 0 && selectNum < mCourseMineList.size()) {
                        isALL = true;
                        tv_quanxuan.setText("全选");
                        img_select_all.setImageResource(R.drawable.img_down_select);
                        tv_quxiao.setTextColor(Color.parseColor("#e9304e"));
                        tv_quxiao.setClickable(true);
                    }
                    if (selectNum == 0) {
                        isALL = true;
                        img_select_all.setImageResource(R.drawable.img_down_unselect);
                        tv_quxiao.setTextColor(Color.parseColor("#999999"));
                        tv_quxiao.setClickable(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    //获取订单ID和课程ID
                    mCourseMineItem = mCourseMineList.get(position);
                    orderId = mCourseMineItem.orderId;
                    netClassId = mCourseMineItem.NetClassId;
                }
            }
        });
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        showProgress();
        ServiceProvider.getCourseDiss(compositeSubscription, page, courseType, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                CourseMineBean mineBean = (CourseMineBean) model.data;
                mCourseMineList.clear();
                mCourseMineList.addAll(mineBean.result);
                if (mCourseMineList != null && mCourseMineList.size() > 0) {
                    ll_down_no.setVisibility(View.GONE);
                    button_titleBar_operations.setVisibility(View.VISIBLE);
                    button_TitleBar_Operation.setVisibility(View.VISIBLE);
                } else {
                    ll_down_no.setVisibility(View.VISIBLE);
                    button_titleBar_operations.setVisibility(View.INVISIBLE);
                    button_TitleBar_Operation.setVisibility(View.INVISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(final Throwable e) {
                hideProgress();
                ll_down_no.setVisibility(View.VISIBLE);
                button_titleBar_operations.setVisibility(View.INVISIBLE);
                button_TitleBar_Operation.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setOnClickListener() {
        button_TitleBar_Operation.setOnClickListener(this);
        tv_quanxuans.setOnClickListener(this);
        tv_quxiao.setOnClickListener(this);
        button_TitleBar_Back.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_TitleBar_Operation:
                tv_quanxuan.setText("全选");
                img_select_all.setImageResource(R.drawable.img_down_unselect);
                if (!isEdit) {
                    button_titleBar_operations.setText("取消");
                    rl_dibu.setVisibility(View.VISIBLE);
                    tv_quxiao.setTextColor(Color.parseColor("#999999"));
                    isEdit = true;
                } else {
                    button_titleBar_operations.setText("编辑");
                    isEdit = false;

                    for (int i = 0; i < mCourseMineList.size(); i++) {
                        mCourseMineList.get(i).setSelect(false);
                    }
                    img_select_all.setImageResource(R.drawable.img_down_unselect);
                    rl_dibu.setVisibility(View.GONE);
                    isALL = true;
                }
                break;
            case R.id.tv_quanxuans:
                if (isALL) {
                    for (int i = 0; i < mCourseMineList.size(); i++) {
                        mCourseMineList.get(i).setSelect(true);
                    }
                    tv_quanxuan.setText("取消全选");
                    img_select_all.setImageResource(R.drawable.img_down_select);
                    isALL = false;
                } else {
                    for (int i = 0; i < mCourseMineList.size(); i++) {
                        mCourseMineList.get(i).setSelect(false);
                    }
                    tv_quanxuan.setText("全选");
                    img_select_all.setImageResource(R.drawable.img_down_unselect);
                    tv_quxiao.setTextColor(Color.parseColor("#999999"));
                    tv_quxiao.setClickable(false);
                    isALL = true;
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_quxiao:
                boolean isSelect = false;
                for (int i = 0; i < mCourseMineList.size(); i++) {
                    if (mCourseMineList.get(i).isSelect()) {
                        isSelect = true;
                        break;
                    }
                }
                if(!isSelect) {
                    return;
                }
                if(confirmDialog == null) {
                    confirmDialog = DialogUtils.createDialog(DissLessionActivity.this, null,
                            "取消隐藏后，您将在我的课程页面看到取消隐藏的课程，确定取消隐藏？");
                }
                confirmDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netClassId = "";
                        orderId = "";
                        boolean isFirst = true;
                        for (int i = 0; i < mCourseMineList.size(); i++) {
                            if (mCourseMineList.get(i).isSelect()) {
                                if (isFirst) {
                                    netClassId = mCourseMineList.get(i).rid;
                                    orderId = mCourseMineList.get(i).orderId;
                                    isFirst = false;
                                } else {
                                    netClassId = netClassId + "," + mCourseMineList.get(i).rid;
                                    orderId = orderId + "," + mCourseMineList.get(i).orderId;
                                }

                            }
                        }
                        //恢复隐藏课程
                        DataController.getInstance().recoverCourseDiss(netClassId, orderId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<CourseDiss>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(CourseDiss courseDiss) {
                                        if (courseDiss.code == 1000000) {
                                            CommonUtils.showToast("恢复成功");
                                            isChanged = true;
                                            showProgress();
                                            initView();
                                            UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    onLoadData();
                                                }
                                            },1000);
                                        } else {
                                            CommonUtils.showToast(courseDiss.message);
                                        }
                                    }

                                });
                    }
                });
                confirmDialog.show();
                break;
            case R.id.button_TitleBar_Back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
    }

    @OnClick(R.id.hide_course_selector_tv)
    public void onClickSelector() {
        initPopWindowSelector();
        mPopupWindowSelector.showAsDropDown(btnSelector, 20, 0);
    }

    private void setSelectorState() {
        if (mPopupWindowSelector == null) {
            initPopWindowSelector();
        }
        if (courseType == 0) {//ALL
            btnSelector.setText("全部");
            btnSelectAll.setBackgroundColor(Color.parseColor("#3c464f"));
            btnSelectLive.setBackgroundColor(Color.parseColor("#00000000"));
            btnSelectVod.setBackgroundColor(Color.parseColor("#00000000"));
        } else if (courseType == 1) {
            btnSelector.setText("直播");
            btnSelectLive.setBackgroundColor(Color.parseColor("#3c464f"));
            btnSelectAll.setBackgroundColor(Color.parseColor("#00000000"));
            btnSelectVod.setBackgroundColor(Color.parseColor("#00000000"));
        } else if (courseType == 2) {
            btnSelector.setText("录播");
            btnSelectVod.setBackgroundColor(Color.parseColor("#3c464f"));
            btnSelectAll.setBackgroundColor(Color.parseColor("#00000000"));
            btnSelectLive.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    public void initPopWindowSelector() {
        if (mPopupWindowSelector == null) {
            LinearLayout mPopLayout = (LinearLayout) (mLayoutInflater.inflate(
                    R.layout.shopping_popwindow_layout, null));
            mPopLayout.setBackgroundResource(R.drawable.my_course_selection_pop_bg);
            mPopupWindowSelector = new PopupWindow(mPopLayout, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindowSelector.setFocusable(true);
            mPopupWindowSelector.setOutsideTouchable(true);
            btnSelectAll = (TextView) mPopLayout.findViewById(R.id.shopping_type_all);
            btnSelectAll.setText("全部");
            btnSelectLive = (TextView) mPopLayout.findViewById(R.id.shopping_type_free);
            btnSelectLive.setText("直播");
            btnSelectVod = (TextView) mPopLayout.findViewById(R.id.shopping_type_fee);
            btnSelectVod.setText("录播");
            setSelectorState();
            mPopLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mPopupWindowSelector.dismiss();
                    return true;
                }
            });
            btnSelectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 0) {
                        courseType = 0;
                        setSelectorState();
                        onLoadData();
                        rListView.setSelection(0);
                    }
                    mPopupWindowSelector.dismiss();
                }
            });
            btnSelectLive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 1) {
                        courseType = 1;
                        setSelectorState();
                        onLoadData();
                        rListView.setSelection(0);
                    }
                    mPopupWindowSelector.dismiss();
                }
            });
            btnSelectVod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 2) {
                        courseType = 2;
                        setSelectorState();
                        onLoadData();
                        rListView.setSelection(0);
                    }
                    mPopupWindowSelector.dismiss();
                }
            });
        }
    }

    private void initView() {
        if (isEdit) {
            button_titleBar_operations.setText("编辑");
            isEdit = false;
            for (int i = 0; i < mCourseMineList.size(); i++) {
                mCourseMineList.get(i).setSelect(false);
            }
            img_select_all.setImageResource(R.drawable.img_down_unselect);
            fl_edit.setVisibility(View.GONE);
            rl_dibu.setVisibility(View.GONE);
            isALL = true;
        }
        mAdapter.notifyDataSetChanged();
    }
    public class CourseDissAdapter extends BaseAdapter {

        private final Context mContext;
        private final ArrayList<CourseMineBean.ResultBean> mCourseMineList;
        private final LayoutInflater mInflater;

        public CourseDissAdapter(Context mContext, ArrayList<CourseMineBean.ResultBean> mCourseMineList) {
            this.mContext = mContext;
            this.mCourseMineList = mCourseMineList;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mCourseMineList.size();

        }

        @Override
        public Object getItem(int position) {
            return mCourseMineList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CourseMineBean.ResultBean mineItem = mCourseMineList.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.disslession_mine,
                        null);
                holder.gq = (ImageView) convertView
                        .findViewById(R.id.iv_item_course_mine_gq);
                holder.ivScaleimg = (ImageView) convertView
                        .findViewById(R.id.iv_item_course_mine_scaleimg);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.tv_item_course_mine_title);
                holder.tv_item_course_mine_timelength = (TextView) convertView
                        .findViewById(R.id.tv_item_course_mine_timelength);
                holder.tvmiStartDate = (TextView) convertView
                        .findViewById(R.id.tv_item_course_mine_startdate);
                holder.tvmiEndDate = (TextView) convertView
                        .findViewById(R.id.tv_item_course_mine_enddate);
                holder.tvCenterdate = (TextView) convertView
                        .findViewById(R.id.tv_item_course_mine_centerdate);
                holder.rl_yincagchuang = (RelativeLayout) convertView.findViewById(R.id.rl_yincagchuang);
                holder.img_select_status = (ImageView) convertView.findViewById(R.id.img_select_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //加载显示数据
            if (isEdit) {
                holder.img_select_status.setVisibility(View.VISIBLE);
                if (mineItem.isSelect()) {
                    holder.img_select_status
                            .setImageResource(R.drawable.img_down_select);
                    tv_quxiao.setTextColor(Color.parseColor("#e9304e"));
                    tv_quxiao.setClickable(true);
                } else {
                    holder.img_select_status
                            .setImageResource(R.drawable.img_down_unselect);
                }
            } else {
                holder.img_select_status.setVisibility(View.GONE);
            }
            holder.tvTitle.setText(mineItem.title);
            if (mineItem.isStudyExpired == 1) {
                holder.gq.setVisibility(View.VISIBLE);
            } else {
                holder.gq.setVisibility(View.GONE);
            }
            if (mineItem.iszhibo == 1) {
                holder.iv.setVisibility(View.VISIBLE);
                    holder.tvmiStartDate.setText(mineItem.startDate);
                    holder.tvmiEndDate.setText(mineItem.endDate);
                    holder.tv_item_course_mine_timelength.setText(mineItem
                            .TimeLength);
                if ("".equals(mineItem.endDate)) {
                    holder.tvmiStartDate.setText(mineItem.startDate);
                    holder.tvCenterdate.setVisibility(View.GONE);
                    holder.tvmiEndDate.setVisibility(View.GONE);
                } else {
                    holder.tvmiStartDate.setText(mineItem.startDate);
                    holder.tvmiEndDate.setText(mineItem.endDate);
                    holder.tv_item_course_mine_timelength.setText(mineItem
                            .TimeLength);
                }
   /*             Glide.with(mContext).load(mineItem.scaleimg)
                        .crossFade(3000)
                        .animate(android.R.anim.fade_in)
                        .placeholder(R.drawable.icon_default)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.ivScaleimg);*/

                ImageLoad.displaynoCacheImage(mContext,R.drawable.icon_default,mineItem.scaleimg,holder.ivScaleimg);

            } else {
                holder.iv.setVisibility(View.GONE);
                    holder.tvmiStartDate.setText(mineItem.startDate);
                    holder.tvmiEndDate.setText(mineItem.endDate);
                    holder.tv_item_course_mine_timelength.setText(mineItem
                            .TimeLength);
                if ("".equals(mineItem.endDate)) {
                    holder.tvmiStartDate.setText(mineItem.startDate);
                    holder.tvCenterdate.setVisibility(View.GONE);
                    holder.tvmiEndDate.setVisibility(View.GONE);
                } else {
                    holder.tvmiStartDate.setText(mineItem.startDate);
                    holder.tvmiEndDate.setText(mineItem.endDate);
                    holder.tv_item_course_mine_timelength.setText(mineItem
                            .TimeLength);
                }
          /*      Glide.with(mContext).load(mineItem.scaleimg)
                        .crossFade(3000)
                        .animate(android.R.anim.fade_in)
                        .placeholder(R.drawable.icon_default)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.ivScaleimg);*/

                ImageLoad.displaynoCacheImage(mContext,R.drawable.icon_default,mineItem.scaleimg,holder.ivScaleimg);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public ImageView gq;
        public ImageView ivScaleimg;
        public ImageView iv;
        public RelativeLayout rl_yincagchuang;
        public TextView tvCenterdate;
        public TextView tvmiEndDate;
        public TextView tvmiStartDate;
        public TextView tv_item_course_mine_timelength;
        public TextView tvTitle;
        public ImageView img_select_status;
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            button_titleBar_operations.setText("编辑");
            isEdit = false;
            for (int i = 0; i < mCourseMineList.size(); i++) {
                mCourseMineList.get(i).setSelect(false);
            }
            img_select_all.setImageResource(R.drawable.img_down_unselect);
            fl_edit.setVisibility(View.GONE);
            rl_dibu.setVisibility(View.GONE);
            isALL = true;
            return;
        } else if(isChanged) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
