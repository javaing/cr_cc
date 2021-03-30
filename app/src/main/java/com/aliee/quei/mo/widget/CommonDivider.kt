package com.aliee.quei.mo.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.scwang.smartrefresh.layout.util.DensityUtil

/**
 * @Author: LiYang
 * @Date: 2018/1/17
 * @Version: 1.0.0
 * @Description: recyclerview 分割线
 */
class CommonDivider constructor(private val marginLeft : Int = DensityUtil.dp2px(10f),
                                private val marginRight : Int = DensityUtil.dp2px(10f),
                                private val height : Int = DensityUtil.dp2px(1f),
                                private val color : Int = Color.parseColor("#E6E6E6")) : RecyclerView.ItemDecoration(){
    val _paint : Paint
    init{
        _paint = Paint()
        _paint.style = Paint.Style.FILL_AND_STROKE
        _paint.color = color
    }
    override fun onDraw(canvas: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(canvas, parent, state)
        canvas?.let {
            canvas.save()
            val top: Int
            val bottom: Int
            if (parent?.clipToPadding!!) {
                top = parent.paddingTop
                bottom = parent.height - parent.paddingBottom
                canvas.clipRect(parent.paddingLeft, top,
                        parent.width - parent.paddingRight, bottom)
            } else {
                top = 0
                bottom = parent.height
            }

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val right = marginLeft + child.width + Math.round(child.translationX)
                val left = marginLeft
                val top = child.bottom
                val bottom = child.bottom
                canvas.drawLine(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(),_paint)
            }
            canvas.restore()
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect?.set(0,0,0,height)
    }
}