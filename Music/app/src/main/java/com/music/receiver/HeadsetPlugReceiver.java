package com.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.music.service.MediaPlaybackService;
import com.music.service.MusicUtil;
import com.music.setting.Setting;

/**
 * Created by dingfeng on 2016/4/26.
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    private boolean mIsHeadSetPlugged;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:// unplug headset
                    mIsHeadSetPlugged = false;
                    pause(context);
                    break;
                case 1:// plug headset
                    mIsHeadSetPlugged = true;
                    if (Setting.getInstance().mHeadSetPlug) {
                        play(context);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private void play(Context context) {
        //play
        Intent i = new Intent(context, MediaPlaybackService.class);
        i.setAction(MediaPlaybackService.SERVICECMD);
        i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDPLAY);
        context.startService(i);
    }

    private void pause(Context context) {
        //pause
        Intent i = new Intent(context, MediaPlaybackService.class);
        i.setAction(MediaPlaybackService.SERVICECMD);
        i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDPAUSE);
        context.startService(i);
    }
}
