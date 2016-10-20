package com.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.music.R;
import com.music.adapter.MusicListAdapter;
import com.music.entity.AlbumInfo;
import com.music.entity.ArtistInfo;
import com.music.entity.FolderInfo;
import com.music.entity.MusicInfo;
import com.music.sqlite.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingfeng on 2016/4/17.
 */
public class MusicListViewFragment extends FragmentBase implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ImageView back;
    private TextView txtTitle;
    private ListView mListView;

    private List<MusicInfo> mMusicList = new ArrayList<>();
    private MusicListAdapter mAdapter;

    public static final int FROM_ALBUM = 1;
    public static final int FROM_ARTIST = 2;
    public static final int FROM_FOLDER = 3;

    public static MusicListViewFragment newInstance(int arg, int from) {
        MusicListViewFragment fragment = new MusicListViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("arg", arg);
        bundle.putInt("from", from);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list_view, container, false);
        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        mListView = (ListView) view.findViewById(R.id.listview);
        bindData();
        return view;
    }

    private void bindData() {
        Bundle bundle = getArguments();
        int albumId = -1;
        int type = -1;
        if (bundle != null) {
            albumId = getArguments().getInt("arg", -1);
            type = getArguments().getInt("from", -1);
        }
        if (albumId < 0) {
            return;
        }

        getMusicList(albumId, type);
        mAdapter = new MusicListAdapter(getActivity(), mMusicList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    private void getMusicList(int id, int from) {
        if (from == FROM_ALBUM) {
            AlbumInfo albumInfo = DBUtil.getInstance().getAlbumInfoDao().queryEntity("albumId", id);
            txtTitle.setText(albumInfo.getAlbumName());
            mMusicList = DBUtil.getInstance().getMusicInfoDao().queryList("albumId", id);
        } else if (from == FROM_ARTIST) {
            ArtistInfo artist = DBUtil.getInstance().getArtistInfoDao().queryEntity("artistId", id);
            txtTitle.setText(artist.getArtistName());
            mMusicList = DBUtil.getInstance().getMusicInfoDao().queryList("artistId", id);
        } else if (from == FROM_FOLDER) {
            FolderInfo folder = DBUtil.getInstance().getFolderInfoDao().queryEntity("folderId", id);
            txtTitle.setText(folder.getFolderName());
            List<MusicInfo> list = DBUtil.getInstance().getMusicInfoDao().queryAll();
            for (MusicInfo music : list) {
                if (music.getData().contains(folder.getFolderPath())) {
                    mMusicList.add(music);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.onItemClick(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.onItemLongClick(view, position);
        return true;
    }

}
