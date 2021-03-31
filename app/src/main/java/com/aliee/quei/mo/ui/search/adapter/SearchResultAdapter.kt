package com.aliee.quei.mo.ui.search.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.inflate

/**
 * Created by Administrator on 2018/4/28 0028.
*/
class SearchResultAdapter : RecyclerView.Adapter<ComicLinearHolder>(){
    private var mData: List<ComicBookBean> = mutableListOf()

    fun setData(list : List<ComicBookBean>?){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicLinearHolder {
        val v = parent.context.inflate(R.layout.item_comic_linear,parent,false)
        return ComicLinearHolder(v)
    }

    override fun onBindViewHolder(holder: ComicLinearHolder, position: Int) {
        val bean = mData[position]
        holder.bindComic(bean)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}