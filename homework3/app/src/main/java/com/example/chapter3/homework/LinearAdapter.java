package com.example.chapter3.homework;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
        private View contentView;
        private ImageView imageView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView=itemView;
            tvIndex=itemView.findViewById(R.id.tv_index);
            tvTitle=itemView.findViewById(R.id.tv_title);
            imageView=itemView.findViewById(R.id.p1);
        }
        public int getResourceId(String imageName) {
            Field field = null;
            try {
                field = R.drawable.class.getDeclaredField(imageName);
                int resId = field.getInt(field.getName());
                return resId;

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return -1;
        }
        public void onBind(int position,TestData data){
            tvIndex.setText(data.message);
            tvTitle.setText(data.title);
            tvTitle.setTextColor(Color.parseColor("#000000"));
            imageView.setImageResource(getResourceId(data.picname));
        }
    }
}
