package com.aliee.quei.mo.data.bean

import android.util.Log
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.exception.RequestException
import retrofit2.Response

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:返回数据实体的基类
 */
data class BaseResponse<T>(val code: Int,
                           val msg : String?,
                           var data: T?)

/*数据解析扩展函数*/
fun <T> BaseResponse<T>.dataConvert(): T? {
    //Log.e("tag", " ${this.data}")
    when (this.code) {
        0 -> {
            if (this.data == null) {
                Log.e("请求失败", "data null")
                throw RequestException(RequestException.ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL)
            }
            return this.data
        }
        else -> {
            Log.e("请求失败", "${this.code} $this.msg")
            throw RequestException(this.code, this.msg?:"")
        }
    }

}

fun <T> BaseResponse<T>.toDataBean(): UIDataBean<T> {
    val dataBean = UIDataBean<T>(Status.Empty)
    try {
        val data = dataConvert()
        Log.e("tag", "请求toDataBean $data")

        dataBean.status = Status.Success
        dataBean.data = data

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("请求失败", " ${e.message}")
        dataBean.status = Status.Error
    }
    return dataBean
}

 suspend fun <T> safeCall(call: suspend () -> UIDataBean<T>): UIDataBean<T> {
    try {
        return call()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return UIDataBean(Status.Error)
}