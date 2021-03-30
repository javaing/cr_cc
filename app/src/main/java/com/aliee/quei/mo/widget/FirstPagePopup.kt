package com.aliee.quei.mo.widget

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseDialogFragment
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.load
import kotlinx.android.synthetic.main.dialog_first_page_popup.*

class FirstPagePopup : BaseDialogFragment(){
    companion object {
        fun newInstance(image : String?,url : String?):FirstPagePopup{
            val dialog = FirstPagePopup()
            val args = Bundle()
            args.putString("image",image)
            args.putString("url",url)
            dialog.arguments = args
            return dialog
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_first_page_popup
    }

    override fun initView() {
        val image = arguments?.getString("image")
        val url = arguments?.getString("url")
        img.load(image)
        img.click {
            ARouter.getInstance().build(url).navigation(it.context)
            dismiss()
        }

        close.click { dismiss() }
    }

    override fun initData() {
    }
}