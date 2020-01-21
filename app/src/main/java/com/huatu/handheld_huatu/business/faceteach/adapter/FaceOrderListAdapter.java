package com.huatu.handheld_huatu.business.faceteach.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.faceteach.bean.FaceOrderBean;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.FaceAreaBean;
import com.huatu.handheld_huatu.ui.CustomShapeDrawable;

import java.util.List;

/**
 * 适配器
 *
 */

public class FaceOrderListAdapter extends SimpleBaseRecyclerAdapter<FaceOrderBean> {

    ShapeDrawable mPayShapeBg,mProtocolShapeBg,mLookShapeBg;

    /*可以签订协议需满足： 1、购课时有协议；   2、订单状态已支付*/
    //订单状态代码 0-未支付，1-已支付，4-已完成
    private ShapeDrawable getShapeDrawable(int status,int isSignedProtocol,int hasProtocol){

        if(status==0){
            if(null==mPayShapeBg){
                mPayShapeBg=CustomShapeDrawable.buildRoundBackground(16,0xFFFF6D73);
            }
            return mPayShapeBg;
        }
        else if(status==1){
            if(null==mLookShapeBg){
                mLookShapeBg=CustomShapeDrawable.buildV2(16,Color.WHITE,0xFF5D9AFF,0.3f);
            }
            return mLookShapeBg;
        }else if(status==2){
            if(null==mProtocolShapeBg){
                mProtocolShapeBg=CustomShapeDrawable.buildRoundBackground(16,0xFF5D9AFF);
            }
            return mProtocolShapeBg;

        }
        return null;
    }


    public FaceOrderListAdapter(Context context, List<FaceOrderBean> listbeans) {
        super(context,listbeans);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         return  new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.face_order_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            ArticleViewHolder h = (ArticleViewHolder) holder;
            FaceOrderBean item = getItem(position);
            h.mTextTitle.setText(item.title);
           h.mPayMoneyTxt.setText("￥"+item.priceCount);
            h.mPayActionTxt.setVisibility(View.VISIBLE);
            h.mOrderDesTxt.setText("订单号："+item.oid);
            // 订单状态代码   0-未支付，1-已支付，2-全部  ,4-已完成

           h.mPayStatusTxt.setText(item.state_txt);
            if(item.state==0)  {
                h.mPayStatusTxt.setText("待付款");
                h.mPayActionTxt.setText("我要付款");

                h.mPayActionTxt.setTextColor(Color.WHITE);
                h.mPayActionTxt.setBackground(getShapeDrawable(0,0,0));
            }
            else if(item.state==1||item.state==4){
                h.mPayStatusTxt.setText("已支付");
                if(item.hasElectronicProtocol==1){
                    h.mPayActionTxt.setTextColor(item.IsElectronicProtocol==1 ?0xFF5D9AFF :Color.WHITE);
                    h.mPayActionTxt.setText(item.IsElectronicProtocol==1? "查看协议":"签订协议");

                    h.mPayActionTxt.setBackground(getShapeDrawable(item.IsElectronicProtocol==1?1:2,0,0));
                }else {
                    h.mPayActionTxt.setVisibility(View.GONE);
                }
            }else {
                h.mPayActionTxt.setVisibility(View.GONE);
             }
    }



    private  class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextTitle ,mPayActionTxt,mPayMoneyTxt;
        private TextView mOrderDesTxt ,mPayStatusTxt;

        private TextView mCallServerTxt;

        private ArticleViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mPayActionTxt= (TextView) itemView.findViewById(R.id.pay_action_txt);
            mPayMoneyTxt= (TextView) itemView.findViewById(R.id.pay_money_txt);
            mOrderDesTxt= (TextView) itemView.findViewById(R.id.order_code_title);
            mPayStatusTxt=(TextView)itemView.findViewById(R.id.pay_result_info_tv);
            mCallServerTxt=(TextView)itemView.findViewById(R.id.tv_call_server);

            mPayActionTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(null!=onRecyclerViewItemClickListener){
                         onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_LIKE);
                    }

                    // Toast.makeText(v.getContext(),"fadsfad"+getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
            mCallServerTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(null!=onRecyclerViewItemClickListener){
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_MORE);
                    }

                    // Toast.makeText(v.getContext(),"fadsfad"+getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
