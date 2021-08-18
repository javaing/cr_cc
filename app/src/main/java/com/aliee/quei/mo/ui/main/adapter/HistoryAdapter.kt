package com.aliee.quei.mo.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.common.adapter.ComicGrid2Holder
import com.aliee.quei.mo.ui.common.adapter.ComicGrid3Holder
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.*
import org.jetbrains.anko.find


/**
 * Created by Administrator on 2018/4/26 0026.
 */
class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var mHistory = mutableListOf<HistoryBean>()
    private var mRecommend = mutableListOf<RecommendBookBean>()

    companion object {
        const val VIEW_TYPE_GUESS_LIKE_HEADER = 1
        const val VIEW_TYPE_COMIC_LINEAR = 2
        const val VIEW_TYPE_HISTORY_HEADER = 5
    }

    private var showRemoveBtn = false

    fun toggleRemoveBtn() : Boolean{
        if (mHistory.isEmpty()) {
            return false
        }
        this.showRemoveBtn = !showRemoveBtn
        notifyDataSetChanged()
        return this.showRemoveBtn
    }

    fun insertAd(index: Int, adInfo: AdInfo) {
        val recommendBookBean = RecommendBookBean("", adInfo.imgurl, -321, adInfo.desc, 1, "", adInfo.title, "")
        recommendBookBean.adCallbackUrl = adInfo.callbackurl
        recommendBookBean.adClickUrl = adInfo.clickurl
        this.mRecommend.add(0, recommendBookBean)
        notifyDataSetChanged()
    }

    fun addRecommend(list : List<RecommendBookBean>?) {
        list?:return
        mRecommend.addAll(list)
        var index = 0
        mRecommend.forEach {
            val m = index % 10
            if (m < 5) {
                it.showType = VIEW_TYPE_COMIC_LINEAR
            } else if (m < 8){
                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
            } else {
                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2
            }
            index ++
        }
        notifyDataSetChanged()
    }


    var recommendClick : ((bean : RecommendBookBean) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            holder as HistoryHeaderHolder
            holder.bind()
            return
        }

        if (position < mHistory.size + 1) {
            holder as HistoryHolder
            holder.bind(mHistory[position - 1])
            return
        }

        if (position < mHistory.size + 1 + 1) {
            holder as TitleHolder
            return
        }
        val recommendBean = mRecommend[position - 1 - 1 - mHistory.size]
        when (recommendBean.showType) {
            VIEW_TYPE_COMIC_LINEAR -> {
                holder as ComicLinearHolder
                holder.bindRecommend(recommendBean,recommendClick)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> {
                holder as ComicGrid3Holder
                holder.bind(recommendBean,recommendClick)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> {
                holder as ComicGrid2Holder
                holder.bind(recommendBean,recommendClick)
            }
        }
    }

    var removeClick : ((bean : HistoryBean)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_HISTORY_HEADER -> {
                val v = parent.context.inflate(R.layout.item_shelf_header,parent,false)
                return HistoryHeaderHolder(itemView = v)
            }
            ShopItemDecoration.VIEW_TYPE_HISTORY -> {
                val v = parent.context.inflate(R.layout.item_history,parent,false)
                return HistoryHolder(v)
            }
            VIEW_TYPE_GUESS_LIKE_HEADER -> {
                val v = parent.context.inflate(R.layout.item_guess_like_header,parent,false)
                return TitleHolder(v)
            }
            VIEW_TYPE_COMIC_LINEAR -> {
                val v = parent.context.inflate(R.layout.item_comic_linear,parent,false)
                return ComicLinearHolder(v)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_2,parent,false)
                return ComicGrid2Holder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_3,parent,false)
                return ComicGrid3Holder(v)
            }
        }

    }

    class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int {
        return mHistory.size + 1 + mRecommend.size + 1
    }

    fun setHistory(data: List<HistoryBean>?) {
        data?.let {
            this.mHistory.clear()
            this.mHistory.addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)return VIEW_TYPE_HISTORY_HEADER
        if (position < mHistory.size + 1) {
            return ShopItemDecoration.VIEW_TYPE_HISTORY
        }
        if (position < mHistory.size + 1 + 1) {
            return VIEW_TYPE_GUESS_LIKE_HEADER
        }
        val recommendBean = mRecommend[position - mHistory.size - 1 - 1]
        return recommendBean.showType
    }

    fun removeItem(id: Int?) {
        val bean : HistoryBean? = mHistory.find {
            it.id == id
        }
        mHistory.remove(bean)
        notifyDataSetChanged()
    }

    inner class HistoryHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivCover = itemView.find<ImageView>(R.id.ivCover)
        val tvTitle = itemView.find<TextView>(R.id.tvTitle)
        val ivDel = itemView.find<ImageView>(R.id.ivDel)
        val tvTagStatus = itemView.find<TextView>(R.id.ivTagStatus)
        fun bind(bean : HistoryBean) {
            tvTagStatus.gone()
            val context = itemView.context
            ivCover.loadNovelCover(bean.thumb)
            tvTitle.text = bean.title

            itemView.click {
                ARouterManager.goReadActivity(it.context,bean.id,bean.chapterId)
            }
            if (showRemoveBtn) {
                ivDel.show()
                ivDel.click {
                    removeClick?.invoke(bean)
                }
            } else {
                ivDel.gone()
            }
        }
    }

    inner class HistoryHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnEdit = itemView.find<TextView>(R.id.btnEdit)
        private val tvAmount = itemView.find<TextView>(R.id.tvAmount)

        fun bind() {
            tvAmount.text = "阅读记录(${mHistory.size})"
            btnEdit.click {
                if(toggleRemoveBtn()) {
                    btnEdit.text = "完成"
                } else {
                    btnEdit.text = "编辑"
                }
            }
        }
    }
//
//    inner class HistoryGuessLikeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bind(bean: RecommendBookBean) {
//            imgae.loadNovelCover(bean.bookcover)
//            title.text = bean.title
//            desc.text = bean.desc
//            itemView.click { ARouterManager.goComicDetailActivity(it.context,bean.bookid) }
//        }
//
//        val imgae = itemView.find<ImageView>(R.id.image)
//        val title = itemView.find<TextView>(R.id.title)
//        val desc = itemView.find<TextView>(R.id.descr)
//
//    }
}