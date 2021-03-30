package com.aliee.quei.mo.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.arch.lifecycle.Lifecycle
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.utils.extention.invisible
import com.aliee.quei.mo.utils.extention.show
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class BottomRewardPopup(private val activity: BaseActivity,private val amount : Int){
    private val WM = activity.windowManager
    private val imageView = ImageView(activity)
    private var imageWidth = 0
    private var imageHeight = 0
    private var windowHeight = 0
    private var windowWidth = 0
    private var params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0,
            0,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.RGBA_8888)

    init {
        imageView.setImageResource(R.mipmap.img_reward_bottom_anmi)
        windowWidth = activity.resources.displayMetrics.widthPixels
        windowHeight = activity.resources.displayMetrics.heightPixels
        imageView.measure(View.MeasureSpec.makeMeasureSpec(windowWidth,View.MeasureSpec.AT_MOST),View.MeasureSpec.makeMeasureSpec(windowHeight,View.MeasureSpec.AT_MOST))
        imageView.invisible()
        imageWidth = imageView.measuredWidth
        imageHeight = imageView.measuredHeight
    }
    fun show(){
        params.gravity = Gravity.LEFT or Gravity.TOP
        params.x = (windowWidth - imageWidth) / 2
        params.y = windowHeight
        WM.addView(imageView,params)
        animateIn()
    }

    private fun animateIn() {
        val animatorIn = ValueAnimator.ofInt(windowHeight,windowHeight - imageHeight)
        animatorIn.duration = 1000
        animatorIn.addUpdateListener {
            params.y = it.animatedValue as Int
            if(activity.isDestroyed || activity.isFinishing)return@addUpdateListener
            try {
                WM.updateViewLayout(imageView,params)
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
        animatorIn.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                Observable.timer(2,TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .bindUntilEvent(activity,Lifecycle.Event.ON_DESTROY)
                        .subscribe({
                            animateOut()
                        },{
                            it.printStackTrace()
                        })
                super.onAnimationEnd(animation)
            }
        })
        animatorIn.start()
        imageView.postDelayed({imageView.show()},200)
    }

    private fun animateOut(){
        var animatorOut = ValueAnimator.ofInt(windowHeight - imageHeight,windowHeight)
        animatorOut.duration = 1000
        animatorOut.addUpdateListener {
            if(activity.isFinishing || activity.isDestroyed)return@addUpdateListener
            params.y = it.animatedValue as Int
            try {
                WM.updateViewLayout(imageView,params)
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
        animatorOut.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                try {
                    WM.removeViewImmediate(imageView)
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
        })
        animatorOut.start()
    }
}