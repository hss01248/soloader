package com.hss01248.soloaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hss01248.soloader.SoDownloadCallback;
import com.hss01248.soloader.SoFileLoadManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    private void load() {
        SoFileLoadManager.printAbis();
        SoFileLoadManager.check("libopencv_java3.so",
                "https://github.com/aayazkhan/Android-Face-Recognition/blob/master/opencv/src/main/jniLibs/armeabi-v7a/libopencv_java3.so?raw=true",
                false, true, "", new SoDownloadCallback() {
                    @Override
                    public void onSuccess(String path) {
                        try {
                            System.loadLibrary("opencv_java3");
                        }catch (Throwable e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e, String msg, String code) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onCancel() {
                        Log.w("dd","cancel");
                    }
                });
    }
}
