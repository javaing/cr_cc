package com.aliee.quei.mo.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.analyze.IAnalyzePage
import com.aliee.quei.mo.widget.view.dialog.LoadingDialog


/**
 * @Author: YangYang
 * @Date: 2017/12/29
 * @Version: 1.0.0
 * @Description:
 */
abstract class BaseFragment : Fragment(), IAnalyzePage {

    private lateinit var loading: LoadingDialog
    private var isFragmentVisible = false
    var isPrepared: Boolean = false
    var isFirst: Boolean = true
    private var isInViewPager: Boolean = false


    var v : View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (getLayoutId() != 0) {
            if (v == null) {
                v = inflater.inflate(getLayoutId(), null)
            }
            v
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ARouter.getInstance().inject(this)
        loading = LoadingDialog(view.context)
        initView()
        isPrepared = true
        lazyLoad()
    }

    protected fun showLoading(canCancel: Boolean = false) {
        loading.setCanCancel(canCancel)
        loading.show()
    }

    protected fun disLoading() {
        loading.dismiss()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isFragmentVisible = isVisibleToUser
        isInViewPager = true
        lazyLoad()
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

    override fun onDestroyView() {
        v = null
        isFirst = true
        super.onDestroyView()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    //用懒加载的方式加载数据
    abstract fun initData()

    private var hasPaused = false
    override fun onPause() {
        super.onPause()
        hasPaused = true
    }

    override fun onResume() {
        super.onResume()
        lazyLoad()
        if (hasPaused) {

        }
    }

    open fun onBackToFrount() {

    }

    open fun scrollToTop() {

    }
}