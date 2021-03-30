package com.aliee.quei.mo.ui.user.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.BonusRecordBean
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class BonusAdapter : RecyclerView.Adapter<BonusAdapter.BonusHolder>(){
    private var mData = mutableListOf<BonusRecordBean>()

    fun setData(list : List<BonusRecordBean>?){
        list?:return
        this.mData.clear()
        this.mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BonusHolder {
        val v = parent.context.inflate(R.layout.item_bonus,parent)
        return BonusHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: BonusHolder, position: Int) {
        val bean = mData[position]
        holder.bind(bean)
    }

    inner class BonusHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val coin = itemView.find<TextView>(R.id.coin)
        private val time = itemView.find<TextView>(R.id.time)
        private val sdf = SimpleDateFormat("yyyy年MM月dd日")

        fun bind(bean : BonusRecordBean){
            val context = itemView.context
            coin.text = context.getString(R.string.recommend_bonuss,bean.bean)
            time.text = sdf.format(Date(bean.createDate?:0))
        }
    }
}