package com.aliee.quei.mo.ui.comic.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.aliee.quei.mo.data.bean.RecommendBookBean

class BookDiffUtil(
        val oldList: List<RecommendBookBean>, val newList: List<RecommendBookBean>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.e("MoreAdapter", "sameID :" + (oldList[oldItemPosition].id == newList[newItemPosition].id))
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}