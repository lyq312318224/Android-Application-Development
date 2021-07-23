package com.example.bigproject;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;

public class VideoPlayActivity extends AppCompatActivity {
    String TAG ="VideoPlay";
    private MediaPlayer mMediaPlayer;
    private RelativeLayout mParent;
    private SurfaceHolder mHolder;
    private SurfaceView mSurfaceView;
    private LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getApplicationContext().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.partscreen);
        }
        else{
            setContentView(R.layout.fullscreen);

            //videoView = findViewById(R.id.videoViewplay);
            //videoView.setVideoURI(Uri.parse(url));
            //videoView.setMediaController(new MediaController(VideoPlayActivity.this));
        }
        lottie=findViewById(R.id.animation_view);
        Intent intent =getIntent();
        String url=intent.getStringExtra("url");
        Log.d(TAG, "onCreate: "+url);
        mParent=findViewById(R.id.mParent);
        //videoView = findViewById(R.id.videoViewplay);
        //videoView.setVideoURI(Uri.parse(url));
        //videoView.setMediaController(new MediaController(VideoPlayActivity.this));
        mSurfaceView = findViewById(R.id.test_surfaceView);

        mMediaPlayer = new MediaPlayer();
        mHolder =mSurfaceView.getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //开始播放
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mMediaPlayer.setDataSource(VideoPlayActivity.this, Uri.parse(url));
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
                        ValueAnimator valueAnimator=ValueAnimator.ofFloat(1f,0);
                        valueAnimator.setRepeatCount(0);
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.setDuration(1000);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                lottie.setAlpha((float)valueAnimator.getAnimatedValue());
                            }
                        });

                        valueAnimator.start();
                        play();
                    }
                });
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
    private void play() {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }
    public void changeVideoSize() {
        int videoWidth =mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();

        int surfaceWidth =mSurfaceView.getWidth();
        int surfaceHeight =mSurfaceView.getHeight();

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

//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.btn_full_video:
//
//                if (ISFULLSCREEN) { // 全屏转半屏
//
//                    ISFULLSCREEN = false;
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                            dp2px(MainActivity.this, 200));
//                    fl_group.setLayoutParams(lp);
//
//                } else { // 非全屏切换全屏
//                    ISFULLSCREEN = true;
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 手动横屏
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                            RelativeLayout.LayoutParams.MATCH_PARENT);
//                    fl_group.setLayoutParams(lp);
//                }
//
//                break;
//
//            case R.id.btn_video_back:
//
//                if (ISFULLSCREEN) { // 全屏转半屏
//
//                    ISFULLSCREEN = false;
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                            dp2px(MainActivity.this, 200));
//                    fl_group.setLayoutParams(lp);
//
//                }
//                break;
//            case R.id.btn_play_video:
//                if(ISPLAYING&&mMediaPlayer!=null){   //视频的播放与暂停
//                    mMediaPlayer.pause();
//                    ISPLAYING=false;
//
//                    btn_play_video.setBackground(getResources().getDrawable(R.drawable.icon_play));
//
//                }else{
//
//                    btn_play_video.setBackground(getResources().getDrawable(R.drawable.icon_topause));
//                    mMediaPlayer.start();
//                    ISPLAYING=true;
//
//                }
//
//                break;
//
//            default:
//                break;
//        }
//    }
}
