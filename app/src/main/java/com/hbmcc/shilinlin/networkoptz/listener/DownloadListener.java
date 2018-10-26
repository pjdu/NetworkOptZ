package com.hbmcc.shilinlin.networkoptz.listener;

/**
 * Created by linlinshi on 2018/5/6.
 */

public interface DownloadListener {
    void onProgerss(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
