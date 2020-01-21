package com.huatu.handheld_huatu.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.utils.DensityUtils;

import org.greenrobot.eventbus.EventBus;

public class CustomBottomDialog extends Dialog {
    private Context mContext;
    private LayoutInflater mInflater;
    private View mDialogView;
    private int mLayoutId = -1;
    private TextView mTitleView;
    private SimpleLabelsLayout mLabelsLayout;
    private ScrollView mScrollverView;

    public CustomBottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initView(mInflater);
    }

    private void initView(LayoutInflater inflater) {
        mDialogView = inflater.inflate(getLayoutId() == -1 ? R.layout.dialog_redbag_tip : getLayoutId(), null);
        mDialogView.findViewById(R.id.imv_dialog_dialog_clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTitleView = (TextView) mDialogView.findViewById(R.id.tv_dialog_title);
        mLabelsLayout = (SimpleLabelsLayout) mDialogView.findViewById(R.id.rules_tips_container);
        mScrollverView = (ScrollView) mDialogView.findViewById(R.id.scrollview);
        mLabelsLayout.setOnLayoutSizeChange(new SimpleLabelsLayout.OnLayoutSizeChange() {
            @Override
            public void onSizeChange(int width, int height) {
                int halfScreenH = DensityUtils.getScreenHeight(getContext())/2;
                if (height >= halfScreenH &&  mScrollverView.getLayoutParams().height != halfScreenH)
                    mScrollverView.getLayoutParams().height = halfScreenH;
            }
        });
        onViewCreate(mDialogView);
    }

    public int getLayoutId() {
       return mLayoutId;
    }

    public void onViewCreate(View contentView){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialogView);
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    public void addItem(String title, String content){
        View view = mInflater.inflate(R.layout.item_redbag_tip_layout, null);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView contentView = (TextView) view.findViewById(R.id.tv_dialog_info);
        if (!TextUtils.isEmpty(title))
            titleView.setText(title);
        if (!TextUtils.isEmpty(content))
            contentView.setText(Html.fromHtml(content));
        mLabelsLayout.addView(view);

    }

    public void remove(int startPostion, int count){
        if (startPostion > 0)
            mLabelsLayout.removeViews(startPostion, count);
    }

    public void onLayoutChange(int height){
        if (height >= DisplayUtil.getScreenHeight()/2) {
            if (mScrollverView.canScrollVertically(1) || mScrollverView.canScrollVertically(-1))
                return;
            mScrollverView.getLayoutParams().height = DisplayUtil.getScreenHeight()/2;
        }
    }
}
