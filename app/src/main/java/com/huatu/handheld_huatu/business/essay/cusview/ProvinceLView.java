package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.view.CommonAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michael on 17/11/21.
 */

public class ProvinceLView extends LinearLayout {

    Context mContext;

    @BindView(R.id.sel_text_tip)
    TextView sel_text_tip;
    @BindView(R.id.sel_text_tv)
    TextView sel_text_tv;
    @BindView(R.id.gview)
    GridView gview;
    @BindView(R.id.show_province_rl)
    RelativeLayout show_province_rl;


    private int resId = R.layout.mult_exam_province_layout;
    private View rootView;

    public ProvinceLView(Context context) {
        super(context);
        initView(context);
    }

    public ProvinceLView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProvinceLView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this,rootView);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
    }

    public void refreshView(List<String> mlist,final int selPos,String tip,String txt,AdapterView.OnItemClickListener listener){
//        sel_text_tv.setText(txt);
        sel_text_tip.setText(tip);
        if(gview!=null) {
            gview.setAdapter(new CommonAdapter<String>(mContext, mlist, R.layout.mult_exam_province_item_layout) {
                @Override
                public void convert(ViewHolder holder, String item, int position) {
                    holder.setText(R.id.province_tv,item);
                    if(selPos ==position){
                        holder.getView(R.id.province_tv).setBackgroundResource(R.drawable.select_provice_s_bg);
                        holder.setTextColor(R.id.province_tv, ContextCompat.getColor(mContext, R.color.white));
                    }else {
                        holder.getView(R.id.province_tv).setBackgroundResource(R.drawable.select_provice_n_bg);
                        holder.setTextColor(R.id.province_tv, ContextCompat.getColor(mContext, R.color.black250));
                    }
                }
            });
            gview.setOnItemClickListener(listener);
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
