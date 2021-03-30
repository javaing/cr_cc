package com.aliee.quei.mo.widget.comic

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class ReadViewFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action){
            MotionEvent.ACTION_UP -> {
                val y = ev.y
                if (y < height / 3){
                    topClick?.invoke()
                } else if (y < height / 3 * 2) {
                    centerClick?.invoke()
                } else {
                    bottomClick?.invoke()
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    var topClick : (()-> Unit)? = null
    var centerClick : (()-> Unit)? = null
    var bottomClick : (()-> Unit)? = null
}