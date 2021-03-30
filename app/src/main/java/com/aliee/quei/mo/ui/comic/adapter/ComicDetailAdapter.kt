package com.aliee.quei.mo.ui.comic.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.local.ReadRecordManager
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.common.adapter.CatalogHolder
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.extention.*
import org.jetbrains.anko.find

class ComicDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val catalog = mutableListOf<CatalogItemBean>()
    private val guessLike = mutableListOf<RecommendBookBean>()
    private var chapterCount: Int = 0
    private var catalogSort = BeanConstants.SORT_ASC
    private var comic : ComicBookBean? = null
    private var isInShelf = false

    companion object {
          const val VIEW_TYPE_DETAIL = 0
          const val VIEW_TYPE_CATALOG_HEADER = 1
          const val VIEW_TYPE_CATALOG_ITEM = 2
          const val VIEW_TYPE_CATALOG_FOOTER = 3
          const val VIEW_TYPE_GUESS_LIKE_HEADER = 4
          const val VIEW_TYPE_GUESS_LIKE_ITEM = 5
    }

    fun setIsInShelf(inShelf : Boolean) {
        isInShelf = inShelf
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType){
            VIEW_TYPE_DETAIL -> {
                val v = parent.context.inflate(R.layout.item_comic_detail,parent)
                return DetailHolder(v)
            }
            VIEW_TYPE_CATALOG_HEADER -> {
                val v = parent.context.inflate(R.layout.item_catalog_header,parent)
                return CatalogHeaderHolder(v)
            }
            VIEW_TYPE_CATALOG_ITEM -> {
                val v = parent.context.inflate(R.layout.item_comic_catalog,parent)
                return CatalogHolder(v)
            }
            VIEW_TYPE_CATALOG_FOOTER -> {
                val v = parent.context.inflate(R.layout.item_catalog_footer,parent)
                return CatalogFooterHolder(v)
            }
            VIEW_TYPE_GUESS_LIKE_HEADER -> {
                val v = parent.context.inflate(R.layout.item_guess_like_header,parent)
                return GuessLikeHeaderHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_comic_linear,parent)
                return ComicLinearHolder(v)
            }
        }
    }

    private var rangeCatalogHeader: Int = 0
    private var rangeCatalogItem: Int = 0
    private var rangeCatalogFooter: Int = 0
    private var rangeGuessLikeHeader: Int = 0
    var guessLikeClick: ((bean: RecommendBookBean) -> Unit)? = null
    var chapterClick : ((bean : CatalogItemBean) -> Unit)? = null
    var sortClick : ((sort : Int) -> Unit)? = null
    var addShelfClick : ((bean : ComicBookBean?) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        val rangeDetail = 1
        rangeCatalogHeader = rangeDetail + 1
        rangeCatalogItem = rangeCatalogHeader + catalog.size
        rangeCatalogFooter = rangeCatalogItem + 1
        rangeGuessLikeHeader = rangeCatalogFooter + 1
        var rangeGuessLikeItem = rangeGuessLikeHeader + guessLike.size

        if (position < rangeDetail)return VIEW_TYPE_DETAIL
        if (position < rangeCatalogHeader)return VIEW_TYPE_CATALOG_HEADER
        if (position < rangeCatalogItem)return VIEW_TYPE_CATALOG_ITEM
        if (position < rangeCatalogFooter)return VIEW_TYPE_CATALOG_FOOTER
        if (position < rangeGuessLikeHeader)return VIEW_TYPE_GUESS_LIKE_HEADER
        return VIEW_TYPE_GUESS_LIKE_ITEM
    }

    override fun getItemCount(): Int {
        var count = 1 //详情
        count ++ //章节列表头
        count += catalog.size
        count ++ //章节列表查看更多
        count ++//猜你喜欢
        count += if (guessLike.size > 6) 6 else guessLike.size
        return count
    }

    fun getCatalogByPosition(position: Int): CatalogItemBean? {
        if (catalog.isNotEmpty()) {
            return catalog[position - rangeCatalogHeader]
        }
        return null
    }

    fun getGuessLikeByPosition(position: Int) : RecommendBookBean? {
        if (guessLike.isNotEmpty()){
            return guessLike[position - rangeGuessLikeHeader]
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType){
            VIEW_TYPE_DETAIL -> {
                val h = holder as DetailHolder
                h.bind()
            }
            VIEW_TYPE_CATALOG_HEADER -> {
                val h = holder as CatalogHeaderHolder
                h.bind()
            }
            VIEW_TYPE_CATALOG_ITEM -> {
                val h = holder as CatalogHolder
                h.bind(getCatalogByPosition(position),chapterClick)
            }
            VIEW_TYPE_CATALOG_FOOTER -> {
                val h = holder as CatalogFooterHolder
                h.bind()
            }
            VIEW_TYPE_GUESS_LIKE_HEADER -> {
                val h = holder as GuessLikeHeaderHolder
                h.bind()
            }
            else -> {
                val h = holder as ComicLinearHolder
                val bean = getGuessLikeByPosition(position)?:return
                h.bindRecommend(bean,guessLikeClick)
            }
        }
    }

    fun setCatalog(data: MutableList<CatalogItemBean>, total : Int,sort: Int) {
        this.catalog.clear()
        this.catalog.addAll(data)
        this.chapterCount = total
        this.catalogSort = sort
        notifyDataSetChanged()
    }

    fun setGuessLike(data : List<RecommendBookBean>?){
        data?:return
        this.guessLike.clear()
        this.guessLike.addAll(data)
        notifyDataSetChanged()
    }

    fun setComic(data: ComicBookBean) {
        this.comic = data
        notifyDataSetChanged()
    }

    fun setSameCategory(list: List<RecommendBookBean>) {
        this.guessLike.addAll(0,list.shuffled())
        notifyDataSetChanged()
    }

    fun updateHistory() {
        notifyItemChanged(0)
    }

    inner class DetailHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val descr = itemView.find<TextView>(R.id.descr)
        private val author = itemView.find<TextView>(R.id.author)
        private val ivCover = itemView.find<ImageView>(R.id.ivCover)
        private val tvTitle = itemView.find<TextView>(R.id.tvTitle)
        private val tvTypeName = itemView.find<TextView>(R.id.tvTypename)
        private val layoutTags = itemView.find<LinearLayout>(R.id.layoutTags)
        private val btnRead = itemView.find<TextView>(R.id.btnRead)
        private val btnAddShelf = itemView.find<TextView>(R.id.btnAddShelf)
        private val tvStar = itemView.find<TextView>(R.id.tvStar)
        private val tvViewNum = itemView.find<TextView>(R.id.tvViewNum)
        fun bind() {
            val context = itemView.context

            comic?.let {
                ivCover.loadNovelCover(if (it.thumbX.isEmpty())it.bookcover else it.thumbX)
                tvTitle.text = it.title.trim()
                tvTypeName.text = "类型:${it.typename}"
                descr.text = it.description
                author.text = context.getString(R.string.author_with_label,it.author)
                tvStar.text = ComicUtils.getCommentStar(it.id)
                tvViewNum.text = ComicUtils.getReadNum(it.id).toString()
                ComicUtils.addSomeTags(layoutTags)

                val record = ReadRecordManager.getReadRecord(it.id)
                if (record != null) {
                    btnRead.text = "继续阅读"
                    btnRead.click {
                        ARouterManager.goReadActivity(it.context,record.bookId,record.chapterId)
                    }
                } else {
                    btnRead.click {
                        val first = catalog.getOrNull(0)
                        if (first != null) {
                            ARouterManager.goReadActivity(it.context,first.bookid,first.id)
                        }
                    }
                }
                if (isInShelf) {
                    btnAddShelf.text = "已在书架"
                    btnAddShelf.disable()
                } else {
                    btnAddShelf.enable()
                    btnAddShelf.text = "订阅追更"
                }
                btnAddShelf.click {
                    addShelfClick?.invoke(comic)
                }
            }
        }
    }

    inner class CatalogHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val btnSort = itemView.find<TextView>(R.id.sort)
        private val total = itemView.find<TextView>(R.id.total)
        private val status = itemView.find<TextView>(R.id.status_detail)
        fun bind() {
            val context = itemView.context
            btnSort.text = if (catalogSort == BeanConstants.SORT_DESC) context.getString(R.string.sort_asc) else context.getString(R.string.sort_desc)
            btnSort.click {
                if (catalogSort == BeanConstants.SORT_ASC) {
                    catalogSort = BeanConstants.SORT_DESC
                } else {
                    catalogSort = BeanConstants.SORT_ASC
                }
                sortClick?.invoke(catalogSort)
            }
            status.visibility = View.INVISIBLE
            total.text = context.getString(R.string.chapter_count,chapterCount)
        }
    }

    inner class CatalogFooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind() {
            itemView.click {
                comic?:return@click
                ARouterManager.goComicCatalogActivity(it.context,comic?.id)
            }
        }
    }

    inner class GuessLikeHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind() {
        }
    }
}