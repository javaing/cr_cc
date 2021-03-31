package com.aliee.quei.mo.base

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.aliee.quei.mo.R

/**
 * Created by Administrator on 2018/5/9 0009.
 */
abstract class BaseDialogFragment : DialogFragment(){
    private var isFragmentVisible = true
    private var isPrepared: Boolean = false
    private var isFirst: Boolean = true
    private var isInViewPager: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.common_dialog_fragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (getLayoutId() != 0) {
            inflater.inflate(getLayoutId(), null)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        isPrepared = true
        lazyLoad()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isFragmentVisible = isVisibleToUser
        isInViewPager = true
        lazyLoad()
    }

    fun showLoading(){
        if(activity is BaseActivity){
            (activity as BaseActivity).showLoading()
        }
    }

    fun disLoading(){
        if(activity is BaseActivity){
            (activity as BaseActivity).disLoading()
        }
    }

    private fun lazyLoad() {
        if (!isInViewPager) {
            isFirst = false
            initData()
            return
        }
        if (!isPrepared || !isFragmentVisible || !isFirst) {
            return
        }
        isFirst = false
        initData()
    }

    fun show(activity: FragmentActivity){
        try {
            show(activity.supportFragmentManager,javaClass.name)
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun show(fragment: Fragment){
        try {
            show(fragment.childFragmentManager,javaClass.name)
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window
        val params = window!!.attributes
        params.width = WindowManager.LayoutParams.WRAP_CONTENT // 宽度填充满屏
        window.attributes = params
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    //用懒加载的方式加载数据
    abstract fun initData()

}