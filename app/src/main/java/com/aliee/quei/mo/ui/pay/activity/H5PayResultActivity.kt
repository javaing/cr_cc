package com.aliee.quei.mo.ui.pay.activity

import androidx.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventRechargeSuccess
import com.aliee.quei.mo.component.EventUserInfoUpdated
import com.aliee.quei.mo.data.Global
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.common.adapter.GuessLikeAdapter
import com.aliee.quei.mo.ui.common.vm.GuessLikeVModel
import com.aliee.quei.mo.ui.pay.vm.OrderVModel
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.rxjava.RxBus
import kotlinx.android.synthetic.main.layout_title.*
import kotlinx.android.synthetic.main.pay_result_h5.*


@Route(path = Path.PATH_H5_PAY_RESULT)
class H5PayResultActivity : BaseActivity(){
    private val guessLikeVModel = GuessLikeVModel()
    private val adapter = GuessLikeAdapter()

    var VM = OrderVModel()

    override fun getLayoutId() = R.layout.pay_result_h5

    @Autowired
    @JvmField
    var tradeNo = ""

    @Autowired
    @JvmField
    var successUrl = ""

    @Autowired
    @JvmField
    var bookid : Int = 0
    var success = false
    override fun initData() {
        doDelay({
            VM.checkH5PayResult(this,tradeNo)
        },2000)
        guessLikeVModel.getGuessLike(this,bookid,CommonDataProvider.instance.currentReading?.typename?:"")
    }

    override fun initView() {
        statuslayout.setErrorText(getString(R.string.reload_pay_status))
        initVM()
        titleBack.click { finish() }
        btnAction.click { finish() }
        titleText.text = getString(R.string.pay_result)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        toast("onNewIntent")
    }

    private fun showSuccess(){
        imgStatus.setImageResource(R.mipmap.ic_pay_success)
        textStatus.text = getString(R.string.pay_success)
        btnAction.click {
            if (!successUrl.isNullOrEmpty()) {

                ARouter.getInstance().build(Uri.parse(successUrl))

                    .navigation(this)
            }
            finish()
        }
        success = true
        SharedPreUtils.getInstance().putBoolean("hasRecharge",true)
    }

    private var retry = 0
    private fun showFailed(){
        imgStatus.setImageResource(R.mipmap.ic_pay_err)
        textStatus.text = getString(R.string.pay_failed)
        btnAction.click {
            finish()
        }
        if (retryTime < 3) {
            doDelay({
                retryTime ++
            },1000)
        }
    }

    private var retryTime = 0
    private fun initVM() {
        VM.checkH5PayResultLiveData.observe(this, Observer {
            when(it?.status){
                Status.Start -> {
                    retry ++
                    statuslayout.showLoading()
                }
                Status.Success -> {
                    statuslayout.showContent()
                    if(it.data == true){
                        showSuccess()
                    } else {
                        showFailed()
                    }
                }
                Status.Error -> {
                    statuslayout.showError {
                        if (it is TextView){
                            it.text = getString(R.string.reload_pay_status)
                            VM.checkH5PayResult(this,tradeNo)
                        }
                    }
                }
            }
        })
        guessLikeVModel.sameCategoryLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    adapter.setSameCategory(it.data.map {
                        RecommendBookBean(it.author,it.thumb,it.id,it.description,it.status,it.thumb,it.title,it.typename)
                    }.filterNot {
                        val recordIds = CommonDataProvider.instance.getHistoryBookIds()
                        recordIds.contains(it.id.toString())
                    })
                }
            }
        })
        guessLikeVModel.guessLikeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    adapter.setGuessLike(it.data?.filterNot {
                        val recordIds = CommonDataProvider.instance.getHistoryBookIds()
                        recordIds.contains(it.id.toString())
                    })
                }
            }
        })
    }

    override fun finish() {
        Global[Global.KEY_HAS_OPEN_H5_PAY] = "false"
        Global[Global.KEY_TRADE_NO] = ""
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(success){
            RxBus.getInstance().post(EventUserInfoUpdated())
            RxBus.getInstance().post(EventRechargeSuccess())
        }
    }

    override fun getPageName() = "H5支付结果页面"
}