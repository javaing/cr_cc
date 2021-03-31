package com.aliee.quei.mo.ui.pay.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.config.PayWayConfig
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.ui.pay.holder.PayWayTestHolder

class PayWayTestAdapter: RecyclerView.Adapter<PayWayTestHolder>() {
    var itemClick : ((bean : PayWayBean) -> Unit)?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayWayTestHolder {
        return PayWayTestHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return PayWayConfig.payWayList.size
    }

    override fun onBindViewHolder(holder: PayWayTestHolder, position: Int) {
        holder.bind(PayWayConfig.payWayList[position],itemClick)
    }
}