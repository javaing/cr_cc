package com.aliee.quei.mo.ui.common

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.aliee.quei.mo.utils.ScreenUtils

class ShopItemDecoration : RecyclerView.ItemDecoration(){
    companion object {
        const val VIEW_TYPE_COMIC_GRID_3 = 100
        const val VIEW_TYPE_COMIC_GRID_2 = 101
        const val VIEW_TYPE_HISTORY = 102
        const val VIEW_TYPE_SHELF = 103

    }
    val dp1 = ScreenUtils.dpToPx(2)
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val adapter = parent.adapter
        val viewType = adapter?.getItemViewType(itemPosition)
        if (viewType == VIEW_TYPE_COMIC_GRID_3 || viewType == VIEW_TYPE_HISTORY || viewType == VIEW_TYPE_SHELF) {
            outRect.top = ScreenUtils.dpToPx(6)
            var startPosition = 0
            for (i in itemPosition downTo 0) {
                val prevViewType = adapter.getItemViewType(i)
                if (viewType != prevViewType) {
                    startPosition = i + 1
                    break
                }
            }
//                    XLog.st(1).e("grid3 StartPosition = $startPosition")
            val column = (itemPosition - startPosition) % 3
            when (column) {
                0 -> outRect.left = dp1 * 3
                1 -> {
                    outRect.right = dp1
                    outRect.left = dp1
                }
                2 -> outRect.right = dp1 * 3
            }
        }
        if (viewType == VIEW_TYPE_COMIC_GRID_2) {
            var startPosition = 0
            for (i in itemPosition downTo 0) {
                val prevViewType = adapter.getItemViewType(i)
                if (viewType != prevViewType) {
                    startPosition = i + 1
                    break
                }
            }
//                    XLog.st(1).e("grid3 StartPosition = $startPosition")
            val column = (itemPosition - startPosition) % 2
            when (column) {
                0 -> {
                    outRect.left = dp1 * 3
                    outRect.right = dp1 * 1
                }
                1 -> {
                    outRect.left = dp1 * 1
                    outRect.right = dp1 * 3
                }
            }
        }
    }
}