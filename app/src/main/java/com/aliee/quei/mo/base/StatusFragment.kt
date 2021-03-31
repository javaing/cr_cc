package com.aliee.quei.mo.base

import androidx.lifecycle.LifecycleOwner
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.aliee.quei.mo.widget.view.statuslayout.StatusLayout

/**
 * @Author: YangYang
 * @Date: 2017/12/29
 * @Version: 1.0.0
 * @Description:
 */
abstract class StatusFragment : Fragment(), LifecycleOwner {
    private var isFragmentVisible = true
    private var isPrepared: Boolean = false
    private var isFirst: Boolean = true
    private var isInViewPager: Boolean = false


    private lateinit var statusLayout: StatusLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (getLayoutId() != 0) {
            statusLayout = StatusLayout(inflater.context)
            val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            statusLayout.addView(inflater.inflate(getLayoutId(), null), params)
            statusLayout
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


    //显示错误状态
    protected fun showError(message: String? = null, imgRes: Int? = null, retry: () -> kotlin.Unit) {
        statusLayout.setErrorText(message!!)
        statusLayout.setErrorImage(imgRes!!)
        statusLayout.showError{ retry() }
    }

    //显示没有网络的状态
    protected fun showNoNetwork(message: String? = null, imgRes: Int? = null, retry: () -> kotlin.Unit) {
        statusLayout.setNoNetworkText(message!!)
        statusLayout.setNoNetworkImage(imgRes!!)
        statusLayout.showNoNetwork{ retry() }
    }

    //显示没有数据的状态
    protected fun showEmpty(message: String? = null, imgRes: Int? = null, retry: (() -> kotlin.Unit)? = null) {
        statusLayout.setEmptyText(message!!)
        statusLayout.setEmptyImage(imgRes!!)
        statusLayout.showEmpty()
        statusLayout.setRefreshListener{retry?.invoke()}
    }

    //显示没有数据的状态
    protected fun showLoading() {
        statusLayout.showLoading()
    }

    //显示没有数据的状态
    protected fun showContent() {
        statusLayout.showContent()
    }


    abstract fun getLayoutId(): Int

    abstract fun initView()

    //用懒加载的方式加载数据
    abstract fun initData()

}