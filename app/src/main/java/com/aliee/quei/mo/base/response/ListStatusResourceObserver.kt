package com.aliee.quei.mo.base.response

import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.exception.RequestException
import com.umeng.analytics.MobclickAgent
import io.reactivex.observers.ResourceObserver
import org.jetbrains.anko.toast
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:列表类型的数据请求
 */
open class ListStatusResourceObserver<T>(private var uiLiveData: MediatorLiveData<UIListDataBean<T>>, private var success: ((ListBean<T>) -> kotlin.Unit)? = null) : ResourceObserver<ListBean<T>>() {

    val context: ReaderApplication = ReaderApplication.instance
    private val dataBean: UIListDataBean<T> = uiLiveData.value ?: UIListDataBean(Status.Before, mutableListOf())

    init {
        if (uiLiveData.value == null) {
            dataBean.status = Status.Before
            uiLiveData.value = dataBean
        }
    }

    override fun onStart() {
        super.onStart()
        dataBean.status = Status.Start
        uiLiveData.postValue(dataBean)
    }

    override fun onComplete() {
        dataBean.status = Status.Complete
        uiLiveData.postValue(dataBean)
    }

    override fun onNext(t: ListBean<T>) {
        if (success != null) {
            success?.invoke(t)
        } else {
            if (t.page == 1) {
                dataBean.data.clear()
            }
            dataBean.data.addAll(t.list)
            dataBean.total = t.count

            dataBean.status = Status.Success
            uiLiveData.value = dataBean

            if (dataBean.data.isEmpty()) {
                dataBean.status = Status.Empty
                uiLiveData.value = dataBean
            }

            if (dataBean.data.size >= t.count) {
                dataBean.status = Status.NoMore
                uiLiveData.value = dataBean
            }
        }
        onComplete()
    }

    override fun onError(e: Throwable) {
        execute(e)
        onComplete()
    }

    private fun execute(e: Throwable) {
        if (e is RequestException && e.code == RequestException.ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL) {
            dataBean.status = Status.Empty
            dataBean.data = mutableListOf()
            uiLiveData.value = dataBean
            return
        }

        if (!isNetworkAvailable()) {
            dataBean.status = Status.NoNetwork
            dataBean.e = e
            uiLiveData.value = dataBean
            context.toast(context.getString(R.string.err_msg_no_net))
            return
        }

        when (e) {
            is HttpException -> //retrofit网络请求异常
            {
                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean
                MobclickAgent.reportError(ReaderApplication.instance,e)
            }
            is SocketTimeoutException -> //网络请求超时
            {
                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean
                MobclickAgent.reportError(ReaderApplication.instance,e)
            }
            is RequestException -> {
                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean

//                when (e.code) {
//                    RequestException.ERROR_CODE_LOGIN,
//                    RequestException.ERROR_CODE_TOKEN_INVALID,
//                    RequestException.ERROR_CODE_TOKEN_EXPIRE,
//                    RequestException.ERROR_CODE_TOKEN_INVALID_2-> {
//                        //token过期等登录异常
//                        ReaderApplication.instance.currentVisibleActivity?.let {
//                            ARouterManager.goLoginActivity(it)
//                            CommonDataProvider.instance.setHasLogin(false)
//                        }
//
////                        CommonDataProvider.instance.setToken(TokenBean())
////                        CommonDataProvider.instance.saveTempToken("")
////                        CommonDataProvider.instance.saveTempRefreshToken("")
////                        context.toast("登录过期请重新登录")
////                        Routers.open(context.appManager.currentActivity, ARouterManager.getLoginActivityUrl(), BaseRouterCallback())
//                    }
//                    else -> {
                    context.toast(e.msg)
//                    }
//                }
            }
            else -> {
                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivity = ReaderApplication.instance
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (info != null && info.isConnected) {
            // 当前网络是连接的
            if (info.state == NetworkInfo.State.CONNECTED) {
                // 当前所连接的网络可用
                return true
            }
        }
        return false
    }
}