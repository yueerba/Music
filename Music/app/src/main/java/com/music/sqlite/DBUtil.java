package com.music.sqlite;

import android.content.Context;

import com.music.MusicApplication;
import com.music.entity.AlbumInfo;
import com.music.entity.ArtistInfo;
import com.music.entity.FolderInfo;
import com.music.entity.MusicInfo;

/**
 * Created by dingfeng on 2016/4/12.
 */
public class DBUtil {
    private Context mContext;
    private BaseDao<MusicInfo, Integer> mMusicInfoDao;
    private BaseDao<AlbumInfo, Integer> mAlbumInfoDao;
    private BaseDao<FolderInfo, Integer> mFolderInfoDao;
    private BaseDao<ArtistInfo, Integer> mArtistInfoDao;

    private static DBUtil dbUtil = null;

    public static DBUtil getInstance() {
        if (dbUtil == null) {
            dbUtil = new DBUtil(MusicApplication.getInstance());
        }
        return dbUtil;
    }

    public DBUtil(Context context) {
        mContext = context;
    }

    public BaseDao<MusicInfo, Integer> getMusicInfoDao() {
        if (mMusicInfoDao == null) {
            mMusicInfoDao = new BaseDaoImpl<MusicInfo, Integer>(mContext, MusicInfo.class);
        }
        return mMusicInfoDao;
    }

    public BaseDao<AlbumInfo, Integer> getAlbumInfoDao() {
        if (mAlbumInfoDao == null) {
            mAlbumInfoDao = new BaseDaoImpl<AlbumInfo, Integer>(mContext, AlbumInfo.class);
        }
        return mAlbumInfoDao;
    }

    public BaseDao<FolderInfo, Integer> getFolderInfoDao() {
        if (mFolderInfoDao == null) {
            mFolderInfoDao = new BaseDaoImpl<FolderInfo, Integer>(mContext, FolderInfo.class);
        }
        return mFolderInfoDao;
    }

    public BaseDao<ArtistInfo, Integer> getArtistInfoDao() {
        if (mArtistInfoDao == null) {
            mArtistInfoDao = new BaseDaoImpl<ArtistInfo, Integer>(mContext, ArtistInfo.class);
        }
        return mArtistInfoDao;
    }

}
