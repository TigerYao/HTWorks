package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


/*import com.gensee.chat.gif.SpanResource;*/
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.LiveChatExpressBean;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GridViewAvatarAdapter extends BaseAdapter{
    public final static Map<String, List<LiveChatExpressBean>> expressMap = new HashMap<>();
	private SelectAvatarInterface selectAvatarInterface;
    private int playerType;
    private final List<LiveChatExpressBean> chatExpressList = new ArrayList();

	public GridViewAvatarAdapter(SelectAvatarInterface selectAvatarInterface) {
       /* Map<String, Drawable> browMap = SpanResource.getBrowMap(UniApplicationContext.getContext());
        Object[] resIds = browMap.keySet().toArray();
        List<LiveChatExpressBean> expressList = new ArrayList<>();
        if(resIds != null) {
            for (int i = 0; i < resIds.length; i++) {
                LiveChatExpressBean bean = new LiveChatExpressBean();
                bean.playerType = 0;
                bean.key = (String)resIds[i];
                bean.imgDrawable = browMap.get(resIds[i]);
                expressList.add(bean);
            }
        }
        expressMap.put(String.valueOf(0), expressList);*/
		this.selectAvatarInterface = selectAvatarInterface;
	}

    public void setPlayerType(int type) {
	    if(playerType==type) return;
        playerType = type;
        chatExpressList.clear();
        List<LiveChatExpressBean> tmpList = expressMap.get(String.valueOf(type));
        if(!Method.isListEmpty(tmpList)) {
            chatExpressList.addAll(tmpList);
        }
        notifyDataSetChanged();
    }

    public void onExpressUpdate(String playerTypeKey, List<LiveChatExpressBean> list) {
        expressMap.remove(playerTypeKey);
        expressMap.put(playerTypeKey, list);
    }

	@Override
	public int getCount() {

	    if(playerType== PlayerTypeEnum.BaiJia.getValue()) return chatExpressList.size();
	    return chatExpressList.size() ;
	}

	@Override
	public Object getItem(int position) {
		return chatExpressList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewHolder viewHolder = null;
		if (null == convertView) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(
					R.layout.single_expression_layout, null);
			viewHolder = new GridViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (GridViewHolder) convertView.getTag();
		}
        final LiveChatExpressBean bean = chatExpressList.get(position);
        if(playerType == 0) {
            //viewHolder.ivAvatar.setBackgroundDrawable(bean.imgDrawable);
            viewHolder.ivAvatar.setImageDrawable(bean.imgDrawable);
        } else if(playerType == 1) {
        /*    GlideApp.with(UniApplicationContext.getContext())
                    .load(bean.imgUrl)
                    .placeholder(R.drawable.live_video_expression)
                    .error(R.drawable.live_video_expression)
                    .into(viewHolder.ivAvatar);
*/
            ImageLoad.displayEmojiCacheImage(viewHolder.ivAvatar.getContext(),R.drawable.live_video_expression,bean.imgUrl,viewHolder.ivAvatar);


      //  Picasso.with(convertView.getContext()).load(bean.imgUrl).placeholder(R.drawable.live_video_expression).into(viewHolder.ivAvatar);
        }
        viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != selectAvatarInterface) {
                    selectAvatarInterface.selectAvatar(bean);
                }
            }
        });
		return convertView;
	}

	private class GridViewHolder {
		private ImageView ivAvatar;
		public GridViewHolder(View view) {
			ivAvatar = (ImageView) view.findViewById(R.id.image);
		}
	}

	public interface SelectAvatarInterface {
		void selectAvatar(LiveChatExpressBean bean);
	}

}