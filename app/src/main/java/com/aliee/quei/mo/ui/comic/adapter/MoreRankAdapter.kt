package com.aliee.quei.mo.ui.comic.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.common.adapter.ComicGrid2Holder
import com.aliee.quei.mo.ui.common.adapter.ComicGrid3Holder
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.inflate

class MoreRankAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var mData = mutableListOf<RecommendBookBean>()

    var itemClick : ((bean : RecommendBookBean) -> Unit)? = null

    companion object {
        const val VIEW_TYPE_COMIC_LINEAR = 0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_COMIC_LINEAR -> {
                val v = parent.context.inflate(R.layout.item_comic_linear,parent,false)
                return ComicLinearHolder(v)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_3,parent,false)
                return ComicGrid3Holder(v)
            }
            else  -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_2,parent,false)
                return ComicGrid2Holder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val m = position % 10
        if (m < 5) return VIEW_TYPE_COMIC_LINEAR
        if (m < 8) return ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
        return ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = mData[position]
//        holder.bindRecommend(bean)
        when (holder) {
            is ComicGrid2Holder -> {
                holder.bind(bean,itemClick)
            }
            is ComicGrid3Holder -> {
                holder.bind(bean,itemClick)
            }
            is ComicLinearHolder -> {
                holder.bindRecommend(bean,itemClick)
            }
        }
    }

    fun setData(data: List<RecommendBookBean>?) {
        data?:return
        mData.clear()
        mData.addAll(data)
        var index = 0
        notifyDataSetChanged()
        Log.e("MoreRankAdapter", "seData")
    }

    fun add(data: List<RecommendBookBean>?) {
        data?:return
        mData.addAll(data)
        notifyDataSetChanged()
    }
}