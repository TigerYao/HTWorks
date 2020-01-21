package com.huatu.handheld_huatu.business.arena.fragment;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ProgressBar;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CustomHeadView;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.base.BaseListFragment;

import java.util.ArrayList;

/**
 * Created by saiyuan on 2016/11/24.
 * （不知道是什么页面）
 */

public class SampleFragment extends BaseListFragment {

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<ArenaDetailBean.Result>(mActivity.getApplicationContext(),
                dataList, R.layout.list_item_arena_statistic_layout) {
            @Override
            public void convert(ViewHolder holder, ArenaDetailBean.Result item, int position) {
                String correctNum = String.valueOf(item.rcount);
                SpannableStringBuilder ssbCorrect = new SpannableStringBuilder("答对 " + correctNum + " 道题");
                int length = correctNum.length();
                ssbCorrect.setSpan(new ForegroundColorSpan(UniApplicationContext.getContext().getResources().getColor(
                        R.color.arena_exam_statistic_item_orange_color)),
                        3, 3 + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder ssbUsedTime;
                holder.setSsbText(R.id.arena_statistic_list_item_correct_tv, ssbCorrect);

                String min = String.valueOf(item.elapsedTime / 60);
                String sec = String.valueOf(item.elapsedTime % 60);
                if (min.length() == 1) {
                    min = "0" + min;
                }
                if (sec.length() == 1) {
                    sec = "0" + sec;
                }
                String strTime = String.format("%s'%s\"", min, sec);
                SpannableStringBuilder ssbTime = new SpannableStringBuilder("用时 " + strTime);
                length = strTime.length();
                ssbTime.setSpan(new ForegroundColorSpan(UniApplicationContext.getContext().getResources().getColor(
                        R.color.arena_exam_statistic_item_orange_color)),
                        3, 3 + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.setSsbText(R.id.arena_statistic_list_item_used_time_tv, ssbTime);

                ProgressBar progressBar = holder.getView(R.id.arena_statistic_list_item_progress);
                progressBar.setMax(10);
                progressBar.setProgress(item.rcount);
                CustomHeadView headView = holder.getView(R.id.arena_statistic_list_item_user_head);
                headView.setEdgingColor(getResources().getColor(R.color.arena_exam_statistic_item_orange_color));
                if (item.userInfo != null) {
                    if (UserInfoUtil.userId == item.userInfo.uid) {
                        holder.getConvertView().setBackgroundResource(R.color.arena_exam_statistic_item_bg_color);
                    } else {
                        holder.getConvertView().setBackgroundResource(R.color.white);
                    }
                    holder.setText(R.id.arena_statistic_list_item_user_name, item.userInfo.nick);
                    headView.setHeadUrl(item.userInfo.avatar);
                }
            }
        };
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        topActionBar.setTitle("测试", Color.BLUE);
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.showButtonText("右侧", TopActionBar.RIGHT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    protected void onLoadData() {
        ArrayList<ArenaDetailBean.Result> data = new ArrayList<>();
        ArenaDetailBean.Result result0 = new ArenaDetailBean.Result();
        result0.uid = 34693;
        result0.elapsedTime = 200;
        result0.rcount = 5;
        result0.userInfo = new ArenaDetailBean.Player(13117013);
        result0.userInfo.nick = "砖题库";
        result0.userInfo.avatar = "http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png";
        ArenaDetailBean.Result result1 = new ArenaDetailBean.Result();
        result1.uid = 3423;
        result1.elapsedTime = 250;
        result1.rcount = 5;
        result1.userInfo = new ArenaDetailBean.Player(34693);
        result1.userInfo.nick = "奋斗的小爆爆";
        result1.userInfo.avatar = "http://tiku.huatu.com/cdn/images/vhuatu/avatars/l/lMIkOc5PsQFCSrO94xAxR4U9ULf.jpg";
        ArenaDetailBean.Result result2 = new ArenaDetailBean.Result();
        result2.uid = 12345;
        result2.elapsedTime = 230;
        result2.rcount = 3;
        result1.userInfo = new ArenaDetailBean.Player(12345);
        result1.userInfo.nick = "采梦abcd";
        result1.userInfo.avatar = "http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png";
        data.add(result0);
        data.add(result1);
        data.add(result2);
        data.add(result0);
        data.add(result1);
        data.add(result2);
        data.add(result0);
        data.add(result1);
        data.add(result2);
        onSuccess(data, true);
    }

    @Override
    public void onRefresh() {
        onLoadData();
    }

    @Override
    public void onLoadMore() {

        ArrayList<ArenaDetailBean.Result> data = new ArrayList<>();
        ArenaDetailBean.Result result0 = new ArenaDetailBean.Result();
        result0.uid = 34693;
        result0.elapsedTime = 200;
        result0.rcount = 5;
        result0.userInfo = new ArenaDetailBean.Player(13117013);
        result0.userInfo.nick = "华图在线";
        result0.userInfo.avatar = "http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png";
        ArenaDetailBean.Result result1 = new ArenaDetailBean.Result();
        result1.uid = 3423;
        result1.elapsedTime = 250;
        result1.rcount = 5;
        result1.userInfo = new ArenaDetailBean.Player(34693);
        result1.userInfo.nick = "奋斗的小爆爆";
        result1.userInfo.avatar = "http://tiku.huatu.com/cdn/images/vhuatu/avatars/l/lMIkOc5PsQFCSrO94xAxR4U9ULf.jpg";
        ArenaDetailBean.Result result2 = new ArenaDetailBean.Result();
        result2.uid = 12345;
        result2.elapsedTime = 230;
        result2.rcount = 3;
        result1.userInfo = new ArenaDetailBean.Player(12345);
        result1.userInfo.nick = "采梦abcd";
        result1.userInfo.avatar = "http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png";
        data.add(result0);
        data.add(result1);
        data.add(result2);
        data.add(result0);
        data.add(result1);
        data.add(result2);
        data.add(result0);
        data.add(result1);
        data.add(result2);
        onSuccess(data, false);
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void dismissProgressBar() {

    }


}
