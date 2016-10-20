package com.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.music.R;
import com.music.entity.FolderInfo;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/29.
 */
public class FolderListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FolderInfo> mFolderList;

    public FolderListAdapter(Context context, List<FolderInfo> list) {
        mContext = context;
        mFolderList = list;
    }

    @Override
    public int getCount() {
        return mFolderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFolderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        FolderInfo folder = mFolderList.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_artist_list, null);
            viewholder.txtArtist = (TextView) convertView.findViewById(R.id.txtArtist);
            viewholder.txtCount = (TextView) convertView.findViewById(R.id.txtCount);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.txtArtist.setText(folder.getFolderName());
        viewholder.txtCount.setText(folder.getFolderPath());
        return convertView;
    }

    class ViewHolder {
        TextView txtArtist;
        TextView txtCount;
    }

}
