package com.aliee.quei.mo.ui.welfare.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.WelfareCoinRecordBean
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class CoinRecordHolder private constructor(itemView : View) : RecyclerView.ViewHolder(itemView) {
    companion object{
        fun create(parent : ViewGroup) :CoinRecordHolder{
            val v = parent.context.inflate(R.layout.item_coin_record,parent,false)
            return CoinRecordHolder(v)
        }
    }
    private val tvName = itemView.find<TextView>(R.id.tvName)
    private val tvTime = itemView.find<TextView>(R.id.tvTime)
    private val tvAmount = itemView.find<TextView>(R.id.tvAmount)
    private val sdf = SimpleDateFormat.getDateTimeInstance()

    fun bind(bean: WelfareCoinRecordBean) {
        tvName.text = bean.event
        tvTime.text = sdf.format(Date(bean.created))
        tvAmount.text = "${bean.bookbean}金币"
    }
}