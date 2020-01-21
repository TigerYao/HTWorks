package com.huatu.handheld_huatu.business.ztk_vod;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_vod.utils.AcFunDanmakuParser;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.DensityUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.JSONSource;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by cjx on 2018\7\26 0026.
 *
 *    //https://blog.csdn.net/luawen/article/details/52846626?locationNum=3&fps=1
 */

public class DanmakuPresenter {


/*


    mDanmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);

        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 10)
            .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.0f)
                .setCacheStuffer(mBackgroundCacheStuffer, null)
    // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair).setDanmakuMargin(40);*/

    public DanmakuPresenter(FrameLayout viewContainer){
        this.initView(viewContainer);
    }

    private void initView(FrameLayout danmuContainer){
        mDanmakuView=new DanmakuView(danmuContainer.getContext());
        if (danmuContainer != null){
            danmuContainer.addView(mDanmakuView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        mDanmakuView.enableDanmakuDrawingCache(true);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mShowDanmaku = true;
                mDanmakuView.start();
                //generateSomeDanmaku();*/
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {
               // LogUtils.e("updateTimer",timer.currMillisecond+"_"+timer.lastInterval());

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        mDanmakuContext = DanmakuContext.create();

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 6); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext.setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setMaximumLines(maxLinesPair)
                      .preventOverlapping(overlappingEnablePair).setDanmakuMargin(10);
       // mDanmakuView.prepare(createParser(" [{\"c\":\"19.408,16777215,1,25,196050,1364468342\",\"m\":\"。。。。。。。。。\"},{\"c\":\"21.408,16777215,1,25,196050,1364468346\",\"m\":\"afsdafa\"},{\"c\":\"22.408,16777215,1,25,196050,1364468347\",\"m\":\"总结一下就是\"}]"), mDanmakuContext);
    }


    DanmakuView mDanmakuView;
    DanmakuContext mDanmakuContext;
    private boolean mShowDanmaku=false;


    private void addDanmaku(String content, boolean withBorder){
       addDanmaku(  content,   withBorder,Color.WHITE);
    }
    public void addDanmaku(String content, boolean withBorder,int textColor) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = DensityUtils.sp2px(UniApplicationContext.getContext(),20);

        danmaku.textColor = textColor;
        danmaku.textShadowColor = textColor <= Color.BLACK ? Color.WHITE : Color.BLACK;
        danmaku.setTime(mDanmakuView.getCurrentTime()+1000);
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        mDanmakuView.addDanmaku(danmaku);
    }

   /* private void testGenerateSomeDanmaku() {
        new Thread(new Runnable() {

            int i=0;
            @Override
            public void run() {
                while(mShowDanmaku) {
                    int time = new Random().nextInt(300);
                    String content =mDanmakuView.getCurrentTime()+ "__" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        }).start();
    }*/

    // 加载json串,就是把从服务器上获取的json加进去
    private BaseDanmakuParser createParser(String jsonArr) {
        if(TextUtils.isEmpty(jsonArr)) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        BaseDanmakuParser parser =new AcFunDanmakuParser();//加载的是json
        try{
            JSONSource jsonSource =new JSONSource(jsonArr);
            IDataSource<?> dataSource = jsonSource;
            parser.load(dataSource);

        }catch(JSONException e) {
            e.printStackTrace();
        }
        return parser;
    }

    public void startDanmaku(String jsonArr) {

       // 这个是对弹幕的初始化，并且启动，具体实现，看弹幕里的Handler
        BaseDanmakuParser mParser = createParser(jsonArr);//解析 json,或者是xml
        mDanmakuView.release();// 先释放之前的资源，和还原时间
        mDanmakuView.prepare(mParser, mDanmakuContext);
    }

    public void start(long ms,boolean isSeek){
        if(mDanmakuView!=null){
            if(!isSeek){
                if(ms >0) {
                    mDanmakuView.start(ms);
                }else{
                    mDanmakuView.start();
                }
            }else
                mDanmakuView.seekTo(ms);
            //testGenerateSomeDanmaku();
        }

    }

   public void resume(){
       if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
           mDanmakuView.resume();
       }
   }

   public void pause(){
       if (mDanmakuView != null && mDanmakuView.isPrepared()) {
           mDanmakuView.pause();
       }
   }

    public void destory(){
        mShowDanmaku = false;
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public void isShow(boolean isShow){
        if (mDanmakuView != null) {
            if(isShow)
                mDanmakuView.show();
            else
                mDanmakuView.hide();

        }
    }
}
