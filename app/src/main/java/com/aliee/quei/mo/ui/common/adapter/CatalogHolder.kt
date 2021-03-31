package com.aliee.quei.mo.ui.common.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.loadNovelCover
import org.jetbrains.anko.find

class CatalogHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val thumb = itemView.find<ImageView>(R.id.thumb)
    private val name  = itemView.find<TextView>(R.id.name)
    private val price = itemView.find<TextView>(R.id.price)
    fun bind(bean: CatalogItemBean?,chapterClick : ((bean : CatalogItemBean) -> Unit)?) {
        val context = itemView.context
        reset()
        bean?.let {
            thumb.loadNovelCover(it.thumb)
            name.text = it.name
            price.text = context.getString(R.string.chapter_price,it.bookBean)
            itemView.click { chapterClick?.invoke(bean) }
        }
    }

    private fun reset() {
        thumb.setImageDrawable(null)
        name.text = ""
        price.text = ""
    }
}