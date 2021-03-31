package com.aliee.quei.mo.ui.video.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.aliee.quei.mo.R;

public class VideoTimeView extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;

    private TextView tv_video_times;

    public VideoTimeView(@NonNull Context context) {
        super(context);
    }

    public VideoTimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoTimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VideoTimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.player_layout_video_time_view, this, true);
        tv_video_times = findViewById(R.id.tv_video_times);
    }

    public void setTime(String time) {
        tv_video_times.setText(time);
    }





    @Override
    public void attach(ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {

    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }


}

