package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.checkfragment.CheckOrderFragment;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodBean;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;

public class PersonGoodsAdapter extends RecyclerView.Adapter<PersonGoodsAdapter.ViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CheckGoodBean> datas;
    private CheckOrderFragment fragment;

    public PersonGoodsAdapter(Context context, ArrayList<CheckGoodBean> datas, CheckOrderFragment fragment) {
        this.context = context;
        if (this.datas!=null){
            this.datas.clear();
        }
        this.datas = datas;
        this.fragment = fragment;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.check_order_item_flayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CheckGoodBean item = datas.get(position);

        if (!TextUtils.isEmpty(item.name)){
            holder.title.setText(item.name);
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldD29));
        }else {
            holder.title.setText("");
        }

        holder.price.setText("¥" + getYuan(item.activityPrice));
        holder.reduce.setImageResource(R.mipmap.person_check_order_reduce_black);
        holder.add.setImageResource(R.mipmap.person_check_order_add);

        if (item.expireFlag==0){
            //无限期
            holder.tv_validity.setText("无限期");
        }else {
            //有限期
            if (item.expireDate>0){
                holder.tv_validity.setText("有效期"+ item.expireDate+"天");
            }else {
                holder.tv_validity.setText("");
            }
        }
        holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldD29));

        if (item.price != item.activityPrice && item.price != 0) {
            holder.originalPrice.setText("¥" + getYuan(item.price));
            holder.originalPrice.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
            holder.originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.originalPrice.setText("");
        }

        if (item.userSetCount > 0) {
            holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_selected);
            holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_count_bg));
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
        } else {
            holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_unselected);
            holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_zero_bg));
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldD29));

        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = holder.orderCount.getText().toString();
                int i = 0;
                if (!TextUtils.isEmpty(text)) {
                    i = Integer.parseInt(text);
                }
                i++;
                if (i <=999) {
                    item.userSetCount = i;
                    item.selectedUserSetCount = i;
                    holder.orderCount.setText(item.userSetCount + "");
                    holder.orderCount.setSelection((item.userSetCount + "").length());
                    if (item.userSetCount > 0) {
                        item.isSelected = true;
                        holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_count_bg));
                        holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
                        holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_selected);
                        holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
                    } else {
                        item.isSelected = false;
                        holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_zero_bg));
                        holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldD29));
                        holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_unselected);
                        holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldD29));

                    }
                    if (item.userSetCount==999){
                        holder.add.setImageResource(R.mipmap.person_check_order_add_black);
                    }
                } else {
                    ToastUtils.showEssayToast("最多只能购买999次哦");
//                    holder.add.setClickable(false);
                    holder.add.setImageResource(R.mipmap.person_check_order_add_black);
                }
            }
        });

        holder.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = holder.orderCount.getText().toString();
                int i = 0;
                if (!TextUtils.isEmpty(text)) {
                    i = Integer.parseInt(text);
                }
                i--;
                if (i >=0) {
                    item.userSetCount = i;
                    item.selectedUserSetCount = i;
                    holder.orderCount.setText(item.userSetCount + "");
                    holder.orderCount.setSelection((item.userSetCount + "").length());
                    if (item.userSetCount == 0) {
                        item.isSelected = false;
                        holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_zero_bg));
                        holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldD29));
                        holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_unselected);
                        holder.reduce.setImageResource(R.mipmap.person_check_order_reduce_black);
                        holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldD29));
                    } else {
                        item.isSelected = true;
                        holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_count_bg));
                        holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
                        holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_selected);
                        holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldA96));

                    }
                } else {
                    ToastUtils.showEssayToast("受不了了，本宝贝不能再减少了");
//                    holder.reduce.setClickable(false);
                    holder.reduce.setImageResource(R.mipmap.person_check_order_reduce_black);
                }
            }
        });

        holder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isSelected) {
                    item.isSelected = true;
                    item.selectedUserSetCount = item.userSetCount;
                    holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_selected);
                } else {
                    item.isSelected = false;
                    item.selectedUserSetCount = 0;
                    holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_unselected);
                }
                fragment.refreshAllPriceCount();
            }
        });

        holder.orderCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                holder.orderCount.requestFocus();
                String str = s.toString();
                if (str.startsWith("0") && str.length() >= 2) {
                    while (str.startsWith("0") && str.length() >= 2) {
                        str = str.substring(1, str.length());
                    }
                    holder.orderCount.setText(str);
                    holder.orderCount.setSelection(str.length());
                    return;
                }
                if (str.length() >= 4){
                    str = str.substring(0, str.length()-1);
                    holder.orderCount.setText(str);
                    holder.orderCount.setSelection(str.length());
                    ToastUtils.showEssayToast("最多只能购买999次哦");
                }
                int i = 0;
                if (!TextUtils.isEmpty(str)) {
                    i = Integer.parseInt(str);
                }
                if (i > 0 && i <= 999) {
                    item.isSelected = true;
                    holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_count_bg));
                    holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
                    holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldA96));
                    holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_selected);
                    item.userSetCount = i;
                    item.selectedUserSetCount = i;
                } else {
                    item.isSelected = false;
                    holder.orderCount.setBackground(ContextCompat.getDrawable(context,R.drawable.person_check_order_zero_bg));
                    holder.tv_validity.setTextColor(ContextCompat.getColor(context,R.color.goldD29));
                    holder.title.setTextColor(ContextCompat.getColor(context,R.color.goldD29));
                    holder.ivSelect.setImageResource(R.mipmap.essay_person_goods_unselected);
                    item.userSetCount = 0;
                    item.selectedUserSetCount = 0;
                }
                if (i==999){
                    holder.add.setImageResource(R.mipmap.person_check_order_add_black);
                }else {
                    holder.add.setImageResource(R.mipmap.person_check_order_add);

                }

                if (i!=0) {
                    holder.reduce.setImageResource(R.mipmap.person_check_order_reduce);
                }else {
                    holder.reduce.setImageResource(R.mipmap.person_check_order_reduce_black);
                }
                holder.add.setClickable(true);
                holder.reduce.setClickable(true);

                fragment.refreshAllPriceCount();
            }
        });
        holder.orderCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.orderCount.setHint("");
                }else {
                    holder.orderCount.setHint("0");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivSelect;
        TextView title;
        TextView price;
        TextView originalPrice;
        ImageView reduce;
        EditText orderCount;
        ImageView add;
        TextView tv_validity;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_select);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            originalPrice = (TextView) itemView.findViewById(R.id.original_price);
            reduce = (ImageView) itemView.findViewById(R.id.reduce);
            orderCount = (EditText) itemView.findViewById(R.id.order_count);
            add = (ImageView) itemView.findViewById(R.id.add);
            tv_validity =itemView.findViewById(R.id.tv_validity);

        }
    }

    private String getYuan(int price) {
        int y = price / 100;
        int f = price % 100;
        if (f < 10) {
            return y + ".0" + f;
        } else {
            return y + "." + f;
        }
    }
}
