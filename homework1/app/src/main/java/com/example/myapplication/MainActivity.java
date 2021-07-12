package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn1=findViewById(R.id.btn1);
        final TextView tv1=findViewById(R.id.tv1);
        final ImageView im1=findViewById(R.id.im1);
        final EditText ed1=findViewById(R.id.ed1);
        final CheckBox cb1=findViewById(R.id.cb1);
        final RatingBar rb1=findViewById(R.id.rb1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1.setText("What a wonderful world!");
                rb1.setNumStars(8);
                Log.d( "MainActivity","hint");
            }
        });

    }
}
