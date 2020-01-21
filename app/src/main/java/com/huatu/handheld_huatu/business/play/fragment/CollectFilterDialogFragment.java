package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsDialogFragment;
import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;
import com.huatu.handheld_huatu.business.play.bean.OptionsInfo;
import com.huatu.handheld_huatu.business.play.view.FlowLayoutManager;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.listener.DetachableDialogDismissListener;
import com.huatu.handheld_huatu.listener.DetachableDialogShowListener;
import com.huatu.handheld_huatu.ui.ShadowDrawable;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.SimpleCommonRVAdapter;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CollectFilterDialogFragment extends AbsDialogFragment implements DialogInterface.OnShowListener {

    private String mTitle;
    private String mPrice;
    private String mOrginPrice;
    private int mBuyNum;
    private CourseDetailBean.FilterOptions filterOptions;
    private List<OptionsInfo> mFilterList;
    private int mSeletedPosition = -1;
//    private boolean mIsComfirm = false;
    private OptionsInfo mSelectedOption;

    private TextView mFilterTitle;
    private TextView mBuyNumView;
    private TextView mTitleView;
    private TextView mPriceView;
    private TextView mOriginPriceView;
    private Button mOkView;
    private RecyclerView mLettersLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private SimpleCommonRVAdapter mAdapter;
    private View mShadow;
    private boolean iso2o = false;

    public static CollectFilterDialogFragment getInstance(OptionsInfo selectOptions, CourseDetailBean.FilterOptions filterOpetions) {
        Bundle args = new Bundle();
        CollectFilterDialogFragment tmpFragment = new CollectFilterDialogFragment();
        args.putSerializable("bean", selectOptions);
        args.putSerializable("option", filterOpetions);
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    public static CollectFilterDialogFragment getInstance(CourseDetailBean courseDetailBean) {
        Bundle args = new Bundle();
        CollectFilterDialogFragment tmpFragment = new CollectFilterDialogFragment();
        args.putSerializable("courseBean", courseDetailBean);
        tmpFragment.setArguments(args);
        return tmpFragment;
    }


    DetachableDialogShowListener mShowListener;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mShowListener) {
            mShowListener.clear();
            mShowListener = null;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("bean")) {
            OptionsInfo info = (OptionsInfo) args.getSerializable("bean");
            if(info != null) {
                mSelectedOption = info;
                mTitle = info.title;
                mPrice = info.actualPrice;
                mOrginPrice = info.price;
                mBuyNum = Integer.parseInt(info.buyNum);
                iso2o = info.iso2o;
            }
            filterOptions = (CourseDetailBean.FilterOptions) args.getSerializable("option");
//            mFilterList = (List<OptionsInfo>) args.getSerializable("list");
            mFilterList = filterOptions.list;
            mSeletedPosition = mFilterList.indexOf(info);
            LogUtils.d("onCreate1...." + mTitle);
        }else if(args.containsKey("courseBean")){
            CourseDetailBean bean = (CourseDetailBean)args.getSerializable("courseBean");
            filterOptions = bean.iso2o ? bean.o2oFilterList : bean.filterList;
            mFilterList = filterOptions.list;
            mTitle = bean.classTitle;
            mPrice = bean.actualPrice;
            mOrginPrice = bean.price;
            mBuyNum = bean.buyNum;
            mFilterList.get(0).chose = true;
            iso2o = bean.iso2o;
            mSeletedPosition = 0;
        }
        LogUtils.d("onCreate...." + mTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.collect_filter_dialog, null);
        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        mLettersLayout = view.findViewById(R.id.xi_comm_page_list);
        mShowListener = DetachableDialogShowListener.wrap(this);
        dialog.setOnShowListener(mShowListener);
        mFilterTitle = view.findViewById(R.id.filter_title);
        mBuyNumView = view.findViewById(R.id.course_detail_buy_num_tv);
        mTitleView = view.findViewById(R.id.filter_title_txt);
        mPriceView = view.findViewById(R.id.course_detail_price_tv);
        mOriginPriceView = view.findViewById(R.id.course_detail_origin_price_tv);
        mOriginPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mShadow = view.findViewById(R.id.view_top_shadow);
        ShadowDrawable.setShadowDrawable(mShadow, Color.parseColor("#ffffff"), DensityUtils.dp2px(getContext(), 8),
                Color.parseColor("#66000000"), DensityUtils.dp2px(getContext(), 8), 0, 0, ShadowDrawable.SHAPE_TOP);
        mOkView = view.findViewById(R.id.tv_dialog_ok);
        mOkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick())
                    return;
