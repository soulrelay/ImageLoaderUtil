package com.baofeng.soulrelay.utils.imageloader.glideprogress;

/**
 * 通知UI进度
 * modified by soulrelay
 */
public interface ProgressUIListener {
    void update(int bytesRead, int contentLength);
}
