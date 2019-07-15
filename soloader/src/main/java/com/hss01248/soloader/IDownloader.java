package com.hss01248.soloader;

/**
 * time:2019/7/14
 * author:hss
 * desription:
 */
public interface IDownloader {

    void download(boolean onlyWif,boolean showProgress,String md5,String savedPath,String url,SoDownloadCallback callback);
}
