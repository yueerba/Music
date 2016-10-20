package com.music.lrc;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.music.R;

import java.util.ArrayList;
import java.util.List;

public class LyricAdapter extends BaseAdapter {
    private static final String TAG = "dingfeng";

    List<LyricSentence> mLyricSentences = null;
    Context mContext;
    /**
     * 当前的句子索引号
     */
    int mIndexOfCurrentSentence = 0;

    float mCurrentSize = 20;
    float mNotCurrentSize = 17;

    public LyricAdapter(Context context) {
        mContext = context;
        mLyricSentences = new ArrayList<LyricSentence>();
        mIndexOfCurrentSentence = 0;
    }

    /**
     * 设置歌词，由外部调用，
     */
    public void setLyric(List<LyricSentence> lyric) {
        mLyricSentences.clear();
        if (lyric != null) {
            mLyricSentences.addAll(lyric);
            Log.i(TAG, "歌词句子数目=" + mLyricSentences.size());
        }
        mIndexOfCurrentSentence = 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        // 歌词为空时，让ListView显示EmptyView
        if (mLyricSentences == null) {
            Log.i(TAG, "isEmpty:null");
            return true;
        } else if (mLyricSentences.size() == 0) {
            Log.i(TAG, "isEmpty:size=0");
            return true;
        } else {
            Log.i(TAG, "isEmpty:not empty");
            return false;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        // 禁止在列表条目上点击
        return false;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mLyricSentences.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mLyricSentences.get(position).getContentText();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertview == null) {
            holder = new ViewHolder();
            convertview = LayoutInflater.from(mContext).inflate(R.layout.lyric_item, null);
            holder.tv_lyric_line = (TextView) convertview.findViewById(R.id.tv_lyric_line);
            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }
        if (position > 0 && position < mLyricSentences.size()) {
            holder.tv_lyric_line.setText(mLyricSentences.get(position).getContentText());
        }
        if (position == mIndexOfCurrentSentence) {
            holder.tv_lyric_line.setTextColor(Color.WHITE);
            holder.tv_lyric_line.setTextSize(mCurrentSize);
        } else {
            holder.tv_lyric_line.setTextColor(Color.BLACK);
            holder.tv_lyric_line.setTextSize(mNotCurrentSize);
        }

        return convertview;
    }

    class ViewHolder {
        TextView tv_lyric_line;
    }

    public void setCurrentSentenceIndex(int index) {
        mIndexOfCurrentSentence = index;
    }

}
