package com.example.bigproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.bigproject.model.Catchvideo;
import com.example.bigproject.model.MessageListResponse;
import com.example.bigproject.model.UploadAPI;
import com.example.bigproject.model.VideoReturnMessage;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements LinearAdapter.MyItemClickListener{
    private String TAG="MainActivity";
    private VideoView mVideoView;
    private Button mButton;
    private UploadAPI api;
    public Context context;
    private RecyclerView rv1;
    private LinearAdapter mAdapter;
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private final static int REQUEST_CODE_RECORD = 1002;
    private String mp4Path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        rv1=findViewById(R.id.rv);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshdata("");
            }
        }).start();
        ImageView imageView = findViewById(R.id.imgsearch);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView recyclerView=findViewById(R.id.rv);
                recyclerView.removeAllViews();
                EditText editText = findViewById(R.id.searchbar);
                String search = editText.getText().toString();
                Log.d(TAG, "onClick: "+search);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshdata(search);
                    }
                }).start();
            }
        });

    }
    public void refreshdata(String stuid){
        Catchvideo catchvideo = new Catchvideo();
        List<VideoReturnMessage> res = catchvideo.getdatafrominternet(stuid);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(rv1.getChildCount()>0){
                    rv1.removeAllViews();
                    Log.d("clear", "run: _+_+_+_+_+");
                }
                Log.d("test", "run: ()()()()"+res.size());
                mAdapter = new LinearAdapter(context, res);
                rv1.setAdapter(mAdapter);
                mAdapter.setItemClickListener(MainActivity.this::onItemClick);
            }
        });
    }

    public void record(View view) {
        requestPermission();
    }
    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            Log.d("aaaa","yes");
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() {


        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mp4Path=getOutputMediaPath();
        Log.d("地址",""+mp4Path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,PathUtils.getUriForFile(MainActivity.this,mp4Path));
        intent.putExtra (MediaStore.EXTRA_DURATION_LIMIT,10);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        Log.d("地址",""+mp4Path);
//        if (intent.resolveActivity(getPackageManager())!=null){
//            Log.d("aa","herehint");
//            startActivityForResult(intent,REQUEST_CODE_RECORD);
//        }
        startActivityForResult(intent,REQUEST_CODE_RECORD);
    }
    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            Log.d("文件名",""+"IMG_" + timeStamp + ".mp4");
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // todo 2.1 视频获取成功，播放视频
        if (requestCode==REQUEST_CODE_RECORD&&resultCode==RESULT_OK){
            Log.d("Result","hahaha");
            play();

        }
    }
    private void play(){
        //mVideoView.setVideoPath(mp4Path);
        //mVideoView.start();
        Intent intent=new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("URI", mp4Path);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, VideoReturnMessage data) {
        Log.d("ss","hint "+position+" "+data.getVideoUrl());
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra("url", data.getVideoUrl());
        startActivity(intent);
    }

    public void selectfromfile(View view) {
        Intent intent = new Intent(MainActivity.this, selectfromfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
