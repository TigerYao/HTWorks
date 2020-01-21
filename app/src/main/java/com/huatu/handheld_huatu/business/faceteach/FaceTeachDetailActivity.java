package com.huatu.handheld_huatu.business.faceteach;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.business.faceteach.fragment.F2fJobSelectFragment;
import com.huatu.handheld_huatu.business.me.SerciveDialog;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.helper.XiaoNengAssist;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FaceClassDetailBean;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FaceGoodsDetailBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.utils.StringUtils;
import com.huatu.widget.X5WebView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

public class FaceTeachDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;                    // 编号
    @BindView(R.id.tv_name)
    TextView tvName;                        // 商品名称
    @BindView(R.id.tv_price)
    TextView tvPrice;                       // 商品价格
    @BindView(R.id.tv_place)
    TextView tvPlace;                       // 上课地点
    @BindView(R.id.ll_class_count)
    LinearLayout llClassCount;              // 课时布局（课程没有课时）
    @BindView(R.id.tv_class_count)
    TextView tvClassCount;                  // 课时数量
    @BindView(R.id.tv_suit)
    TextView tvSuit;                        // 适用考试
    @BindView(R.id.ll_recruit_count)
    LinearLayout llRecruitCount;            // 招生人数（课程没有招生人数）
    @BindView(R.id.tv_recruit_count)
    TextView tvRecruitCount;                // 招生人数

    @BindView(R.id.tv_protocol)
    TextView tvProtocol;                    // 招生协议
    @BindView(R.id.tv_protocol_content)
    TextView tvProtocolContent;             // 招生协议内容
    @BindView(R.id.sp_protocol)
    Spinner spProtocol;                     // 协议下拉列表

    @BindView(R.id.tv_see_protocol)
    TextView tvSeeProtocol;                 // 去查看协议

    @BindView(R.id.tv_time)
    TextView tvTime;                        // 上课时间
    @BindView(R.id.sp_time)
    Spinner spTime;

    @BindView(R.id.tv_live)
    TextView tvLive;                        // 住宿情况
    @BindView(R.id.sp_live)
    Spinner spLive;

    @BindView(R.id.tv_food)
    TextView tvFood;                        // 餐饮情况
    @BindView(R.id.sp_food)
    Spinner spFood;

    @BindView(R.id.tv_introduce)
    TextView tvIntroduce;                   // 课程介绍Title
    @BindView(R.id.wv_introduce)
    X5WebView wvIntroduce;                  // 课程介绍内容
    @BindView(R.id.tv_introduce_content)
    TextView tvIntroduceContent;            // 课程介绍

    @BindView(R.id.tv_cur_price)
    TextView tvCurPrice;                    // 最底部红色的最后价格

    @BindView(R.id.ll_service)
    LinearLayout llService;                 // 客服

    @BindView(R.id.tv_buy)
    TextView tvBuy;                         // 购买按钮

    @BindView(R.id.err_view)
    CommonErrorView errView;                // 错误页面

    private int width;                      // 下拉宽度

    private int type;                       // 0、面授课程 1、面授商品

    // 面授课程的参数
    private String courseid;                // 招生简章钟的course_id   "77677"
    private String cityid;                  // 招生简章所属的cityid    "1770"

    // 面授商品参数
    private String aid;                     // 课程aid        "5201698"
    private String area;                    // 选择的地区名称   "2"
    private String cid;                     // 选择的科目ID    "103"

    private FaceClassDetailBean faceClass;                                  // 面授课程
    private ArrayList<FaceClassDetailBean.FaceClassBean> faceClassList;     // 面授课程列表
    private FaceClassDetailBean.FaceClassBean selectFaceClassBean;          // 当前选择的课程

    private FaceGoodsDetailBean faceGoods;                                  // 面授商品

    private String mShareUrl, mShareTitle = "";

    private AppPermissions rxPermissions;
    private String phone;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_face_teach_detail;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayFinishEvent(BaseMessageEvent event) {
        if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ON_PAYED_ALL) {
            finish();
        }
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(FaceTeachDetailActivity.this);

        XiaoNengAssist.getInstance().init(this);

        type = originIntent.getIntExtra("type", 1);

        mShareUrl = originIntent.getStringExtra(ArgConstant.KEYWORDS);

        if (type == 0) {        // 面授课程
            tvTitle.setText("课程详情");
            tvIntroduce.setText("课程介绍");
            courseid = originIntent.getStringExtra(ArgConstant.COURSE_ID);
            cityid = originIntent.getStringExtra(ArgConstant.KEY_ID);
        } else {                // 面授商品
            tvTitle.setText("商品详情");
            tvIntroduce.setText("课程须知");
            aid = originIntent.getStringExtra("aid");
            area = originIntent.getStringExtra("area");
            cid = originIntent.getStringExtra("cid");
        }

        spProtocol.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int measuredWidth = spProtocol.getMeasuredWidth();
                if (measuredWidth > 0) {
                    width = measuredWidth;
                    spProtocol.setDropDownWidth(measuredWidth);
                    spTime.setDropDownWidth(measuredWidth);
                    spLive.setDropDownWidth(measuredWidth);
                    spFood.setDropDownWidth(measuredWidth);
                    spProtocol.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        rxPermissions = new AppPermissions(this);
    }

    @Override
    protected void onLoadData() {
        if (type == 0) {        // 面授课程
            initSpinner();
            loadClassData();
        } else {                // 面授商品
            loadGoodsData();
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_service, R.id.tv_buy, R.id.err_view, R.id.tv_see_protocol, R.id.iv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_service:
                String nengId, title, aid;
                if (type == 0) {        // 面授课程
                    nengId = faceClass.ntalker_id;
                    title = selectFaceClassBean.title;
                    aid = selectFaceClassBean.aid;
                    phone = faceClass.city_tel;
                } else {                // 面授商品
                    nengId = faceGoods.customer_branch;
                    title = faceGoods.title;
                    aid = faceGoods.aid;
                    phone = faceGoods.phone;
                }
                if (!StringUtils.isEmpty(nengId)) {
                    XiaoNengAssist.getInstance().startChat(nengId, title, aid);
                } else {
                    final SerciveDialog serciveDialog = new SerciveDialog(this, R.layout.dialog_me_sevice);

                    TextView text_ok = serciveDialog.mContentView.findViewById(R.id.text_ok);
                    TextView text_cancel = serciveDialog.mContentView.findViewById(R.id.text_cancel);
                    TextView zs_phone = serciveDialog.mContentView.findViewById(R.id.zs_phone);
                    if (zs_phone != null) {
                        zs_phone.setText(phone);
                    }
                    text_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            serciveDialog.dismiss();
                        }
                    });

                    text_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            serciveDialog.dismiss();
                            tel();
                        }
                    });

                    serciveDialog.show();
                }
                break;
            case R.id.tv_buy:           // 立即购买。根据zwbh字段判断是否有职位保护。0、否 1、是。到下单页面/职位选择页面
                goToBuy();
                break;
            case R.id.err_view:
                onLoadData();
                break;
            case R.id.tv_see_protocol:  // 显示协议
                if (type == 0) {
                    BaseWebViewFragment.lanuch(this, selectFaceClassBean.dzqz_viewurl, "电子协议");
                } else {
                    BaseWebViewFragment.lanuch(this, faceGoods.dzqz_viewurl, "电子协议");
                }
                break;
            case R.id.iv_share:
                if (TextUtils.isEmpty(mShareTitle)) return;
                String host = mShareUrl;
                try {
                    host = mShareUrl.substring(mShareUrl.indexOf("://") + 3);
                    host = host.substring(0, host.indexOf("/"));
                } catch (Exception e) {
                }
                ShareUtil.test(this, "", host, mShareTitle, mShareUrl, "", null, "");
                break;
        }
    }

    private void tel() {
        rxPermissions.request(Manifest.permission.CALL_PHONE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取打电话权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                            FaceTeachDetailActivity.this.startActivity(intent);
                        } else {
                            CommonUtils.showToast("没有打电话权限");
                        }
                    }
                });
    }

    /**
     * 去 购买页/职位保护页
     */
    private void goToBuy() {
        if (type == 0) {    // 面授课程
            buyClass();
        } else {            // 面授商品
            buyGoods();
        }
    }

    // 面授课程
    private void buyClass() {
        if (selectFaceClassBean != null) {
            if (checkTimeOverdue(selectFaceClassBean.kksj)) {
                ToastUtils.showEssayToast("课程已开课，请选择其他课程");
                return;
            }
            // 下单需要的字段
            Bundle args = new Bundle();
            args.putString("aid", selectFaceClassBean.aid);                             // 商品aid
            args.putInt("zstype", (selectFaceClassBean.zstj.equals("0") ? 0 : 1));      // 0、不住宿 1、住宿
            args.putString("price", selectFaceClassBean.xfprice);                       // 价格

            if (selectFaceClassBean.zwbh.equals("0")) {       // 没有职位保护
                ConfirmOrderActivity.launch(FaceTeachDetailActivity.this, args);
            } else {                                // 职位保护，需要跳转职位保护页，职位保护页面选择职位后添加htwyid字段，再跳转下单页面
                if (selectFaceClassBean.bx.equals("国家公务员考试")) {                    // 请求职位保护需要
                    args.putString("ssfb", "0");
                } else {
                    args.putString("ssfb", selectFaceClassBean.fxid);
                }
                BaseFrgContainerActivity.newInstance(FaceTeachDetailActivity.this, F2fJobSelectFragment.class.getName(), args);
            }
        }
    }

    // 面授商品
    private void buyGoods() {
        if (checkTimeOverdue(faceGoods.kksj)) {
            ToastUtils.showEssayToast("课程已开课，请选择其他课程");
            return;
        }
        // 下单需要的字段
        Bundle args = new Bundle();
        args.putString("aid", faceGoods.aid);                                                               // 商品aid

        // 添加各种下个页面显示的价格
        if ("1".equals(faceGoods.zsflag)) {
            args.putInt("zstype", 1);                       // 住宿
            args.putString("price", faceGoods.zs_price);    // 价格
        } else {
            args.putInt("zstype", 0);                       // 不住宿
            args.putString("price", faceGoods.no_zs_price); // 价格
        }
        args.putString("zs_price", faceGoods.zs);
        args.putString("yh_price", faceGoods.yh_price);         // 优惠的价格

        if (faceGoods.zwbh.equals("0")) {       // 没有职位保护
            ConfirmOrderActivity.launch(FaceTeachDetailActivity.this, args);
        } else {                                // 职位保护，需要跳转职位保护页，职位保护页面选择职位后添加htwyid字段，再跳转下单页面
            args.putString("ssfb", faceGoods.ssfb);                                                         // 请求职位保护需要
            BaseFrgContainerActivity.newInstance(FaceTeachDetailActivity.this, F2fJobSelectFragment.class.getName(), args);
        }
    }

    /**
     * 检查开课时间是否已过期
     */
    private boolean checkTimeOverdue(String sksj) {
        try {
            String time = sksj + " 00:00:00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = simpleDateFormat.parse(time);
            long start = date.getTime();
            return System.currentTimeMillis() - start >= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 面授商品详情
     */
    private void loadGoodsData() {

        if (!NetUtil.isConnected()) {
            showError(0);
            return;
        }

        showProgress();

        String curTime = System.currentTimeMillis() + "";

        String md5 = String.format("aid=%s&area=%s&cid=%s&timestamp=%s", aid, area, cid, curTime);

        String sign = Md5Util.toSign(md5);

        String params = String.format("{'aid':'%s','area':'%s','cid':'%s','timestamp':'%s','sign':'%s'}", aid, area, cid, curTime, sign).replace("'", "\"");

        ServiceProvider.getFaceGoodsDetail(compositeSubscription, params, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                showError(1);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                faceGoods = (FaceGoodsDetailBean) model.data;
                if (faceGoods != null) {
                    showGoodsDetail();
                } else {
                    showError(1);
                }
                hideProgress();
            }
        });
    }

    // 显示商品详情
    private void showGoodsDetail() {
        tvOrderNum.setText("编号：" + faceGoods.bc);
        tvName.setText(faceGoods.title);
        mShareTitle = faceGoods.title;
        // 住宿标志
        // 0-强制不住宿 价格显示zs_price，文字显示不含住宿
        // 1-强制住宿 价格显示zs_price，文字显示含住宿
        // 2-可选择 默认显示zs_price，文字显示含住宿（后来确定，不是可选，是不住宿）
        // 3-住宿已满 价格显示no_zs_price，文字显示不含住宿
        if ("1".equals(faceGoods.zsflag)) {
            if (StringUtils.isEmpty(faceGoods.yh_price) || faceGoods.yh_price.equals("0")) {
                tvPrice.setText("¥" + faceGoods.zs_price);
                tvCurPrice.setText("¥" + faceGoods.zs_price);
            } else {
                tvPrice.setText("¥" + strSub(faceGoods.zs_price, faceGoods.yh_price));
                tvCurPrice.setText("¥" + strSub(faceGoods.zs_price, faceGoods.yh_price));
            }
            tvLive.setText("含住宿");
        } else {
            if (StringUtils.isEmpty(faceGoods.yh_price) || faceGoods.yh_price.equals("0")) {
                tvPrice.setText("¥" + faceGoods.no_zs_price);
                tvCurPrice.setText("¥" + faceGoods.no_zs_price);
            } else {
                tvPrice.setText("¥" + strSub(faceGoods.no_zs_price, faceGoods.yh_price));
                tvCurPrice.setText("¥" + strSub(faceGoods.no_zs_price, faceGoods.yh_price));
            }
            tvLive.setText("不含住宿");
        }
        tvPlace.setText(faceGoods.kcdd);
        tvClassCount.setText(faceGoods.xs + "课时");
        tvSuit.setText(faceGoods.bx);
        tvRecruitCount.setText(faceGoods.zsrs);

        tvProtocol.setText(faceGoods.lx);
        if (!faceGoods.lx.equals("非协议") && !StringUtils.isEmpty(faceGoods.zsjzprice)) {
            tvProtocolContent.setText(faceGoods.zsjzprice);
            tvProtocolContent.setVisibility(View.VISIBLE);
        } else {
            tvProtocolContent.setVisibility(View.GONE);
        }

        if (faceGoods.lx.equals("非协议") || StringUtils.isEmpty(faceGoods.dzqz_viewurl)) {
            tvSeeProtocol.setVisibility(View.GONE);
        } else {
            tvSeeProtocol.setVisibility(View.VISIBLE);
        }

        tvTime.setText(faceGoods.sksj);
        wvIntroduce.loadUrl(faceGoods.intro);
        wvIntroduce.setVisibility(View.VISIBLE);
        tvIntroduceContent.setVisibility(View.GONE);
    }

    private String strSub(String one, String two) {
        String sub = one;
        try {
            sub = LUtils.formatPoint(Float.valueOf(one) - Float.valueOf(two));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sub;
    }

    /**
     * 获取面授课程详情
     */
    private void loadClassData() {

        if (!NetUtil.isConnected()) {
            showError(0);
            return;
        }

        showProgress();

        String timestamp = System.currentTimeMillis() + "";

        String md5 = String.format("cityid=%s&courseid=%s&timestamp=%s", cityid, courseid, timestamp);

        String sign = Md5Util.toSign(md5);

        ServiceProvider.getFaceClassDetail(compositeSubscription, cityid, courseid, timestamp, sign, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                showError(1);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                faceClass = (FaceClassDetailBean) model.data;
                if (faceClass != null) {
                    showClassDetail();
                } else {
                    showError(1);
                }
                hideProgress();
            }
        });
    }

    /**
     * 显示课程详情
     * 显示规则：
     * 如果list就一个，就显示一个，
     * 协议和时间显示固定这一个，住宿、餐饮、根据是否可选，显示下拉列表，或者固定一个。
     * 如果list有多个，
     * 筛选不同协议列表，如果筛选出来就一个，就显示固定的一个；有多个，就下拉，默认显示第一个。
     * 筛选不同的上课时间列表，如果有一个，就显示固定的一个；有多个就下拉，显示第一个。
     * 住宿默认显示第一个的。
     * 餐饮也默认选择第一个的。
     * <p>
     * 筛选规则：
     * 上边的条件影响下边的条件。
     * 协议类型是全部列表的的分类。
     */

    private String split = "#";                 // 拼接过滤 faceClassBean.refundrule + rulsSplit + faceClassBean.refundtext

    private String selectProtocol;              // 当前选择的协议
    private ArrayList<String> protocolList;     // 协议
    private SpinnerAdapter protocolAdapter;

    private String selectTime;
    private ArrayList<String> timeList;         // 时间
    private SpinnerAdapter timeAdapter;

    private String selectLive;
    private ArrayList<String> liveList;         // 住宿
    private SpinnerAdapter liveAdapter;

    private String selectFood;
    private ArrayList<String> foodList;         // 食宿
    private SpinnerAdapter foodAdapter;

    private void initSpinner() {

        protocolList = new ArrayList<>();
        protocolAdapter = new SpinnerAdapter(FaceTeachDetailActivity.this, protocolList, 0);
        spProtocol.setAdapter(protocolAdapter);
        spProtocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectProtocol = protocolList.get(position);
                filterTime();
                filterLive();
                filterFood();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        timeList = new ArrayList<>();
        timeAdapter = new SpinnerAdapter(FaceTeachDetailActivity.this, timeList, 1);
        spTime.setAdapter(timeAdapter);
        spTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectTime = timeList.get(position);
                filterLive();
                filterFood();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        liveList = new ArrayList<>();
        liveAdapter = new SpinnerAdapter(FaceTeachDetailActivity.this, liveList, 2);
        spLive.setAdapter(liveAdapter);
        spLive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectLive = liveList.get(position);
                filterFood();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        foodList = new ArrayList<>();
        foodAdapter = new SpinnerAdapter(FaceTeachDetailActivity.this, foodList, 3);
        spFood.setAdapter(foodAdapter);
        spFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectFood = foodList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showClassDetail() {
        if (faceClass.list != null && faceClass.list.size() > 0) {
            faceClassList = faceClass.list;
            if (faceClassList.size() == 1) {        // 只有一个，都显示固定了就行了
                selectFaceClassBean = faceClassList.get(0);
                showClass(faceClassList.get(0));
                showClassOther(faceClassList.get(0));
            } else {                                // 如果有多个
                filterProtocol();
                filterTime();
                filterLive();
                filterFood();
            }
        } else {
            showError(2);
        }
    }

    // 筛选是否协议课
    private void filterProtocol() {
        // 筛选协议
        protocolList.clear();
        for (FaceClassDetailBean.FaceClassBean faceClassBean : faceClassList) {
            String protocol = faceClassBean.refundrule + split + faceClassBean.refundtext;
            if (!protocolList.contains(protocol)) {
                protocolList.add(protocol);
            }
        }
        selectProtocol = protocolList.get(0);
        if (protocolList.size() == 1) {     // 如果协议只有一个
            spProtocol.setVisibility(View.GONE);      // 隐藏下拉列表隐藏
            tvProtocol.setVisibility(View.VISIBLE);

            String[] splitProtocol = selectProtocol.split(split);

            tvProtocol.setText(splitProtocol[0]);
            if (!splitProtocol[0].equals("非协议") && splitProtocol.length > 1 && !StringUtils.isEmpty(splitProtocol[1])) {
                tvProtocolContent.setText(splitProtocol[1]);
                tvProtocolContent.setVisibility(View.VISIBLE);
            } else {
                tvProtocolContent.setVisibility(View.GONE);
            }
        } else {                            // 如果协议有多个
            spProtocol.setVisibility(View.VISIBLE);   // 显示下拉列表
            tvProtocol.setVisibility(View.GONE);
            protocolAdapter.notifyDataSetChanged();
        }
    }

    // 根据选择协议项，过滤时间段
    private void filterTime() {
        // 根据当前显示的协议，筛选时间
        timeList.clear();
        for (FaceClassDetailBean.FaceClassBean faceClassBean : faceClassList) {
            String protocol = faceClassBean.refundrule + split + faceClassBean.refundtext;
            if (protocol.equals(selectProtocol)) {
                String time = faceClassBean.kksj + split + faceClassBean.sksj;
                if (!timeList.contains(time)) {
                    timeList.add(time);
                }
            }
        }
        selectTime = timeList.get(0);
        if (timeList.size() == 1) {
            spTime.setVisibility(View.GONE);
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(selectTime.split(split)[1]);
        } else {
            spTime.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.GONE);
            timeAdapter.notifyDataSetChanged();
        }
    }

    // 根据前两项，过滤住宿项
    private void filterLive() {
        // 根据当前显示的协议和时间，显示住宿
        liveList.clear();
        for (FaceClassDetailBean.FaceClassBean faceClassBean : faceClassList) {
            String protocol = faceClassBean.refundrule + split + faceClassBean.refundtext;
            String time = faceClassBean.kksj + split + faceClassBean.sksj;
            if (protocol.equals(selectProtocol) && time.equals(selectTime)) {
                if (!liveList.contains(faceClassBean.zstj)) {
                    liveList.add(faceClassBean.zstj);
                }
            }
        }
        selectLive = liveList.get(0);
        if (liveList.size() == 1) {
            spLive.setVisibility(View.GONE);
            tvLive.setVisibility(View.VISIBLE);
            tvLive.setText(selectLive.equals("是") ? "包住宿" : "不含住宿");
        } else {
            spLive.setVisibility(View.VISIBLE);
            tvLive.setVisibility(View.GONE);
            liveAdapter.notifyDataSetChanged();
        }
    }

    // 根据前三项，过滤饮食项
    private void filterFood() {
        // 根据当前显示的协议、时间、住宿 筛选
        foodList.clear();
        for (FaceClassDetailBean.FaceClassBean faceClassBean : faceClassList) {
            String protocol = faceClassBean.refundrule + split + faceClassBean.refundtext;
            String time = faceClassBean.kksj + split + faceClassBean.sksj;
            if (protocol.equals(selectProtocol) && time.equals(selectTime) && faceClassBean.zstj.equals(selectLive)) {
                if (!foodList.contains(faceClassBean.canyin)) {
                    foodList.add(faceClassBean.canyin);
                }
            }
        }
        selectFood = foodList.get(0);
        if (foodList.size() == 1) {
            spFood.setVisibility(View.GONE);
            tvFood.setVisibility(View.VISIBLE);
            tvFood.setText(selectFood.equals("0") ? "不含餐饮" : "含餐饮");
        } else {
            spFood.setVisibility(View.VISIBLE);
            tvFood.setVisibility(View.GONE);
            foodAdapter.notifyDataSetChanged();
        }
        filterAllAndShow();
    }

    // 过滤所有选项并显示第一个
    private void filterAllAndShow() {
        for (FaceClassDetailBean.FaceClassBean faceClassBean : faceClassList) {
            String protocol = faceClassBean.refundrule + split + faceClassBean.refundtext;
            String time = faceClassBean.kksj + split + faceClassBean.sksj;
            if (protocol.equals(selectProtocol) && time.equals(selectTime) && faceClassBean.zstj.equals(selectLive) && faceClassBean.canyin.equals(selectFood)) {
                selectFaceClassBean = faceClassBean;
                showClass(faceClassBean);
                break;
            }
        }
    }

    // 显示详情基础项
    private void showClass(FaceClassDetailBean.FaceClassBean faceClassBean) {

        tvOrderNum.setText("编号：" + faceClassBean.bc);
        tvName.setText(faceClassBean.bb);
        mShareTitle = faceClassBean.bb;

        tvPrice.setText("¥" + faceClassBean.xfprice);
        tvCurPrice.setText("¥" + faceClassBean.xfprice);

        tvPlace.setText(faceClassBean.kcdd);

        llClassCount.setVisibility(View.GONE);
        tvSuit.setText(faceClassBean.bx);
        llRecruitCount.setVisibility(View.GONE);

        if (!faceClassBean.refundrule.equals("非协议") && !StringUtils.isEmpty(faceClassBean.refundtext)) {
            tvProtocolContent.setText(faceClassBean.refundtext);
            tvProtocolContent.setVisibility(View.VISIBLE);
        } else {
            tvProtocolContent.setVisibility(View.GONE);
        }

        if (faceClassBean.refundrule.equals("非协议") || StringUtils.isEmpty(faceClassBean.dzqz_viewurl)) {
            tvSeeProtocol.setVisibility(View.GONE);
        } else {
            tvSeeProtocol.setVisibility(View.VISIBLE);
        }

        tvIntroduceContent.setText(faceClassBean.course_text);

        wvIntroduce.setVisibility(View.GONE);
        tvIntroduceContent.setVisibility(View.VISIBLE);
    }

    // 显示其他详情
    private void showClassOther(FaceClassDetailBean.FaceClassBean faceClassBean) {

        selectProtocol = faceClassBean.refundrule + split + faceClassBean.refundtext;

        tvProtocol.setText(faceClassBean.refundrule);

        tvProtocol.setVisibility(View.VISIBLE);
        spProtocol.setVisibility(View.GONE);

        selectTime = faceClassBean.kksj + split + faceClassBean.sksj;
        tvTime.setText(faceClassBean.sksj);
        tvTime.setVisibility(View.VISIBLE);
        spTime.setVisibility(View.GONE);

        selectLive = faceClassBean.zstj;
        tvLive.setText(faceClassBean.zstj.equals("是") ? "包住宿" : "不含住宿");
        tvLive.setVisibility(View.VISIBLE);
        spLive.setVisibility(View.GONE);

        selectFood = faceClassBean.canyin;
        tvFood.setText(faceClassBean.canyin.equals("0") ? "不含餐饮" : "含餐饮");
        tvFood.setVisibility(View.VISIBLE);
        spFood.setVisibility(View.GONE);
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
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

    public class SpinnerAdapter extends BaseAdapter {

        private Context ctx;
        private ArrayList<String> data;
        private int optionType;                   // 0、协议 1、时间 2、住宿 3、餐饮

        SpinnerAdapter(Context ctx, ArrayList<String> data, int optionType) {
            this.ctx = ctx;
            this.data = data;
            this.optionType = optionType;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ctx).inflate(R.layout.face_detail_spinner_item, parent, false);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.tvTitle.setSingleLine(false);
            holder.tvContent.setSingleLine(false);
            holder.tvTitle.setText(getTitle(position));
            holder.tvContent.setVisibility(View.GONE);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ctx).inflate(R.layout.face_detail_spinner_item, parent, false);
                new ViewHolder(convertView);
            }
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            if (width > 0 && layoutParams.width != width - 10) {
                layoutParams.width = width - 10;
                convertView.setLayoutParams(layoutParams);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.tvTitle.setSingleLine(false);
            holder.tvContent.setSingleLine(false);
            holder.tvTitle.setText(getTitle(position));

            setContent(position, holder);

            return convertView;
        }

        private void setContent(int position, ViewHolder holder) {
            String[] split = data.get(position).split(FaceTeachDetailActivity.this.split);
            if (optionType == 0 && !getTitle(position).equals("非协议") && split.length > 1 && !StringUtils.isEmpty(split[1])) {
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(split[1]);
            } else {
                holder.tvContent.setVisibility(View.GONE);
            }
        }

        private String getTitle(int position) {
            switch (optionType) {
                case 0:
                    return data.get(position).split(split)[0];
                case 1:
                    String[] split = data.get(position).split(FaceTeachDetailActivity.this.split);
                    return split.length > 1 ? split[1] : "";
                case 2:
                    if (FaceTeachDetailActivity.this.type == 0) {       // 课程
                        return data.get(position).equals("是") ? "包住宿" : "不含住宿";
                    } else {                                            // 商品
                        return data.get(position).equals("是") ? "住宿" : "不住宿";
                    }
                case 3:
                    return data.get(position).equals("0") ? "不含餐饮" : "含餐饮";
                default:
                    return data.get(position);
            }
        }

        class ViewHolder {
            TextView tvTitle;
            TextView tvContent;

            ViewHolder(View convertView) {
                tvTitle = convertView.findViewById(R.id.tv_title);
                tvContent = convertView.findViewById(R.id.tv_content);
                convertView.setTag(this);
            }
        }
    }

    private void showError(int type) {
        hideProgress();
        errView.setVisibility(View.VISIBLE);
        errView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errView.setErrorImage(R.drawable.no_server_service);
                errView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 获取数据失败
                errView.setErrorImage(R.drawable.no_data_bg);
                errView.setErrorText("获取数据失败，点击重试");
                break;
            case 2:                         // 暂无数据
                errView.setErrorImage(R.drawable.no_data_bg);
                errView.setErrorText("暂无课程");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wvIntroduce != null) {
            wvIntroduce.onDestory();
        }
    }

    /**
     * 面授课程
     *
     * @param courseId 招生简章钟的course_id
     * @param cityId   招生简章所属的cityid
     */
    public static void launch(Context context, String courseId, String cityId, String url) {
        Intent intent = new Intent(context, FaceTeachDetailActivity.class);
        intent.putExtra("type", 0);             // 面授课程
        intent.putExtra(ArgConstant.COURSE_ID, courseId);
        intent.putExtra(ArgConstant.KEY_ID, cityId);
        intent.putExtra(ArgConstant.KEYWORDS, url);
        context.startActivity(intent);
    }

    /**
     * 面授商品
     *
     * @param aid  课程aid
     * @param area 选择的地区名称
     * @param cid  选择的科目ID
     */
    public static void launch(Context context, String aid, String area, String cid, String url) {
        Intent intent = new Intent(context, FaceTeachDetailActivity.class);
        intent.putExtra("type", 1);             // 面授商品
        intent.putExtra("aid", aid);
        intent.putExtra("area", area);
        intent.putExtra("cid", cid);
        intent.putExtra(ArgConstant.KEYWORDS, url);
        context.startActivity(intent);
    }
}
