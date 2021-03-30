package com.aliee.quei.mo.ui.comic.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find

class ReadFinishAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    private val guessLikeList = mutableListOf<RecommendBookBean>()
    private var comicStatus = BeanConstants.STATUS_FINISH
    var recommendClick : ((bean : RecommendBookBean) -> Unit)? = null

    companion object {
        const val VIEW_TYPE_STATUS = 0
        const val VIEW_TYPE_GUESS_LIKE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_STATUS -> {
                val v = parent.context.inflate(R.layout.layout_read_finish,parent)
                return ComicStatusHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_comic_linear)
                return ComicLinearHolder(v)
            }
        }
    }

    override fun getItemCount(): Int {
        return guessLikeList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)return VIEW_TYPE_STATUS
        return VIEW_TYPE_GUESS_LIKE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType){
            VIEW_TYPE_STATUS -> {
                holder as ComicStatusHolder
                holder.bind()
            }
            else -> {
                holder as ComicLinearHolder
                holder.bindRecommend(guessLikeList[position - 1],recommendClick)
            }
        }
    }

    fun setGuessLike(data: List<RecommendBookBean>?) {
        data?:return
        this.guessLikeList.clear()
        this.guessLikeList.addAll(data)
        notifyDataSetChanged()
    }

    fun setStatus(bookStatus: Int) {
        this.comicStatus = bookStatus
        notifyDataSetChanged()
    }

    inner class ComicStatusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val findBook = itemView.find<View>(R.id.findBook)
        val imgStatus = itemView.find<ImageView>(R.id.imgStatus)
        val textStatus = itemView.find<TextView>(R.id.textStatus)
        fun bind(){
            val context = itemView.context
            findBook.click {
                ARouterManager.goMainActivity(it.context,showPage = ARouterManager.TAB_SHOP)
            }
            if (comicStatus == BeanConstants.STATUS_FINISH) {
                textStatus.text = context.getString(R.string.this_book_finished)
            } else {
                textStatus.text = context.getString(R.string.author_is_writting)
            }
        }
    }
}