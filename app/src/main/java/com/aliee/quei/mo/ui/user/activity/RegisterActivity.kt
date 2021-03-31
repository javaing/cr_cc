package com.aliee.quei.mo.ui.user.activity

import androidx.lifecycle.Observer
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.disable
import com.aliee.quei.mo.utils.extention.enable
import com.aliee.quei.mo.widget.view.panelSwitch.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.btnLogin
import kotlinx.android.synthetic.main.activity_register.editPhone
import kotlinx.android.synthetic.main.activity_register.password1
import kotlinx.android.synthetic.main.activity_register.title_right_but


@Route(path = Path.PATH_REGISTER)
class RegisterActivity : BaseActivity(), TextWatcher {
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
    override fun getLayoutId() = R.layout.activity_register

    override fun initData() {
        initVM()
    }

    private fun initVM() {
        VM.registerLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (url.isNotEmpty()) {
                        toast("注册成功")
                        ARouter.getInstance().build(Uri.parse(url)).navigation(this)
                    }
                    CommonDataProvider.instance.setToken(editPhone.text.toString())
                    CommonDataProvider.instance.saveUserTempType(0)
                    if (to == 1) {
                        ARouterManager.goMainActivity(this, showPage = ARouterManager.TAB_SHOP)
                    }
                    finish()
                }
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Error -> {

                }
                Status.TokenError->{
                    launchVModel.registerToken(this)
                }
            }
        })
        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    val phone = editPhone.text.toString()
                    val password = password1.text.toString()
                    VM.register(this, phone, password)
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
        tvcsAssist.visibility = View.INVISIBLE
    }


    private fun initClick() {
        r_titleBack.click {
            finish()
        }

        btnLogin.click {
            val phone = editPhone.text.toString()
            val password = password1.text.toString()
            VM.register(this, phone, password)
        }
        goLogin.click {
            ARouterManager.goLoginActivity(this)
            finish()
        }
        title_right_but.click { finish() }

//        tvGuest.click {
//            if (url.isNotEmpty()) {
//                ARouter.getInstance().build(Uri.parse(url)).navigation(this)
//            }
//            finish()
//        }
    }

    private fun initEditText() {
        editPhone.addTextChangedListener(this)
        password1.addTextChangedListener(this)
    }

    override fun getPageName() = "登录界面"
}