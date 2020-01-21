package com.huatu.handheld_huatu.business.me;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.common.permission.AppPermissions;
import com.google.gson.Gson;
import com.google.zxing.ResultPoint;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaAnswerCardActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamReportActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamReportExActivity;
import com.huatu.handheld_huatu.business.arena.bean.ScanResultBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.lessons.bean.PurchasedBean;
import com.huatu.handheld_huatu.business.matches.activity.ScReportActivity;
import com.huatu.handheld_huatu.business.me.bean.ScanCourseData;
import com.huatu.handheld_huatu.business.me.fragment.LoadScanCourseFragment;
import com.huatu.handheld_huatu.business.me.fragment.LoadScanResultFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.mvpmodel.exercise.PaperBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CameraPreview;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chq on 2017/9/7.
 */

public class MyScanActivity extends BaseActivity {

    private String TAG = "MyScanActivity";
    private ImageView mImageView;
    private ImageView backIv;
    // 时间限制
    private long timeLimit = (long) (1.5 * 60 * 60 * 1000);
    private RelativeLayout rl_back;
    //    private Button saomiao_back;
    private String selectCourseInfo;
    private String currentClassCardId;
    private SharedPreferences sp_config;
    private String activity_from;
    private TextView tv_main_title;
    private CompoundBarcodeView barcodeScannerView;
    private String key = "0123456789QWEQWEEWQQ1234";

    private SharedPreferences sp;
    private String uid;
    private boolean isDestoryed = false;
    private Activity mActivity = this;
    private boolean isHasCameraPermissions;
    private AppPermissions rxPermissions;
    String desStr;
    Intent intent1;
    String desUrl1;
    String[] e1;
    String[] desUrl2;

