package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.ExerciseBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljzyuhenda on 16/7/19.
 * 行测搜索适配器
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolderSimulation> {
    private List<ExerciseBean.ExerciseInfoBean> mDatas;
    private Context mContext;
    private onRecyclerViewItemClickListener onItemClickListener;
    private Map<String, String> mTypeMap;
    private String mFontPrefix = "<font color='#FF3F47'>";
    private String mFontSubFix = "</font>";
    private String mKeyWord;

    public SearchListAdapter(Context context) {
        mContext = context;

        mTypeMap = new HashMap<>();
        mTypeMap.put("99", "单选题");
        mTypeMap.put("100", "多选题");
        mTypeMap.put("101", "不定项");
        mTypeMap.put("109", "对错题");
        mTypeMap.put("105", "复合题");
        mTypeMap.put(String.valueOf(ArenaConstant.QUESTION_TYPE_SUBJECTIVE), "主观题");
    }

    @Override
    public ViewHolderSimulation onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_search, null);
        ViewHolderSimulation holderSimulation = new ViewHolderSimulation(view);

        return holderSimulation;
    }

    @Override
    public void onBindViewHolder(ViewHolderSimulation holder, int position) {
        ExerciseBean.ExerciseInfoBean exerciseInfoBean = mDatas.get(position);

        holder.itemView.setTag(R.id.tag_position_item, position);
        holder.tv_content.setTag(R.id.tag_position_item, position);
        if (!TextUtils.isEmpty(exerciseInfoBean.teachType)) {
            holder.tv_label.setText(exerciseInfoBean.teachType);
        } else {
            holder.tv_label.setText("");
        }

        if (!TextUtils.isEmpty(exerciseInfoBean.from)) {
            holder.tv_info.setVisibility(View.VISIBLE);
            holder.tv_info.setText("来源:   " + exerciseInfoBean.from);
        } else {
            holder.tv_info.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mKeyWord)) {
//            holder.tv_content.setHtmlSource(DisplayUtil.dp2px(240 - 20), addNbsp("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", exerciseInfoBean.fragment));
            Spanned charSequence;
            String content = addNbsp("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", exerciseInfoBean.fragment);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                charSequence = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
            } else {
                charSequence = Html.fromHtml(content);
            }
            holder.tv_content.setText(charSequence);
        } else {
            String content = exerciseInfoBean.fragment;
            if (content != null) {
                String prefixReplace = content.replaceAll("<b>", mFontPrefix);
                String subfixReplace = prefixReplace.replaceAll("</b>", mFontSubFix);
                if (exerciseInfoBean.teachType != null && exerciseInfoBean.teachType.length() > 3) {
//                    holder.tv_content.setHtmlSource(DisplayUtil.dp2px(240 - 20), addNbsp("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", subfixReplace));
                    Spanned charSequence;
                    String contentx = addNbsp("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", subfixReplace);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        charSequence = Html.fromHtml(contentx, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        charSequence = Html.fromHtml(contentx);
                    }
                    holder.tv_content.setText(charSequence);
                } else {
//                    holder.tv_content.setHtmlSource(DisplayUtil.dp2px(240 - 20), addNbsp("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", subfixReplace));
                    Spanned charSequence;
                    String contentx = addNbsp("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", subfixReplace);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        charSequence = Html.fromHtml(contentx, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        charSequence = Html.fromHtml(contentx);
                    }
                    holder.tv_content.setText(charSequence);
                }
            }
        }
    }

    public ExerciseBean.ExerciseInfoBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }

        return mDatas.size();
    }

    public class ViewHolderSimulation extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_label)
        TextView tv_label;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_info)
        TextView tv_info;

        ViewHolderSimulation(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tv_content.setOnClickListener(this);
            itemView.setOnClickListener(this);
//            tv_content.setCenterImg(true);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, (int) view.getTag(R.id.tag_position_item), -1);
            }
        }
    }

    public List<ExerciseBean.ExerciseInfoBean> getDataList() {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }

        return mDatas;
    }

    public void setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setKeyWords(String keyWords) {
        mKeyWord = keyWords;
    }

    private String addNbsp(String startStr, String content) {
        if (content.startsWith("<p>")) {
            return "<p>" + startStr + content.substring(3);
        }
        return startStr + content;
    }
}
