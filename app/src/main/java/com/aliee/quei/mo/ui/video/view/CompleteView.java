package com.aliee.quei.mo.ui.video.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.aliee.quei.mo.R;
import com.aliee.quei.mo.component.CommonDataProvider;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class CompleteView extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;

    private final ImageView mStopFullscreen;

    private final TextView tv_replay_tips;
    private final TextView btn_register;
    private final TextView btn_replay;

    private boolean isPreview = false;

    public CompleteView(@NonNull Context context) {
        super(context);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.player_layout_complete_view, this, true);
        tv_replay_tips = findViewById(R.id.tv_replay_tips);
        btn_register = findViewById(R.id.btn_register);
        btn_replay = findViewById(R.id.btn_replay);
        findViewById(R.id.iv_replay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlWrapper.replay(true);
            }
        });
        findViewById(R.id.btn_replay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlWrapper.replay(true);
            }
        });
        mStopFullscreen = findViewById(R.id.stop_fullscreen);
        mStopFullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mControlWrapper.isFullScreen()) {
                    Activity activity = PlayerUtils.scanForActivity(getContext());
                    if (activity != null && !activity.isFinishing()) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mControlWrapper.stopFullScreen();
                    }
                }
            }
        });
        setClickable(true);
    }

    public View getRegisterView() {
        return btn_register;
    }

    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;

    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
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
        if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
          /*  setVisibility(VISIBLE);
            mStopFullscreen.setVisibility(mControlWrapper.isFullScreen() ? VISIBLE : GONE);
            bringToFront();
            boolean isTempUser = CommonDataProvider.Companion.getInstance().getUserTempType();
            if (isTempUser) {
                tv_replay_tips.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.VISIBLE);
            } else {
                tv_replay_tips.setVisibility(View.GONE);
                btn_register.setVisibility(View.GONE);
            }*/
            mStopFullscreen.setVisibility(mControlWrapper.isFullScreen() ? VISIBLE : GONE);
            if (!isPreview) {
                mControlWrapper.replay(true);
            }
            bringToFront();
        }else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            mStopFullscreen.setVisibility(VISIBLE);
        } else if (playerState == VideoView.PLAYER_NORMAL) {
            mStopFullscreen.setVisibility(GONE);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                sflp.setMargins(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                sflp.setMargins(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                sflp.setMargins(0, 0, 0, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }
}
