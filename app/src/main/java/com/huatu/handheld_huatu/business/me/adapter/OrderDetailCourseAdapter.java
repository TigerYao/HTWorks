package com.huatu.handheld_huatu.business.me.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.me.bean.OrderDetailData;
import com.huatu.handheld_huatu.business.me.fragment.CourseContractFragment;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.Exposition;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.WXUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by chq on 2018/8/25.
 */

public class OrderDetailCourseAdapter extends RecyclerView.Adapter<OrderDetailCourseAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflate;
    private List<OrderDetailData.Course> mCourseData;
    private String url;
    private int isCollage;
    private String collageOrderId;

    public OrderDetailCourseAdapter(Context mContext) {
        this.mContext = mContext;
        mInflate = LayoutInflater.from(mContext);
        mCourseData = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.item_order_course, parent, false);
        return new OrderDetailCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrderDetailData.Course mData = mCourseData.get(position);
        if (mData.title != null) {
            holder.tv_title.setText(mData.title);
        }
        if (isCollage == 0 || isCollage == 2) {
//            普通订单
            //老师头像
            if (mData.teachers != null && mData.teachers.size() != 0) {
                holder.ll_teacher.removeAllViews();
                int l = mData.teachers.size();

                if (l > 3) {
                    l = 3;
                }
                for (int i = 0; i < l; i++) {
                    LinearLayout ll_teacher = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_teacher2, null, false);
                    ImageView imageView = ll_teacher.findViewById(R.id.civ_teacher_photo);
                    TextView textView = ll_teacher.findViewById(R.id.tv_teacher_name);
                    // Glide.with(UniApplicationContext.getContext()).load(mResult.teacher.get(i).roundPhoto).into(imageView);

                    ImageLoad.displaynoCacheUserAvater(mContext,mData.teachers.get(i).roundPhoto,imageView,R.mipmap.user_default_avater);
                    textView.setText(mData.teachers.get(i).teacherName);
                    holder.ll_teacher.addView(ll_teacher);
                }
            }
            if (mData.lessonCount != null) {
                holder.tv_lesson_count.setText(mData.brief);
            }
            if (mData.price != null) {
                holder.tv_final_price.setText("¥ " + mData.price);
            }
          /*  if (!mData.finalPrice.equals(mData.price) && mData.price != null) {
                holder.tv_price.setText("¥ " + mData.price);
                holder.tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }*/
            if (isCollage == 2) {
                holder.tv_check_group_detail.setVisibility(View.VISIBLE);
                holder.ll_send_essay.setVisibility(View.GONE);
                holder.ll_send_book.setVisibility(View.GONE);
                holder.tv_check_group_detail.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //已拼成 拼单详情
                        WXUtils.appOrderToWxApp(mContext, collageOrderId);
                    }
                });
            } else {
                holder.tv_check_group_detail.setVisibility(View.GONE);
                if (mData.exposition != null && !mData.exposition.isEmpty()) {
                    holder.ll_send_essay.setVisibility(View.VISIBLE);
//                    final StringBuffer ss = new StringBuffer();
//                    for(OrderDetailData.Exposition exp : mData.exposition){
//                        ss.append(exp.getTitleEx()).append("\n");
//                    }
//                    if (mData.normEdit != 0) {
//                        ss.append("标准答案批改 x" + mData.normEdit);
//                    }
//                    if (mData.multiEdit != 0) {
//                        if (mData.normEdit != 0) {
//                            ss.append("\n" + "套题批改 x" + mData.multiEdit);
//                        } else {
//                            ss.append("套题批改 x" + mData.multiEdit);
//                        }
//                    }
//                    if (mData.argumentEdit != 0) {
//                        if (mData.normEdit == 0 && mData.multiEdit == 0) {
//                            ss.append("文章写作批改 x" + mData.argumentEdit);
//                        } else {
//                            ss.append("\n" + "文章写作批改 x" + mData.argumentEdit);
//                        }
//                    }
                    holder.lv_send_essay.setAdapter(new CommonAdapter<Exposition>(mData.exposition, R.layout.essay_order_item_layout) {
                        @Override
                        public void convert(ViewHolder holder, Exposition item, int position) {
                            holder.setText(R.id.tv_order_content, item.getTitleEx());
                            if (!Utils.isEmptyOrNull(item.expireDate))
                                holder.setText(R.id.tv_order_expiry_date, item.expireDate);
                        }
                    });
                } else {
                    holder.ll_send_essay.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(mData.entity)) {
                    holder.ll_send_book.setVisibility(View.VISIBLE);
                    holder.tv_send_book.setText(mData.entity);
                } else {
                    holder.ll_send_book.setVisibility(View.GONE);
                }
                //显示查看协议
                if (!TextUtils.isEmpty(url)) {
                    holder.tv_check_protocols.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_check_protocols.setVisibility(View.GONE);
                }
                holder.tv_check_protocols.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetUtil.isConnected()) {
                            ToastUtils.showShort("网络未连接，请检查您的网络设置");
                            return;
                        }
                        if (url != null) {
                            BaseFrgContainerActivity.newInstance(mContext,
                                    CourseContractFragment.class.getName(),
                                    CourseContractFragment.getArgs(url));
                        }
                    }
                });
            }
        } else {
            //拼团订单
            //老师头像
            if (mData.teacherInfo != null && mData.teacherInfo.size() != 0) {
                holder.ll_teacher.removeAllViews();
                int l = mData.teacherInfo.size();

                if (l > 3) {
                    l = 3;
                }
                for (int i = 0; i < l; i++) {
                    LinearLayout ll_teacher = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_teacher2, null, false);
                    ImageView imageView = ll_teacher.findViewById(R.id.civ_teacher_photo);
                    TextView textView = ll_teacher.findViewById(R.id.tv_teacher_name);
                    // Glide.with(UniApplicationContext.getContext()).load(mResult.teacher.get(i).roundPhoto).into(imageView);

                    ImageLoad.displaynoCacheUserAvater(mContext,mData.teacherInfo.get(i).roundPhoto,imageView,R.mipmap.user_default_avater);
                    textView.setText(mData.teacherInfo.get(i).teacherName);
                    holder.ll_teacher.addView(ll_teacher);
                }
            }
            //课时
            if (mData.timeLength != null) {
                holder.tv_lesson_count.setText(mData.timeLength + "课时");
            }
            if (mData.actualPrice != null) {
                holder.tv_final_price.setText("¥ " + mData.actualPrice);
            }
         /*   if (mData.price != null && mData.actualPrice != null && !mData.actualPrice.equals(mData.price)) {
                holder.tv_price.setText("¥ " + mData.price);
                holder.tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }*/
            holder.tv_check_group_detail.setVisibility(View.GONE);
            holder.ll_send_essay.setVisibility(View.GONE);
            holder.ll_send_book.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCourseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_lesson_count)
        TextView tv_lesson_count;
        @BindView(R.id.ll_teacher)
        LinearLayout ll_teacher;
        @BindView(R.id.tv_check_protocols)
        TextView tv_check_protocols;
        @BindView(R.id.tv_check_group_detail)
        TextView tv_check_group_detail;
     /*   @BindView(R.id.tv_price)
        TextView tv_price;*/
        @BindView(R.id.tv_final_price)
        TextView tv_final_price;
        @BindView(R.id.ll_send_essay)
        LinearLayout ll_send_essay;
        @BindView(R.id.lv_send_essay)
        NoScrollListView lv_send_essay;
        @BindView(R.id.ll_send_book)
        LinearLayout ll_send_book;
        @BindView(R.id.tv_send_book)
        TextView tv_send_book;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }

    public void setData(ArrayList<OrderDetailData.Course> mOrderList, String protocolUrl, int isCollage, String collageOrderID) {
        mCourseData.clear();
        mCourseData.addAll(mOrderList);
        url = protocolUrl;
        collageOrderId = collageOrderID;
        this.isCollage = isCollage;
        notifyDataSetChanged();
    }

}
