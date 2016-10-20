package com.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.music.R;
import com.music.entity.ArtistInfo;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/29.
 */
public class ArtistListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ArtistInfo> mArtistList;

    public ArtistListAdapter(Context context, List<ArtistInfo> list) {
        mContext = context;
        mArtistList = list;
    }

    @Override
    public int getCount() {
        return mArtistList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArtistList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        ArtistInfo artist = mArtistList.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_artist_list, null);
            viewholder.txtArtist = (TextView) convertView.findViewById(R.id.txtArtist);
            viewholder.txtCount = (TextView) convertView.findViewById(R.id.txtCount);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.txtArtist.setText(artist.getArtistName());
        viewholder.txtCount.setText(artist.getNumOfTracks() + "首歌曲");
        return convertView;
    }

    class ViewHolder {
        TextView txtArtist;
        TextView txtCount;
    }
}