    private String mTitle;
    private String mShareTitle;
    private String mShareDesc;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_my_scan;
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
    public void onResume() {
        super.onResume();
        if (isHasCameraPermissions) {
            barcodeScannerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    protected void onInitView() {
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        barcodeScannerView = (CompoundBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        backIv = (ImageView) findViewById(R.id.back_iv);
        barcodeScannerView.getBarcodeView().addStateListener(stateListener);
        barcodeScannerView.decodeContinuous(callback);
        rl_back = (RelativeLayout) this.findViewById(R.id.rl_back);
        Intent intent_From = getIntent();
        sp_config = getSharedPreferences("config", MODE_PRIVATE);
        setOnListener();
        reqRxPermissions();
    }

    public void setOnListener() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void reqRxPermissions() {
        rxPermissions = new AppPermissions(this);
        //  rxPermissions.setLogging(true);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取相机权限失败");
                        MyScanActivity.this.finish();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            isHasCameraPermissions = true;
                            barcodeScannerView.resume();
                        } else {
                            CommonUtils.showToast("获取相机权限失败");
                            MyScanActivity.this.finish();
                        }
                    }
                });
    }

    /**
     * 处理扫描结果
     *
     * @param decodeStr 扫描出来的字符串
     */
    public void handleDecode(String decodeStr) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络错误，请检查您的网络");
        }
        LogUtils.e("url_scan", decodeStr);
        final String result = TextUtils.isEmpty(decodeStr) ? "" : decodeStr;
        if (result != null && result.startsWith("http://m.v.huatu.com/z/19114/index.html?lessionId=")) {
            //扫一扫跳转h5
            Uri uri = Uri.parse(result);
            final String lessonId = uri.getQueryParameter("lessionId");
            if (lessonId != null) {
                //请求接口，获取title
                ServiceProvider.getScanCourseData(compositeSubscription, lessonId, new NetResponse() {
                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        super.onSuccess(model);
                        if (model != null) {
                            ScanCourseData mData = (ScanCourseData) model.data;
                            mTitle = mData.lessionTitle;
                            mShareTitle = mData.shareTitle;
                            mShareDesc = mData.shareDesc;
                            BaseFrgContainerActivity.newInstance(mActivity,
                                    LoadScanCourseFragment.class.getName(),
                                    LoadScanCourseFragment.getArgs(result, lessonId, mTitle, mShareDesc, mShareTitle));
                            MyScanActivity.this.finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtil.showToast("请求失败了，请重新扫描");
                        if (barcodeScannerView != null) {
                            barcodeScannerView.resume();
                        }

                    }
                });
            }

        } else if (result != null && result.contains(".huatu.com/")) {
            intent1 = new Intent();
            if (result.contains("v.huatu.com/cla") && result.contains("fromuser")) {
                desUrl2 = result.split("&");
                e1 = desUrl2[0].split("=");
                String[] desBytesArr2 = desUrl2[1].split("=");
                LogUtils.d(TAG, "desBytesArr2:" + desBytesArr2);
                showProgress();
                String username = SpUtils.getUname();
                String rid = e1[1];
                Observable<BaseResponseModel<PurchasedBean>> pObservable = RetrofitManager.getInstance()
                        .getApiTkService().checkIsBuyWithId(username, rid);
                pObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<BaseResponseModel<PurchasedBean>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                hideProgress();
                                CommonUtils.showToast("加载失败");
                            }

                            @Override
                            public void onNext(BaseResponseModel<PurchasedBean> pvar) {
                                hideProgress();
                                if (pvar != null && e1.length >= 2) {
                                    if (pvar.code == 1) {
                                        /*if (pvar.data != null && pvar.data.IsSuit == 1) {
                                            intent1.putExtra("course_id", e1[1]);
                                            intent1.putExtra("NetClassId", e1[1]);
                                            intent1.putExtra("rid", e1[1]);
                                            intent1.setClass(MyScanActivity.this, CourseSuitActivity.class);
                                            startActivity(intent1);
                                        } else*/
                                        {
                                            intent1.putExtra("course_id", e1[1]);
                                            intent1.putExtra("play_index", 0);
                                            intent1.putExtra("classid", String.valueOf(e1[1]));
                                            intent1.putExtra(ArgConstant.TYPE, 1);
                                            intent1.setClass(MyScanActivity.this, BJRecordPlayActivity.class);
                                            startActivity(intent1);
                                        }
                                    } else {
                                        desStr = e1[1];
                                        BaseIntroActivity.newIntent(MyScanActivity.this, desStr);
                                    }
                                }
                                MyScanActivity.this.finish();
                            }
                        });

                if (desBytesArr2.length >= 2) {
                    desStr = desBytesArr2[1];
                    Long fromuserData = Long.valueOf(System.currentTimeMillis());
                    SpUtils.setScanFromUser(desStr);
                    SpUtils.setScanFromUserData(fromuserData + "");
                }
                return;
            } else {
                String desBytesArr1;
                if (result.contains("v.huatu.com/cla")) {
                    desUrl2 = result.split("_");
                    if (desUrl2.length >= 3) {
                        e1 = desUrl2[2].split("\\.");
                        desBytesArr1 = e1[0];
                        intent1.putExtra("rid", desBytesArr1);
//                        intent1.setClass(this, BuyDetailsActivity.class);
                        intent1.setClass(this, BaseIntroActivity.class);
                        this.startActivity(intent1);
                    }
                    this.finish();
                    return;
                } else if (result.contains("v.huatu.com/zxapp")) {
                    if (result.contains("apc=")) {
                        int desUrl3 = result.lastIndexOf("apc=");
                        String e3 = result.substring(desUrl3 + 4);
                        String[] arystr = e3.split("_");
                        if (arystr != null && arystr.length >= 2) {
                            e3 = arystr[1];
                        }
                        intent1.putExtra("course_id", e3);
                        intent1.setClass(this, BJRecordPlayActivity.class);

                        intent1.putExtra("classid", String.valueOf(e3));
                        intent1.putExtra(ArgConstant.TYPE, 1);
                        startActivity(intent1);
                        this.finish();
                        return;
                    }
                    desUrl1 = "app_bookid=";
                    if (result.contains(desUrl1)) {
                        int e2 = result.lastIndexOf(desUrl1);
                        desBytesArr1 = result.substring(e2 + desUrl1.length());
                        VideoCloudDirectoryActivity.newIntent(this, desBytesArr1);
                    }
                    this.finish();
                    return;
                } else if (result.startsWith("http")) {
                    BaseFrgContainerActivity.newInstance(mActivity,
                            LoadScanResultFragment.class.getName(),
                            LoadScanResultFragment.getArgs(result));
                    this.finish();
                    return;
                } else {
                    DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (barcodeScannerView != null) {
                                barcodeScannerView.resume();
                            }
                        }
                    }, "扫描结果", result, null, "知道了").setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (barcodeScannerView != null) {
                                barcodeScannerView.resume();
                            }
                        }
                    });
                    if (result.contains("v.huatu.com/smapp")) {

                        return;
                    }
                }
            }
        } else if (result.startsWith("http")) {
            BaseFrgContainerActivity.newInstance(mActivity,
                    LoadScanResultFragment.class.getName(),
                    LoadScanResultFragment.getArgs(result));
            this.finish();
        } else if (result.contains("exercise")) {
            // exercise:{"paperId":"123"}   // 其他扫码
            // exercise:{"answerId":"123"}  // 错题导出扫码
            showProgress();
            String substring = result.substring(result.indexOf(":") + 1);
            ScanResultBean scanResultBean = new Gson().fromJson(substring, ScanResultBean.class);
            long paperId = scanResultBean.getPaperId();
            long answerId = scanResultBean.getAnswerId();
            if (paperId > 0) {
                ServiceProvider.getScanArenaPaper(compositeSubscription, paperId, new NetResponse() {
                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        hideProgress();
                        if (model.data instanceof RealExamBeans.RealExamBean) {
                            final RealExamBeans.RealExamBean cardBean = (RealExamBeans.RealExamBean) model.data;
                            PaperBean paper = cardBean.paper;
                            if (paper != null && paper.modules != null && paper.questions != null && paper.questions.size() > 0) {
                                if (cardBean.status == 1 || cardBean.status == 2) {          // 答题
                                    Intent intent = new Intent(MyScanActivity.this, ArenaAnswerCardActivity.class);
                                    intent.putExtra("cardBean", cardBean);
                                    startActivity(intent);
                                } else if (cardBean.status == 3) {                            // 跳转报告页面
                                    if (cardBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {                                          // 模考大赛报告
                                        Intent intent = new Intent(mActivity, ScReportActivity.class);
                                        intent.putExtra("tag", 1);

                                        Bundle arg = new Bundle();
                                        arg.putLong("practice_id", cardBean.paper.id);

                                        intent.putExtra("arg", arg);
                                        mActivity.startActivity(intent);
                                    } else {                                                            // 真题演练报告
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("practice_id", cardBean.id);
                                        bundle.putBoolean("show_statistic", true);
                                        ArenaExamReportActivity.show(MyScanActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
                                    }
                                }
                            } else {
                                ToastUtils.showShort("试卷信息错误");
                            }
                        } else {
                            ToastUtils.showShort("试卷信息错误");
                        }
                        MyScanActivity.this.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        if (e instanceof ApiException) {
                            String errorMsg = ((ApiException) e).getErrorMsg();
                            ToastUtils.showShort(errorMsg);
                        } else {
                            ToastUtils.showShort("网络错误");
                        }
                        MyScanActivity.this.finish();
                    }
                });
            } else if (answerId > 0) {      // 错题导出试卷，扫码
                compositeSubscription.add(RetrofitManager.getInstance().getService().getPractiseDetails(answerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<RealExamBeans>() {
                            @Override
                            public void onCompleted() {
                                hideProgress();
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                hideProgress();
                                CommonUtils.showToast("获取练习信息失败，请您重试");
                                MyScanActivity.this.finish();
                            }

                            @Override
                            public void onNext(RealExamBeans practiceDetailsBeans) {
                                hideProgress();
                                if (practiceDetailsBeans != null && "1000104".equals(practiceDetailsBeans.code)) {
                                    String content = "二维码账号信息不符，请核对账号重新扫描！";
                                    final CustomConfirmDialog dialog = DialogUtils.createDialog(MyScanActivity.this, "", content);
                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            MyScanActivity.this.finish();
                                        }
                                    });
                                    dialog.setBtnDividerVisibility(false);
                                    dialog.setCancelBtnVisibility(false);
                                    dialog.setMessage(content, 18);
                                    dialog.setOkBtnConfig(200, 20, R.drawable.eva_explain_btn_bg);
                                    dialog.setContentGravity(Gravity.CENTER_HORIZONTAL);
                                    dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
                                    dialog.setPositiveButton("知道了", null);
                                    dialog.setTitleBold();
                                    View contentView = dialog.getContentView();
                                    LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
                                    layoutParams.height = DisplayUtil.dp2px(66);
                                    contentView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    if (practiceDetailsBeans != null && !"1000000".equals(practiceDetailsBeans.code)) {
                                        CommonUtils.showToast("获取练习信息失败，" + practiceDetailsBeans.message);
                                        return;
                                    }
                                    if (practiceDetailsBeans == null || practiceDetailsBeans.data == null || practiceDetailsBeans.data.paper == null) {
                                        CommonUtils.showToast("获取练习信息失败，请您重试");
                                        return;
                                    }
                                    RealExamBeans.RealExamBean cardBean = practiceDetailsBeans.data;
                                    PaperBean paper = cardBean.paper;
                                    if (paper != null && paper.modules != null && paper.questions != null && paper.questions.size() > 0) {
                                        if (cardBean.status == 1 || cardBean.status == 2) {          // 答题
                                            Intent intent = new Intent(MyScanActivity.this, ArenaAnswerCardActivity.class);
                                            intent.putExtra("cardBean", cardBean);
                                            startActivity(intent);
                                        } else if (cardBean.status == 3) {                            // 跳转报告页面
                                            Bundle bundle = new Bundle();
                                            bundle.putLong("practice_id", cardBean.id);
                                            bundle.putBoolean("show_statistic", true);
                                            ArenaExamReportExActivity.show(MyScanActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT, bundle);
                                        }
                                    } else {
                                        ToastUtils.showShort("试卷信息错误");
                                    }
                                    MyScanActivity.this.finish();
                                }
                            }
                        }));
            } else {
                hideProgress();
                ToastUtils.showShort("试卷id错误");
                MyScanActivity.this.finish();
            }
        } else {

            DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (barcodeScannerView != null) {
                        barcodeScannerView.resume();
                    }
                }
            }, "扫描结果", result, null, "知道了").setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (barcodeScannerView != null) {
                        barcodeScannerView.resume();
                    }
                }
            });
