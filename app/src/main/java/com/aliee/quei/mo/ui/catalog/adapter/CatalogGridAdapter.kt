package com.aliee.quei.mo.ui.catalog.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class CatalogGridAdapter constructor(data : OrderedRealmCollection<CatalogItemBean>) : RealmRecyclerViewAdapter<CatalogItemBean, CatalogGridHolder>(data,true){
    private var selected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogGridHolder {
        val v = parent.context.inflate(R.layout.item_catalog_grid,parent,false)
        return CatalogGridHolder(v)
    }

    override fun getItemCount(): Int {
        data?:return 0
        return Math.ceil((data?.size?:0) / 100.0).toInt()
    }

    override fun onBindViewHolder(holder: CatalogGridHolder, position: Int) {
        val context = holder.itemView.context
        val index = position
        val total = data?.size?:0
        val start = position * 100 + 1
        var end = start + 99
        if (end > total)end = total
        holder.textChapters.text = context.getString(R.string.chapter_range,start,end)
        holder.textChapters.isActivated = selected != index

        holder.itemView.click {
            selected = index
            notifyDataSetChanged()
            listener?.invoke(start - 1,end - 1)
        }
    }

    var listener : ((start : Int,end : Int)-> Unit)? = null
}

class CatalogGridHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val textChapters = itemView as TextView
    init {
//        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textChapters,10,12,1,TypedValue.COMPLEX_UNIT_SP)
    }
}