package com.aliee.quei.mo.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.data.bean.CheckInStatsBean
import com.aliee.quei.mo.ui.main.holder.SignDayHolder

class SignDayAdapter : RecyclerView.Adapter<SignDayHolder>() {
    private var checkInStats = mutableListOf<CheckInStatsBean>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignDayHolder {
        return SignDayHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return 7
    }

    override fun onBindViewHolder(holder: SignDayHolder, position: Int) {
        var hasSign = checkInStats.find {
            it.weekday == position + 1
        }?.status?:0 > 0
        holder.bind(hasSign,position)
    }

    fun setData(data: List<CheckInStatsBean>?) {
        data?:return
        this.checkInStats.clear()
        this.checkInStats.addAll(data)
        notifyDataSetChanged()
    }
}