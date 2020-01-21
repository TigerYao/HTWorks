package com.huatu.handheld_huatu.business.ztk_vod.utils;

import android.graphics.Color;

import com.huatu.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuFactory;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.JSONSource;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

/**
 * Created by cjx on 2018\7\26 0026.
 *  https://blog.csdn.net/benhuo931115/article/details/51056646
 * http://windrunnerlihuan.com/2016/07/02/DanmakuFlameMaster%E7%AE%80%E5%8D%95%E5%88%86%E6%9E%90/
 */


public class AcFunDanmakuParser extends BaseDanmakuParser {

    @Override
    public BaseDanmakuParser setDisplayer(IDisplayer disp) {
        int tmpWidth= disp.getWidth();

        super.setDisplayer(disp);
        mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH;
        mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT;
        return this;
    }
    protected float mDispScaleX;
    protected float mDispScaleY;
    public AcFunDanmakuParser() {

    }

    @Override
    public Danmakus parse() {
        if (mDataSource != null && mDataSource instanceof JSONSource) {
            JSONSource jsonSource = (JSONSource) mDataSource;
            return doParse(jsonSource.data());//go on
        }
        return new Danmakus();
    }
    private Danmakus doParse(JSONArray danmakuListData) {
        Danmakus danmakus = new Danmakus();
        if (danmakuListData == null || danmakuListData.length() == 0) {
            return danmakus;
        }
        for (int i = 0; i < danmakuListData.length(); i++) {
            try {
                JSONObject danmakuArray = danmakuListData.getJSONObject(i);
                if (danmakuArray != null) {
                    danmakus = _parse2(danmakuArray, danmakus,i);//解析每一条弹幕
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return danmakus;
    }


/*    弹幕json格式:
            [{"c":"19.408,16777215,1,25,196050,1364468342","m":"。。。。。。。。。"},{"c":"21.408,16777215,1,25,196050,1364468346","m":"afsdafa"},{"c":"22.408,16777215,1,25,196050,1364468346","m":"总结一下就是"}]

    表达的意思:
    {"c": "播放时间,颜色,模式,字号,uid,发送时间", "m": "弹幕内容"}
    这里介绍下c里面各自参数：
            1、播放时间单位是秒 如1.234
            2、颜色是十进制
            3、模式在BaseDanmaku里有声明，总结一下就是
              1:滚动弹幕
              4:底端弹幕
              5:顶端弹幕*/


    /**
     * {"c":"19.408,16777215,1,25,178252,1376325904","m":"金刚如来！"}
     // 0:时间(弹幕出现时间)
     // 1:颜色
     // 2:类型(1从右往左滚动弹幕|6从右至左滚动弹幕|5顶端固定弹幕|4底端固定弹幕|7高级弹幕|8脚本弹幕)
     // 3:字号
     // 4:用户id ?
     // 5:时间戳 ?
     */
    private Danmakus _parse(JSONObject jsonObject, Danmakus danmakus,int index) {
        if (danmakus == null) {
            danmakus = new Danmakus();
        }
        if (jsonObject == null || jsonObject.length() == 0) {
            return danmakus;
        }
        try {
                JSONObject obj = jsonObject;
                String c = obj.getString("c");//弹幕配置信息
                String[] values = c.split(",");
                if (values.length > 0) {
                    int type = Integer.parseInt(values[2]); // 弹幕类型
                    if (type == 7){
                        // FIXME : hard code
                        // TODO : parse advance danmaku json
                        return danmakus;
                    }
                    long time = (long) (Float.parseFloat(values[0]) * 1000); // 出现时间
                    int color = Integer.parseInt(values[1]) | 0xFF000000; // 颜色
                    float textSize = Float.parseFloat(values[3]); // 字体大小
                    //使用弹幕工厂创建一条弹幕
                    BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(type, mContext);
                    if (item != null) {
                        item.setTime(time)  ;
                        item.textSize = textSize * (mDispDensity - 0.6f);
                        item.textColor = color;
                        item.textShadowColor = color <= Color.BLACK ? Color.WHITE : Color.BLACK;
                        //弹幕文字内容，如果多行文本会拆分内容
                        DanmakuUtils.fillText(item, obj.optString("m", "...."));
                        item.index = index;
                        item.setTimer(mTimer);//将定时器设置给每一条弹幕
                        item.flags = mContext.mGlobalFlagValues;

                        Object lock = danmakus.obtainSynchronizer();
                        synchronized (lock) {
                            danmakus.addItem(item);
                        }
                     }
                }
            } catch (JSONException e) {
            } catch (NumberFormatException e) {
            }

        return danmakus;
    }

/*
    public static class Data {
        public String background;
        public String content;
        public String rateDate;
        public String userId;
        public String userName;
        public float    videoNode;


    }
*/

    private Danmakus _parse2(JSONObject jsonObject, Danmakus danmakus,int index) {
        if (danmakus == null) {
            danmakus = new Danmakus();
        }
        if (jsonObject == null || jsonObject.length() == 0) {
            return danmakus;
        }
        try {
            JSONObject obj = jsonObject;
           // String c = obj.getString("c");//弹幕配置信息
           // String[] values = c.split(",");
            //if (values.length > 0)

                /*int type = Integer.parseInt(values[2]); // 弹幕类型
                if (type == 7){
                    // FIXME : hard code
                    // TODO : parse advance danmaku json
                    return danmakus;
                }*/
                long time = (long) (StringUtils.parseFloat(obj.optString("videoNode")) * 1000); // 出现时间
                int color = obj.optInt("background",Color.WHITE);//Integer.parseInt(values[1]) | 0xFF000000; // 颜色
                float textSize =25f  ;// 字体大小
                //使用弹幕工厂创建一条弹幕
                BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(1, mContext);
                if (item != null) {
                    item.setTime(time)  ;
                    item.textSize = textSize * (mDispDensity - 0.6f);
                    item.textColor = color;
                    item.textShadowColor = color <= Color.BLACK ? Color.WHITE : Color.BLACK;
                    //弹幕文字内容，如果多行文本会拆分内容
                    DanmakuUtils.fillText(item, obj.optString("content", "...."));
                    item.index = index;
                    item.setTimer(mTimer);//将定时器设置给每一条弹幕
                    item.flags = mContext.mGlobalFlagValues;

                    Object lock = danmakus.obtainSynchronizer();
                    synchronized (lock) {
                        danmakus.addItem(item);
                    }
                }

           } catch (NumberFormatException e) {
        }

        return danmakus;
    }
}