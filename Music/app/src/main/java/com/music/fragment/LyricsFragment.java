package com.music.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.AppInfo;
import com.music.R;
import com.music.base.IConstants;
import com.music.entity.MusicInfo;
import com.music.lrc.LyricAdapter;
import com.music.lrc.LyricDownloadManager;
import com.music.lrc.LyricLoadHelper;
import com.music.lrc.LyricSentence;

import java.io.File;
import java.util.List;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class LyricsFragment extends Fragment implements View.OnClickListener {

    String musicname;
    String artist;

    private ListView lyricshow;
    private TextView lyric_empty;

    private LyricAdapter mLyricAdapter;
    private LyricLoadHelper mLyricLoadHelper;
    private LyricDownloadManager mLyricDownloadManager;
    /**
     * 歌词是否正在下载
     */
    private boolean mIsLyricDownloading;

    MusicInfo currentmusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        currentmusic = intent.getParcelableExtra("current");
        if (currentmusic != null) {
            musicname = currentmusic.getMusicName();
            artist = currentmusic.getArtist();
        }

        mLyricLoadHelper = new LyricLoadHelper();
        mLyricLoadHelper.setLyricListener(mLyricListener);
        mLyricDownloadManager = new LyricDownloadManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        lyricshow = (ListView) view.findViewById(R.id.lyricshow);
        lyric_empty = (TextView) view.findViewById(R.id.lyric_empty);
        lyric_empty.setOnClickListener(this);
        mLyricAdapter = new LyricAdapter(getActivity());
        lyricshow.setAdapter(mLyricAdapter);
        lyricshow.setEmptyView(lyric_empty);

        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadLyric(currentmusic);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.lyric_empty:
                if (currentmusic == null) {
                    return;
                }
                showLryDialog();
                break;
        }
    }

    private LyricLoadHelper.LyricListener mLyricListener = new LyricLoadHelper.LyricListener() {

        @Override
        public void onLyricLoaded(List<LyricSentence> lyricSentences, int indexOfCurSentence) {
            // TODO Auto-generated method stub
            if (lyricSentences != null) {
                mLyricAdapter.setLyric(lyricSentences);
                mLyricAdapter.setCurrentSentenceIndex(indexOfCurSentence);
                mLyricAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLyricSentenceChanged(int indexOfCurSentence) {
            // TODO Auto-generated method stub
            mLyricAdapter.setCurrentSentenceIndex(indexOfCurSentence);
            mLyricAdapter.notifyDataSetChanged();
            lyricshow.smoothScrollToPositionFromTop(indexOfCurSentence, lyricshow.getHeight() / 2, 500);
        }

    };

    class LyricDownloadAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String lyricFilePath = mLyricDownloadManager.searchLyricFromWeb(params[0], params[1], currentmusic.getMusicName());
            mIsLyricDownloading = false;
            return lyricFilePath;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            mLyricLoadHelper.loadLyric(result);
        }

    }

    public void slideLyricLine(long millisecond) {
        if (mLyricLoadHelper != null) {
            mLyricLoadHelper.notifyTime(millisecond);
        }
    }

    public void loadLyric(MusicInfo currentMusic) {
        if (currentMusic == null) {
            return;
        }
        String lyricFilePath = IConstants.LYRICPATH + currentMusic.getMusicName() + ".lrc";
        File lyricFile = new File(lyricFilePath);

        if (lyricFile.exists()) {
            mLyricLoadHelper.loadLyric(lyricFilePath);
        } else {
            mIsLyricDownloading = true;
            new LyricDownloadAsyncTask().execute(currentMusic.getMusicName(), currentMusic.getArtist());
        }

    }

    public void loadLyricByHand(String musicName, String artist) {
        String lyricFilePath = IConstants.LYRICPATH + musicName + ".lrc";
        File lyricFile = new File(lyricFilePath);

        if (lyricFile.exists()) {
            mLyricLoadHelper.loadLyric(lyricFilePath);
        } else {
            mIsLyricDownloading = true;
            new LyricDownloadAsyncTask().execute(musicName, artist);
        }
    }

    private void showLryDialog() {
        final View view = View.inflate(getActivity(), R.layout.lry_dialog, null);
        view.setMinimumWidth(AppInfo.screenWidth - 40);

        final Dialog dialog = new Dialog(getActivity(), R.style.lrc_dialog);
        final Button okBtn = (Button) view.findViewById(R.id.ok_btn);
        final Button cancleBtn = (Button) view.findViewById(R.id.cancel_btn);
        final EditText artistEt = (EditText) view.findViewById(R.id.artist_tv);
        final EditText musicEt = (EditText) view.findViewById(R.id.music_tv);

        artistEt.setText(currentmusic.getArtist());
        musicEt.setText(currentmusic.getMusicName());
        View.OnClickListener btnListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == okBtn) {
                    if (mIsLyricDownloading) {
                        Toast.makeText(getActivity(), R.string.downloading, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String artist = artistEt.getText().toString().trim();
                    String music = musicEt.getText().toString().trim();
                    if (TextUtils.isEmpty(artist) || TextUtils.isEmpty(music)) {
                        Toast.makeText(getActivity(), R.string.artist_music_null, Toast.LENGTH_SHORT).show();
                    } else {
                        // 开始搜索
                        loadLyricByHand(music, artist);
                        dialog.dismiss();
                    }
                } else if (v == cancleBtn) {
                    dialog.dismiss();
                }
            }
        };
        okBtn.setOnClickListener(btnListener);
        cancleBtn.setOnClickListener(btnListener);
        dialog.setContentView(view);
        dialog.show();
    }

}

