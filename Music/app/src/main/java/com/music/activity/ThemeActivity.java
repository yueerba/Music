package com.music.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.music.R;
import com.music.base.IConstants;
import com.music.util.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class ThemeActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    ImageView back;
    GridView mGridView;
    List<ThemeEntity> mThemeList;
    ThemeAdapter mThemeAdapter;
    String mSelectedTheme;

    class ThemeEntity {
        Bitmap bitmap;
        String path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_list);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backClickListener);

        getData();

        mSelectedTheme = SharedPreferencesUtil.getInstance().getShare(IConstants.SELECTED_THEME, null);

        mGridView = (GridView) findViewById(R.id.gv_content);
        mThemeAdapter = new ThemeAdapter(mThemeList);
        mGridView.setAdapter(mThemeAdapter);
        mGridView.setOnItemClickListener(this);
    }

    private void getData() {
        AssetManager am = getAssets();
        try {
            String[] drawableList = am.list("theme");
            mThemeList = new ArrayList<>();
            for (String path : drawableList) {
                ThemeEntity te = new ThemeEntity();
                InputStream is = am.open("theme/" + path);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                te.bitmap = bitmap;
                te.path = path;
                mThemeList.add(te);
                is.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        String path = mThemeList.get(position).path;
        mSelectedTheme = path;
        SharedPreferencesUtil.getInstance().putShare(IConstants.SELECTED_THEME, mSelectedTheme);
        mThemeAdapter.notifyDataSetChanged();
        sendBroadcast(new Intent(IConstants.BROADCAST_THEMECHANGE));
        finish();
    }

    private class ThemeAdapter extends BaseAdapter {

        private List<ThemeEntity> themelist;
        private Resources resources;

        public ThemeAdapter(List<ThemeEntity> list) {
            this.themelist = list;
            this.resources = getResources();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return themelist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return themelist.get(position);
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
                convertview = getLayoutInflater().inflate(R.layout.theme_list_item, null);
                holder.iv_theme = (ImageView) convertview.findViewById(R.id.iv_theme);
                holder.iv_item_checked = (ImageView) convertview.findViewById(R.id.iv_item_checked);
                convertview.setTag(holder);
            } else {
                holder = (ViewHolder) convertview.getTag();
            }

            holder.iv_theme.setImageBitmap(themelist.get(position).bitmap);
            if (themelist.get(position).path.equals(mSelectedTheme)) {
                holder.iv_item_checked.setVisibility(View.VISIBLE);
            } else {
                holder.iv_item_checked.setVisibility(View.GONE);
            }

            return convertview;
        }

        class ViewHolder {
            ImageView iv_theme;
            ImageView iv_item_checked;
        }

    }

}

