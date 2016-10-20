package com.music.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.AppInfo;
import com.music.IMediaPlaybackService;
import com.music.R;
import com.music.adapter.NowListAdapter;
import com.music.base.IConstants;
import com.music.entity.AlbumInfo;
import com.music.entity.MusicInfo;
import com.music.fragment.HomeFragment;
import com.music.service.MusicControl;
import com.music.service.MusicUtil;
import com.music.setting.ScanTask;
import com.music.setting.SensorListenerUtil;
import com.music.setting.Setting;
import com.music.sqlite.DBUtil;
import com.music.util.ImageLoaderUtil;
import com.music.util.SharedPreferencesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    DrawerLayout drawerLayout;
    View bottom;
    ImageView ivHead;
    TextView txtName;
    TextView txtArtist;
    ImageButton btPlay;
    ImageButton playList;
    RelativeLayout rlPlayList;
    ProgressBar progressBar;

    private HomeFragment mHomeFragment;
    private MusicUtil.ServiceToken mToken;
    private IMediaPlaybackService mService = null;

    //上次按下返回键的系统时间
    private long mLastBackTime = 0;
    //当前按下返回键的系统时间
    private long mCurBackTime = 0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(IConstants.BROADCAST_THEMECHANGE)) {
                setTheme();
            }
        }
    };

    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicControl.PLAYSTATE_CHANGED)) {
                mHandler.removeMessages(REFRESH_PROGRESS);
                mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 500);
                updateNowPlaying();
                setPauseButtonImage();
            } else if (action.equals(MusicControl.META_CHANGED)) {
                updateNowPlaying();
                setPauseButtonImage();
            }
        }
    };

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    refereshProgress();
                    mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 500);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");//remove toolbar title
        setSupportActionBar(toolbar);
        initView();
        // bind service
        mToken = MusicUtil.bindToService(this, serviceConnection);
        // create directory
        createDir();
        // add home fragment
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content, mHomeFragment).commitAllowingStateLoss();
        }

        if (SharedPreferencesUtil.getInstance().getShare("IsFirst", true)) {
            firstScan();
        }

        // register shake listener
        registerShakeEvent();

        IntentFilter filter = new IntentFilter();
        filter.addAction(IConstants.BROADCAST_THEMECHANGE);
        registerReceiver(receiver, filter);

        IntentFilter f = new IntentFilter();
        f.addAction(MusicControl.PLAYSTATE_CHANGED);
        f.addAction(MusicControl.META_CHANGED);
        registerReceiver(mStatusListener, f);

    }

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        bottom = findViewById(R.id.bottom);
        bottom.setOnClickListener(this);
        ivHead = (ImageView) findViewById(R.id.ivHead);
        txtName = (TextView) findViewById(R.id.txtName);
        txtArtist = (TextView) findViewById(R.id.txtArtist);
        btPlay = (ImageButton) findViewById(R.id.btPlay);
        btPlay.setOnClickListener(this);
        playList = (ImageButton) findViewById(R.id.playList);
        playList.setOnClickListener(this);
        rlPlayList = (RelativeLayout) findViewById(R.id.rlPlayList);
        rlPlayList.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setTheme();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMediaPlaybackService.Stub.asInterface(service);
            setPauseButtonImage();
            try {
                updateNowPlaying();
                if (mService.isPlaying()) {
                    mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 500);
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void firstScan() {
        new ScanTask(this, new ScanTask.Callback() {
            @Override
            public void start() {
                showLoadingProgressDialog();
            }

            @Override
            public void scan() {

            }

            @Override
            public void end() {
                closeLoadingProgressDialog();
                mHomeFragment.getCount();
                try {
                    List<MusicInfo> list = DBUtil.getInstance().getMusicInfoDao().queryAll();
                    mService.refreshPlayList(list);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                updateNowPlaying();
                SharedPreferencesUtil.getInstance().putShare("IsFirst", false);
            }
        }).execute();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    //register shake event
    private void registerShakeEvent() {
        SensorListenerUtil.getInstance(this).register(new SensorListenerUtil.ICallBack() {
            @Override
            public void onShake() {
                if (Setting.getInstance().mShakeChangeSong) {
                    next();
                }
            }

            @Override
            public void onProxityChange() {
                if (Setting.getInstance().mFlickChangeSong) {
                    next();
                }
            }
        });
    }

    private void next() {
        if (MusicUtil.sService != null) {
            try {
                mService.next();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeMessages(REFRESH_PROGRESS);
        unregisterReceiver(receiver);
        unregisterReceiver(mStatusListener);
        // unregister shake listener
        SensorListenerUtil.getInstance(this).unregister();
        MusicUtil.unbindFromService(mToken);
        mService = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.bottom:
                try {
                    MusicInfo musicinfo = MusicUtil.sService.getCurrentMusic();
                    if (musicinfo == null) {
                        musicinfo = DBUtil.getInstance().getMusicInfoDao().queryFirst();
                    }
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MediaPlaybackActivity.class);
                    intent.putExtra("current", musicinfo);
                    startActivity(intent);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.btPlay:
                doPauseResume();
                break;
            case R.id.playList:
            case R.id.rlPlayList:
                showNowPlayingList(view);
                break;
            default:
                break;
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
                btPlay.setImageResource(R.drawable.ic_pause);
            } else {
                btPlay.setImageResource(R.drawable.ic_play);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateNowPlaying() {
        try {
            MusicInfo musicinfo = mService.getCurrentMusic();
            if (musicinfo != null) {
                setCurrentInfo(musicinfo);
                refereshProgress();
            } else {
                setDefaultMusic();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    * default music:the first music in locale music list
    * */
    private void setDefaultMusic() {
        List<MusicInfo> list = DBUtil.getInstance().getMusicInfoDao().queryAll();
        MusicInfo musicinfo;
        if (list != null && list.size() > 0) {
            musicinfo = list.get(0);
        } else {
            return;
        }
        setCurrentInfo(musicinfo);
    }

    private void setCurrentInfo(MusicInfo music) {
        txtName.setText(music.getMusicName());
        txtArtist.setText(music.getArtist());
        int albumId = music.getAlbumId();
        AlbumInfo albuminfo = DBUtil.getInstance().getAlbumInfoDao().queryEntity("albumid", albumId);
        if (albuminfo != null) {
            ImageLoader.getInstance().displayImage(albuminfo.getAlbumCover(), ivHead, ImageLoaderUtil.getAlbumCoverOption());
        }
    }

    private void refereshProgress() {
        if (mService == null) return;
        try {
            long duration = mService.duration();
            long position = mService.position();
            int rate = 0;
            if (duration != 0) {
                rate = (int) ((float) position / duration * 100);
            }
            progressBar.setProgress(rate);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setTheme() {
        String path = SharedPreferencesUtil.getInstance().getShare(IConstants.SELECTED_THEME, null);
        Bitmap bitmap = getBitmapFromAssets(path);
        if (bitmap != null) {
            drawerLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    private Bitmap getBitmapFromAssets(String path) {
        AssetManager am = getAssets();
        Bitmap bitmap = null;
        try {
            InputStream is = am.open("theme/" + path);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void createDir() {
        File file = new File(IConstants.LYRICPATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public void openDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        mCurBackTime = System.currentTimeMillis();
        if (HomeFragment.atHome) {
            if (mCurBackTime - mLastBackTime > 2000) {
                Snackbar.make(txtName, R.string.prompt_exit, Snackbar.LENGTH_SHORT).show();
                mLastBackTime = mCurBackTime;
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showNowPlayingList(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.now_list, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, AppInfo.screenHeight / 2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - popupWindow.getWidth(), location[1] - popupWindow.getHeight());

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

}
