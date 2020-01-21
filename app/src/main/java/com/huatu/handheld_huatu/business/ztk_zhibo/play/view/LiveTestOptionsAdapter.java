package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baijiahulian.livecore.models.LPAnswerSheetModel;
import com.baijiahulian.livecore.models.LPAnswerSheetOptionModel;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.ui.recyclerview.SelectItemAdapter;

public class LiveTestOptionsAdapter extends SelectItemAdapter<LiveTestOptionsAdapter.TestOptionsViewHolder> {

    private Context mCtx;
    private LPAnswerSheetModel model;

    public LiveTestOptionsAdapter(Context ctx, LPAnswerSheetModel sheetModel) {
        mCtx = ctx;
        model = sheetModel;
    }

    @Override
    public void whenBindViewHolder(TestOptionsViewHolder holder, int position, boolean isSelected) {
        holder.mOptionButton.setBackgroundResource(getSelectMode() == SelectMode.MULTI_SELECT ? R.drawable.bg_test_muti_opt_selector : R.drawable.bg_test_option_selector);
        LPAnswerSheetOptionModel optionModel = model.options.get(position);
        holder.mOptionButton.setText(optionModel.text);
        holder.mOptionButton.setChecked(isSelected);
    }

    public void setData(LPAnswerSheetModel sheetModel) {
        model = sheetModel;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TestOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_option_layout, parent, false);
        return new TestOptionsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return model == null ? 0 : model.options.size();
    }

    class TestOptionsViewHolder extends RecyclerView.ViewHolder {
        RadioButton mOptionButton;

        public TestOptionsViewHolder(View itemView) {
            super(itemView);
            mOptionButton = (RadioButton) itemView;
        }
    }
}
