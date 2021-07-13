package com.example.hm2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> {
    private List<TestData>mDataset =new ArrayList<>();
    public LinearAdapter(List<TestData> myDataset){

        mDataset.addAll(myDataset);
    }
    @NonNull

    @Override
    public LinearAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_linear_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LinearAdapter.LinearViewHolder holder, int position) {
        holder.onBind(position,mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView tvIndex;
        private TextView tvTitle;
        private TextView tvHot;
        private View contentView;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView=itemView;
            tvIndex=itemView.findViewById(R.id.tv_index);
            tvTitle=itemView.findViewById(R.id.tv_title);
            tvHot=itemView.findViewById(R.id.tv_hot);
        }
        public void onBind(int position,TestData data){
            tvIndex.setText(new StringBuilder().append(position+1).append(".  ").toString());
            tvTitle.setText(data.title);
            tvHot.setText(data.hot);
            if(position<3){
                tvIndex.setTextColor(Color.parseColor("#FFD700"));
            }
            else {
                tvIndex.setTextColor(Color.parseColor("#FFFFFF"));
            }

        }
    }
}
