package com.aliee.quei.mo.ui.video.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.aliee.quei.mo.R;
import com.aliee.quei.mo.data.bean.Freetime;
import com.aliee.quei.mo.data.bean.VideoInfo;

import static com.dueeeke.videoplayer.util.PlayerUtils.stringForTime;

public class PrepareView extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;

    private ImageView mThumb, iv_blue_thumb;
    private ImageView mStartPlay;
    private ProgressBar mLoading;
    private FrameLayout mNetWarning;
    private LinearLayout mLayoutTips;
    private TextView mBtnRecharge;
    private TextView mTvTips, mTvFreeCountTips, tv_video_times;
    private FrameLayout ll_preview_end_view;
    private boolean isShowTips;
    private boolean isPreview = false;

    public PrepareView(@NonNull Context context) {
        super(context);
    }

    public PrepareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PrepareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.player_layout_prepare_view, this, true);
        mThumb = findViewById(R.id.thumb);
        iv_blue_thumb = findViewById(R.id.iv_blue_thumb);
        mStartPlay = findViewById(R.id.start_play);
        mLoading = findViewById(R.id.loading);
        mNetWarning = findViewById(R.id.net_warning_layout);
        mLayoutTips = findViewById(R.id.layout_tips);
        mBtnRecharge = findViewById(R.id.btn_recharge);
        mTvTips = findViewById(R.id.tv_tips);
        mTvFreeCountTips = findViewById(R.id.tv_free_count_tips);
        ll_preview_end_view = findViewById(R.id.ll_preview_end_view);
        tv_video_times = findViewById(R.id.tv_video_times);
        findViewById(R.id.status_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetWarning.setVisibility(GONE);
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                mControlWrapper.start();
            }
        });
    }

    public void showTipsView(boolean isShow) {
        this.isShowTips = isShow;
    }

    public void setTipsText(String price) {
        mTvTips.setText("免费观看已用完，金币剩余" + price + "" +
                "\n如需继续观看请充值喔");
    }

    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;
        Log.d("tag", "isssssssPreview:" + isPreview);

    }

    public void setVideoTimes(int times) {
        tv_video_times.setText(times + "");
    }

    public void setBackgroundColor(int color) {
        mThumb.setBackgroundColor(color);
    }

    public void setFreeCount(Freetime freeTime) {
        if (freeTime.getUse() != 0 && freeTime.getCount() == 0) {
            mTvFreeCountTips.setVisibility(View.GONE);
        } else {
            if (freeTime.getCount() == 0) {
                mTvFreeCountTips.setVisibility(View.GONE);
            } else {
                mTvFreeCountTips.setText("剩余免费观看次数" + freeTime.getCount() + "次");
                mTvFreeCountTips.setVisibility(View.VISIBLE);
            }
        }
    }


    public void hideTipsView() {
        Log.d("hideTipsView", "hideTipsView");
        mLayoutTips.setVisibility(View.GONE);
    }

    public void hideFreeCountTips() {
        mTvFreeCountTips.setVisibility(View.GONE);
    }

    public View getRecharge() {
        return mBtnRecharge;
    }


    /**
     * 设置点击此界面开始播放
     */
    public void setClickStart() {
        setOnClickListener(v -> mControlWrapper.start());
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
        Log.d("TAG", "PrepareView,onPlayStateChanged:" + playState);
        switch (playState) {
            case VideoView.STATE_PREPARING:
                ll_preview_end_view.setVisibility(View.GONE);
                mLayoutTips.setVisibility(View.GONE);
                bringToFront();
                setVisibility(VISIBLE);
                mStartPlay.setVisibility(View.GONE);
                mNetWarning.setVisibility(GONE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
                setVisibility(GONE);
                mTvFreeCountTips.setVisibility(View.GONE);
                mLayoutTips.setVisibility(View.GONE);
                ll_preview_end_view.setVisibility(View.GONE);
                break;
            case VideoView.STATE_PAUSED:
                mStartPlay.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_ERROR:
                if (isShowTips) {
                    mLayoutTips.setVisibility(View.VISIBLE);
                } else {
                    mLayoutTips.setVisibility(View.GONE);
                }
                ll_preview_end_view.setVisibility(View.GONE);
                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                mLoading.setVisibility(View.GONE);
                setVisibility(VISIBLE);
                mThumb.setVisibility(View.VISIBLE);
                showPreviewEndView();
                break;
            case VideoView.STATE_IDLE:
                //showPreviewEndView();
                isPreview = false;
                ll_preview_end_view.setVisibility(View.GONE);
                mTvFreeCountTips.setVisibility(View.GONE);
                mLayoutTips.setVisibility(View.GONE);
                setVisibility(VISIBLE);
                bringToFront();
                mLoading.setVisibility(View.GONE);
                mNetWarning.setVisibility(GONE);
                mStartPlay.setVisibility(View.VISIBLE);
                mThumb.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_START_ABORT:
                /*setVisibility(VISIBLE);
                mNetWarning.setVisibility(VISIBLE);
                mNetWarning.bringToFront();*/
                break;
        }
    }

    private void showPreviewEndView() {
        Log.d("tag", "isPreview:" + isPreview);
        if (isPreview) {
            mStartPlay.setVisibility(View.VISIBLE);
            ll_preview_end_view.setVisibility(View.VISIBLE);
        } else {
            mStartPlay.setVisibility(View.GONE);
            ll_preview_end_view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {
        if (tv_video_times != null)
            tv_video_times.setText(stringForTime(duration));
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
