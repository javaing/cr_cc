package com.aliee.quei.mo.ui.pay.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.config.PayWayConfig
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.ui.pay.holder.PayWayHolder

class PayWayAdapter: RecyclerView.Adapter<PayWayHolder>() {
    var itemClick : ((bean : PayWayBean) -> Unit)?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayWayHolder {
        return PayWayHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return PayWayConfig.payWayList.size
    }

    override fun onBindViewHolder(holder: PayWayHolder, position: Int) {
        holder.bind(PayWayConfig.payWayList[position],itemClick)
    }
}