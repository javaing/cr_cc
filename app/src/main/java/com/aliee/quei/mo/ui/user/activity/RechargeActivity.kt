package com.aliee.quei.mo.ui.user.activity

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventRechargeSuccess
import com.aliee.quei.mo.data.Global
import com.aliee.quei.mo.data.Global.KEY_HAS_OPEN_H5_PAY
import com.aliee.quei.mo.data.Global.KEY_TRADE_NO
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.data.bean.PriceBean
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.net.Web
import com.aliee.quei.mo.net.WebListener
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.pay.dialog.PayWayDialog
import com.aliee.quei.mo.ui.pay.dialog.PayWayTestDialog
import com.aliee.quei.mo.ui.user.adapter.RechargeAdapter1
import com.aliee.quei.mo.ui.user.vm.RechargeVModel
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_recharge.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


@Route(path = Path.PATH_USER_RECHARGE)
class RechargeActivity : BaseActivity(), PayWayDialog.OnPayWayChooseListener, PayWayTestDialog.OnPayWayTestChooseListener {

    private var payType: String? = ""

    private lateinit var api: IWXAPI

    //    val checkSum = "041ef50165edb75dcd0de74b8752cf7a"
    val checkSum = "8781ec2c0b53b1a4bae51f0513adb783"


    private lateinit var payWayBean: PayWayBean
    override fun onPayWaySelected(bean: PayWayBean) {
        payWayBean = bean
        payType = bean.sdk
        payInfo(bean)

    }

    override fun onPayWayTestSelected(bean: PayWayBean) {
        payType = bean.sdk
        if (bean.sdk.equals("Weixin")) {
            priceBean?.let {
                //弹出是否支付成功弹窗
                show()
                VM.createOrder(this, bean.id, it.id, bookid)
            } // 這邊能確保一定有priceBean才執行
        } else if (bean.sdk.equals("appWeixin")) {
            priceBean?.let {
                ApiConstants.APP_ID = bean.appid
                api = WXAPIFactory.createWXAPI(this, bean.appid, false)
                api.registerApp(bean.appid)

                VM.createOrder(this, bean.id, it.id, bookid)

                SharedPreUtils.getInstance().putString("appid", ApiConstants.APP_ID)
                show()

            }
        } else {
            priceBean?.let {
                VM.createOrder(this, bean.id, it.id, bookid)
                show()
            }
        }
    }


    private fun payInfo(bean: PayWayBean){
        if (bean.sdk.equals("Weixin")) {
            priceBean?.let {
                //弹出是否支付成功弹窗
                show()

                VM.createOrder(this, bean.id, it.id, bookid)
            } // 這邊能確保一定有priceBean才執行
        } else if (bean.sdk.equals("appWeixin")) {
            priceBean?.let {
                ApiConstants.APP_ID = bean.appid
                api = WXAPIFactory.createWXAPI(this, bean.appid, false)
                api.registerApp(bean.appid)

                VM.createOrder(this, bean.id, it.id, bookid)

                SharedPreUtils.getInstance().putString("appid", ApiConstants.APP_ID)
                show()
            }
        } else {
            priceBean?.let {
                VM.createOrder(this, bean.id, it.id, bookid)
                show()
            }
        }
    }

    @Autowired
    @JvmField
    var successUrl: String? = null

    @Autowired
    @JvmField
    var bookid: Int = -1

    @Autowired
    @JvmField
    var isBook: Boolean = true

    @Autowired
    @JvmField
    var typename: String = ""

    private val VM = RechargeVModel()
    private val launchVModel = LaunchVModel()
    private val adapter = RechargeAdapter1()
    private var tokenFlag = 0
    override fun getLayoutId() = R.layout.activity_recharge

    private var priceBean: PriceBean? = null

    override fun initData() {
        VM.getList(this)
        VM.getBalance(this)
//        VM.getPayWayConfig(this,checkSum)
    }

