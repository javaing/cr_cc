package com.aliee.quei.mo.ui.video.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.aliee.quei.mo.R;

/**
 * 免费观看次数View
 */
public class TipsController extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;

    private TextView tv_tips;
    private TextView btn_recharge;

    public TipsController(@NonNull Context context) {
        super(context);
    }

    public TipsController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TipsController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TipsController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.player_layout_tips_view, this, true);
        tv_tips = findViewById(R.id.tv_tips);
        btn_recharge = findViewById(R.id.btn_recharge);
    }

    public void setTips(String price) {
        tv_tips.setText(price);
    }

    public void hideTipsView(){
        setVisibility(View.GONE);
    }

    public void goRecharge() {
        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