//            ------fengexian----
//
//            try {
//                byte[] intent = TripleDES.hex2byte(result);
//                byte[] desUrl = TripleDES.decrypt(intent, this.key.getBytes());
//                str = new String(desUrl);
//            } catch (Exception var10) {
//                ;
//            }
//
//            if(!"MainActivity".equals(this.activity_from)) {
//                this.scansign(result);
//            } else {
//                if(result != null && result.startsWith("HTJY_IM_PERSONAL_")) {
//                    if(!this.sp_config.getBoolean("iflogin", false)) {
//                        intent1 = new Intent(this, LoginActivity.class);
//                        this.startActivity(intent1);
//                        return;
//                    }
//
//                    intent1 = new Intent(this, FriendDetailsInfoActivity.class);
//                    intent1.putExtra("Qrcode", result.substring(17));
//                    intent1.putExtra("Search_Type", "TYPE_SEARCHGROUP");
//                    intent1.putExtra("From", "ScanActivity");
//                    intent1.putExtra("deleteFlag", false);
//                } else if(result != null && result.startsWith("HTJY_CAMPUS_BINDING_")) {
//                    if(!this.sp_config.getBoolean("iflogin", false)) {
//                        intent1 = new Intent(this, LoginActivity.class);
//                        this.startActivity(intent1);
//                        return;
//                    }
//
//                    intent1 = new Intent(this, BranchDetailsActivity.class);
//                    intent1.putExtra("Qrcode", result);
//                    intent1.putExtra("From", "ScanActivity");
//                } else if(result != null && result.startsWith("HTJY_CAMPUS_REBATE_")) {
//                    if(!this.sp_config.getBoolean("iflogin", false)) {
//                        intent1 = new Intent(this, LoginActivity.class);
//                        this.startActivity(intent1);
//                        return;
//                    }
//
//                    intent1 = new Intent(this, PersonalRebate.class);
//                    intent1.putExtra("Qrcode", result);
//                    intent1.putExtra("From", "ScanActivity");
//                } else {
//                    if(str != null && str.startsWith("TKZ")) {
//                        if(!this.sp_config.getBoolean("iflogin", false)) {
//                            intent1 = new Intent(this, LoginActivity.class);
//                            this.startActivity(intent1);
//                            return;
//                        }
//
//                        desUrl1 = str.substring(str.indexOf("_") + 1);
//                        this.sendStudentSignInfo(this.uid, this.remark, desUrl1, this.signTime);
//                        return;
//                    }
//
//                    if(str != null && str.startsWith(this.classCardStartNum)) {
//                        if(!this.sp_config.getBoolean("iflogin", false)) {
//                            intent1 = new Intent(this, LoginActivity.class);
//                            this.startActivity(intent1);
//                            return;
//                        }
//
//                        this.scansign(result);
//                        return;
//                    }
//
//                    try {
//                        byte[] e = TripleDES.hex2byte(result);
//                        byte[] desBytesArr = TripleDES.decrypt(e, this.key.getBytes());
//                        desStr = URLDecoder.decode(new String(desBytesArr));
//                        desUrl1 = desStr + "&pwd=htjyapp";
//                    } catch (Exception var9) {
//                        desUrl1 = result;
//                    }
//
//                    intent1 = new Intent(this, LiveActivity.class);
//                    intent1.putExtra("From", "ScanActivity");
//                    intent1.putExtra("url", desUrl1);
//                }
//
//                this.startActivity(intent1);
//                this.finish();
//        }
        }
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
//                barcodeScannerView.setStatusText(result.getText());
                barcodeScannerView.pause();
                try {
                    handleDecode(result.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    MyScanActivity.this.finish();
                }
            }
