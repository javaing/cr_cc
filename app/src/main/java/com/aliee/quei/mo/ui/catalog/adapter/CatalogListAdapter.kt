package com.aliee.quei.mo.ui.catalog.adapter

import android.view.ViewGroup
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.ui.common.adapter.CatalogHolder
import com.aliee.quei.mo.utils.extention.inflate
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class CatalogListAdapter constructor(data : OrderedRealmCollection<CatalogItemBean>): RealmRecyclerViewAdapter<CatalogItemBean, CatalogHolder>(data,true){
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogHolder {
        val v = parent.context.inflate(R.layout.item_comic_catalog,parent)
        return CatalogHolder(v)
    }

    override fun getItemId(position: Int): Long {
        return (getItem(position)?.id ?: 0) .toLong()
    }

    override fun onBindViewHolder(holder: CatalogHolder, position: Int) {
        val bean = getItem(position)?:return
        holder.bind(bean){
            onClick?.invoke(it,position)
        }
    }

    private var current: Int = -1

    fun setCurrent(i: Int) {
        this.current = i
        notifyDataSetChanged()
    }

    var onClick : ((bean : CatalogItemBean,position : Int) -> Unit)? = null
}

