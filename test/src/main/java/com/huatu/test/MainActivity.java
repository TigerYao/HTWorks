package com.huatu.test;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DataSource;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.engine.GlideException;
import com.baijiayun.glide.load.resource.bitmap.DownsampleStrategy;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.RequestListener;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.ImageViewTarget;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.target.Target;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.music.player.BgPlayContract;
import com.huatu.music.player.BgPlayPresenter;
import com.huatu.test.bean.CheckDetailBean;
import com.huatu.test.bean.Line;
import com.huatu.test.bean.ScoreListEntity;
import com.huatu.test.bean.Tag;
import com.huatu.test.bean.TagBean;
import com.huatu.test.custom.DensityUtils;
import com.huatu.test.custom.EssayExerciseTextView;
import com.huatu.test.helper.LoginTrace;
import com.huatu.test.utils.DateUtils;
import com.huatu.test.utils.DialogUtils;
import com.huatu.test.utils.LimitScaleTransformation;
import com.huatu.test.utils.ResourceUtils;
import com.huatu.music.IMusicService;
import com.huatu.music.bean.Music;
import com.huatu.music.player.PlayManager;
import com.huatu.music.utils.LogUtil;
import com.huatu.test.utils.ToastUtils;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Action1;

import static com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.huatu.music.player.PlayManager.mService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private PlayManager.ServiceToken mToken;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mToken != null) {
            PlayManager.unbindFromService(mToken);
            mToken = null;
        }
     }
    protected void listener() {
    }

    //Music(type=baidu, id=54, mid=7356091, title=认真的雪, artist=薛之谦, album=薛之谦, artistId=2517, albumId=7327990, trackNumber=0, duration=0, isLove=false,
    // isOnline=true, uri=http://audio01.dmhmusic.com/71_53_T10047981991_128_4_1_0_sdk-cpm/0207/M00/7B/93/ChR461z87ZCAcB1eAD_XaNRR9N4986.mp3?xcode=e39838a8d114bbea4f4df19d92731d19f5f4796,
    // lyric=http://qukufile2.qianqian.com/data2/lrc/4f73f5fe79cf9dbce6b5d78d3bf038a0/660150847/660150847.lrc,
    // coverUri=http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_150,h_150,
    // coverBig=http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_450,h_450,
    // coverSmall=http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_90,h_90,
    // fileName=null, fileSize=4183912, year=null, date=0, isCp=false, isDl=true, collectId=null, quality=128000,qualityList=false false false)


    protected  void initData(){
         List<Music> tmplist=new ArrayList<>();
         //"videoId":"16091936","token":"w_ZgK9dOGftMyA0LsBVeTLKXgwsBKRfiE0xsO4HlQ9acpc4W_VwHrDTSEzZrILF4"
         for(int i=0;i<3;i++){
             Music tmpDto=new Music();
             tmpDto.type="baidu";
             //tmpDto.id=54;
             tmpDto.mid="7356091";
             tmpDto.title="认真的雪"+i;
             tmpDto.artist="薛之谦"+i;
             tmpDto.album="薛之谦"+i;
             tmpDto.artistId="2517";
             tmpDto.albumId="7327990";
             tmpDto.trackNumber=0;
             tmpDto.duration=0;
             tmpDto.isLove=false;
             tmpDto.isOnline=true;
             tmpDto.uri="http://audio01.dmhmusic.com/71_53_T10041185674_128_4_1_0_sdk-cpm/0207/M00/30/FF/ChR47FplLZqAeQhQAEiOsPdPX2o299.mp3?xcode=cbcaf2a702292f204f4ec5118e34caba31dc5aa";

             String url="http://dalbj-video.baijiayun.com/3067abc4d892f224f6abc467b6b10647/5d5d2c7f/00-x-upload/video/24821080_b3c45da60b5406fa18ea327dc53ed734_2BfPWMY5_mp4/24821080_b3c45da60b5406fa18ea327dc53ed734_2BfPWMY5.flv?uuid=d59ef380-2e32-e1ce-f8c9-97bbd6587ea2";

             String url2="http://dalbj-video.baijiayun.com/b9227efa4121f18e9aecb083ac2d3ab3/5d5d4ae8/00-x-upload/video/24349930_1817c1fd632215342b58ef59ff888436_nSkmVFX5_mp4/24349930_1817c1fd632215342b58ef59ff888436_nSkmVFX5.flv?uuid=d59ef380-2e32-e1ce-f8c9-97bbd6587ea2";

             String url3="http://dalbj-video.baijiayun.com/e2442d035eca7ebcdb5a1585abb5cdc0/5d5e4f2c/00-x-upload/video/15317663_ab9da00ee9368229dcbf46b3762338a6_LuuWA7sW_mp4/15317663_ab9da00ee9368229dcbf46b3762338a6_LuuWA7sW.ev1";

             // tmpDto.uri=url3;


             //  tmpDto.uri="http://audio01.dmhmusic.com/71_53_T10047981991_128_4_1_0_sdk-cpm/0207/M00/7B/93/ChR461z87ZCAcB1eAD_XaNRR9N4986.mp3?xcode=e39838a8d114bbea4f4df19d92731d19f5f4796";
             tmpDto.coverUri="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_150,h_150";
             tmpDto.coverBig="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_450,h_450";
             tmpDto.coverSmall="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_90,h_90";
             tmpDto.fileSize=4183912;
             tmpDto.date=0;
             tmpDto.isDl=true;
             tmpDto.quality=128000;

             if(i==0){
                 tmpDto.id=15317663;
                 tmpDto.collectId="-Q8AiElRnZ5MyA0LsBVeTMKRYlbVK-P0dBgByx65H_fr9IezZr42XDTSEzZrILF4";
             }else if(i==1){
                 tmpDto.id=16091936;
                 tmpDto.collectId="w_ZgK9dOGftMyA0LsBVeTLKXgwsBKRfiE0xsO4HlQ9acpc4W_VwHrDTSEzZrILF4";
             }
             else if(i==2){
                 //"videoId":"15325035","token":"ncnxwo4TJSZMyA0LsBVeTG2eSPbHMbuUE0xsO4HlQ9acpc4W_VwHrDTSEzZrILF4"
                  tmpDto.id=15325035;
                 tmpDto.collectId="ncnxwo4TJSZMyA0LsBVeTG2eSPbHMbuUE0xsO4HlQ9acpc4W_VwHrDTSEzZrILF4";
             }
             tmplist.add(tmpDto);


         }

        PlayManager.play(0, tmplist, "public_tuijian_winter");

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = IMusicService.Stub.asInterface(iBinder);
        listener();
        initData();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }
    public static String filterFileName(String fileName) {
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.replaceAll(""); // 将匹配到的非法字符以空替换
    }


    private void loadPic(final String url,final ImageView bigImagView){


       /* RequestOptions myOptions = new RequestOptions().skipMemoryCache(true).placeholder(R.mipmap.trans_bg).optionalTransform
                (new BlurTransformation(16,12)) ;*/
        Glide.with(MainActivity.this)
              // .load("http://tiku.huatu.com/cdn/essay/correct/493d915a720f4f89822c314912cadf5e.jpeg")
                .load(url)
                //.load("http://tiku.huatu.com/cdn/essay/correct/826a1ca905b646a28dd65fc282afc276.jpg")
                .apply(RequestOptions.placeholderOf(R.mipmap.trans_bg)
                        .skipMemoryCache(true)
                       // .optionalTransform(new LimitScaleTransformation(1))

                        .format(DecodeFormat.PREFER_RGB_565).downsample(DownsampleStrategy.AT_LEAST)

                        //.dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .transition(DrawableTransitionOptions.withCrossFade(250))

             .into(new ImageViewTarget<Drawable>(bigImagView) {
            @Override
            protected void setResource(@Nullable Drawable bitmap) {
                LogUtil.e("setResource0","setResource");
                getView().setImageDrawable(bitmap);

            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                boolean needNext=false;
                if(null!=resource){
                    boolean hasOver=(resource.getIntrinsicHeight()*1f/resource.getIntrinsicWidth())>2.4f;
                    if(hasOver){
                        bigImagView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,DensityUtils.getScreenHeight(bigImagView.getContext())));
                        bigImagView.setAdjustViewBounds(false);
                    }
                    else
                    {
                        if(resource.getIntrinsicWidth()>DisplayUtil.getScreenWidth()*3f){
                            needNext=true;
                            bigImagView.setAdjustViewBounds(false);
                            bigImagView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,DensityUtils.dp2px(bigImagView.getContext(),30)));


                        }else {
                            bigImagView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

                            bigImagView.setAdjustViewBounds(true);
                        }

                    }

                    LogUtil.e("setResource",resource.getIntrinsicHeight()+","+resource.getIntrinsicWidth());
                }

                super.onResourceReady(resource,transition);
                LogUtil.e("setResource1","onResourceReady");
                if(null!=resource){
                    getView().setTag(R.id.reuse_tag2,"1");

                }


            }
        });
    }

    private void loadPic2(final String url,final ImageView bigImagView){


       /* RequestOptions myOptions = new RequestOptions().skipMemoryCache(true).placeholder(R.mipmap.trans_bg).optionalTransform
                (new BlurTransformation(16,12)) ;*/
        Glide.with(MainActivity.this)
                // .load("http://tiku.huatu.com/cdn/essay/correct/493d915a720f4f89822c314912cadf5e.jpeg")
                .load(url)
                //.load("http://tiku.huatu.com/cdn/essay/correct/826a1ca905b646a28dd65fc282afc276.jpg")
                .apply(RequestOptions.placeholderOf(R.mipmap.trans_bg)
                        .skipMemoryCache(true)
                        // .optionalTransform(new LimitScaleTransformation(1))

                        .format(DecodeFormat.PREFER_RGB_565).downsample(DownsampleStrategy.AT_LEAST)

                        //.dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .transition(DrawableTransitionOptions.withCrossFade(250))

                .into(new SimpleTarget<Drawable>() {
                  //  onResourceReady(@NonNull R resource, @Nullable Transition<? super R> transition);
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        boolean needNext=false;
                        if(null!=resource){
                            boolean hasOver=(resource.getIntrinsicHeight()*1f/resource.getIntrinsicWidth())>2.4f;
                            if(hasOver){
                                bigImagView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,DensityUtils.getScreenHeight(bigImagView.getContext())));
                                bigImagView.setAdjustViewBounds(false);
                            }
                            else
                            {
                                if(resource.getIntrinsicWidth()>DisplayUtil.getScreenWidth()*3f){
                                    needNext=true;
                                    bigImagView.setAdjustViewBounds(false);
                                    bigImagView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,DensityUtils.dp2px(bigImagView.getContext(),30)));


                                }else {
                                    bigImagView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

                                    bigImagView.setAdjustViewBounds(true);
                                }

                            }

                            LogUtil.e("setResource",resource.getIntrinsicHeight()+","+resource.getIntrinsicWidth());
                        }
                        bigImagView.setImageDrawable(resource);
                        //super.onResourceReady(resource,transition);
                        LogUtil.e("setResource1","onResourceReady");



                    }
                });
    }




    private void switchPic(){

        final  ImageView bigImagView=this.findViewById(R.id.bigViewpic);
        this.findViewById(R.id.show_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPic("http://tiku.huatu.com/cdn/essay/correct/493d915a720f4f89822c314912cadf5e.jpeg",bigImagView);


            }
        });

        this.findViewById(R.id.show_next2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(bigImagView);

            }
        });
    }

    @LoginTrace(type = 0)
    private void show(ImageView bigImagView){

        loadPic("http://tiku.huatu.com/cdn/essay/correct/826a1ca905b646a28dd65fc282afc276.jpg",bigImagView);
    }

    private String hostAndPath(String url) {
        int postion = url.indexOf("://");
        if(postion != -1) {
            url = url.substring(postion + "://".length());
        }

        postion = url.indexOf("?");
        if(postion != -1) {
            url = url.substring(0, postion);
        }

        String[] urls = url.split("/");
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < urls.length; ++i) {
            sb.append(URLEncoder.encode(urls[i]));
            if(i != urls.length - 1) {
                sb.append('/');
            }
        }

        return sb.toString();
    }
    public boolean matches(String inputUrl) {
        return    this.regex.matcher(this.hostAndPath(inputUrl)).find();
    }
    private  Pattern regex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //


        Uri url=Uri.parse("ztk://course/yearreport?url=http://m.v.huatu.com/z/1912report/#/");


        LogUtil.e("url",url.getQueryParameter("url"));
      //  mToken = PlayManager.bindToService(this, this);
        setContentView(R.layout.activity_main);
        switchPic();
        // "2019年1月7日【六点半课堂讲义：常识第一讲：一个口诀，搞定人大】-李梦娇老师.pdf"
        String test="2019年1月7日【六点半课/堂讲义：常识第一讲：一个口诀，搞定人大】-李梦娇老师.pdf";


        this.regex = Pattern.compile(this.hostAndPath("ztk://course/collection/{fragment}").replaceAll("%7B(([a-zA-Z][a-zA-Z0-9_-]*))%7D", "([a-zA-Z0-9_#'!+%~,\\-\\.\\$]*)") + "$");


        LogUtil.e("filterFileName", matches("ztk://course/collection")+"");

        int fontsize= ResourceUtils.getDimensionPixelSize(R.dimen.check_15sp);
        LogUtil.e("filterFileName",filterFileName(test)+"_____"+fontsize+"___"+DensityUtils.sp2px(this,15));

        final  ImageView bigImagView=this.findViewById(R.id.bigViewpic);

        //"https://dg-fd.zol-img.com.cn/t_s2000x2000/g5/M00/0F/04/ChMkJ1vtKp-IUQb0AANhw8f2qMgAAtLUwOgTR8AA2Hb581.jpg"
       // loadPic2("https://imgsa.baidu.com/exp/w=480/sign=c691eade37fa828bd1239cebcd1d41cd/ac6eddc451da81cb4374ed165666d0160824315e.jpg",bigImagView);
        bindBgPlayPresent();
   /*     Glide.with(this)
                .load("https://dg-fd.zol-img.com.cn/t_s2000x2000/g5/M00/0F/04/ChMkJ1vtKp-IUQb0AANhw8f2qMgAAtLUwOgTR8AA2Hb581.jpg")
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 5)))
                .into(bigImagView);
*/


     /*   Glide.with(this).asDrawable()
                .load("http://tiku.huatu.com/cdn/essay/correct/493d915a720f4f89822c314912cadf5e.jpeg")
                .apply(RequestOptions.placeholderOf(R.drawable.trans_bg)

                .skipMemoryCache(true)
                .transition(withCrossFade())
                .format(DecodeFormat.PREFER_RGB_565)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)

                .into(new ImageViewTarget<Drawable>(bigImagView) {
                    @Override
                    protected void setResource(@Nullable Drawable bitmap) {




                        getView().setImageDrawable(bitmap);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource,transition);
                        if(null!=resource){
                            getView().setTag(R.id.reuse_tag2,"1");

                        }
                    }
                });*/
        //test5();
        //testVerson();
        //test2();
    }

    BgPlayPresenter mBgPlayPresenter;
    private void bindBgPlayPresent(){

        if(null==mBgPlayPresenter){
            Music curDto =com.huatu.test.utils.LogUtils.convertCoursewareToMusic();
            mBgPlayPresenter=new BgPlayPresenter();
            mBgPlayPresenter.attachView(new BgPlayContract.BaseView() {
                @Override
                public long getCurrentPosition() {
                    /*if(null!=mPlayerView){
                        return   mPlayerView.getCurrentPosition();
                    }*/
                    return 23;
                }

                @Override
                public boolean isPlaying() {

                    return true;
                }

                @Override
                public void doPauseAction() {
                    //  if(null!=mB)
                    /*if(null!=mPlayerView){
                        mPlayerView.pauseVideo();
                    }*/
                }

                @Override
                public void doResumeAction() {
                   /* if(null!=mPlayerView){
                        mPlayerView.playVideo();
                    }*/
                }

                @Override
                public Context getContext() {
                    return MainActivity.this;
                }
            });
           // mBgPlayPresenter.updateMetaData(curDto);

        }else {
           // mBgPlayPresenter.requestAudioFocus();
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setClipViewCornerRadius(View view, final int radius) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //不支持5.0版本以下的系统
            return;
        }

        if (view == null) return;

        if (radius <= 0) {
            return;
        }
        view.setOutlineProvider(new ViewOutlineProvider() {

            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        view.setClipToOutline(true);

    }
    void test5(){
        LogUtils.e("testVerson", DateUtils.isVersionLower("7.2.0","7.2.001")+"");
        LogUtils.e("testVerson", DateUtils.isVersionLower("7.1.163","7.2.001")+"");
        EssayExerciseTextView textView=this.findViewById(R.id.answer_analysis_txt);
        textView.setHtmlSource(testStr);



       //String url="http://dalbj-video.baijiayun.com/3067abc4d892f224f6abc467b6b10647/5d5d2c7f/00-x-upload/video/24821080_b3c45da60b5406fa18ea327dc53ed734_2BfPWMY5_mp4/24821080_b3c45da60b5406fa18ea327dc53ed734_2BfPWMY5.flv?uuid=d59ef380-2e32-e1ce-f8c9-97bbd6587ea2";
        String url="http://audio01.dmhmusic.com/71_53_T10041186386_128_4_1_0_sdk-cpm/0208/M00/31/82/ChR461pkWrSATIJAADxgUoFZkm0420.mp3?xcode=9bcc9192100b10b94f511bb439f56be0d1c252c";
       // url=Environment.getExternalStorageDirectory()+"/song.mp3";

        url=Environment.getExternalStorageDirectory()+"/test.flv";
        url="http://dalbj-video.baijiayun.com/3067abc4d892f224f6abc467b6b10647/5d5d2c7f/00-x-upload/video/24821080_b3c45da60b5406fa18ea327dc53ed734_2BfPWMY5_mp4/24821080_b3c45da60b5406fa18ea327dc53ed734_2BfPWMY5.flv?uuid=d59ef380-2e32-e1ce-f8c9-97bbd6587ea2";

        //initMediaPlayer(url, 0);

  /* ViewGroup.LayoutParams tmpParams= coverImg.getLayoutParams();
        tmpParams.width= DensityUtils.dp2px(coverImg.getContext(),166);
        tmpParams.height=DensityUtils.dp2px(coverImg.getContext(),99);
        coverImg.setLayoutParams(tmpParams);*/

      /*  int Width=coverImg.getWidth();
        int heigth=coverImg.getHeight();

        int scaleWidth=DensityUtils.dp2px(coverImg.getContext(),166);
        int scaleHeigh=DensityUtils.dp2px(coverImg.getContext(),99);

        Rect fullBounds = new Rect(coverImg.getLeft(), coverImg.getTop(),coverImg.getRight(), coverImg.getBottom());

       // Rect thumbnailBounds = new Rect(coverImg.getLeft(), (fullBounds.bottom-scaleHeigh)/2, (fullBounds.right+scaleWidth)/2, (fullBounds.bottom+scaleHeigh)/2);

        Rect thumbnailBounds = new Rect(coverImg.getLeft(), (fullBounds.bottom-scaleHeigh)/2, (fullBounds.right+scaleWidth)/2, (fullBounds.bottom+scaleHeigh)/2);
        Animator boundsAnimator = ObjectAnimator.ofObject(coverImg, BOUNDS,
                new TransitionImageView.RectEvaluator(),  fullBounds   ,  thumbnailBounds);

        boundsAnimator.setDuration(300);
        boundsAnimator.setStartDelay(2000);
        boundsAnimator.start();*/

       final  View image= this.findViewById(R.id.bigViewpic);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(image,"scaleX",1f,0.5f);

        ObjectAnimator  objectAnimator2 = ObjectAnimator.ofFloat(image,"scaleY",1f,0.6f);

        setClipViewCornerRadius(image, DensityUtils.dp2px(image.getContext(),15));
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(
                objectAnimator,
                objectAnimator2);
        animator.setDuration(300);
        animator.setStartDelay(2000);
       /* animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setClipViewCornerRadius(image, DensityUtils.dp2px(image.getContext(),15));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
        animator.start();
    }

    MediaPlayer  mMediaPlayer =null;
    private void initMediaPlayer(String url,int Rid) {


        try {

            if(Rid>0){
                 mMediaPlayer=  MediaPlayer.create(this, Rid);
             }else {
                 mMediaPlayer=  new MediaPlayer();
            }

            mMediaPlayer.setDataSource(this, Uri.parse(url));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mMediaPlayer.start();
                }
            });
          //  mMediaPlayer.setOnCompletionListener(this);
          //  mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
          //  mMediaPlayer.setOnSeekCompleteListener(this);
          //  mMediaPlayer.setOnErrorListener(this);
          //  mMediaPlayer.setOnInfoListener(this);
         //   mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void test4(){
        final  View tmpView= this.findViewById(R.id.show_next);
        this.findViewById(R.id.show_next2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)tmpView.getParent()).removeView(tmpView);
            }
        });

        tmpView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                  LogUtils.e("ViewAttached","onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                LogUtils.e("ViewAttached","onViewDetachedFromWindow");
            }
        });
    }

   /* private void test3(){
       final File musiceFile = new File(Environment.getExternalStorageDirectory(), "shuyan.mp3");
      *//*  if(musiceFile.exists())
            audioPlayerView.setAudioData(musiceFile.getAbsolutePath(),0,240);*//*

        final CustomAudioPlayerView playerView=  this.findViewById(R.id.voice_playview);
        playerView.setAudioData(musiceFile.getAbsolutePath(),0,240);

        this.findViewById(R.id.show_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerView.reSetAudioData(musiceFile.getAbsolutePath(),0,240);
            }
        });
    }*/


    private void testVerson(){
        LogUtils.e("testVerson", DateUtils.isVersionLower("7.1.163","7.2.0")+"");

        LogUtils.e("testVerson2", DateUtils.isVersionLower("7.2.0","7.1.163")+"");

        LogUtils.e("testVerson2", DateUtils.isVersionLower("7.2.0","7.2.2")+"");
    }




    String testStr="<p>第一步，本题考查年龄问题，用代入排除法解题。<br/>第二步，2009年的50多年前，是1950—1958年。<br/>代入A选项，如果2015年时老人78岁，那么老人是1937年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://img.mp.itc.cn/upload/20170703/ff038c9bcf0c44c49f9c92a7a75a36f9_th.jpg\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\"><br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。</p><br/>代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/f2c49778-d444-4902-ac26-5b3a7185b4e0..png?imageView2/0/w/795/format/jpg\" width=\"641\" height=\"99\" style=\"width: 641px; height: 99px;\"><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。</p>";
    //String testStrok2="<br/>第一步，本题考查年龄问题，用代入排除法解题。<br/>第二步，2009年的50多年前，是1950—1958年。<br/>（1）代入A选项，如果2015年时老人78岁，那么老人是1937年出生的。那么1950—1958年他的年龄如下表：<br/>2<img src=\"http://static.nipic.com/images/aqkx_124x47.png\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\"><br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。<br/>（2）代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/>rrr<img src=\"https://imgsa.baidu.com/normandy/pic/item/72f082025aafa40f2dcf8636a564034f79f019cb.jpg\" width=\"641\" height=\"99\" style=\"width: 641px; height: 99px;\"><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。";
    //String testStrok="<br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。<br/>（2）代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/f2c49778-d444-4902-ac26-5b3a7185b4e0..png?imageView2/0/w/795/format/jpg\" width=\"641\" height=\"99\" style=\"width: 641px; height: 99px;\"><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。</p><br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/7aa1c567-0bce-41b1-a570-dfe4b3a1f4f0..png?imageView2/0/w/806/format/jpg\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\">";

     String testStrok="<p><br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。<br/>（2）代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/f2c49778-d444-4902-ac26-5b3a7185b4e0..png?imageView2/0/w/795/format/jpg\" width=\"641\" height=\"99\" style=\"width: 641px; height: 99px;\"><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。</p><br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/7aa1c567-0bce-41b1-a570-dfe4b3a1f4f0..png?imageView2/0/w/806/format/jpg\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\">";
//<img src="http://tiku.huatu.com/cdn/pandora/img/7aa1c567-0bce-41b1-a570-dfe4b3a1f4f0..png" width="641" height="99" style="width: 641px; height: 99px;">
   // String testStr="<p>第一步，本题考查年龄问题，用代入排除法解题。<br/>第二步，2009年的50多年前，是1950—1958年。<br/>（1）代入A选项，如果2015年时老人78岁，那么老人是1937年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/7aa1c567-0bce-41b1-a570-dfe4b3a1f4f0..png?imageView2/0/w/806/format/jpg\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\">1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。";
   // String testStr="<br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。<br/>（2）代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/f2c49778-d444-4902-ac26-5b3a7185b4e0..png?imageView2/0/w/795/format/jpg\" width=\"641\" height=\"99\" style=\"width: 641px; height: 99px;\"><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。</p>";
   // String testStr="<p>第一步，本题考查年龄问题，用代入排除法解题。<br/>第二步，2009年的50多年前，是1950—1958年。<br/>（1）代入A选项，如果2015年时老人78岁，那么老人是1937年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/7aa1c567-0bce-41b1-a570-dfe4b3a1f4f0..png?imageView2/0/w/806/format/jpg\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\"><br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。<br/>（2）代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。</p>";

   // String testStr="<p>第一步，本题考查年龄问题，用代入排除法解题。<br/>第二步，2009年的50多年前，是1950—1958年。<br/>（1）代入A选项，如果2015年时老人78岁，那么老人是1937年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/7aa1c567-0bce-41b1-a570-dfe4b3a1f4f0..png?imageView2/0/w/806/format/jpg\" width=\"645\" height=\"99\" style=\"width: 645px; height: 99px;\">";
   //  String testStr="<p>第一步，本题考查年龄问题，用代入排除法解题。<br/>第二步，2009年的50多年前，是1950—1958年。<br/>（1）代入A选项，如果2015年时老人78岁，那么老人是1937年出生的。那么1950—1958年他的年龄如下表：<br/><br/>1958年他21岁，但是1＋9＋5＋8＝23≠21，不满足条件，故排除A选项。<br/>（2）代入B选项，如果2015年时老人80岁，那么老人是1935年出生的。那么1950—1958年他的年龄如下表：<br/><img src=\"http://tiku.huatu.com/cdn/pandora/img/f2c49778-d444-4902-ac26-5b3a7185b4e0..png?imageView2/0/w/795/format/jpg\" width=\"641\" height=\"99\" style=\"width: 641px; height: 99px;\"><br/>1958年他23岁，且1＋9＋5＋8＝23，满足条件，故B选项符合题意。<br/>因此，选择B选项。</p>";
    private  final RequestOptions mImageOptions =
            new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);
    private final static Pattern ATTRIBPATTERN = Pattern.compile("(seq|description|score|drawType)" + "=\\s*\"([^\"]+)\"" );//"=\\s*\"([^\"]+)\""          "=\\s*([^\\s]+)"
    private void  test(){


        ImageView thumbImagView=this.findViewById(R.id.bigViewpic);
        String imgUrl="http://tiku.huatu.com/cdn/essay/correct/3362cb774eb9476da9516c97ec048929.jpeg";
      /*  Glide.with(this)
                .load(imgUrl).apply(mImageOptions)//.override(mWidthPic,mHeightPic)
                // .apply(mUploadlocalOption.override(size[0], size[1]))

                .transition(withCrossFade())
                .into(thumbImagView);*/


      final  ImageView bigImagView=this.findViewById(R.id.bigViewpic);
        String imgUrl2="http://tiku.huatu.com/cdn/essay/correct/3362cb774eb9476da9516c97ec048929.jpeg?t=1";
/*        Glide.with(this)
                .load(imgUrl2)//.override(mWidthPic,mHeightPic)
                // .apply(mUploadlocalOption.override(size[0], size[1]))


                .into(bigImagView);*/

/*
       Glide.with(this).asDrawable().load(imgUrl2).transition(withCrossFade()).into(new ImageViewTarget<Drawable>(bigImagView) {
           @Override
           protected void setResource(@Nullable Drawable bitmap) {
               getView().setImageDrawable(bitmap);
           }
       });*/

        Glide.with(this)
                .load("http://pic.58pic.com/58pic/13/16/45/68p58PICJZr_1024.png")
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .skipMemoryCache(true)
                        // .transition(withCrossFade())
                        .format(DecodeFormat.PREFER_RGB_565)
                        //.dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).transition(withCrossFade()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        })
                .into(bigImagView);

/*
       Glide.with(this)
                .asDrawable()
                .thumbnail(.2f)
                .load(imgUrl2)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).skipMemoryCache(true))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        // hide loading

                        bigImagView.setImageDrawable(resource);

                    }
                }); */

        EssayExerciseTextView textView=this.findViewById(R.id.answer_analysis_txt);

        CheckDetailBean tmpBean=new CheckDetailBean();
        tmpBean.correctType=1;
        tmpBean.correctedContent="     1.<电商平台(+2.0分)>的出现和普及，改变了农产品<销售方式(+1.0分)>，拓宽了销售渠道。\n       2.村镇居民<收入增(+2.0分)>加，生活水平提高。\n       \n" +
                "        3.村民对[文化生活(文化生活……期待 +2.0分)]有新的[期待]，希望有跳舞、下棋的娱乐场所。\n       \n" +
                "        4.村镇的交通、居住的生活[环境]得到了[改善(环境……改善 +2.0分)]。\n       5.<人口流(+2.0分)>向发生改变，回乡创业打工的人越来越多。\n       \n" +
                "        6.<资金流(+2.0分)>向发生改变，城里往乡镇的投资越来越多。\n       7.城镇居民[观念(观念……变 +2.0分)]转[变]，对农村未来的发展信心更足，相信生活会越来越好。";



       // String testStr="<label_1 seq=\"1\" description=\"文采文采不优美(丑)要点准确(不准确)\" score=\"0.0\" drawType=\"0\"></label_1 seq=\"1\" description=\"文采文采不优美(丑)要点准确(不准确)\" score=\"0.0\" drawType=\"0\"><label_2 seq=\"2\" description=\"要点不准确\" score=\"0.0\" drawType=\"0\"><label_3 seq=\"3\" description=\"文采文采斐然(美)\" score=\"0.0\" drawType=\"0\">";

    //   String testStr="    M工具(四) 选项F(出) 周LW心的Q留.●.A.00的。血’0.~.\n    名称点击左最的箭头按钮。\n    a x.1个人网站\n    02个人网站x ＋i mysqL config\n    mysqL_ secure installation\n    Troot aiz22 ehvny1Lsb7a258fkygz bin]# cd mysql\n    hvsaL embedded\n    mysqLmysqladminmysqlimport\n    mysq1LbinLogmysql install db \n    mysqlcheckmysql plugin\n    HVsaL Clientest_ embedded mysqLpump\n    I mysqlmysql configmysqL secure installation\n    mysqL config_ editormysqlshow\n    mysqldmysqlslap\n    mysqld-debugmysql ssl rsa setup\n    mys<label_2 seq=\"2\" description=\"标题切题 ,论点准确,论证比较有力,结构完整,语言 比较通畅,文采 比较有文采,思想性思想深刻,分段 过多,字数符合要求,的法国红酒，。 \" score=\"0.0\" drawType=\"0\">qld_ multimysqltest embeddedmysqld safemysql tzinfo to sqlmysqldumpmysql upgrade, mysqRmysqldumpsLowmvsaiytos</label_2>t\n    @iz2zehvny1lsb7 a258fkygz bin]# cd mysgl\n    -bash: cd: mysql: Not a directory[root aizz <label_1 seq=\"1\" description=\"标题切题 ,论点准确,论证强有力,结构完整,语言通畅,文采文采斐然,思想性思想深刻,分段合理,字数符合要求,此处填写其他批注，应该还是可以ide\" score=\"0.0\" drawType=\"0\">ehvny1lsb7a258fkygz bin]# ls\n    innochecksum\n    mysqL_ config editor\n    mysqlshow\n    Lz4 decompressmysqLdmysqlslap\n    myisamchkysqLd-debugmysql_ ssl_ rsa setup\n    myisam ftdupysqLd_mwLtimysqltest_ embedde</label_1>dmysqLd safeysqL tzinfo .to sqLmysqL upgradey print defaultsmysqLdumpsLowysqlxtest\n    mysqperror\n    replace\n    resolveip\n    resolve_ stack dump\n    mysqL client_ test embedded mysqLpumpzlib decompressmysqL configmysqL_ secure installation[root@iz2zehvny1Lsb7 a258fkygz bin]# vi mysq站属性[root @iz2zehvny1lsb7a258fkygz bin]#socket error Event: 32 Error: 10053.值个人网站Lonnection cLos1ng. . .Socket close .会话Connection closed by foreign host,39.107.82..Disconnected from remote host(个 人网站) at 13:04:31.SSHTyDe \"heLp' to learn how to use Xshell prompt.户名[D:\\~]$连接。O\n    在这里输入你要搜索的内容。HC.四\n    。CHOVO\n    HBE\n    Pad令下午2:45\n    1业76%申论批改订单人工批改\n    标准答案1次\n    1\n    ＋￥3.00标准答案-人工[一1＋]￥1.00￥2.00套题批改1次(一0＋}￥1.00文章写作批改1次[一0＋I￥1.00文章写作批改1次-有效期3个月[--0＋￥1.00智能批改\n    标准答案1次\n    0十\n    ￥3.00套题批改1次(一0＋)\n    ￥5.00文章写作批改1次(一0＋}￥1.00支付方式:\n    微信支付\n    图币支付\n    支支付宝\n    说明:\n    您在该设备.上购买的商品为虚拟内容服务，购买后不支持退款、转让、退换和发票申请。\n    合计:￥4.00\n    立即购买\n    ";


       String testStr="选贤任能 为政以德\n  自古以来，我国就是一个伦理道德色彩浓厚的国家，反映在治国理政领域，尤其推崇选贤任能、道德之治，认为贤明和官德是兴国安邦的重要元素，是构建治理体系的重要基础。儒家强调“政治就是道德之治”，墨家主张“尚贤”“兼爱”，实际上都认可贤明的道德治理具有重要意义。<label_1 seq=\"1\" description=\"“可以说，“选贤任能，为政以德”一直以来都是中华政治文明中的深沉底色。”很充分体现了“为政以德是中华政治文明中的深沉底色”\" score=\"0.0\" drawType=\"0\">可以说，“选贤任能，为政以德”一直以来都是中华政治文明中的深沉底色。</label_1>\n    <label_2 seq=\"2\" description=\"“为政以德的贤者须“厚乎德行”。”基本体现本文思想\" score=\"0.0\" drawType=\"0\">为政以德的贤者须“厚乎德行”。</label_2><label_3 seq=\"3\" description=\"““厚乎德行”，在墨子看来就是：有力者助人、有财者分人、有道者教人。”比较充分论证了“为政以德的贤者须“厚乎德行”。”这个思想\" score=\"0.0\" drawType=\"0\">“厚乎德行”，在墨子看来就是：有力者助人、有财者分人、有道者教人。显然，墨子将贤者的“德”由个人修养扩展到社会层面，“德”不再停留在自律层面，尽到社会责任也成为“德”的一把标尺。为政者肩负调整社会利益、增进民众权益的责任，如果一味打着自己的小算盘、小九九，缺少道德约束，社会治理必将紊乱不堪。所以贤能的为政者向来注重“厚乎德行”，不断砥砺提升自身道德水平。C市基层干部许女士，被群众称为“月光书记”，多年以来，把所有的工资、奖金都捐给了社区，还向企业家“化缘”，以资助百姓，对自己的生活却非常“刻薄”。可以说，她为我们树立了贤明的为政者“厚乎德行”的光辉形象。</label_3>\n    <label_4 seq=\"4\" description=\"“为政以德的贤者须“辩乎言谈”。”基本体现本文思想\" score=\"0.0\" drawType=\"0\">为政以德的贤者须“辩乎言谈”。</label_4><label_5 seq=\"5\" description=\"“这里的“辩”与“言”不是以口舌的劳累来显示自己漂亮的口才，而是要善于为自己所坚守的道义做出解释和宣传……”充分论证了“为政以德的贤者须“辩乎言谈”。”这个思想\" score=\"0.0\" drawType=\"0\">这里的“辩”与“言”不是以口舌的劳累来显示自己漂亮的口才，而是要善于为自己所坚守的道义做出解释和宣传。为政者并非活在真空中，必须时刻与民众保持密切联系。每个为政者必须认真考量，如何与民众进行有效沟通交流，让民众心甘情愿听从为政者的治理安排，做到“教成于上，化成于下”。原N自治区某县组织部卢副部长在做群众工作时，注重方式方法，说“老百姓的话”给百姓听，让民众将其当做贴心人。这就是贤明为政者“辩乎言谈”的有效体现。</label_5>当前，为政者都应该具备这种“辩乎言谈”的贤士之德，以“言”传道、以“辩”论圣人之言，如此才能劝勉他人，居上位能体恤百姓，处下位能发光发热。\n    为政以德的贤者须“博乎道术”。“博乎道术”要求贤者学习治国辅政的知识与谋略，从而具备良好的处理事情的才能和运用知识的实践能力。明代著名哲学家王阳明，戎马倥偬之余，格物致知，不断学习，创立了“致良知”的心学，将“知行合一”运用到实践中，这是为政以德的贤者“博乎道术”的典型体现。当今社会发展速度加快，利益纠葛冲突高发，为政者更应该紧跟时代发展，“博乎道术”，具备适应时代的本领，不停接受新理念、不断学习新知识，方能更好应对愈益复杂的社会生活，使自身才具匹配国家发展的要求。\n    值此新时代，实现民族复兴的中国梦尚需我们勠力同心。处于“关键少数”位置的为政者更应该以德修身，常怀慎独之心；<label_6 seq=\"6\" description=\"“以德服众，常思为民之道，不断提高道德修养，不断干出工作实绩，从而无愧于民众期许，无愧于时代使命。”结束全文，结构完整\" score=\"0.0\" drawType=\"0\">以德服众，常思为民之道，不断提高道德修养，不断干出工作实绩，从而无愧于民众期许，无愧于时代使命。</label_6>。";
         String regEx_html = "<[^>/]+>"; //定义HTML标签的正则表达式

        Pattern tagPattern = Pattern.compile(regEx_html);
        Matcher matcher = tagPattern.matcher(testStr);
        ArrayList<ScoreListEntity> tagBeans = new ArrayList<>();
        boolean hasFind=false;
        while (matcher.find()) {

            String content = matcher.group();
            ScoreListEntity bean = new ScoreListEntity();

            Matcher attribmatcher= ATTRIBPATTERN.matcher(content);
            hasFind=false;
            while (attribmatcher.find()){
                hasFind=true;
                // LogUtils.e("attribmatcher", attribmatcher.group(1)+","+ attribmatcher.group(2));
                String attrName=attribmatcher.group(1);
                String attrValue=attribmatcher.group(2);
                attrValue= TextUtils.isEmpty(attrValue)? attrValue:attrValue.replace(">","").replaceAll("\"","");
                if("seq".equals(attrName)){
                    bean.sequenceNumber= StringUtils.parseInt(attrValue);

                }else if("description".equals(attrName)){
                    bean.scorePoint=attrValue;
                }else if("score".equals(attrName)){
                    bean.score=StringUtils.parseDouble(attrValue);
                }
            }
            if(hasFind)
             tagBeans.add(bean);
        }
        //textView.initContent(EssayExerciseTextView.StringTest);
        textView.setHtmlSource(testStr);
       // textView.setText();
    }


    private void test2(){


        String str = "<line len=5 size=20 content=百日依山尽>012345<background len=10 size=10 content=黄河入海流>6789</ line>abcd</ background>ABCDE";
        String match = "<line.*?>|<background.*?>|</.*?line>|</.*?background>";
        Pattern pattern = Pattern.compile(match);
        Matcher matcher = pattern.matcher(str);
        ArrayList<TagBean> tagBeans = new ArrayList<>();
        while (matcher.find()) {

            int start = matcher.start();
            int end = matcher.end();
            String content = matcher.group();

            TagBean bean = new TagBean();
            bean.isStart = !content.startsWith("</");
            bean.tag = bean.isStart ?
                    content.split(" ")[0].replace("<", "") :
                    content.split(" ")[1].replace(">", "");
            bean.content = content;
            bean.start = start;
            bean.end = end;
            tagBeans.add(bean);
        }

        // 这里操作变量，得到最后去标签的内容
        StringBuffer buffer = new StringBuffer(str);

        // 存储临时未闭合的标签变量
        LinkedList<TagBean> tempTag = new LinkedList<>();
        // 存储闭合的解析
        ArrayList<Line> lines = new ArrayList<>();
        // 从后往前遍历，为了容易计算位置
        for (int i = tagBeans.size() - 1; i >= 0; i--) {
            TagBean bean = tagBeans.get(i);
            // 还要去除buffer中的标签
            buffer.replace(bean.start, bean.end, "");
            if (bean.isStart) { // 开始，去寻找对应的就近的结束标签，然后解析变量，完成配对，放入lines
                TagBean endTag = findEndTag(tempTag, bean);
                int realStart = buffer.length() - bean.start;
                Line line = new Line();
                line.start = realStart;
                line.end = endTag.realEnd;
                Tag tag = new Tag();
                line.tag = tag;
                // 解析标签的内容
                String content = bean.content;
                String[] split = content.replace(">", "").split(" ");
                for (int j = 1; j < split.length; j++) {
                    String s = split[j];
                    String[] field = s.split("=");
                    if (field[0].equals("len")) {
                        tag.len = Integer.parseInt(field[1]);
                    } else if (field[0].equals("size")) {
                        tag.size = Integer.parseInt(field[1]);
                    } else if (field[0].equals("content")) {
                        tag.content = field[1];
                    }
                }
                lines.add(line);
            } else {            // 如果是结束就直接存储进tempTag，因为只有开始标签才能和结束标签闭合
                // 真实倒数位置
                bean.realEnd = buffer.length() - bean.start;
                tempTag.add(0, bean);
            }
        }
        // 重新计算位置
        for (Line line : lines) {
            line.start = buffer.length() - line.start;
            line.end = buffer.length() - line.end;
        }

        System.out.println("");

        System.out.println(str);
        for (Line line : lines) {
            System.out.println(line);
        }
        System.out.println(buffer);
    }

    private static TagBean findEndTag(LinkedList<TagBean> tempTag, TagBean bean) {
        for (TagBean tagBean : tempTag) {
            if (tagBean.tag.equals(bean.tag)) {
                tempTag.remove(tagBean);
                return tagBean;
            }
        }
        return null;
    }

}