    override fun initView() {
        initTitle()
        initVM()
        initEvent()
        initRecyclerView()
    }

    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
                .subscribe {
                    if (it is EventRechargeSuccess) {
                        VM.getBalance(this)
                    }
                }

    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (adapter.getItemViewType(position)) {
                    RechargeAdapter1.VIEW_TYPE_ITEM_PRICE -> return 1
                    RechargeAdapter1.VIEW_TYPE_ITEM_PRICE_H -> return 2
                    else -> return 2
                }
            }
        }
        var test: Boolean? = false
        recyclerView.layoutManager = layoutManager
        adapter.itemClick = {
            priceBean = it

            if (test == false) {
                if (dialog == null) {
                    dialog = PayWayDialog()
                    dialog?.onPayWayChooseListener = this
                }
                VM.getPayWayConfig(this, checkSum, it.id)
                dialog?.show(supportFragmentManager, "payMethod")
            } else {

                if (dialog_Test == null) {
                    dialog_Test = PayWayTestDialog()
                    dialog_Test?.onPayWayChooseListener = this
                }
                VM.getPayWayConfigTest(this, checkSum)
                dialog_Test?.show(supportFragmentManager, "payMethod")
            }

        }

        /*adapter.HeadClick = {
            var counter = it
            Log.d("RechargeActivity", "ClickCount:" + counter)
            if (counter >= 10) {
                paytest.visibility = View.INVISIBLE
                paytest.click {
                    VM.getTest(this)
                    VM.getPayWayConfigTest(this,checkSum)
                    test = true
                    counter = 0
                    paytest.visibility = View.INVISIBLE
                }

                adapter.TitleClick = {
                    if (it.equals("书币充值")) {
                        VM.getTest(this)
                        VM.getPayWayConfigTest(this,checkSum)
                        test = true
                    }

                }

            }
        }*/

        var counter: Int = 0
        titleText.click {
            counter++
            if (counter >= 10) {

                adapter.TitleClick = {
                    if (it.equals("书币充值")) {
                        VM.getTest(this)

                        test = true
                        counter = 0
                    }

                }

            }

        }

        adapter.HelpClick = {
            val dialog = AlertDialog.Builder(this)
                    .setMessage(getString(R.string.csenter))
                    .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                        dialog.dismiss()
                        ARouterManager.goCustomServiceActivity(this)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()

                    }.create()
            dialog.show()
        }

        adapter.ComplaintClick = {
            val dialog = AlertDialog.Builder(this)
                    .setMessage(getString(R.string.csenter))
                    .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                        dialog.dismiss()
                        ARouterManager.goCustomServiceActivity(this)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()

                    }.create()
            dialog.show()
        }

    }

    var dialog: PayWayDialog? = null
    var dialog_Test: PayWayTestDialog? = null
    private fun initVM() {
        VM.priceListLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    adapter.setPrice(it.data)
                }
                Status.TokenError->{
                    tokenFlag =3
                    launchVModel.registerToken(this)
                }
            }
        })

        VM.payWayLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
