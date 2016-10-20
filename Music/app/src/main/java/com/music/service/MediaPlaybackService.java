package com.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

import com.music.IMediaPlaybackService;
import com.music.R;
import com.music.activity.MainActivity;
import com.music.entity.MusicInfo;
import com.music.receiver.HeadsetPlugReceiver;
import com.music.sqlite.DBUtil;
import com.music.util.LogUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/13.
 */
public class MediaPlaybackService extends Service {

    public static final String SERVICECMD = "com.fbm.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static final String TOGGLEPAUSE_ACTION = "com.fbm.music.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "com.fbm.music.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "com.fbm.music.musicservicecommand.previous";
    public static final String NEXT_ACTION = "com.fbm.music.musicservicecommand.next";

    public static final String CANCEL_NOTIFICATION_ACTION = "com.music.notification.cancel";

    private MusicControl mMusicControl;
    private NotificationManager mNotificationManager;
    private int mServiceStartId = -1;
    private List<MusicInfo> mMusicList;

    private final static int NOTIFICATION_ID = 1;

    private HeadsetPlugReceiver headsetPlugReceiver;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                Log.i("dingfeng", "next...");
                mMusicControl.next();
            } else if (CMDPREVIOUS.equals(cmd)) {
                mMusicControl.pre();
            } else if (CMDTOGGLEPAUSE.equals(cmd)) {
                mMusicControl.pause();
            } else if (CMDPAUSE.equals(cmd)) {
                mMusicControl.pause();
            } else if (CMDPLAY.equals(cmd)) {
                mMusicControl.rePlay();
            } else if (CMDSTOP.equals(cmd)) {

            }

            if (CANCEL_NOTIFICATION_ACTION.equals(action)) {
                mMusicControl.stop();
                cancelNotification();
            }
        }

    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

            }
        }

    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mMusicList = DBUtil.getInstance().getMusicInfoDao().queryAll();
        mMusicControl = new MusicControl(this, mMusicList, this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        commandFilter.addAction(CANCEL_NOTIFICATION_ACTION);
        registerReceiver(mIntentReceiver, commandFilter);

        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        mServiceStartId = startId;
        Log.d("dingfeng", "onStartCommand:startId--" + startId);
        if (intent != null) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                Log.i("dingfeng", "onStartCommand next...");
                mMusicControl.next();
            } else if (CMDPREVIOUS.equals(cmd)) {
                mMusicControl.pre();
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (mMusicControl.isPlaying()) {
                    mMusicControl.pause();
                } else {
                    if (mMusicControl.getCurrentMusic() == null) {
                        mMusicControl.play(0);
                    } else {
                        mMusicControl.rePlay();
                    }
                }
            } else if (CMDPAUSE.equals(cmd)) {
                mMusicControl.pause();
            } else if (CMDPLAY.equals(cmd)) {
                mMusicControl.rePlay();
            } else if (CMDSTOP.equals(cmd)) {

            }
        }

        updateNotification();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mIntentReceiver);
        unregisterReceiver(headsetPlugReceiver);
        stopForeground(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void updateNotification() {
        LogUtil.d(null, "mediaplaybaceservice updateNotification...");
        final ComponentName serviceName = new ComponentName(this, MediaPlaybackService.class);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentIntent(pi)
                .setAutoCancel(false);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_NO_CLEAR;

        MusicInfo music = mMusicControl.getCurrentMusic();
        if (music != null) {
            remoteViews.setTextViewText(R.id.txtTitle, music.getMusicName());
        } else {
            List<MusicInfo> list = DBUtil.getInstance().getMusicInfoDao().queryAll();
            if (list != null && list.size() > 0) {
                music = list.get(0);
                remoteViews.setTextViewText(R.id.txtTitle, music.getMusicName());
            }
        }
        if (mMusicControl.isPlaying()) {
            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_notification_pause);
        } else {
            remoteViews.setImageViewResource(R.id.play, R.drawable.ic_notification_play);
        }

        intent = new Intent(MediaPlaybackService.TOGGLEPAUSE_ACTION);
        intent.setComponent(serviceName);
        PendingIntent pausePIntent = PendingIntent.getService(this, 0 /* no requestCode */, intent, 0 /* no flags */);
        remoteViews.setOnClickPendingIntent(R.id.play, pausePIntent);

        intent = new Intent(MediaPlaybackService.NEXT_ACTION);
        intent.setComponent(serviceName);
        PendingIntent nextPIntent = PendingIntent.getService(this, 0 /* no requestCode */, intent, 0 /* no flags */);
        remoteViews.setOnClickPendingIntent(R.id.next, nextPIntent);

        Intent cancelIntent = new Intent(CANCEL_NOTIFICATION_ACTION);
        PendingIntent cancelPIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.rlCancel, cancelPIntent);

        startForeground(NOTIFICATION_ID, notification);
    }

    public void cancelNotification() {
        stopForeground(true);
    }

    /*	private class MultiPlayer {
        private CompatMediaPlayer mCurrentMediaPlayer = new CompatMediaPlayer();
		private CompatMediaPlayer mNextMediaPlayer;
		private boolean mIsInitialized = false;

		public void setDataSource(String path) {
			mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
			if(mIsInitialized) {

			}
		}

		private boolean setDataSourceImpl(MediaPlayer mp, String path) {

			try {
				mp.reset();
				mp.setOnPreparedListener(null);
				if (path.startsWith("content://")) {
					mp.setDataSource(MediaPlaybackService.this, Uri.parse(path));
				} else {
					mp.setDataSource(path);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				return false;
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				return false;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
//			mp.setOnCompletionListener(listener);

			return true;
		}



		MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
					mCurrentMediaPlayer.release();
					mCurrentMediaPlayer = mNextMediaPlayer;
					mNextMediaPlayer = null;
				}else {

				}
			}

		};

	}


	static class CompatMediaPlayer extends MediaPlayer implements OnCompletionListener {
		private boolean mCompatMode = true;
		private MediaPlayer mNextPlayer;
		private OnCompletionListener mCompletion;

		public CompatMediaPlayer() {
			try {
				MediaPlayer.class.getMethod("setNextMediaPlayer", MediaPlayer.class);
				mCompatMode = false;
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				mCompatMode = true;
				super.setOnCompletionListener(this);
			}
		}

		public void setNextMediaPlayer(MediaPlayer next) {
			if(mCompatMode) {
				mNextPlayer = next;
			}else {
//				super.setNextMediaPlayer(next);
			}
		}

		@Override
		public void setOnCompletionListener(OnCompletionListener listener) {
			// TODO Auto-generated method stub
			if(mCompatMode) {
				mCompletion = listener;
			}else {
				super.setOnCompletionListener(listener);
			}
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			if(mNextPlayer != null) {
				SystemClock.sleep(50);
				mNextPlayer.start();
			}
			mCompletion.onCompletion(this);
		}

	}*/

    private final IBinder mBinder = new ServiceStub();

    class ServiceStub extends IMediaPlaybackService.Stub {

        @Override
        public void play(int pos) throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.play(pos);
        }

        @Override
        public void playById(int id) throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.playById(id);
        }

        @Override
        public void rePlay() throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.rePlay();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            // TODO Auto-generated method stub
            return mMusicControl.isPlaying();
        }

        @Override
        public void pause() throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.pause();
        }

        @Override
        public void stop() throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.stop();
        }

        @Override
        public void pre() throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.pre();
        }

        @Override
        public void next() throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.next();
        }

        @Override
        public long duration() throws RemoteException {
            // TODO Auto-generated method stub
            return mMusicControl.getDuration();
        }

        @Override
        public long position() throws RemoteException {
            // TODO Auto-generated method stub
            return mMusicControl.getCurrentPosition();
        }

        @Override
        public long seek(long pos) throws RemoteException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void refreshPlayList(List<MusicInfo> list)
                throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.refreshPlayList(list);
        }

        @Override
        public List<MusicInfo> getPlayList() throws RemoteException {
            return mMusicControl.getPlayList();
        }

        @Override
        public void removeMusic(MusicInfo music) throws RemoteException {
            mMusicControl.removeMusicFromPlaylist(music);
        }

        @Override
        public MusicInfo getCurrentMusic() throws RemoteException {
            // TODO Auto-generated method stub
            return mMusicControl.getCurrentMusic();
        }

        @Override
        public void seekTo(int pos) throws RemoteException {
            // TODO Auto-generated method stub
            mMusicControl.seekTo(pos);
        }

        @Override
        public String getTrackName() throws RemoteException {
            return mMusicControl.getTrackName();
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return null;
        }

        @Override
        public void exit() throws RemoteException {
            mMusicControl.stop();
            stopSelf(mServiceStartId);
        }

        @Override
        public int getCurrentPos() throws RemoteException {
            return mMusicControl.getPosition();
        }
    }

}
