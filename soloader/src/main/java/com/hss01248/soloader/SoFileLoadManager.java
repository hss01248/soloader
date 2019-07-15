package com.hss01248.soloader;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Description:读写和加载指定路径下so
 *
 * @author Dusan, Created on 2019/3/15 - 18:19.
 * E-mail:duqian2010@gmail.com
 */
public class SoFileLoadManager {

    private static String CUSTOM_SO_DIR ;
    private static Application app;
    private static boolean isDebug;
    private static IDownloader downloader;

    public static void init(Application app,boolean isDebug,String soName,String md5,String url,IDownloader iDownloader){
        SoFileLoadManager.app = app;
        SoFileLoadManager.isDebug = isDebug;
        downloader = iDownloader;

        CUSTOM_SO_DIR = app.getDir("libs", Context.MODE_PRIVATE).getAbsolutePath();


        check(soName, url, false, false,md5, new SoDownloadCallback() {
            @Override
            public void onSuccess(String path) {

            }

            @Override
            public void onError(Throwable e, String msg, String code) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public static void printAbis(){
        Log.w("abis","Build.CPU_ABI 1:"+ Build.CPU_ABI);
        Log.w("abis","Build.CPU_ABI 2:"+ Build.CPU_ABI2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.w("abis","support abis:"+ Arrays.toString(Build.SUPPORTED_ABIS));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.w("abis","support abis 32:"+ Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.w("abis","support abis 64:"+ Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
        }
    }

    /**
     * 使用的时候调用
     * @param soName
     * @param url
     * @param callback
     */
    public static void check(String soName, String url, boolean onlyWifi, boolean showLoading,String md5, final SoDownloadCallback callback){

        File file = new File(CUSTOM_SO_DIR,soName);
        if(file.exists()){
            try {
                LoadLibraryUtil.installNativeLibraryPath(app.getApplicationContext().getClassLoader(), new File(CUSTOM_SO_DIR));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            callback.onSuccess(file.getAbsolutePath());
            return;
        }
        //不存在,则下载
        downloader.download(onlyWifi, showLoading,md5, file.getAbsolutePath(),url,new SoDownloadCallback() {
            @Override
            public void onSuccess(String path) {
                try {
                    LoadLibraryUtil.installNativeLibraryPath(app.getApplicationContext().getClassLoader(), new File(CUSTOM_SO_DIR));
                    callback.onSuccess(path);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    callback.onError(throwable,throwable.getMessage(),"");
                }

            }

            @Override
            public void onError(Throwable e, String msg, String code) {
                callback.onError(e,msg,code);
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }
        });
    }





    /**
     * 加载 so 文件(直接指定你so下载的路径即可)
     *
     * @param fromPath 下载的so，存放到sdcard的目录，拷贝私有目录，非必须，但是建议直接下载到私有目录路，这样做，不需要读写sdcard，
     */
    /*public static void loadSoFile(Context context, String fromPath) {
        try {
            //File dir = new File(fromPath);
            File dir = context.getDir("libs", Context.MODE_PRIVATE);
            if (!isLoadSoFile(dir)) {
                copy(fromPath, dir.getAbsolutePath());
            }
            LoadLibraryUtil.installNativeLibraryPath(context.getApplicationContext().getClassLoader(), dir);
        } catch (Throwable throwable) {
            Log.e("dq", "loadSoFile error " + throwable.getMessage());
        }
    }*/

    /**
     * 判断 so 文件是否存在
     */
    public static boolean isLoadSoFile(File dir,String soName) {
        File[] currentFiles;
        currentFiles = dir.listFiles();
        boolean hasSoLib = false;
        if (currentFiles == null) {
            return false;
        }
        for (int i = 0; i < currentFiles.length; i++) {
            // TODO: 2019/3/15 补充加校验so的逻辑。
            if (currentFiles[i].getName().toLowerCase().contains(soName)) {
                hasSoLib = true;
            }
        }
        return hasSoLib;
    }

    /**
     * @param fromFile 指定的下载目录
     * @param toFile   应用的包路径
     * @return
     */
    public static int copy(String fromFile, String toFile) {
        File root = new File(fromFile);
        if (!root.exists()) {
            return -1;
        }
        //如果存在,则获取当前目录下的全部文件
        File[] currentFiles = root.listFiles();
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (currentFiles != null && currentFiles.length > 0) {
            //遍历要复制该目录下的全部文件
            for (File currentFile : currentFiles) {
                if (currentFile.isDirectory()) {
                    //如果当前项为子目录 进行递归
                    copy(currentFile.getPath() + "/", toFile + currentFile.getName() + "/");
                } else {
                    //如果当前项为文件则进行文件拷贝
                    if (currentFile.getName().endsWith(".so")) {
                        int id = copySdcardFile(currentFile.getPath(), toFile + File.separator + currentFile.getName());
                    }
                }
            }
        }
        return 0;
    }


    /**
     * 文件拷贝,要复制的目录下的所有非子目录(文件夹)文件拷贝
     *
     * @param fromFile 源文件路径
     * @param toFile   目标文件路径
     * @return
     */
    public static int copySdcardFile(String fromFile, String toFile) {
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fosfrom.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 从内存到写入到具体文件
            fosto.write(baos.toByteArray());
            // 关闭文件流
            baos.close();
            fosto.close();
            fosfrom.close();
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

}
