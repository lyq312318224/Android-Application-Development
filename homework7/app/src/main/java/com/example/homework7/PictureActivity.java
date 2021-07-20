package com.example.homework7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
public class PictureActivity extends AppCompatActivity {
    String url="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg95.699pic.com%2Fphoto%2F40118%2F2695.gif_wh300.gif%21%2Fgifto%2Ftrue&refer=http%3A%2F%2Fimg95.699pic.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1629356939&t=5236976c7a829909a4c4828b62d8c128";
    String errorurl="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01678e5becdc0da80121ab5d2da1fd.jpg%402o.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1629355293&t=e28fe1ed90b76fad6ab3afbb1dc0f5d6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Button btn3=findViewById(R.id.btn3);
        Button btn4=findViewById(R.id.btn4);
        Button btn5=findViewById(R.id.btn5);
        final ImageView im=findViewById(R.id.im);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(PictureActivity.this).load(url).placeholder(R.drawable.loading).error(R.drawable.error).into(im);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(PictureActivity.this).load(errorurl).placeholder(R.drawable.loading).error(R.drawable.error).into(im);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawableCrossFadeFactory drawableCrossFadeFactory=new DrawableCrossFadeFactory.Builder(2000).setCrossFadeEnabled(true).build();

                Glide.with(PictureActivity.this).load(url).placeholder(R.drawable.loading).error(R.drawable.error).apply(RequestOptions.bitmapTransform(new RoundedCorners(100))).transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(im);
            }
        });
    }
}
