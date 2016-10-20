package com.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.R;

/**
 * Created by dingfeng on 2016/4/17.
 */
public class HomeGridAdapter extends BaseAdapter {

    private Context mContext;
    private String[] names;
    private String[] icons;
    private String[] nums;

    public HomeGridAdapter(Context context, String[] icons, String[] names, String[] nums) {
        mContext = context;
        this.icons = icons;
        this.names = names;
        this.nums = nums;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return icons[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewholder;
        if (convertview == null) {
            viewholder = new ViewHolder();
            convertview = LayoutInflater.from(mContext).inflate(R.layout.main_gridview_item, null);
            viewholder.icon = (ImageView) convertview.findViewById(R.id.iv_item_icon);
            viewholder.name = (TextView) convertview.findViewById(R.id.tv_item_name);
            viewholder.num = (TextView) convertview.findViewById(R.id.tv_item_num);
            convertview.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertview.getTag();
        }

        int iconId = mContext.getResources().getIdentifier(icons[position], "drawable", mContext.getPackageName());
        viewholder.icon.setImageResource(iconId);
        viewholder.name.setText(names[position]);
        viewholder.num.setText(nums[position]);

        return convertview;
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView num;
    }

}
