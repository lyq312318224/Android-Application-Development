package com.example.bigproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vincent.videocompressor.VideoCompress;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class zipvideo extends AppCompatActivity {
    private static final int REQUEST_FOR_VIDEO_FILE = 1000;
    private TextView tv_input, tv_output, tv_indicator, tv_progress;
    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    private String inputPath;
    private String outputPath;

    private ProgressBar pb_compress;

    private long startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        inputPath=intent.getStringExtra("path");
        outputPath=getOutputMediaPath();
        Log.d("4324____", "onCreate: "+ inputPath+"  "+outputPath);
        initView();
    }


    private void initView() {
                String destPath = getOutputMediaPath();
                Log.d("()((((((", "onClick: "+destPath+" | "+getOutputMediaPath());
                VideoCompress.compressVideoLow(inputPath, destPath, new VideoCompress.CompressListener() {
                    @Override
                    public void onStart() {
                        startTime = System.currentTimeMillis();
                        Util.writeFile(zipvideo.this, "Start at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()) + "\n");
                    }

                    @Override
                    public void onSuccess() {
                        Intent intent= getIntent();
                        intent.putExtra("returnpath", destPath);
                        endTime = System.currentTimeMillis();
                        Util.writeFile(zipvideo.this, "End at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()) + "\n");
                        Util.writeFile(zipvideo.this, "Total: " + ((endTime - startTime)/1000) + "s" + "\n");
                        Util.writeFile(zipvideo.this);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO 把压缩好的路径传回去更新
                                Log.d("test", "run:&&&& complete");
                            }
                        });
                    }

                    @Override
                    public void onFail() {
                        endTime = System.currentTimeMillis();
                        Util.writeFile(zipvideo.this, "Failed Compress!!!" + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
                    }

                    @Override
                    public void onProgress(float percent) {
                    }
                });
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "VID_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }

        return sysLocale;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config){
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config){
        return config.getLocales().get(0);
    }
}
