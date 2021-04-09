package com.aliee.quei.mo.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.extention.loadNovelCover
import kotlin.random.Random

class HotRankAdapter(private val data: Map<Int, List<RecommendBookBean>>) : RecyclerView.Adapter<HotRankAdapter.BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_one_list, parent, false)
        Log.e("tag", "HotRankAdapter onCreateViewHolder")
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.e("tag", "HotRankAdapter onBindViewHolder")
        data.get(position)?.forEachIndexed { index, it ->
            if(index>5) return
            val itemView: View = LayoutInflater.from(holder.llHot.context).inflate(R.layout.item_comic_hotrank, holder.llHot, false)
            itemView.findViewById<TextView>(R.id.tv_HotNum).text = (index+1).toString()
            itemView.findViewById<ImageView>(R.id.img_hot_cover).loadNovelCover(it.thumb)
            //itemView.findViewById<TextView>(R.id.tv_HotDescr).text = it.desc
            itemView.findViewById<TextView>(R.id.tv_HotDescr).text = "第${Random.nextInt(1, 100)}话"
            itemView.findViewById<TextView>(R.id.tv_HotTitle).text = it.title
            itemView.findViewById<TextView>(R.id.tvHotType).text = it.typename
            itemView.findViewById<TextView>(R.id.tvHotStar).text = ComicUtils.getCommentStar(it.id)

            holder.llHot.addView(itemView)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llHot: LinearLayout = itemView.findViewById(R.id.ll_hot)
    }

}