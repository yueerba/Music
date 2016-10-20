package com.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.music.activity.MainActivity;
import com.music.service.MediaPlaybackService;
import com.music.setting.Setting;

/**
 * Created by dingfeng on 2016/4/26.
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private static final int MSG_LONGPRESS_TIMEOUT = 1;
    private static final int LONG_PRESS_DELAY = 1000;

    private static long mLastClickTime = 0;
    private static boolean mDown = false;
    private static boolean mLaunched = false;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LONGPRESS_TIMEOUT:
                    if (!mLaunched) {
                        Context context = (Context) msg.obj;
                        Intent i = new Intent();
//					i.putExtra("autoshuffle", "true");
                        i.setClass(context, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                        mLaunched = true;
                    }
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String intentAction = intent.getAction();
        Log.d("dingfeng", "onreceive  intentAction:"+intentAction);
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
            //pause
            Intent i = new Intent(context, MediaPlaybackService.class);
            i.setAction(MediaPlaybackService.SERVICECMD);
            i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDPAUSE);
            context.startService(i);

        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.d("dingfeng", "onreceive  key code:"+event.getKeyCode());
            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            // single quick press: pause/resume.
            // double press: next track
            // long press: start auto-shuffle mode.

            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = MediaPlaybackService.CMDSTOP;
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    command = MediaPlaybackService.CMDTOGGLEPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = MediaPlaybackService.CMDNEXT;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = MediaPlaybackService.CMDPREVIOUS;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = MediaPlaybackService.CMDPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    command = MediaPlaybackService.CMDPLAY;
                    break;
            }

            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (mDown) {
                        if ((MediaPlaybackService.CMDTOGGLEPAUSE.equals(command)
                                || MediaPlaybackService.CMDPLAY.equals(command))
                                && mLastClickTime != 0
                                && eventtime - mLastClickTime > LONG_PRESS_DELAY) {

                            mHandler.sendMessage(mHandler.obtainMessage(MSG_LONGPRESS_TIMEOUT, context));
                        }

                    } else if (event.getRepeatCount() == 0) {
                        // only consider the first event in a sequence, not the repeat events,
                        // so that we don't trigger in cases where the first event went to
                        // a different app (e.g. when the user ends a phone call by
                        // long pressing the headset button)

                        // The service may or may not be running, but we need to send it
                        // a command.
                        Intent i = new Intent(context, MediaPlaybackService.class);
                        i.setAction(MediaPlaybackService.SERVICECMD);
                        //If we press twice continuously, change to next song.
                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK && eventtime - mLastClickTime < 300 && Setting.getInstance().mDoublePress) {
                            i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDNEXT);
                            context.startService(i);
                            mLastClickTime = 0;

                        } else {
                            if (Setting.getInstance().mHookkeyControl) {
                                i.putExtra(MediaPlaybackService.CMDNAME, command);
                                context.startService(i);
                                mLastClickTime = eventtime;
                            }
//                            i.putExtra(MediaPlaybackService.CMDNAME, command);
//                            context.startService(i);
//                            mLastClickTime = eventtime;

                        }

                        mLaunched = false;
                        mDown = true;
                    }
                } else {
                    mHandler.removeMessages(MSG_LONGPRESS_TIMEOUT);
                    mDown = false;
                }
                if (isOrderedBroadcast()) {
                    abortBroadcast();
                }
            }
        }

    }

}

