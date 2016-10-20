package com.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.music.R;
import com.music.activity.MainActivity;
import com.music.adapter.HomeGridAdapter;
import com.music.entity.AlbumInfo;
import com.music.entity.ArtistInfo;
import com.music.entity.FolderInfo;
import com.music.entity.MusicInfo;
import com.music.sqlite.DBUtil;
import com.music.util.LogUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/12.
 */
public class HomeFragment extends FragmentBase implements AdapterView.OnItemClickListener {

    ImageView btSlide;
    private GridView mGridView;
    private HomeGridAdapter mAdapter;

    private String[] nums = {"0", "0", "0", "0", "0",};

    private MusicListFragment mMusicListFragment;
    private MyFavoriteFragment mFavoriteFragment;
    private FolderListFragment mFolderListFragment;
    private AlbumListFragment mAlbumListFragment;
    private ArtistFragment mArtistFragment;

    public static boolean atHome = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetInvalidated();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        btSlide = (ImageView) view.findViewById(R.id.btSlide);
        btSlide.setOnClickListener(slideOnClickListener);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mAdapter = new HomeGridAdapter(getActivity(), getResources().getStringArray(R.array.home_options_icon), getResources().getStringArray(R.array.home_options), nums);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        return view;
    }

    View.OnClickListener slideOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).openDrawer();
        }
    };

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getCount();
        atHome = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        atHome = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getCount();
            atHome = true;
        } else {
            atHome = false;
        }
    }

    public void getCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musiclist = DBUtil.getInstance().getMusicInfoDao().queryAll();
                List<MusicInfo> favoritelist = DBUtil.getInstance().getMusicInfoDao().queryList("isFavorite", true);
                List<FolderInfo> folderlist = DBUtil.getInstance().getFolderInfoDao().queryAll();
                List<AlbumInfo> albumlist = DBUtil.getInstance().getAlbumInfoDao().queryAll();
                List<ArtistInfo> artistlist = DBUtil.getInstance().getArtistInfoDao().queryAll();
                if (musiclist != null) {
                    nums[0] = musiclist.size() + "";
                }
                if (favoritelist != null) {
                    nums[1] = favoritelist.size() + "";
                }
                if (folderlist != null) {
                    nums[2] = folderlist.size() + "";
                }
                if (artistlist != null) {
                    nums[3] = artistlist.size() + "";
                }
                if (albumlist != null) {
                    nums[4] = albumlist.size() + "";
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (position == 0) {
            if (mMusicListFragment == null) {
                mMusicListFragment = new MusicListFragment();
            }
            showFragment(mMusicListFragment);
        }
        if (position == 1) {
            if (mFavoriteFragment == null) {
                mFavoriteFragment = new MyFavoriteFragment();
            }
            showFragment(mFavoriteFragment);
        }
        if (position == 2) {
            if (mFolderListFragment == null) {
                mFolderListFragment = new FolderListFragment();
            }
            showFragment(mFolderListFragment);
        }
        if (position == 3) {
            if (mArtistFragment == null) {
                mArtistFragment = new ArtistFragment();
            }
            showFragment(mArtistFragment);
        }

        if (position == 4) {
            if (mAlbumListFragment == null) {
                mAlbumListFragment = new AlbumListFragment();
            }
            showFragment(mAlbumListFragment);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_from_left, R.anim.in_from_left, R.anim.out_from_right);
        transaction.add(R.id.content, fragment);
        transaction.hide(this);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

}
