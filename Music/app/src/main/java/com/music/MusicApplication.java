package com.music;

import android.app.Application;

import com.music.base.IConstants;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by dingfeng on 2016/4/12.
 */
public class MusicApplication extends Application {

    private static MusicApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initImageLoader();
        AppInfo.init(mInstance);
    }

    public static MusicApplication getInstance() {
        return mInstance;
    }

    private void initImageLoader() {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(mInstance);
        config.diskCacheFileCount(100); //缓存文件数量
        config.threadPoolSize(5);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.memoryCacheSize(2 * 1024 * 1024);
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        config.diskCache(new UnlimitedDiskCache(new File(IConstants.CACHE)));

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
