package com.aliee.quei.mo.ui.welfare.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.data.bean.WelfareCoinRecordBean
import com.aliee.quei.mo.ui.welfare.holder.CoinRecordHolder

class CoinListAdapter : RecyclerView.Adapter<CoinRecordHolder>() {
    private var mData = mutableListOf<WelfareCoinRecordBean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinRecordHolder {
        return CoinRecordHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setData(list : List<WelfareCoinRecordBean>?) {
        mData.clear()
        mData.addAll(list?: mutableListOf())
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CoinRecordHolder, position: Int) {
        val bean = mData[position]
        holder.bind(bean)
    }
}