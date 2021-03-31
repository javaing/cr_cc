package com.aliee.quei.mo.ui.comic.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.ShelfBean

class ShelfDiffUtil(
        val oldList: List<ShelfBean>, val newList: List<ShelfBean>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.e("ShelfBean", "sameID :" + (oldList[oldItemPosition].bookid == newList[newItemPosition].bookid))
        return oldList[oldItemPosition].bookid == newList[newItemPosition].bookid
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}