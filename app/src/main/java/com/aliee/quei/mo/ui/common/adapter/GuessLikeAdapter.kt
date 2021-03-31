package com.aliee.quei.mo.ui.common.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.loadNovelCover
import org.jetbrains.anko.find

class GuessLikeAdapter : RecyclerView.Adapter<GuessLikeAdapter.GuessLikeHolder>() {
    var max = 3

    private val mData = mutableListOf<RecommendBookBean>()

    fun setGuessLike(list : List<RecommendBookBean>?) {
        list?:return
        mData.addAll(list.shuffled())
        notifyDataSetChanged()
    }

    fun setSameCategory(list : List<RecommendBookBean>) {
        mData.addAll(0,list.shuffled())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessLikeHolder {
        val v = parent.context.inflate(R.layout.item_guess_like)
        return GuessLikeHolder(v)
    }

    override fun getItemCount(): Int {
        return if (mData.size > max) max else mData.size
    }

    override fun onBindViewHolder(holder: GuessLikeHolder, position: Int) {
        holder.bind(bean = mData[position])
    }

    inner class GuessLikeHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val thumb = itemView.find<ImageView>(R.id.thumb)
        private val title = itemView.find<TextView>(R.id.title)
        private val category = itemView.find<TextView>(R.id.category)
        fun bind(bean: RecommendBookBean?) {
            bean?.let {
                thumb.loadNovelCover(it.bookcover)
                title.text = it.title
                category.text = it.typename
                itemView.click {
//                    guessLikeClick?.invoke(bean)
                    ARouterManager.goComicDetailActivity(it.context,bean.id)
                }
            }

        }
        private fun reset() {
            thumb.setImageDrawable(null)
            title.text = ""
            category.text = ""
        }
    }
}