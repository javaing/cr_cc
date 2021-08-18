package com.aliee.quei.mo.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.safeCall
import com.aliee.quei.mo.data.bean.toDataBean
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
open class BaseViewModel : ViewModel() {

    fun viewModelLaunch(job: suspend () -> Unit, fail: ()->Unit) {
        viewModelScope.launch {
            try {
                job.invoke()
            } catch (e: Exception) {
                fail.invoke()
            }
        }
    }

}