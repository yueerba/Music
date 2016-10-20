package com.music.setting;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.music.entity.AlbumInfo;
import com.music.entity.ArtistInfo;
import com.music.entity.FolderInfo;
import com.music.entity.MusicInfo;
import com.music.sqlite.DBUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingfeng on 2016/6/12.
 */
public class ScanTask extends AsyncTask {

    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private static String[] MUSIC_SCAN = {MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.SIZE,};

    private static String[] ALBUM_SCAN = {MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ALBUM_ART};

    private static String[] FOLDER_SCAN = {MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA};

    private static String[] ARTIST_SCAN = {MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.ARTIST_KEY,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
    };

    Context mContext;
    Callback callback;

    public ScanTask(Context context, Callback callback) {
        super();
        this.mContext = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // before scan
        callback.start();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        // scanning
        doScan();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        // after scan
        callback.end();
    }

    private void doScan() {
        queryMusic(mContext, null, null);
        queryAlbum(mContext, null, null);
        queryFolder(mContext, null, null);
        queryArtist(mContext, null, null);
    }

    //query music
    private List<MusicInfo> queryMusic(Context context, String selections, String selection) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        StringBuffer select = new StringBuffer(" 1=1 ");
        if (Setting.getInstance().mScanSizeLimit) {
            select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        }
        if (Setting.getInstance().mScanTimeLimit) {
            select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        }

        if (!TextUtils.isEmpty(selections)) {
            select.append(selections);
        }
        Cursor cursor = cr.query(uri, MUSIC_SCAN, /*selections*/select.toString(), null, MediaStore.Audio.Media.ARTIST_KEY);
        List<MusicInfo> list = getMusicList(cursor);
        // favorite music
        List<MusicInfo> favorites = DBUtil.getInstance().getMusicInfoDao().queryList("isFavorite", true);
        if (list != null && favorites != null) {
            for (int i = 0; i < favorites.size(); i++) {
                MusicInfo music = favorites.get(i);
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).songId == music.songId) {
                        list.get(j).setIsFavorite(true);
                    }
                }
            }
        }
        DBUtil.getInstance().getMusicInfoDao().deleteAll();
        DBUtil.getInstance().getMusicInfoDao().saveList(list);
        return list;
    }

    private ArrayList<MusicInfo> getMusicList(Cursor cursor) {
        if (cursor == null) return null;
        ArrayList<MusicInfo> musicList = new ArrayList<MusicInfo>();
        while (cursor.moveToNext()) {
            MusicInfo music = new MusicInfo();
            music.setSongId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            music.setAlbumId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            music.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            music.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
            music.setMusicName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            music.setArtistId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
            music.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            music.setIsMusic(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) == 1);
            //Only add music file
            if (music.isMusic()) {
                musicList.add(music);
            }
        }
        cursor.close();
        return musicList;
    }

    //query album
    private List<AlbumInfo> queryAlbum(Context context, String selections, String selection) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        StringBuilder where = new StringBuilder(MediaStore.Audio.Albums._ID
                + " in (select distinct " + MediaStore.Audio.Media.ALBUM_ID
                + " from audio_meta where (1=1 ");

        if (Setting.getInstance().mScanSizeLimit) {
            where.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        }
        if (Setting.getInstance().mScanTimeLimit) {
            where.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        }
        where.append("))");

        Cursor cursor = cr.query(uri, ALBUM_SCAN, /*selections*/where.toString(), null, MediaStore.Audio.Media.ALBUM_KEY);
        List<AlbumInfo> list = getAlbumList(cursor);
        DBUtil.getInstance().getAlbumInfoDao().deleteAll();
        DBUtil.getInstance().getAlbumInfoDao().saveList(list);
        return list;
    }

    private ArrayList<AlbumInfo> getAlbumList(Cursor cursor) {
        if (cursor == null) return null;
        ArrayList<AlbumInfo> albumList = new ArrayList<AlbumInfo>();
        while (cursor.moveToNext()) {
            AlbumInfo album = new AlbumInfo();
            album.setAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            album.setAlbumId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
            album.setSongsOfAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
            album.setAlbumCover(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
            albumList.add(album);
        }
        cursor.close();
        return albumList;
    }

    //query folder
    private List<FolderInfo> queryFolder(Context context, String selections, String selection) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        StringBuilder mSelection = new StringBuilder(MediaStore.Files.FileColumns.MEDIA_TYPE
                + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " and " + "("
                + MediaStore.Files.FileColumns.DATA + " like'%.mp3' or " + MediaStore.Audio.Media.DATA
                + " like'%.wma')");

        mSelection.append(") group by ( " + MediaStore.Files.FileColumns.PARENT);
        Cursor cursor = cr.query(uri, FOLDER_SCAN, mSelection.toString(), null, null);
        List<FolderInfo> list = getFolderList(cursor);
        DBUtil.getInstance().getFolderInfoDao().deleteAll();
        DBUtil.getInstance().getFolderInfoDao().saveList(list);
        return list;
    }

    private ArrayList<FolderInfo> getFolderList(Cursor cursor) {
        if (cursor == null) return null;
        ArrayList<FolderInfo> albumList = new ArrayList<FolderInfo>();
        while (cursor.moveToNext()) {
            FolderInfo folder = new FolderInfo();
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String filepath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            String folderpath = filepath.substring(0, filepath.lastIndexOf(File.separator));
            String foldername = folderpath.substring(folderpath.lastIndexOf(File.separator) + 1);

            folder.setId(id);
            folder.setFolderPath(folderpath);
            folder.setFolderName(foldername);
            albumList.add(folder);
        }
        cursor.close();
        return albumList;
    }

    // query artists
    private List<ArtistInfo> queryArtist(Context context, String selections, String selection) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

//        StringBuilder where = new StringBuilder(MediaStore.Audio.Artists._ID
//                + " in (select distinct " + MediaStore.Audio.Media.ARTIST_ID
//                + " from audio_meta where (1=1 ");
//
//        if (Setting.getInstance().mScanSizeLimit) {
//            where.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
//        }
//        if (Setting.getInstance().mScanTimeLimit) {
//            where.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
//        }
//        where.append("))");

        Cursor cursor = cr.query(uri, ARTIST_SCAN, selections, null, MediaStore.Audio.Artists._ID);
        List<ArtistInfo> list = getArtistList(cursor);
        DBUtil.getInstance().getArtistInfoDao().deleteAll();
        DBUtil.getInstance().getArtistInfoDao().saveList(list);
        return list;
    }

    private ArrayList<ArtistInfo> getArtistList(Cursor cursor) {
        if (cursor == null) return null;
        ArrayList<ArtistInfo> artistList = new ArrayList<ArtistInfo>();
        while (cursor.moveToNext()) {
            ArtistInfo album = new ArtistInfo();
            album.setArtistId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
            album.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
            album.setNumOfTracks(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
            album.setArtistKey(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST_KEY)));
            artistList.add(album);
        }
        cursor.close();
        return artistList;
    }

    public interface Callback {
        void start();
        void scan();
        void end();
    }
}
