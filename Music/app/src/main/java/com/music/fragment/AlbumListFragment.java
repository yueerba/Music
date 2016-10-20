package com.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.music.R;
import com.music.adapter.AlbumListAdapter;
import com.music.entity.AlbumInfo;
import com.music.sqlite.DBUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/15.
 */
public class AlbumListFragment extends FragmentBase implements AdapterView.OnItemClickListener {
    private Context mContext;
    private TextView txtTitle;
    private ListView mListView;
    private List<AlbumInfo> mAlbumList;
    private AlbumListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        initView(view);
        bindData();
        return view;
    }

    private void initView(View view) {
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mListView = (ListView) view.findViewById(R.id.listview);
    }

    private void bindData() {
        mAlbumList = DBUtil.getInstance().getAlbumInfoDao().queryAll();
        if (mAlbumList != null) {
            mAdapter = new AlbumListAdapter(mContext, mAlbumList);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
        // TODO Auto-generated method stub
        AlbumInfo albuminfo = mAlbumList.get(postion);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_from_left, R.anim.in_from_left, R.anim.out_from_right);
        transaction.add(R.id.content, MusicListViewFragment.newInstance(albuminfo.getAlbumId(), MusicListViewFragment.FROM_ALBUM));
        transaction.hide(this);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

}

