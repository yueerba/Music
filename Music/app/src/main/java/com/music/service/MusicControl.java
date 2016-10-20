package com.music.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.music.base.IConstants;
import com.music.entity.MusicInfo;
import com.music.setting.Setting;
import com.music.util.LogUtil;
import com.music.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by dingfeng on 2016/4/13.
 */
public class MusicControl implements MediaPlayer.OnCompletionListener {

    //
    private static final int NOFILE = -1; //
    private static final int INVALID = 0; //
    private static final int PREPARED = 1; //
    private static final int PLAYING = 2; //
    private static final int PAUSE = 3; //

    public static final String PLAYSTATE_CHANGED = "com.music.playstatechanged";
    public static final String META_CHANGED = "com.music.metachanged";

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private int mCurPlayIndex;
    private int mCurMusicId;
    private int mPlayState = INVALID;

    private boolean mIsPlaying = false;
    private boolean mIsInitialized = false;

    private int mPlayMode;
    private Random mRandom;

    private List<MusicInfo> mMusicList;
    MediaPlaybackService mediaPlaybackService;

    public MusicControl(Context context, List<MusicInfo> list, MediaPlaybackService service) {
        mContext = context;
        mMusicList = list;
        mediaPlaybackService = service;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mCurPlayIndex = -1;
        mCurMusicId = -1;
        mPlayState = NOFILE;

        mPlayMode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        mRandom = new Random();
        mRandom.setSeed(System.currentTimeMillis());
    }

    public void play(int pos) {
        if (mCurPlayIndex == pos) {
            if (mMediaPlayer.isPlaying()) {
                pause();
            } else {
                mMediaPlayer.start();
                mPlayState = PLAYING;
                notifyChange(PLAYSTATE_CHANGED);
            }
            return;
        }

        if (prepare(pos)) {
            mMediaPlayer.start();
            mPlayState = PLAYING;
            notifyChange(PLAYSTATE_CHANGED);
        } else {
            LogUtil.i("dingfeng", "prepare fail");
        }
    }

    public void playById(int id) {
        int position = MusicUtil.seekPosInListById(mMusicList, id);
        if (mCurPlayIndex == position) {
            if (mMediaPlayer.isPlaying()) {
                pause();
            } else {
                mMediaPlayer.start();
                mPlayState = PLAYING;
                notifyChange(PLAYSTATE_CHANGED);
            }
            return;
        }
        if (prepare(position)) {
            mMediaPlayer.start();
            mPlayState = PLAYING;
            notifyChange(PLAYSTATE_CHANGED);
        } else {
            LogUtil.i("dingfeng", "prepare fail");
        }
    }

    public void rePlay() {
        if (mIsInitialized) {
            mMediaPlayer.start();
            mPlayState = PLAYING;
            notifyChange(PLAYSTATE_CHANGED);
        } else {
            play(0);
        }
    }

    public void pause() {
        if (mPlayState != PLAYING) return;
        mMediaPlayer.pause();
        mPlayState = PAUSE;
        notifyChange(PLAYSTATE_CHANGED);
    }

    public void pre() {
        mCurPlayIndex--;
        mCurPlayIndex = reviseIndex(mCurPlayIndex);
        if (prepare(mCurPlayIndex)) {
            rePlay();
            notifyChange(META_CHANGED);
        } else {
            LogUtil.i("dingfeng", "prepare fail");
        }
    }

    public void next() {
        mCurPlayIndex = getNextPosition();
        if (mCurPlayIndex == -1) {//stop play
            mCurPlayIndex = 0;
            prepare(mCurPlayIndex);
        } else {
            if (prepare(mCurPlayIndex)) {
                rePlay();
                notifyChange(META_CHANGED);
            } else {
                LogUtil.i("dingfeng", "prepare fail");
            }
        }
    }

