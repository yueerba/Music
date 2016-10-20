package com.music.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dingfeng on 2016/4/29.
 */
@DatabaseTable(tableName = "table_artist")
public class ArtistInfo {
    @DatabaseField(id = true, uniqueCombo = true, columnName = "artistId")
    public int artistId = -1;
    @DatabaseField(columnName = "artistname")
    public String artistName;
    @DatabaseField(columnName = "artistKey")
    public String artistKey;
    @DatabaseField(columnName = "numOfTracks")
    public int numOfTracks;

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }

    public int getNumOfTracks() {
        return numOfTracks;
    }

    public void setNumOfTracks(int numOfTracks) {
        this.numOfTracks = numOfTracks;
    }
}
