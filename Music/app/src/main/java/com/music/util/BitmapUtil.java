package com.music.util;

/**
 * Created by dingfeng on 2016/4/13.
 */
public class BitmapUtil {
    private static BitmapUtil bitmap_util;
//    private static BitmapUtils bitmap;
//    private static BitmapUtils bitmap_album_cover;

    public static BitmapUtil getInstance() {
        if(bitmap_util == null) {
            bitmap_util = new BitmapUtil();
        }
//        if(bitmap == null) {
//            bitmap = new BitmapUtils(MusicApplication.getAppContext(), IConstants.CACH_IMG_PATH);
//            bitmap.configDefaultLoadingImage(R.drawable.ic_launcher);
//            bitmap.configDefaultLoadFailedImage(R.drawable.ic_launcher);
//            bitmap.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//        }
//        if(bitmap_album_cover == null) {
//            bitmap_album_cover = new BitmapUtils(MusicApplication.getAppContext(), IConstants.CACH_IMG_PATH);
//            bitmap_album_cover.configDefaultLoadingImage(R.drawable.img_album_background);
//            bitmap_album_cover.configDefaultLoadFailedImage(R.drawable.img_album_background);
//            bitmap_album_cover.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//        }
        return bitmap_util;
    }

//    public BitmapUtils getBitmapUtils() {
//        return bitmap;
//    }
//
//    public BitmapUtils getBmpAlbumCoverUtils() {
//        return bitmap_album_cover;
//    }
}
