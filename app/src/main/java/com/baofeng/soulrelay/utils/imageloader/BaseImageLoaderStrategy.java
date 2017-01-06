package com.baofeng.soulrelay.utils.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.baofeng.soulrelay.utils.imageloader.glideprogress.ProgressLoadListener;


/**
 * Created by soulrelay on 2016/10/11.
 * Class Note:
 * abstract class/interface defined to load image
 * (Strategy Pattern used here)
 */
public interface BaseImageLoaderStrategy {
    //无占位图
    void loadImage(String url, ImageView imageView);
    //这里的context指定为ApplicationContext
    void loadImageWithAppCxt(String url, ImageView imageView);

    void loadImage(String url, int placeholder, ImageView imageView);

    void loadImage(Context context, String url, int placeholder, ImageView imageView);

    void loadGifImage(String url, int placeholder, ImageView imageView);

    void loadImageWithProgress(String url, ImageView imageView, ProgressLoadListener listener);

    void loadGifWithProgress(String url, ImageView imageView, ProgressLoadListener listener);

    void loadGifWithPrepareCall(String url, ImageView imageView, SourceReadyListener listener);
    //清除硬盘缓存
    void clearImageDiskCache(final Context context);
    //清除内存缓存
    void clearImageMemoryCache(Context context);
    //根据不同的内存状态，来响应不同的内存释放策略
    void trimMemory(Context context, int level);
    //获取缓存大小
    String getCacheSize(Context context);

}
