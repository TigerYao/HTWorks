package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.livecore.models.LPAnswerSheetModel;
import com.baijiahulian.livecore.models.LPAnswerSheetOptionModel;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.BaiJiaVideoPlayerImpl;
import com.huatu.handheld_huatu.ui.ShadowDrawable;
import com.huatu.handheld_huatu.ui.recyclerview.SelectItemAdapter;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveVideoTestView extends FrameLayout {
    @BindView(R.id.send_answer_btn)
    TextView mSendAnswer;
    @BindView(R.id.test_options_rv)
    RecyclerView mOptionsList;
    @BindView(R.id.title_vertical)
    View mVerTitleView;
    @BindView(R.id.rv_container)
    RelativeLayout rvContainerView;
    private LiveTestOptionsAdapter mOptionsAdaper;
    private GridLayoutManager gridLayoutManager;
    private BaiJiaVideoPlayerImpl mPlayer;
    private List<String> answer;
    private List<String> rightAnswer;
    private LPAnswerSheetModel lpAnswerSheetModel;
    private LayoutInflater mInflater;

    public LiveVideoTestView(@NonNull Context context) {
        this(context, null);
    }

    public LiveVideoTestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoTestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context ctx) {
        mInflater = LayoutInflater.from(ctx);
        mInflater.inflate(R.layout.layout_videotest, this);
        ButterKnife.bind(this);
        mOptionsAdaper = new LiveTestOptionsAdapter(ctx, null);
        mOptionsAdaper.setOnItemSingleSelectListener(new SelectItemAdapter.OnItemSingleSelectListener() {
            @Override
            public void onSelected(int itemPosition, boolean isSelected) {
                if (itemPosition == -1)
                    return;
                updateSendBtn(mOptionsAdaper.getSingleSelectedPosition() != -1);
                if (answer == null)
                    answer = new ArrayList<>();
                String value = (itemPosition + 1) + "";
                answer.clear();
                if (!answer.contains(value) && isSelected)
                    answer.add(value);
            }
        });
        mOptionsAdaper.setSelectMode(SelectItemAdapter.SelectMode.SINGLE_SELECT);
        gridLayoutManager = new GridLayoutManager(ctx, 4);
        mOptionsList.setLayoutManager(gridLayoutManager);
        mOptionsList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.common_20dp)));
        mOptionsList.setAdapter(mOptionsAdaper);
        mOptionsAdaper.setOnItemMultiSelectListener(new SelectItemAdapter.OnItemMultiSelectListener() {
            @Override
            public void onSelected(SelectItemAdapter.Operation operation, int itemPosition, boolean isSelected) {
                if (itemPosition == -1)
                    return;
                if (answer == null)
                    answer = new ArrayList<>();
                String value =(itemPosition + 1) + ""; //mOptionsAdaper.getItemText(itemPosition);
                if (!answer.contains(value) && isSelected)
                    answer.add(value);
                else if (answer.contains(value) && !isSelected)
                    answer.remove(value);
                updateSendBtn(answer.size() > 0);
            }
        });

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setBackgroundResource(R.color.white);
        updateSendBtn(false);
    }

    public void setOptions(LPAnswerSheetModel lpAnswerSheetModel, boolean isLand, BaiJiaVideoPlayerImpl player) {
        if (rightAnswer != null)
            rightAnswer.clear();
        if (answer != null)
            answer.clear();
        if (lpAnswerSheetModel != null && lpAnswerSheetModel.options != null && !lpAnswerSheetModel.options.isEmpty()) {
            if (rightAnswer == null)
                rightAnswer = new ArrayList<>();
            this.lpAnswerSheetModel = lpAnswerSheetModel;
            for(int i = 0;i< lpAnswerSheetModel.options.size();i++){
                LPAnswerSheetOptionModel model = lpAnswerSheetModel.options.get(i);
                if (model.isRight)
                    rightAnswer.add((i+1)+"");
            }
            mOptionsAdaper.clearSelected();
            mOptionsAdaper.setSelectMode(rightAnswer.size() > 1 ? SelectItemAdapter.SelectMode.MULTI_SELECT : SelectItemAdapter.SelectMode.SINGLE_SELECT);
            int count = lpAnswerSheetModel.options.size();
            if (count > 4)
                count = isLand ? count < 8 ? count : 8 : 4;
            gridLayoutManager.setSpanCount(count);
            mOptionsAdaper.setData(lpAnswerSheetModel);
            mPlayer = player;
            updateSendBtn(false);
            rvContainerView.setVisibility(VISIBLE);
            mSendAnswer.setVisibility(VISIBLE);
        }
    }

    public void setFullScreen(boolean isFullScreen){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSendAnswer.getLayoutParams();
        RelativeLayout.LayoutParams listViewParams = (RelativeLayout.LayoutParams) rvContainerView.getLayoutParams();
        int count = mOptionsAdaper.getItemCount();
        destroyView();
        if (isFullScreen) {
            mVerTitleView.setVisibility(GONE);
            layoutParams.width = dpToPx(120);
            layoutParams.height = dpToPx(80);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            listViewParams.rightMargin = dpToPx(120);
            listViewParams.topMargin = 0;
            listViewParams.height = dpToPx(80);
            listViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            listViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            shapeType = ShadowDrawable.SHAPE_ROUND_TOP_RIGHT;
            offY = 0;
            mOptionsList.getLayoutParams().width = (int)(count * getResources().getDimension(R.dimen.common_dimens_60dp));
            mGravity = Gravity.FILL|Gravity.TOP;
            ShadowDrawable.setShadowDrawable(rvContainerView, Color.parseColor("#ffffff"), dpToPx(8),
                    Color.parseColor("#66000000"), dpToPx(8), 0, 0, ShadowDrawable.SHAPE_ROUND_TOP_LEFT);
            setBackgroundResource(R.color.live_transparent_opacity_02);
        }else {
            shapeType = ShadowDrawable.SHAPE_ROUND_TOP;
            mVerTitleView.setVisibility(VISIBLE);
            layoutParams.height = dpToPx(50);
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            listViewParams.rightMargin = dpToPx(0);
            listViewParams.topMargin = dpToPx(50);
            listViewParams.height = LayoutParams.WRAP_CONTENT;
            listViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            listViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mOptionsList.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            offY = (int)getResources().getDimension(R.dimen.common_dimens_100dp);
            mGravity = Gravity.CENTER;
            rvContainerView.setBackgroundColor(Color.WHITE);
            setBackgroundResource(R.color.white);
        }
        if (count > 4)
            count = isFullScreen ? count < 8 ? count : 8 : 4;
        gridLayoutManager.setSpanCount(count);
        updateSendBtn(mIsBtnEnable);
        mOptionsAdaper.notifyDataSetChanged();
        rvContainerView.setGravity(Gravity.CENTER);
    }

    private int dpToPx(int dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp + 0.5f);
    }

    private boolean mIsBtnEnable = true;
    private int shapeType = ShadowDrawable.SHAPE_ROUND_TOP;
    private int offY = 0;
    private int mGravity = Gravity.CENTER;
    private Toast mToast;

    private void updateSendBtn(boolean isEnable) {
            mIsBtnEnable = isEnable;
            mSendAnswer.setTextColor(isEnable ? Color.WHITE : Color.parseColor("#9B9B9B"));
            int bgColor = isEnable ? Color.parseColor("#EC74A0") : Color.parseColor("#e0e0e0");
            int shadowColor = isEnable ? Color.parseColor("#66EC74A0") : Color.parseColor("#66000000");
            ShadowDrawable.setShadowDrawable(mSendAnswer, bgColor, dpToPx(8), shadowColor, dpToPx(8), 0, 0, shapeType);
            mSendAnswer.setOnClickListener(!isEnable ? null : new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rightAnswer == null || answer == null || answer.isEmpty())
                        return;
                    mPlayer.sendAnswer(answer);
                    View contentView = mInflater.inflate(R.layout.toast_answer_tip_layout, null);
                    if (rightAnswer.size() != answer.size())
                        mToast = ToastUtils.makeTextTip(getContext(),false, mGravity,contentView, offY);
                    else if (rightAnswer.containsAll(answer) && answer.containsAll(rightAnswer)){
                        mToast = ToastUtils.makeTextTip(getContext(),true, mGravity,contentView, offY);
                    } else
                        mToast = ToastUtils.makeTextTip(getContext(),false, mGravity,contentView, offY);
                    mToast.show();
                    if (rightAnswer != null)
                        rightAnswer.clear();
                    if (answer != null)
                        answer.clear();
                    view.setOnClickListener(null);
                    mOptionsAdaper.clearSelected();
                    rvContainerView.setVisibility(GONE);
                    mSendAnswer.setVisibility(GONE);
                    UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mToast != null)
                                mToast.cancel();
//                            LiveVideoTestResultView layout = new LiveVideoTestResultView(getContext());
//                            layout.setData(lpAnswerSheetModel);
//                            mToast = ToastUtils.makeTextTip(getContext(),false, mGravity, layout, offY);
//                            mToast.show();
                            setVisibility(GONE);
                        }
                    }, 2000);
                }
            });
    }

    public void destroyView(){
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        if (rvContainerView.getVisibility() == GONE && getVisibility() == View.VISIBLE) {
            setVisibility(GONE);
            rvContainerView.setVisibility(VISIBLE);
            mSendAnswer.setVisibility(VISIBLE);
        }
        UniApplicationLike.getApplicationHandler().removeCallbacksAndMessages(null);
    }

}
