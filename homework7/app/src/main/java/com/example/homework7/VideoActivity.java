package com.example.homework7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;


public class VideoActivity extends AppCompatActivity  {
    String uri="https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    int process = 0;
    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!VideoActivity.this.isDestroyed()) {
                try {
                    VideoView videoView = (VideoView) findViewById(R.id.video);
                    process = videoView.getCurrentPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    private OrientationEventListener mOrientationEventListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationEventListener.disable();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        VideoView video=findViewById(R.id.video);
        video.setMediaController(new MediaController(this));
        initView();
        initVideoView();
        startOrientationEventListener();
    }
    private void initVideoView() {
        VideoView videoView = (VideoView) findViewById(R.id.video);
        videoView.setVideoURI(Uri.parse(uri));
        videoView.seekTo(process);
        videoView.start();
    }
    private void initView() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.button_land).setVisibility(View.VISIBLE);

            findViewById(R.id.button_land).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

                }
            });

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            findViewById(R.id.button_land).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initView();
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }
    private void startOrientationEventListener() {
        mOrientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (30 < orientation && orientation < 50) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }
            }
        };
        mOrientationEventListener.enable();
    }
}
