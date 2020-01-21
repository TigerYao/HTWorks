package com.huatu.handheld_huatu.business.arena.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.mvpmodel.previewpaper.PreviewPaperBean;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TeacherPreviewPaperActivity extends BaseActivity {

    @BindView(R.id.rl_view)
    RecyclerView rlView;

    private ArrayList<PreviewPaperBean> previewPaperBeans;          // 白名单老师课件的试卷

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_teacher_preview_paper;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        previewPaperBeans = originIntent.getParcelableArrayListExtra("previewPaperBeans");
        if (previewPaperBeans == null || previewPaperBeans.size() <= 0) {
            finish();
        }
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onLoadData() {
        PaperAdapter adapter = new PaperAdapter();
        rlView.setLayoutManager(new LinearLayoutManager(this));
        rlView.setAdapter(adapter);
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperHolder> {

        @NonNull
        @Override
        public PaperHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View rootView = LayoutInflater.from(TeacherPreviewPaperActivity.this).inflate(R.layout.preview_paper_item, viewGroup, false);
            return new PaperHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull PaperHolder holder, int i) {
            holder.tvTitle.setText(previewPaperBeans.get(i).name);
        }

        @Override
        public int getItemCount() {
            return previewPaperBeans != null ? previewPaperBeans.size() : 0;
        }

        class PaperHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;

            PaperHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        PreviewPaperBean previewPaperBean = previewPaperBeans.get(getAdapterPosition());
                        if (previewPaperBean != null) {
                            List<Integer> questions = previewPaperBean.questions;

                            if (questions != null && questions.size() > 0) {
                                String ids = getExerciseIds(questions);
                                ArenaDataCache.getInstance().clearCacheErrorData();
                                ArenaDataCache.getInstance().setErrorIdsAry(questions);
                                ArenaDataCache.getInstance().setErrorIdsStr(ids);
                                Bundle bundle = new Bundle();
                                bundle.putString("exerciseIdList", ids);
                                ArenaExamActivityNew.show(TeacherPreviewPaperActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_PREVIEW, bundle);
                            }
                        }
                    }
                });
            }
        }
    }

    private String getExerciseIds(List<Integer> resutls) {
        String ids = "";
        for (Integer id : resutls) {
            ids += id + ",";
        }
        if (!TextUtils.isEmpty(ids)) {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (previewPaperBeans != null && previewPaperBeans.size() > 0) {
            outState.putParcelableArrayList("previewPaperBeans", previewPaperBeans);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        previewPaperBeans = savedInstanceState.getParcelableArrayList("previewPaperBeans");
    }
}
