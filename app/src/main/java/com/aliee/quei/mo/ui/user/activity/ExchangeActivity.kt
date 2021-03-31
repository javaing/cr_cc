package com.aliee.quei.mo.ui.user.activity

import androidx.lifecycle.Observer

import android.text.Editable
import android.text.TextWatcher
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.EventUserInfoUpdated
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.user.vm.BillVModel
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.disable
import com.aliee.quei.mo.utils.extention.enable
import com.aliee.quei.mo.utils.rxjava.RxBus

import kotlinx.android.synthetic.main.activity_exchange.*


@Route(path = Path.PATH_USER_EXCHANGE)
class ExchangeActivity : BaseActivity(), TextWatcher{
    private val VM = BillVModel()
    private val launchVModel = LaunchVModel()

    override fun getLayoutId(): Int {
        return R.layout.activity_exchange
    }

    override fun initData() {
//        VM.loadList(this)
        initVM()
    }

    override fun initView() {
//        initTitle()
//        initRecyclerView()
        initEditText()
        initClick()

//        initRefresh()
    }

    private fun initClick() {
        btnExchange.click {
            val exchangeCode = editExchageCode.text.toString()
            VM.sendExange(this,exchangeCode)
        }

        titleBack.click { finish() }
    }

    private fun initVM() {
        VM.exchangeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Error -> {
                    val e = it.e
                    if (e is RequestException) {
//                        if (e.code == 3011) {
                            toast(e.msg)
//                        }
                    }
                }
                Status.Start -> {

                }
                Status.Success -> {
                    toast("兑换成功")
                    RxBus.getInstance().post(EventUserInfoUpdated())
                    finish()
                }
                Status.TokenError->{
                    launchVModel.registerToken(this)
                }


            }
        })

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    val exchangeCode = editExchageCode.text.toString()
                    VM.sendExange(this,exchangeCode)
                }
            }
        })
    }

    private fun initRecyclerView() {

    }

    private fun initTitle() {
        titleBack.click { finish() }
        titleText.text = "收入明细"
    }

    private fun initEditText() {
        editExchageCode.addTextChangedListener(this)
    }

    override fun getPageName() = "兌換碼"
    override fun afterTextChanged(p0: Editable?) {
        val code = editExchageCode.text.toString()
        if (code.isEmpty()) {
            btnExchange.disable()
        } else {
            btnExchange.enable()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
}