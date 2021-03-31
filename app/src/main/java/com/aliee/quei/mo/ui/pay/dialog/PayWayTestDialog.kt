package com.aliee.quei.mo.ui.pay.dialog

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseDialogFragment
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.ui.pay.adapter.PayWayTestAdapter
import kotlinx.android.synthetic.main.dialog_pay_way.*

class PayWayTestDialog : BaseDialogFragment(){

    override fun getLayoutId(): Int {
        return R.layout.dialog_pay_way
    }

    override fun initView() {
        initRecyclerView()
    }

    val adapter = PayWayTestAdapter()
    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity, OrientationHelper.HORIZONTAL, false)
        adapter.itemClick = {
            dismiss()
            onPayWayChooseListener?.onPayWayTestSelected(it)
        }
    }

    override fun initData() {
    }

    fun refreshPayWay() {
        adapter.notifyDataSetChanged()
    }

    var onPayWayChooseListener : OnPayWayTestChooseListener? = null

    interface OnPayWayTestChooseListener {
        fun onPayWayTestSelected(bean : PayWayBean)
    }
}