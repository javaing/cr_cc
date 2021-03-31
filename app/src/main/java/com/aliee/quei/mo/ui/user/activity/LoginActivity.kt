package com.aliee.quei.mo.ui.user.activity

import androidx.lifecycle.Observer
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.user.vm.AuthVModel
import com.aliee.quei.mo.utils.extention.*
import com.aliee.quei.mo.widget.view.panelSwitch.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_login.*

@Route(path = Path.PATH_LOGIN)
class LoginActivity : BaseActivity(), TextWatcher {
    @Autowired
    @JvmField
    var phone: String = ""

    @Autowired
    @JvmField
    var url: String = ""
    @Autowired
    @JvmField
    var to: Int = 0

    override fun afterTextChanged(s: Editable?) {
        val phone = editPhone.text.toString()
        val password = password1.text.toString()
        if (phone.isEmpty() || password.isEmpty()) {
            btnLogin.disable()
        } else {
            btnLogin.enable()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private val VM = AuthVModel()
    private val launchVModel = LaunchVModel()
    override fun getLayoutId() = R.layout.activity_login

    override fun initData() {
        initVM()
    }

    private fun initVM() {
        VM.loginLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (url.isNotEmpty()) {
                        toast("登录成功")
                        ARouter.getInstance().build(Uri.parse(url)).navigation(this)
                    }
                    CommonDataProvider.instance.setToken(editPhone.text.toString())
                    CommonDataProvider.instance.saveUserTempType(0)
                    finish()
                }
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.TokenError->{
                    launchVModel.registerToken(this)
                }
                Status.Error->{

                }
            }
        })

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    val phone = editPhone.text.toString()
                    val password = password1.text.toString()
                    VM.login(this, phone, password)
                }
            }
        })
    }

    override fun initView() {

        initEditText()
        initClick()
        if (phone.isNotEmpty()) {
            editPhone.setText(phone)
            password1.requestFocus()
            KeyboardUtil.showKeyboard(password1)
        }

    }

    private fun initClick() {
        titleBack.click {
            if (to == 1) {
                VM.tempLogin(this)
                ARouterManager.goMainActivity(this, showPage = ARouterManager.TAB_SHOP)
            }
            finish()
        }
        btnLogin.click {
            val phone = editPhone.text.toString()
            val password = password1.text.toString()
            VM.login(this, phone, password)
        }
        goRegister.click {
            ARouterManager.goRegister(this)
            finish()
        }

        goReset.click {
            ARouterManager.goReset(this)

        }

        title_right_but.click { finish() }

        tvGuest.click {
            if (url.isNotEmpty()) {
                ARouter.getInstance().build(Uri.parse(url)).navigation(this)
            }
            finish()
        }
    }

    private fun initEditText() {
        editPhone.addTextChangedListener(this)
        password1.addTextChangedListener(this)
    }

    override fun getPageName() = "登录界面"
}