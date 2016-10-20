package com.music.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.music.AppInfo;
import com.music.IMediaPlaybackService;
import com.music.R;
import com.music.adapter.NowListAdapter;
import com.music.base.IConstants;
import com.music.entity.MusicInfo;
import com.music.fragment.LyricsFragment;
import com.music.fragment.RhythmFragment;
import com.music.service.MusicControl;
import com.music.service.MusicUtil;
import com.music.util.SharedPreferencesUtil;
import com.music.view.indicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingfeng on 2016/4/15.
 */
public class MediaPlaybackActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MediaPlaybackActivity.class.getSimpleName();
    private static final int REFRESH_PROGRESS = 0;

    ImageView iv_back;
    TextView tv_title;
    ViewPager mViewpager;
    CirclePageIndicator indicator;
    SeekBar mSeekBar;
    ImageView iv_playback_pre;
    ImageView iv_playback_pause;
    ImageView iv_playback_next;
    ImageView playmode;

    ImageView nowlist;
    private MyAdapter mPagerAdapter;

    private List<Fragment> mFragmentList;
    LyricsFragment lyricsFragment;
    RhythmFragment rhythmFragment;

    private int mProgress;

    private MusicInfo mCurrent;
    private MusicUtil.ServiceToken mToken;

    private IMediaPlaybackService mService = null;

    BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicControl.PLAYSTATE_CHANGED)) {
                updateCurrentMusic();
                setPauseButtonImage();
            } else if (action.equals(MusicControl.META_CHANGED)) {
                updateCurrentMusic();
                setPauseButtonImage();
                lyricsFragment.loadLyric(mCurrent);
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    refreshSeekBar();
                    mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 500);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        Intent intent = getIntent();
        mCurrent = intent.getParcelableExtra("current");

        mFragmentList = new ArrayList<>();
        lyricsFragment = new LyricsFragment();
        rhythmFragment = new RhythmFragment();
        mFragmentList.add(lyricsFragment);
        mFragmentList.add(rhythmFragment);

        initView();
        // bind service
        mToken = MusicUtil.bindToService(this, serviceConnection);

        IntentFilter f = new IntentFilter();
        f.addAction(MusicControl.PLAYSTATE_CHANGED);
        f.addAction(MusicControl.META_CHANGED);
        registerReceiver(mStatusListener, f);
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(backClickListener);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(mCurrent.getMusicName());
        iv_playback_pre = (ImageView) findViewById(R.id.iv_playback_pre);
        iv_playback_pre.setOnClickListener(this);
        iv_playback_pause = (ImageView) findViewById(R.id.iv_playback_pause);
        iv_playback_pause.setOnClickListener(this);
        iv_playback_next = (ImageView) findViewById(R.id.iv_playback_next);
        iv_playback_next.setOnClickListener(this);
        playmode = (ImageView) findViewById(R.id.playmode);
        playmode.setOnClickListener(this);
        updatePlayModeState();
        nowlist = (ImageView) findViewById(R.id.nowlist);
        nowlist.setOnClickListener(this);
        mSeekBar = (SeekBar) findViewById(R.id.sk_progress);
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new MyAdapter(getSupportFragmentManager(), mFragmentList);
        mViewpager.setAdapter(mPagerAdapter);

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewpager);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMediaPlaybackService.Stub.asInterface(service);
            setPauseButtonImage();
            updateSeekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.iv_playback_pre:
                pre();
                break;
            case R.id.iv_playback_pause:
                doPauseResume();
                break;
            case R.id.iv_playback_next:
                next();
                break;
            case R.id.playmode:
                setPlayMode();
                break;
            case R.id.nowlist:
                showNowPlayingList(view);
                break;
            default:
                break;
        }
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (seekbar == mSeekBar) {
                mProgress = progress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekbar) {
            // TODO Auto-generated method stub
            if (seekbar == mSeekBar) {
                try {
                    mService.pause();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekbar) {
            // TODO Auto-generated method stub
            if (seekbar == mSeekBar) {
                try {
                    mService.seekTo(mProgress);
                    mService.rePlay();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mStatusListener);
        mHandler.removeMessages(REFRESH_PROGRESS);
        MusicUtil.unbindFromService(mToken);
        mService = null;
    }

    private void updateSeekBar() {
        refreshSeekBar();
        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 500);
    }

    private void updateCurrentMusic() {
        try {
            mCurrent = mService.getCurrentMusic();
            if (mCurrent != null) {
                tv_title.setText(mCurrent.getMusicName());
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void pre() {
        if (mService != null) {
            try {
                mService.pre();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void next() {
        if (mService != null) {
            try {
                mService.next();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void refreshSeekBar() {
        try {
            if (mService != null && mService.isPlaying()) {
                long duration = mService.duration();
                long position = mService.position();
                int rate = 0;
                if (duration != 0) {
                    rate = (int) ((float) position / duration * 100);
                }
                mSeekBar.setProgress(rate);

                lyricsFragment.slideLyricLine(position);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void doPauseResume() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.rePlay();
                }
                setPauseButtonImage();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setPauseButtonImage() {
        try {
            if (mService != null && mService.isPlaying()) {
                iv_playback_pause.setImageResource(R.drawable.img_playback_pause);
            } else {
                iv_playback_pause.setImageResource(R.drawable.img_playback_play);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updatePlayModeState() {
        int mode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        Drawable drawable;
        switch (mode) {
            case IConstants.ORDER_PLAY:
                drawable = getResources().getDrawable(R.drawable.media_playmode_normal);
                playmode.setImageDrawable(drawable);
                break;
            case IConstants.LIST_LOOP_PLAY:
                drawable = getResources().getDrawable(R.drawable.media_playmode_repeat_all);
                playmode.setImageDrawable(drawable);
                break;
            case IConstants.RANDOM_PLAY:
                drawable = getResources().getDrawable(R.drawable.media_playmode_shuffle);
                playmode.setImageDrawable(drawable);
                break;
            case IConstants.SINGLE_LOOP_PLAY:
                drawable = getResources().getDrawable(R.drawable.media_playmode_single_repeat);
                playmode.setImageDrawable(drawable);
                break;
            default:
                break;
        }
    }

    private void showNowPlayingList(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.now_list, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, AppInfo.screenWidth * 2 / 3, AppInfo.screenHeight * 2 / 3, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.popwin_anim_style2);
        int[] location = new int[2];
        iv_playback_pause.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight());

        ListView listView = (ListView) contentView.findViewById(R.id.listview);
        try {
            List<MusicInfo> nowlist = mService.getPlayList();
            NowListAdapter adapter = new NowListAdapter(this, nowlist);
            listView.setAdapter(adapter);
            listView.setSelection(mService.getCurrentPos());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        mService.play(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setPlayMode() {
        int mode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        if (mode == IConstants.SINGLE_LOOP_PLAY) {
            mode = IConstants.ORDER_PLAY;
        } else {
            mode++;
        }
        SharedPreferencesUtil.getInstance().putShare("play_mode", mode);
        updatePlayModeState();
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentlist;

        public MyAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragmentlist = list;
        }

        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            return fragmentlist.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragmentlist.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            super.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            return super.instantiateItem(container, position);
        }

    }

}

