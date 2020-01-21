package com.huatu.handheld_huatu.business.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.animation.BounceTopEnter;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.ABaseAdapter;
import com.huatu.handheld_huatu.mvpmodel.RegisterFreeCourseBean;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.BaseDialog;
import com.huatu.widget.LinearLayoutListView;

import java.util.ArrayList;

/**
 * Created by cjx on 2019\3\12 0012.
 */

public class CouponReceiveDialog extends BaseDialog {

    public static boolean checkShow(Context context) {
        int hasCoupon = PrefStore.getUserSettingInt(Constant.APP_COUPON_CHECK, 0);
        if (hasCoupon == 1) {

            RegisterFreeCourseBean courseBean = GsonUtil.GsonToBean(IoExUtils.getJsonString(Constant.APP_COUPON_CHECK), RegisterFreeCourseBean.class);
            if (null == courseBean) {
                PrefStore.putUserSettingInt(Constant.APP_COUPON_CHECK, 0);
                return false;
            }
            CouponReceiveDialog dialog2 = new CouponReceiveDialog(context, courseBean);
            dialog2
                    .showAnim(new BounceTopEnter())//
                    .dismissAnim(new AnimUtils.SlideBottomExit(context));//
            dialog2.show();
            return true;
        }
        return false;
    }

    RegisterFreeCourseBean mFreeCourseBean;

    public CouponReceiveDialog(Context context, RegisterFreeCourseBean couponItemlist) {
        super(context);
        mFreeCourseBean = couponItemlist;
        init();
    }

    boolean mHasJump = false;

    private void init() {
        widthScale(0.7f);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!mHasJump && (context instanceof MainTabActivity)) {
                    ((MainTabActivity) context).changeTipSet();
                }
            }
        });
    }

    @Override
    public View onCreateView() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.coupon_login_dialog, null);
        return rootView;
    }

    private LinearLayoutListView mCouponCardList;

    @SuppressWarnings("deprecation")
    @Override
    public void setUiBeforShow() {
        this.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.findViewById(R.id.quick_look_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UIHelper.openInnerBrowser(getContext(), "http://www.baidu.com");
                //  UserCouponFragment.lanuch(getContext(), 0);
                Intent intent = new Intent(context, MainTabActivity.class);
                intent.putExtra("require_index", 2);
                context.startActivity(intent);
                mHasJump = true;
                dismiss();
            }
        });

        ((TextView) this.findViewById(R.id.tv_need_gold)).setText(String.format("+%d图币", mFreeCourseBean.rcoin));
        ((TextView) this.findViewById(R.id.tv_growup_value)).setText(String.format("+%d成长值", mFreeCourseBean.rgrowUpValue));
        ((TextView) this.findViewById(R.id.title_name_txt)).setText(mFreeCourseBean.rtitle + "");

        int size = ArrayUtils.size(mFreeCourseBean.rcourseList);
        if (size > 3) {
            this.findViewById(R.id.coupon_list_container).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(getContext(), 210)));
        }
        mCouponCardList = (LinearLayoutListView) this.findViewById(R.id.xi_product_list);
        mCouponCardList.setAdapter(new MyBaseAdapter((ArrayList) mFreeCourseBean.rcourseList, getContext(), true));
    }

    @Override
    public void superDismiss() {
        super.superDismiss();
        PrefStore.putUserSettingInt(Constant.APP_COUPON_CHECK, 0);
    }

    private ABaseAdapter.AbstractItemView<RegisterFreeCourseBean.CouponCourseBean> newItemView() {
        return new CouponItemView();
    }

    class MyBaseAdapter extends ABaseAdapter<RegisterFreeCourseBean.CouponCourseBean> {

        public MyBaseAdapter(ArrayList<RegisterFreeCourseBean.CouponCourseBean> datas, Context context, boolean hasStyle) {
            super(datas, context, hasStyle);
        }

        @Override
        protected AbstractItemView<RegisterFreeCourseBean.CouponCourseBean> newItemView() {
            return CouponReceiveDialog.this.newItemView();
        }
    }


    public class CouponItemView extends ABaseAdapter.AbstractItemView<RegisterFreeCourseBean.CouponCourseBean> {
        TextView mTitle, mDescription;


        private ShapeDrawable getDefaultBackground(int radius, int color, boolean isAllRound) {
            int r = DensityUtils.dp2px(context, radius);
            float[] outerR = new float[]{r, r, isAllRound ? r : 0, isAllRound ? r : 0, isAllRound ? r : 0, isAllRound ? r : 0, r, r};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

            RoundRectShape rr = new RoundRectShape(outerR, null, null);
            ShapeDrawable drawable = new ShapeDrawable(rr);
            drawable.getPaint().setColor(color);
            return drawable;
        }

        @Override
        public int inflateViewId() {
            return R.layout.coupon_login_list_item;
        }

        @Override
        public void bindingView(View convertView) {
            //ButterKnife.inject(this, convertView);
            mTitle = (TextView) convertView.findViewById(R.id.coupon_money_txt);
            mDescription = (TextView) convertView.findViewById(R.id.coupn_content_tv);
            convertView.setBackground(getDefaultBackground(2, 0xFFFFE7E9, true));
        }

        @Override
        public void bindingData(View convertView, RegisterFreeCourseBean.CouponCourseBean data) {

            mTitle.setText(data.name);
            mDescription.setText(data.time + "   共" + data.lesson + "课时");
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        superDismiss();
    }
}
