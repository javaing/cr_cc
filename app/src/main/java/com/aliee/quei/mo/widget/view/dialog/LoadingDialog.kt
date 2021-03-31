package com.aliee.quei.mo.widget.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import androidx.annotation.StyleRes
import com.aliee.quei.mo.R
import com.aliee.quei.mo.utils.extention.getResDrawable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_loading.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
class LoadingDialog(context: Context) : Dialog(context,R.style.dialog_loading) {
    companion object {
        val minDisplayTime = 200
    }

    constructor(context: Context, @StyleRes themeResId: Int) : this(context)

    var animationDrawable: AnimationDrawable = context.getResDrawable(R.drawable.loading_animation_jump) as AnimationDrawable

    init {
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_loading)
        setCanceledOnTouchOutside(false)
    }

    fun setCanCancel(canCancel: Boolean): LoadingDialog {
        setCanceledOnTouchOutside(canCancel)
        return this
    }

    private var showTime = System.currentTimeMillis()
    private var dismissSubscription : Disposable? = null
    override fun show() {
        try {
            if(isShowing){
                dismissSubscription?.dispose()
                return
            }
            super.show()
            showTime = System.currentTimeMillis()
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            if(System.currentTimeMillis() - showTime > minDisplayTime){
                super.dismiss()
            } else {
                Observable.timer(minDisplayTime - (System.currentTimeMillis() - showTime),TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            super.dismiss()
                        },{},{},{
                            dismissSubscription = it
                        })
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        imageView.setImageDrawable(animationDrawable)
        animationDrawable.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationDrawable.stop()
    }
}