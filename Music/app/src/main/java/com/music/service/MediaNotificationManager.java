package com.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.widget.RemoteViews;

import com.music.R;
import com.music.activity.MainActivity;
import com.music.entity.MusicInfo;
import com.music.sqlite.DBUtil;
import com.music.util.LogUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/28.
 */
public class MediaNotificationManager {

    private Context mContext;
    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationManager mNotificationManager;
    private static MediaNotificationManager sInstance;
    private final static int NOTIFICATION_ID = 1;

    public static synchronized MediaNotificationManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MediaNotificationManager(context);
        }
        return sInstance;
    }

    public MediaNotificationManager(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void remove() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * 更新notification
     */
    public void updateNotification() {
        LogUtil.d(null, "updateNotification...");
        final ComponentName serviceName = new ComponentName(mContext, MediaPlaybackService.class);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification);

        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentIntent(pi)
                .setAutoCancel(false);
        notification = builder.getNotification();
        notification.flags = Notification.FLAG_NO_CLEAR;

        if (MusicUtil.sService != null) {
            try {
                MusicInfo music = MusicUtil.sService.getCurrentMusic();
                if (music != null) {
                    remoteViews.setTextViewText(R.id.txtTitle, music.getMusicName());
                } else {
                    List<MusicInfo> list = DBUtil.getInstance().getMusicInfoDao().queryAll();
                    if (list != null && list.size() > 0) {
                        music = list.get(0);
                        remoteViews.setTextViewText(R.id.txtTitle, music.getMusicName());
                    }
                }
                if (MusicUtil.sService.isPlaying()) {
                    remoteViews.setImageViewResource(R.id.play, R.drawable.ic_notification_pause);
                } else {
                    remoteViews.setImageViewResource(R.id.play, R.drawable.ic_notification_play);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        intent = new Intent(MediaPlaybackService.TOGGLEPAUSE_ACTION);
        intent.setComponent(serviceName);
        PendingIntent pausePIntent = PendingIntent.getService(mContext, 0 /* no requestCode */, intent, 0 /* no flags */);
        remoteViews.setOnClickPendingIntent(R.id.play, pausePIntent);

        intent = new Intent(MediaPlaybackService.NEXT_ACTION);
        intent.setComponent(serviceName);
        PendingIntent nextPIntent = PendingIntent.getService(mContext, 0 /* no requestCode */, intent, 0 /* no flags */);
        remoteViews.setOnClickPendingIntent(R.id.next, nextPIntent);

        mNotificationManager.notify(NOTIFICATION_ID, notification);

    }


}
