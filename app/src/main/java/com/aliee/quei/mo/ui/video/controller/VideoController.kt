package com.aliee.quei.mo.ui.video.controller

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.annotation.AttrRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.dueeeke.videocontroller.component.CompleteView
import com.dueeeke.videocontroller.component.ErrorView
import com.dueeeke.videocontroller.component.LiveControlView
import com.dueeeke.videocontroller.component.TitleView
import com.dueeeke.videoplayer.controller.GestureVideoController
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoViewManager
import com.dueeeke.videoplayer.util.PlayerUtils
import com.aliee.quei.mo.R
import com.aliee.quei.mo.ui.video.view.GestureView
import com.aliee.quei.mo.ui.video.view.PrepareView
import com.aliee.quei.mo.ui.video.view.VodControlView


open class VideoController @JvmOverloads constructor(@NonNull context: Context?, @Nullable attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : GestureVideoController(context!!, attrs, defStyleAttr), View.OnClickListener {
    private var mLockButton: ImageView? = null
    private var mLoadingProgress: ProgressBar? = null
    override fun getLayoutId(): Int {
        return R.layout.video_layout_controller
    }

    override fun initView() {
        super.initView()
        mLockButton = findViewById(R.id.lock)
        mLockButton!!.setOnClickListener(this)
        mLoadingProgress = findViewById(R.id.loading)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.lock) {
            mControlWrapper.toggleLockState()
        }
    }

    override fun onLockStateChanged(isLocked: Boolean) {
        if (isLocked) {
            mLockButton!!.isSelected = true
            Toast.makeText(context, R.string.dkplayer_locked, Toast.LENGTH_SHORT).show()
        } else {
            mLockButton!!.isSelected = false
            Toast.makeText(context, R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation) {
        if (mControlWrapper.isFullScreen) {
            if (isVisible) {
                if (mLockButton!!.visibility == View.GONE) {
                    mLockButton!!.visibility = View.VISIBLE
                    if (anim != null) {
                        mLockButton!!.startAnimation(anim)
                    }
                }
            } else {
                mLockButton!!.visibility = View.GONE
                if (anim != null) {
                    mLockButton!!.startAnimation(anim)
                }
            }
        }
    }

   override fun showNetWarning(): Boolean {
        // super.showNetWarning()
    /*val showNetWarning=    (PlayerUtils.getNetworkType(context) == PlayerUtils.NETWORK_MOBILE
                && !VideoViewManager.instance().playOnMobileNetwork())*/
        return false
    }
    override fun onPlayerStateChanged(playerState: Int) {
        super.onPlayerStateChanged(playerState)
        when (playerState) {
            VideoView.PLAYER_NORMAL -> {
                layoutParams = LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                mLockButton!!.visibility = View.GONE
            }
            VideoView.PLAYER_FULL_SCREEN -> if (isShowing) {
                mLockButton!!.visibility = View.VISIBLE
            } else {
                mLockButton!!.visibility = View.GONE
            }
        }
        if (mActivity != null && hasCutout()) {
            val orientation = mActivity!!.requestedOrientation
            val dp24 = PlayerUtils.dp2px(context, 24f)
            val cutoutHeight = cutoutHeight
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                val lblp = mLockButton!!.layoutParams as LayoutParams
                lblp.setMargins(dp24, 0, dp24, 0)
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                val layoutParams = mLockButton!!.layoutParams as LayoutParams
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0)
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                val layoutParams = mLockButton!!.layoutParams as LayoutParams
                layoutParams.setMargins(dp24, 0, dp24, 0)
            }
        }
    }

    override fun onPlayStateChanged(playState: Int) {
        super.onPlayStateChanged(playState)
        when (playState) {
            VideoView.STATE_IDLE -> {
                mLockButton!!.isSelected = false
                mLoadingProgress!!.visibility = View.GONE
            }
            VideoView.STATE_PLAYING, VideoView.STATE_PAUSED, VideoView.STATE_PREPARED, VideoView.STATE_ERROR, VideoView.STATE_BUFFERED -> mLoadingProgress!!.visibility = View.GONE
            VideoView.STATE_PREPARING, VideoView.STATE_BUFFERING -> mLoadingProgress!!.visibility = View.VISIBLE
            VideoView.STATE_PLAYBACK_COMPLETED -> {
                mLoadingProgress!!.visibility = View.GONE
                mLockButton!!.visibility = View.GONE
                mLockButton!!.isSelected = false
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (isLocked) {
            show()
            Toast.makeText(context, R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show()
            return true
        }
        return if (mControlWrapper.isFullScreen) {
            stopFullScreen()
        } else super.onBackPressed()
    }
}