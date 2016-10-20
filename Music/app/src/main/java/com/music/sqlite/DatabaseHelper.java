package com.music.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.music.entity.AlbumInfo;
import com.music.entity.ArtistInfo;
import com.music.entity.FolderInfo;
import com.music.entity.MusicInfo;

import java.sql.SQLException;

/**
 * Created by dingfeng on 2016/4/12.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // Database Name
    private static String DB_NAME = "music.db";
    // Database Version
    private static final int DB_VERSION = 1;

    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
        // TODO Auto-generated constructor stub
    }

    /**
     * DatabaseHelper single instance
     *
     * @param context
     * @return DatabaseHelper
     */
    public static synchronized DatabaseHelper getDatabaseHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    public void resetDB(Context context, String dbName) {
        DB_NAME = dbName;
        if (instance != null) {
            instance = null;
        }
        getDatabaseHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        // TODO Auto-generated method stub
        try {
            TableUtils.createTable(connectionSource, MusicInfo.class);
            TableUtils.createTable(connectionSource, AlbumInfo.class);
            TableUtils.createTable(connectionSource, FolderInfo.class);
            TableUtils.createTable(connectionSource, ArtistInfo.class);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        try {
            TableUtils.dropTable(connectionSource, MusicInfo.class, true);
            TableUtils.dropTable(connectionSource, AlbumInfo.class, true);
            TableUtils.dropTable(connectionSource, FolderInfo.class, true);
            TableUtils.dropTable(connectionSource, ArtistInfo.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
