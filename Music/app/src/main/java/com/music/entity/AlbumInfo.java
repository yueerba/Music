package com.music.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dingfeng on 2016/4/13.
 */
@DatabaseTable(tableName = "table_album")
public class AlbumInfo implements Parcelable {
    @DatabaseField(id = true, uniqueCombo = true, columnName = "albumId")
    private int albumId;                 // 专辑id
    @DatabaseField(columnName = "albumName")
    private String albumName;            // 专辑名字
    @DatabaseField(columnName = "songsOfAlbum")
    private int songsOfAlbum;            // 专辑歌曲数
    @DatabaseField(columnName = "albumCover")
    private String albumCover;           // 专辑封面

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getSongsOfAlbum() {
        return songsOfAlbum;
    }

    public void setSongsOfAlbum(int songsOfAlbum) {
        this.songsOfAlbum = songsOfAlbum;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public static final Parcelable.Creator<AlbumInfo> CREATOR = new Parcelable.Creator<AlbumInfo>() {

        @Override
        public AlbumInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            AlbumInfo album = new AlbumInfo();
            album.setAlbumId(source.readInt());
            album.setAlbumName(source.readString());
            album.setSongsOfAlbum(source.readInt());
            album.setAlbumCover(source.readString());
            return album;
        }

        @Override
        public AlbumInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new AlbumInfo[size];
        }

    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(albumId);
        dest.writeString(albumName);
        dest.writeInt(songsOfAlbum);
        dest.writeString(albumCover);
    }

}
