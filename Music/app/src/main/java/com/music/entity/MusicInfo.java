package com.music.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dingfeng on 2016/4/13.
 */
@DatabaseTable(tableName = "table_music")
public class MusicInfo implements Parcelable {
    @DatabaseField(id = true, uniqueCombo = true, columnName = "songId")
    public int songId = -1;
    @DatabaseField(columnName = "albumId")
    public int albumId = -1;
    @DatabaseField(columnName = "duration")
    public long duration;
    @DatabaseField(columnName = "size")
    public long size;
    @DatabaseField(columnName = "musicName")
    public String musicName;
    @DatabaseField(columnName = "artist")
    public String artist;
    @DatabaseField(columnName = "artistId")
    public int artistId;
    @DatabaseField(columnName = "data")
    public String data;
    @DatabaseField(columnName = "folder")
    public String folder;
//    @DatabaseField(columnName = "musicNameKey")
//    public String musicNameKey;
    @DatabaseField(columnName = "artistKey")
    public String artistKey;
    @DatabaseField(columnName = "isMusic")
    public boolean isMusic;
    @DatabaseField(columnName = "isFavorite")
    public boolean isFavorite;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

//    public String getMusicNameKey() {
//        return musicNameKey;
//    }
//
//    public void setMusicNameKey(String musicNameKey) {
//        this.musicNameKey = musicNameKey;
//    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setIsMusic(boolean isMusic) {
        this.isMusic = isMusic;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>() {

        @Override
        public MusicInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            MusicInfo music = new MusicInfo();
            music.setSongId(source.readInt());
            music.setAlbumId(source.readInt());
            music.setDuration(source.readLong());
            music.setSize(source.readLong());
            music.setMusicName(source.readString());
            music.setArtist(source.readString());
            music.setArtistId(source.readInt());
            music.setData(source.readString());
            music.setFolder(source.readString());
//            music.setMusicNameKey(source.readString());
            music.setArtistKey(source.readString());
            music.setIsMusic(source.readInt() == 1);
            music.setIsFavorite(source.readInt() == 1);
            return music;
        }

        @Override
        public MusicInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new MusicInfo[size];
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
        dest.writeInt(songId);
        dest.writeInt(albumId);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(musicName);
        dest.writeString(artist);
        dest.writeInt(artistId);
        dest.writeString(data);
        dest.writeString(folder);
//        dest.writeString(musicNameKey);
        dest.writeString(artistKey);
        dest.writeInt(isMusic?1:0);
        dest.writeInt(isFavorite?1:0);
    }

}