    public int getNextPosition() {
        int pos = mCurPlayIndex;
        mPlayMode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        if (mPlayMode == IConstants.ORDER_PLAY) {
            if (pos != (mMusicList.size() - 1)) {
                pos++;
            } else {
                return -1;
            }
        } else if (mPlayMode == IConstants.RANDOM_PLAY) {
            int index = getRandomIndex();
            if (index != -1) {
                pos = index;
            } else {
                pos = 0;
            }
        } else if (mPlayMode == IConstants.LIST_LOOP_PLAY) {
            if (pos == (mMusicList.size() - 1)) {
                pos = 0;
            } else {
                pos++;
            }
        } else if (mPlayMode == IConstants.SINGLE_LOOP_PLAY) {
            return pos;
        }
        pos = reviseIndex(pos);
        return pos;
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public boolean prepare(int pos) {
        mCurPlayIndex = pos;
        mMediaPlayer.reset();
        mMediaPlayer.setOnCompletionListener(this);
        String path = mMusicList.get(pos).getData();
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
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
        mIsInitialized = true;
        return true;
    }

    public void seekTo(int progress) {
        int pro = reviseSeekValue(progress);
        long duration = getDuration();
        int current = (int) ((float) pro / 100 * duration);
        mMediaPlayer.seekTo(current);
    }

    private int reviseSeekValue(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        return progress;
    }

    private int reviseIndex(int index) {
        if (index < 0) {
            index = mMusicList.size() - 1;
        }
        if (index >= mMusicList.size()) {
            index = 0;
        }
        return index;
    }

    public boolean isPlaying() {
        return mIsPlaying = (mPlayState == PLAYING);
    }

    public void refreshPlayList(List<MusicInfo> list) {
        if (mMusicList == null) {
            mMusicList = list;
        } else {
            mMusicList.clear();
            mMusicList.addAll(list);
        }
        mCurPlayIndex = -1;
    }

    public List<MusicInfo> getPlayList() {
        return mMusicList;
    }

    public void removeMusicFromPlaylist(MusicInfo music) {
        if (mMusicList != null && mMusicList.contains(music)) {
            mMusicList.remove(music);
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        // TODO Auto-generated method stub
//        Intent intent = new Intent(IConstants.PLAY_COMPLETED);
//        mContext.sendBroadcast(intent);

        mPlayMode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        Log.d("dingfeng", "playmode:" + mPlayMode + "  mCurPlayIndex:" + mCurPlayIndex);
        switch (mPlayMode) {
            case IConstants.ORDER_PLAY:
                if (mCurPlayIndex != (mMusicList.size() - 1)) {
                    next();
                } else {
                    prepare(mCurPlayIndex);
                }
                break;
            case IConstants.LIST_LOOP_PLAY:
                next();
                break;
            case IConstants.RANDOM_PLAY:
                int index = getRandomIndex();
                if (index != -1) {
                    mCurPlayIndex = index;
                } else {
                    mCurPlayIndex = 0;
                }
                if (prepare(mCurPlayIndex)) {
                    rePlay();
                }
                break;
            case IConstants.SINGLE_LOOP_PLAY:
                play(mCurPlayIndex);
                break;
            default:
                break;
        }
    }

    private int getRandomIndex() {
        int size = mMusicList.size();
        if (size == 0) {
            return -1;
        }
        return Math.abs(mRandom.nextInt() % size);
    }

    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public MusicInfo getCurrentMusic() {
        if (mCurPlayIndex != -1) {
            return mMusicList.get(mCurPlayIndex);
        }
        return null;
    }

    public String getTrackName() {
        if (mCurPlayIndex != -1) {
            MusicInfo musicInfo = mMusicList.get(mCurPlayIndex);
            if (musicInfo != null) return musicInfo.getMusicName();
        }
        return "";
    }

    public String getAlbumName() {
        return "";
    }

    public int getPosition() {
        if (mCurPlayIndex < 0) {
            return 0;
        }
        return mCurPlayIndex;
    }

    private void notifyChange(String what) {
        Intent intent = new Intent(what);
        mContext.sendBroadcast(intent);
        if (Setting.getInstance().mNotificationDisplay) {
//            MediaNotificationManager.getInstance(mContext).updateNotification();
            mediaPlaybackService.updateNotification();
        }
    }

}

