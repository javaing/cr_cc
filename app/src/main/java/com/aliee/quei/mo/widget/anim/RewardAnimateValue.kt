package com.aliee.quei.mo.widget.anim

import android.animation.TypeEvaluator
import android.graphics.Point
import com.scwang.smartrefresh.layout.util.DensityUtil

class RewardAnimateValue(
        val x : Int,
        val y : Int,
        val alpha : Float,
        val scale : Float
)

class RewardAnimatorEvaluator (val p1 : Point,var p2 : Point = Point(p1.x + DensityUtil.dp2px(50f),p1.y)) : TypeEvaluator<RewardAnimateValue>{
//    val p2 = Point(p1.x + DensityUtil.dp2px(50f),p1.y)
    override fun evaluate(t: Float, p0: RewardAnimateValue, p3: RewardAnimateValue): RewardAnimateValue {
        val x = p0.x * (1 - t) * (1 - t) * (1 - t) + 3 * p1.x * t * (1 - t) * (1 - t) + 3 * p2.x * t * t * (1 - t) + p3.x * t * t * t
        val y = p0.y * (1 - t) * (1 - t) * (1 - t) + 3 * p1.y * t * (1 - t) * (1 - t) + 3 * p2.y * t * t * (1 - t) + p3.y * t * t * t
        return RewardAnimateValue(x.toInt(), y.toInt(), 1 - t, 1 - t)
    }
}

class GlobalRewardPopupShrinkEvaluator : TypeEvaluator<RewardAnimateValue>{
    override fun evaluate(t: Float, p0: RewardAnimateValue, p1: RewardAnimateValue): RewardAnimateValue {
        val x = p0.x + (p1.x - p0.x) * t
        val y = p0.y + (p1.y - p0.y) * t
        val scale = 1 - t
        val alpha = 1 - t

        return RewardAnimateValue(x.toInt(),y.toInt(),alpha,scale)
    }
}