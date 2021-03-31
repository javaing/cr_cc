package com.aliee.quei.mo.ui.user.activity

import androidx.lifecycle.Observer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.EventModifyPwd
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.user.vm.AuthVModel
import com.aliee.quei.mo.utils.ToastUtil
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.disable
import com.aliee.quei.mo.utils.extention.enable
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.widget.view.panelSwitch.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_reset.*

@Route(path = Path.PATH_RESET)
class ResetActivity : BaseActivity(), TextWatcher {

    private val VM = AuthVModel()
    override fun getLayoutId(): Int {
        return R.layout.activity_reset
    }

    override fun afterTextChanged(s: Editable?) {
        val phone = editPhone_reset.text.toString()
        val password_orig = password1_orig.text.toString()
        val password = password1_reset.text.toString()
        if (phone.isEmpty() || password_orig.isEmpty() || password.isEmpty()) {
            btnReset.disable()
        } else {
            btnReset.enable()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun initData() {
        initVM()

    }

    private fun initVM() {
        VM.resetLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    RxBus.getInstance().post(EventModifyPwd())
                    toast("密码更改成功")
                   // ARouterManager.goLoginActivity(this)
                    finish()
                }
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Error -> {
                    disLoading()
                    val e = it.e
                    if (e is RequestException) {
                        if (e.code == 1002 ){
                            toast(e.msg)
                        }
                    }
                }
            }
        })
    }


    override fun initView() {
        initEditText()
        initClick()
        KeyboardUtil.showKeyboard(editPhone_reset)
    }

    private fun initAnim() {
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mTopInAnim.duration = 300
        mTopOutAnim.duration = 300
    }

    private fun initClick() {

        btnReset.click {
            val phone = editPhone_reset.text.toString()
            val password_orig = password1_orig.text.toString()
            val password = password1_reset.text.toString()
            VM.reset(this, phone, password_orig, password)
        }

        titleBack_reset.click {
//            ARouterManager.goLoginActivity(this)
            finish()
        }

    }

    private fun initEditText() {
        editPhone_reset.addTextChangedListener(this)
        password1_reset.addTextChangedListener(this)
    }


    private lateinit var mTopInAnim: Animation
    private lateinit var mTopOutAnim: Animation


    override fun getPageName() = "密码更改"
}