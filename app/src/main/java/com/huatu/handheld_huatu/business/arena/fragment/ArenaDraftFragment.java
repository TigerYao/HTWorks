package com.huatu.handheld_huatu.business.arena.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.custom.MZtkDraftPagerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by saiyuan on 2016/10/26.
 * 草稿纸Fragment
 */
public class ArenaDraftFragment extends BaseFragment {

    @BindView(R.id.ll_draft_paper_top)
    LinearLayout ll_draft_paper_top;
    @BindView(R.id.rl_draft_paper_close)
    RelativeLayout rl_draft_paper_close;
    @BindView(R.id.iv_draft_paper_close)
    ImageView iv_draft_paper_close;
    @BindView(R.id.rl_draft_paper_delete)
    RelativeLayout rl_draft_paper_delete;
    @BindView(R.id.iv_draft_paper_delete)
    ImageView iv_draft_paper_delete;
    @BindView(R.id.rl_draft_paper_undo)
    RelativeLayout rl_draft_paper_undo;
    @BindView(R.id.iv_draft_paper_undo)
    ImageView iv_draft_paper_undo;
    @BindView(R.id.rl_draft_paper_redo)
    RelativeLayout rl_draft_paper_redo;
    @BindView(R.id.iv_draft_paper_redo)
    ImageView iv_draft_paper_redo;
    @BindView(R.id.draft_paper_view)
    MZtkDraftPagerView draft_paper_view;
    @BindView(R.id.draft_paper_tips_layout)
    ViewGroup layoutTips;
    @BindView(R.id.draft_paper_tips_tv)
    TextView tvTips;

    private int currentItem;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_arena_draft_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        currentItem = args.getInt("currentItem");
        draft_paper_view.initView(currentItem);
        if (SpUtils.getDraftTipsShow()) {
            SpUtils.setDraftTipsShow(false);
            layoutTips.setVisibility(View.VISIBLE);
            SpannableString sb = new SpannableString("双指按住，上下左右拖动稿纸");
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#fe7f31")), 0, 2, SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#fe7f31")), 9, 11, SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTips.setText(sb, TextView.BufferType.SPANNABLE);
            layoutTips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutTips.setVisibility(View.GONE);
                }
            });
        }
        setColor();
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            ll_draft_paper_top.setBackgroundResource(R.color.arena_top_title_bg);
        } else {
            ll_draft_paper_top.setBackgroundResource(R.color.arena_top_title_bg_night);
        }
    }

    @OnClick(R.id.rl_draft_paper_close)
    public void onBack() {
        //关闭草稿纸
        mActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.rl_draft_paper_delete)
    public void onDelete() {
        //删除内容
        draft_paper_view.removeAllPaint();
    }

    @OnClick(R.id.rl_draft_paper_undo)
    public void onUndo() {
        //撤销
        draft_paper_view.undo();
    }

    @OnClick(R.id.rl_draft_paper_redo)
    public void onReDo() {
        //取消撤销
        draft_paper_view.redo();
    }

    // 根据接收的event 改变顶部按钮的状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(MessageEvent event) {
        int day_night_mode = SpUtils.getDayNightMode();
        if (event.message == 11) {
            //夜间模式，该页未保存东西
            if (day_night_mode != 0) {
                iv_draft_paper_redo.setImageResource(R.mipmap.icon_draftpaper_redo_none_night);
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_none_night);
                iv_draft_paper_undo.setImageResource(R.mipmap.icon_draftpaper_undo_none_night);
            }
        }
        if (event.message == 10) {
            //夜间模式，且该页保存的有东西
            if (day_night_mode != 0) {
                iv_draft_paper_redo.setImageResource(R.mipmap.icon_draftpaper_redo_none_night);
            }
        }
        if (event.message == 2) {
            if (day_night_mode == 0) {
                iv_draft_paper_undo.setImageResource(R.mipmap.icon_draftpaper_undo_ok);
            } else {
                iv_draft_paper_undo.setImageResource(R.mipmap.icon_draftpaper_undo_ok_night);
            }
        } else if (event.message == 3) {
            if (day_night_mode == 0) {
                iv_draft_paper_redo.setImageResource(R.mipmap.icon_draftpaper_redo_ok);
            } else {
                iv_draft_paper_redo.setImageResource(R.mipmap.icon_draftpaper_redo_ok_night);
            }
        } else if (event.message == 4) {
            if (day_night_mode == 0) {
                iv_draft_paper_undo.setImageResource(R.mipmap.icon_draftpaper_undo_none);
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_none);
            } else {
                iv_draft_paper_undo.setImageResource(R.mipmap.icon_draftpaper_undo_none_night);
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_none_night);
            }
        } else if (event.message == 5) {
            if (day_night_mode == 0) {
                iv_draft_paper_redo.setImageResource(R.mipmap.icon_draftpaper_redo_none);
            } else {
                iv_draft_paper_redo.setImageResource(R.mipmap.icon_draftpaper_redo_none_night);
            }
        } else if (event.message == 6) {
            if (day_night_mode == 0) {
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_ok);
            } else {
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_ok_night);
            }
        } else if (event.message == 7) {
            if (day_night_mode == 0) {
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_none);
            } else {
                iv_draft_paper_delete.setImageResource(R.mipmap.icon_draftpaper_delete_none_night);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (draft_paper_view != null) {
            draft_paper_view.onDestroyResource(currentItem);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public static ArenaDraftFragment newInstance(int num) {
        ArenaDraftFragment fragment = new ArenaDraftFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("currentItem", num);
        fragment.setArguments(bundle);
        return fragment;
    }
}
