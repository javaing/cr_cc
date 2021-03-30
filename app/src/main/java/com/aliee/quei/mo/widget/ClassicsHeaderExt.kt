package com.aliee.quei.mo.widget

import android.content.Context
import android.util.AttributeSet
import com.scwang.smartrefresh.layout.api.RefreshInternal
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader

class ClassicsHeaderExt @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ClassicsHeader(context, attrs, defStyleAttr), RefreshInternal {
    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        callback?.onMoving(isDragging,percent,offset,height, maxDragHeight)
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        super.onReleased(refreshLayout, height, maxDragHeight)
        callback?.onRelease()
    }

    var callback : DragCallback? = null
    interface DragCallback{
        fun onMoving(isDragging: Boolean,percent: Float,offset: Int,height: Int,maxDragHeight: Int)

        fun onRelease()
    }
}