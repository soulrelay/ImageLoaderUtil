package com.baofeng.soulrelay.utils.imageloader.glideprogress;

/**
 * 通知图片加载进度
 * modified by soulrelay
 */
public interface ProgressLoadListener {

    void update(int bytesRead, int contentLength);

    void onException();

    void onResourceReady();
}
