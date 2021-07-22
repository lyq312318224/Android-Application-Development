package com.example.bigproject;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapterpic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<picdata> mDataset;
    private IOnItemClickListener mItemClickListener;
    public MyAdapterpic(List<picdata> m){
        mDataset = m;
    }
    public interface IOnItemClickListener {
        void onItemCLick(int position, picdata data);
    }
    public void setmItemClickListener(IOnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyHolder m1=(MyHolder)holder;
        m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, mDataset.get(position));
                }
            }
        });
        Log.d("hello", "onBindViewHolder: "+position);
        m1.onBind(position, mDataset.get(position));
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView image1;

        private View contentView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            contentView=itemView;
            image1=itemView.findViewById(R.id.imageitem);
        }

        public void onBind(int position, picdata data){
            Log.d("()()", "onBind: "+position);
            Bitmap bitmap1 = data.bitmap;
            if(bitmap1!=null) image1.setImageBitmap(bitmap1);
        }
        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }


    }

}
