package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.helper.GlideApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by saiyuan on 2016/10/14.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    public Context mContext;
    protected List<T> mData = new ArrayList<>();

    protected int layoutId;
    public MultiItemTypeSupport<T> mMultiItemSupport;

    public CommonAdapter() {
        this.mContext = UniApplicationContext.getContext();
    }

    public CommonAdapter(List<T> data, int layoutId) {
        this.mContext = UniApplicationContext.getContext();
        if (data != null) {
            this.mData.addAll(data);
        }
        this.layoutId = layoutId;
    }

    public CommonAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        if (data != null) {
            this.mData.addAll(data);
        }
        this.layoutId = layoutId;
    }


    public CommonAdapter(Context context, List<T> data, int layoutId, MultiItemTypeSupport<T> mMultiItemSupport) {
        this(context, data, layoutId);
        this.mMultiItemSupport = mMultiItemSupport;
    }

    public CommonAdapter(List<T> data, MultiItemTypeSupport<T> mMultiItemSupport) {
        this.mContext = UniApplicationContext.getContext();
        if (data != null) {
            this.mData.addAll(data);
        }
        this.mMultiItemSupport = mMultiItemSupport;
    }

    public CommonAdapter(Context context, List<T> data, MultiItemTypeSupport<T> mMultiItemSupport) {
        this.mContext = context;
        if (data != null) {
            this.mData.addAll(data);
        }
        this.mMultiItemSupport = mMultiItemSupport;
    }

    public ViewHolder getAdapterHelper(int position, View convertView, ViewGroup parent, final int count) {
        if (mMultiItemSupport != null) {
            return get(
                    mContext,
                    convertView,
                    parent,
                    mMultiItemSupport.getLayoutId(position, mData.get(position)),
                    count,position);
        } else {
            return get(mContext, convertView, parent, layoutId, count, position);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData == null || mData.isEmpty() ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = getAdapterHelper(position, convertView, parent, getCount());
        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }


    static ViewHolder get(Context context, View convertView,
                          ViewGroup parent, int layoutId, int count, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, count, position);
        }

        // Retrieve the existing helper and update its position
        ViewHolder helper = (ViewHolder) convertView.getTag();

        if (helper.layoutId != layoutId) {
            return new ViewHolder(context, parent, layoutId, count, position);
        }

        helper.position = position;
        return helper;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemSupport != null) {
            return mMultiItemSupport.getViewTypeCount();
        }
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemSupport != null)
            return position >= mData.size() ? 0 : mMultiItemSupport
                    .getItemViewType(position, mData.get(position));

        return position >= mData.size() ? 0 : 1;

    }

    /**
     * 更新数据
     *
     * @param items
     */
    public void setDataAndNotify(List<T> items) {
        if (items == null) {
            return;
        }
        if (mData != null) {
            mData.clear();
        } else {
            mData = new ArrayList<T>();
        }
        mData.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新数据
     *
     * @param items
     */
    public void removeDataAndAdd(List<T> items, List<T> newItems) {
        if (items == null) {
            return;
        }
        if (mData == null) {
            return;
        }
        if (mData.containsAll(items)) {
            mData.removeAll(items);
            mData.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param items
     */
    public void addData(List<T> items) {
        if (items == null) {
            return;
        }
        if (mData == null) {
            mData = new ArrayList<T>();
        }
        mData.addAll(items);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData == null ? null : Collections.unmodifiableList(mData);
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public abstract void convert(ViewHolder holder, final T item, final int position);

    public static class ViewHolder {
        private SparseArray<View> views;
        private View convertView;
        public int layoutId;
        public int count;
        public int position;
        public Context context;
        public <T extends View> T getView(int viewId) {
            View view = views.get(viewId);
            if (view == null) {
                view = convertView.findViewById(viewId);
                views.put(viewId, view);
            }
            return (T) view;
        }

        private ViewHolder(Context context, ViewGroup parent, int layoutId, int count, int position) {
            this.context=context;
            this.views = new SparseArray<View>();
            convertView = LayoutInflater.from(context).inflate(layoutId, null);
            this.layoutId = layoutId;
            convertView.setTag(this);
            this.count = count;
            this.position = position;
        }

        public View getConvertView() {
            return convertView;
        }

        public ViewHolder getPaint(int viewId, int flags) {
            TextView view = getView(viewId);
            view.getPaint().setFlags(flags);
            return this;
        }
        public ViewHolder setText(int viewId, String text) {
            TextView view = getView(viewId);
            view.setText(text);
            return this;
        }

        public ViewHolder setText(int viewId, SpannableStringBuilder text) {
            TextView view = getView(viewId);
            view.setText(text);
            return this;
        }
        public String getText(int viewId) {
            EditText view = getView(viewId);
            return  view.getText().toString();

        }
        public ViewHolder addEditChangeListener(int viewId,  TextWatcher textWatcher) {
            EditText view = getView(viewId);
            if(view != null) {
                view.addTextChangedListener(textWatcher);
            }
            return this;
        }

        public ViewHolder setEditText(int viewId, String text) {
            EditText view = getView(viewId);
            view.setText(text);
            return this;
        }
        public ViewHolder setEnable(int viewId, boolean isEnable) {
            ImageView view = getView(viewId);
            view.setEnabled(isEnable);
            return this;
        }

        public ViewHolder setSsbText(int viewId, SpannableStringBuilder text) {
            TextView view = getView(viewId);
            view.setText(text, TextView.BufferType.SPANNABLE);
            view.setMovementMethod(LinkMovementMethod.getInstance());
            return this;
        }

        public ViewHolder setCheckBoxText(int viewId, String text){
            CheckBox view = getView(viewId);
            view.setText(text);
            return this;
        }

        public ViewHolder setColorText(int viewId, int color, String text){
            TextView view = getView(viewId);
            view.setText(text);
            view.setTextColor(ContextCompat.getColor(context,color));
            return this;
        }

        public ViewHolder setTextFaceType(int viewId,String type){
            TextView view = getView(viewId);
            Typeface TextType = Typeface.createFromAsset(context.getAssets(), type);
            view.setTypeface(TextType);
            return this;
        }
        public ViewHolder setBoldFaceType(int viewId,int type){
            TextView view = getView(viewId);
            view.setTypeface(Typeface.defaultFromStyle(type));
            return this;
        }

        public ViewHolder setTextFromHtml(int viewId, String text) {
            TextView view = getView(viewId);
            view.setText(Html.fromHtml(text));
            return this;
        }

        public ViewHolder setTextColor(int viewId, int color) {
            TextView view = getView(viewId);
            view.setTextColor(color);
            return this;
        }

        public ViewHolder setTextColorRes(int viewId, int color) {
            TextView view = getView(viewId);
            view.setTextColor(ContextCompat.getColor(context,color));
            return this;
        }
        public ViewHolder setEditTextColorRes(int viewId, int color) {
            EditText view = getView(viewId);
            view.setTextColor(ContextCompat.getColor(context,color));
            return this;
        }

        public ViewHolder setCheckBoxTextColor(int viewId, int color) {
            CheckBox view = getView(viewId);
            view.setTextColor(color);
            return this;
        }

        public ViewHolder setViewVisibility(int viewId, int visibility) {
            View view = getView(viewId);
            view.setVisibility(visibility);
            return this;
        }

        public ViewHolder setSeekBarProgress(int viewId, int progress) {
            SeekBar view = getView(viewId);
            view.setProgress(progress);
            return this;
        }

        public ViewHolder setViewBackgroundColor(int viewId, int res) {
            View view = getView(viewId);
            view.setBackgroundColor(ContextCompat.getColor(context,res));
            return this;
        }

        public ViewHolder setViewBackgroundRes(int viewId, int res) {
            View view = getView(viewId);
            view.setBackgroundResource(res);
            return this;
        }

        public ViewHolder setViewImageResource(int viewId, int res){
            ImageView view = getView(viewId);
            view.setImageResource(res);
            return this;
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId
         * @param drawableId
         * @return
         */
        public ViewHolder setImageResource(int viewId, int drawableId) {
            ImageView view = getView(viewId);
            view.setImageResource(drawableId);
            return this;
        }

        public ViewHolder setViewOnClickListener(int viewId, View.OnClickListener l) {
            View view = getView(viewId);
            if(view != null) {
                view.setOnClickListener(l);
            }
            return this;
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId
         * @param bm     drawableId
         * @return
         */
        public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bm);
            return this;
        }
        public ViewHolder setImage(int viewId, String imageUrl) {
            ImageView view = getView(viewId);
            if(view!=null){
                GlideApp.with(view.getContext()).load(imageUrl).into(view);
            }

            return this;
        }
        public ViewHolder addItemView(int viewId, View child) {
            LinearLayout view = getView(viewId);
            view.addView(child);
            return this;
        }
        public ViewHolder addItemView(int viewId, ViewGroup child) {
            LinearLayout view = getView(viewId);
            view.addView(child);
            return this;
        }

        public ViewHolder removeAllViews(int viewId) {
            LinearLayout view = getView(viewId);
            view.removeAllViews();
            return this;
        }

        public ViewHolder setAutoDivider(int viewId, int position) {
            return setAutoDivider(viewId, position, 0);
        }

        public ViewHolder setAutoDivider(int viewId, int position, int topMargin) {
            View view = getView(viewId);
            if (view != null) {
                if (topMargin != 0) {
                    if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        params.topMargin = topMargin;
                        view.setLayoutParams(params);
                    }
                }
                view.setVisibility(View.VISIBLE);
                if (position + 1 == count) {
                    view.setBackgroundResource(R.color.transparent);

                } else {
                    view.setBackgroundResource(R.color.divider);
                }
            }
            return this;
        }

        //最后一行需要divider
        public ViewHolder setAutoDividerInSelect(int viewId, int position, int topMargin) {
            View view = getView(viewId);
            if (view != null) {
                if (topMargin != 0) {
                    if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        params.topMargin = topMargin;
                        view.setLayoutParams(params);
                    }
                }
                view.setVisibility(View.VISIBLE);
                if (position + 1 == count) {
                    view.setBackgroundResource(R.color.divider);
                } else {
                    view.setBackgroundResource(R.color.divider);
                }
            }
            return this;
        }

    }
}

