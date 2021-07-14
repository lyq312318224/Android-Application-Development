package com.example.chapter3.homework;


import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;



public class PlaceholderFragment extends Fragment {
    private LottieAnimationView lottie;
    private RecyclerView rv1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件


        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        lottie=view.findViewById(R.id.animation_view);
        rv1=view.findViewById(R.id.rv);
        rv1.setLayoutManager(new LinearLayoutManager(getContext() ));
        rv1.setAdapter(new LinearAdapter(TestDataSet.getData()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator valueAnimator=ValueAnimator.ofFloat(1f,0);
                valueAnimator.setRepeatCount(0);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.setDuration(1000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        lottie.setAlpha((float)valueAnimator.getAnimatedValue());
                        rv1.setAlpha(1f-(float)valueAnimator.getAnimatedValue());
                    }
                });

                valueAnimator.start();
                // 这里会在 5s 后执行

                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
            }
        }, 5000);
    }


}
