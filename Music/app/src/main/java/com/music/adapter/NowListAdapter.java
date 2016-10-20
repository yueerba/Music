package com.music.adapter;

import android.content.Context;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.R;
import com.music.entity.MusicInfo;
import com.music.service.MusicUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/5/16.
 */
public class NowListAdapter extends BaseAdapter {

    Context mContext;
    List<MusicInfo> mList;

    public NowListAdapter(Context context, List<MusicInfo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MusicInfo item = mList.get(position);
        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_now_list, null);
            holder = new ViewHolder();
            holder.txtNo = (TextView) convertView.findViewById(R.id.txtNo);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.remove = (RelativeLayout) convertView.findViewById(R.id.remove);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.txtNo.setText(position + 1 + ".");
        holder.txtTitle.setText(item.getMusicName());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MusicUtil.sService.removeMusic(item);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView txtNo;
        TextView txtTitle;
        RelativeLayout remove;
    }

}
