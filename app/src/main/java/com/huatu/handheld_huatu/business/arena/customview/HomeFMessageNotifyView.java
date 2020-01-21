package com.huatu.handheld_huatu.business.arena.customview;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ZtkSchemeTargetStartTo;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * 首页，行测的提示View条
 */
public class HomeFMessageNotifyView extends RelativeLayout {

    private Context mContext;
    private View rootView;
    @BindView(R.id.homef_message_notify_content)
    TextView messageNotifyContent;
    @BindView(R.id.homef_message_notify_close)
    ImageView messageNotifyClose;

    private int resId = R.layout.layout_partv_homef_message_notify_rl;
    private int requestType;
    private String tagFrom;
    private boolean isAttached = false;
    private boolean canShowTip = true;
    private List<String> closeMsgList;
    private String curMessage;
    private final String TAG = this.getClass().getSimpleName();
    private AdvertiseConfig mAdvertiseConfig;
    private CompositeSubscription compositeSubscription;

    public HomeFMessageNotifyView(Context context) {
        super(context);
        init(context);
    }

    public HomeFMessageNotifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeFMessageNotifyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        initCloseMsgList();
        initView();
    }

    private void initCloseMsgList() {
        closeMsgList = new ArrayList<>();
        String closeMsgsStr = SpUtils.getHomefMsgCloseList();
        List<String> varlist = (List<String>) GsonUtil.string2JsonObject(closeMsgsStr, new TypeToken<List<String>>() {
        }.getType());
        if (varlist != null) {
            closeMsgList.addAll(varlist);
            if (closeMsgList.size() > 10) {
                String var = closeMsgList.get(closeMsgList.size() - 1);
                closeMsgList.clear();
                closeMsgList.add(var);
            }
        }
    }

    private void initView() {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
        updateCloseView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
        writeSpRecordClose();
    }

    public void updateViews(AdvertiseConfig mAdvertiseConfig, CompositeSubscription compositeSubscription) {
        if (!isAttached || mAdvertiseConfig == null || mAdvertiseConfig.params == null) {
            LogUtils.e(TAG, "updateViews direct return because !isAttached || TextUtils.isEmpty(AdvertiseConfig) ");
            updateCloseView();
            return;
        }
        this.mAdvertiseConfig = mAdvertiseConfig;
        String msg = mAdvertiseConfig.params.title;

        if (!closeMsgList.contains(msg)) {
            updateOpenView(msg);
            curMessage = msg;
        } else {
            updateCloseView();
        }

        LogUtils.i(TAG, "updateViews: closeMsgList " + closeMsgList);
        LogUtils.i(TAG, "updateViews: closeMsgList.size() " + closeMsgList.size());
        LogUtils.i(TAG, "updateViews: msg " + msg);
        LogUtils.i(TAG, "updateViews: curMessage " + curMessage);
        this.compositeSubscription = compositeSubscription;
    }

    @OnClick({
            R.id.homef_message_notify_close,            // 关闭提示条
            R.id.homef_message_notify_content           // 提示条跳转
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.homef_message_notify_close:
                addCloseMsg(curMessage);
                updateCloseView();
                writeSpRecordClose();
                break;
            case R.id.homef_message_notify_content:
                if (this.mAdvertiseConfig != null && mAdvertiseConfig.params != null) {
//                     Intent intent = new Intent(mContext, BuyDetailsActivity.class);
//                     intent.putExtra("AdvNetClassId", mAdvertiseConfig.getParams().getRid());
//                     mContext.startActivity(intent);
                    ZtkSchemeTargetStartTo.startTo((Activity) mContext, mAdvertiseConfig.params, mAdvertiseConfig.target, false, compositeSubscription);
                }
                break;
        }
    }

    private void writeSpRecordClose() {
        String var = GsonUtil.toJsonStr(closeMsgList);
        if (var != null) {
            SpUtils.setHomefMsgCloseList(var);
        }
    }

    private void addCloseMsg(String msg) {
        if (!closeMsgList.contains(msg)) {
            closeMsgList.add(msg);
        }
    }

    private void updateCloseView() {
        setVisibility(GONE);
        if (getLayoutParams() != null) {
            getLayoutParams().height = DisplayUtil.dp2px(10);
        }
        requestLayout();
    }

    private void updateOpenView(String msg) {
        if (!closeMsgList.contains(msg) && msg != null && !msg.equals(curMessage)) {
            setVisibility(VISIBLE);
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.homef_notify_bg));
            getLayoutParams().height = -2;
            messageNotifyContent.setText(msg);
        }
    }
}