//                    Log.d("RechargeActivity", "PayWay:" + it.data)
                    dialog?.refreshPayWay()
                }
            }
        })

        VM.payWayTestLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    Log.d("RechargeActivity", "PayWayTest:" + it.data)
                    dialog_Test?.refreshPayWay()
                }
            }
        })

        VM.createOrderLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    val bean = it.data
                    bean ?: return@Observer
                    bean.payurl ?: return@Observer
                    Log.d("RechargeActivity", "Payurl:" + bean.payurl)
                    val data: Array<String> = bean.payurl.split("\\&".toRegex()).toTypedArray()
                    Log.d("RechargeActivity", "PayMode:" + data[8])
                    val payMode = data[8]
                    if (payMode.equals("paymode=1")) {
                       // val uri = Uri.parse(URLEncoder.encode(bean.payurl, "utf-8"))
                       val uri = Uri.parse(bean.payurl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    } else {
                        ARouterManager.goWebActivity2(
                                this,
                                bean.payurl,
                                "",
                                referer = bean.referer ?: ""
                        )

                        Log.d("referer","referer:${bean.referer}")
                    }

                    //http://deqfvfw.cn/cartoon/pay/recharge?money=30.00&paymentId=10233&orderId=CTT161051919298568383&isVip=1&paySdk=kaiyiyunWeixin&callback=http://deqfvfw.cn&isTemp=1&fromSite=D0&paymode=1&paymentIdInput=10233
                    //http://cbk3ak7xjp.huijuhb.com/cartoon/pay/recharge?money=50.00&paymentId=30729&orderId=CTD161051927596969612&isVip=1&paySdk=kaiyiyunWeixin&callback=http://cbk3ak7xjp.huijuhb.com&isTemp=1&fromSite=D0&paymode=1&paymentIdInput=30729
//                    if (payType.equals("Weixin") || payType.equals("Alipay")) {
//                        if (bean.payurl.contains("juhe")) {
//                            ARouterManager.goWebActivity2(
//                                    this,
//                                    bean.payurl,
//                                    "",
//                                    referer = bean.referer ?: ""
//                            )
//                        } else {
//                            val HTTPURL = SharedPreUtils.getInstance().getString("fakeDomain")
//                            val uri = Uri.parse(HTTPURL + URLEncoder.encode(bean.payurl, "utf-8"))
//                            val intent = Intent(Intent.ACTION_VIEW, uri)
//                            startActivity(intent)
//                        }
//
//                    }  else  if (payType.equals("appWeixin")){
//
//                        callPay(bean.payurl)
//
//                    }else {
//                        val HTTPURL = SharedPreUtils.getInstance().getString("fakeDomain")
//                        val uri = Uri.parse(HTTPURL + URLEncoder.encode(bean.payurl, "utf-8"))
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                        ARouterManager.goWebActivity(
//                                this,
//                                bean.payurl,
//                                "",
//                                referer = bean.referer ?: ""
//                        )
//                    }
                }

                Status.TokenError->{
                    tokenFlag = 1
                    launchVModel.registerToken(this)
                }
            }
        })
        VM.balanceLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> adapter.setUserInfo(it.data)
                Status.TokenError -> {
                    tokenFlag = 2
                    launchVModel.registerToken(this)
                }
            }
        })

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    when (tokenFlag) {
                        1 -> {
                            payInfo(payWayBean)
                        }
                        2 -> {
                            VM.getBalance(this)
                        }
                        3 -> {
                            VM.getList(this)
                        }
                    }

                }
            }
        })
    }

    private fun initTitle() {
        titleText.text = getString(R.string.title_recharge)
        titleBack.click { onBackPressed() }
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        Observable.timer(1, TimeUnit.SECONDS)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
                .subscribe {
                    if (this.isForground) {
                        val outTradeNo = Global[KEY_TRADE_NO] ?: return@subscribe
                        if (Global[KEY_HAS_OPEN_H5_PAY].equals("true") && outTradeNo.isNotEmpty()) {
                            ARouterManager.goH5PayResultActivity(this, outTradeNo, successUrl ?: "")
                        }
                    }
                }
        RxBus.getInstance().post(EventRechargeSuccess())
    }


    private fun show() {
        var dialog = AlertDialog.Builder(this)
                .setTitle("支付结果")
                .setMessage("是否已完成支付？")
                .setNegativeButton("已支付") { dialog, _ ->
                    dialog.dismiss()
                    val tlength = CommonDataProvider.instance.getToken()
                    if (tlength.length > 11) {
                        ARouterManager.goRegister(this)
                        finish()
                    } else {
                        VM.getBalance(this)
                    }
                }
                .setPositiveButton("未支付") { dialog, _ ->

                    dialog.dismiss()
                }
        dialog.create()
        dialog.show()
    }

    private fun callPay(url: String) {
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body?.string()
                    val json = JSONObject(resStr)
//                    Log.d("RechargeActivity", "返回資料：" + resStr.toString())
                    val rtnCode = json.getString("code")
//                    Log.d("RechargeActivity", "返回值：" + rtnCode)

                    try {
                        if (rtnCode.equals("0")) {
                            val req = PayReq()
                            //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                            req.appId = json.getString("appid") //应用ID
                            req.partnerId = json.getString("partnerid") //商户号
                            req.prepayId = json.getString("prepayid") //预支付交易会话ID
                            req.nonceStr = json.getString("noncestr")
                            req.timeStamp = json.getString("timestamp")
                            req.packageValue = json.getString("package")
                            req.sign = json.getString("sign")
                            req.extData = "app data" // optional

                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                            api!!.sendReq(req)
//                            runOnUiThread {
//                                Toast.makeText(applicationContext, "调起支付结果:" + result, Toast.LENGTH_SHORT).show()
//                            }
                        } else {
                            Log.d("PAY_GET", "返回错误" + json!!.getString("msg"))

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    Log.d("RechargeActivity", "异常：" + e.message)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {}
        })
        web.Get_Data(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        Global[KEY_HAS_OPEN_H5_PAY] = "false"
        Global[KEY_TRADE_NO] = ""
    }

    override fun getPageName() = "充值页面"

}