//                if (mIsComfirm)
//                    ((BaseIntroActivity) getActivity()).onClickSingeBuy();
//                else
                    onSelected(mFilterList.get(mSeletedPosition), true);
                dismiss();
            }
        });
        mFilterTitle.setText(filterOptions.title);
        mLayoutManager = iso2o ? new FlowLayoutManager(getContext(), false) : new GridLayoutManager(getContext(), 4);
        mLettersLayout.setLayoutManager(mLayoutManager);
        mAdapter = new SimpleCommonRVAdapter<OptionsInfo>(getContext(), mFilterList, R.layout.collect_filter_item_layout) {
            @Override
            public void convert(SimpleViewHolder holder, final OptionsInfo item, final int position) {
                final TextView textView = holder.getView(R.id.option_tv);
                textView.setText(item.name);
                boolean isChosed = mSeletedPosition == -1 ? (mSelectedOption != null ? item.id == mSelectedOption.id : item.chose) : mSeletedPosition == position;
                textView.setSelected(isChosed);
                if (isChosed && mSelectedOption == null) {
                    onSelected(item, false);
                }
                if(!isChosed)
                    textView.setEnabled(!item.disable);
                else
                    mSeletedPosition = position;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        if (!item.disable)
                            onItemClick(position, textView);
                    }
                });
            }
        };
        ((SimpleItemAnimator)mLettersLayout.getItemAnimator()).setSupportsChangeAnimations(false);
        bindData();
        return dialog;
    }

    public void onItemClick(int position, TextView textView) {
        if (mSeletedPosition == position)
            return;
        try {
            if(mSeletedPosition != -1)
                mLayoutManager.findViewByPosition(mSeletedPosition).findViewById(R.id.option_tv).setSelected(false);
            int tmep = mSeletedPosition;
            mSeletedPosition = position;
//            mAdapter.notifyItemChanged(tmep);
            OptionsInfo info = mFilterList.get(position);
            if (!TextUtils.isEmpty(info.title)) {
                mTitle = info.title;
                mPrice = info.actualPrice;
                mOrginPrice = info.price;
                mBuyNum = Integer.parseInt(info.buyNum);
            }
            textView.setSelected(true);
            bindData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void bindData() {
        if (mBuyNum > 0) {
            mBuyNumView.setText(mBuyNum + "人购买");
        } else
            mBuyNumView.setText("");
        if(!Utils.isEmptyOrNull(mTitle))
            mTitleView.setText(mTitle);
        if (Utils.isEmptyOrNull(mPrice) || Float.parseFloat(mPrice) == 0f) {
            mPriceView.setText("免费");
            mPriceView.setTextColor(Color.parseColor("#49CF9E"));
        } else {
            mPriceView.setText("¥" + mPrice);
            mPriceView.setTextColor(Color.parseColor("#ffff3f47"));
        }
        if (!Utils.isEmptyOrNull(mOrginPrice) && !TextUtils.equals(mPrice, mOrginPrice))
            mOriginPriceView.setText("¥" + mOrginPrice);
        else
            mOriginPriceView.setText("");

        LogUtils.d("bindData");
    }

    public void setFilterList(List<OptionsInfo> filterList) {
        this.mFilterList = filterList;
        if (mAdapter != null)
            mLettersLayout.setAdapter(mAdapter);
    }

    @Override
    public void onShow(DialogInterface dialog) {
//        LogUtils.d("onShow...." + mSeletedPosition);
        if (mAdapter != null)
            mLettersLayout.setAdapter(mAdapter);
    }

    private void onSelected(OptionsInfo selectedName, boolean seleted) {
        if(!seleted && !TextUtils.isEmpty(selectedName.title)){
            mTitle = selectedName.title;
            mPrice = selectedName.actualPrice;
            mOrginPrice = selectedName.price;
            if(!TextUtils.isEmpty(selectedName.buyNum))
                mBuyNum = Integer.parseInt(selectedName.buyNum);
            bindData();
        }else if(!TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(selectedName.title)){
            selectedName.title = mTitle;
            selectedName.actualPrice = mPrice;
            selectedName.price = mOrginPrice;
            selectedName.buyNum = mBuyNum+"";
        }
        mSelectedOption = selectedName;
        ((BaseIntroActivity) getActivity()).onSelected(selectedName, mSeletedPosition, seleted);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}
