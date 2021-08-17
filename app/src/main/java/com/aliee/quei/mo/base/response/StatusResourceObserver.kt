package com.aliee.quei.mo.base.response

import androidx.lifecycle.MediatorLiveData
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.utils.extention.toast
import com.umeng.analytics.MobclickAgent
import io.reactivex.observers.ResourceObserver
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */
class StatusResourceObserver<T>(private var uiLiveData: MediatorLiveData<UIDataBean<T>>, private var success: ((T) -> kotlin.Unit)? = null, private val silent: Boolean = false) : ResourceObserver<T>() {

    val context: ReaderApplication = ReaderApplication.instance
    private val dataBean: UIDataBean<T> = uiLiveData.value ?: UIDataBean(Status.Before)

    override fun onStart() {
        super.onStart()
        if (uiLiveData.value == null) {
            uiLiveData.postValue(dataBean)
        }
        dataBean.status = Status.Start
        uiLiveData.postValue(dataBean)
    }

    override fun onComplete() {
        dataBean.status = Status.Complete
        uiLiveData.value = dataBean
    }

    override fun onNext(t: T) {
        if (success != null) {
            success?.invoke(t)
        } else {
            dataBean.status = Status.Success
            dataBean.data = t
            uiLiveData.value = dataBean
        }
        onComplete()
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        execute(e)
        onComplete()
    }

    private fun execute(e: Throwable) {
        if (e is RequestException) {
            if (e.code == RequestException.ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL) {
                dataBean.status = Status.Success
//                dataBean.data = null
                uiLiveData.value = dataBean
                return
            }
        }

        if (!isNetworkAvailable()) {
            dataBean.status = Status.NoNetwork
            dataBean.e = e
            uiLiveData.value = dataBean
            showMessage(context.getString(R.string.err_msg_no_net))
            return
        }
        when (e) {
            is HttpException -> //retrofit网络请求异常
            {
                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean
                MobclickAgent.reportError(ReaderApplication.instance, e)
            }
            is SocketTimeoutException -> //网络请求超时
            {
                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean
                MobclickAgent.reportError(ReaderApplication.instance, e)
            }
            is RequestException -> {
                if (e.code == RequestException.ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL) {
                    dataBean.status = Status.Success
                    dataBean.data = null
                    uiLiveData.value = dataBean
                    return
                }
                if (e.code == RequestException.ERROR_REQUEST_TOKEN_FAILED) {
                    dataBean.status = Status.TokenError
                    dataBean.data = null
                    uiLiveData.value = dataBean
                    return
                }

                dataBean.status = Status.Error
                dataBean.e = e
                uiLiveData.value = dataBean
//                when (e.code) {
//                    RequestException.ERROR_CODE_LOGIN,
//                    RequestException.ERROR_CODE_TOKEN_EXPIRE,
//                    RequestException.ERROR_CODE_TOKEN_INVALID,
//                    RequestException.ERROR_CODE_TOKEN_INVALID_2-> {
//                        //token过期等登录异常
//                        ReaderApplication.instance.appManager.currentActivity?.let {
//                            ARouterManager.goLoginActivity(it)
//                            CommonDataProvider.instance.setHasLogin(false)
//                        }
//                    }
//                    RequestException.ERROR_ACCOUNT_NOT_EXIST,
//                    RequestException.ERROR_ACCOUNT_MOBILE_EMPTY ->{
//
//                    }
//                    else -> {
                if(e.code!=3001)
                    showMessage(e.msg)
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

    private fun showMessage(s: String) {
        if (silent) return
        ReaderApplication.instance.toast(s)
    }

    private fun readFromCache() {

    }
}