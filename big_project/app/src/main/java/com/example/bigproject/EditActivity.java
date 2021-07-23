package com.example.bigproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.bigproject.model.UploadAPI;
import com.example.bigproject.model.Uploadresponce;
import com.vincent.videocompressor.VideoCompress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, MyAdapterpic.IOnItemClickListener {
    private VideoView mVideoView;
    private String mp4Path;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mHolder;
    private RelativeLayout mParent;
    private Button mBtnsubmit;
    private Intent intent;
    private int posbefore;
    private UploadAPI api;
    private Uri coverImageUri;
    private Uri videoUri;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog_zip;
    byte[] videodata;
    byte[] coverimagedata;
    String TAG = "videosel";
    int state;
    Thread myThread;
    testStop testStop;

    RecyclerView rv1;
    List<picdata> mDataList = new ArrayList<>();
    MyAdapterpic myAdaptor;
    private SeekBar sb_main_bar;
    private ImageButton imageButton;
    private Button mBtnPlay;

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setIndeterminate(false);//循环滚动
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("上传中...");
        progressDialog.setCancelable(false);//false不能取消显示，true可以取消显示
    }

    private void initProgressDialog_zip() {
        progressDialog_zip = new ProgressDialog(EditActivity.this);
        progressDialog_zip.setIndeterminate(false);//循环滚动
        progressDialog_zip.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog_zip.setMessage("压缩中...");
        progressDialog_zip.setCancelable(false);//false不能取消显示，true可以取消显示
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        posbefore = -1;
        initProgressDialog();
        initProgressDialog_zip();
        intent = getIntent();
        state = intent.getIntExtra("state", 0);
        Log.d(TAG, "onCreate: %%%%" + intent.getStringExtra("URI"));
        videoUri = FileProvider.getUriForFile(this, "com.example.bigproject.fileprovider", new File(intent.getStringExtra("URI")));
        String inputpath = intent.getStringExtra("URI");
        String destPath = getOutputMediaPath();
        Log.d(TAG, "onCreate:path* " + inputpath + " " + destPath);
        Button btnn = findViewById(R.id.zip_video_btn);
        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog_zip.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VideoCompress.compressVideoLow(inputpath, destPath, new VideoCompress.CompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess() {
                                progressDialog_zip.dismiss();
                                intent.putExtra("URI", destPath);
                                intent.putExtra("state", 1);
                                startActivity(intent);

//                                 videoUri = FileProvider.getUriForFile(EditActivity.this, "com.example.bigproject.fileprovider", new File(destPath));
//                                myThread.stop();
//                                mMediaPlayer.release();
//                                 fun();
                            }

                            @Override
                            public void onFail() {
                            }

                            @Override
                            public void onProgress(float percent) {
                            }
                        });

                    }
                }).start();

            }
        });
        fun();
    }
    //TODO？？？？？？

    private void fun() {
        videodata = readDataFromUri(videoUri);
        Log.d(TAG, "submit: " + videodata.length / 1000 + "KB||||" + videoUri);
        TextView textView = findViewById(R.id.filesize);
        textView.setText("视频大小：" + videodata.length / 1000 + "KB");
        if (videodata.length / 1000000 > 30) {
            Button button = findViewById(R.id.test_btn_play);
            button.setVisibility(View.INVISIBLE);
            textView.setText("视频大小：" + videodata.length / 1000 + "KB, 过大！请重新选择或压缩！");
        }
        Log.d(TAG, "onCreate: ********" + videoUri);
//        mp4Path = intent.getStringExtra("VideoPath");
//        mVideoView.setVideoPath(mp4Path);
//        mVideoView.start();
        initData();
        initNetwork();

        rv1 = findViewById(R.id.recyclerviewsel);
        rv1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getcover(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            data.getStringExtra("returnpath");

        }
    }

    private void getcover(Intent intent) {
        String tmp = intent.getStringExtra("URI");
        findViewById(R.id.recyclerviewsel).setAlpha(0f);
        findViewById(R.id.progressBarsel).setAlpha(1f);
        ProgressBar progressBar = findViewById(R.id.progressBarsel);
        progressBar.setProgress(0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getpic(tmp);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        myAdaptor = new MyAdapterpic(mDataList);
                        myAdaptor.setmItemClickListener(EditActivity.this::onItemCLick);
                        rv1.setAdapter(myAdaptor);
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
                        valueAnimator.setDuration(1000);
                        valueAnimator.setRepeatCount(0);
                        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ProgressBar progressBar = findViewById(R.id.progressBarsel);
                                progressBar.setProgress(100);
                                progressBar.setAlpha((float) valueAnimator.getAnimatedValue());
                                rv1.setAlpha(1f - (float) valueAnimator.getAnimatedValue());
                            }
                        });
                        valueAnimator.start();
                    }
                });
            }
        });
        thread.start();
    }

    public void getpic(String fileName) {
        Log.d(TAG, "getpic: " + fileName);
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(fileName);

        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.d(TAG, "duration = " + duration);
        int durationMs = Integer.parseInt(duration);

        //每秒取一次
        Bitmap frameAtIndex = null;
        for (int i = 0; i < durationMs; i += 1000) {
            long start = System.nanoTime();
//            Log.d(TAG, "getFrameAtTime time = " + i);
            //这里传入的是ms
//            Log.d(TAG, "getpic:+++=== "+i*1000);
            frameAtIndex = metadataRetriever.getFrameAtTime(i * 1000, metadataRetriever.OPTION_CLOSEST);

            mDataList.add(new picdata(frameAtIndex));
            long end = System.nanoTime();
            long cost = end - start;
            Log.d(TAG, "cost time in millis = " + (cost * 1f / 1000000));
            ProgressBar progressBar = findViewById(R.id.progressBarsel);
            progressBar.setProgress(i * 100 / durationMs);
        }

        Log.d(TAG, "getpic: ()()" + mDataList.size());
        metadataRetriever.release();
    }

    private void initData() {
        mBtnsubmit = findViewById(R.id.test_btn_play);
        mSurfaceView = findViewById(R.id.test_surfaceView);
        mParent = findViewById(R.id.test_parent_play);
        mBtnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posbefore == -1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "未选择封面", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Log.d(TAG, "onClick: submit");
                    progressDialog.show();
                    submit();
                }

            }
        });
        mMediaPlayer = new MediaPlayer();
        mHolder = mSurfaceView.getHolder();
        mHolder.setKeepScreenOn(true);
        imageButton = findViewById(R.id.imagebutton);
        sb_main_bar = (SeekBar) findViewById(R.id.sb_main_bar);
        sb_main_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMediaPlayer != null && b) {
                    mMediaPlayer.seekTo(i);
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    int duration = mMediaPlayer.getDuration();
                    Log.d("423", "current---" + currentPosition + "--duration--" + duration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                //进度条在当前位置播放
                mMediaPlayer.seekTo(progress);
            }
        });

        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //开始播放
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
                Log.d("TAG", "changed");
                changeVideoSize();
            }
        });
    }

    public void initNetwork() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UploadAPI.class);
    }

    public void submit() {
        Log.d(TAG, "submit:#### " + videoUri + " " + coverImageUri);
        coverimagedata = readDataFromUri(coverImageUri);

        videodata = readDataFromUri(videoUri);

        Log.d(TAG, "submit: " + videodata.length / 1000000 + "MB");

        MultipartBody.Part coverimage = MultipartBody.Part.createFormData("cover_image", "cover.png", RequestBody.create(MediaType.parse("multipart/form-data"), coverimagedata));
        MultipartBody.Part covervideo = MultipartBody.Part.createFormData("video", "video.mp4", RequestBody.create(MediaType.parse("multipart/form-data"), videodata));
        Call<Uploadresponce> call = api.submit(Constants.STUDENT_ID, Constants.USER_NAME, "", coverimage, covervideo, Constants.token);
        call.enqueue(new Callback<Uploadresponce>() {
            @Override
            public void onResponse(Call<Uploadresponce> call, Response<Uploadresponce> response) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT);
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

    final Handler handler = new Handler();

    public class testStop implements Runnable {
        private boolean flag = true;

        @Override
        public void run() {
            int position;
            while ((sb_main_bar.getProgress() <= sb_main_bar.getMax())&&flag==true) {
                //获取音乐当前播放的位置

                position = mMediaPlayer.getCurrentPosition();
                sb_main_bar.setProgress(position);
            }
        }

        //自定义一个stop方法，采用标识
        public void stop() {
            this.flag = false;
        }
    }

    class MyThread extends Thread {
        @Override
        public void run() {

        }
    }

    //改变视频的尺寸自适应。
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
    public void readyPlay() {
        String url = intent.getStringExtra("URI");
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.setLooping(false);
        // 把视频画面输出到SurfaceView
        mMediaPlayer.setDisplay(mHolder);
        // 通过异步的方式装载媒体资源
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //装载完毕回调
//                if (mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.pause();
//                } else {
//                    mMediaPlayer.start();
//
//                }
                play();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("position:", "" + mMediaPlayer.getCurrentPosition());
                int fprogress = 0;
                sb_main_bar.setProgress(fprogress);
                ObjectAnimator animator4=ObjectAnimator.ofFloat(imageButton,"alpha",0f,1f);
                animator4.setRepeatMode(ValueAnimator.REVERSE);
                animator4.setDuration(200);
                animator4.setInterpolator(new LinearInterpolator());
                animator4.setRepeatCount(0);
                animator4.start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test_btn_play) {
            play();
        }
        if (v.getId() == R.id.test_surfaceView) {
            play();
        }
        if (v.getId() == R.id.imagebutton) {
            play();
        }
    }

    private void play() {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageButton, "alpha", 0f, 1f);
            animator4.setRepeatMode(ValueAnimator.REVERSE);
            animator4.setDuration(200);
            animator4.setInterpolator(new LinearInterpolator());
            animator4.setRepeatCount(0);
            animator4.start();
        } else {
            mMediaPlayer.start();
            ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageButton, "alpha", 1f, 0f);
            animator4.setRepeatMode(ValueAnimator.REVERSE);
            animator4.setDuration(200);
            animator4.setInterpolator(new LinearInterpolator());
            animator4.setRepeatCount(0);
            animator4.start();
            //获取音乐的总时长
            int duration = mMediaPlayer.getDuration();
            //将进度条设置最大值为：音乐的总时长
            sb_main_bar.setMax(duration);
            testStop = new testStop();
            myThread=new Thread(testStop);
            myThread.start();
        }
    }


    @Override
    public void onItemCLick(int position, picdata data) {
        coverImageUri = saveMyBitmap("", data.bitmap);
        Log.d(TAG, "onItemCLick: " + coverImageUri);
        View itemView = rv1.getLayoutManager().findViewByPosition(position);
        View viewbefore = null;
        if (posbefore != -1) {
            viewbefore = rv1.getLayoutManager().findViewByPosition(posbefore);
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 70f);
        animator.setDuration(500);
        View finalViewbefore = viewbefore;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (finalViewbefore != null) {
                    finalViewbefore.findViewById(R.id.imageitem).setTranslationY(-70f + animatedValue);
                }
                itemView.findViewById(R.id.imageitem).setTranslationY(-animatedValue);
            }
        });
        animator.start();
        posbefore = position;
        Log.d(TAG, "onItemCLick: " + position + " " + data.bitmap);
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
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }
    public Uri saveMyBitmap(String bitName,Bitmap mBitmap){
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");

        try {
            mediaFile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //  DebugMessage.put("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(mediaFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(this, "com.example.bigproject.fileprovider", new File(mediaFile.getAbsolutePath()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: 111111");
        testStop.stop();
        if(state==1){
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            mDataList.get(i).bitmap.recycle();
        }
        super.onDestroy();
    }

}


