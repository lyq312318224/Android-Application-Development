package com.example.hm2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

public class LinearRecyclerViewActivity extends AppCompatActivity {
    private RecyclerView rv1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_recycler_view);

        rv1=findViewById(R.id.rv);
        List<TestData> n=new LinkedList<>();

        rv1.setLayoutManager(new LinearLayoutManager(LinearRecyclerViewActivity.this));
        rv1.setAdapter(new LinearAdapter(TestDataSet.getData()));
    }
}
