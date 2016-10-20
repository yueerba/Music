package com.music.util;

import com.music.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by dingfeng on 2016/4/15.
 */
public class ImageLoaderUtil {

    public static DisplayImageOptions albumCover = null;

    public static DisplayImageOptions getAlbumCoverOption() {
        if(albumCover == null) {
            albumCover = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.img_album_background) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.img_album_background) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.img_album_background) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(90)) // 设置成圆角图片
                    .build();
        }
        return albumCover;
    }

}
