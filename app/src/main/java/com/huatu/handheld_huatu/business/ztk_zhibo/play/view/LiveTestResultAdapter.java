package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baijiahulian.livecore.models.LPAnswerSheetModel;
import com.baijiahulian.livecore.models.LPAnswerSheetOptionModel;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.ui.recyclerview.SelectItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveTestResultAdapter extends RecyclerView.Adapter<LiveTestResultAdapter.TestOptionsViewHolder> {

    private Context mCtx;
    private LPAnswerSheetModel model;

    public LiveTestResultAdapter(Context ctx, LPAnswerSheetModel sheetModel) {
        mCtx = ctx;
        model = sheetModel;
    }

    @Override
    public void onBindViewHolder(@NonNull TestOptionsViewHolder holder, int position) {
        LPAnswerSheetOptionModel optionModel = model.options.get(position);
        int percent = position * 10;
        holder.mOptionName.setText(optionModel.text);
        holder.mOptionPercent.setText(percent+ "%");
        holder.mOptionName.setSelected(optionModel.isRight);
        holder.mOptionPercent.setSelected(optionModel.isRight);
        holder.mOptionProgress.setProgress(percent);
        if(optionModel.isRight)
            holder.mOptionProgress.setProgressDrawable(ContextCompat.getDrawable(mCtx, R.drawable.progress_livevideo_test_result_green));
        else
            holder.mOptionProgress.setProgressDrawable(ContextCompat.getDrawable(mCtx, R.drawable.progress_livevideo_test_result));
    }

    public void setData(LPAnswerSheetModel sheetModel){
        model = sheetModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TestOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_videotest_result, parent, false);
        return new TestOptionsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return model == null ? 0 : model.options.size();
    }


    class TestOptionsViewHolder extends RecyclerView.ViewHolder{
        TextView mOptionName;
        ProgressBar mOptionProgress;
        TextView mOptionPercent;
        public TestOptionsViewHolder(View itemView) {
            super(itemView);
            mOptionName = itemView.findViewById(R.id.option_name);
            mOptionPercent = itemView.findViewById(R.id.option_percent);
            mOptionProgress = itemView.findViewById(R.id.progress);
        }
    }
}
