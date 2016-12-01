package com.baofeng.soulrelay.utils.imageloader.glideprogress;
/**
 * @author  "https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java"
 * @see <a href="https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java">OkHttp sample</a>
 * 通知下载进度
 * modified by soulrelay
 */
interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
