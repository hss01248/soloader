package com.hss01248.soloaderdemo;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hss01248.soloader.IDownloader;
import com.hss01248.soloader.SoDownloadCallback;
import com.hss01248.soloader.SoFileLoadManager;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.io.File;

/**
 * time:2019/7/14
 * author:hss
 * desription:
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SoFileLoadManager.init(this, true, "libopencv_java3.so", "",
                "https://github.com/aayazkhan/Android-Face-Recognition/blob/master/opencv/src/main/jniLibs/armeabi-v7a/libopencv_java3.so?raw=true",
                new IDownloader() {
                    @Override
                    public void download(boolean onlyWif, boolean showProgress, String md5, final String savedPath, String url, final SoDownloadCallback callback) {
                        File file = new File(savedPath);
                        DownloadTask  task = new DownloadTask.Builder(url, file.getParentFile())
                                .setFilename(file.getName())
                                // the minimal interval millisecond for callback progress
                                .setMinIntervalMillisCallbackProcess(30)
                                // do re-download even if the task has already been completed in the past.
                                .setPassIfAlreadyCompleted(false)
                                .setWifiRequired(false)
                                .build();
                        //
                        task.enqueue(new DownloadListener3() {
                            @Override
                            protected void started(@NonNull DownloadTask task) {

                            }

                            @Override
                            protected void completed(@NonNull DownloadTask task) {
                                callback.onSuccess(savedPath);
                            }

                            @Override
                            protected void canceled(@NonNull DownloadTask task) {
                                callback.onCancel();
                            }

                            @Override
                            protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
callback.onError(e,e.getMessage(),"");
                            }

                            @Override
                            protected void warn(@NonNull DownloadTask task) {

                            }

                            @Override
                            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

                            }

                            @Override
                            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

                            }

                            @Override
                            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                                Log.d("dd","total:"+totalLength+",progress:"+currentOffset);
                            }
                        });
                    }
                });
    }
}
