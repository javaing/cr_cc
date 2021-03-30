package com.aliee.quei.mo.ui.search.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.SearchHistoryBean
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val searchRecordList = mutableListOf<SearchHistoryBean>()
    private val recommendList = mutableListOf<RecommendBookBean>()

    companion object {
        const val VIEW_TYPE_RECORD_TITLE = 0
        const val VIEW_TYPE_RECORD_ITEM = 1
        const val VIEW_TYPE_RECOMMEND = 2
        const val VIEW_TYPE_RECOMMEND_TITLE = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_RECORD_TITLE -> {
                val v = parent.context.inflate(R.layout.item_record_title,parent)
                return TitleHolder(v)
            }
            VIEW_TYPE_RECORD_ITEM -> {
                val v = parent.context.inflate(R.layout.item_search_record,parent)
                return SearchRecordHolder(v)
            }
            VIEW_TYPE_RECOMMEND_TITLE -> {
                val v = parent.context.inflate(R.layout.item_search_recommend_title,parent)
                return RecommendTitleHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_comic_linear,parent)
                return ComicLinearHolder(v)
            }
        }
    }

    var recordDelClick : ((keyword : String) -> Unit)? = null
    var recommendClick : ((bean : RecommendBookBean) -> Unit)? = null
    var recordClick : ((keyword : String) -> Unit)? = null
    var shiftClick : (() -> Unit)? = null

    private var recordTitleRange = 0
    private var recordItemRange = 0
    private var recommendTitleRange = 0
    private var recommendRange = 0
    override fun getItemCount(): Int {
        if (searchRecordList.isNotEmpty()) {
            recordTitleRange = 1
            recordItemRange = recordTitleRange + searchRecordList.size
        } else {
            recordTitleRange = 0
            recordItemRange = 0
        }
        recommendTitleRange = recordItemRange + 1
        recommendRange = recommendTitleRange + recommendList.size
        return recommendRange
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            VIEW_TYPE_RECORD_TITLE -> {}
            VIEW_TYPE_RECORD_ITEM -> {
                holder as SearchRecordHolder
                holder.bind(searchRecordList.getOrNull(position - recordTitleRange))
            }
            VIEW_TYPE_RECOMMEND_TITLE -> {
                holder as RecommendTitleHolder
            }
            VIEW_TYPE_RECOMMEND -> {
                holder as ComicLinearHolder
                val bean = recommendList[position - recommendTitleRange]
                holder.bindRecommend(bean,recommendClick)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < recordTitleRange) return VIEW_TYPE_RECORD_TITLE
        if (position < recordItemRange) return VIEW_TYPE_RECORD_ITEM
        if (position < recommendTitleRange)return VIEW_TYPE_RECOMMEND_TITLE
        return VIEW_TYPE_RECOMMEND
    }

    fun setSearchHistory(list: List<SearchHistoryBean>?) {
        list?:return
        this.searchRecordList.clear()
        this.searchRecordList.addAll(list)
        notifyDataSetChanged()
    }

    fun setRecommend(list : List<RecommendBookBean>?){
        list?:return
        this.recommendList.clear()
        this.recommendList.addAll(list)
        notifyDataSetChanged()
    }

    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class SearchRecordHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvKeyword = itemView.find<TextView>(R.id.keyword)
        val imgDel = itemView.find<ImageView>(R.id.del)
        fun bind(bean : SearchHistoryBean?) {
            bean?:return
            tvKeyword.text = bean.keyword
            imgDel.click {
                recordDelClick?.invoke(bean.keyword)
            }
            tvKeyword.click {
                recordClick?.invoke(bean.keyword)
            }
        }
    }

    inner class RecommendTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.find<TextView>(R.id.shift)
                .click {
                    shiftClick?.invoke()
                }
        }
    }

}