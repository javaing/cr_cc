package com.aliee.quei.mo.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.ShelfBean
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.common.adapter.ComicGrid2Holder
import com.aliee.quei.mo.ui.common.adapter.ComicGrid3Holder
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.extention.*
import org.jetbrains.anko.find

class ShelfAdapter2 : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val mShelfList = mutableListOf<ShelfBean>()
    private val recommendList = mutableListOf<RecommendBookBean>()

    var itemClick : ((bean : ShelfBean) -> Unit)? = null
    var removeClick : ((bean : ShelfBean) -> Unit)? = null
    companion object {
        const val VIEW_TYPE_GUESS_TITLE = 1
        const val VIEW_TYPE_COMIC_LINEAR = 2
        const val VIEW_TYPE_SHELF_HEADER = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_SHELF_HEADER -> {
                val v = parent.context.inflate(R.layout.item_shelf_header,parent,false)
                return ShelfHeaderHolder(v)
            }
            ShopItemDecoration.VIEW_TYPE_SHELF -> {
                val v = parent.context.inflate(R.layout.item_shelf_2,parent)
                return ShelfHolder2(v)
            }
            VIEW_TYPE_GUESS_TITLE -> {
                val v = parent.context.inflate(R.layout.item_guess_like_header)
                return TitleHolder(v)
            }
            VIEW_TYPE_COMIC_LINEAR -> {
                val v = parent.context.inflate(R.layout.item_comic_linear,parent,false)
                return ComicLinearHolder(v)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_3,parent,false)
                return ComicGrid3Holder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_2,parent,false)
                return ComicGrid2Holder(v)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)return VIEW_TYPE_SHELF_HEADER
        if (position < mShelfList.size + 1) return ShopItemDecoration.VIEW_TYPE_SHELF
        if (position < mShelfList.size + 1 + 1) return VIEW_TYPE_GUESS_TITLE
        val bean = recommendList[position - 1 - 1 - mShelfList.size]
        return bean.showType
    }

    private var showRemoveBtn = false

    override fun getItemCount(): Int {
        return mShelfList.size + 1 + 1 + recommendList.size
    }

    var recommendClick: ((bean: RecommendBookBean) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            VIEW_TYPE_SHELF_HEADER -> {
                holder as ShelfHeaderHolder
                holder.bind()
            }
            ShopItemDecoration.VIEW_TYPE_SHELF -> {
                val bean = mShelfList[position - 1]
                holder as ShelfHolder2
                holder.bind(bean)
            }
            VIEW_TYPE_GUESS_TITLE -> {

            }
            else -> {
                val bean = recommendList[position - 1 - 1 - mShelfList.size]
                when (holder) {
                    is ComicLinearHolder -> holder.bindRecommend(bean,recommendClick)
                    is ComicGrid2Holder -> holder.bind(bean,recommendClick)
                    is ComicGrid3Holder -> holder.bind(bean,recommendClick)
                }
            }
        }

    }

    fun setShelfItem(data: MutableList<ShelfBean>) {
        this.mShelfList.clear()
        this.mShelfList.addAll(data)
        notifyDataSetChanged()
    }

    fun toggleRemoveBtn() : Boolean{
        if (mShelfList.isEmpty()) {
            return false
        }
        this.showRemoveBtn = !showRemoveBtn
        notifyDataSetChanged()
        return this.showRemoveBtn
    }


    fun addRecommend(list: List<RecommendBookBean>?) {
        list?:return
        recommendList.addAll(list)
        var index = 0
        recommendList.forEach {
            val m = index % 10
            if (m < 5) {
                it.showType = VIEW_TYPE_COMIC_LINEAR
            } else if (m < 8) {
                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
            } else {
                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2
            }
            index ++
        }
        notifyDataSetChanged()
    }

    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    inner class ShelfHolder2(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val ivCover = itemView.find<ImageView>(R.id.ivCover)
        val tvTitle = itemView.find<TextView>(R.id.tvTitle)
        val ivDel = itemView.find<ImageView>(R.id.ivDel)
        val tvTagStatus = itemView.find<TextView>(R.id.ivTagStatus)
        fun bind(bean : ShelfBean) {
            if (bean.bookInfo?.status == BeanConstants.STATUS_FINISH) {
                tvTagStatus.text = "完结"
                tvTagStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
            } else {
                tvTagStatus.text = "连载"
                tvTagStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
            }

            if (showRemoveBtn) {
                ivDel.show()
                ivDel.click {
                    removeClick?.invoke(bean)
                }
            } else {
                ivDel.gone()
            }

            tvTitle.text = bean.bookInfo?.title.toString()
            ivCover.loadNovelCover(bean.bookInfo?.thumb)
            itemView.click {
                itemClick?.invoke(bean)
            }
        }
    }

    inner class ShelfHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnEdit = itemView.find<TextView>(R.id.btnEdit)
        private val tvAmount = itemView.find<TextView>(R.id.tvAmount)

        fun bind() {
            tvAmount.text = "我的书架(${mShelfList.size})"
            btnEdit.click {
                if(toggleRemoveBtn()) {
                    btnEdit.text = "完成"
                } else {
                    btnEdit.text = "编辑"
                }
            }
        }
    }
}