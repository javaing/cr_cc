package com.aliee.quei.mo.ui.comic.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.inflate

class MoreAdapter : RecyclerView.Adapter<ComicLinearHolder>() {
    private var mData: MutableList<RecommendBookBean> = mutableListOf()
    var itemClick: ((bean: RecommendBookBean) -> Unit)? = null

    fun setData(list: MutableList<RecommendBookBean>?) {
        list ?: return
        this.mData = list
        notifyDataSetChanged()
    }




    fun insertAd(index: Int, adInfo: AdInfo) {
       val recommendBookBean = RecommendBookBean("", adInfo.imgurl, -321, adInfo.desc, 1, "", adInfo.title, "")
        recommendBookBean.adCallbackUrl = adInfo.callbackurl
        recommendBookBean.adClickUrl = adInfo.clickurl
        this.mData.add(index, recommendBookBean)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicLinearHolder {
        val v = parent.context.inflate(R.layout.item_comic_linear, parent, false)
        return ComicLinearHolder(v)
    }

    override fun onBindViewHolder(holder: ComicLinearHolder, position: Int) {
        val bean = mData[position]
        Log.d("tag","title:${bean!!.title}")
        holder.bindRecommend(bean, itemClick)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
