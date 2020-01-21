package com.huatu.handheld_huatu.business.ztk_vod.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherJieshaoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht-djd on 2017/9/6.
 *
 */

public class TeacherIntroduceView  {
    private View mView;
    private Context mContext;
    private String teacherId;
    private TeacherJieshaoBean.DataBean introduction;
    private RecyclerView rv_teacherintroduce;
    public TeacherIntroduceView(Context mContext, View view, String personID, TeacherJieshaoBean.DataBean introduction) {
        this.mContext = mContext;
        this.mView = view;
        this.teacherId = personID;
        this.introduction = introduction;
        initView();
    }

    private void initView() {
        rv_teacherintroduce = (RecyclerView) mView.findViewById(R.id.rv_teacherintroduce);
        if(introduction!=null && rv_teacherintroduce!=null){
            rv_teacherintroduce.setLayoutManager(new LinearLayoutManager(mContext));
            TeacherIntrAdapter teacherIntrAdapter = new TeacherIntrAdapter(mContext);
            rv_teacherintroduce.setAdapter(teacherIntrAdapter);
        }
    }

    public class TeacherIntrAdapter extends RecyclerView.Adapter<TeacherIntrAdapter.Holder> {
        private Context mContext;
        private List<Integer> mList;

        public TeacherIntrAdapter(Context context) {
            this.mContext = context;
            mList = new ArrayList<>();
            mList.add(1);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(mContext).inflate(R.layout.vp_teacherintroduce_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            if (!TextUtils.isEmpty(introduction.SubjectName)) {
                holder.tv_teacher_kemu.setText(introduction.SubjectName);
            }
            if (!TextUtils.isEmpty(introduction.teachingstyle)) {
                holder.tv_teacher_fengge.setText(introduction.teachingstyle);
            }
            if (!TextUtils.isEmpty(introduction.Brief)) {
                holder.tv_teacher_jieshao.setText(introduction.Brief);
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_teacher_kemu)
            TextView tv_teacher_kemu;
            @BindView(R.id.tv_teacher_fengge)
            TextView tv_teacher_fengge;
            @BindView(R.id.tv_teacher_jieshao)
            TextView tv_teacher_jieshao;
            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
