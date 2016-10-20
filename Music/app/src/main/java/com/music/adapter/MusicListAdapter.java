package com.music.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.music.AppInfo;
import com.music.R;
import com.music.entity.MusicInfo;
import com.music.service.MusicUtil;
import com.music.sqlite.DBUtil;
import com.music.util.CommonUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/15.
 */
public class MusicListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MusicInfo> mMusicList;

    public MusicListAdapter(Context context, List<MusicInfo> list) {
        mContext = context;
        mMusicList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mMusicList.size();
    }

    @Override
    public MusicInfo getItem(int position) {
        // TODO Auto-generated method stub
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup parent) {
        // TODO Auto-generated method stub
        final MusicInfo music = getItem(position);
        ViewHolder viewholder;
        if (convertview == null) {
            viewholder = new ViewHolder();
            convertview = LayoutInflater.from(mContext).inflate(R.layout.music_list_item, null);
            viewholder.itemMusic = (LinearLayout) convertview.findViewById(R.id.itemMusic);
            viewholder.tv_music_name = (TextView) convertview.findViewById(R.id.tv_music_name);
            viewholder.tv_artist_name = (TextView) convertview.findViewById(R.id.tv_artist_name);
            viewholder.tv_music_duration = (TextView) convertview.findViewById(R.id.tv_music_duration);
            convertview.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertview.getTag();
        }

        viewholder.tv_music_name.setText(music.getMusicName());
        viewholder.tv_artist_name.setText(music.getArtist());
        viewholder.tv_music_duration.setText(CommonUtil.makeTimeString(music.getDuration()));

        return convertview;
    }

    class ViewHolder {
        TextView tv_music_name;
        TextView tv_artist_name;
        TextView tv_music_duration;
        LinearLayout itemMusic;
    }

    public void onItemClick(int position) {
        try {
            MusicUtil.sService.refreshPlayList(mMusicList);
            MusicUtil.sService.play(position);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onItemLongClick(View view, int position) {
        MusicInfo music = mMusicList.get(position);
        showPopMenu(view, music);
    }

    private void showPopMenu(final View view, final MusicInfo music) {

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_menu, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, AppInfo.screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.popwin_anim_style2);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView txtFavorite = (TextView) contentView.findViewById(R.id.txtFavorite);
        TextView txtDelete = (TextView) contentView.findViewById(R.id.txtDelete);
        TextView txtRing = (TextView) contentView.findViewById(R.id.txtRing);

        if (music.isFavorite) {
            txtFavorite.setText(R.string.add_favorite);
        } else {
            txtFavorite.setText(R.string.cancel_favorite);
        }

        txtFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.setIsFavorite(!music.isFavorite);
                DBUtil.getInstance().getMusicInfoDao().update(music);
                Snackbar.make(view, music.isFavorite ? R.string.add_favorite_success : R.string.cancel_favorite_success, Snackbar.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_music, music.getMusicName()))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBUtil.getInstance().getMusicInfoDao().deleteEntity(music);
                                mMusicList.remove(music);
                                notifyDataSetChanged();
                                try {
                                    MusicUtil.sService.removeMusic(music);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                // update artist table
                                List<MusicInfo> list_artist = DBUtil.getInstance().getMusicInfoDao().queryList("artistId", music.artistId);
                                if (list_artist == null || list_artist.size() < 1) {
                                    DBUtil.getInstance().getArtistInfoDao().deleteEntity("artistId", music.artistId);
                                } else {
                                    DBUtil.getInstance().getArtistInfoDao().update("artistId", music.artistId, new String[]{"numOfTracks"}, new Object[]{list_artist.size()});
                                }
                                // update album table
                                List<MusicInfo> list_album = DBUtil.getInstance().getMusicInfoDao().queryList("albumId", music.albumId);
                                if (list_album == null || list_album.size() < 1) {
                                    DBUtil.getInstance().getAlbumInfoDao().deleteEntity("albumId", music.albumId);
                                } else {
                                    DBUtil.getInstance().getAlbumInfoDao().update("albumId", music.albumId, new String[]{"songsOfAlbum"}, new Object[]{list_album.size()});
                                }

                                Snackbar.make(view,  mContext.getString(R.string.delete_music_success, music.getMusicName()), Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        txtRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                MusicUtil.setRingtone(mContext, music.songId, view);
            }
        });

    }

}

