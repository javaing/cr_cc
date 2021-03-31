package com.aliee.quei.mo.ui.ticket.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.ui.ticket.holder.TicketHolder
import io.realm.Realm

class TickerAdapter(
    private val realm : Realm
) : RecyclerView.Adapter<TicketHolder>(){
    private val mData = mutableListOf<TicketBean>()

    var itemClick : ((bean : TicketBean,status : Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketHolder {
        return TicketHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: TicketHolder, position: Int) {
        holder.bind(mData[position],realm,itemClick)
    }

    fun setData(data: List<TicketBean>?) {
        mData.clear()
        mData.addAll(data?: mutableListOf())
        notifyDataSetChanged()
    }
}