//            //Added preview of scanned barcode
//            ImageView imageView = (ImageView) findViewById(R.id.iv_scanImage);
//            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    //获取扫描的当前时间
    private String getDate() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new java.util.Date());
    }

    private long getLongDate(String date, String time) {
        long ltime = 0;
        String strDate = date + " " + time;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date parse = simpleDateFormat.parse(strDate);
            ltime = parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ltime;
    }

    //获取roleid身份信息
    private String getRoleId() {
        return sp.getString("roleid", "");
    }

    private final CameraPreview.StateListener stateListener = new CameraPreview.StateListener() {
        @Override
        public void previewSized() {
            if (barcodeScannerView != null && barcodeScannerView.getViewFinder() != null) {
                int position = ((CustomViewfinderView) barcodeScannerView.getViewFinder()).getFrameHeight();
                if (position > 0)
                    barcodeScannerView.getStatusView().setY(position + 12);
            }
        }

        @Override
        public void previewStarted() {

        }

        @Override
        public void previewStopped() {

        }

        @Override
        public void cameraError(Exception error) {
            displayFrameworkBugMessageAndExit();
        }

        public void cameraClosed() {

        }
    };

    protected void displayFrameworkBugMessageAndExit() {
        if (isFinishing() || isDestoryed) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("camera相机");
        builder.setMessage("相机启动失败！请重新打开相机。");
        builder.setPositiveButton(com.google.zxing.client.android.R.string.zxing_button_ok, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
