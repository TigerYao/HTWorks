package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;

public class LiveVideoNotificationService extends Service {
    private static final String TAG = "zhibo.play.LiveVideo";
    private Context mContext;
    private NotificationCompat.Builder mBuilder = null;
    private NotificationChannel mNotificationChannel = null;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private static int mActionOperation = -1;
    private String imgUri;
    public static final int PAUSE_MUSIC = 10000;
    public static final int PLAY_MUSIC = 10002;
    public static final int CANCEL_NOTIFY = 10003;
    public static final String ACTION = "com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoNotificationService";

    public static void notifyAction(Context ctx, String title, String img, int action) {
        if(action == mActionOperation)
            return;
        Intent intent = new Intent(ctx, LiveVideoNotificationService.class);
        intent.putExtra("operation", action);
        intent.putExtra("title", title);
        intent.putExtra("img", img);
        Log.i("xiaolong", "open" + action);
//        ctx.startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && action != CANCEL_NOTIFY) {
            ctx.startForegroundService(intent);
        } else {
            ctx.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if (null != mRemoteViews) {
            mRemoteViews.removeAllViews(R.layout.live_video_notify_layout);
            mRemoteViews = null;
        }
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.live_video_notify_layout);
        createNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return flags;
        Log.i("xiaolong", "onStartCommand");
        int operation = intent.getIntExtra("operation", -1);
        if (mActionOperation == operation && operation != -1)
            return START_STICKY;
        mActionOperation = operation;
        if (TextUtils.isEmpty(imgUri)) {
            String musicTitle = intent.getStringExtra("title");
            imgUri = intent.getStringExtra("img");
            if (!TextUtils.isEmpty(musicTitle))
                mRemoteViews.setTextViewText(R.id.notify_title, musicTitle);
            if (!TextUtils.isEmpty(imgUri))
                displayRemoteView(imgUri);
        }
        switch (operation) {
            case PAUSE_MUSIC:
                mRemoteViews.setImageViewResource(R.id.notify_play, R.drawable.ic_play);
                mBuilder.setTicker("暂停播放");
                break;
            case PLAY_MUSIC:
                mRemoteViews.setImageViewResource(R.id.notify_play, R.drawable.ic_pause);
                mBuilder.setTicker("开始播放");
                break;
            case CANCEL_NOTIFY:
                hideNotification();
                stopSelf();
                return START_STICKY;
            default:
                break;
        }
        startForeground(100, mBuilder.build());
        return START_STICKY;
    }

    private void displayRemoteView(String url) {
        Glide.with(UniApplicationContext.getContext()).asBitmap().apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap drawable, Transition<? super Bitmap> transition) {
                mRemoteViews.setImageViewBitmap(R.id.notify_img, drawable);
                mNotificationManager.notify(100, mBuilder.build());
            }
        });
    }

    private void createNotification() {
        if (null == mNotificationChannel) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationChannel = new NotificationChannel(TAG, TAG, NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationChannel.setSound(null,null);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
            }
        }
        if (null == mBuilder) {
            mBuilder = new NotificationCompat.Builder(mContext, TAG)
                    .setSmallIcon(R.drawable.icon_app)
                    .setContent(mRemoteViews)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .setSound(null)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            mBuilder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        }

        Intent mainIntent = new Intent(mContext, LiveVideoForLiveActivity.class);
        mainIntent.setAction(ACTION);
        PendingIntent  openIntent = PendingIntent.getActivity(mContext, 0, mainIntent, 0);
        mBuilder.setContentIntent(openIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.ll_customNotification,openIntent);

        Intent playMusicIntent = new Intent(ACTION);
        playMusicIntent.putExtra("operation", PLAY_MUSIC);
        PendingIntent playMusicPi = PendingIntent.getBroadcast(mContext, 2, playMusicIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_play, playMusicPi);

        Intent cancelNotifyIntent = new Intent(mContext, LiveVideoNotificationService.class);
        cancelNotifyIntent.putExtra("operation", CANCEL_NOTIFY);
        PendingIntent cancelNotifyPi = PendingIntent.getService(mContext, 4, cancelNotifyIntent, 0);
        mBuilder.setDeleteIntent(cancelNotifyPi);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_clear, cancelNotifyPi);
    }

    public void hideNotification() {
        imgUri = null;
        mNotificationManager.cancel(TAG, 100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.deleteNotificationChannel(TAG);
        } else {
            stopForeground(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideNotification();
    }
}