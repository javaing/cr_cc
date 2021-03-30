package com.aliee.quei.mo.widget

import android.support.v4.view.ViewPager
import android.view.View

class DepthScaleTransformer constructor(private val minScale : Float = 0.8f, private var maxRotate : Float = 30f): ViewPager.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        val scale = minScale + (1 - minScale) * (1 - Math.abs(position))
        val rotation = maxRotate * Math.abs(position)

        if (position <= 0f) {
            view.translationX = view.width.toFloat() * -position * 0.19f
            view.pivotY = 0.5f * view.height
            view.pivotX = 0.5f * view.width
            view.scaleX = scale
            view.scaleY = scale
            view.rotationY = rotation
        } else if (position <= 1f) {
            view.translationX = view.width.toFloat() * -position * 0.19f
            view.pivotY = 0.5f * view.height
            view.pivotX = 0.5f * view.width
            view.scaleX = scale
            view.scaleY = scale
            view.rotationY = -rotation
        }
    }
}