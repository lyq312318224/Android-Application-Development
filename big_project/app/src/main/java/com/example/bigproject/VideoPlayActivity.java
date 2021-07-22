package com.example.bigproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    String TAG ="VideoPlay";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getApplicationContext().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.partscreen);
        }
        else{
            setContentView(R.layout.fullscreen);
        }
        Intent intent =getIntent();
        String url=intent.getStringExtra("url");
        Log.d(TAG, "onCreate: "+url);
        VideoView videoView = findViewById(R.id.videoViewplay);
        videoView.setVideoURI(Uri.parse(url));
        videoView.setMediaController(new MediaController(VideoPlayActivity.this));
        videoView.start();

    }
}
