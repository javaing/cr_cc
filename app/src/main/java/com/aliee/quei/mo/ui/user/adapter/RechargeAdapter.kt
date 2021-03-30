package com.aliee.quei.mo.ui.user.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
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

class RechargeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData = mutableListOf<Any>()
    private var priceList = mutableListOf<PriceBean>()
    private var vipPriceList = mutableListOf<PriceBean>()

    var itemClick: ((bean: PriceBean) -> Unit)? = null
    var HeadClick: ((counter: Int) -> Unit)? = null
    var HelpClick: ((counter: Int) -> Unit)? = null
    var ComplaintClick: ((counter: Int) -> Unit)? = null

    companion object {
        const val VIEW_TYPE_BALANCE = 0
        const val VIEW_TYPE_GROUP_HEADER = 1
        const val VIEW_TYPE_ITEM_PRICE = 2
    }

    private var selected: PriceBean? = null

    fun setPrice(list: List<PriceBean>?) {
        list ?: return
        priceList.clear()
        priceList = list.filter { priceBean ->
            if (priceBean.default > 0) {
                selected = priceBean
            }
            priceBean.isVip == 1
        }.toMutableList()
        vipPriceList = list.filter {
            if (it.default > 0) {
                selected = it
            }
            it.isVip > 1
        }.toMutableList()
        mData.add(TitleBean(R.mipmap.ic_balance_coins, "书币充值"))
        mData.addAll(priceList)
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
            else -> {
                val v = parent.context.inflate(R.layout.item_price, parent, false)
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
            else -> VIEW_TYPE_ITEM_PRICE
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
            is PriceBean -> {
                if (holder is PriceHolder) holder.bind(bean)
            }
        }
    }

    inner class PriceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrice = itemView.find<TextView>(R.id.tvPrice)
        private val tvAmount = itemView.find<TextView>(R.id.tvAmount)
        private val tvExtra = itemView.find<TextView>(R.id.tvExtra)
        private val ivTag = itemView.find<ImageView>(R.id.ivTag)
        private val layoutPrice = itemView.find<View>(R.id.layoutPrice)
        private val tvPrice_s = itemView.find<TextView>(R.id.tvPrice_s)
        private val tvExtra_vip = itemView.find<TextView>(R.id.tvExtra_vip)
        private val rotate_textview = itemView.find<SlantedTextView>(R.id.rotate_textview)
        fun bind(bean: PriceBean) {
            val context = itemView.context
            tvPrice.text = "${bean.price.toInt()}元"
            tvPrice_s.text = "(多送${bean.givePrice}元)"
            tvAmount.text = "${bean.realBean}金币"
            tvExtra.text = "赠送${bean.giveBean}金币"
            if (bean.isVip > 1) {
                tvExtra_vip.show()
                tvExtra_vip.text = bean.description?.split("|")?.getOrNull(1)
                tvAmount.text = bean.description?.split("|")?.getOrNull(0)
                tvExtra.text = "${bean.vipName}内免费阅读"
            } else {
                tvAmount.text = "${bean.realBean}金币"
                tvExtra_vip.gone()
            }
            if (bean.givePrice == 0) {
                tvPrice_s.gone()
                if (bean.isVip > 1) {
                    tvExtra.show()
                } else {
                    tvExtra.invisible()
                }
            } else {
                tvPrice_s.show()
                tvExtra.show()
            }


            if (TextUtils.isEmpty(bean?.getTag()!!.label)) {
                rotate_textview.visibility = View.GONE
            } else {
                rotate_textview.visibility = View.VISIBLE
                rotate_textview.text = bean?.getTag()!!.label
            }

            when (bean.sort) {
                2, 3, 5 -> {
                    ivTag.visibility = View.VISIBLE
                }
                else -> {
                    ivTag.visibility = View.GONE
                }
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
            val vipEndTime = bean.vipEndtime ?: 0
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