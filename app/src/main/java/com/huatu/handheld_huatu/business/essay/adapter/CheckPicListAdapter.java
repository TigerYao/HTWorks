package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.helper.GlideOptions;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionInfoBean;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.List;

import static com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Administrator on 2019\6\24 0024.
 */


public class CheckPicListAdapter extends SimpleBaseRecyclerAdapter<SingleQuestionInfoBean.UserMetaInfo> {
    private  final GlideOptions mImageOptions =
            new GlideOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);
    private int mSelected=0;
    private int mWidthPic=0;
    private int mHeightPic=0;

    public int getSelectIndex(){
        return mSelected;
    }

    public void setSelectIndex(int selectIndex){
        mSelected=selectIndex;

        /*android:layout_width="46dp"
        android:layout_height="80dp"*/
       // this.notifyDataSetChanged();
    }

    public CheckPicListAdapter(Context context) {
        super(context);
        mWidthPic= DensityUtils.dp2px(context,46);
        mHeightPic= DensityUtils.dp2px(context,80);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads){
        //super.onBindViewHolder(holder,position,payloads);
        if(ArrayUtils.isEmpty(payloads))
            onBindViewHolder(holder,position);
        else {
            ArticleViewHolder viewHolder=(ArticleViewHolder)holder;
            viewHolder.mRootView.setSelected(position==mSelected);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.essay_checkpic_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ArticleViewHolder viewHolder=(ArticleViewHolder)holder;
        // ImageLoad.displaynoCacheImage(mContext,0,getItem(position),viewHolder.mImageView);
        Glide.with(mContext)
                .load(getItem(position).finalUrl).apply(mImageOptions.override(mWidthPic,mHeightPic))
               // .apply(mUploadlocalOption.override(size[0], size[1]))
              //  .thumbnail(.2f)
                .transition(withCrossFade())
                .into(viewHolder.mImageView);

        viewHolder.mRootView.setSelected(position==mSelected);

    }

    private  class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ImageView    mImageView;
        private View  mRootView;


        private ArticleViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.piclayout);
            mRootView=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(null!=onRecyclerViewItemClickListener)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                    // Toast.makeText(v.getContext(),"fadsfad"+getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }



}
