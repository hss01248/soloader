package com.hss01248.soloader;

/**
 * time:2019/7/14
 * author:hss
 * desription:
 */
public interface SoDownloadCallback {

    void onSuccess(String path);
    void onError(Throwable e,String msg,String code);

    void onCancel();
}
