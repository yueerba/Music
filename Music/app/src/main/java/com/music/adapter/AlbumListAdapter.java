package com.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.music.R;
import com.music.entity.AlbumInfo;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/15.
 */
public class AlbumListAdapter extends BaseAdapter {

    private Context mContext;
    private List<AlbumInfo> mAlbumList;

    public AlbumListAdapter(Context context, List<AlbumInfo> list) {
        mContext = context;
        mAlbumList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mAlbumList.size();
    }

    @Override
    public AlbumInfo getItem(int position) {
        // TODO Auto-generated method stub
        return mAlbumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewholder;
        AlbumInfo album = mAlbumList.get(position);
        if(convertview == null) {
            viewholder = new ViewHolder();
            convertview = LayoutInflater.from(mContext).inflate(R.layout.item_album_list, null);
            viewholder.tv_album_name = (TextView) convertview.findViewById(R.id.tv_album_name);
            viewholder.tv_songs_nums = (TextView) convertview.findViewById(R.id.tv_songs_nums);
            convertview.setTag(viewholder);
        }else {
            viewholder = (ViewHolder) convertview.getTag();
        }

        viewholder.tv_album_name.setText(album.getAlbumName());
        viewholder.tv_songs_nums.setText(album.getSongsOfAlbum()+"首歌曲");
        return convertview;
    }

    class ViewHolder {
        TextView tv_album_name;
        TextView tv_songs_nums;
    }

}

