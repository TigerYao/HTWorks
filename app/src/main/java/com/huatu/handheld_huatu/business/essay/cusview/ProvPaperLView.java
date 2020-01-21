package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleAreaListBean;
import com.huatu.handheld_huatu.view.CommonAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michael on 17/11/21.
 */

public class ProvPaperLView extends LinearLayout {

    Context mContext;

    @BindView(R.id.sel_text_tip)
    TextView sel_text_tip;
    @BindView(R.id.sel_text_tv)
    TextView sel_text_tv;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.show_province_rl)
    RelativeLayout show_province_rl;

    private int resId = R.layout.mult_exam_prov_paper_layout;
    private View rootView;

    public ProvPaperLView(Context context) {
        super(context);
        initView(context);
    }

    public ProvPaperLView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProvPaperLView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this, rootView);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
    }

    public void refreshView(List<SingleAreaListBean> mlist, final int selPos, String tip, String txt, AdapterView.OnItemClickListener listener) {
        sel_text_tv.setText(txt);
        if (listview != null) {
            listview.setAdapter(new CommonAdapter<SingleAreaListBean>(mContext, mlist, R.layout.mult_exam_prov_paper_item_layout) {
                @Override
                public void convert(ViewHolder holder, SingleAreaListBean item, int position) {
                    holder.setText(R.id.title, item.areaName);

                    holder.setViewVisibility(R.id.tv_correct_sum, View.VISIBLE);
                    if (item.correctNum > 0 && item.manualNum > 0) {        // 都不为0
                        holder.setText(R.id.tv_correct_sum, "你已智能批改" + item.correctNum + "次，已人工批改" + item.manualNum + "次");
                    } else if (item.correctNum > 0) {                       // 智能不为0
                        holder.setText(R.id.tv_correct_sum, "你已智能批改" + item.correctNum + "次");
                    } else if (item.manualNum > 0) {                        // 人工不为0
                        holder.setText(R.id.tv_correct_sum, "你已人工批改" + item.manualNum + "次");
                    } else {
                        holder.setViewVisibility(R.id.tv_correct_sum, View.GONE);
                    }

                    if (selPos == position) {
                        holder.setTextColorRes(R.id.title, R.color.red250);
                        holder.setTextColorRes(R.id.tv_correct_sum, R.color.red250);
                    } else {
                        holder.setTextColorRes(R.id.title, R.color.gray_333333);
                        holder.setTextColorRes(R.id.tv_correct_sum, R.color.gray_333333);
                    }
                }
            });
            listview.setOnItemClickListener(listener);
        }
    }

    @OnClick({R.id.show_province_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_province_rl:
                this.setVisibility(GONE);
                return;
        }
    }

}
