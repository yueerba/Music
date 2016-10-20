package com.music.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dingfeng on 2016/4/16.
 */
@DatabaseTable(tableName = "table_folder")
public class FolderInfo implements Parcelable {
    @DatabaseField(id = true, uniqueCombo = true, columnName = "folderId")
    private int folderId;
    @DatabaseField(columnName = "fname")
    private String fname;
    @DatabaseField(columnName = "fpath")
    private String fpath;

    public int getId() {
        return folderId;
    }

    public void setId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return fname;
    }

    public void setFolderName(String fname) {
        this.fname = fname;
    }

    public String getFolderPath() {
        return fpath;
    }

    public void setFolderPath(String fpath) {
        this.fpath = fpath;
    }

    public static final Parcelable.Creator<FolderInfo> CREATOR = new Parcelable.Creator<FolderInfo>() {

        @Override
        public FolderInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            FolderInfo folder = new FolderInfo();
            folder.setId(source.readInt());
            folder.setFolderName(source.readString());
            folder.setFolderPath(source.readString());
            return folder;
        }

        @Override
        public FolderInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new FolderInfo[size];
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
        dest.writeInt(folderId);
        dest.writeString(fname);
        dest.writeString(fpath);
    }

}

