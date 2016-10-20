package com.music.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.music.R;
import com.music.adapter.MusicListAdapter;
import com.music.entity.MusicInfo;
import com.music.service.MusicUtil;
import com.music.sqlite.DBUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/28.
 */
public class MyFavoriteFragment extends FragmentBase implements AdapterView.OnItemClickListener{

    private ListView mListView;
    private ImageView back;
    private TextView txtTitle;
    private MusicListAdapter mAdapter;
    private List<MusicInfo> mMusicList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_list, container, false);
        initView(view);
        bindData();
        return view;
    }

    private void initView(View view) {
        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        mListView = (ListView) view.findViewById(R.id.listview);
    }

    private void bindData() {
        mMusicList = DBUtil.getInstance().getMusicInfoDao().queryList("isFavorite", true);
        if (mMusicList != null) {
            mAdapter = new MusicListAdapter(getActivity(), mMusicList);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        try {
            MusicUtil.sService.refreshPlayList(mMusicList);
            MusicUtil.sService.play(position);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
