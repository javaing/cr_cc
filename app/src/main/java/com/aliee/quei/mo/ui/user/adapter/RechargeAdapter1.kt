package com.aliee.quei.mo.ui.user.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.haozhang.lib.SlantedTextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.PriceBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.utils.extention.*
import org.jetbrains.anko.find
import org.jetbrains.anko.imageResource
import java.text.SimpleDateFormat
import java.util.*

class RechargeAdapter1 : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData = mutableListOf<Any>()
    private var priceList = mutableListOf<PriceBean>()
    private var vipPriceList = mutableListOf<PriceBean>()
    private var vipVideoDayPriceList = mutableListOf<PriceBean>()

    var itemClick: ((bean: PriceBean) -> Unit)? = null
    var HeadClick: ((counter: Int) -> Unit)? = null
    var HelpClick: ((counter: Int) -> Unit)? = null
    var ComplaintClick: ((counter: Int) -> Unit)? = null

    companion object {
        const val VIEW_TYPE_BALANCE = 0
        const val VIEW_TYPE_GROUP_HEADER = 1
        const val VIEW_TYPE_ITEM_PRICE = 2
        const val VIEW_TYPE_ITEM_PRICE_H = 3
    }

    private var selected: PriceBean? = null

    fun setPrice(list: List<PriceBean>?) {
        list ?: return
        priceList.clear()
        priceList = list.filter { priceBean ->
            if (priceBean.isDefault == 1) {
                selected = priceBean
            }
            priceBean.isVip == 1
        }.toMutableList()
        vipVideoDayPriceList = list.filter {
            it.id == 28
        }.toMutableList()
        vipPriceList = list.filter {
            if (it.default > 0) {
                selected = it
            }
            it.isVip > 1 && it.id != 28
        }.toMutableList()
        mData.add(TitleBean(R.mipmap.ic_balance_coins, "书币充值"))
        mData.addAll(priceList)
        if (vipVideoDayPriceList.size > 0) {
            mData.add(TitleBean(R.mipmap.ic_balance_video_vip, "视频日卡"))
            mData.addAll(vipVideoDayPriceList)
        }
        mData.add(TitleBean(R.mipmap.ic_balance_vip, "VIP充值"))
        mData.addAll(vipPriceList)
        notifyDataSetChanged()
    }

    fun setUserInfo(userInfoBean: UserInfoBean?) {
        userInfoBean ?: return
        if (mData.getOrNull(0) is UserInfoBean) {
            mData.removeAt(0)
        }
        mData.add(0, userInfoBean)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_BALANCE -> {
                val v = parent.context.inflate(R.layout.item_userinfo_recharge, parent, false)
                return BalanceHolder(v)
            }
            VIEW_TYPE_GROUP_HEADER -> {
                val v = parent.context.inflate(R.layout.item_price_list_group_header, parent, false)
                return GroupTitleHolder(v)
            }
            VIEW_TYPE_ITEM_PRICE_H -> {
                val v = parent.context.inflate(R.layout.item_price_video_vip, parent, false)
                return VideoVipPriceHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_price1, parent, false)
                return PriceHolder(v)
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        val bean = mData.getOrNull(position)
        return when (bean) {
            is UserInfoBean -> VIEW_TYPE_BALANCE
            is TitleBean -> VIEW_TYPE_GROUP_HEADER
            else -> {
                val priceBean = bean as PriceBean
                if (priceBean.id == 28) {
                    VIEW_TYPE_ITEM_PRICE_H
                } else {
                    VIEW_TYPE_ITEM_PRICE
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = mData[position]
        when (bean) {
            is UserInfoBean -> {
                if (holder is BalanceHolder) holder.bind(bean)
            }
            is TitleBean -> {
                if (holder is GroupTitleHolder) holder.bind(bean)
            }
           else -> {
                val priceBean = bean as PriceBean
                if (priceBean.id == 28) {
                    if (holder is VideoVipPriceHolder) holder.bind(bean)
                } else {
                    if (holder is PriceHolder) holder.bind(bean)
                }

            }
        }
    }

    inner class VideoVipPriceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrice = itemView.find<TextView>(R.id.tvPrice)
        private val tvPrice1 = itemView.find<TextView>(R.id.tv_price1)
        private val tv_price2 = itemView.find<TextView>(R.id.tv_price2)
        private val tag_hot = itemView.find<View>(R.id.tag_hot)
        private val tag_promote = itemView.find<View>(R.id.tag_promote)
        private val layoutPrice = itemView.find<View>(R.id.layoutPrice)

        @SuppressLint("SetTextI18n")
        fun bind(bean: PriceBean) {
            tvPrice.text = "${bean.price.toInt()}元"
            tvPrice1.text = "${bean.price.toInt()}"
            tv_price2.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

            //request by 彭總
            tv_price2.gone()

            if (bean.isPromote == 1) {
                tag_promote.visibility = View.VISIBLE
            } else {
                tag_promote.visibility = View.GONE
            }

            if (bean.isHot == 1) {
                tag_hot.visibility = View.VISIBLE
            } else {
                tag_hot.visibility = View.GONE
            }

            itemView.click {
                selected = bean
                notifyDataSetChanged()
                itemClick?.invoke(bean)
            }
            layoutPrice.isActivated = bean != selected
        }
    }

    inner class PriceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrice = itemView.find<TextView>(R.id.tvPrice)
        private val tvAmount = itemView.find<TextView>(R.id.tvAmount)
        private val tvExtra = itemView.find<TextView>(R.id.tvExtra)
        private val layoutPrice = itemView.find<View>(R.id.layoutPrice)
        private val tag_hot = itemView.find<View>(R.id.tag_hot)
        private val tag_promote = itemView.find<View>(R.id.tag_promote)
        private val tvExtra_vip = itemView.find<View>(R.id.tvExtra_vip)
        private val rotate_textview = itemView.find<SlantedTextView>(R.id.rotate_textview)

        @SuppressLint("SetTextI18n")
        fun bind(bean: PriceBean) {
            val context = itemView.context
            val descriptions = bean.description?.split("|")
            val amountDis = descriptions!![0].replace("金币", "")
            val amounts = amountDis.split("+")

            if (bean.isVip > 1) {
                val vipName = descriptions[1].replace("会员", "")
                tvPrice.text = vipName
                tvAmount.text = "${bean.price.toInt()}元 ${bean.vipName}免费看"
                tvExtra.visibility = View.GONE
                tvExtra_vip.visibility = View.GONE
            } else {
                tvPrice.text = "${bean.price.toInt()}元"
                tvExtra.visibility = View.VISIBLE
                tvExtra_vip.visibility = View.VISIBLE

                if (amounts.size <= 1) { // tesppay 时 ，描述没有分隔符，进行特有判断
                    tvAmount.text = amountDis
                } else {
                    if (amounts[1] == "0") {
                        tvAmount.text = amounts[0]
                    } else {
                        tvAmount.text = amountDis
                    }
                }

                if (descriptions.size <= 1) {  // tesppay 时 ，描述没有分隔符，进行特有判断
                    tvExtra.text = descriptions[0]
                } else {
                    val moreDis = descriptions[1]
                    if (moreDis == "送0元") {
                        tvExtra.visibility = View.INVISIBLE
                    } else {
                        tvExtra.visibility = View.VISIBLE
                    }
                    if (moreDis.contains("多")) {
                        tvExtra.text = descriptions[1]
                    } else {
                        tvExtra.text = "多$moreDis"
                    }
                }

            }

            if (TextUtils.isEmpty(bean.getTag()!!.label)) {
                rotate_textview.visibility = View.GONE
            } else {
                rotate_textview.visibility = View.VISIBLE
            }

            if (bean.isPromote == 1) {
                tag_promote.visibility = View.VISIBLE
            } else {
                tag_promote.visibility = View.GONE
            }

            if (bean.isHot == 1) {
                tag_hot.visibility = View.VISIBLE
            } else {
                tag_hot.visibility = View.GONE
            }

//            bean.getTag()?.icon?.let {
//                GlideApp.with(ivTag)
//                    .asBitmap()
//                    .load(it)
//                    .into(ivTag)
//            }
            itemView.click {
                selected = bean
                notifyDataSetChanged()
                itemClick?.invoke(bean)
            }
            layoutPrice.isActivated = bean != selected
        }
    }

    inner class BalanceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivHead = itemView.find<ImageView>(R.id.headImg)
        private val ivHelp = itemView.find<ImageView>(R.id.helpBanner)
        private val tvNickName = itemView.find<TextView>(R.id.nickName)
        private val tvId = itemView.find<TextView>(R.id.userId)
        private val tvBalance = itemView.find<TextView>(R.id.balance)
        private val tvEndTime = itemView.find<TextView>(R.id.vipEndTime)
        private val tvComplaint = itemView.find<TextView>(R.id.complaint)

        var counter: Int = 0
        fun bind(bean: UserInfoBean) {
            val context = itemView.context
            ivHead.setImageResource(R.mipmap.ic_launcher1)
            ivHead.click {
                counter++
                HeadClick?.invoke(counter)

            }

            ivHelp.click {
                HelpClick?.invoke(1)
            }

            tvComplaint.click {
                ComplaintClick?.invoke(1)
            }

            tvNickName.text = if (bean.phone.isNullOrEmpty()) "游客" else bean.phone
            tvId.text = context.getString(R.string.user_id, bean.uid)
            tvBalance.text = bean.bookBean.toString()
            val vipEndTime = bean.vipEndtime
            if (vipEndTime > System.currentTimeMillis()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                tvEndTime.text = sdf.format(Date(vipEndTime))
            } else {
                tvEndTime.text = "未开通"
            }

            if (bean.phone.isNullOrEmpty()) {
                tvId.text = context.getString(R.string.user_id, bean.id)
                tvNickName.text = context.getString(R.string.guest)
            } else {
                tvId.text = context.getString(R.string.user_id, bean.uid)
                tvNickName.text = bean.phone
            }
        }
    }

    var TitleClick: ((text: String) -> Unit)? = null

    inner class GroupTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.find<ImageView>(R.id.icon)
        private val text = itemView.find<TextView>(R.id.text)
        fun bind(bean: TitleBean) {
            icon.imageResource = bean.iconRes
            text.text = bean.text
            text.click {
                TitleClick?.invoke(bean.text)
            }

        }
    }

    data class TitleBean(val iconRes: Int, val text: String)
}