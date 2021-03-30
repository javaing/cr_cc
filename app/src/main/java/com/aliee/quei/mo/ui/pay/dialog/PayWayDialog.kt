package com.aliee.quei.mo.ui.pay.dialog

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseDialogFragment
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.ui.pay.adapter.PayWayAdapter
import kotlinx.android.synthetic.main.dialog_pay_way.*

class PayWayDialog : BaseDialogFragment(){

    override fun getLayoutId(): Int {
        return R.layout.dialog_pay_way
    }

    override fun initView() {
        initRecyclerView()
    }

    val adapter = PayWayAdapter()
    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity,OrientationHelper.HORIZONTAL,false)
        adapter.itemClick = {
            dismiss()
            onPayWayChooseListener?.onPayWaySelected(it)
        }
    }

    override fun initData() {
    }

    fun refreshPayWay() {
        adapter.notifyDataSetChanged()
    }

    var onPayWayChooseListener : OnPayWayChooseListener? = null

    interface OnPayWayChooseListener {
        fun onPayWaySelected(bean : PayWayBean)
    }
}