package com.aliee.quei.mo.widget

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aliee.quei.mo.R
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.dialog_custom.*

/**
 * Created by Administrator on 2018/5/9 0009.
 */
class ConfirmDialog : DialogFragment(){
    companion object {
        fun newInstance(title: String,content: String): ConfirmDialog {
            val dialog = ConfirmDialog()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("content", content)
            dialog.arguments = bundle
            return dialog
        }
    }

    var cancelClick: ((dialog : ConfirmDialog) -> kotlin.Unit)? = null
    var confirmClick: ((dialog : ConfirmDialog) -> kotlin.Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onStart() {
        super.onStart()
        //点击window外的区域 是否消失
        dialog.setCanceledOnTouchOutside(false)
        val dialogWindow = dialog.window
        dialogWindow!!.setGravity(Gravity.CENTER)
        val lp = dialogWindow.attributes
        val displayMetrics = activity!!.resources.displayMetrics
        lp.width = (displayMetrics.widthPixels * 0.8f).toInt()
        dialogWindow.attributes = lp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_custom, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments!!.getString("title")
        setTitle(title)
        val content = arguments!!.getString("content")
        setContent(content)
        textView_cancel.click {
            cancelClick?.invoke(this)
            dismiss()
        }
        textView_ok.click {
            confirmClick?.invoke(this)
            dismiss()
        }
    }

    fun setTitle(title: String): ConfirmDialog {
        textView_title.text = title
        return this
    }

    fun setContent(content: String): ConfirmDialog {
        textView_content.text = content
        return this
    }

    fun setCancelClick(click: (dialog : ConfirmDialog) -> kotlin.Unit): ConfirmDialog {
        cancelClick = click
        return this
    }

    fun setConfirmClick(click: (dialog : ConfirmDialog) -> kotlin.Unit): ConfirmDialog {
        confirmClick = click
        return this
    }

    fun setIsShowCancel(isShowCancel: Boolean): ConfirmDialog {
        if (isShowCancel) {
            textView_cancel.visibility = View.GONE
        } else {
            textView_cancel.visibility = View.VISIBLE
        }
        return this
    }
}