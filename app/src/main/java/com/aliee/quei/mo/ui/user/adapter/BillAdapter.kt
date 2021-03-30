package com.aliee.quei.mo.ui.user.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.BillRecordBean
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class BillAdapter : RecyclerView.Adapter<BillAdapter.BillHolder>(){
    private var mData = mutableListOf<BillRecordBean>()

    fun setData(list : List<BillRecordBean>?){
        list?:return
        this.mData.clear()
        this.mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillHolder {
        val v = parent.context.inflate(R.layout.item_bill,parent)
        return BillHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: BillHolder, position: Int) {
        val bean = mData[position]
        holder.bind(bean)
    }

    inner class BillHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val price = itemView.find<TextView>(R.id.price)
        private val chapterName = itemView.find<TextView>(R.id.chapterName)
        private val time = itemView.find<TextView>(R.id.time)
        private val sdf = SimpleDateFormat.getDateTimeInstance()

        fun bind(bean : BillRecordBean){
            val context = itemView.context
            price.text = context.getString(R.string.coins_minus,bean.bookBean)
            chapterName.text = context.getString(R.string.book_chapter_name,bean.bookName,bean.chapeterName)
            time.text = sdf.format(Date(bean.time?:0))
        }
    }
}