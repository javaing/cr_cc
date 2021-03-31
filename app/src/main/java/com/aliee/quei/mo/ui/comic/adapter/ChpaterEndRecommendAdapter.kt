package com.aliee.quei.mo.ui.comic.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.common.adapter.ComicGrid3Holder
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find

class ChpaterEndRecommendAdapter : RecyclerView.Adapter<ComicGrid3Holder>(){
    private val mData = mutableListOf<RecommendBookBean>()

    fun setData (list : List<RecommendBookBean>) {
        //mData.clear()
        //mData.addAll(list)
        //notifyDataSetChanged()

        val oldList = this.mData.toList()
        mData.clear()
        mData.addAll(list)
        val diffResult = DiffUtil.calculateDiff(BookDiffUtil(oldList, this.mData))
        diffResult.dispatchUpdatesTo(this)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicGrid3Holder {
        val v = parent.context.inflate(R.layout.item_comic_grid_3,parent,false)
        return ComicGrid3Holder(v)
    }

    override fun getItemCount(): Int {
        if (mData.size>6){
            return 6
        }
        return mData.size
    }

    override fun onBindViewHolder(holder: ComicGrid3Holder, position: Int) {
        val thumb_rl = holder.itemView.find<RelativeLayout>(R.id.thumb_rl)
        val thumb_ = holder.itemView.find<ImageView>(R.id.thumb)
        val mDensity : Float = ScreenUtils.getDisplayMetrics().density;
        if(mDensity.toInt() == 2){
            thumb_rl.layoutParams.width = (RelativeLayout.LayoutParams.MATCH_PARENT)
            thumb_rl.layoutParams.height = (370)
            thumb_.layoutParams.width = (RelativeLayout.LayoutParams.MATCH_PARENT)
            thumb_.layoutParams.height = (366)
        }
        val bean = mData[position]
        holder.bind(bean) {
            ARouterManager.goReadActivity(holder.itemView.context!!, it.id, if (it.rid == "") 27 else it.rid!!.toInt(), 0, true)
            //ARouterManager.goComicDetailActivity(holder.itemView.context,it.id,true)
        }
    }
}