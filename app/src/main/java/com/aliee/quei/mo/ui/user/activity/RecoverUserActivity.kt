package com.aliee.quei.mo.ui.user.activity

import android.arch.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventUserInfoUpdated
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.user.vm.RecoverUserVModel
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.elvishew.xlog.BuildConfig
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import kotlinx.android.synthetic.main.activity_recover.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path=Path.PATH_USER_RECOVER)
class RecoverUserActivity : BaseActivity(){
    private val VM = RecoverUserVModel()
    private val launchVModel = LaunchVModel()
    override fun getLayoutId(): Int {
        return R.layout.activity_recover
    }

    override fun initData() {
    }

    override fun initView() {
        titleBack.click { onBackPressed() }
        titleText.text = getString(R.string.find_lost_account)

        if (BuildConfig.DEBUG) {
//            etOrderNo.setText("CT156051411876788606")
            etOrderNo.setText("CT156051818853399360")
        }
        btnRecover.click {
            val tradeNo = etOrderNo.text.toString()
            if (tradeNo.isEmpty())return@click
            VM.recoverUser(this,tradeNo)
        }
        customService.click {
            ARouterManager.goCustomServiceActivity(it.context)
        }
        initVM()
    }

    private fun initVM() {
        VM.recoverUserLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    val bean = it.data?:return@Observer
                    if (bean.temp == 0) { //注册用户
                        tvAccount.show()
                        tvAccount.text = getString(R.string.yout_account_click_login,bean.uid)
                        tvAccount.click {
                            ARouterManager.goLoginActivity(this,bean.uid?:"")
                            finish()
                        }
                    } else { //游客
                        CommonDataProvider.instance.setToken(bean.uid?:"")
                        RxBus.getInstance().post(EventUserInfoUpdated())
                        toast(getString(R.string.has_login_find_account))
                        finish()
                    }
                }
                Status.TokenError->{
                    launchVModel.registerToken(this)
                }
            }
        })
        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    val tradeNo = etOrderNo.text.toString()
                    if (tradeNo.isEmpty())return@Observer
                    VM.recoverUser(this,tradeNo)
                }
            }
        })
    }

    override fun getPageName() = "找回用户"
}