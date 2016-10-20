package com.music.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.music.R;
import com.music.adapter.ArtistListAdapter;
import com.music.entity.ArtistInfo;
import com.music.sqlite.DBUtil;

import java.util.List;

/**
 * Created by dingfeng on 2016/4/29.
 */
public class ArtistFragment extends FragmentBase implements AdapterView.OnItemClickListener {

    private ImageView back;
    private TextView txtTitle;
    private ListView mListView;
    private List<ArtistInfo> mArtistList;
    private ArtistListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
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
        mArtistList = DBUtil.getInstance().getArtistInfoDao().queryAll();
        if (mArtistList != null) {
            mAdapter = new ArtistListAdapter(getActivity(), mArtistList);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        ArtistInfo artistInfo = mArtistList.get(position);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_from_left, R.anim.in_from_left, R.anim.out_from_right);
        transaction.add(R.id.content, MusicListViewFragment.newInstance(artistInfo.artistId, MusicListViewFragment.FROM_ARTIST));
        transaction.hide(this);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

}
