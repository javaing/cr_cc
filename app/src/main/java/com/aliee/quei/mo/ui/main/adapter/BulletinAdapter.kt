package com.aliee.quei.mo.ui.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.BulletinInfo
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.loadNovelCover
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class BulletinAdapter : RecyclerView.Adapter<BulletinAdapter.BulletinLinearHolder>(){
    private var mData: ArrayList<BulletinInfo> = ArrayList()
    var itemClick : ((title: String) -> Unit)? = null

    fun setData(list : ArrayList<BulletinInfo>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletinLinearHolder {
        val v = parent.context.inflate(R.layout.item_bulletin_linear,parent,false)

        return BulletinLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: BulletinLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindBulletin(item)
    }

    inner class BulletinLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.find<ImageView>(R.id.img_bulletin)
        val item= itemView.find<TextView>(R.id.bulletin_Item)
        val title = itemView.find<TextView>(R.id.bulletin_desc)
        val date = itemView.find<TextView>(R.id.bulletin_date)

        private val sdf = SimpleDateFormat("yyyy-MM-dd")
        fun bindBulletin(bean : BulletinInfo){
            image.loadNovelCover(bean.imagepath)
            if(bean.tag.toInt() == 1){
                item.text = "公告"
            }else if(bean.tag.toInt() == 2){
                item.text = "活动"
            }
            title.text = bean.title
            val releaseDay: Long = bean.releaseDate.toLong()
            date.text = sdf.format(Date(releaseDay))
            image.click {
                itemClick?.invoke(bean.id)
            }

        }
    }



}