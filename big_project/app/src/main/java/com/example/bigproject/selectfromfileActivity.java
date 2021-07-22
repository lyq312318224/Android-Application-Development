package com.example.bigproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.bigproject.model.UploadAPI;
import com.example.bigproject.model.Uploadresponce;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class selectfromfileActivity extends AppCompatActivity {
    String TAG = "selectfromfileActivity";
    private int statecover;
    private int statevideo;
    private UploadAPI api;
    private Uri coverImageUri;
    private Uri videoUri;
    ProgressDialog progressDialog;
    byte[] videodata;
    byte[] coverimagedata;
    private SurfaceView mSurfaceView;
    private RelativeLayout mParent;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mHolder;
    private Intent intent;

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(selectfromfileActivity.this);
        progressDialog.setIndeterminate(false);//循环滚动
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("上传中...");
        progressDialog.setCancelable(false);//false不能取消显示，true可以取消显示
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectfromfile);

        statevideo=0;
        statecover=0;
        Button btncover = findViewById(R.id.coverselectbtn);
        Button btnvideo = findViewById(R.id.videoselectbtn);
        Button btnsubmit = findViewById(R.id.submit_selected);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statecover==1&&statevideo==1){
                    progressDialog.show();
                    initNetwork();
                    submit();
                }
                else if(statevideo==0){
                    Toast toast=Toast.makeText(getApplicationContext(), "未选择视频", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(statecover==0){
                    Toast toast=Toast.makeText(getApplicationContext(), "未选择封面", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Toast toast=Toast.makeText(getApplicationContext(), "未选择视频与封面", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        btncover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//这里是设置打开文件的类型，也可以换成图片
                startActivityForResult(intent,1);
            }
        });
        btnvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");//这里是设置打开文件的类型，也可以换成图片
                startActivityForResult(intent,2);
            }
        });
    }
    private void initData() {


        initProgressDialog();
        Log.d("aaa","2");

        intent=getIntent();
        mParent = findViewById(R.id.test_parent_play);
        mMediaPlayer = new MediaPlayer();
        mSurfaceView = findViewById(R.id.test_surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.setKeepScreenOn(true);


        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //开始播放
                Log.d("aaa","3");
                    readyPlay();


            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                Log.d("TAG","changed");
                changeVideoSize();
            }
        });
    }
    public void changeVideoSize() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();

        int surfaceWidth = mSurfaceView.getWidth();
        int surfaceHeight = mSurfaceView.getHeight();

        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) surfaceHeight);
        } else {
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth / (float) surfaceHeight), (float) videoHeight / (float) surfaceWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, mParent.getId());
        mSurfaceView.setLayoutParams(params);
    }

    //准好播放了
    public void readyPlay()  {
        Log.d("aaa","4");
        String url=intent.getStringExtra("URI");
        Log.d("AAA",""+url);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(this, videoUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.setLooping(true);
        // 把视频画面输出到SurfaceView
        mMediaPlayer.setDisplay(mHolder);
        // 通过异步的方式装载媒体资源
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //装载完毕回调
                play();
            }
        });
    }
    private void play() {
        Log.d("sssss","intoplay");
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }
    private void selectcover(@Nullable Intent data){
        coverImageUri = data.getData();
        ImageView imageView=findViewById(R.id.coverfromfile);
        imageView.setImageURI(coverImageUri);
    }

    private void selectvideo(@Nullable Intent data){

        videoUri = data.getData();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null) return;
        if(requestCode==1){
            selectcover(data);
            statecover=1;
        }
        else if(requestCode==2){
            selectvideo(data);
            statevideo=1;
        }
    }
    public void initNetwork(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UploadAPI.class);
    }

    public void submit(){
        coverimagedata = readDataFromUri(coverImageUri);
        videodata = readDataFromUri(videoUri);
        Log.d(TAG, "submit: "+videodata.length/1000000+"MB");
        MultipartBody.Part coverimage=MultipartBody.Part.createFormData("cover_image", "cover.png", RequestBody.create(MediaType.parse("multipart/form-data"), coverimagedata));
        MultipartBody.Part covervideo=MultipartBody.Part.createFormData("video", "video.mp4", RequestBody.create(MediaType.parse("multipart/form-data"), videodata));
        Call<Uploadresponce> call = api.submit(Constants.STUDENT_ID, Constants.USER_NAME, "", coverimage, covervideo, Constants.token);
        call.enqueue(new Callback<Uploadresponce>() {
            @Override
            public void onResponse(Call<Uploadresponce> call, Response<Uploadresponce> response) {
                progressDialog.dismiss();
                Toast toast=Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<Uploadresponce> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
