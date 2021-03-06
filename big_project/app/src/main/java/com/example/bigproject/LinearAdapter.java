package com.example.bigproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.bigproject.model.VideoReturnMessage;


public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> {
    private List<VideoReturnMessage>mDataset =new ArrayList<>();
    private MyItemClickListener mItemClickListener;
    private Context mcontext;

    public LinearAdapter(Context mcontext, List<VideoReturnMessage> myDataset){
        this.mcontext=mcontext;
        mDataset.addAll(myDataset);
    }
    @NonNull

    @Override
    public LinearAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LinearAdapter.LinearViewHolder holder,final int position) {
        final LinearViewHolder m1=(LinearViewHolder) holder;
        m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position, mDataset.get(position));
                }
            }
        });
        holder.onBind(position,mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private MyItemClickListener mListener;
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

        public void onBind(int position, VideoReturnMessage data){
            tvIndex.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.getCreatedAt()));
            tvTitle.setText(data.getUsername());
            tvTitle.setTextColor(Color.parseColor("#000000"));
            Glide.with(mcontext).load(data.getImageUrl()).placeholder(R.drawable.load1).transition(new DrawableTransitionOptions().crossFade()).into(imageView);
//            imageView.setImageResource(getResourceId(data.getImageUrl()));
        }

        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }
    }
//    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private MyItemClickListener mListener;
//
//        public ViewHolder(View itemView, MyItemClickListener myItemClickListener) {
//            super(itemView);
//            //?????????????????????????????????
//            this.mListener = myItemClickListener;
//            itemView.setOnClickListener(this);
//        }
//
//        /**
//         * ??????OnClickListener?????????????????????
//         * @param v
//         */
//        @Override
//        public void onClick(View v) {
//            if (mListener != null) {
//                mListener.onItemClick(v, getPosition());
//            }
//
//        }
//    }

    /**
     * ????????????????????????
     */
    public interface MyItemClickListener {
        void onItemClick(int position,VideoReturnMessage data);
    }

    /**
     * ???activity??????adapter???????????????????????????,?????????????????????????????????,???????????????????????????
     *
     * @param myItemClickListener
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }


}